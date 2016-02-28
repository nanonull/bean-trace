package conversion7.trace

abstract class AbstractBeanFactory {

    public <C extends TraceBean> C create(final Class<C> type) {
        return create(type, null)
    }

    public <C extends TraceBean> C create(final Class<C> type, Map<String, Object> initProps) {
        def instance = type.newInstance()
        // TODO add "WARN: Bean was not marked with ${BeanTransformation}")

        if (initProps != null) {
            instance.handleInputProps(initProps)
        }

        return instance
    }
}
