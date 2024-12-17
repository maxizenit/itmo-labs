package com.vk.itmo.generator;

import com.vk.itmo.generator.util.AnnotationUtils;
import com.vk.itmo.generator.util.ArrayUtils;
import com.vk.itmo.generator.util.CollectionUtils;
import com.vk.itmo.generator.util.DiamondOperatorUtils;
import com.vk.itmo.generator.util.EnumUtils;
import com.vk.itmo.generator.util.MapUtils;
import com.vk.itmo.generator.util.PrimitiveTypeUtils;
import com.vk.itmo.generator.util.ProxyUtils;
import com.vk.itmo.generator.util.StringUtils;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

/** Генератор экземпляров классов. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Generator {

  /**
   * Возвращает сгенерированный объект заданного класса.
   *
   * @param clazz класс генерируемого объекта
   * @param random рандомизатор
   * @param depthRemaining оставшаяся глубина генерации. Необходима для Generatable-объектов
   * @return сгенерированный объект заданного класса
   * @param <T> тип генерируемого объекта
   */
  public static <T> T generateValueOfType(
      @NonNull Class<T> clazz, @NonNull Random random, int depthRemaining) {
    return generateValueOfType(clazz, null, random, depthRemaining);
  }

  /**
   * Возвращает сгенерированный объект заданного класса.
   *
   * @param clazz класс генерируемого объекта
   * @param diamondOperatorContent содержимое diamond-оператора
   * @param random рандомизатор
   * @param depthRemaining оставшаяся глубина генерации. Необходима для Generatable-объектов
   * @return сгенерированный объект заданного класса
   * @param <T> тип генерируемого объекта
   */
  @SuppressWarnings("unchecked")
  public static <T> T generateValueOfType(
      @NonNull Class<T> clazz,
      String diamondOperatorContent,
      @NonNull Random random,
      int depthRemaining) {
    // Примитивный тип
    if (PrimitiveTypeUtils.isPrimitive(clazz)) {
      PrimitiveTypeUtils.PrimitiveType primitiveType =
          PrimitiveTypeUtils.getPrimitiveTypeByClass(clazz);
      return (T) PrimitiveTypeUtils.generatePrimitiveTypeValue(primitiveType, random);
    }

    // Enum
    if (clazz.isEnum()) {
      return (T) EnumUtils.getRandomEnumValue((Class<? extends Enum<?>>) clazz, random);
    }

    // String
    if (clazz.equals(String.class)) {
      return (T) StringUtils.generateRandomString(random);
    }

    // Массив
    if (clazz.isArray()) {
      return (T)
          ArrayUtils.generateArray(
              (Class<? extends Array>) clazz, diamondOperatorContent, random, depthRemaining);
    }

    // Коллекция
    if (Collection.class.isAssignableFrom(clazz)) {
      return (T)
          CollectionUtils.generateCollection(clazz, diamondOperatorContent, random, depthRemaining);
    }

    // Словарь
    if (Map.class.isAssignableFrom(clazz)) {
      return (T) MapUtils.generateMap(diamondOperatorContent, random, depthRemaining);
    }

    // Класс/интерфейс, не помеченный аннотацией Generatable
    if (!AnnotationUtils.hasAnnotation(clazz, Generatable.class)) {
      return null;
    }

    // Максимальная глубина генерации достигнута
    if (depthRemaining == 0) {
      return null;
    }

    // Интерфейс, помеченный аннотацией Generatable
    if (clazz.isInterface()) {
      return (T) ProxyUtils.generateProxy(clazz, random);
    }

    // Класс, помеченный аннотацией Generatable
    Constructor<?> randomConstructor = getRandomConstructor(clazz, random);
    return (T) callConstructor(randomConstructor, random, depthRemaining);
  }

  /**
   * Возвращает случайный конструктор для заданного класса.
   *
   * @param clazz класс
   * @param random рандомизатор
   * @return случайный конструктор класса
   */
  private static @NonNull Constructor<?> getRandomConstructor(
      @NonNull Class<?> clazz, @NonNull Random random) {
    Constructor<?>[] constructors = clazz.getDeclaredConstructors();
    int randomConstructorIndex = random.nextInt(clazz.getDeclaredConstructors().length);
    return constructors[randomConstructorIndex];
  }

  /**
   * Вызывает заданный конструктор и возвращает созданный при его помощи объект.
   *
   * @param constructor конструктор
   * @param random рандомизатор
   * @param depthRemaining оставшаяся глубина генерации
   * @return объект, созданный конструктором
   */
  @SneakyThrows
  private static Object callConstructor(
      @NonNull Constructor<?> constructor, @NonNull Random random, int depthRemaining) {
    constructor.setAccessible(true);
    Class<?>[] parameterTypes = constructor.getParameterTypes();
    Type[] genericParameterTypes = constructor.getGenericParameterTypes();
    Object[] parameters = new Object[parameterTypes.length];

    for (int i = 0; i < parameterTypes.length; i++) {
      String diamondOperatorContent = null;
      String genericParameterTypeName = genericParameterTypes[i].getTypeName();

      if (DiamondOperatorUtils.hasStringDiamondOperator(genericParameterTypeName)) {
        diamondOperatorContent =
            DiamondOperatorUtils.getContentInDiamondOperator(genericParameterTypeName);
      }
      parameters[i] =
          generateValueOfType(
              parameterTypes[i], diamondOperatorContent, random, depthRemaining - 1);
    }

    return constructor.newInstance(parameters);
  }
}
