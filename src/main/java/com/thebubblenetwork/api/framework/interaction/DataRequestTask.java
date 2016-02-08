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

    public static Map<?,?> requestAsync(Map<String,Map<?,?>> place,String name){
        BubbleNetwork.getInstance().logInfo("Requesting data for " + name);
        DataRequestTask task = new DataRequestTask(place,name);
        while(!task.isCompleted()){
        }
        return place.remove(name);
    }

    private String name;
    private Map<String,Map<?,?>> place;

    private DataRequestTask(Map<String,Map<?,?>> place,String name) {
        this.name = name;
        this.place = place;
        runTask(BubbleNetwork.getInstance().getPlugin());
    }

    public void run(){
        try {
            BubbleNetwork.getInstance().getPacketHub().sendMessage(BubbleNetwork.getInstance().getProxy(),new PlayerDataRequest(getName()));
        } catch (IOException e) {
            place.put(getName(),new HashMap());
        }
    }

    public boolean isCompleted() {
        return place.containsKey(name);
    }

    public String getName(){
        return name;
    }
}
