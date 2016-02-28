package conversion7.trace

import conversion7.trace.test_beans.TestApp
import conversion7.trace.test_beans.TestBeanWithSteps1
import conversion7.trace.test_beans.TestBeanWithSteps1Ext

class TraceBeanLogTest extends ConsoleBasedTest {

    //  works only for tests with setUpStreams?
    void 'test log in simple bean'() {

        def beanWithSteps1 = TestApp.beanFactory.create(TestBeanWithSteps1)
        assert (consoleOutContent.toString()
                .replaceAll(".*BaseBean - ", "")
                .split("\r\n") as List) == TestBeanWithSteps1.expLogLines
    }

    //  works only for tests with setUpStreams?
    void 'test log in extended bean'() {

        def bean = TestApp.beanFactory.create(TestBeanWithSteps1Ext)
        assert (consoleOutContent.toString()
                .replaceAll(".*BaseBean - ", "")
                .split("\r\n") as List) == TestBeanWithSteps1Ext.expLogLines2
    }


}
