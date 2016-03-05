package conversion7.trace.plain

import conversion7.trace.BeanTransformation
import conversion7.trace.ConsoleBasedTest
import conversion7.trace.graph.BaseGraphTestBean
import conversion7.trace.graph.GraphBeanTestFactory
import conversion7.trace.plain.test_beans.BaseTestBean
import conversion7.trace.plain.test_beans.TestBeanWithSteps1
import conversion7.trace.plain.test_beans.TestBeanWithSteps1Ext

class TraceBeanLogTest extends ConsoleBasedTest {

    void 'test log in simple bean'() {

        def beanWithSteps1 = BeanTestFactory.beanFactory.create(TestBeanWithSteps1)
        assert (consoleOutContent.toString()
                .split("\r\n") as List) == TestBeanWithSteps1.expLogLines
    }

    void 'test log in extended bean'() {

        def bean = BeanTestFactory.beanFactory.create(TestBeanWithSteps1Ext)
        assert (consoleOutContent.toString()
                .split("\r\n") as List) == TestBeanWithSteps1Ext.expLogLines2
    }

    void 'test should trace: plain'() {
        def bean = BeanTestFactory.beanFactory.create(Bean40)
        bean.f1++
        assert bean._changes == 0
        assert consoleOutContent.toString() == ""
    }

    @BeanTransformation
    static class Bean40 extends BaseTestBean {
        int f1
        @Override
        boolean shouldTrace() {
            return false
        }

        @Override
        void run() {

        }
    }

    void 'test should trace: graph'() {
        def bean = GraphBeanTestFactory.beanFactory.create(Bean41)
        bean.f1++
        assert bean._changes == 0
        assert consoleOutContent.toString() == ""
    }

    @BeanTransformation
    static class Bean41 extends BaseGraphTestBean {
        int f1
        @Override
        boolean shouldTrace() {
            return false
        }

        @Override
        void run() {

        }
    }



}
