package org.itmo.salescalculator.job;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;

public abstract class AbstractJob extends Configured implements Tool {

  @Override
  public int run(String[] args) {
    String inputDir = args[0];
    String outputDir = args[1];
    int reducerCount = Integer.parseInt(args[2]);
    Configuration configuration = getConf();

    try (Job job = createJob(inputDir, outputDir, reducerCount, configuration)) {
      return job.waitForCompletion(true) ? 0 : 1;
    } catch (Exception e) {
      return 1;
    }
  }

  protected abstract Job createJob(
      String inputDir, String outputDir, int reducerCount, Configuration configuration)
      throws IOException;
}
