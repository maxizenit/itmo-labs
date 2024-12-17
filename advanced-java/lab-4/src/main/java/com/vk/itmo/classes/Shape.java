package com.vk.itmo.classes;

import com.vk.itmo.generator.Generatable;

@Generatable
@SuppressWarnings("unused")
public interface Shape {

  double getArea();

  double getPerimeter();
}
