package conversion7.trace

import conversion7.trace.test_beans.ClientBeanImpl
import conversion7.trace.test_beans.TestBean1


class TraceBeanTest extends GroovyTestCase {


    void 'test create new dynamic properties'() {
        def obj1 = BaseBeanManager.create(TestBean1, ['newF1': 2, 'newF2': null])
        assert obj1._changes == 0

        assert obj1.getProperty('newF1') == 2
        assert obj1.newF2 == null
        assert obj1.metaClass.getProperty(obj1, 'newF1') == 2
        assert obj1.metaClass.getProperty(obj1, 'newF2') == null

        // change dynamic prop
        obj1.newF1 = 3
        assert obj1.'newF1' == 3
        // no listening to dynamic prop
        assert obj1._changes == 0

    }

    void 'test update from dynamic props'() {
        def obj1 = BaseBeanManager.create(TestBean1, ['f1': 10])
        assert obj1.f1 == 10
    }

    void 'test sys props are not used in getDependentBean '() {
        def b1 = BaseBeanManager.create(Bean1)
        def b2 = BaseBeanManager.create(Bean2, b1.initialBeanProperties)

        ClientBeanImpl.SYS_PROPS.each {
            assert !b1.initialBeanProperties.containsKey(it)
            assert !b2.initialBeanProperties.containsKey(it)
        }
    }

    void 'test props after getDependentBean '() {
        def b1 = BaseBeanManager.create(Bean1, [f1: 1, dynProp1: 2, dynProp2: 3])
        def b2 = BaseBeanManager.create(Bean2, b1.initialBeanProperties)

        assert b2.f1 == 1
        assert b2.dynProp1 == 2
        assert b2.dynProp2 == 3
        // field is not initProp for getDependentBean
        shouldFail(MissingPropertyException, { b2.f2 })
    }

    void 'test props after getDependentBean with additional props'() {
        def b1 = BaseBeanManager.create(Bean1, [f1: 1, dynProp1: 2, dynProp2: 3])
        def b2 = BaseBeanManager.create(Bean2, b1.initialBeanProperties + [f11: 11, dynProp11: 22, dynProp22: 33])

        assert b2.f1 == 1
        assert b2.dynProp1 == 2
        assert b2.dynProp2 == 3
        assert b2.f11 == 11
        assert b2.dynProp11 == 22
        assert b2.dynProp22 == 33
    }

    @BeanTransformation
    static class Bean1 extends ClientBeanImpl {
        int f1
        int f2 = 5

        @Override
        void run() {

        }
    }

    @BeanTransformation
    static class Bean2 extends ClientBeanImpl {
        int f1
        int f11
        int dynProp2
        int dynProp22

        @Override
        void run() {

        }
    }

    void 'test handle sys prop'() {
        ClientBeanImpl.SYS_PROPS.add("dataSource")
        try {

            def dataSource = new Object()
            def b = BaseBeanManager.create(BeanWithDataSourceSysProp, [dataSource: dataSource])
            assert b.dataSource == dataSource
            assert !b.initialBeanProperties.containsKey("dataSource")
            assert b.initialBeanProperties.size() == 0
        } finally {
            ClientBeanImpl.SYS_PROPS.remove("dataSource")
        }
    }

    static class BeanWithDataSourceSysProp extends ClientBeanImpl {
        Object dataSource

        void handleInputSysProp(String propName, Object value) {
            if (propName == "dataSource") {
                dataSource = value
            }
        }

        @Override
        void run() {

        }
    }

}


