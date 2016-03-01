package conversion7.trace.plain

import conversion7.trace.BeanTransformation
import conversion7.trace.plain.test_beans.*
import org.codehaus.groovy.runtime.MetaClassHelper

import java.lang.reflect.Modifier

class TraceBeanTransformTest extends GroovyTestCase {


    void testClassRel() {
        assert TraceBean.isAssignableFrom(TestBean1)
    }

    void testPropertyChangeListenersAreCreatedAtCompile() {
        def obj1 = BaseTestBean.beanFactory.create(TestBean1)
        println obj1
        obj1.f1++
        obj1.f2++
        obj1.f3++
        assert obj1._changes == 3
    }

    void testPropertyGetter() {
        def obj1 = BaseTestBean.beanFactory.create(TestBean1)
        assert obj1.f1 == 0
        assert obj1.getF1() == 0
        assert obj1.f3 == 0
        assert obj1.getF3() == 0
        assert obj1.getThisF3() == 0
    }

    void testPropertySetter() {
        def obj1 = BaseTestBean.beanFactory.create(TestBean1)
        obj1.setF1(1)
        assert obj1.f1 == 1
        obj1.setThisF3(2)
        assert obj1.f3 == 2
    }


    void 'test static super field access'() {
        assert BaseTestBean.beanFactory.create(Bean1).FF_BEAN1_FINAL == 100
        assert BaseTestBean.beanFactory.create(Bean2).FF_BEAN1_FINAL == 100
    }

    @BeanTransformation
    static class Bean1 extends BaseTestBean {
        static int NO_PROBLEM_NAME = 200

        static int F1_BEAN1_FINAL = 100
        static int FF_BEAN1_FINAL = 100
        static int FF1_BEAN1_FINAL = 100

        @Override
        void run() {
            assert F1_BEAN1_FINAL == 100
//            assert FF_BEAN1_FINAL == 100
        }
    }

    @BeanTransformation
    static class Bean2 extends Bean1 {

        @Override
        void run() {
            assert NO_PROBLEM_NAME == 200
            assert FF_BEAN1_FINAL == 100
        }
    }

    void 'test property return type'() {
        def bean1 = BaseTestBean.beanFactory.create(TestBean1)
        def f1 = bean1.f1
        assert f1.getClass() == Integer
        assert bean1.getF1().class == Integer
        assert f1 * 10 == f1.toInteger() * 10
    }

    void 'test do not transform prop if custom getter found'() {
        def b = BaseTestBean.beanFactory.create(Bean3)
        assert b.f1 == Bean3.CONST_F1
        assert b.getF1() == Bean3.CONST_F1
        assert b.@f1 == 1
    }

    @BeanTransformation
    static class Bean3 extends BaseTestBean {
        int f1 = 1
        static int CONST_F1 = 10

        int getF1() {
            return CONST_F1
        }

        @Override
        void run() {

        }
    }

    void 'test do not transform prop if custom setter found'() {

        def b = BaseTestBean.beanFactory.create(TestBean4)
        b.f1 = 2
        assert b.f1 == TestBean4.CONST_F1

        b.@f1 = -1
        b.setF1(3)
        assert b.getF1() == TestBean4.CONST_F1
    }


    void 'test transformed fields'() {
        def b = BaseTestBean.beanFactory.create(TestBeanWithAllFieldMods)

        assert !findField(TestBeanWithAllFieldMods, "f1")
        assert !findField(TestBeanWithAllFieldMods, "f2")
        assert !findField(TestBeanWithAllFieldMods, "f3")
        assert !findField(TestBeanWithAllFieldMods, "f5")

        assert findField(TestBeanWithAllFieldMods, "f1_").modifiers == Modifier.PUBLIC
        assert findField(TestBeanWithAllFieldMods, "f2_").modifiers == Modifier.PROTECTED
        assert findField(TestBeanWithAllFieldMods, "f3_").modifiers == Modifier.PRIVATE
        assert findField(TestBeanWithAllFieldMods, "f5_").modifiers == Modifier.PRIVATE

        // read fields
        assert b.@f1_ == 0
        assert b.@f2_ == 0
        assert b.@f5_ == 0
//        assert b.f3 == 0 // Won't fail in groovy

    }

