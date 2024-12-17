package com.vk.itmo.generator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Array;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ArrayUtilsTests {

  @SuppressWarnings("unused")
  private static class TestClass {}

  @Test
  @SuppressWarnings("all")
  public void generateArrayTest() {
    Random mockRandom = Mockito.mock(Random.class);
    Mockito.when(mockRandom.nextInt(Mockito.anyInt(), Mockito.anyInt())).thenReturn(2);

    Class<?> intArrayClass = int[].class;
    int[] intArray =
        (int[])
            ArrayUtils.generateArray((Class<? extends Array>) intArrayClass, null, mockRandom, 1);
    assertEquals(2, intArray.length);

    Class<?> nonGeneratableArrayClass = TestClass[].class;
    TestClass[] nonGeneratableArray =
        (TestClass[])
            ArrayUtils.generateArray(
                (Class<? extends Array>) nonGeneratableArrayClass, null, mockRandom, 1);
    assertEquals(0, nonGeneratableArray.length);

    assertThrows(
        NullPointerException.class, () -> ArrayUtils.generateArray(null, null, mockRandom, 1));
    assertThrows(
        NullPointerException.class,
        () ->
            ArrayUtils.generateArray(
                (Class<? extends Array>) nonGeneratableArrayClass, null, null, 1));
  }
}
