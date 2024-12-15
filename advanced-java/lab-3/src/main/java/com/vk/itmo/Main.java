package com.vk.itmo;

import com.vk.itmo.lunchsimulator.LunchSimulator;
import lombok.NonNull;

public class Main {

    public static void main(@NonNull String[] args) {
        int programmersCount = Integer.parseInt(args[0]);
        int waitersCount = Integer.parseInt(args[1]);
        int foodPortionsCount = Integer.parseInt(args[2]);
        int maxDelta = Integer.parseInt(args[3]);
        int minActionDuration = Integer.parseInt(args[4]);
        int maxActionDuration = Integer.parseInt(args[5]);

        LunchSimulator lunchSimulator = new LunchSimulator(programmersCount,
                waitersCount,
                foodPortionsCount,
                minActionDuration,
                maxActionDuration);
        lunchSimulator.simulateLunch(maxDelta);
    }
}