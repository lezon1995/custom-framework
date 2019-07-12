package com.zl.spring.annotation;

import java.lang.annotation.*;

/**
 * 自定义RequestMapping注解
 *
 * @author zhuliang
 * @date 2019/7/12 14:49
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestMapping {
    String value() default "";
}
