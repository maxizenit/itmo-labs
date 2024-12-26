package org.itmo.salescalculator.job;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.itmo.salescalculator.mapper.CalculateMapper;
import org.itmo.salescalculator.model.CalculateData;
import org.itmo.salescalculator.reducer.CalculateReducer;

public class CalculateJob extends AbstractJob {

  @Override
  protected Job createJob(
      String inputDir, String outputDir, int reducerCount, Configuration configuration)
      throws IOException {
    Job job = Job.getInstance(configuration, "calculateJob");

    job.setJarByClass(CalculateJob.class);

    job.setNumReduceTasks(reducerCount);

    job.setMapperClass(CalculateMapper.class);
    job.setReducerClass(CalculateReducer.class);

    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(CalculateData.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    FileInputFormat.addInputPath(job, new Path(inputDir));
    FileOutputFormat.setOutputPath(job, new Path(outputDir));

    return job;
  }
}
