package conversion7.trace.test_beans

import conversion7.trace.TraceBean

abstract class BeanTestImpl implements GroovyObject, TraceBean {

    public BeanTestImpl() {
        initTracing(this)
    }

    abstract void run()

    // testing only
    @Deprecated
    public static <C extends BeanTestImpl> C create(final Class<C> type) {
        create(type, null)
    }

    // testing only
    @Deprecated
    public static <C extends BeanTestImpl> C create(final Class<C> type, Map<String, Object> initProps) {
        def bean = TestApp.beanFactory.create(type, initProps)
        bean.run()
        return bean
    }
}
