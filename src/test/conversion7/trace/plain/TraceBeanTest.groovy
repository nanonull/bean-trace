package conversion7.trace.plain

import conversion7.trace.BeanException
import conversion7.trace.BeanTransformation
import conversion7.trace.TestUtils
import conversion7.trace.plain.test_beans.BaseTestBean
import conversion7.trace.plain.test_beans.Bean21WithStaticMethod

import static conversion7.trace.TestUtils.assertWillFail

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
        def b = BeanTestFactory.beanFactory.create(Bean11)
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
        def b = BeanTestFactory.beanFactory.create(Bean12NonStandardNamesNoTransform)
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
        def b = BeanTestFactory.beanFactory.create(Bean15ClientImpl)
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

    void 'test method which look like accessor, but invoked as property'() {
        def b = BeanTestFactory.beanFactory.create(Bean15ClientImpl)
        assert b.getProperties().containsKey("class")
        assert b.properties.containsKey("class")
    }

    void 'test static method invoke'() {
        def b = BeanTestFactory.beanFactory.create(Bean20)
        assert b.intToStringFunc(2) == "2"

        def b2 = BeanTestFactory.beanFactory.create(Bean21WithStaticMethod)
        assert b2.intToStringFunc(2) == "2"
    }

    static class Bean20 extends BaseTestBean {

        @Override
        void run() {
            assert intToStringFunc(1) == "1"
        }

        static String intToStringFunc(int num) {
            return num.toString()
        }
    }

    void 'test class-casting error on set'() {
        def b = BeanTestFactory.beanFactory.create(Bean33)
        assertWillFail(BeanException, ".*setProperty failure: f2. Cause: argument type mismatch.*",
                { b.f2 += new Double(1) })
        assertWillFail(BeanException, ".*setProperty failure: f2. Cause: argument type mismatch.*",
                { b.f2 += 1.2d })
        assertWillFail(BeanException, ".*setProperty failure: f1. Cause: argument type mismatch.*",
                { b.f1 += 1f })
        assertWillFail(BeanException, ".*setProperty failure: f1. Cause: argument type mismatch.*",
                { b.f1 += 1.2f })

    }

    @BeanTransformation
    static class Bean33 extends BaseTestBean {
        int f1
        BigDecimal f2

        @Override
        void run() {
            f2 = 0

            assertWillFail(BeanException, ".*setProperty failure: f2. Cause: argument type mismatch.*",
                    { f2 += new Double(1) })
            assertWillFail(BeanException, ".*setProperty failure: f2. Cause: argument type mismatch.*",
                    { f2 += 1.2d })
            assertWillFail(BeanException, ".*setProperty failure: f1. Cause: argument type mismatch.*",
                    { f1 += 1f })
            assertWillFail(BeanException, ".*setProperty failure: f1. Cause: argument type mismatch.*",
                    { f1 += 1.2f })

        }
    }

    void 'test injects props'() {
        def b = BeanTestFactory.beanFactory.create(Bean34)
        b.injectProperties([f1: 1])
        assert b.f1 == 1

        assertWillFail(BeanException, ".*setProperty failure: f1. Cause: IllegalArgumentException: null.*",
                { b.injectProperties([f1: null]) })
        assert b.f1 == 1

        b.injectProperties([f2: 2.toBigDecimal()])
        assert b.f2 == 2.toBigDecimal()

        assertWillFail(BeanException, ".*setProperty failure: f2. Cause: argument type mismatch.*",
                { b.injectProperties([f2: 3]) })
        assert b.f2 == 2.toBigDecimal()

        b.injectProperties([f2: null])
        assert b.f2 == null
    }

    @BeanTransformation
    static class Bean34 extends BaseTestBean {
        int f1
        BigDecimal f2

        @Override
        void run() {
            injectProperties([f1: 1])
            assert f1 == 1

            assertWillFail(BeanException, ".*setProperty failure: f1. Cause: IllegalArgumentException: null.*",
                    { injectProperties([f1: null]) })
            assert f1 == 1

            injectProperties([f2: 2.toBigDecimal()])
            assert f2 == 2.toBigDecimal()

            assertWillFail(BeanException, ".*setProperty failure: f2. Cause: argument type mismatch.*",
                    { injectProperties([f2: 3]) })
            assert f2 == 2.toBigDecimal()

            injectProperties([f2: null])
            assert f2 == null
        }
    }


}


