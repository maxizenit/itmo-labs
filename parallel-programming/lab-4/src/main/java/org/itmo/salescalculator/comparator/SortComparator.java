package org.itmo.salescalculator.comparator;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class SortComparator extends WritableComparator {

  public SortComparator() {
    super(DoubleWritable.class, true);
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compare(WritableComparable a, WritableComparable b) {
    return b.compareTo(a);
  }
}
