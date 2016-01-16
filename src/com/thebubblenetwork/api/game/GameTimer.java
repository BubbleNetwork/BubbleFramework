package com.thebubblenetwork.api.game;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.util.java.DateUtil;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jacob on 04/01/2016.
 */
public abstract class GameTimer{
    private long start;
    private long end;
    private BukkitTask runnable;
    private int left;

    public GameTimer(int interval, int times) {
        final GameTimer instance = this;
        start = System.currentTimeMillis();
        end = getStart() + (times * interval * (1000/20));
        left = times;
        runnable = new BukkitRunnable() {
            private Date enddate = new Date(end);

            public void run() {
                if (new Date(getCurrent()).after(enddate)) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            end();
                        }
                    }.runTask(BubbleNetwork.getInstance());
                    instance.cancel();
                }
                else {
                    new BukkitRunnable() {
                        public void run() {
                            if (!instance.isCancelled()) {
                                instance.run(left);
                            }
                        }
                    }.runTask(BubbleNetwork.getInstance());
                    left--;
                }
            }
        }.runTaskTimerAsynchronously(BubbleNetwork.getInstance(), (long) interval, (long) interval);
    }

    public long getStart() {
        return start;
    }

    public long getCurrent() {
        return System.currentTimeMillis();
    }

    public long getEnd() {
        return end;
    }

    public int getLeft(){
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
        end = getCurrent();
        left = 0;
    }
}
