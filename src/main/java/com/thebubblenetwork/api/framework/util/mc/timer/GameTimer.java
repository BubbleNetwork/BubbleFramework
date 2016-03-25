package com.thebubblenetwork.api.framework.util.mc.timer;

import com.thebubblenetwork.api.framework.plugin.BubbleAddon;
import com.thebubblenetwork.api.game.BubbleGameAPI;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

/**
 * Created by Jacob on 04/01/2016.
 */
public abstract class GameTimer {
    private BukkitTask runnable;
    private int left;

    public GameTimer(int interval, int times, BubbleAddon addon) {
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
        runnable = addon.runTaskTimer(r, TimeUnit.MILLISECONDS, interval * 50);
        r.run();
    }

    public GameTimer(int interval, int times){
        this(interval, times, BubbleGameAPI.getInstance());
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
        if (runnable != null) {
            runnable.cancel();
        }
        runnable = null;
        left = 0;
    }
}
