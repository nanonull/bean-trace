package conversion7.trace.graph

import conversion7.trace.BeanTransformation

@BeanTransformation
class GraphTestBean1 extends BaseGraphTestBean {

    int field1

    @Override
    void run() {
        method1()
        method2()
    }

    void method1(){
        method1_1()
    }

    void method1_1() {
        field1++
    }
    void method2(){
        method2_2()
    }

    void method2_2(){
        field1++
    }
}
