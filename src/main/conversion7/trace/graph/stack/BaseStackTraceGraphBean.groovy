package conversion7.trace.graph.stack

/**Minimal implementation.<br>
 * Especially, it'll be helpful in env without trait support. */
abstract class BaseStackTraceGraphBean implements StackTraceGraphBean {

    BaseStackTraceGraphBean() {
        initialization(this)
    }
}
