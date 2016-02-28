package conversion7.trace.plain

import conversion7.trace.AbstractBeanFactory
import conversion7.trace.plain.test_beans.BaseTestBean

class BeanTestFactory extends AbstractBeanFactory {

    public <C extends BaseTestBean> C create(final Class<C> type, Map<String, Object> initProps) {
        def inst = super.create(type, initProps)
        inst.run()
        return inst
    }
}
