package com.vk.itmo.classes;

import com.vk.itmo.generator.Generatable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Generatable
@Getter
@AllArgsConstructor
@ToString
@SuppressWarnings("unused")
public class BinaryTreeNode {

  private Integer data;
  @Setter private BinaryTreeNode left;
  @Setter private BinaryTreeNode right;
}
