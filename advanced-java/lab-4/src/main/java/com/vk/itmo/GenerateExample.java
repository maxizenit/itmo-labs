package com.vk.itmo;

import com.vk.itmo.generator.Generator;
import com.vk.itmo.generator.util.ClassUtils;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

public class GenerateExample {

  /** Стандартный пакет для Generatable-классов. */
  private static final String GENERATABLE_CLASSES_PACKAGE = "com.vk.itmo.classes";

  /** Стандартная глубина генерации. */
  private static final int DEFAULT_GENERATION_DEPTH = 2;

  public static void main(String[] args) {
    int generationDepth;

    try {
      generationDepth = Integer.parseInt(args[0]);
    } catch (NumberFormatException | IndexOutOfBoundsException e) {
      generationDepth = DEFAULT_GENERATION_DEPTH;
    }

    Random random = new Random(System.currentTimeMillis());
    Set<Class<?>> classes = ClassUtils.getClassesFromPackage(GENERATABLE_CLASSES_PACKAGE);
    int finalGenerationDepth = generationDepth;

    Stream<Object> objects =
        classes.stream()
            .map(clazz -> Generator.generateValueOfType(clazz, random, finalGenerationDepth));
    objects.forEach(System.out::println);
  }
}
