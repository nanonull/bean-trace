package conversion7.trace.graph

import java.beans.PropertyChangeEvent

abstract class BaseGraphTestBean implements GraphTraceBean, Runnable {

    public BaseGraphTestBean() {
        initialization(this)
    }

    String buildLogStepMessage(String classOrigin, String name) {
        return "$name from $classOrigin"
    }

    String buildPropertyChangedMessage(PropertyChangeEvent changeEvent) {
        return "${changeEvent.propertyName}: ${changeEvent.oldValue} --- ${changeEvent.newValue}"
    }

}
