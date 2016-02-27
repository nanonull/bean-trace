package conversion7.trace.beans

import conversion7.trace.BeanTransformation
import conversion7.trace.TraceBean

@BeanTransformation
class TestBean2InnerFieldWrite extends TraceBean {
    int f1

    @Override
    void run() {
        int expChanges = 1
        f1++
        assert f1 == 1
        assert changes == expChanges
        expChanges++

        setF1(10)
        assert changes == expChanges
        expChanges++

        setProperty('f1', 20)
        assert changes == expChanges
        expChanges++

        this.f1++
        assert changes == expChanges
        expChanges++

        // TODO 0 impl like this:
//        this.metaClass.setAttribute = { Object object, String attribute, Object newValue ->
//            object.setProperty(attribute, newValue)
//        }

//        this.@f1 = 30 // skip!
//        assert changes == expChanges
//        expChanges++
    }

}