### Lib goal:
- logging/catching of write access into properties and directly into fields;
- logging of methods execution;

#### Why this lib? - Because it handles direct field access.

##### How field access intercepted

In 2 words: <b>fields are renamed</b> and catched by java.bean.PropertyChangeSupport

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
- each access to field is replaced with access to get/set-Field by default groovy rules (because field1 is not found due to previous renaming)
- groovy-property field (default modifier) is transformed into private field with getter/setter.

Rules:
- fields with already defined getter/setter are skipped;
- final/static are skipped;
- fields with all access modifiers support transformation; 

### Usage: 
Maven dependency using jitpack (Choose version by "get it" button and add repository with dependency):
https://jitpack.io/#nanonull/bean-trace

### Depends on/Tested on:
- version 1.0:
    [groovy-2.4.5](http://mvnrepository.com/artifact/org.codehaus.groovy/groovy-all/2.4.5)

