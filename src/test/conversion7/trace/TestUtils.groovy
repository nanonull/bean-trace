package conversion7.trace

class TestUtils {

    static void assertWillFail(Class<? extends Throwable> errorLowestType, String messagePattern, Closure code) {
        try {
            code.run()
            throw new AssertionError("Expected to be failed: $errorLowestType, msgPattern: $messagePattern")
        } catch (Throwable t) {
            assert (t.getClass().isAssignableFrom(errorLowestType))
            if (messagePattern) {
                assert t.getMessage().matches(messagePattern)
            }
        }
    }
}
