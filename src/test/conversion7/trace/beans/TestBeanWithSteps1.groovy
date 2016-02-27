package conversion7.trace.beans

import conversion7.trace.BeanTransformation
import conversion7.trace.TraceBean

@BeanTransformation
class TestBeanWithSteps1 extends TraceBean {

    static int FIELD1_BEAN1_FINAL = 100
    static List expLogLines =
            ["TestBeanWithSteps1: '_f1' write: '1' >>> '2'"
             , "TestBeanWithSteps1: step1"
             , "TestBeanWithSteps1: '_f1' write: '2' >>> '3'"
             , "TestBeanWithSteps1: step1_1"
             , "TestBeanWithSteps1: '_f1' write: '3' >>> '100'"
             , "TestBeanWithSteps1: in step1_1"
            ]
    int f1 = 1

    @Override
    void run() {
        f1++
        assert f1 == 2
        step1()
    }

    void step1() {
        f1++
        assert f1 == 3
        step1_1()
    }

    def step1_1() {
        f1 = FIELD1_BEAN1_FINAL
        assert f1 == FIELD1_BEAN1_FINAL
        println "in step1_1"
    }
}
