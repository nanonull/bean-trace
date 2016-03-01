package conversion7.trace.graph

import conversion7.trace.AbstractBeanFactory
import conversion7.trace.utils.GraphDumpWriter

class GraphBeanTestFactory<C extends BaseGraphTestBean> extends AbstractBeanFactory {

    GraphDumpWriter dumpWriter = new GraphDumpWriter()

    public C create(final Class<C> type) {
        def inst = super.create(type)
        inst.node = new Node(type.getSimpleName())
        try {
            inst.run()
        } catch (Throwable throwable) {
            inst.println(throwable.getMessage())
        } finally {
            saveBeanGraph(inst)
        }
        return inst
    }

    def saveBeanGraph(GraphTraceBean bean) {
        dumpWriter.write(bean.getClass().getSimpleName(), bean.getGraphJson())
    }
}
