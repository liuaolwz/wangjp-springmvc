package com.tumbleweed.annotation;

import java.lang.annotation.*;

//识别服务层属性
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Qualifier {
    String value() default "";
}
