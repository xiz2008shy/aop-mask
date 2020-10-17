package com.tomqi.aop_mask.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author TOMQI
 * @description 允许mask的继承类使用其他命名（如不用默认命名规则，需要指定value为对应@Masking标注方法所在的类名名）
 * @date 2020/9/27 10:15
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Component
public @interface MaskOn {

    String value() default "";

}
