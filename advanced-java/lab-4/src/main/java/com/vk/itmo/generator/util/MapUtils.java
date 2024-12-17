package com.vk.itmo.generator.util;

import com.vk.itmo.generator.Generator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** Утилиты для работы со словарями. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapUtils {

  /** Минимальный размер генерируемого словаря. */
  private static final int MIN_MAP_SIZE = 1;

  /** Максимальный размер генерируемого словаря. */
  private static final int MAX_MAP_SIZE = 5;

  /**
   * Возвращает сгенерированный словарь.
   *
   * @param diamondGeneratorContent содержимое diamond-оператора
   * @param random рандомизатор
   * @param depthRemaining оставшаяся глубина генерации для Generatable-классов
   * @return сгенерированный словарь
   */
  public static @NonNull Map<Object, Object> generateMap(
      String diamondGeneratorContent, @NonNull Random random, int depthRemaining) {
    Class<?> keyClass;
    Class<?> valueClass;
    String keyDiamondOperatorContentPart;
    String valueDiamondOperatorContentPart;

    String keyDiamondOperatorContent = null;
    String valueDiamondOperatorContent = null;

    if (diamondGeneratorContent != null) {
      List<String> parts =
          DiamondOperatorUtils.getPartsFromDiamondOperatorContent(diamondGeneratorContent);
      if (parts.size() != 2) {
        throw new IllegalArgumentException("Content expects exactly two parts");
      }

      keyDiamondOperatorContentPart = parts.get(0);
      valueDiamondOperatorContentPart = parts.get(1);

      if (DiamondOperatorUtils.hasStringDiamondOperator(keyDiamondOperatorContentPart)) {
        keyClass =
            ClassUtils.loadClass(
                DiamondOperatorUtils.getContentBeforeDiamondOperator(
                    keyDiamondOperatorContentPart));
        keyDiamondOperatorContent =
            DiamondOperatorUtils.getContentInDiamondOperator(keyDiamondOperatorContentPart);
      } else {
        keyClass = ClassUtils.loadClass(keyDiamondOperatorContentPart);
      }

      if (DiamondOperatorUtils.hasStringDiamondOperator(valueDiamondOperatorContentPart)) {
        valueClass =
            ClassUtils.loadClass(
                DiamondOperatorUtils.getContentBeforeDiamondOperator(
                    valueDiamondOperatorContentPart));
        valueDiamondOperatorContent =
            DiamondOperatorUtils.getContentInDiamondOperator(valueDiamondOperatorContentPart);
      } else {
        valueClass = ClassUtils.loadClass(valueDiamondOperatorContentPart);
      }
    } else {
      return Collections.emptyMap();
    }

    int size = random.nextInt(MIN_MAP_SIZE, MAX_MAP_SIZE + 1);
    Map<Object, Object> map = new HashMap<>();
    for (int i = 0; i < size; i++) {
      Object key =
          Generator.generateValueOfType(
              keyClass, keyDiamondOperatorContent, random, depthRemaining);
      Object value =
          Generator.generateValueOfType(
              valueClass, valueDiamondOperatorContent, random, depthRemaining);

      if (key == null || value == null) {
        return Collections.emptyMap();
      }

      map.put(key, value);
    }

    return map;
  }
}
