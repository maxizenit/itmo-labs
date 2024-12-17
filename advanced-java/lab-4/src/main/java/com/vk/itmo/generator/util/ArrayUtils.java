package com.vk.itmo.generator.util;

import com.vk.itmo.generator.Generator;
import java.lang.reflect.Array;
import java.util.Random;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** Утилиты для работы с массивами. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArrayUtils {

  /** Минимальная длина генерируемого массива. */
  private static final int MIN_ARRAY_LENGTH = 2;

  /** Максимальная длина генерирумемого массива. */
  private static final int MAX_ARRAY_LENGTH = 5;

  /**
   * Возвращает сгенерированный массив.
   *
   * @param arrayClass класс массива. Содержит в себе информацию о классе компонентов, но без
   *     дженериков
   * @param diamondOperatorContent содержимое diamond-оператора
   * @param random рандомизатор
   * @param depthRemaining оставшаяся глубина генерации для Generatable-классов
   * @return сгенерированный массив
   */
  public static Object generateArray(
      @NonNull Class<? extends Array> arrayClass,
      String diamondOperatorContent,
      @NonNull Random random,
      int depthRemaining) {
    Class<?> elementClass = arrayClass.getComponentType();
    int length = random.nextInt(MIN_ARRAY_LENGTH, MAX_ARRAY_LENGTH + 1);
    Object array = Array.newInstance(elementClass, length);

    for (int i = 0; i < length; i++) {
      Object element =
          Generator.generateValueOfType(
              elementClass, diamondOperatorContent, random, depthRemaining);
      if (element == null) {
        return Array.newInstance(elementClass, 0);
      }
      Array.set(array, i, element);
    }
    return array;
  }
}
