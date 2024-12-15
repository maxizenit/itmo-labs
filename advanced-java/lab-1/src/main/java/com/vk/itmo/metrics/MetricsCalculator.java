package com.vk.itmo.metrics;

import com.vk.itmo.classinfo.ClassInfo;
import com.vk.itmo.enm.ObjectMethods;

import java.util.*;

public class MetricsCalculator {

    public Metrics calculate(Map<String, ClassInfo> visitedClasses) {
        Map<String, Integer> classInheritanceDepth = createClassInheritanceDepthMap(visitedClasses);

        return Metrics.builder()
                .maxInheritanceDepth(calculateMaxClassInheritanceDepth(classInheritanceDepth))
                .averageInheritanceDepth(calculateAverageClassInheritanceDepth(classInheritanceDepth))
                .abcMetrics(calculateAbcMetrics(new HashSet<>(visitedClasses.values())))
                .averageOverrideMethodsCount(calculateAverageOverrideMethodsCount(visitedClasses))
                .averageClassFieldsCount(calculateAverageFieldsInClasses(visitedClasses))
                .build();
    }

    private Map<String, Integer> createClassInheritanceDepthMap(Map<String, ClassInfo> visitedClasses) {
        Map<String, Integer> inheritanceDepthMap = new HashMap<>();

        visitedClasses.values().forEach(vc -> {
            int depth = 1;
            String superClassName = vc.getSuperName();
            ClassInfo currentSuperClassInfo;

            while ((currentSuperClassInfo = visitedClasses.getOrDefault(superClassName, null)) != null) {
                depth++;
                superClassName = currentSuperClassInfo.getSuperName();
            }

            inheritanceDepthMap.put(vc.getName(), depth);
        });

        return inheritanceDepthMap;
    }

    private int calculateMaxClassInheritanceDepth(Map<String, Integer> classInheritanceDepthMap) {
        return classInheritanceDepthMap.values().stream().max(Integer::compareTo).orElse(0);
    }

    private double calculateAverageClassInheritanceDepth(Map<String, Integer> classInheritanceDepthMap) {
        return classInheritanceDepthMap.values().stream().mapToDouble(Integer::doubleValue).average().orElse(0);
    }

    private double calculateAbcMetrics(Set<ClassInfo> visitedClasses) {
        double assignments = visitedClasses.stream().mapToDouble(ClassInfo::getAssignmentsCount).sum();
        double branches = visitedClasses.stream().mapToDouble(ClassInfo::getBranchesCount).sum();
        double conditions = visitedClasses.stream().mapToDouble(ClassInfo::getConditionsCount).sum();

        return Math.sqrt(Math.pow(assignments, 2) + Math.pow(branches, 2) + Math.pow(conditions, 2));
    }

    private double calculateAverageOverrideMethodsCount(Map<String, ClassInfo> visitedClasses) {
        return visitedClasses.values().stream()
                .mapToDouble(
                        vc -> getOverrideMethodsCount(visitedClasses,
                                vc.getName(),
                                new HashSet<>(vc.getMethodsWithDescriptors()),
                                new HashSet<>())
                )
                .average().orElse(0);
    }

    private int getOverrideMethodsCount(Map<String, ClassInfo> visitedClasses,
                                        String currentClassName,
                                        Set<String> initialMethods,
                                        Set<String> excludedMethods) {
        if (initialMethods.isEmpty() || initialMethods.size() == excludedMethods.size()) {
            return excludedMethods.size();
        }

        ClassInfo currentClassInfo = visitedClasses.get(currentClassName);
        Iterator<String> iterator = initialMethods.iterator();

        while (iterator.hasNext()) {
            String method = iterator.next();
            if (currentClassInfo.getMethodsWithDescriptors().contains(method)
                    || ObjectMethods.getNonConstructorMethods().contains(method)) {
                iterator.remove();
                excludedMethods.add(method);
            }
        }

        if (!visitedClasses.containsKey(currentClassInfo.getSuperName()) || initialMethods.isEmpty()) {
            return excludedMethods.size();
        }
        return getOverrideMethodsCount(visitedClasses, currentClassInfo.getSuperName(), initialMethods, excludedMethods);
    }

    private double calculateAverageFieldsInClasses(Map<String, ClassInfo> visitedClasses) {
        return visitedClasses.values().stream().mapToDouble(ClassInfo::getFieldsCount).average().orElse(0);
    }
}
