package com.vk.itmo.lunchsimulator.programmer;

import com.vk.itmo.lunchsimulator.Spoon;
import com.vk.itmo.lunchsimulator.Waiters;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 * Программист. Циклически выполняет следующие действия (пока ему дают еду):
 *
 * <ul>
 *     <li>
 *         <p>Обсуждение преподавателей</p>
 *     </li>
 *     <li>
 *         <p>Поднятие ложек (если недоступны, ожидает их доступности)</p>
 *     </li>
 *     <li>
 *         <p>Просьба официанта дать еды</p>
 *     </li>
 *     <li>
 *         <p>Приём пищи (если официант дал еду на предыдущем шаге)</p>
 *     </li>
 *     <li>
 *         <p>Возврат ложек на место</p>
 *     </li>
 * </ul>
 */
@Slf4j
@Builder
public class Programmer implements Callable<Void> {

    /**
     * ID.
     */
    @Getter
    private final int id;

    /**
     * Офиицанты.
     */
    private final Waiters waiters;

    /**
     * Рандомизатор для различного времени приёма пищи/дискуссии.
     */
    private final Random random;

    /**
     * Минимальное время приёма пищи/дискуссии.
     */
    private final int minActionDuration;

    /**
     * Максимальное время приёма пищи/дискуссии.
     */
    private final int maxActionDuration;

    /**
     * Лок критической секции.
     */
    private final Lock criticalSectionLock;

    /**
     * Семафор, определяющий доступность обеих ложек для данного программиста.
     */
    @Getter
    private final Semaphore bothSpoonsAvailableSemaphore = new Semaphore(0);

    /**
     * Первая ложка.
     */
    private final Spoon firstSpoon;

    /**
     * Вторая ложка.
     */
    private final Spoon secondSpoon;

    /**
     * {@link Supplier}, возвращающий первого соседа.
     */
    private Supplier<Programmer> firstNeighborSupplier;

    /**
     * {@link Supplier}, возвращающий второго соседа.
     */
    private Supplier<Programmer> secondNeighborSupplier;

    /**
     * Количество съеденных порций.
     */
    private final AtomicInteger eatenFoodPortionsCount = new AtomicInteger();

    /**
     * Текущее состояние программиста.
     */
    @Getter
    @Builder.Default
    private ProgrammerState state = ProgrammerState.DISCUSSING;

    @Override
    public Void call() {
        boolean canEat;
        do {
            discuss();
            takeSpoons();
            canEat = waiters.askFoodFrom();
            if (canEat) {
                eat();
            }
            putSpoons();
        } while (canEat);
        return null;
    }

    /**
     * Возвращает количество съеденных программистом порций.
     *
     * @return количество съеденных программистом порций
     */
    public int getEatenFoodPortionsCount() {
        return eatenFoodPortionsCount.get();
    }

    /**
     * Проверяет состоящие программиста и его соседей. Если программист голодный и его соседи не едят, то освобождает
     * его семафор доступности обеих вилок.
     *
     * <p>Может быть вызван из потока другого программиста. Именно поэтому здесь используется семафор.</p>
     */
    public void checkStates() {
        Programmer firstNeighbor = firstNeighborSupplier.get();
        Programmer secondNeighbor = secondNeighborSupplier.get();

        if (state == ProgrammerState.HUNGRY &&
                firstNeighbor.getState() != ProgrammerState.EATING &&
                secondNeighbor.getState() != ProgrammerState.EATING) {
            state = ProgrammerState.EATING;
            bothSpoonsAvailableSemaphore.release();
        }
    }

    /**
     * Обсуждает преподавателей.
     */
    private void discuss() {
        log.info("Programmer {} discusses teachers", id);
        sleep();
    }

    /**
     * Берёт ложки, переходит в состояние голодания.
     */
    @SneakyThrows
    private void takeSpoons() {
        log.info("Programmer {} takes spoons", id);

        criticalSectionLock.lock();
        state = ProgrammerState.HUNGRY;
        checkStates();
        criticalSectionLock.unlock();

        bothSpoonsAvailableSemaphore.acquire();
        firstSpoon.take();
        secondSpoon.take();
        log.info("Programmer {} took spoons", id);
    }

    /**
     * Принимает пищу.
     */
    private void eat() {
        log.info("Programmer {} eats", id);
        sleep();
        eatenFoodPortionsCount.incrementAndGet();
    }

    /**
     * Возвращает ложки, переходит в состояние дискуссии.
     */
    private void putSpoons() {
        firstSpoon.put();
        secondSpoon.put();
        log.info("Programmer {} returned spoons", id);

        Programmer firstNeighbor = firstNeighborSupplier.get();
        Programmer secondNeighbor = secondNeighborSupplier.get();

        criticalSectionLock.lock();
        state = ProgrammerState.DISCUSSING;
        firstNeighbor.checkStates();
        secondNeighbor.checkStates();
        criticalSectionLock.unlock();
    }

    /**
     * Ожидает случайное время (необходимо для приёма пищи/дискуссии).
     */
    @SneakyThrows
    private void sleep() {
        int sleepTime = random.nextInt(minActionDuration, maxActionDuration + 1);
        Thread.sleep(sleepTime);
    }
}
