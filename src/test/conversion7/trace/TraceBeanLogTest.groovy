package conversion7.trace

import conversion7.trace.beans.TestBeanWithSteps1
import conversion7.trace.beans.TestBeanWithSteps1Ext

class TraceBeanLogTest extends GroovyTestCase {
    static def sysOut = System.out
    static def sysErr = System.err

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    void tearDown() {
        println 'cleanUpStreams'
        System.setOut(sysOut);
        System.setErr(sysErr);
        println 'cleanUpStreams2'
    }

    //  works only for tests with setUpStreams?
    void 'test log in simple bean'() {
        setUpStreams()

        def beanWithSteps1 = TraceBeanCalc.create(TestBeanWithSteps1)
        assert (outContent.toString()
                .replaceAll(".*BaseBean - ", "")
                .split("\r\n") as List) == TestBeanWithSteps1.expLogLines
    }

    //  works only for tests with setUpStreams?
    void 'test log in extended bean'() {
        setUpStreams()

        def bean = TraceBeanCalc.create(TestBeanWithSteps1Ext)
        assert (outContent.toString()
                .replaceAll(".*BaseBean - ", "")
                .split("\r\n") as List) == TestBeanWithSteps1Ext.expLogLines2
    }


}
