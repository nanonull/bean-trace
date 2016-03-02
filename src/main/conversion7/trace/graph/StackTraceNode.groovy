package conversion7.trace.graph

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false, includeFields = true,
        includes = ["name", "children"]
)
@EqualsAndHashCode()
class StackTraceNode extends Node {

    transient StackTraceElement trace

    StackTraceNode(String name) {
        super(name)
    }

    StackTraceNode findFirstNodeWithTraceElement(StackTraceElement stackTraceElement) {
        if (trace){
            if (areEqualStackTraceElements(trace, stackTraceElement)) {
                return this
            }

            if (parent) {
                return (parent as StackTraceNode).findFirstNodeWithTraceElement(stackTraceElement)
            }

            return null
        }
        return null
    }

    static boolean areEqualStackTraceElements(StackTraceElement e1, StackTraceElement e2) {
        return e1.className == e2.className &&
                e1.methodName == e2.methodName
    }
}
