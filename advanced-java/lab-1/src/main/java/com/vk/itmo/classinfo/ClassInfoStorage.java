package com.vk.itmo.classinfo;

import com.vk.itmo.enm.ObjectMethods;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Getter
public class ClassInfoStorage {

    private final Map<String, ClassInfo> visitedClasses = new HashMap<>();

    public void addNewClass(String className, String superClassName) {
        ClassInfo classInfo = new ClassInfo();
        classInfo.setName(className);
        classInfo.setSuperName(superClassName);
        visitedClasses.put(className, classInfo);
    }

    public void addMethodWithDescriptor(String className, String methodName, String descriptor) {
        if (!visitedClasses.containsKey(className)) {
            throw new IllegalStateException("No such class. Add this class with method \"addNewClass\"");
        }

        String methodWithDescriptor = String.format("%s%s", methodName, descriptor);
        if (ObjectMethods.getConstructorMethods().contains(methodName)) {
            return;
        }

        ClassInfo classInfo = visitedClasses.get(className);
        if (classInfo.getMethodsWithDescriptors() == null) {
            classInfo.setMethodsWithDescriptors(new HashSet<>());
        }

        classInfo.getMethodsWithDescriptors().add(methodWithDescriptor);
    }

    public void incrementAssignmentsCountInClass(String className) {
        if (!visitedClasses.containsKey(className)) {
            throw new IllegalStateException("No such class. Add this class with method \"addNewClass\"");
        }

        ClassInfo classInfo = visitedClasses.get(className);
        classInfo.setAssignmentsCount(classInfo.getAssignmentsCount() + 1);
    }

    public void incrementBranchesCountInClass(String className) {
        if (!visitedClasses.containsKey(className)) {
            throw new IllegalStateException("No such class. Add this class with method \"addNewClass\"");
        }

        ClassInfo classInfo = visitedClasses.get(className);
        classInfo.setBranchesCount(classInfo.getBranchesCount() + 1);
    }

    public void incrementConditionsCountInClass(String className) {
        if (!visitedClasses.containsKey(className)) {
            throw new IllegalStateException("No such class. Add this class with method \"addNewClass\"");
        }

        ClassInfo classInfo = visitedClasses.get(className);
        classInfo.setConditionsCount(classInfo.getConditionsCount() + 1);
    }

    public void incrementFieldsCountInClass(String className) {
        if (!visitedClasses.containsKey(className)) {
            throw new IllegalStateException("No such class. Add this class with method \"addNewClass\"");
        }

        ClassInfo classInfo = visitedClasses.get(className);
        classInfo.setFieldsCount(classInfo.getFieldsCount() + 1);
    }
}
