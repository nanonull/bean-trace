package conversion7.trace.graph.stack

import conversion7.trace.graph.Node
import conversion7.trace.utils.ResourcesReader
import conversion7.trace.utils.TraceUtils

class StackTraceGraphBeanTest extends GroovyTestCase {

    void 'test 1'() {
        def b = MethodsGraphBeanTestFactory.factory.create(StackTraceGraphBean1)
        def graphJson = b.getGraphJson()
        println graphJson
        def act = TraceUtils.GSON.fromJson(graphJson, Node)
        def exp = TraceUtils.GSON.fromJson(
                ResourcesReader.loadResource("MethodsGraphTraceBeanTest_1.json"), Node)
        assert act == exp
    }

}