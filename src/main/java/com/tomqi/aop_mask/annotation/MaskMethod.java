package com.tomqi.aop_mask.annotation;

import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.*;

/**
 * @author TOMQI
 * @description 用于在AbstractDefaultDataMasking的继承类中对处理方法进行标注，被标注的方法与原本的执行方法一一对应
 * @date 2020/9/21 10:47
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
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
     * 用于指定修饰同一方法，同一节点的先后执行顺序，数字小的优先执行，相同大小不能保证执行顺序。
     * @return
     */
    int order () default 0;
}
