package com.vk.itmo.projecttracker.annotation;

import com.vk.itmo.projecttracker.model.enm.ProjectRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckProjectRoles {
    ProjectRole[] value() default {};
}