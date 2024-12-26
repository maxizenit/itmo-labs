package org.itmo.salescalculator.mapper;

import java.io.IOException;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.itmo.salescalculator.model.SortData;

public class SortMapper extends Mapper<LongWritable, Text, DoubleWritable, SortData> {

  @Override
  protected void map(LongWritable key, Text value, Context context)
      throws IOException, InterruptedException {
    String[] fields = value.toString().split("\t");

    String category = fields[0];
    double revenue = Double.parseDouble(fields[1]);
    int quantity = Integer.parseInt(fields[2]);

    DoubleWritable resultKey = new DoubleWritable(revenue);
    SortData resultValue = new SortData(category, quantity);

    context.write(resultKey, resultValue);
  }
}
