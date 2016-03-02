package conversion7.trace.graph

import conversion7.trace.BeanException

import java.beans.PropertyChangeEvent

trait StackTraceGraphTraceBean implements GraphTraceBean {
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

        if (!newTrace) {
            throw new BeanException(myClass.getName() + " was not found in stacktrace!")
        }

        // chose active node for next child node by comparing trace positions:
        activeNode = getActiveNodeOnStackFrom(stackTraceNodeActive, false)
        if (!activeNode) {
            activeNode = root.children.last()
        }

        lastTrace = newTrace
    }

    @Override
    void propertyChange(PropertyChangeEvent changeEvent) {
        activeNode = getActiveNodeOnStackFrom(stackTraceNodeActive, true)
        super.propertyChange(changeEvent)

        (lastCreatedNode as StackTraceNode).with {
            it.trace = lastTrace
        }
    }

    StackTraceNode getActiveNodeOnStackFrom(StackTraceNode input, boolean mustFound) {
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
