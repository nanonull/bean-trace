package conversion7.trace

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

trait TraceBean implements PropertyChangeListener {

    static List<String> SYS_PROPS = []

    int _changes
    Map initialBeanProperties = new TreeMap()
    private PropertyChangeSupport propertyChangeSupport
    GroovyObject instanceOwner

    void initTracing(GroovyObject instanceOwner) {
        propertyChangeSupport = new PropertyWriteListeningSupport(this);
        propertyChangeSupport.addPropertyChangeListener(PropertyWriteListeningSupport.COMMON_LISTENER, this)
        this.instanceOwner = instanceOwner
    }

    /**Called from setters created in conversion7.trace.BeanTransformer#wrapFieldsAndPropertiesForListening*/
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
