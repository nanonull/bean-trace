package conversion7.trace.graph.stack

import conversion7.trace.BeanException
import conversion7.trace.graph.GraphTraceBean
import conversion7.trace.graph.Node

import java.beans.PropertyChangeEvent

trait StackTraceGraphBean implements GraphTraceBean {
    StackTraceElement lastTrace

    void resolveMethodPositionOnStack() {
        // resolve stack trace place
        def myClass = getClass()
        def stackTrace = Thread.currentThread().stackTrace
        def newTrace = stackTrace.find { trace ->
            try {
                myClass.isAssignableFrom(Class.forName(trace.className))
            } catch (ClassNotFoundException t) {
            }
        }

        if (newTrace) {
            // chose active node for next child node by comparing trace positions:
            activeNode = findActiveNodeOnStackFrom(getStackTraceNodeActive(), false)
            if (!activeNode) {
                activeNode = getFallbackRootNode()
            }

            lastTrace = newTrace
        } else {
            activeNode = getFallbackRootNode()
        }
    }

    def getFallbackRootNode(){
        root.children.last()
    }

    @Override
    void propertyChange(PropertyChangeEvent changeEvent) {
        activeNode = findActiveNodeOnStackFrom(getStackTraceNodeActive(), false)
        if (!activeNode) {
            activeNode = getFallbackRootNode()
        }
        super.propertyChange(changeEvent)

        (lastCreatedNode as StackTraceNode).with {
            it.trace = lastTrace
        }
    }

    StackTraceNode findActiveNodeOnStackFrom(StackTraceNode input, boolean mustFound) {
        def node
        Thread.currentThread().stackTrace.find { trace ->
            node = input.findFirstNodeWithTraceElement(trace)
            return node
        }

        if (node) {
            return node
        } else {
            if (mustFound) {
                throw new BeanException("BUG: Stacktrace was resolved in wrong way!")
            } else {
                return null
            }
        }
    }

    @Override
    void logStep(String classOrigin, String name) {
        resolveMethodPositionOnStack()

        super.logStep(classOrigin, name)

        (lastCreatedNode as StackTraceNode).with {
            it.trace = lastTrace
        }
        activeNode = lastCreatedNode
    }

    Node createNewNode(String text) {
        return new StackTraceNode(text)
    }

    StackTraceNode getStackTraceNodeRoot() {
        return (StackTraceNode) root
    }

    StackTraceNode getStackTraceNodeActive() {
        return (StackTraceNode) activeNode
    }


}
