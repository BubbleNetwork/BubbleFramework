package com.thebubblenetwork.api.framework.interaction;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.request.PlayerDataRequest;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.response.PlayerDataResponse;
import org.bukkit.scheduler.BukkitRunnable;
import sun.awt.SunToolkit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright Statement
 * ----------------------
 * Copyright (C) The Bubble Network, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Wrote by Jacob Evans <jacobevansminor@gmail.com>, 01 2016
 * <p/>
 * <p/>
 * Class information
 * ---------------------
 * Package: com.thebubblenetwork.api.framework.interaction
 * Date-created: 30/01/2016 00:19
 * Project: BubbleFramework
 */
public class DataRequestTask extends BukkitRunnable{
    private static Map<UUID,PlayerDataResponse> dataRequestTaskMap = new HashMap<>();
    private static Thread thread;

    static{
        thread = Thread.currentThread();
    }

    public static PlayerDataResponse requestAsync(UUID u) throws SunToolkit.IllegalThreadException{
        if(Thread.currentThread() == thread)throw new SunToolkit.IllegalThreadException("Not async");
        DataRequestTask task = new DataRequestTask(u);
        while(!task.isCompleted()){
        }
        return dataRequestTaskMap.remove(u);
    }

    private UUID u;
    private boolean completed = false;

    private DataRequestTask(UUID u) {
        this.u = u;
        runTask(BubbleNetwork.getInstance().getPlugin());
    }

    public void run(){
        try {
            BubbleNetwork.getInstance().getPacketHub().sendMessage(BubbleNetwork.getInstance().getProxy(),new PlayerDataRequest(getUUID()));
        } catch (IOException e) {
            dataRequestTaskMap.put(getUUID(),new PlayerDataResponse(u,new HashMap<>()));
        }
        completed = true;
    }

    public boolean isCompleted() {
        return completed;
    }

    public UUID getUUID(){
        return u;
    }
}
