package conversion7.trace.test_beans

import conversion7.trace.TraceBean

abstract class BeanTestImpl implements GroovyObject, TraceBean {

    public BeanTestImpl() {
        initTracing(this)
    }

    abstract void run()

}
