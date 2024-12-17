package com.vk.itmo.classes.extended;

import com.vk.itmo.classes.Example;
import com.vk.itmo.generator.Generatable;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Generatable
@AllArgsConstructor
@ToString
@SuppressWarnings("unused")
public class NestedCollections {

  private List<Set<Integer>> listSet;
  private Queue<Set<List<Example>>> queueSetList;
}
