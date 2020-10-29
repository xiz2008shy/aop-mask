package com.tomqi.aop_mask.annotation;

import java.lang.annotation.*;

/**
 * 360 Financial Copyright
 *
 * @author YanWenqi
 * @description
 * @date 2020/10/29 14:25
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MLog {

    LogMode logMode () default LogMode.GENERIC;
}
