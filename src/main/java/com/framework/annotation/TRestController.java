package com.framework.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * 简化@RestController和@RequestMapping
 * 
 * @author nbbjack
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Validated
@RestController
@RequestMapping
public @interface TRestController {

    /**
     * 对元注解的值进行重写
     * 
     * @return
     */
    @AliasFor(annotation = RequestMapping.class)
    String name() default "";

    @AliasFor(annotation = RequestMapping.class)
    String[] value() default {};

    @AliasFor(annotation = RequestMapping.class)
    String[] path() default {};
}
