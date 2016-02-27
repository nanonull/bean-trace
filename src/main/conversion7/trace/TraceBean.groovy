package conversion7.trace

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

abstract class TraceBean extends GroovyObjectSupport implements PropertyChangeListener {

    public static boolean BEAN_DUMPING = ('y' == System.getProperty('bean.dump'))
    public static boolean BEAN_CRASH_EXIT = ('y' == System.getProperty('bean.crash.exit'))
    public static final String PARENT_BEAN = "parentBean"
    static boolean TRACE_BEAN = "y" == System.getProperty("bean.trace")
    static List SYS_PROPS = ["dataSource"]
    static String PROP_CHANGE_NAME = "propertyChange"

    protected int changes
    public List<String> beanPath
    public Map initialBeanProperties

    // TODO override PropertyChangeSupport#firePropertyChange to trigger even oldVal == newVal
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public void firePropertyChange(String prop, Object oldVal, Object newVal) {
        pcs.firePropertyChange(prop, oldVal, newVal)
    }

    void propertyChange(PropertyChangeEvent changeEvent) {
        if (TRACE_BEAN) {
            this.changes++
            this.println "'${changeEvent.propertyName}' change: '${changeEvent.oldValue}' >>> '${changeEvent.newValue}'"
        }
    }

    TraceBean() {
        initialBeanProperties = new TreeMap()
        beanPath = new ArrayList<>()
        pcs.addPropertyChangeListener(this)
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
            // TODO sysPropertyHandler.call
            initProps.remove(it)
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

    static void init(TraceBean bean) {
        if (!TRACE_BEAN) {
            return
        }

        try {
            bean."$PROP_CHANGE_NAME" = { PropertyChangeEvent changeEvent ->
                bean.changes++
                bean.println "'${changeEvent.propertyName}' change: '${changeEvent.oldValue}' >>> '${changeEvent.newValue}'"
            }

        } catch (MissingPropertyException e) {
            if (e.getMessage().contains("No such property: $PROP_CHANGE_NAME")) {
                println("Check @BeanTransformation annotation.")
            } else {
                throw new BeanException(e.getMessage(), e)
            }
        }
    }

    void println(String msg) {
        if (TRACE_BEAN) {
            super.println(this.getClass().getSimpleName() + ": " + msg)
        }
    }

}
