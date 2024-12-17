package com.vk.itmo.generator.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Утилиты для работы с примитивами. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PrimitiveTypeUtils {

  /** Примитивные классы. */
  @RequiredArgsConstructor
  public enum PrimitiveType {
    BYTE(byte.class, Byte.class),
    SHORT(short.class, Short.class),
    INTEGER(int.class, Integer.class),
    LONG(long.class, Long.class),
    FLOAT(float.class, Float.class),
    DOUBLE(double.class, Double.class),
    BOOLEAN(boolean.class, Boolean.class),
    CHARACTER(char.class, Character.class);

    /** Класс примитива. */
    @NonNull private final Class<?> primitiveType;

    /** Класс обёртки для примитива. */
    @NonNull private final Class<?> wrapperType;
  }

  /** Генераторы случайных значений примитивных типов. */
  private static final Map<PrimitiveType, Function<Random, Object>> GENERATORS = new HashMap<>();

  static {
    GENERATORS.put(
        PrimitiveType.BYTE, random -> (byte) random.nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE + 1));
    GENERATORS.put(
        PrimitiveType.SHORT,
        random -> (short) random.nextInt(Short.MIN_VALUE, Short.MAX_VALUE + 1));
    GENERATORS.put(PrimitiveType.INTEGER, Random::nextInt);
    GENERATORS.put(PrimitiveType.LONG, Random::nextLong);
    GENERATORS.put(PrimitiveType.FLOAT, Random::nextFloat);
    GENERATORS.put(PrimitiveType.DOUBLE, Random::nextDouble);
    GENERATORS.put(PrimitiveType.BOOLEAN, Random::nextBoolean);
    GENERATORS.put(
        PrimitiveType.CHARACTER,
        random -> (char) random.nextInt(Character.MIN_VALUE, Character.MAX_VALUE + 1));
  }

  /**
   * Возвращает {@code true}, если переданный класс - класс примитива. Помимо стандартных
   * примитивных классов классом примитива может быть класс его обёртки или {@link Number}.
   *
   * @param clazz класс
   * @return {@code true}, если переданный класс - класс примитива
   */
  public static boolean isPrimitive(@NonNull Class<?> clazz) {
    return clazz.isPrimitive()
        || Number.class.equals(clazz)
        || Arrays.stream(PrimitiveType.values()).anyMatch(pt -> pt.wrapperType.equals(clazz));
  }

  /**
   * Генерирует случайное значение примитивного типа.
   *
   * @param primitiveType примитивный тип. Можно получить из {@link
   *     PrimitiveTypeUtils#getPrimitiveTypeByClass(Class)}
   * @param random рандомизатор
   * @return случайное значение примитивного типа
   */
  public static @NonNull Object generatePrimitiveTypeValue(
      @NonNull PrimitiveType primitiveType, @NonNull Random random) {
    return GENERATORS.get(primitiveType).apply(random);
  }

  /**
   * Возвращает объект {@link PrimitiveType} для заданного класса.
   *
   * @param clazz класс
   * @return объект {@link PrimitiveType} для заданного класса
   */
  @SuppressWarnings("all")
  public static @NonNull PrimitiveType getPrimitiveTypeByClass(@NonNull Class<?> clazz) {
    if (!isPrimitive(clazz)) {
      throw new IllegalArgumentException("No primitive type found for class " + clazz);
    }
    if (Number.class.equals(clazz)) {
      return PrimitiveType.INTEGER;
    }
    if (clazz.isPrimitive()) {
      return Arrays.stream(PrimitiveType.values())
          .filter(pt -> pt.primitiveType.equals(clazz))
          .findFirst()
          .get();
    }
    return Arrays.stream(PrimitiveType.values())
        .filter(pt -> pt.wrapperType.equals(clazz))
        .findFirst()
        .get();
  }
}
