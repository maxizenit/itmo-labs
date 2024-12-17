package com.vk.itmo.generator.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vk.itmo.generator.Generatable;
import org.junit.jupiter.api.Test;

public class AnnotationUtilsTests {

  @Generatable
  private static class WithAnnotationClass {}

  private static class WithoutAnnotationClass {}

  @Test
  @SuppressWarnings("all")
  public void hasAnnotationTest() {
    assertTrue(AnnotationUtils.hasAnnotation(WithAnnotationClass.class, Generatable.class));
    assertFalse(AnnotationUtils.hasAnnotation(WithoutAnnotationClass.class, Generatable.class));

    assertThrows(
        NullPointerException.class, () -> AnnotationUtils.hasAnnotation(null, Generatable.class));
    assertThrows(
        NullPointerException.class,
        () -> AnnotationUtils.hasAnnotation(WithAnnotationClass.class, null));
  }
}
