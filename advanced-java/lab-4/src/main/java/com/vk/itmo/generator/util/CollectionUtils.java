package com.vk.itmo.generator.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.vk.itmo.generator.Generator;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** Утилиты для работы с коллекциями. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionUtils {

  /** Минимальный размер генерируемой коллекции. */
  private static final int MIN_COLLECTION_SIZE = 2;

  /** Максимальный размер генерируемой коллекции. */
  private static final int MAX_COLLECTION_SIZE = 5;

  /**
   * Возвращает сгенерированную коллекцию.
   *
   * @param collectionClass класс коллекции
   * @param diamondGeneratorContent содержимое diamond-оператора
   * @param random рандомизатор
   * @param depthRemaining оставшаяся глубина генерации для Generatable-классов
   * @return сгенерированная коллекция
   */
  public static @NonNull Collection<?> generateCollection(
      @NonNull Class<?> collectionClass,
      String diamondGeneratorContent,
      @NonNull Random random,
      int depthRemaining) {
    if (!Collection.class.isAssignableFrom(collectionClass)) {
      throw new IllegalArgumentException("The given class is not a collection class");
    }

    Class<?> elementClass;
    String diamondOperatorContent = null;

    if (diamondGeneratorContent != null) {
      if (DiamondOperatorUtils.hasStringDiamondOperator(diamondGeneratorContent)) {
        // diamondGeneratorContent содержит вложенные дженерики
        String param =
            DiamondOperatorUtils.getContentBeforeDiamondOperator(diamondGeneratorContent);
        elementClass = ClassUtils.loadClass(param);
        diamondOperatorContent =
            DiamondOperatorUtils.getContentInDiamondOperator(diamondGeneratorContent);
      } else {
        // diamondGeneratorContent - просто название класса
        elementClass = ClassUtils.loadClass(diamondGeneratorContent);
      }
    } else {
      return emptyCollection(collectionClass);
    }

    Collection<Object> collection;
    if (Set.class.isAssignableFrom(collectionClass)) {
      collection = Sets.newHashSet();
    } else if (Queue.class.isAssignableFrom(collectionClass)) {
      collection = Queues.newArrayDeque();
    } else {
      collection = Lists.newArrayList();
    }

    int size = random.nextInt(MIN_COLLECTION_SIZE, MAX_COLLECTION_SIZE + 1);
    for (int i = 0; i < size; ++i) {
      Object element =
          Generator.generateValueOfType(
              elementClass, diamondOperatorContent, random, depthRemaining);
      if (element == null) {
        return emptyCollection(collectionClass);
      }
      collection.add(element);
    }

    return collection;
  }

  /**
   * Возвращает пустую коллекцию заданного класса.
   *
   * @param collectionClass класс коллекции
   * @return пустая коллекция заданного класса
   */
  private static @NonNull Collection<?> emptyCollection(Class<?> collectionClass) {
    if (Set.class.isAssignableFrom(collectionClass)) {
      return Collections.emptySet();
    } else if (Queue.class.isAssignableFrom(collectionClass)) {
      return Queues.newArrayDeque();
    }
    return Collections.emptyList();
  }
}
