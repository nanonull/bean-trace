package conversion7.trace


@BeanTransformation
class TestBean1 extends TraceBean {
    int f1
    int f2
    int f3

    @Override
    void run() {
    }

    int getThisF3() {
        println 'getThisF3'
        return this.f3
    }

    void setThisF3(final int f3) {
        println 'setF3'
        this.f3 = f3
    }
}