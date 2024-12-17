package com.vk.itmo.generator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MapUtilsTests {

  @SuppressWarnings("unused")
  private static class TestClass {}

  @Test
  @SuppressWarnings("all")
  public void generateMapTest() {
    Random mockRandom = Mockito.mock(Random.class);
    Mockito.when(mockRandom.nextInt(Mockito.anyInt(), Mockito.anyInt())).thenReturn(2);
    Mockito.when(mockRandom.nextInt()).thenReturn(1, 2);
    Mockito.when(mockRandom.nextLong()).thenReturn(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L);

    Map<Object, Object> actualMap = MapUtils.generateMap(null, mockRandom, 1);
    assertTrue(actualMap.isEmpty());

    actualMap = MapUtils.generateMap("java.lang.Integer, java.lang.Boolean", mockRandom, 1);
    assertEquals(2, actualMap.size());

    actualMap =
        MapUtils.generateMap(
            "java.util.Map<java.lang.Long, java.lang.String>, java.util.Map<java.lang.Long, java.lang.String>",
            mockRandom,
            1);
    assertEquals(2, actualMap.size());
    for (Object key : actualMap.keySet()) {
      Map<Object, Object> nestedMap = (Map<Object, Object>) actualMap.get(key);
      assertEquals(2, nestedMap.size());
    }

    actualMap =
        MapUtils.generateMap(
            "java.lang.Long, com.vk.itmo.generator.util.MapUtilsTests$TestClass", mockRandom, 1);
    assertTrue(actualMap.isEmpty());

    actualMap =
        MapUtils.generateMap(
            "com.vk.itmo.generator.util.MapUtilsTests$TestClass, java.lang.Long", mockRandom, 1);
    assertTrue(actualMap.isEmpty());

    assertThrows(NullPointerException.class, () -> MapUtils.generateMap(null, null, 0));
    assertThrows(
        IllegalArgumentException.class,
        () -> MapUtils.generateMap("java.lang.Long", mockRandom, 1));
  }
}
