package com.vk.itmo.classes;

import com.vk.itmo.generator.Generatable;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Generatable
@AllArgsConstructor
@ToString
@SuppressWarnings("unused")
public class Rectangle implements Shape {

  private double length;
  private double width;

  @Override
  public double getArea() {
    return length * width;
  }

  @Override
  public double getPerimeter() {
    return 2 * (length + width);
  }
}
