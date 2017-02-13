package com.tumbleweed.annotation;

import java.lang.annotation.*;

//请求地址
@Target({ElementType.TYPE, ElementType.METHOD})//作用在方法上
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value() default "";
}
