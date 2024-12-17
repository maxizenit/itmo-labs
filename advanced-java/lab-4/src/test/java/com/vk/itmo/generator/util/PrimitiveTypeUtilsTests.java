package com.vk.itmo.generator.util;

import static com.vk.itmo.generator.util.PrimitiveTypeUtils.PrimitiveType.BOOLEAN;
import static com.vk.itmo.generator.util.PrimitiveTypeUtils.PrimitiveType.BYTE;
import static com.vk.itmo.generator.util.PrimitiveTypeUtils.PrimitiveType.CHARACTER;
import static com.vk.itmo.generator.util.PrimitiveTypeUtils.PrimitiveType.DOUBLE;
import static com.vk.itmo.generator.util.PrimitiveTypeUtils.PrimitiveType.FLOAT;
import static com.vk.itmo.generator.util.PrimitiveTypeUtils.PrimitiveType.INTEGER;
import static com.vk.itmo.generator.util.PrimitiveTypeUtils.PrimitiveType.LONG;
import static com.vk.itmo.generator.util.PrimitiveTypeUtils.PrimitiveType.SHORT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PrimitiveTypeUtilsTests {

  @Test
  @SuppressWarnings("all")
  public void isPrimitiveTest() {
    assertTrue(PrimitiveTypeUtils.isPrimitive(Integer.class));
    assertTrue(PrimitiveTypeUtils.isPrimitive(double.class));
    assertTrue(PrimitiveTypeUtils.isPrimitive(char.class));
    assertTrue(PrimitiveTypeUtils.isPrimitive(Number.class));

    assertFalse(PrimitiveTypeUtils.isPrimitive(Object.class));
    assertFalse(PrimitiveTypeUtils.isPrimitive(List.class));

    assertThrows(NullPointerException.class, () -> PrimitiveTypeUtils.isPrimitive(null));
  }

  @Test
  @SuppressWarnings("all")
  public void generatePrimitiveTypeValueTest() {
    Random mockRandom = Mockito.mock(Random.class);
    Mockito.when(mockRandom.nextInt()).thenReturn(3);
    Mockito.when(mockRandom.nextInt(Mockito.anyInt(), Mockito.anyInt())).thenReturn(3);
    Mockito.when(mockRandom.nextLong()).thenReturn(3L);
    Mockito.when(mockRandom.nextFloat()).thenReturn(3.0f);
    Mockito.when(mockRandom.nextDouble()).thenReturn(3.0d);
    Mockito.when(mockRandom.nextBoolean()).thenReturn(Boolean.TRUE);

    assertEquals((byte) 3, PrimitiveTypeUtils.generatePrimitiveTypeValue(BYTE, mockRandom));
    assertEquals((short) 3, PrimitiveTypeUtils.generatePrimitiveTypeValue(SHORT, mockRandom));
    assertEquals(3, PrimitiveTypeUtils.generatePrimitiveTypeValue(INTEGER, mockRandom));
    assertEquals(3L, PrimitiveTypeUtils.generatePrimitiveTypeValue(LONG, mockRandom));

    assertEquals(3f, PrimitiveTypeUtils.generatePrimitiveTypeValue(FLOAT, mockRandom));
    assertEquals(3d, PrimitiveTypeUtils.generatePrimitiveTypeValue(DOUBLE, mockRandom));

    assertEquals(true, PrimitiveTypeUtils.generatePrimitiveTypeValue(BOOLEAN, mockRandom));

    assertEquals((char) 3, PrimitiveTypeUtils.generatePrimitiveTypeValue(CHARACTER, mockRandom));

    assertThrows(
        NullPointerException.class,
        () -> PrimitiveTypeUtils.generatePrimitiveTypeValue(null, mockRandom));
    assertThrows(
        NullPointerException.class,
        () -> PrimitiveTypeUtils.generatePrimitiveTypeValue(INTEGER, null));
  }

  @Test
  @SuppressWarnings("all")
  public void getPrimitiveTypeByClassTest() {
    assertEquals(BYTE, PrimitiveTypeUtils.getPrimitiveTypeByClass(Byte.class));
    assertEquals(BYTE, PrimitiveTypeUtils.getPrimitiveTypeByClass(byte.class));
    assertEquals(SHORT, PrimitiveTypeUtils.getPrimitiveTypeByClass(Short.class));
    assertEquals(SHORT, PrimitiveTypeUtils.getPrimitiveTypeByClass(short.class));
    assertEquals(INTEGER, PrimitiveTypeUtils.getPrimitiveTypeByClass(Integer.class));
    assertEquals(INTEGER, PrimitiveTypeUtils.getPrimitiveTypeByClass(int.class));
    assertEquals(LONG, PrimitiveTypeUtils.getPrimitiveTypeByClass(Long.class));
    assertEquals(LONG, PrimitiveTypeUtils.getPrimitiveTypeByClass(long.class));

    assertEquals(FLOAT, PrimitiveTypeUtils.getPrimitiveTypeByClass(Float.class));
    assertEquals(FLOAT, PrimitiveTypeUtils.getPrimitiveTypeByClass(float.class));
    assertEquals(DOUBLE, PrimitiveTypeUtils.getPrimitiveTypeByClass(Double.class));
    assertEquals(DOUBLE, PrimitiveTypeUtils.getPrimitiveTypeByClass(double.class));

    assertEquals(BOOLEAN, PrimitiveTypeUtils.getPrimitiveTypeByClass(Boolean.class));
    assertEquals(BOOLEAN, PrimitiveTypeUtils.getPrimitiveTypeByClass(boolean.class));

    assertEquals(CHARACTER, PrimitiveTypeUtils.getPrimitiveTypeByClass(Character.class));
    assertEquals(CHARACTER, PrimitiveTypeUtils.getPrimitiveTypeByClass(char.class));

    assertEquals(INTEGER, PrimitiveTypeUtils.getPrimitiveTypeByClass(Number.class));

    assertThrows(
        NullPointerException.class, () -> PrimitiveTypeUtils.getPrimitiveTypeByClass(null));
    assertThrows(
        IllegalArgumentException.class,
        () -> PrimitiveTypeUtils.getPrimitiveTypeByClass(String.class));
  }
}
