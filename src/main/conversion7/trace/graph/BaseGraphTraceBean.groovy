package conversion7.trace.graph

/**Minimal implementation.<br>
 * Especially, it'll be helpful in env without trait support. */
abstract class BaseGraphTraceBean implements GraphTraceBean {

    BaseGraphTraceBean() {
        initialization(this)
    }
}
