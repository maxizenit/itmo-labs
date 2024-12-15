package com.vk.itmo.lunchsimulator;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Ложка. Класс необходим для демонстрации отсутствия конфликтов взятия ложек.
 */
public class Spoon {

    /**
     * {@link Lock}, обеспечивающий одновременное пользование ложкой только одним программистом.
     */
    private final Lock lock = new ReentrantLock();

    /**
     * Берёт ложку (блокирует).
     */
    public void take() {
        lock.lock();
    }

    /**
     * Возвращает ложку (разблокирует).
     */
    public void put() {
        lock.unlock();
    }
}
