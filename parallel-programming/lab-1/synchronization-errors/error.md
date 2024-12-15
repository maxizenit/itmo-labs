Ошибки синхронизации были сэмулированы при помощи CyclicBarrier с неверным количеством потоков

```
List<Callable<Void>> tasks = new ArrayList<>();
CyclicBarrier barrier = new CyclicBarrier(parallelism + 1);

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
        barrier.await();
        return null;
    });
}
```