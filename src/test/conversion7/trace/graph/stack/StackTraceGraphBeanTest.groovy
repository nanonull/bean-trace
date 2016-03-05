package conversion7.trace.graph.stack

import conversion7.trace.BeanTransformation
import conversion7.trace.graph.Node
import conversion7.trace.utils.ResourcesReader
import conversion7.trace.utils.TraceUtils

class StackTraceGraphBeanTest extends GroovyTestCase {

    void 'test 1'() {
        def b = StackTraceBeanTestFactory.factory.create(StackTraceGraphBean1)
        def graphJson = b.getGraphJson()
        println graphJson
        def act = TraceUtils.GSON.fromJson(graphJson, Node)
        def exp = TraceUtils.GSON.fromJson(
                ResourcesReader.loadResource("StackTraceGraphBeanTest_1.json"), Node)
        assert act == exp
    }

    void 'test tracing when stack is outside instance'() {
        def b = StackTraceBeanTestFactory.factory.create(Bean1)
        b.f1++
        b.step1()

        def graphJson = b.getGraphJson()
        println graphJson
        def act = TraceUtils.GSON.fromJson(graphJson, Node)
        def exp = TraceUtils.GSON.fromJson(
                ResourcesReader.loadResource("StackTraceGraphBeanTest_2.json"), Node)
        assert act == exp
    }

    @BeanTransformation
    static class Bean1 extends StackTestGraphBean {
        int f1

        @Override
        void run() {

        }

        void step1() {

        }
    }


}
