package com.vk.itmo.classes;

import com.vk.itmo.generator.Generatable;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Generatable
@AllArgsConstructor
@ToString
@SuppressWarnings("unused")
public class Triangle implements Shape {

  private double sideA;
  private double sideB;
  private double sideC;

  @Override
  public double getArea() {
    double s = (sideA + sideB + sideC) / 2;
    return Math.sqrt(s * (s - sideA) * (s - sideB) * (s - sideC));
  }

  @Override
  public double getPerimeter() {
    return sideA + sideB + sideC;
  }
}
