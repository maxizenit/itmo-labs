package com.vk.itmo.lunchsimulator;

import lombok.SneakyThrows;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Официанты.
 */
public class Waiters {

    /**
     * Семафор, позволяющий одновременно просить еду только определённому количеству программистов.
     */
    private final Semaphore semaphore;

    /**
     * Текущее количество порций еды.
     */
    private final AtomicInteger currentFoodPortionsCount;

    public Waiters(int waitersCount, int foodPortionsCount) {
        semaphore = new Semaphore(waitersCount);
        currentFoodPortionsCount = new AtomicInteger(foodPortionsCount);
    }

    /**
     * Вызывается программистом. Возвращает {@code true}, если программисту осталась доступной хотя бы одна порция еды.
     *
     * @return {@code true}, если программисту осталась доступной хотя бы одна порция еды
     */
    @SneakyThrows
    public boolean askFoodFrom() {
        semaphore.acquire();
        boolean canEat = currentFoodPortionsCount.getAndDecrement() > 0;
        semaphore.release();
        return canEat;
    }
}
