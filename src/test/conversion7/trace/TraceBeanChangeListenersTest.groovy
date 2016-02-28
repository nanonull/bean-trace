package conversion7.trace

import conversion7.trace.test_beans.TestApp
import conversion7.trace.test_beans.TestBean1
import conversion7.trace.test_beans.TestBean2Ext
import conversion7.trace.test_beans.TestBean2InnerFieldWrite

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class TraceBeanChangeListenersTest extends GroovyTestCase {

    void testPropertyChangeListenerWorks() {
        def obj1 = TestApp.beanFactory.create(TestBean1)

        obj1.f1++
        assert obj1._changes == 1

        obj1.f1++
        assert obj1._changes == 2
    }

    void 'test PropertyChangeListener Works for bean created with dynamic props'() {
        def obj1 = TestApp.beanFactory.create(TestBean1, ['someF': 10])

        obj1.f1++
        assert obj1._changes == 1

        obj1.f1++
        assert obj1._changes == 2
    }

    void 'test PropertyChangeListener Works after property updated from dynamic props'() {
        def obj1 = TestApp.beanFactory.create(TestBean1, ['f1': 10, f3: 10])
        assert obj1.f1 == 10
        assert obj1._changes == 2

        obj1.f1++
        assert obj1.f1 == 11
        assert obj1._changes == 3

        obj1.f3++
        assert obj1._changes == 4
    }

    void 'testPropertyChangeListener DifferentWays outside instance'() {
        def obj1 = TestApp.beanFactory.create(TestBean1)

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
        def b1 = TestApp.beanFactory.create(TestBean2InnerFieldWrite) // tested inside
    }

    void 'testPropertyChangeListener DifferentWays within instance exts'() {
        def b2 = TestApp.beanFactory.create(TestBean2Ext) // tested inside
    }

    void 'test PropertyChange listens NOT only for new values'() {
        def obj1 = TestApp.beanFactory.create(TestBean1)

        obj1.f1 = 10
        assert obj1._changes == 1
        obj1.f1 = 10
        assert obj1._changes == 2
    }

    void 'test add custom property listener'() {
        def b = TestApp.beanFactory.create(TestBean1)
        PropertyChangeEvent changeEvent
        b.addPropertyChangeListener('f1', new PropertyChangeListener() {
            @Override
            void propertyChange(PropertyChangeEvent evt) {
                changeEvent = evt
            }
        })

        b.f1 = 10
        assert b._changes == 1
        assert changeEvent.oldValue == 0
        assert changeEvent.newValue == 10
        assert changeEvent.propertyName == 'f1'

        b.f1 = 11
        assert b._changes == 2
        assert changeEvent.oldValue == 10
        assert changeEvent.newValue == 11
        assert changeEvent.propertyName == 'f1'
    }

    void 'test remove custom property listener'() {
        def b = TestApp.beanFactory.create(TestBean1)
        PropertyChangeEvent changeEvent
        def listener = new PropertyChangeListener() {
            @Override
            void propertyChange(PropertyChangeEvent evt) {
                changeEvent = evt
            }
        }
        b.addPropertyChangeListener('f1', listener)

        b.f1 = 10
        assert b._changes == 1
        assert changeEvent.newValue == 10

        changeEvent = null
        // remove from common listeners, but not from 'f1', so it will not stop to listen
        b.removePropertyChangeListener(listener)
        b.f1 = 11
        assert b._changes == 2
        assert changeEvent.newValue == 11

        changeEvent = null
        b.removePropertyChangeListener('f1', listener)
        b.f1 = 12
        assert b._changes == 3
        assert changeEvent == null
    }

    void 'test add/remove custom common property listener'() {
        def b = TestApp.beanFactory.create(TestBean1)
        PropertyChangeEvent changeEvent
        def listener = new PropertyChangeListener() {
            @Override
            void propertyChange(PropertyChangeEvent evt) {
                changeEvent = evt
            }
        }
        b.addPropertyChangeListener(listener)

        b.f1 = 10
        assert b._changes == 1
        assert changeEvent.newValue == 10

        b.f2 = 11
        assert b._changes == 2
        assert changeEvent.propertyName == 'f2'
        assert changeEvent.newValue == 11

        changeEvent = null
        b.removePropertyChangeListener(listener) // remove from common listeners, but not from 'f1'...
        b.f1 = 22
        b.f2 = 23
        assert b._changes == 4
        assert changeEvent == null
    }

}
