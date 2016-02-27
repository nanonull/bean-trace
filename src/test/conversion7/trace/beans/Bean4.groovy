package conversion7.trace.beans

import conversion7.trace.BeanTransformation
import conversion7.trace.TraceBean

@BeanTransformation
class Bean4 extends TraceBean {
    int f1 = -1
    static int CONST_F1 = 10


    void setF1(final int f1) {
        this.f1 = CONST_F1
    }
}
