package com.vk.itmo.lunchsimulator;

import com.vk.itmo.lunchsimulator.programmer.Programmer;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Симулятор обеда в ресторане.
 */
@Slf4j
public class LunchSimulator {

    /**
     * Программисты.
     */
    private final List<Programmer> programmers;

    /**
     * Количество порций в ресторане.
     */
    private final int foodPortionsCount;

    /**
     * {@link ExecutorService} для потоков программистов.
     */
    private final ExecutorService executorService;

    public LunchSimulator(int programmersCount,
                          int waitersCount,
                          int foodPortionsCount,
                          int minActionDuration,
                          int maxActionDuration) {
        this.foodPortionsCount = foodPortionsCount;
        executorService = Executors.newFixedThreadPool(programmersCount);

        Waiters waiters = createWaiters(waitersCount, foodPortionsCount);
        programmers = createProgrammers(programmersCount, waiters, minActionDuration, maxActionDuration);
    }

    /**
     * Создаёт официантов.
     *
     * @param waitersCount      количество официантов
     * @param foodPortionsCount количество порций еды
     * @return официанты
     */
    private Waiters createWaiters(int waitersCount, int foodPortionsCount) {
        return new Waiters(waitersCount, foodPortionsCount);
    }

    /**
     * Создаёт программистов.
     *
     * @param programmersCount  количество программистов
     * @param waiters           официанты
     * @param minActionDuration минимальное время приёма пищи/дискуссии в миллисекундах
     * @param maxActionDuration максимальное время приёма пищи/дискуссии в миллисекундах
     * @return список программистиов
     */
    private List<Programmer> createProgrammers(int programmersCount,
                                               @NonNull Waiters waiters,
                                               int minActionDuration,
                                               int maxActionDuration) {
        Random random = new Random(System.currentTimeMillis());
        Lock criticalSectionLock = new ReentrantLock();

        List<Spoon> spoons = new ArrayList<>(programmersCount);
        for (int i = 0; i < programmersCount; i++) {
            spoons.add(new Spoon());
        }

        List<Programmer> programmers = new ArrayList<>(programmersCount);
        for (int i = 0; i < programmersCount; i++) {
            int firstNeighborIndex = (i - 1 + programmersCount) % programmersCount;
            int secondNeighborIndex = (i + 1) % programmersCount;

            int secondSpoonIndex = (i + 1 == programmersCount) ? 0 : i + 1;

            Programmer programmer = Programmer.builder()
                    .id(i)
                    .waiters(waiters)
                    .random(random)
                    .minActionDuration(minActionDuration)
                    .maxActionDuration(maxActionDuration)
                    .criticalSectionLock(criticalSectionLock)
                    .firstSpoon(spoons.get(i))
                    .secondSpoon(spoons.get(secondSpoonIndex))
                    .firstNeighborSupplier(() -> programmers.get(firstNeighborIndex))
                    .secondNeighborSupplier(() -> programmers.get(secondNeighborIndex))
                    .build();
            programmers.add(programmer);
        }

        return programmers;
    }

    /**
     * Симулирует обед. После окончания обеда выводит информацию о нём.
     *
     * <p>Должны быть съедены все порции еды (не больше и не меньше), все программисты должны поесть примерно одинаково
     * (максимальное значение задаётся параметром {@code maxDelta}).</p>
     *
     * @param maxDelta максимальное отклонение количества съеденных порций конкретного программиста от среднего
     *                 количества по всем программистам.
     */
    @SneakyThrows
    public void simulateLunch(int maxDelta) {
        executorService.invokeAll(programmers);
        executorService.shutdown();

        int[] totalEatenByProgrammers = programmers.stream().mapToInt(Programmer::getEatenFoodPortionsCount).toArray();
        log.info("Portions eaten by programmers: {}", totalEatenByProgrammers);

        int sum = Arrays.stream(totalEatenByProgrammers).sum();
        if (sum != foodPortionsCount) {
            log.error("The number of portions eaten is not equal to the number of servings");
            throw new IllegalStateException("It's impossible :(");
        }

        int expectedPortion = foodPortionsCount / programmers.size();
        for (int totalEatenByProgrammer : totalEatenByProgrammers) {
            if (Math.abs(expectedPortion - totalEatenByProgrammer) > maxDelta) {
                log.error("The difference between the portions is too big");
                throw new IllegalStateException("It's unfair :(");
            }
        }
    }
}
