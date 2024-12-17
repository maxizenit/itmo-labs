package com.vk.itmo.classes.extended;

import com.vk.itmo.generator.Generatable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Generatable
@AllArgsConstructor
@ToString
@SuppressWarnings("unused")
public class Maps {

  private Map<Integer, String> map;
  private Map<Map<Long, Integer>, Map<String, Boolean>> nestedMap;
}
