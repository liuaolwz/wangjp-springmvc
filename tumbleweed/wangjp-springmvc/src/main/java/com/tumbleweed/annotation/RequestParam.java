package com.tumbleweed.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})//作用在参数里
@Retention(RetentionPolicy.RUNTIME)//运行时
@Documented
public @interface RequestParam {
    String value() default "";
}
