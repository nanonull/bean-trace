package conversion7.trace.plain.test_beans

import conversion7.trace.plain.BeanTestFactory
import conversion7.trace.plain.TraceBean

abstract class BaseTestBean implements TraceBean, GroovyObject, Runnable {

    public static BeanTestFactory beanFactory = new BeanTestFactory()

    public BaseTestBean() {
        initialization(this)
    }

}
