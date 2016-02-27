package conversion7.trace.pure

class PureJavaBeansTest extends GroovyTestCase {

    void 'test Integer++ throws null'(){
        def bean1 = new Bean1()
        shouldFail(NullPointerException, {bean1.f1++})
        bean1.f1 = 1
        assert bean1.f1 == 1
    }

    static class Bean1 {
        Integer f1
    }

}
