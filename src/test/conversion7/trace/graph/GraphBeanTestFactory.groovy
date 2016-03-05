package conversion7.trace.graph

import conversion7.trace.AbstractBeanFactory
import conversion7.trace.utils.GraphDumpWriter

class GraphBeanTestFactory extends AbstractBeanFactory {

    public static GraphBeanTestFactory beanFactory = new GraphBeanTestFactory()
    GraphDumpWriter dumpWriter = new GraphDumpWriter()

    public <C extends BaseGraphTestBean> C create(final Class<C> type) {
        def inst = super.create(type)
        inst.addNodeAtCurrentLevel(type.getSimpleName())
        try {
            inst.run()
        } catch (Throwable throwable) {
            inst.addNodeAtCurrentLevel(throwable.getMessage())
        } finally {
            saveBeanGraph(inst)
        }
        return inst
    }

    def saveBeanGraph(GraphTraceBean bean) {
        dumpWriter.write(bean.getClass().getSimpleName(), bean.getGraphJson())
    }
}
