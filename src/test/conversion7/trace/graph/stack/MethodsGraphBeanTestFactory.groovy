package conversion7.trace.graph.stack

import conversion7.trace.AbstractBeanFactory
import conversion7.trace.graph.GraphTraceBean
import conversion7.trace.utils.GraphDumpWriter

class MethodsGraphBeanTestFactory<C extends StackTestGraphBean> extends AbstractBeanFactory {

    static MethodsGraphBeanTestFactory factory = new MethodsGraphBeanTestFactory()
    GraphDumpWriter dumpWriter = new GraphDumpWriter()

    public C create(final Class<C> type) {
        def inst = super.create(type)
        inst.addNodeAtCurrentLevel(type.getSimpleName())
        def error
        try {
            inst.run()
        } catch (Throwable throwable) {
            inst.addNodeAtCurrentLevel(throwable.getMessage())
            error = throwable
        } finally {
            saveBeanGraph(inst)
            if (error) {
                throw error
            }
        }
        return inst
    }

    def saveBeanGraph(GraphTraceBean bean) {
        dumpWriter.write(bean.getClass().getSimpleName(), bean.getGraphJson())
    }
}
