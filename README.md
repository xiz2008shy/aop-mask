# AOPMask
A Spring AOP extension

AopMask主要作用是对目标方法进行快速增强，是基于springAop的扩展，颗粒度是方法级别的。
允许在方法执行前后，进行额外操作。
>The main functionality of AopMask is to quickly enhance the target method, which is based on the extension of springAop, and the granularity is method level.
Allows for additional operations before and after the method is executed.

开启AopMask，会将一个方法分割成5个顺序执行节点
BEFORE_PRE_HANDLE -> PRE_HANDLE -> HANDLE -> POST_HANDLE -> AFTER_POST_HANDLE

其中HANDLE节点中会执行 原目标方法。
AopMask使用时通常在BEFORE_PRE_HANDLE，PRE_HANDLE，POST_HANDLE，AFTER_POST_HANDLE四个节点中操作，而HANDLE节点通常不变动。
>Turn on AopMask, and a method will be divided into 5 sequential execution nodes
 BEFORE_PRE_HANDLE -> PRE_HANDLE -> HANDLE -> POST_HANDLE -> AFTER_POST_HANDLE <br>
 The original target method will be executed in the HANDLE node.
 In AopMask, it usually operates in four nodes: BEFORE_PRE_HANDLE, PRE_HANDLE, POST_HANDLE, AFTER_POST_HANDLE, and the HANDLE node is usually unchanged.


新增功能 new features
==========================================================

## 一、AopMask可扩展的前置效验_AopMask scalable pre-validation

通过继承AbstractMaskValidator类，重写valid方法，实现具体的效验逻辑。
支持效验模块的单独使用。
支持多效验器声明处理，可以指定效验器之间的执行顺序。
多个效验器之间为短路处理，即当前效验器如果验证失败将不再调用后续效验器（如果有）验证。
效验器可以拿到方法形参和对应的@Validator注解对象，具有一些常用的字段，方便扩展效验器的使用。

>By inheriting the AbstractMaskValidator class and overriding the valid method, you can implement your logic simply .
 Support the independent use of validation module.
 Support multi-validator declaration processing, you can specify the order of execution between validators.
 There is a short-circuit process between multiple validators, that is, if the current validator fails to verify, the subsequent validators (if any) will not be called for verification.
 The validator can get the method parameters and the corresponding @Validator annotated object. It has some commonly used fields to facilitate the use of the validator.


## 二、减少犯错，增加易用性_Reduce mistakes and increase ease of use
对JoinPoint进一步分装，原JoinPoint对象几乎不再对外暴露，提供了新封装的proceed API。
对方法形参进一步分装，不再直接暴露Object[]数组，转而由MethodArgs负责对外，提供相关处理方法，不再关心新参最后如何传递给方法。
>After further packaging JoinPoint, the original JoinPoint object is almost no longer exposed to the outside world, and a newly packaged proceed API is provided.
 The method parameters are further subpackaged, and the Object[] array is no longer directly exposed. Instead, MethodArgs is responsible for external processing and provides related processing methods, and no longer cares about how the new parameters are finally passed to the method.

==========================================================



## 二、使用说明_Instructions for use

`方法增强开启`<br>
1.使用@Masking注解，标注需要增强的方法，表明该方法需要被AopMask增强。<br>
2.增强方法 在AbstractDefaultDataMask类的子类中写入，子类需要标注@MaskOn注解，value属性必填（value中需要填入对应增强方法所在的类名）表明该类对应增强哪个类，类名不限。<br>
3.AbstractDefaultDataMask的子类中所写入的增强方法通过@MaskMethod注解与原方法进行对应。注解的methodName/value必填声明该增强方法所增强的原方法，timing属性用于声明该增强方法所执行的节点，默认为POST_HANDLE后置处理节点。<br>

>值得注意的是，只有方法指定为Handle节点时，才能在MaskMessage对象中拿到JoinPoint对象进行操作，避免在不恰当的节点执行原方法而引发问题，也是方便统一维护管理。

