package conversion7.trace.graph

import conversion7.trace.utils.TraceUtils
import conversion7.trace.plain.TraceBean

trait GraphTraceBean implements TraceBean {

    Node node

    @Override
    void initialization(GroovyObject instanceOwner) {
        super.initialization(instanceOwner)
    }

    @Override
    void println(String msg) {
        node.addChildren(new Node(msg))
        super.println(msg)
    }

    String getGraphJson() {
        return TraceUtils.GSON.toJson(node)
    }
}
