package com.vk.itmo.generator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vk.itmo.generator.util.classutilstestpackage.TestClass1;
import com.vk.itmo.generator.util.classutilstestpackage.TestClass2;
import com.vk.itmo.generator.util.classutilstestpackage.TestInterface;
import com.vk.itmo.generator.util.classutilstestpackage.nested.NestedTestClass;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClassUtilsTests {

  @Test
  @SuppressWarnings("all")
  public void loadClassTest() {
    assertEquals(List.class, ClassUtils.loadClass("java.util.List"));
    assertEquals(Integer.class, ClassUtils.loadClass("java.lang.Integer"));

    assertThrows(NullPointerException.class, () -> ClassUtils.loadClass(null));
  }

  @Test
  @SuppressWarnings("all")
  public void getClassesFromPackageTest() {
    Set<Class<?>> classes =
        ClassUtils.getClassesFromPackage("com.vk.itmo.generator.util.classutilstestpackage");
    assertTrue(classes.contains(TestClass1.class));
    assertTrue(classes.contains(TestClass2.class));
    assertTrue(classes.contains(TestInterface.class));
    assertTrue(classes.contains(NestedTestClass.class));

    assertThrows(NullPointerException.class, () -> ClassUtils.getClassesFromPackage(null));
  }
}
