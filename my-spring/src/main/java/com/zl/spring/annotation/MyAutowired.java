package com.zl.spring.annotation;

import java.lang.annotation.*;

/**
 * 自定义Service注解
 *
 * @author zhuliang
 * @date 2019/7/12 14:49
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAutowired {
}
