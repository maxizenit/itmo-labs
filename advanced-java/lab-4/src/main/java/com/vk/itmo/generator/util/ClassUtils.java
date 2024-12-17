package com.vk.itmo.generator.util;

import com.google.common.reflect.ClassPath;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

/** Утилиты для работы с классами. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassUtils {

  /** Загрузчик классов. */
  private static final ClassLoader CLASS_LOADER = ClassUtils.class.getClassLoader();

  /**
   * Загружает класс по его названию.
   *
   * @param className название класса
   * @return класс с заданным названием
   */
  @SneakyThrows
  public static Class<?> loadClass(@NonNull String className) {
    return CLASS_LOADER.loadClass(className);
  }

  /**
   * Возвращает все классы в указанном пакете и во вложенных пакетах.
   *
   * @param packageName название пакета
   * @return классы пакета и вложенных пакетов
   */
  @SneakyThrows
  public static Set<Class<?>> getClassesFromPackage(@NonNull String packageName) {
    return ClassPath.from(CLASS_LOADER).getTopLevelClassesRecursive(packageName).stream()
        .map(ClassPath.ClassInfo::load)
        .collect(Collectors.toSet());
  }
}
