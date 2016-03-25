package com.thebubblenetwork.api.framework.plugin.util;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.plugin.BubbleAddon;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public abstract class BubbleRunnable implements Runnable {
    private BukkitTask task = null;

    private void taskCheck() {
        if (task != null) {
            throw new IllegalArgumentException("Already running");
        }
    }

    public BukkitTask runTask(BubbleAddon plugin) {
        taskCheck();
        return task = BubbleNetwork.getInstance().registerRunnable(plugin, this, TimeUnit.MILLISECONDS, 0L, false, false);
    }

    public BukkitTask runTaskAsynchonrously(BubbleAddon plugin) {
        taskCheck();
        return task = BubbleNetwork.getInstance().registerRunnable(plugin, this, TimeUnit.MILLISECONDS, 0L, false, true);
    }

    public BukkitTask runTaskLater(BubbleAddon plugin, TimeUnit unit, long time) {
        taskCheck();
        return task = BubbleNetwork.getInstance().registerRunnable(plugin, this, unit, time, false, false);
    }

    public BukkitTask runTaskLaterAsynchronously(BubbleAddon plugin, TimeUnit unit, long time) {
        taskCheck();
        return task = BubbleNetwork.getInstance().registerRunnable(plugin, this, unit, time, false, true);
    }

    public BukkitTask runTaskTimer(BubbleAddon plugin, TimeUnit unit, long time) {
        taskCheck();
        return task = BubbleNetwork.getInstance().registerRunnable(plugin, this, unit, time, true, false);
    }

    public BukkitTask runTaskTimerAsynchronously(BubbleAddon plugin, TimeUnit unit, long time) {
        taskCheck();
        return task = BubbleNetwork.getInstance().registerRunnable(plugin, this, unit, time, true, true);
    }

    public BukkitTask getTask() {
        return task;
    }

    public void cancel() {
        task.cancel();
    }
}
