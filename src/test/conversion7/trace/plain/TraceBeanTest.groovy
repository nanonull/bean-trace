package conversion7.trace.plain

import conversion7.trace.BeanTransformation
import conversion7.trace.plain.test_beans.BaseTestBean
import conversion7.trace.plain.test_beans.TestBean1


class TraceBeanTest extends GroovyTestCase {


    void 'test create new dynamic properties'() {
        def obj1 = BaseTestBean.beanFactory.create(TestBean1, ['newF1': 2, 'newF2': null])
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
        def obj1 = BaseTestBean.beanFactory.create(TestBean1, ['f1': 10])
        assert obj1.f1 == 10
    }

    void 'test injects props'() {
        def obj1 = BaseTestBean.beanFactory.create(TestBean1, ['f1': 10])
        assert obj1.f1 == 10
        assert obj1.f2 == 0
        assert obj1.f3 == 0
        assert obj1._changes == 1
        obj1.injectProperties(['f1': 11, f2: 22, f3:null])
        assert obj1.f1 == 11
        assert obj1.f2 == 22
        assert obj1.f3 == null
        assert obj1._changes == 4
    }

    void 'test sys props are not used in getDependentBean '() {
        def b1 = BaseTestBean.beanFactory.create(Bean1, null)
        def b2 = BaseTestBean.beanFactory.create(Bean2, b1.initialBeanProperties)

        BaseTestBean.SYS_PROPS.each {
            assert !b1.initialBeanProperties.containsKey(it)
            assert !b2.initialBeanProperties.containsKey(it)
        }
    }

    void 'test props after getDependentBean '() {
        def b1 = BaseTestBean.beanFactory.create(Bean1, [f1: 1, dynProp1: 2, dynProp2: 3])
        def b2 = BaseTestBean.beanFactory.create(Bean2, b1.initialBeanProperties)

        assert b2.f1 == 1
        assert b2.dynProp1 == 2
        assert b2.dynProp2 == 3
        // field is not initProp for getDependentBean
        shouldFail(MissingPropertyException, { b2.f2 })
    }

    void 'test props after getDependentBean with additional props'() {
        def b1 = BaseTestBean.beanFactory.create(Bean1, [f1: 1, dynProp1: 2, dynProp2: 3])
        def b2 = BaseTestBean.beanFactory.create(Bean2, b1.initialBeanProperties + [f11: 11, dynProp11: 22, dynProp22: 33])

        assert b2.f1 == 1
        assert b2.dynProp1 == 2
        assert b2.dynProp2 == 3
        assert b2.f11 == 11
        assert b2.dynProp11 == 22
        assert b2.dynProp22 == 33
    }

    @BeanTransformation
    static class Bean1 extends BaseTestBean {
        int f1
        int f2 = 5

        @Override
        void run() {

        }
    }

    @BeanTransformation
    static class Bean2 extends BaseTestBean {
        int f1
        int f11
        int dynProp2
        int dynProp22

        @Override
        void run() {

        }
    }

    void 'test handle sys prop'() {
        BaseTestBean.SYS_PROPS.add("dataSource")
        try {

            def dataSource = new Object()
            def b = BaseTestBean.beanFactory.create(BeanWithDataSourceSysProp, [dataSource: dataSource])
            assert b.dataSource == dataSource
            assert !b.initialBeanProperties.containsKey("dataSource")
            assert b.initialBeanProperties.size() == 0
        } finally {
            BaseTestBean.SYS_PROPS.remove("dataSource")
        }
    }

    @BeanTransformation
    static class BeanWithDataSourceSysProp extends BaseTestBean {
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

    void 'test fields access non-standard names'(){
        def b = BaseTestBean.beanFactory.create(Bean11, null)
        assert b.Field_1 == 1
        assert b.Field_B2 == 2
        assert b.Field == 3
        assert b.Field1 == 4
        assert b.a_f == 5
        assert b.b_F == 6
        assert b.C_F == 7
        assert b.DD == 8
        assert b.e == 9
        assert b.F == 10

        b.Field_1 = 11
        b.Field_B2 = 22
        b.Field = 33
        b.Field1 = 44
        b.a_f = 55
        b.b_F = 66
        b.C_F = 77
        b.DD = 88
        b.e = 99
        b.F = 1000

        assert b.Field_1 == 11
        assert b.Field_B2 == 22
        assert b.Field == 33
        assert b.Field1 == 44
        assert b.a_f == 55
        assert b.b_F == 66
        assert b.C_F == 77
        assert b.DD == 88
        assert b.e == 99
        assert b.F == 1000
    }

    @BeanTransformation
    static class Bean11 extends BaseTestBean {
        int Field_1
        int Field_B2
        int Field
        int Field1
        int a_f
        int b_F
        int C_F
        int DD
        int e
        int F

        @Override
        void run() {
            Field_1 = 1
            Field_B2 = 2
            Field = 3
            Field1 = 4
            a_f = 5
            b_F = 6
            C_F = 7
            DD = 8
            e = 9
            F = 10
        }
    }

    void 'test fields access non-standard names and no transformation'(){
        def b = BaseTestBean.beanFactory.create(Bean12NonStandardNamesNoTransform, null)
        assert b.Field_1 == 1
        assert b.Field_B2 == 2
        assert b.Field == 3
        assert b.Field1 == 4
        assert b.a_f == 5
        assert b.b_F == 6
        assert b.C_F == 7
        assert b.DD == 8
        assert b.e == 9
        assert b.F == 10

        b.Field_1 = 11
        b.Field_B2 = 22
        b.Field = 33
        b.Field1 = 44
        b.a_f = 55
        b.b_F = 66
        b.C_F = 77
        b.DD = 88
        b.e = 99
        b.F = 1000

        assert b.Field_1 == 11
        assert b.Field_B2 == 22
        assert b.Field == 33
        assert b.Field1 == 44
        assert b.a_f == 55
        assert b.b_F == 66
        assert b.C_F == 77
        assert b.DD == 88
        assert b.e == 99
        assert b.F == 1000
    }


    static class Bean12NonStandardNamesNoTransform extends BaseTestBean {
        int Field_1
        int Field_B2
        int Field
        int Field1
        int a_f
        int b_F
        int C_F
        int DD
        int e
        int F

        @Override
        void run() {
            Field_1 = 1
            Field_B2 = 2
            Field = 3
            Field1 = 4
            a_f = 5
            b_F = 6
            C_F = 7
            DD = 8
            e = 9
            F = 10
        }
    }


}


