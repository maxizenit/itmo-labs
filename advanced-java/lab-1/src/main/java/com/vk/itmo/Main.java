package com.vk.itmo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vk.itmo.classinfo.ClassInfoStorage;
import com.vk.itmo.classinfo.ClassInfoStorageFiller;
import com.vk.itmo.metrics.Metrics;
import com.vk.itmo.metrics.MetricsCalculator;

import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Missing input jar file path argument");
        }

        ClassInfoStorage storage = new ClassInfoStorage();
        ClassInfoStorageFiller filler = new ClassInfoStorageFiller(storage);
        MetricsCalculator metricsCalculator = new MetricsCalculator();

        filler.fillFromJarFile(args[0]);

        Metrics metrics = metricsCalculator.calculate(storage.getVisitedClasses());
        String jsonMetrics = new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(metrics);

        System.out.println(jsonMetrics);
        if (args.length > 1) {
            try (FileWriter writer = new FileWriter(args[1], false)) {
                writer.write(jsonMetrics);
            }
        }
    }
}