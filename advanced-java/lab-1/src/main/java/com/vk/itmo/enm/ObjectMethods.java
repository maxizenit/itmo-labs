package com.vk.itmo.enm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum ObjectMethods {

    EQUALS("equals(Ljava/lang/Object;)Ljava/lang/Object;", false),
    HASHCODE("hashCode()I", false),
    TO_STRING("toString()Ljava/lang/String;", false),
    CLONE("clone()Ljava/lang/Object;", false),
    INIT("<init>()V", true),
    CLINIT("<clinit>()V", true);

    private final String nameWithDescriptor;
    private final boolean constructorMethod;

    public static Set<String> getConstructorMethods() {
        return Arrays.stream(values())
                .filter(ObjectMethods::isConstructorMethod)
                .map(ObjectMethods::getNameWithDescriptor)
                .collect(Collectors.toSet());
    }

    public static Set<String> getNonConstructorMethods() {
        return Arrays.stream(values())
                .filter(v -> !v.isConstructorMethod())
                .map(ObjectMethods::getNameWithDescriptor)
                .collect(Collectors.toSet());
    }
}
