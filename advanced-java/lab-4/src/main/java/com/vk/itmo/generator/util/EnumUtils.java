package com.vk.itmo.generator.util;

import java.util.Random;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** Утилиты для работы с {@link Enum}. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnumUtils {

  /**
   * Возвращает случайный элемент {@link Enum}, если {@link Enum} не пуст, либо {@code null}, если
   * пуст.
   *
   * @param enumClass {@link Enum}-класс
   * @param random рандомизатор
   * @return случайный элемент {@link Enum}, если {@link Enum} не пуст, либо {@code null}, если пуст
   */
  public static Object getRandomEnumValue(
      @NonNull Class<? extends Enum<?>> enumClass, @NonNull Random random) {
    if (enumClass.getEnumConstants().length == 0) {
      return null;
    }
    int randomIndex = random.nextInt(enumClass.getEnumConstants().length);
    return enumClass.getEnumConstants()[randomIndex];
  }
}
