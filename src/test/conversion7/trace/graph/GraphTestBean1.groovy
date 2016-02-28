package conversion7.trace.graph

import conversion7.trace.BeanTransformation

@BeanTransformation
class GraphTestBean1 extends BaseGraphTestBean {

    void method1(){
        method1_1()
    }

    void method1_1(){
        println("Value 1-1")
    }

    void method2(){
        method2_2()
    }
    void method2_2(){
        println("Value 2-2")
    }

    @Override
    void run() {
        method1()
        method2()
    }
}
