package conversion7.trace.plain

import conversion7.trace.ConsoleBasedTest
import conversion7.trace.plain.test_beans.BaseTestBean
import conversion7.trace.plain.test_beans.TestBeanWithSteps1
import conversion7.trace.plain.test_beans.TestBeanWithSteps1Ext

class TraceBeanLogTest extends ConsoleBasedTest {

    //  works only for tests with setUpStreams?
    void 'test log in simple bean'() {

        def beanWithSteps1 = BaseTestBean.beanFactory.create(TestBeanWithSteps1, null)
        assert (consoleOutContent.toString()
                .split("\r\n") as List) == TestBeanWithSteps1.expLogLines
    }

    //  works only for tests with setUpStreams?
    void 'test log in extended bean'() {

        def bean = BaseTestBean.beanFactory.create(TestBeanWithSteps1Ext, null)
        assert (consoleOutContent.toString()
                .split("\r\n") as List) == TestBeanWithSteps1Ext.expLogLines2
    }


}
