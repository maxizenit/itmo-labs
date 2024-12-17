package com.vk.itmo.generator.util;

import java.lang.annotation.Annotation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** Утилиты для работы с аннотациями. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnnotationUtils {

  /**
   * Возвращает {@code true}, если класс аннотирован заданой аннотацией.
   *
   * @param clazz класс
   * @param annotationClass класс аннотации
   * @return {@code true}, если класс аннотирован заданой аннотацией
   */
  public static boolean hasAnnotation(
      @NonNull Class<?> clazz, @NonNull Class<? extends Annotation> annotationClass) {
    return clazz.getAnnotation(annotationClass) != null;
  }
}
