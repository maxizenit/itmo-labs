package org.itmo.salescalculator.reducer;

import java.io.IOException;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.itmo.salescalculator.model.SortData;

public class SortReducer extends Reducer<DoubleWritable, SortData, Text, Text> {

  @Override
  protected void reduce(DoubleWritable key, Iterable<SortData> values, Context context)
      throws IOException, InterruptedException {
    for (SortData value : values) {
      Text resultKey = new Text(value.getCategory());
      String result = String.format("%.2f\t%d", key.get(), value.getQuantity());
      Text resultValue = new Text(result);

      context.write(resultKey, resultValue);
    }
  }
}
