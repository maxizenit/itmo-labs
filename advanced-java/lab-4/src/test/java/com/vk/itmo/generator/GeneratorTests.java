package com.vk.itmo.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.vk.itmo.generator.util.ArrayUtils;
import com.vk.itmo.generator.util.EnumUtils;
import com.vk.itmo.generator.util.PrimitiveTypeUtils;
import com.vk.itmo.generator.util.StringUtils;
import java.util.List;
import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class GeneratorTests {

  private enum TestEnum {
    FIRST
  }

  @Generatable
  private interface GeneratableTestInterface {}

  @Generatable
  @Getter
  @AllArgsConstructor
  private static class GeneratableTestClass {
    private int intField;
  }

  @Test
  @SuppressWarnings("all")
  public void generateValueOfTypeTest() {
    Random mockRandom = Mockito.mock(Random.class);

    try (MockedStatic<EnumUtils> mockEnumUtils = Mockito.mockStatic(EnumUtils.class)) {
      mockEnumUtils
          .when(() -> EnumUtils.getRandomEnumValue(Mockito.eq(TestEnum.class), Mockito.any()))
          .thenReturn(TestEnum.FIRST);
      assertEquals(TestEnum.FIRST, Generator.generateValueOfType(TestEnum.class, mockRandom, 0));
    }

    try (MockedStatic<StringUtils> mockStringUtils = Mockito.mockStatic(StringUtils.class)) {
      mockStringUtils.when(() -> StringUtils.generateRandomString(Mockito.any())).thenReturn("abc");
      assertEquals("abc", Generator.generateValueOfType(String.class, mockRandom, 0));
    }

    try (MockedStatic<ArrayUtils> mockArrayUtils = Mockito.mockStatic(ArrayUtils.class)) {
      mockArrayUtils
          .when(
              () ->
                  ArrayUtils.generateArray(
                      Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt()))
          .thenReturn(new int[] {1, 2});
      assertEquals(2, Generator.generateValueOfType(int[].class, mockRandom, 0).length);
    }

    assertNull(Generator.generateValueOfType(Object.class, mockRandom, 0));
    assertNotNull(Generator.generateValueOfType(GeneratableTestInterface.class, mockRandom, 1));
    assertNull(Generator.generateValueOfType(GeneratableTestInterface.class, mockRandom, 0));

    try (MockedStatic<PrimitiveTypeUtils> mockPrimitiveTypeUtils =
        Mockito.mockStatic(PrimitiveTypeUtils.class)) {
      mockPrimitiveTypeUtils
          .when(() -> PrimitiveTypeUtils.isPrimitive(Mockito.eq(int.class)))
          .thenReturn(true);
      mockPrimitiveTypeUtils
          .when(() -> PrimitiveTypeUtils.getPrimitiveTypeByClass(Mockito.any()))
          .thenReturn(PrimitiveTypeUtils.PrimitiveType.INTEGER);
      mockPrimitiveTypeUtils
          .when(
              () ->
                  PrimitiveTypeUtils.generatePrimitiveTypeValue(
                      Mockito.eq(PrimitiveTypeUtils.PrimitiveType.INTEGER), Mockito.any()))
          .thenReturn(5);
      Mockito.when(mockRandom.nextInt(Mockito.anyInt())).thenReturn(0);
      assertEquals(
          5,
          Generator.generateValueOfType(GeneratableTestClass.class, mockRandom, 1).getIntField());
    }

    assertThrows(
        NullPointerException.class, () -> Generator.generateValueOfType(null, mockRandom, 0));
    assertThrows(
        NullPointerException.class, () -> Generator.generateValueOfType(List.class, null, 0));
    assertThrows(
        NullPointerException.class, () -> Generator.generateValueOfType(null, null, mockRandom, 0));
    assertThrows(
        NullPointerException.class, () -> Generator.generateValueOfType(List.class, null, null, 0));
  }
}
