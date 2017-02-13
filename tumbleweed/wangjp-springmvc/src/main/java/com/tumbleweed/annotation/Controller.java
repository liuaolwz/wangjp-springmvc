package com.tumbleweed.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//控制层注解
@Target({java.lang.annotation.ElementType.TYPE})//作用在类上
@Retention(RetentionPolicy.RUNTIME)//在运行时有效
@Documented//可以打包成api
public @interface Controller {
    String value() default "";
}
