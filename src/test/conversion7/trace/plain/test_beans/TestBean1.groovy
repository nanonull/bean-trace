package conversion7.trace.plain.test_beans

import conversion7.trace.BeanTransformation

@BeanTransformation
class TestBean1 extends BaseTestBean {
    int f1
    int f2
    Integer f3 = 0

    int getThisF3() {
        println 'getThisF3'
        return this.f3
    }

    void setThisF3(final int f3) {
        println 'setF3'
        this.f3 = f3
    }

    @Override
    void run() {

    }
}