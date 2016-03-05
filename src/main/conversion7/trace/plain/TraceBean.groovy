package conversion7.trace.plain

import conversion7.trace.BeanException
import conversion7.trace.PropertyWriteListeningSupport
import org.codehaus.groovy.runtime.metaclass.MultipleSetterProperty

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.lang.reflect.Field

trait TraceBean implements PropertyChangeListener, GroovyObject {

    int _changes
    PropertyWriteListeningSupport propertyChangeSupport
    GroovyObject instanceOwner

    void initialization(GroovyObject instanceOwner) {
        this.instanceOwner = instanceOwner
        propertyChangeSupport = new PropertyWriteListeningSupport(instanceOwner);
        if (instanceOwner instanceof PropertyChangeListener) {
            if (shouldTrace()) {
                addPropertyChangeListener(instanceOwner)
            }
        } else {
            throw new BeanException(instanceOwner.class.getSimpleName() +
                    " has to implement PropertyChangeListener! " + instanceOwner)
        }

        MetaClass metaClassLink = instanceOwner.metaClass
        Class classLink = instanceOwner.class
//        assert metaClassLink == classLink.metaClass: "Dodge class or metaClass..."
        // activate overridden accessors
        metaClassLink.getProperty = { String property ->
            try {
                MetaBeanProperty metaBeanProperty = (MetaBeanProperty) metaClassLink.getProperties().find { f ->
                    f.name.equalsIgnoreCase(property)
                }

                if (metaBeanProperty) {
                    return getPropertyValue(metaBeanProperty)
                }

                Field field = findField(classLink, property)
                if (field) {
                    field.setAccessible(true)
                    return field.get(instanceOwner)
                }


                def accName = "get" + property
                def accessor = metaClassLink.metaMethods.find { mm ->
                    mm.name.equalsIgnoreCase(accName)
                }
                if (accessor) {
                    return accessor.doMethodInvoke(instanceOwner, null)
                }

                throw new MissingPropertyException(property, classLink)

            } catch (Throwable e) {
                def errMsg = e.getMessage()
                if (!errMsg) {
                    errMsg = e.getClass().getSimpleName() + ": $errMsg"
                }
                throw new BeanException("getProperty failure: $property. Cause: ${errMsg}", e)
            }
        }

        metaClassLink.setProperty = { String property, Object newValue ->
            try {
                def metaProperty = metaClassLink.getProperties().find { f ->
                    f.name.equalsIgnoreCase(property)
                }

                if (metaProperty && metaProperty instanceof MetaBeanProperty) {
                    setPropertyValue(metaProperty, newValue)
                    return
                }

                if (metaProperty instanceof MultipleSetterProperty) {
                    // handle?
                }

                Field field = findField(classLink, property)
                if (field) {
                    field.setAccessible(true)
                    field.set(instanceOwner, newValue)
                    return
                }

                def accName = "set" + property
                def accessor = metaClassLink.metaMethods.find { mm ->
                    mm.name.equalsIgnoreCase(accName)
                }
                if (accessor) {
                    return accessor.doMethodInvoke(instanceOwner, newValue)
                }

                throw new MissingPropertyException(property, classLink)

            } catch (Throwable e) {
                def errMsg = e.getMessage()
                if (!errMsg) {
                    errMsg = e.getClass().getSimpleName() + ": $errMsg"
                }
                throw new BeanException("setProperty failure: $property. Cause: ${errMsg}", e)
            }
        }
    }

    public Field findField(Class clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                return null;
            } else {
                return findField(superClass, fieldName);
            }
        }
    }

    boolean shouldTrace(){
        return true
    }

    Object getPropertyValue(MetaBeanProperty metaBeanProperty) {
        if (metaBeanProperty.getter) {
            return metaBeanProperty.getter.invoke(instanceOwner)
        } else {
            return metaBeanProperty.field.field.get(instanceOwner)
        }
    }

    void setPropertyValue(MetaBeanProperty metaBeanProperty, Object newValue) {
        if (metaBeanProperty.setter) {
            if (newValue == null) {
                metaBeanProperty.setter.invoke(instanceOwner, [newValue] as Object[])
            } else {
                metaBeanProperty.setter.invoke(instanceOwner, newValue)
            }
        } else {
            metaBeanProperty.field.field.set(instanceOwner, newValue)
        }
    }

    void injectProperties(Map<String, Object> props) {
        props.each { entry ->
            def property = entry.key
            def newValue = entry.value

            MetaBeanProperty beanProperty = instanceOwner.metaClass.getProperties()
                    .find { it.name.equalsIgnoreCase property } as MetaBeanProperty
            def updateToMetaProps = false
            if (beanProperty) {
                instanceOwner.setProperty(property, newValue)
            } else {
                updateToMetaProps = true
            }

            if (updateToMetaProps) {
                // fallback for dynamic properties
                // listeners doesn't work after instance's fields are init in this way
                instanceOwner.metaClass."$entry.key" = entry.value
            }
        }
    }

    /** #param propertyName - name as defined in class before compilation */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener)
    }

    /** #param propertyName - name as defined in class before compilation */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener)
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(PropertyWriteListeningSupport.COMMON_LISTENER, listener)
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(PropertyWriteListeningSupport.COMMON_LISTENER, listener)
    }

    /**Called from setters created in conversion7.trace.BeanASTTransformer#wrapFieldsAndPropertiesForListening*/
    public void firePropertyChange(String prop, Object oldVal, Object newVal) {
        if (shouldTrace()) {
            propertyChangeSupport.firePropertyChange(prop, oldVal, newVal)
        }
    }

    /**Invoked by propertyChangeSupport*/
    void propertyChange(PropertyChangeEvent changeEvent) {
        this._changes += 1
        println(buildPropertyChangedMessage(changeEvent))
    }

    String buildPropertyChangedMessage(PropertyChangeEvent changeEvent) {
        return "'${changeEvent.propertyName}' property: '${changeEvent.oldValue}' >>> '${changeEvent.newValue}'"
    }

    /**For manual logging in current class context*/
    void logStep(String name) {
        logStep(getClass().getSimpleName(), name)
    }

    /**Main output point*/
    void logStep(String classOrigin, String name) {
        println(buildLogStepMessage(classOrigin, name))
    }

    String buildLogStepMessage(String classOrigin, String name) {
        return " Invoke '${name}' from $classOrigin"
    }

    /**Invoked from transformed stack*/
    void methodInvoked(String classNameWhereMethodDefined, String name) {
        if (shouldTrace()){
            logStep(classNameWhereMethodDefined, name)
        }
    }

    void println() {
        println("")
    }

    void println(Object object) {
        println(String.valueOf(object))
    }

    void println(String msg) {
        System.out.println(this.getClass().getSimpleName() + ": " + msg)
    }

}
