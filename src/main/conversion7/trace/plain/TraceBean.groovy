package conversion7.trace.plain

import conversion7.trace.BeanException
import conversion7.trace.PropertyWriteListeningSupport

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

trait TraceBean implements PropertyChangeListener {

    static List<String> SYS_PROPS = []

    int _changes
    Map initialBeanProperties = new TreeMap()
    PropertyWriteListeningSupport propertyChangeSupport
    GroovyObject instanceOwner

    void initialization(GroovyObject instanceOwner) {
        this.instanceOwner = instanceOwner
        propertyChangeSupport = new PropertyWriteListeningSupport(instanceOwner);
        if (instanceOwner instanceof PropertyChangeListener) {
            addPropertyChangeListener(instanceOwner)
        } else {
            throw new BeanException("Have to implement PropertyChangeListener! " + instanceOwner)
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
        propertyChangeSupport.firePropertyChange(prop, oldVal, newVal)
    }

    /**Invoked by propertyChangeSupport*/
    void propertyChange(PropertyChangeEvent changeEvent) {
        this._changes += 1
        println("'${changeEvent.propertyName}' write: '${changeEvent.oldValue}' >>> '${changeEvent.newValue}'")
    }

    void methodInvoked(String name) {
        methodInvoked(getClass().getSimpleName(), name)
    }

    /**Invoked from transformed methods. <br>
     * Also it could be invoked manually from bean code*/
    void methodInvoked(String classNameWhereMethodDefined, String name) {
        println(" Invoke '${name}' from $classNameWhereMethodDefined")
    }

    void handleInputProps(Map<String, Object> initProps) {
        injectProperties(initProps)
        initialBeanProperties.putAll(initProps)
    }

    void injectProperties(Map<String, Object> props) {
        SYS_PROPS.each {
            def removed = props.remove(it)
            handleInputSysProp(it, removed)
        }

        props.each { entry ->
            def property = entry.key
            def newValue = entry.value

            MetaBeanProperty field = instanceOwner.metaClass.getProperties()
                    .find { f -> f.name == property } as MetaBeanProperty
            def updateToMetaProps = false
            if (field != null) {
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

    /**Override if you have system properties input via properties map*/
    void handleInputSysProp(String propName, Object value) {

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
