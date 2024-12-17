package com.vk.itmo.classes;

import com.vk.itmo.generator.Generatable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Generatable
@Getter
@Setter
@AllArgsConstructor
@ToString
@SuppressWarnings("unused")
public class Product {

  private String name;
  private double price;

  public Product(String name) {
    this.name = name;
    this.price = Double.MIN_VALUE;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }
}
