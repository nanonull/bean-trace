package conversion7.trace.plain.test_beans

import conversion7.trace.BeanTransformation

@BeanTransformation
class TestBeanWithSteps1Ext extends TestBeanWithSteps1 {

    static List expLogLines2 =
            [
                    "TestBeanWithSteps1Ext:  Invoke 'run' from TestBeanWithSteps1Ext"
                    , "TestBeanWithSteps1Ext:  Invoke 'run' from TestBeanWithSteps1"
                    , "TestBeanWithSteps1Ext: 'f1' property: '1' >>> '2'"
                    , "TestBeanWithSteps1Ext:  Invoke 'step1' from TestBeanWithSteps1"
                    , "TestBeanWithSteps1Ext: 'f1' property: '2' >>> '3'"
                    , "TestBeanWithSteps1Ext:  Invoke 'step1_1' from TestBeanWithSteps1"
                    , "TestBeanWithSteps1Ext: 'f1' property: '3' >>> '100'"
                    , "TestBeanWithSteps1Ext: in step1_1"
                    , "TestBeanWithSteps1Ext:  Invoke 'last manual method' from TestBeanWithSteps1Ext"
            ] + [
                    "TestBeanWithSteps1Ext:  Invoke 'manualStep' from TestBeanWithSteps1Ext"
                    , "TestBeanWithSteps1Ext: 'f1' property: '100' >>> '101'"
                    , "TestBeanWithSteps1Ext:  Invoke 'step2' from TestBeanWithSteps1Ext"
                    , "TestBeanWithSteps1Ext: 'f1' property: '101' >>> '102'"
                    , "TestBeanWithSteps1Ext:  Invoke 'step2_1' from TestBeanWithSteps1Ext"
                    , "TestBeanWithSteps1Ext: 'f1' property: '102' >>> '103'"
            ]

    @Override
    void run() {
        super.run()
        logStep("manualStep")

        f1++
        assert f1 == FIELD1_BEAN1_FINAL + 1
        step2()
    }

    void step2() {
        f1++
        assert f1 == FIELD1_BEAN1_FINAL + 2
        step2_1()
    }

    void step2_1() {
        f1++
        assert f1 == FIELD1_BEAN1_FINAL + 3
    }
}
