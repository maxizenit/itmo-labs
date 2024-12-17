package com.vk.itmo.generator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class StringUtilsTests {

  @Test
  @SuppressWarnings("all")
  public void generateRandomStringTest() {
    Random mockRandom = Mockito.mock(Random.class);
    Mockito.when(mockRandom.nextInt(Mockito.anyInt(), Mockito.anyInt())).thenReturn(3);

    String randomString = StringUtils.generateRandomString(mockRandom);
    assertTrue(org.apache.commons.lang3.StringUtils.isNotBlank(randomString));
    assertEquals(3, randomString.length());

    assertThrows(NullPointerException.class, () -> StringUtils.generateRandomString(null));
  }
}
