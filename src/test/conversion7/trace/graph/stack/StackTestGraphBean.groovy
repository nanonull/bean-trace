package conversion7.trace.graph.stack

import conversion7.trace.graph.StackTraceGraphBean

import java.beans.PropertyChangeEvent

abstract class StackTestGraphBean implements StackTraceGraphBean {

    StackTestGraphBean() {
        initialization(this)
    }

    abstract void run()

    String buildLogStepMessage(String classOrigin, String name) {
        return "$name from $classOrigin"
    }

    String buildPropertyChangedMessage(PropertyChangeEvent changeEvent) {
        return "${changeEvent.propertyName}: ${changeEvent.oldValue} --- ${changeEvent.newValue}"
    }

}
