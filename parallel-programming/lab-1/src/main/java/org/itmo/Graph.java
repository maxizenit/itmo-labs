package org.itmo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicIntegerArray;

class Graph {

    private int V;
    private ArrayList<Integer>[] adjList;

    Graph(int vertices) {
        this.V = vertices;
        adjList = new ArrayList[vertices];
        for (int i = 0; i < vertices; ++i) {
            adjList[i] = new ArrayList<>();
        }
    }

    void addEdge(int src, int dest) {
        if (!adjList[src].contains(dest)) {
            adjList[src].add(dest);
        }
    }

    //Generated by Maxim Puzanov
    void parallelBFS(int startVertex, int parallelism) {
        ExecutorService executor = Executors.newFixedThreadPool(parallelism);

        AtomicIntegerArray visited = new AtomicIntegerArray(V);
        visited.set(startVertex, 1);

        Queue<Integer> globalQueue = new ConcurrentLinkedQueue<>();
        globalQueue.add(startVertex);

        while (!globalQueue.isEmpty()) {
            List<Queue<Integer>> localQueues = new ArrayList<>();
            for (int i = 0; i < parallelism; i++) {
                localQueues.add(new LinkedList<>());
            }

            List<Callable<Void>> tasks = new ArrayList<>();

            for (int i = 0; i < parallelism; i++) {
                Queue<Integer> localQueue = localQueues.get(i);
                tasks.add(() -> {
                    while (!globalQueue.isEmpty()) {
                        Integer v = globalQueue.poll();
                        if (v != null) {
                            for (int n : adjList[v]) {
                                if (visited.getAndSet(n, 1) == 0) {
                                    localQueue.add(n);
                                }
                            }
                        }
                    }
                    return null;
                });
            }

            try {
                executor.invokeAll(tasks);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (Queue<Integer> localQueue : localQueues) {
                globalQueue.addAll(localQueue);
            }
        }

        executor.shutdown();
    }

    //Generated by ChatGPT
    void bfs(int startVertex) {
        boolean[] visited = new boolean[V];
        LinkedList<Integer> queue = new LinkedList<>();

        visited[startVertex] = true;
        queue.add(startVertex);

        while (!queue.isEmpty()) {
            startVertex = queue.poll();

            for (int n : adjList[startVertex]) {
                if (!visited[n]) {
                    visited[n] = true;
                    queue.add(n);
                }
            }
        }
    }

}