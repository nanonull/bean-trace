package conversion7.trace.test_beans

import conversion7.trace.TraceBean
import conversion7.trace.TraceBeanFactory

abstract class ClientBeanTestImpl implements GroovyObject, TraceBean {

    public ClientBeanTestImpl() {
        initTracing(this)
    }

    abstract void run()

    public static <C extends ClientBeanTestImpl> C create(final Class<C> type) {
        create(type, null)
    }

    public static <C extends ClientBeanTestImpl> C create(final Class<C> type, Map<String, Object> initProps) {
        def bean = TraceBeanFactory.create(type, initProps)
        bean.run()
        return bean
    }
}
