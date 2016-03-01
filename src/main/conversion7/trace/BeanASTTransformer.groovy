package conversion7.trace

import conversion7.trace.plain.TraceBean
import groovy.beans.BindableASTTransformation
import org.apache.commons.lang3.StringUtils
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
public class BeanASTTransformer extends BindableASTTransformation {

    static ClassNode TARGET_CLASS_NODE
    static final StringBuilder DEBUG = new StringBuilder()

    // TODO replace by checking presence of original method in trait (it means system member if present in trait)
    // TODO or add @SkipHandling annotation
    /**Hide these methods from handling.*/
    static List<String> SYSTEM_METHODS = [
            "\$getStaticMetaClass"
            , "initialization"
            , "methodInvoked"
            , "propertyChange"
            , "handleInputSysProp"
            , "handleInputProps"
            , "handleInputProps"
            , "println"
    ]
    static String RENAMED_PROPERTY_PREFIX = "_"
    static Class TARGET_ROOT_CLASS

    static {
        init(TraceBean)
    }

    public static void init(Class rootBeanClass) {
        TARGET_ROOT_CLASS = rootBeanClass
        TARGET_CLASS_NODE = ClassHelper.make(rootBeanClass)
    }

    @Override
    public void visit(final ASTNode[] nodes, final SourceUnit source) {
        if (TARGET_CLASS_NODE == null) {
            throw new Exception("TARGET_CLASS_NODE is null!")
        }
        DEBUG.setLength(0)

        AnnotationNode annotationNode = (AnnotationNode) nodes[0];
        ClassNode classNode = (ClassNode) nodes[1];
        ClassNode declaringClass = classNode.getDeclaringClass();

        DEBUG.append("TRANSFORM: ").append(classNode).append("\n")
        if (isNodeImplementsInterface(classNode)) {
            transformBeanClass(classNode, annotationNode, source, declaringClass)
        } else {
            def wrongClassMsg = new SimpleMessage("ERROR: $classNode is selected for transformation, but doesn't implement $TARGET_CLASS_NODE"
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
            if (!(method.code instanceof BlockStatement)
                    || Modifier.isStatic(method.modifiers)
                    || method.name in SYSTEM_METHODS
            ) {
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

        def newPropName = StringUtils.uncapitalize(originalFieldName) + RENAMED_PROPERTY_PREFIX
        field.rename(newPropName)

        def fieldExpression = fieldX(field);
        Statement setterBlock =
                stmt(callThisX("firePropertyChange", args(constX(originalFieldName)
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

    static boolean isNodeImplementsInterface(ClassNode classNode) {
        DEBUG.append("=== isNodeImplementsInterface start for: ").append(classNode).append("\n")

        while (classNode != null) {
            DEBUG.append("== current classNode: ").append(classNode).append("\n")
            if (classNode.interfaces.find {
                DEBUG.append(classNode).append(" implements ").append(it).append("\n")
                if (it == TARGET_CLASS_NODE) {
                    return true
                }

                return doesImplement(it, TARGET_CLASS_NODE)

            }) {
                return true
            }

            classNode = classNode.superClass
        }
        return false
    }

    static boolean doesImplement(ClassNode child, ClassNode zuper) {
        DEBUG.append(child).append(" has interfaces ").append(child.interfaces).append("\n")
        if (child.interfaces.find {
            DEBUG.append(child).append(" implements? ").append(it).append("\n")
            if (it == zuper) {
                return true
            }

            doesImplement(it, zuper)

        }) {
            return true
        }

        return false

    }
}
