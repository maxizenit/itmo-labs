package com.vk.itmo.classes;

import com.vk.itmo.generator.Generatable;
import java.util.List;
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
public class Cart {

  private List<Product> items;
}
