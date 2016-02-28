package conversion7.trace.test_beans

import conversion7.trace.AbstractBeanFactory

class BeanFactoryTestImpl extends AbstractBeanFactory {

    BeanFactoryTestImpl() {

    }

    public <C extends BeanTestImpl> C create(final Class<C> type, Map<String, Object> initProps) {
        def inst = super.create(type, initProps)
        inst.run()
        return inst
    }
}
