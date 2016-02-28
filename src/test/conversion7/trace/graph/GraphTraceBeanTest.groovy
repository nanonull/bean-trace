package conversion7.trace.graph

import conversion7.trace.plain.TraceBean
import conversion7.trace.utils.ResourcesReader
import conversion7.trace.utils.TraceUtils
import org.apache.commons.io.FileUtils

import static ResourcesReader.loadResource
import static conversion7.trace.utils.ResourcesReader.findResource

public class GraphTraceBeanTest extends GroovyTestCase {

    void 'test assignable from'() {
        assert TraceBean.isAssignableFrom(BaseGraphTestBean)
        assert TraceBean.isAssignableFrom(GraphTestBean1)
    }

    void 'test dump content'() {
        def b = BaseGraphTestBean.beanFactory.create(GraphTestBean1, null)
        def expGraph = TraceUtils.GSON.fromJson(loadResource('GraphTraceBeanTest_1.json'), Node)
        def actGraph = TraceUtils.GSON.fromJson(b.getGraphJson(), Node)
        assert actGraph.name == expGraph.name
        assert actGraph.children == expGraph.children
    }

    void 'test dump saved'() {
        // create dump dir from scratch:
        BaseGraphTestBean.beanFactory.dumpWriter.threadFolder = null
        def b = BaseGraphTestBean.beanFactory.create(GraphTestBean1, null)

        def expDumpFile = BaseGraphTestBean.beanFactory.dumpWriter.threadFolder
        expDumpFile = new File(expDumpFile, GraphTestBean1.getSimpleName() + "_1")
        def expDumpFile1 = new File(expDumpFile, "dump.json")
        def expDumpFile2 = new File(expDumpFile, "index.html")

        assert expDumpFile1.exists()
        assert expDumpFile2.exists()
        assert FileUtils.readFileToString(expDumpFile1).contains("\"name\":\"${GraphTestBean1.getSimpleName()}\"")
        assert !FileUtils.readFileToString(expDumpFile2).isEmpty()
    }

}