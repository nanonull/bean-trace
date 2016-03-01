package conversion7.trace.plain.test_beans

import conversion7.trace.BeanTransformation

@BeanTransformation
class Bean21WithStaticMethod extends BaseTestBean {

    @Override
    void run() {
        assert intToStringFunc(1) == "1"
    }

    static String intToStringFunc(int num) {
        return num.toString()
    }
}
