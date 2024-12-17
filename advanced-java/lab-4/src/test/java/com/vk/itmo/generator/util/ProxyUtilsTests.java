package com.vk.itmo.generator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ProxyUtilsTests {

  private interface TestInterface {
    int testMethod();
  }

  private static class TestClass {}

  @Test
  @SuppressWarnings("all")
  public void generateProxyTest() {
    Random mockRandom = Mockito.mock(Random.class);
    Mockito.when(mockRandom.nextInt()).thenReturn(1);

    TestInterface proxy = (TestInterface) ProxyUtils.generateProxy(TestInterface.class, mockRandom);
    assertEquals("TestInterface(proxy)", proxy.toString());
    assertTrue(proxy.equals(proxy));
    assertFalse(proxy.equals(null));
    assertEquals(System.identityHashCode(proxy), proxy.hashCode());
    assertEquals(1, proxy.testMethod());

    assertThrows(NullPointerException.class, () -> ProxyUtils.generateProxy(null, mockRandom));
    assertThrows(
        NullPointerException.class, () -> ProxyUtils.generateProxy(TestInterface.class, null));
    assertThrows(
        IllegalArgumentException.class,
        () -> ProxyUtils.generateProxy(TestClass.class, mockRandom));
  }
}
