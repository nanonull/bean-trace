package conversion7.trace.graph

import conversion7.trace.plain.TraceBean
import conversion7.trace.utils.ResourcesReader
import conversion7.trace.utils.TraceUtils
import org.apache.commons.io.FileUtils

import static ResourcesReader.loadResource

public class GraphTraceBeanTest extends GroovyTestCase {

    void 'test assignable from'() {
        assert TraceBean.isAssignableFrom(BaseGraphTestBean)
        assert TraceBean.isAssignableFrom(GraphTestBean1)
    }

    void 'test dump content'() {
        def b = GraphBeanTestFactory.beanFactory.create(GraphTestBean1)
        def expGraph = TraceUtils.GSON.fromJson(loadResource('GraphTraceBeanTest_1.json'), Node)

        def graphJson = b.getGraphJson()
        println graphJson
        def actGraph = TraceUtils.GSON.fromJson(graphJson, Node)
        assert actGraph.name == expGraph.name
        assert actGraph.children == expGraph.children
    }

    void 'test dump saved'() {
        // create dump dir from scratch:
        GraphBeanTestFactory.beanFactory.dumpWriter.threadFolder = null
        def b = GraphBeanTestFactory.beanFactory.create(GraphTestBean1)

        def expDumpFile = GraphBeanTestFactory.beanFactory.dumpWriter.threadFolder
        expDumpFile = new File(expDumpFile, GraphTestBean1.getSimpleName() + "_1")
        def expDumpFile1 = new File(expDumpFile, "dump.json")
        def expDumpFile2 = new File(expDumpFile, "index.html")

        assert expDumpFile1.exists()
        assert expDumpFile2.exists()
        assert FileUtils.readFileToString(expDumpFile1).contains("\"name\":\"${GraphTestBean1.getSimpleName()}\"")
        assert !FileUtils.readFileToString(expDumpFile2).isEmpty()
    }

    static class Bean1Empty extends BaseGraphTestBean{
        @Override
        void run() {

        }
    }
    void 'test start graph'(){
        def b = GraphBeanTestFactory.beanFactory.create(Bean1Empty)
        assert b.root
        assert b.root.name == "root"
        assert b.root.children.size() == 1
        assert b.root.children.first().name == Bean1Empty.getSimpleName()
        assert b.root.children.first().children.size() == 0
    }

    void 'test addNodeAtCurrentLevel'(){
        def b = GraphBeanTestFactory.beanFactory.create(Bean1Empty)
        b.addNodeAtCurrentLevel("newNode")

        assert b.root.children.size() == 2
        assert b.root.children.first().name == Bean1Empty.getSimpleName()
        assert b.root.children.first().children.size() == 0

        assert b.root.children.get(1).name == "newNode"
        assert b.root.children.get(1).children.size() == 0
    }

    void 'test addNodeAtNextLevel'(){
        def b = GraphBeanTestFactory.beanFactory.create(Bean1Empty)
        b.addNodeAtNextLevel("newNode")

        assert b.root.children.size() == 1
        assert b.root.children.first().name == Bean1Empty.getSimpleName()
        assert b.root.children.first().children.size() == 1

        assert b.root.children.first().children.first().name == "newNode"
        assert b.root.children.first().children.first().children.size() == 0
    }


}