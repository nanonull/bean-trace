## Runtime handling of <b>field<b>-writing and method-invoking.
Transformed class handles executed methods and changed properties in runtime. It could help with debugging of complex execution flows, especially if no access to development-debug.

### Lib goal:
- logging/notifying about writing into properties/fields;
- logging of methods invocations;
- dumping of execution into graph view TODO;
- transformation takes no extra client code (TODO instrumentation); 

#### When to use: 
- you need log of execution path of your code;
- you need handle direct write-field access (and even if field was not changed; TODO configurable).

#### When don't use:
- renamed fields are not acceptable;
- you need dump whole object at given execution points (TODO)

##### How field access intercepted

In 2 words: <b>fields are renamed</b> and wrapped into accessors which are handled by <b>java.bean.PropertyChangeSupport</b>

For example you have bean:
```
@BeanTransform
class Bean extends TraceBean {
   int field1
   
   void method1() {
        field1 = 1
   }
}
```
Groovyc at compile time:
- renames field1 into _field1;
- creates setField1/getField1 methods with access to renamed _field (this._field1);
- each access to field1 is resolved as access to getField1/setField1 methods (by default groovy rule: try to find accessor method if there is no field)

Rules:
- fields with already defined getter/setter are skipped;
- final/static are skipped;
- groovy-property field (default modifier) is transformed into private field with getter/setter (before groovy generates accessors itself);
- fields with non-default access modifiers (private, protected, public) will have accessors with the same modifier;
- notifications are injected into each accessor method.

### Usage: 
Maven dependency using jitpack:
https://jitpack.io/#nanonull/bean-trace
(click Look-up > choose version and add repository with lib dependency to your build script)

### Depends on/Tested on:
- version 1.0:
    [groovy-all-2.4.5](http://mvnrepository.com/artifact/org.codehaus.groovy/groovy-all/2.4.5)

### Build
```
gradle clean install
```
PS: just ignore javadoc errors...

