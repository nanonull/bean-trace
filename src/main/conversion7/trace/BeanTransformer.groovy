package conversion7.trace


import groovy.beans.BindableASTTransformation
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.messages.SimpleMessage
import org.codehaus.groovy.runtime.MetaClassHelper
import org.codehaus.groovy.transform.GroovyASTTransformation

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.lang.reflect.Modifier

import static org.codehaus.groovy.ast.tools.GeneralUtils.*

/**see transformBeanClass*/
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class BeanTransformer extends BindableASTTransformation {

    static String TARGET_CLASS
    static final StringBuilder DEBUG = new StringBuilder()
    static final String PROP_CHANGE_SUPPORT_NAME = "this\$propertyChangeSupport"


    static {
        init(BeanTransformer, TraceBean)
    }

    public static void init(Class boundClass, Class rootBeanClass) {
        boundClassNode = ClassHelper.make(boundClass)
        TARGET_CLASS = rootBeanClass.getName()
    }

    @Override
    public void visit(final ASTNode[] nodes, final SourceUnit source) {
        if (TARGET_CLASS == null) {
            throw new Exception("TARGET_CLASS is null!")
        }
        DEBUG.setLength(0)

        AnnotationNode annotationNode = (AnnotationNode) nodes[0];
        ClassNode classNode = (ClassNode) nodes[1];
        ClassNode declaringClass = classNode.getDeclaringClass();

        if (isNodeMatchesTargetClass(classNode)) {
            transformBeanClass(classNode, annotationNode, source, declaringClass)
        } else {
            def wrongClassMsg = new SimpleMessage("ERROR: $classNode is selected for transformation, but doesn't extend $TARGET_CLASS"
                    , source)
            throw new RuntimeException(wrongClassMsg.getMessage())
        }

        debug()
    }

    void debug() {
        //        classNode.addField("testField", ACC_PUBLIC
//                , ClassHelper.STRING_TYPE, new ConstantExpression("hey"))
//        throw new RuntimeException(DEBUG.toString())
    }


    void transformBeanClass(final ClassNode classNode, AnnotationNode node, SourceUnit source
                            , ClassNode declaringClass) {
        addPropertyListenerToClass(source, classNode)
        addStepInterceptors(source, classNode)
    }

    static void addStepInterceptors(final SourceUnit sourceUnit, final ClassNode classNode) {
        classNode.methods.each { method ->
            def methodName = method.name
            if (!methodName.startsWith("step")) {
                return
            }
            DEBUG.append(method.text).append("\n===\n")

            def methodExpression = (BlockStatement) method.code
            DEBUG.append(methodExpression.text).append("\n===\n")

            def newCode = new ExpressionStatement(
                    new MethodCallExpression(
                            new ConstantExpression("this")
                            , "println"
                            , new ArgumentListExpression([
                            new ConstantExpression(classNode.nameWithoutPackage + ': ' + methodName)
                    ])
                    )
            )
            methodExpression.getStatements().add(0, newCode)
        }

    }

    // TODO create this not in ast
    private void addPropertyListenerToClass(SourceUnit source, ClassNode classNode) {
//        addPropertyChangeSupport(classNode);

        def wrapped = new ArrayList<>()
        classNode.fields.each {
            if (shouldWrap(it)) {
                if (tryWrap(classNode, it, source)) {
                    wrapped.add(it)
                }
            }
        }

        // disable synth. generation
        def iterator = classNode.getProperties().iterator()
        while (iterator.hasNext()) {
            def next = iterator.next()
            if (wrapped.contains(next.field)) {
                // remove property, due to it was transformed to field
                iterator.remove()
            }
        }
    }

    @Override
    protected void addPropertyChangeSupport(ClassNode declaringClass) {
        ClassNode pcsClassNode = ClassHelper.make(PropertyChangeSupport.class);
        ClassNode pclClassNode = ClassHelper.make(PropertyChangeListener.class);
        //String pcsFieldName = "this$propertyChangeSupport";

        // add field:
        // protected final PropertyChangeSupport this$propertyChangeSupport = new java.beans.PropertyChangeSupport(this)
        FieldNode pcsField = declaringClass.addField(
                PROP_CHANGE_SUPPORT_NAME,
                ACC_FINAL | ACC_PRIVATE | ACC_SYNTHETIC,
                pcsClassNode,
                ctorX(pcsClassNode, args(varX("this"))));

        // add method:
        // void addPropertyChangeListener(listener) {
        //     this$propertyChangeSupport.addPropertyChangeListener(listener)
        //  }
        declaringClass.addMethod(
                new MethodNode(
                        "addPropertyChangeListener",
                        ACC_PUBLIC,
                        ClassHelper.VOID_TYPE,
                        params(param(pclClassNode, "listener")),
                        ClassNode.EMPTY_ARRAY,
                        stmt(callX(fieldX(pcsField), "addPropertyChangeListener", args(varX("listener", pclClassNode))))));

        // add method:
        // void addPropertyChangeListener(name, listener) {
        //     this$propertyChangeSupport.addPropertyChangeListener(name, listener)
        //  }
        declaringClass.addMethod(
                new MethodNode(
                        "addPropertyChangeListener",
                        ACC_PUBLIC,
                        ClassHelper.VOID_TYPE,
                        params(param(ClassHelper.STRING_TYPE, "name"), param(pclClassNode, "listener")),
                        ClassNode.EMPTY_ARRAY,
                        stmt(callX(fieldX(pcsField), "addPropertyChangeListener", args(varX("name", ClassHelper.STRING_TYPE), varX("listener", pclClassNode))))));

        // add method:
        // boolean removePropertyChangeListener(listener) {
        //    return this$propertyChangeSupport.removePropertyChangeListener(listener);
        // }
        declaringClass.addMethod(
                new MethodNode(
                        "removePropertyChangeListener",
                        ACC_PUBLIC,
                        ClassHelper.VOID_TYPE,
                        params(param(pclClassNode, "listener")),
                        ClassNode.EMPTY_ARRAY,
                        stmt(callX(fieldX(pcsField), "removePropertyChangeListener", args(varX("listener", pclClassNode))))));

        // add method: void removePropertyChangeListener(name, listener)
        declaringClass.addMethod(
                new MethodNode(
                        "removePropertyChangeListener",
                        ACC_PUBLIC,
                        ClassHelper.VOID_TYPE,
                        params(param(ClassHelper.STRING_TYPE, "name"), param(pclClassNode, "listener")),
                        ClassNode.EMPTY_ARRAY,
                        stmt(callX(fieldX(pcsField), "removePropertyChangeListener", args(varX("name", ClassHelper.STRING_TYPE), varX("listener", pclClassNode))))));

        // add method:
        // void firePropertyChange(String name, Object oldValue, Object newValue) {
        //     this$propertyChangeSupport.firePropertyChange(name, oldValue, newValue)
        //  }
        declaringClass.addMethod(
                new MethodNode(
                        "firePropertyChange",
                        ACC_PUBLIC,
                        ClassHelper.VOID_TYPE,
                        params(param(ClassHelper.STRING_TYPE, "name"), param(ClassHelper.OBJECT_TYPE, "oldValue"), param(ClassHelper.OBJECT_TYPE, "newValue")),
                        ClassNode.EMPTY_ARRAY,
                        stmt(callX(fieldX(pcsField), "firePropertyChange", args(varX("name", ClassHelper.STRING_TYPE), varX("oldValue"), varX("newValue"))))));

        // add method:
        // PropertyChangeListener[] getPropertyChangeListeners() {
        //   return this$propertyChangeSupport.getPropertyChangeListeners
        // }
        declaringClass.addMethod(
                new MethodNode(
                        "getPropertyChangeListeners",
                        ACC_PUBLIC,
                        pclClassNode.makeArray(),
                        Parameter.EMPTY_ARRAY,
                        ClassNode.EMPTY_ARRAY,
                        returnS(callX(fieldX(pcsField), "getPropertyChangeListeners"))));

        // add method:
        // PropertyChangeListener[] getPropertyChangeListeners(String name) {
        //   return this$propertyChangeSupport.getPropertyChangeListeners(name)
        // }
        declaringClass.addMethod(
                new MethodNode(
                        "getPropertyChangeListeners",
                        ACC_PUBLIC,
                        pclClassNode.makeArray(),
                        params(param(ClassHelper.STRING_TYPE, "name")),
                        ClassNode.EMPTY_ARRAY,
                        returnS(callX(fieldX(pcsField), "getPropertyChangeListeners", args(varX("name", ClassHelper.STRING_TYPE))))));
    }

    static boolean shouldWrap(final FieldNode field) {
        if (((field.getModifiers() & ACC_FINAL) != 0)
                || field.isStatic()
                || field.name.startsWith("this\$")
                || field.name.startsWith("_")) {
            return false
        }

        return true
    }

    private static boolean tryWrap(ClassNode classNode, FieldNode field, SourceUnit source) {

        def originalFieldName = field.getName()
        def fieldMethodsMod = field.modifiers
        if (classNode.hasProperty(originalFieldName)) {
            fieldMethodsMod = Modifier.PUBLIC
            DEBUG.append("Prop-field: " + originalFieldName).append("\n")
        }
        DEBUG.append(Modifier.toString(fieldMethodsMod)).append(" ").append(originalFieldName).append("\n")
        String setterName = "set" + MetaClassHelper.capitalize(originalFieldName);
        if (!classNode.getMethods(setterName).isEmpty()) {
            return false
        }
        String getterName = "get" + MetaClassHelper.capitalize(originalFieldName);
        if (!classNode.getMethods(getterName).isEmpty()) {
            return false
        }

        def newPropName = "_" + originalFieldName
        field.rename(newPropName)

        def fieldExpression = fieldX(field);
        Statement setterBlock =
                stmt(callThisX("firePropertyChange", args(constX(field.getName()), fieldExpression, assignX(fieldExpression, varX("value")))));
        MethodNode setter = new MethodNode(
                setterName,
                fieldMethodsMod,
                ClassHelper.VOID_TYPE,
                params(param(field.getType(), "value")),
                ClassNode.EMPTY_ARRAY,
                setterBlock);
        setter.setSynthetic(true);
        classNode.addMethod(setter);

        MethodNode getter = new MethodNode(
                getterName,
                fieldMethodsMod,
                field.getType(),
                Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,
                returnS(propX(new VariableExpression("this"), newPropName))
        )
        getter.setSynthetic(true);
        classNode.addMethod(getter);

        return true
    }

    static boolean isNodeMatchesTargetClass(ClassNode classNode) {

        if (classNode.name == TARGET_CLASS) {
            return true
        }

        while (true) {
            def superName = classNode.superClass.name
            if (superName == TARGET_CLASS) {
                return true
            }

            classNode = classNode.superClass
            if (superName == 'java.lang.Object') {
                return false
            }
        }
    }
}
