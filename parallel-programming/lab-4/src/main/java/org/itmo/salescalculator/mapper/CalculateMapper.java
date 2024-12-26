package org.itmo.salescalculator.mapper;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.itmo.salescalculator.model.CalculateData;

public class CalculateMapper extends Mapper<LongWritable, Text, Text, CalculateData> {

  @Override
  protected void map(LongWritable key, Text value, Context context)
      throws IOException, InterruptedException {
    String line = value.toString();
    if (isCsvHeader(key, line)) {
      return;
    }

    if (isCsvHeader(key, line)) {
      return;
    }

    String[] fields = line.split(",");
    String category = fields[2];
    double price = Double.parseDouble(fields[3]);
    int quantity = Integer.parseInt(fields[4]);

    double revenue = price * quantity;

    Text resultKey = new Text(category);
    CalculateData resultValue = new CalculateData(revenue, quantity);

    context.write(resultKey, resultValue);
  }

  private boolean isCsvHeader(LongWritable key, String line) {
    return key.get() == 0 && line.contains("transaction_id");
  }
}
