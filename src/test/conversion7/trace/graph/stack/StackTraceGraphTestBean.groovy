package conversion7.trace.graph.stack

import conversion7.trace.graph.StackTraceGraphTraceBean

import java.beans.PropertyChangeEvent

abstract class StackTraceGraphTestBean implements StackTraceGraphTraceBean {

    StackTraceGraphTestBean(){
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
