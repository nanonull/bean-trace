package conversion7.trace.plain

import conversion7.trace.BeanTransformation
import conversion7.trace.ConsoleBasedTest
import conversion7.trace.plain.test_beans.BaseTestBean

class MethodInterceptionTest extends ConsoleBasedTest {

    void 'covered in TraceBeanLogTest: test default interception'() {}

    void 'test custom interception'() {
        def b = BaseTestBean.beanFactory.create(Bean1, null)
        assert b.mainRun == 1
        b.run3()
        assert b.mainRun == 2

        def expRows = """\
            Bean1: MethodInterceptionTest\$Bean1 >>> run
            Bean1: MethodInterceptionTest\$Bean1 >>> runMain
            Bean1: MethodInterceptionTest\$Bean1 >>> runMain1_2
            Bean1: 'mainRun' write: '0' >>> '1'
            Bean1: MethodInterceptionTest\$Bean1 >>> run3
            Bean1: MethodInterceptionTest\$Bean1 >>> runMain
            Bean1: MethodInterceptionTest\$Bean1 >>> runMain1_2
            Bean1: 'mainRun' write: '1' >>> '2'""".stripIndent().split("\n").toList()
        def actualRows = consoleOutContent.toString().stripIndent().split("\r\n").toList()
        assert actualRows == expRows
    }

    @BeanTransformation
    static class Bean1 extends BaseTestBean {

        int mainRun

        void methodInvoked(String classNameWhereMethodDefined, String name) {
            if (name == 'runMain1_1') {
                return
            }
            println("$classNameWhereMethodDefined >>> $name")
        }

        @Override
        void run() {
            runMain()
        }

        void runMain() {
            runMain1_1()
            runMain1_2()
            mainRun++
        }

        void runMain1_1() {

        }

        void runMain1_2() {

        }

        void run3() {
            runMain()
        }
    }

    void 'test custom interception in ext classes'() {
        def b = BaseTestBean.beanFactory.create(Bean1Ext, null)
        assert b.mainRun == 2

        def expRows = """\
            Bean1Ext: MethodInterceptionTest\$Bean1Ext >>> run
            Bean1Ext: MethodInterceptionTest\$Bean1 >>> run
            Bean1Ext: MethodInterceptionTest\$Bean1 >>> runMain
            Bean1Ext: MethodInterceptionTest\$Bean1 >>> runMain1_2
            Bean1Ext: 'mainRun' write: '0' >>> '1'
            Bean1Ext: MethodInterceptionTest\$Bean1Ext >>> extLogic
            Bean1Ext: MethodInterceptionTest\$Bean1Ext >>> step1
            Bean1Ext: MethodInterceptionTest\$Bean1 >>> runMain
            Bean1Ext: MethodInterceptionTest\$Bean1 >>> runMain1_2
            Bean1Ext: 'mainRun' write: '1' >>> '2'""".stripIndent().split("\n").toList()
        def actualRows = consoleOutContent.toString().stripIndent().split("\r\n").toList()
        assert actualRows == expRows
    }

    @BeanTransformation
    static class Bean1Ext extends Bean1 {
        @Override
        void run() {
            super.run()
            extLogic()
        }

        // no logging:
        @Override
        void runMain1_1() {
            super.runMain1_1()
        }

        void extLogic() {
            step1()
            super.runMain()
        }

        void step1() {

        }
    }
}
