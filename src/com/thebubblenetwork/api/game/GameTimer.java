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
    private long start = System.currentTimeMillis();
    private long end;
    private BukkitTask runnable;
    private int left;

    public GameTimer(int interval, final long end) {
        this.end = end;
        final GameTimer instance = this;
        left = (int)((DateUtil.getDateDiff(new Date(getStart()), new Date(getEnd()), TimeUnit.MILLISECONDS) / (long)(1000 / 20))/interval);
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
                else
                    new BukkitRunnable() {
                        public void run() {
                            if (!instance.isCancelled()) {
                                instance.run(left);
                                instance.left--;
                            }
                        }
                    }.runTask(BubbleNetwork.getInstance());
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
