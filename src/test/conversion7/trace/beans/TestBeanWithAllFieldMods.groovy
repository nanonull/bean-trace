package conversion7.trace.beans

import conversion7.trace.BeanTransformation
import conversion7.trace.TraceBean

@BeanTransformation
class TestBeanWithAllFieldMods extends TraceBean {
    public int f1
    protected int f2
    private int f3
    private int f4
    int f5

    @Override
    void run() {
        println("")
    }
}
