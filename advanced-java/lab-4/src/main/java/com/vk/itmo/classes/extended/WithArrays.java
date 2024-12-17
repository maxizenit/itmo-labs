package com.vk.itmo.classes.extended;

import com.vk.itmo.classes.Example;
import com.vk.itmo.classes.Product;
import com.vk.itmo.generator.Generatable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Generatable
@AllArgsConstructor
@ToString
@SuppressWarnings("unused")
public class WithArrays {

  String[] strings;
  int[] ints;
  Example[] examples;
  List<Product>[] productsLists;
}
