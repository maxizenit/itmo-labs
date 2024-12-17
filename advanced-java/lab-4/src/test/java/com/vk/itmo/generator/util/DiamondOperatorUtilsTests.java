package com.vk.itmo.generator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DiamondOperatorUtilsTests {

  @Test
  @SuppressWarnings("all")
  public void hasStringDiamondOperatorTest() {
    assertTrue(DiamondOperatorUtils.hasStringDiamondOperator("List<Integer>"));
    assertTrue(DiamondOperatorUtils.hasStringDiamondOperator("Map<Integer,String>"));
    assertTrue(DiamondOperatorUtils.hasStringDiamondOperator("List<Map<Integer,String>>"));

    assertFalse(DiamondOperatorUtils.hasStringDiamondOperator("List"));
    assertFalse(DiamondOperatorUtils.hasStringDiamondOperator("List><String"));
    assertFalse(DiamondOperatorUtils.hasStringDiamondOperator("List>String"));
    assertFalse(DiamondOperatorUtils.hasStringDiamondOperator("List<String"));

    assertThrows(
        NullPointerException.class, () -> DiamondOperatorUtils.hasStringDiamondOperator(null));
  }

  @Test
  @SuppressWarnings("all")
  public void getContentBeforeDiamondOperatorTest() {
    assertEquals("List", DiamondOperatorUtils.getContentBeforeDiamondOperator("List<Integer>"));
    assertEquals("Map", DiamondOperatorUtils.getContentBeforeDiamondOperator("Map<Integer,String>"));
    assertEquals(
        "Map",
        DiamondOperatorUtils.getContentBeforeDiamondOperator(
            "Map<Map<Integer,String>,Set<Integer>>"));

    assertThrows(
        IllegalArgumentException.class,
        () -> DiamondOperatorUtils.getContentBeforeDiamondOperator("Set"));
    assertThrows(
        NullPointerException.class,
        () -> DiamondOperatorUtils.getContentBeforeDiamondOperator(null));
  }

  @Test
  @SuppressWarnings("all")
  public void getContentInDiamondOperatorTest() {
    assertEquals("Integer", DiamondOperatorUtils.getContentInDiamondOperator("List<Integer>"));
    assertEquals(
        "Integer,String", DiamondOperatorUtils.getContentInDiamondOperator("Map<Integer,String>"));

    assertThrows(
        IllegalArgumentException.class,
        () -> DiamondOperatorUtils.getContentInDiamondOperator("List"));
    assertThrows(
        NullPointerException.class, () -> DiamondOperatorUtils.getContentInDiamondOperator(null));
  }

  @Test
  @SuppressWarnings("all")
  public void getPartsFromDiamondOperatorContentTest() {
    assertEquals(
        List.of("Integer", "String"),
        DiamondOperatorUtils.getPartsFromDiamondOperatorContent("Integer,String"));
    assertEquals(
        List.of("Map<Integer,String>", "Set<String>"),
        DiamondOperatorUtils.getPartsFromDiamondOperatorContent("Map<Integer,String>,Set<String>"));
    assertEquals(
        List.of("Double"), DiamondOperatorUtils.getPartsFromDiamondOperatorContent("Double"));

    assertThrows(
        NullPointerException.class, () -> DiamondOperatorUtils.getPartsFromDiamondOperatorContent(null));
  }
}
