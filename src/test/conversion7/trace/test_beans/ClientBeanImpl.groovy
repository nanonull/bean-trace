package conversion7.trace.test_beans

import conversion7.trace.BaseBeanManager
import conversion7.trace.TraceBean

abstract class ClientBeanImpl implements GroovyObject, TraceBean {

    public ClientBeanImpl() {
        initTracing(this)
    }

    abstract void run()

    public static <C extends ClientBeanImpl> C create(final Class<C> type) {
        create(type, null)
    }

    public static <C extends ClientBeanImpl> C create(final Class<C> type, Map<String, Object> initProps) {
        def bean = BaseBeanManager.create(type, initProps)
        bean.run()
        return bean
    }
}
