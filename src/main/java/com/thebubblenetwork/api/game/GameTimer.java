package com.thebubblenetwork.api.game;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Created by Jacob on 04/01/2016.
 */
public abstract class GameTimer {
    private BukkitTask runnable;
    private int left;

    public GameTimer(int interval, int times) {
        left = times;
        runnable = new BukkitRunnable() {
            public void run() {
                if (getLeft() == 0) {
                    GameTimer.this.cancel();
                    end();
                }
                GameTimer.this.run(getLeft());
                left--;
            }
        }.runTaskTimer(BubbleNetwork.getInstance().getPlugin(), 0L, (long) interval);
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
        runnable.cancel();
        runnable = null;
        left = 0;
    }
}
