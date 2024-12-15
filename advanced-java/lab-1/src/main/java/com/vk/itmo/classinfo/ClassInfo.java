package com.vk.itmo.classinfo;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ClassInfo {

    private String name;
    private String superName;
    private Integer assignmentsCount = 0;
    private Integer branchesCount = 0;
    private Integer conditionsCount = 0;
    private Set<String> methodsWithDescriptors = new HashSet<>();
    private Integer fieldsCount = 0;
}
