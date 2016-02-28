## Runtime handling of <b>field<b>-writing and method-invoking.

### Lib goal:
- logging/notifying about writing into properties/fields;
- logging of methods invocations;
- beans dumping into tree structures TODO.

#### Why use lib? - It handles direct field access (and even if field was not changed).
#### Why not use lib? - Renamed fields are not acceptable.

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
Maven dependency using jitpack (Choose version by "get it" button and add repository with dependency):
https://jitpack.io/#nanonull/bean-trace

### Depends on/Tested on:
- version 1.0:
    [groovy-all-2.4.5](http://mvnrepository.com/artifact/org.codehaus.groovy/groovy-all/2.4.5)

### Build
```
gradle clean install
```
PS: just ignore javadoc errors...