    void 'test transformation created getters/setters'() {
        assert !findMethod(TestBeanWithAllFieldMods, null, "getf1_")
        assert !findMethod(TestBeanWithAllFieldMods, null, "getF1_")
        assert !findMethod(TestBeanWithAllFieldMods, null, "getf2_")
        assert !findMethod(TestBeanWithAllFieldMods, null, "getF2_")
        assert !findMethod(TestBeanWithAllFieldMods, null, "getf3_")
        assert !findMethod(TestBeanWithAllFieldMods, null, "getF3_")
        assert !findMethod(TestBeanWithAllFieldMods, null, "getf5_")
        assert !findMethod(TestBeanWithAllFieldMods, null, "getF5_")

        assert findMethod(TestBeanWithAllFieldMods, null, "getF1")
        assert findMethod(TestBeanWithAllFieldMods, null, "getF1").modifiers == Modifier.PUBLIC
        assert findMethod(TestBeanWithAllFieldMods, null, "getF2")
        assert findMethod(TestBeanWithAllFieldMods, null, "getF2").modifiers == Modifier.PROTECTED
        assert findMethod(TestBeanWithAllFieldMods, null, "getF3")
        assert findMethod(TestBeanWithAllFieldMods, null, "getF3").modifiers == Modifier.PRIVATE
        assert findMethod(TestBeanWithAllFieldMods, null, "getF5")
        assert Modifier.toString(findMethod(TestBeanWithAllFieldMods, null, "getF5").modifiers) == "public"

        assert !findMethod(TestBeanWithAllFieldMods, null, "setf1_")
        assert !findMethod(TestBeanWithAllFieldMods, null, "setF1_")
        assert !findMethod(TestBeanWithAllFieldMods, null, "setf2_")
        assert !findMethod(TestBeanWithAllFieldMods, null, "setF2_")
        assert !findMethod(TestBeanWithAllFieldMods, null, "setf3_")
        assert !findMethod(TestBeanWithAllFieldMods, null, "setF3_")
        assert !findMethod(TestBeanWithAllFieldMods, null, "setf5_")
        assert !findMethod(TestBeanWithAllFieldMods, null, "setF5_")

        assert findMethod(TestBeanWithAllFieldMods, null, "setF1")
        assert findMethod(TestBeanWithAllFieldMods, null, "setF1").modifiers == Modifier.PUBLIC
        assert findMethod(TestBeanWithAllFieldMods, null, "setF2")
        assert findMethod(TestBeanWithAllFieldMods, null, "setF2").modifiers == Modifier.PROTECTED
        assert findMethod(TestBeanWithAllFieldMods, null, "setF3")
        assert findMethod(TestBeanWithAllFieldMods, null, "setF3").modifiers == Modifier.PRIVATE
        assert findMethod(TestBeanWithAllFieldMods, null, "setF5")
        assert findMethod(TestBeanWithAllFieldMods, null, "setF5").modifiers == Modifier.PUBLIC
    }

    static def findField(Class aClass, String name) {
        return aClass.declaredFields.find { it.name == name }
    }

    static def findMethod(Class aClass, String prefix, String name) {
        def fullName
        if (prefix) {
            fullName = prefix + MetaClassHelper.capitalize(name)
        } else {
            fullName = name
        }
        return aClass.declaredMethods.find { it.name == fullName }
    }

    // do not assert expected parameters with null
    // assert property exists only once
    static void testPropertyExists(String name, Object object
                                   , Boolean inFields, Boolean inProps, Boolean inMetaProps) {
        boolean hasProperty

        if (inFields != null) {
            hasProperty = false
            object.class.fields.each {
                if (it.name == name) {
                    if (hasProperty) {
                        fail("Property found twice: " + name)
                    }
                    hasProperty = true
                }
            }
            object.class.declaredFields.each {
                if (it.name == name) {
                    if (hasProperty) {
                        fail("Property found twice: " + name)
                    }
                    hasProperty = true
                }
            }
            assert hasProperty == inFields
        }

        if (inProps != null) {
            hasProperty = false
            object.properties.each {
                if (it.key.toString() == name) {
                    if (hasProperty) {
                        fail("Property found twice: " + name)
                    }
                    hasProperty = true
                }
            }
            assert hasProperty == inProps
        }

        if (inMetaProps != null) {
            hasProperty = false
            object.metaClass.properties.each {
                if (it.name == name) {
                    if (hasProperty) {
                        fail("Property found twice: " + name)
                    }
                    hasProperty = true
                }
            }
            assert hasProperty == inMetaProps
        }
    }
}