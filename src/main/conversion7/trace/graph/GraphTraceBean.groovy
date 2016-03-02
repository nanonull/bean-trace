package conversion7.trace.graph

import conversion7.trace.plain.TraceBean
import conversion7.trace.utils.TraceUtils

import java.beans.PropertyChangeEvent

/**By default this graph will have 2 levels: root and all children.<br>
 * You need organize graph logic in your Impl.<br>
 * Example of impl: StackTraceGraphBean<p>
 * Finally after bean completed its execution graph could be printed with:<br>
 * void saveBeanGraph(GraphTraceBean bean) {<br>
 new GraphDumpWriter().write(bean.getClass().getSimpleName(), bean.getGraphJson())<br>}*/
trait GraphTraceBean implements TraceBean {

    Node root = createNewNode("root")
    Node activeNode = root
    Node lastCreatedNode

    @Override
    void initialization(GroovyObject instanceOwner) {
        super.initialization(instanceOwner)
    }

    @Override
    void propertyChange(PropertyChangeEvent changeEvent) {
        addNodeAtNextLevel(buildPropertyChangedMessage(changeEvent))
        super.propertyChange(changeEvent)
    }

    @Override
    void logStep(String classOrigin, String name) {
        addNodeAtNextLevel(buildLogStepMessage(classOrigin, name))
        super.logStep(classOrigin, name)
    }

    void addNodeAtCurrentLevel(String text) {
        def newNode = createNewNode(text)
        if (activeNode.parent) {
            activeNode.parent.addChildren(newNode)
        } else {
            root.addChildren(newNode)
        }
        activeNode = newNode
        lastCreatedNode = newNode
    }

    void addNodeAtNextLevel(String text) {
        def newNode = createNewNode(text)
        activeNode.addChildren(newNode)
        lastCreatedNode = newNode
    }

    Node createNewNode(String text) {
        lastCreatedNode = new Node(text)
        return lastCreatedNode
    }

    String getGraphJson() {
        return TraceUtils.GSON.toJson(root)
    }
}
