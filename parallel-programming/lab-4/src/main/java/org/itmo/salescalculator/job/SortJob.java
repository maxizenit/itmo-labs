package org.itmo.salescalculator.job;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.itmo.salescalculator.comparator.SortComparator;
import org.itmo.salescalculator.mapper.SortMapper;
import org.itmo.salescalculator.model.SortData;
import org.itmo.salescalculator.reducer.SortReducer;

public class SortJob extends AbstractJob {

  @Override
  protected Job createJob(
      String inputDir, String outputDir, int reducerCount, Configuration configuration)
      throws IOException {
    Job job = Job.getInstance(configuration, "sortJob");

    job.setJarByClass(SortJob.class);

    job.setMapperClass(SortMapper.class);
    job.setReducerClass(SortReducer.class);

    job.setMapOutputKeyClass(DoubleWritable.class);
    job.setMapOutputValueClass(SortData.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    job.setSortComparatorClass(SortComparator.class);

    FileInputFormat.addInputPath(job, new Path(inputDir));
    FileOutputFormat.setOutputPath(job, new Path(outputDir));

    return job;
  }
}
