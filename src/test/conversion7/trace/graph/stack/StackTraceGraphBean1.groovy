package conversion7.trace.graph.stack

import conversion7.trace.BeanTransformation

@BeanTransformation
class StackTraceGraphBean1 extends StackTestGraphBean {

    int field1

    @Override
    void run() {
        field1++
        step1()
        step2()
        field1++
    }

    void step1() {
        step1_1()
        field1++
    }

    void step1_1() {
        field1++
    }

    void step2() {
        field1++
    }
}
