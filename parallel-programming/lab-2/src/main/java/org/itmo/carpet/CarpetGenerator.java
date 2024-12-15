package org.itmo.carpet;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.itmo.carpet.model.Carpet;
import org.itmo.carpet.model.Square;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Генератор ковра Серпинского по заданным параметрам.
 */
@UtilityClass
public class CarpetGenerator {

    /**
     * Генерирует ковёр Серпинского с заданным числом итераций.
     *
     * @param iterationsCount число итераций
     * @param parallelism     количество потоков для генерации (не менее одного)
     * @return Ковёр Серпинского с заданным числом итераций
     */
    public Carpet generate(int iterationsCount, int parallelism) {
        if (iterationsCount <= 0) {
            throw new IllegalArgumentException("iterationsCount must be greater than 0");
        }
        if (parallelism <= 0) {
            throw new IllegalArgumentException("parallelism must be greater than 0");
        }

        return parallelism == 1 ? generateSerial(iterationsCount) : generateParallel(iterationsCount, parallelism);
    }

    /**
     * Разделяет переданный квадрат на 9 квадратов, добавляя средний из них в {@code internalSquares}, а остальные -
     * в {@code nextLevelSquares}
     *
     * @param square              квадрат, который требуется разделить
     * @param nextLevelSquareSize длина стороны квадратов следующего уровня
     * @param nextLevelSquares    квадраты следующего уровня
     * @param internalSquares     внутренние (вырезанные) квадраты
     */
    private void splitSquare(Square square, int nextLevelSquareSize, Queue<Square> nextLevelSquares, Queue<Square> internalSquares) {
        // Координаты внутреннего квадрата
        int middleSquareX = square.x() + nextLevelSquareSize;
        int middleSquareY = square.y() + nextLevelSquareSize;

        for (int x = square.x(); x < square.x() + square.size(); x += nextLevelSquareSize) {
            for (int y = square.y(); y < square.y() + square.size(); y += nextLevelSquareSize) {
                Square nextLevelSquare = new Square(x, y, nextLevelSquareSize);
                if (x == middleSquareX && y == middleSquareY) {
                    internalSquares.add(nextLevelSquare);//Средний квадрат не разделяется дальше, а вырезается
                } else {
                    nextLevelSquares.add(nextLevelSquare);
                }
            }
        }
    }

    /**
     * Генерирует ковёр Серпинского последовательно.
     *
     * @param iterationsCount количество итераций
     * @return ковёр Серпинского
     */
    private Carpet generateSerial(int iterationsCount) {
        int carpetSize = (int) Math.pow(3, iterationsCount);//Длина стороны ковра
        Queue<Square> currentLevelSquares = new LinkedList<>();//Необработанные квадраты текущего уровня
        Queue<Square> nextLevelSquares = new LinkedList<>();//Необработанные квадраты следующего уровня
        Queue<Square> internalSquares = new LinkedList<>();//Вырезанные квадраты

        Square initialSquare = new Square(0, 0, carpetSize);//Первый квадрат - сам ковёр
        currentLevelSquares.add(initialSquare);

        for (int i = 0; i < iterationsCount; ++i) {
            int nextLevelSquareSize = (int) Math.pow(3, iterationsCount - i - 1);
            while (!currentLevelSquares.isEmpty()) {
                Square currentSquare = currentLevelSquares.poll();
                splitSquare(currentSquare, nextLevelSquareSize, nextLevelSquares, internalSquares);
            }

            currentLevelSquares.addAll(nextLevelSquares);
            nextLevelSquares.clear();
        }

        return new Carpet(carpetSize, new HashSet<>(internalSquares));
    }

    /**
     * Генерирует ковёр Серпинского параллельно.
     *
     * @param iterationsCount количество итераций
     * @param parallelism     количество потоков генерации
     * @return ковёр Серпинского
     */
    @SneakyThrows
    private Carpet generateParallel(int iterationsCount, int parallelism) {
        ExecutorService executorService = Executors.newFixedThreadPool(parallelism);

        int carpetSize = (int) Math.pow(3, iterationsCount);//Длина стороны ковра
        Queue<Square> currentLevelSquares = new ConcurrentLinkedQueue<>();//Необработанные квадраты текущего уровня
        List<Queue<Square>> nextLevelSquares = new ArrayList<>();//Локальные очереди квадратов следующего уровня
        Set<Square> internalSquares = new HashSet<>();//Вырезанные квадраты
        List<Queue<Square>> localInternalSquares = new ArrayList<>();//Локальные очереди вырезанных квадратов

        Set<Callable<Void>> tasks = new HashSet<>();

        Square initialSquare = new Square(0, 0, carpetSize);//Первый квадрат - сам ковёр
        currentLevelSquares.add(initialSquare);

        //Инициализация локальных очередей
        for (int i = 0; i < parallelism; ++i) {
            nextLevelSquares.add(new LinkedList<>());
            localInternalSquares.add(new LinkedList<>());
        }

        for (int i = 0; i < iterationsCount; ++i) {
            int nextLevelSquareSize = (int) Math.pow(3, iterationsCount - i - 1);

            for (int j = 0; j < parallelism; ++j) {
                int finalJ = j;
                tasks.add(() -> {
                    while (!currentLevelSquares.isEmpty()) {
                        Square currentSquare = currentLevelSquares.poll();
                        splitSquare(currentSquare, nextLevelSquareSize, nextLevelSquares.get(finalJ), localInternalSquares.get(finalJ));
                    }
                    return null;
                });
            }

            executorService.invokeAll(tasks);
            tasks.clear();

            //Заполнение глобальных очередей локальными
            for (int j = 0; j < parallelism; ++j) {
                currentLevelSquares.addAll(nextLevelSquares.get(j));
                nextLevelSquares.get(j).clear();

                internalSquares.addAll(localInternalSquares.get(j));
                localInternalSquares.get(j).clear();
            }
        }

        executorService.close();
        return new Carpet(carpetSize, internalSquares);
    }
}
