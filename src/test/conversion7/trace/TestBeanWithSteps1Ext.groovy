package conversion7.trace

@BeanTransformation
class TestBeanWithSteps1Ext extends TestBeanWithSteps1 {

    static List expLogLines2 =
            ["TestBeanWithSteps1Ext: '_f1' change: '1' >>> '2'"
             , "TestBeanWithSteps1: step1"
             , "TestBeanWithSteps1Ext: '_f1' change: '2' >>> '3'"
             , "TestBeanWithSteps1: step1_1"
             , "TestBeanWithSteps1Ext: '_f1' change: '3' >>> '100'"
             , "TestBeanWithSteps1Ext: in step1_1"
            ] +
                    ["TestBeanWithSteps1Ext: '_f1' change: '100' >>> '101'"
                     , "TestBeanWithSteps1Ext: step2"
                     , "TestBeanWithSteps1Ext: '_f1' change: '101' >>> '102'"
                     , "TestBeanWithSteps1Ext: step2_1"
                     , "TestBeanWithSteps1Ext: '_f1' change: '102' >>> '103'"
                    ]

    @Override
    void run() {
        super.run()

        f1++
        assert PROP_CHANGE_NAME == "propertyChange"
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
