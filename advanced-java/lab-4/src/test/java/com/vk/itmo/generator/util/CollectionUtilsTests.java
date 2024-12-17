package com.vk.itmo.generator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CollectionUtilsTests {

  @SuppressWarnings("unused")
  private static class TestClass {}

  @Test
  @SuppressWarnings("all")
  public void generateCollectionTest() {
    Random mockRandom = Mockito.mock(Random.class);
    Mockito.when(mockRandom.nextInt(Mockito.anyInt(), Mockito.anyInt())).thenReturn(2);
    Mockito.when(mockRandom.nextLong()).thenReturn(1L, 2L);

    Collection<Object> collection =
        (Collection<Object>)
            CollectionUtils.generateCollection(List.class, "java.lang.Integer", mockRandom, 0);
    assertEquals(ArrayList.class, collection.getClass());
    assertEquals(2, collection.size());

    collection =
        (Collection<Object>)
            CollectionUtils.generateCollection(Set.class, "java.lang.Long", mockRandom, 0);
    assertEquals(HashSet.class, collection.getClass());
    assertEquals(2, collection.size());

    collection =
        (Collection<Object>)
            CollectionUtils.generateCollection(Queue.class, "java.lang.Integer", mockRandom, 0);
    assertEquals(ArrayDeque.class, collection.getClass());
    assertEquals(2, collection.size());

    collection =
        (Collection<Object>)
            CollectionUtils.generateCollection(
                List.class, "java.util.List<java.lang.Integer>", mockRandom, 0);
    List<Object> nestedList = (List<Object>) ((List<Object>) collection).get(0);
    assertEquals(2, nestedList.size());

    collection =
        (Collection<Object>) CollectionUtils.generateCollection(List.class, null, mockRandom, 0);
    assertTrue(org.apache.commons.collections4.CollectionUtils.isEmpty(collection));

    collection =
        (Collection<Object>)
            CollectionUtils.generateCollection(
                List.class,
                "com.vk.itmo.generator.util.CollectionUtilsTests$TestClass",
                mockRandom,
                0);
    assertTrue(org.apache.commons.collections4.CollectionUtils.isEmpty(collection));

    collection =
        (Collection<Object>)
            CollectionUtils.generateCollection(
                Set.class,
                "com.vk.itmo.generator.util.CollectionUtilsTests$TestClass",
                mockRandom,
                0);
    assertTrue(org.apache.commons.collections4.CollectionUtils.isEmpty(collection));

    collection =
        (Collection<Object>)
            CollectionUtils.generateCollection(
                Queue.class,
                "com.vk.itmo.generator.util.CollectionUtilsTests$TestClass",
                mockRandom,
                0);
    assertTrue(org.apache.commons.collections4.CollectionUtils.isEmpty(collection));

    assertThrows(
        NullPointerException.class,
        () -> CollectionUtils.generateCollection(null, null, mockRandom, 0));
    assertThrows(
        NullPointerException.class,
        () -> CollectionUtils.generateCollection(Set.class, null, null, 0));
    assertThrows(
        IllegalArgumentException.class,
        () -> CollectionUtils.generateCollection(Map.class, null, mockRandom, 0));
  }
}
