package conversion7.trace.graph

abstract class BaseGraphTestBean implements GraphTraceBean, Runnable {

    public static GraphBeanTestFactory beanFactory = new GraphBeanTestFactory()

    public BaseGraphTestBean() {
        initialization(this)
    }

    // simplify testing
    @Override
    void methodInvoked(String classNameWhereMethodDefined, String name) {
        println("$name from $classNameWhereMethodDefined")
    }

}
