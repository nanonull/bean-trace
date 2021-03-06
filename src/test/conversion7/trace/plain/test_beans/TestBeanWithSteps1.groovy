package conversion7.trace.plain.test_beans

import conversion7.trace.BeanTransformation

@BeanTransformation
class TestBeanWithSteps1 extends BaseTestBean {

    static int FIELD1_BEAN1_FINAL = 100
    static List expLogLines =
            [
                    "TestBeanWithSteps1:  Invoke 'run' from TestBeanWithSteps1"
                    , "TestBeanWithSteps1: 'f1' property: '1' >>> '2'"
                    , "TestBeanWithSteps1:  Invoke 'step1' from TestBeanWithSteps1"
                    , "TestBeanWithSteps1: 'f1' property: '2' >>> '3'"
                    , "TestBeanWithSteps1:  Invoke 'step1_1' from TestBeanWithSteps1"
                    , "TestBeanWithSteps1: 'f1' property: '3' >>> '100'"
                    , "TestBeanWithSteps1: in step1_1"
                    , "TestBeanWithSteps1:  Invoke 'last manual method' from TestBeanWithSteps1"
            ]
    int f1 = 1

    @Override
    void run() {
        f1++
        assert f1 == 2
        step1()
        logStep("last manual method")
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
