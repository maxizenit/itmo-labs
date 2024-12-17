package com.vk.itmo.generator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;

import java.util.Random;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class EnumUtilsTests {

  private enum EmptyEnum {}

  private enum NonEmptyEnum {
    FIRST,
    SECOND,
    THIRD
  }

  @Test
  @SuppressWarnings("all")
  public void getRandomEnumValueTest() {
    Random mockRandom = Mockito.mock(Random.class);
    Mockito.when(mockRandom.nextInt(anyInt())).thenReturn(1);

    assertNull(EnumUtils.getRandomEnumValue(EmptyEnum.class, mockRandom));
    assertEquals(NonEmptyEnum.SECOND, EnumUtils.getRandomEnumValue(NonEmptyEnum.class, mockRandom));

    assertThrows(NullPointerException.class, () -> EnumUtils.getRandomEnumValue(null, mockRandom));
    assertThrows(
        NullPointerException.class, () -> EnumUtils.getRandomEnumValue(EmptyEnum.class, null));
  }
}
