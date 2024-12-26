package org.itmo.salescalculator.reducer;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.itmo.salescalculator.model.CalculateData;

public class CalculateReducer extends Reducer<Text, CalculateData, Text, Text> {

  @Override
  protected void reduce(Text key, Iterable<CalculateData> values, Context context)
      throws IOException, InterruptedException {
    double revenueSum = 0.0;
    int quantitySum = 0;

    for (CalculateData value : values) {
      revenueSum += value.getRevenue();
      quantitySum += value.getQuantity();
    }

    String result = String.format("%.2f\t%d", revenueSum, quantitySum);

    context.write(key, new Text(result));
  }
}
