package com.vk.itmo.generator.util;

import java.util.Random;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.RandomStringUtils;

/** Утилиты для работы со строками. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtils {

  /** Минимальная длина генерируемой строки. */
  private static final int MIN_RANDOM_STRING_LENGTH = 2;

  /** Максимальная длина генерируемой строки. */
  private static final int MAX_RANDOM_STRING_LENGTH = 5;

  /**
   * Генерирует строку случайной длины с случайным набором символов.
   *
   * @param random рандомизатор
   * @return случайная строка
   */
  public static @NonNull String generateRandomString(@NonNull Random random) {
    int length = random.nextInt(MIN_RANDOM_STRING_LENGTH, MAX_RANDOM_STRING_LENGTH + 1);
    return RandomStringUtils.insecure().next(length, true, true);
  }
}
