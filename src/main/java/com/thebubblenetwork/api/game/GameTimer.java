package com.thebubblenetwork.api.game;

import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

/**
 * Created by Jacob on 04/01/2016.
 */
public abstract class GameTimer {
    private BukkitTask runnable;
    private int left;

    public GameTimer(int interval, int times) {
        left = times;
        Runnable r = new Runnable() {
            public void run() {
                if (getLeft() == 0) {
                    GameTimer.this.cancel();
                    end();
                    return;
                }
                GameTimer.this.run(getLeft());
                left--;
            }
        };
        runnable = BubbleGameAPI.getInstance().runTaskTimer(r, TimeUnit.MILLISECONDS,interval*50);
        r.run();
    }

    public int getLeft() {
        return left;
    }

    public abstract void run(int left);

    public abstract void end();

    public boolean isCancelled() {
        return runnable == null;
    }

    public void cancel() {
        if(runnable != null)runnable.cancel();
        runnable = null;
        left = 0;
    }
}
