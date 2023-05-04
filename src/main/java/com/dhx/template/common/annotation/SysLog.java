package com.dhx.template.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    /**
     * 执行的内容
     * @return
     */
    String value() default "";
}
