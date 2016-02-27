package conversion7.trace

@BeanTransformation
class Bean4 extends TraceBean {
    int f1 = -1
    static int CONST_F1 = 10

    @Override
    void run() {

    }

    void setF1(final int f1) {
        this.f1 = CONST_F1
    }
}
