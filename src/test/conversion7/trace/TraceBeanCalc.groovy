package conversion7.trace

abstract class TraceBeanCalc extends TraceBean {

    abstract void run()

    public static <C extends TraceBeanCalc> C create(final Class<C> type) {
        create(type, null)
    }

    public static <C extends TraceBeanCalc> C create(final Class<C> type, Map<String, Object> initProps) {
        def bean = TraceBean.create(type, initProps)
        bean.run()
        return bean
    }
}
