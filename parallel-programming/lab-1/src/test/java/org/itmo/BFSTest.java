package org.itmo;

import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class BFSTest {

    private static final int THREADS_COUNT = Runtime.getRuntime().availableProcessors();

    @Test
    public void bfsTest() throws IOException {
        int[] sizes = new int[]{10, 100, 1000, 10_000, 10_000, 50_000, 100_000};
        int[] connections = new int[]{50, 500, 5000, 50_000, 100_000, 1_000_000, 1_000_000};
        Random r = new Random(42);
        try (FileWriter fw = new FileWriter("tmp/results.txt")) {
            for (int i = 0; i < sizes.length; i++) {
                Graph g = generateGraph(r, sizes[i], connections[i]);
                long serialTime = executeSerialBfsAndGetTime(g);
                long parallelTime = executeParallelBfsAndGetTime(g);
                fw.append("Times for " + sizes[i] + " vertices and " + connections[i] + " connections: ");
                fw.append("\nSerial: " + serialTime);
                fw.append("\nParallel: " + parallelTime);
                fw.append(" (" + THREADS_COUNT + " threads)");
                fw.append("\n--------\n");
            }
            fw.flush();
        }
    }

    private Graph generateGraph(Random r, int size, int numOfConnections) {
        Graph graph = new Graph(size);
        for (int i = 0; i < numOfConnections; i++) {
            graph.addEdge(r.nextInt(size), r.nextInt(size));
        }
        return graph;
    }

    private long executeSerialBfsAndGetTime(Graph g) {
        long startTime = System.currentTimeMillis();
        g.bfs(0);
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    private long executeParallelBfsAndGetTime(Graph g) {
        long startTime = System.currentTimeMillis();
        g.parallelBFS(0, THREADS_COUNT);
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

}
