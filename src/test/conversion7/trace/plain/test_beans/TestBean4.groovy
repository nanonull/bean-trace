package conversion7.trace.plain.test_beans

import conversion7.trace.BeanTransformation

@BeanTransformation
class TestBean4 extends BaseTestBean {
    int f1 = -1
    static int CONST_F1 = 10


    void setF1(final int f1) {
        this.f1 = CONST_F1
    }

    @Override
    void run() {

    }
}
