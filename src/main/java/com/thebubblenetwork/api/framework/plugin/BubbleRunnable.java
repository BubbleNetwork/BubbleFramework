package com.thebubblenetwork.api.framework.plugin;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public abstract class BubbleRunnable implements Runnable{
    public BukkitTask runTask(BubbleAddon plugin){
        return BubbleNetwork.getInstance().registerRunnable(plugin,this, TimeUnit.MILLISECONDS,0L,false,false);
    }

    public BukkitTask runTaskAsynchonrously(BubbleAddon plugin){
        return BubbleNetwork.getInstance().registerRunnable(plugin,this, TimeUnit.MILLISECONDS,0L,false,true);
    }

    public BukkitTask runTaskLater(BubbleAddon plugin, TimeUnit unit, long time){
        return BubbleNetwork.getInstance().registerRunnable(plugin,this, unit,time,false,false);
    }

    public BukkitTask runTaskLaterAsynchronously(BubbleAddon plugin, TimeUnit unit, long time){
        return BubbleNetwork.getInstance().registerRunnable(plugin,this, unit,time,false,true);
    }
    public BukkitTask runTaskTimer(BubbleAddon plugin, TimeUnit unit, long time){
        return BubbleNetwork.getInstance().registerRunnable(plugin,this, unit,time,true,false);
    }

    public BukkitTask runTaskTimerAsynchronously(BubbleAddon plugin, TimeUnit unit, long time){
        return BubbleNetwork.getInstance().registerRunnable(plugin,this, unit,time,true,true);
    }
}
