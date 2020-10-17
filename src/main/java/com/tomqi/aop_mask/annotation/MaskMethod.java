package com.tomqi.aop_mask.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author TOMQI
 * @description 用于在AbstractDefaultDataMasking的继承类中对处理方法进行标注，被标注的方法与原本的执行方法一一对应
 * @date 2020/9/21 10:47
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MaskMethod {

    /**
     * 必须填入标注方法所对应原本的方法名称，以此为依据与原本的方法建立映射关系
     * 如果被@Masking标注的方法的alias属性值不为空，则填入指定的别名
     * @return
     */
    @AliasFor("methodName")
    String value() default "";

    /**
     * 与value二选一即可，两者互为别名
     * @return
     */
    @AliasFor("value")
    String methodName() default "";

    /**
     * 见下方枚举值,默认在后置处理节点
     * @return
     */
    TimeNode timing() default TimeNode.POST_HANDLE;

    /**
     * 定义方法的执行节点，提供4种节点，顺序是BEFORE_PRE_HANDLE --> preHandle前置处理 --> postHandle后置处理 --> AFTER_POST_HANDLE
     * 如果指定HANDLE 意味着你打算自定义执行操作。他的执行时点在preHandle和postHandle之间,需要注意的是只有这个节点可以拿到joinPoint对象。
     */
    enum TimeNode {
        //前置处理前
        BEFORE_PRE_HANDLE,
        //前置处理
        PRE_HANDLE,
        //process处理
        HANDLE,
        //后置处理
        POST_HANDLE,
        //后置处理后
        AFTER_POST_HANDLE
    }
}
