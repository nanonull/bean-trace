package conversion7.trace

class TestUtils {

    public static final String NULL_MESSAGE = "null"

    static void assertWillFail(Class<? extends Throwable> errorLowestType, String messagePattern, Closure code) {
        try {
            code.run()
            throw new AssertionError("Expected to be failed: $errorLowestType, msgPattern: $messagePattern")
        } catch (Throwable t) {
            assert (t.getClass().isAssignableFrom(errorLowestType))
            if (messagePattern) {
                if (messagePattern == NULL_MESSAGE) {
                    assert t.getMessage() == null
                } else {
                    assert t.getMessage()
                    assert t.getMessage().matches(messagePattern)
                }
            }
        }
    }
}
