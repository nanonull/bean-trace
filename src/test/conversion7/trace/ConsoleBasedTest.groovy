package conversion7.trace

abstract class ConsoleBasedTest extends GroovyTestCase{
    def sysOut = System.out
    def sysErr = System.err

    final ByteArrayOutputStream consoleOutContent = new ByteArrayOutputStream();
    final ByteArrayOutputStream consoleErrorContent = new ByteArrayOutputStream();

    public void setUpStreams() {
        System.setOut(new PrintStream(consoleOutContent));
        System.setErr(new PrintStream(consoleErrorContent));
    }

    @Override
    protected void setUp() throws Exception {
        setUpStreams()
    }

    void tearDown() {
        println 'tearDown start'
        System.setOut(sysOut);
        System.setErr(sysErr);
        println 'tearDown end'
    }

}
