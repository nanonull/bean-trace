package conversion7.trace

import conversion7.trace.plain.TraceBean

abstract class AbstractBeanFactory {

    public <C extends TraceBean> C create(final Class<C> type, Map<String, Object> initProps) {
        def instance = type.newInstance()
        // TODO add "WARN: Bean was not marked with ${BeanTransformation}")

        if (initProps != null) {
            instance.handleInputProps(initProps)
        }

        return instance
    }
}
