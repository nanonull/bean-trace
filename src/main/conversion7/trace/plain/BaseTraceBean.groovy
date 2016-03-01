package conversion7.trace.plain

/**Minimal implementation.<br>
 * Especially, it'll be helpful in env without trait support. */
abstract class BaseTraceBean implements TraceBean {

    BaseTraceBean() {
        initialization(this)
    }
}
