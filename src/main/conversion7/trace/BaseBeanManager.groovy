package conversion7.trace

class BaseBeanManager {
    public static <C extends TraceBean> C create(final Class<C> type) {
        return create(type, null)
    }

    public static <C extends TraceBean> C create(final Class<C> type, Map<String, Object> initProps) {
        def instance = type.newInstance()

        if (initProps != null) {
            instance.handleInputProps(initProps)
        }

        return instance
    }
}
