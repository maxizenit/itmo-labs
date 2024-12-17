package com.vk.itmo.generator.util;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** Утилиты для работы с diamond-оператором. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DiamondOperatorUtils {

  private static final String STRING_DOES_NOT_CONTAIN_DIAMOND_OPERATOR_MESSAGE =
      "String does not contain diamond operator";

  /**
   * Возвращает {@code true}, если строка содержит diamond-оператор.
   *
   * @param string строка
   * @return {@code true}, если строка содержит diamond-оператор
   */
  public static boolean hasStringDiamondOperator(@NonNull String string) {
    int diamondStartIndex = string.indexOf("<");
    int diamondEndIndex = string.indexOf(">");
    // должны быть и открывающая, и закрывающая угловые скобки (причём открывающая должна идти перед
    // закрывающей, а не после)
    return diamondStartIndex != -1 && diamondEndIndex != -1 && diamondStartIndex < diamondEndIndex;
  }

  /**
   * Возвращает часть строки до diamond-оператора.
   *
   * @param string строка
   * @return часть строки до diamond-оператора
   */
  public static @NonNull String getContentBeforeDiamondOperator(@NonNull String string) {
    if (!hasStringDiamondOperator(string)) {
      throw new IllegalArgumentException(STRING_DOES_NOT_CONTAIN_DIAMOND_OPERATOR_MESSAGE);
    }
    return string.substring(0, string.indexOf("<"));
  }

  /**
   * Возвращает содержимое diamond-оператора.
   *
   * @param string строка
   * @return содержимое diamond-оператора
   */
  public static @NonNull String getContentInDiamondOperator(@NonNull String string) {
    if (!hasStringDiamondOperator(string)) {
      throw new IllegalArgumentException(STRING_DOES_NOT_CONTAIN_DIAMOND_OPERATOR_MESSAGE);
    }
    return string.substring(string.indexOf("<") + 1, string.lastIndexOf(">"));
  }

  /**
   * Возвращает содержимое diamond-оператора в виде списка его элементов. Элементы diamond-оператора
   * разделяются запятой (не считая запятых во вложенных diamond-операторах).
   *
   * @param diamondOperatorContent строка
   * @return содержимое diamond-оператора в виде списка его элементов
   */
  @SuppressWarnings("all")
  public static @NonNull List<String> getPartsFromDiamondOperatorContent(
      @NonNull String diamondOperatorContent) {
    List<Integer> borderIndexes = Lists.newArrayList(); // индексы запятых между элементами
    int unClosedAngleBracketsCounter = 0; // счётчик незакрытых открывающих угловых скобок

    for (int i = 0; i < diamondOperatorContent.length(); i++) {
      if (diamondOperatorContent.charAt(i) == '<') {
        ++unClosedAngleBracketsCounter;
      } else if (diamondOperatorContent.charAt(i) == '>') {
        --unClosedAngleBracketsCounter;
      } else if (diamondOperatorContent.charAt(i) == ',' && unClosedAngleBracketsCounter == 0) {
        // так как незакрытых угловых скобок нет, эта запятая не находится во вложенном
        // diamond-операторе
        borderIndexes.add(i);
      }
    }

    // если запятых нет, то внутри diamond-оператора только один элемент
    if (borderIndexes.isEmpty()) {
      return List.of(diamondOperatorContent);
    }

    List<String> contents = Lists.newArrayList();
    int currentStartIndex = 0;
    for (Integer borderIndex : borderIndexes) {
      contents.add(diamondOperatorContent.substring(currentStartIndex, borderIndex));
      currentStartIndex = borderIndex + 1;
    }
    contents.add(diamondOperatorContent.substring(currentStartIndex).trim());

    return contents;
  }
}
