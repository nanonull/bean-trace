package conversion7.trace

import conversion7.trace.plain.TraceBean

abstract class AbstractBeanFactory {

    public <C extends TraceBean> C create(final Class<C> type) {
        def instance = type.newInstance()
        // TODO add "WARN: Bean was not marked with ${BeanTransformation}")
        assert instance.instanceOwner : 'Your implementation should call TraceBean#initialization!'
        return instance
    }
}
