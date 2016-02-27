package conversion7.trace.beans

import conversion7.trace.BeanTransformation

@BeanTransformation
class TestBean2Ext extends TestBean2InnerFieldWrite {
    int f11

    void run() {
        super.run()

        // super props test
        int expChanges = _changes + 1
        f1 = 100
        assert f1 == 100
        assert _changes == expChanges
        expChanges++

        setF1(110)
        assert f1 == 110
        assert _changes == expChanges
        expChanges++

        setProperty('f1', 120)
        assert f1 == 120
        assert _changes == expChanges
        expChanges++

        this.f1++
        assert f1 == 121
        assert _changes == expChanges
        expChanges++

        // my props test
        f11++
        assert f11 == 1
        assert _changes == expChanges
        expChanges++

        setF11(10)
        assert f11 == 10
        assert _changes == expChanges
        expChanges++

        setProperty('f11', 20)
        assert f11 == 20
        assert _changes == expChanges
        expChanges++

        this.f11++
        assert f11 == 21
        assert _changes == expChanges
        expChanges++
    }

}
