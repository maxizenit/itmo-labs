package org.itmo.salescalculator;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;
import org.itmo.salescalculator.job.CalculateJob;
import org.itmo.salescalculator.job.SortJob;

@Slf4j
public class SalesCalculatorApp {

  public static void main(String[] args) throws Exception {
    if (!validateArgs(args)) {
      log.error(
          "Invalid arguments. Correct arguments: <input directory> <output directory> <reducers count> <data block size (kb)>");
      System.exit(1);
    }

    String inputDirectory = args[0];
    String outputDirectory = args[1];
    int reducerCount = Integer.parseInt(args[2]);
    int dataBlockSize = Integer.parseInt(args[3]) * 1024; // bytes

    String tempOutputDirectory = outputDirectory + "-temp";
    Configuration configuration = new Configuration();
    configuration.set("fs.defaultFS", "hdfs://localhost:9000");
    configuration.set(
        "mapreduce.input.fileinputformat.split.maxsize", Integer.toString(dataBlockSize));

    log.info("Starting SalesCalculatorApp");
    long startTime = System.currentTimeMillis();

    // Calculation
    String[] calculationArgs =
        new String[] {inputDirectory, tempOutputDirectory, Integer.toString(reducerCount)};

    log.info("Starting calculation job");
    boolean isSuccess = ToolRunner.run(configuration, new CalculateJob(), calculationArgs) == 0;
    log.info("Finished calculation job with {}", isSuccess ? "success" : "failure");

    if (!isSuccess) {
      System.exit(1);
    }

    // Sorting
    String[] sortingArgs =
        new String[] {tempOutputDirectory, outputDirectory, Integer.toString(reducerCount)};

    log.info("Starting sorting job");
    isSuccess = ToolRunner.run(configuration, new SortJob(), sortingArgs) == 0;
    log.info("Finished sorting job with {}", isSuccess ? "success" : "failure");

    if (!isSuccess) {
      System.exit(1);
    }

    long endTime = System.currentTimeMillis();
    log.info("Finished jobs with {} ms", endTime - startTime);
  }

  private static boolean validateArgs(String[] args) {
    try {
      Integer.parseInt(args[2]);
      Integer.parseInt(args[3]);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
