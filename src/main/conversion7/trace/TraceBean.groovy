package conversion7.trace

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

abstract class TraceBean extends GroovyObjectSupport implements PropertyChangeListener {

    static boolean TRACE_BEAN = "y" == System.getProperty("bean.trace")
    static List<String> SYS_PROPS = []

    protected int changes
    public List<String> beanPath
    public Map initialBeanProperties
    private final PropertyChangeSupport pcs = new PropertyWriteListeningSupport(this);

    TraceBean() {
        initialBeanProperties = new TreeMap()
        beanPath = new ArrayList<>()
        pcs.addPropertyChangeListener(PropertyWriteListeningSupport.COMMON_LISTENER, this)
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    /**Called from setters created in conversion7.trace.BeanTransformer#wrapFieldsAndPropertiesForListening*/
    public void firePropertyChange(String prop, Object oldVal, Object newVal) {
        pcs.firePropertyChange(prop, oldVal, newVal)
    }

    void propertyChange(PropertyChangeEvent changeEvent) {
        if (TRACE_BEAN) {
            this.changes++
            this.println "'${changeEvent.propertyName}' write: '${changeEvent.oldValue}' >>> '${changeEvent.newValue}'"
        }
    }

    abstract void run()

    public static <C extends TraceBean> C create(final Class<C> type) {
        return create(type, null)
    }

    public static <C extends TraceBean> C create(final Class<C> type, Map<String, Object> initProps) {
        def instance = type.newInstance()
//        init(instance)

        if (initProps != null) {
            instance.handleInputProps(initProps)
        }

        instance.run()
        return instance
    }

    void handleInputProps(Map<String, Object> initProps) {
        SYS_PROPS.each {
            def removed = initProps.remove(it)
            handleInputSysProp(it, removed)
        }
        initialBeanProperties.putAll(initProps)

        initProps.each { entry ->
            def property = entry.key
            def newValue = entry.value

            MetaBeanProperty field = owner.metaClass.getProperties()
                    .find { f -> f.name == property } as MetaBeanProperty
            def updateToMetaProps = false
            if (field != null) {
                try {
                    owner.setProperty(property, newValue)
//                    field.field.field.set(instance, newValue)
                } catch (NullPointerException e) {
                    updateToMetaProps = true
                }
            } else {
                updateToMetaProps = true
            }

            if (updateToMetaProps) {
                // fallback for dynamic properties
                // listeners doesn't work after instance's fields are init in this way
                owner.metaClass."$entry.key" = entry.value
            }
        }
    }

    protected void handleInputSysProp(String propName, Object value) {

    }

    void println(String msg) {
        super.println(this.getClass().getSimpleName() + ": " + msg)
    }

}
