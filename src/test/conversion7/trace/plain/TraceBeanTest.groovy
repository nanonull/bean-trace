package conversion7.trace.plain

import conversion7.trace.BeanTransformation
import conversion7.trace.plain.test_beans.BaseTestBean
import conversion7.trace.plain.test_beans.Bean21WithStaticMethod
import conversion7.trace.plain.test_beans.TestBean1


class TraceBeanTest extends GroovyTestCase {


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

    @BeanTransformation
    static class BeanWithDataSourceSysProp extends BaseTestBean {
        Object dataSource

        @Override
        void run() {

        }
    }

    void 'test fields access non-standard names'() {
        def b = BaseTestBean.beanFactory.create(Bean11)
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

    void 'test fields access non-standard names and no transformation'() {
        def b = BaseTestBean.beanFactory.create(Bean12NonStandardNamesNoTransform)
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

//@BeanTransformation
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

    void 'test field access from ClientImpl into ClientBaseImpl'() {
        def b = BaseTestBean.beanFactory.create(Bean15ClientImpl)
        b.field2 = 2
        assert b.field2 == 2
        b.field1 = 1
        assert b.field1 == 1
    }

    static abstract class Bean15ClientBaseImpl extends BaseTestBean {

        public Integer field1
        protected Integer field2
        Object sys

    }

    static class Bean15ClientImpl extends Bean15ClientBaseImpl {

        @Override
        void run() {
            println("hey")
        }
    }

    void 'test method which look like accessor, but invoked as property'(){
        def b = BaseTestBean.beanFactory.create(Bean15ClientImpl)
        assert b.getProperties().containsKey("class")
        assert b.properties.containsKey("class")
    }

    void 'test static method invoke'(){
        def b = BaseTestBean.beanFactory.create(Bean20)
        assert b.intToStringFunc(2) == "2"

        def b2 = BaseTestBean.beanFactory.create(Bean21WithStaticMethod)
        assert b2.intToStringFunc(2) == "2"
    }

    static class Bean20 extends BaseTestBean{

        @Override
        void run() {
            assert intToStringFunc(1) == "1"
        }

        static String intToStringFunc(int num) {
            return num.toString()
        }
    }


}