`Method to enhance opening`<br>
1. Use the @Masking annotation to mark the method that needs to be enhanced, indicating that the method needs to be enhanced by AopMask. <br>
2. The enhancement method is written in the subclass of the AbstractDefaultDataMask class, the subclass needs to be annotated with @MaskOn, and the value attribute is required (the value of the corresponding enhancement method needs to be filled in the class name) indicating which class and class name the class corresponds to Unlimited. <br>
3. The enhanced method written in the subclass of AbstractDefaultDataMask corresponds to the original method through the @MaskMethod annotation. The methodName/value of the annotation is required to declare the original method enhanced by the enhanced method. The timing attribute is used to declare the node executed by the enhanced method. The default is POST_HANDLE post-processing node. <br>
> It is worth noting that only when the method is specified as the Handle node, can the JoinPoint object be obtained in the MaskMessage object for operation, to avoid problems caused by executing the original method on an inappropriate node, and to facilitate unified maintenance and management.

`方法效验开启`<br>
1.@Masking注解的value属性中使用@Validator注解开启方法形参效验。<br>
2.所需效验的方法所在的类上添加@MValid注解。<br>
3.效验模块可以独立于方法增强使用，@Masking注解的onlyValid属性设置为true表示只开启效验，默认为false。<br>
4.@Validator通过validBy指定具体效验器，相关设置参数可以通过预设字段传入，多个@Validator之间的执行顺序可以通过order参数指定。<br>

`Method validation open`<br>
1. Use the @Validator annotation in the value attribute of the @Masking annotation to enable the method parameter validation. <br>
2. Add @MValid annotation to the class where the method to be validated is located. <br>
3. The validation module can be used independently of method enhancement. The onlyValid attribute of the @Masking annotation is set to true to indicate that only validation is enabled, and the default is false. <br>
4. @Validator specifies a specific validator through validBy, the relevant setting parameters can be passed in through preset fields, and the execution order between multiple @Validators can be specified through the order parameter. <br>

一个后置处理的例子_An example of post processing：
```java
@MValid
@Controller
public class WebController {
    ...

    @Masking({
        @Validator(validBy= NotAllEmpty.class,validParamIndex={2,3}),
        @Validator(validBy= RequestNotEmpty.class,keyWord="mobile",order=2)
    })
    public ResponseEntity<Entity> queryCustInfo(HttpServletRequest req,String custNo,String userNo){
        ...
    }

    ...
}
```

```java
@MaskOn("WebController")
public class WebMaskData extends AbstractDefaultDataMask {
    ...

    @MaskMethod("queryCustInfo")
    public void timingHandleDetail(MaskMessage message){
        ResponseEntity<Entity> result = message.getResult;
        ...
        message.setResult(updatedResult);
    }

    ...
}
```



## 三、补充说明
### 1.@Masking注解的注意事项

@Masking注解标记的方法不能是private修饰符修饰，不能是final修饰符修饰，方法所在的类中，其他方法应避免使用final修饰，偶见controller类中，因为存在final方法导致类中属性注入失败的情况。其与具体原因却是springAop本身的使用限制。<br>

### 2.Validation模块的注意事项

validation模块如果效验失败将以抛出异常的方式中断当前执行，但会被外层的异常处理器捕获，需要注意的是在定义validator时其实并不需要进行异常抛出操作，而如果存在在validator中抛出异常的需求，需要注意抛出的异常类型，当前异常处理器类为com.qihoo.finance.msf.web.exception.ExceptionAdvice,请注意其中能够捕捉的异常类型。<br>

### 3.方法形参细节

对于方法形参入参的修改，可以使用MaskMessage.setMethodArg(Object arg ,int index)方法，参数类型需要与原方法保持一致。最关键的在于修改形参后，对于原JoinPoint执行proceed方法时，需要考虑形参是否被真正修改，原生api中提供proceed()和proceed(Object[] args)正是出于这样的考虑设计的。在AopMask中对proceed的执行需要使用MaskMessage.proceed(),方法内部正是基于setMethodArgs的使用情况来判断是具体调用哪种原生api执行，减少调用者的关注点，不用担心明明修改了参数方法却没有使用新参数的尴尬。

以上面WebController的queryCustInfo方法为例，现在增加一个处理Handle节点的例子：
```java
public class WebMaskData extends AbstractDefaultDataMask {
   
    @MaskMethod(methodName = "queryCustInfo",timing = TimeNode.HANDLE)
    public void mainMethodHandle(MaskMessage message){
        String custNo = message.getMethodArg(2);
        if (custNo.equals("888")){
            new Three8Handler(message);
        }else {
            message.proceed();
        }
    }
  
}
```
