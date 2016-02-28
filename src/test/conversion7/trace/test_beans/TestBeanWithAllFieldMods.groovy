package conversion7.trace.test_beans

import conversion7.trace.BeanTransformation

@BeanTransformation
class TestBeanWithAllFieldMods extends BeanTestImpl {
    public int f1
    protected int f2
    private int f3
    private int f4
    int f5

    @Override
    void run() {
        println("Done.")
    }
}
