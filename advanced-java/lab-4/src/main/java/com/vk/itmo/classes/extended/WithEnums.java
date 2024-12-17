package com.vk.itmo.classes.extended;

import com.vk.itmo.generator.Generatable;
import java.time.DayOfWeek;
import java.time.Month;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Generatable
@AllArgsConstructor
@ToString
@SuppressWarnings("unused")
public class WithEnums {

  private Month month;
  private DayOfWeek dayOfWeek;
}
