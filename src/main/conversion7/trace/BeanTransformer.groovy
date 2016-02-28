package conversion7.trace

import groovy.beans.BindableASTTransformation
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.messages.SimpleMessage
import org.codehaus.groovy.runtime.MetaClassHelper
import org.codehaus.groovy.transform.GroovyASTTransformation

import java.lang.reflect.Modifier

import static org.codehaus.groovy.ast.tools.GeneralUtils.*

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class BeanTransformer extends BindableASTTransformation {

    static ClassNode TARGET_CLASS
    static final StringBuilder DEBUG = new StringBuilder()
    static List<String> SYSTEM_METHODS = ["\$getStaticMetaClass"]

    static {
        init(TraceBean)
    }

    public static void init(Class rootBeanClass) {
        TARGET_CLASS = ClassHelper.make(rootBeanClass)
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

        DEBUG.append("TRANSFORM: ").append(classNode).append("\n")
        if (isNodeMatchesTargetClass(classNode)) {
            transformBeanClass(classNode, annotationNode, source, declaringClass)
        } else {
            def wrongClassMsg = new SimpleMessage("ERROR: $classNode is selected for transformation, but doesn't implement $TARGET_CLASS"
                    , source)
            throwError(new RuntimeException(wrongClassMsg.getMessage()))
        }
    }

    static void throwError(Throwable e) {
        throw new RuntimeException("DEBUG:\n" + DEBUG.toString() + "\nERROR:\n" + e.getMessage(), e);
    }

    static void transformBeanClass(final ClassNode classNode, AnnotationNode node, SourceUnit source
                                   , ClassNode declaringClass) {
        wrapFieldsAndPropertiesForListening(source, classNode)
        addMethodInterceptors(source, classNode)
    }

    static void throwDebugError() {
        throwError(new Exception("DEBUGGING..."))
    }

    static void addMethodInterceptors(final SourceUnit sourceUnit, final ClassNode classNode) {
        classNode.methods.each { method ->
            DEBUG.append("\n=====\n")
            if (!(method.code instanceof BlockStatement) || method.name in SYSTEM_METHODS) {
                DEBUG.append("SKIP METHOD: " + method.text).append("\n===\n")
                return
            }
            DEBUG.append("HANDLING METHOD: " + method.text).append("\n")

            def methodExpression
            try {
                methodExpression = (BlockStatement) method.code
            } catch (Throwable t) {
                DEBUG.append("ERROR ON: " + method.text).append("\n===\n")
                return
            }
            DEBUG.append(methodExpression.text).append("\n===\n")

            def addCode = new ExpressionStatement(callThisX("methodInvoked"
                    , args(constX(classNode.nameWithoutPackage), constX(method.name))))
            DEBUG.append("addCode: " + addCode.text).append("\n")

            methodExpression.getStatements().add(0, addCode)
        }

    }

    private static void wrapFieldsAndPropertiesForListening(SourceUnit source, ClassNode classNode) {
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

    static boolean shouldWrap(final FieldNode field) {
        if (((field.getModifiers() & ACC_FINAL) != 0)
                || field.isStatic()
                || field.name.startsWith("this\$")
                || field.name == "metaClass"
                || field.name.startsWith("_")) {
            return false
        }

        DEBUG.append(field.name).append("\n")
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
                stmt(callThisX("firePropertyChange", args(constX(field.getName())
                        , fieldExpression, assignX(fieldExpression, varX("value")))));
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
        while (classNode != null) {
            if (classNode.interfaces.find {
                DEBUG.append(it).append("\n")
                return it == TARGET_CLASS
            }) {
                return true
            }

            classNode = classNode.superClass
        }
        return false
    }
}
