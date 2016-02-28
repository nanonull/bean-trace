package conversion7.trace

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@org.codehaus.groovy.transform.GroovyASTTransformationClass("conversion7.trace.BeanASTTransformer")
public @interface BeanTransformation {
}
