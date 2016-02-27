package conversion7.trace

import conversion7.trace.test_beans.ClientBeanImpl
import conversion7.trace.test_beans.TestBean1
import conversion7.trace.test_beans.TestBean2Ext
import conversion7.trace.test_beans.TestBean2InnerFieldWrite

class TraceBeanChangeListenersTest extends GroovyTestCase {

    void testPropertyChangeListenerWorks() {
        def obj1 = BaseBeanManager.create(TestBean1)

        obj1.f1++
        assert obj1._changes == 1

        obj1.f1++
        assert obj1._changes == 2
    }

    void 'test PropertyChangeListener Works for bean created with dynamic props'() {
        def obj1 = BaseBeanManager.create(TestBean1, ['someF': 10])

        obj1.f1++
        assert obj1._changes == 1

        obj1.f1++
        assert obj1._changes == 2
    }

    void 'test PropertyChangeListener Works after property updated from dynamic props'() {
        def obj1 = BaseBeanManager.create(TestBean1, ['f1': 10, f3: 10])
        assert obj1.f1 == 10
        assert obj1._changes == 2

        obj1.f1++
        assert obj1.f1 == 11
        assert obj1._changes == 3

        obj1.f3++
        assert obj1._changes == 4
    }

    void 'testPropertyChangeListener DifferentWays outside instance'() {
        def obj1 = ClientBeanImpl.create(TestBean1)

        obj1.f1++
        assert obj1._changes == 1

        obj1.setF1(10)
        assert obj1._changes == 2

        obj1.setProperty('f1', 20)
        assert obj1._changes == 3

        // not supported!
//        obj1.@f1 = 30
//        assert obj1._changes == 3

        obj1."f1" = 40
        assert obj1._changes == 4

    }

    void 'testPropertyChangeListener DifferentWays within instance'() {
        def b1 = ClientBeanImpl.create(TestBean2InnerFieldWrite) // tested inside
    }

    void 'testPropertyChangeListener DifferentWays within instance exts'() {
        def b2 = ClientBeanImpl.create(TestBean2Ext) // tested inside
    }

    void 'test PropertyChange listens NOT only for new values'() {
        def obj1 = BaseBeanManager.create(TestBean1)

        obj1.f1 = 10
        assert obj1._changes == 1
        obj1.f1 = 10
        assert obj1._changes == 2
    }

}
