package com.explosion_wands.tick;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

public class TickQueue {
    private final Queue<Runnable> queue = new ArrayDeque<>();
    private Runnable onComplete;
    //How many entities the queue will cycle through in one tick
    private final int amountPerTick;

    public TickQueue(int amount, int amountPerTick) {
        //The amount of entities the queue will cycle through
        this.amountPerTick = amountPerTick;
    }

    public void add(Runnable task) {
        queue.add(task);
    }

    public void onComplete(Runnable callback) {
        this.onComplete = callback;
    }

    public boolean tick() {
        if(queue.isEmpty()
            ) {
            if(onComplete != null) {
                onComplete.run();
                onComplete = null;
            }
            return true;
        }

        int runsThisTick = Math.min(amountPerTick, queue.size());

        for(int i = 0; i < runsThisTick; i++) {
            Objects.requireNonNull(queue.poll()).run();
        }
        return false;
    }
}
