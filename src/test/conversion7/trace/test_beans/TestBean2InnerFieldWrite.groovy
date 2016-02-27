package conversion7.trace.test_beans

import conversion7.trace.BeanTransformation

@BeanTransformation
class TestBean2InnerFieldWrite extends ClientBeanTestImpl {
    int f1

    @Override
    void run() {
        int expChanges = 1
        f1++
        assert f1 == 1
        assert _changes == expChanges
        expChanges++

        setF1(10)
        assert _changes == expChanges
        expChanges++

        setProperty('f1', 20)
        assert _changes == expChanges
        expChanges++

        this.f1++
        assert _changes == expChanges
        expChanges++

        // TODO 0 impl like this:
//        this.metaClass.setAttribute = { Object object, String attribute, Object newValue ->
//            object.setProperty(attribute, newValue)
//        }

//        this.@f1 = 30 // skip!
//        assert _changes == expChanges
//        expChanges++
    }

}