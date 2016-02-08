package com.thebubblenetwork.api.framework.interaction;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.request.PlayerDataRequest;
import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.response.PlayerDataResponse;
import org.bukkit.scheduler.BukkitRunnable;
import sun.awt.SunToolkit;

import javax.xml.crypto.Data;
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
    private static Map<String,DataRequestTask> taskMap = new HashMap<>();

    public static void setData(String name,Map<?,?> received){
        if(taskMap.containsKey(name.toLowerCase())){
            taskMap.remove(name.toLowerCase()).setResult(received);
            BubbleNetwork.getInstance().logInfo("Received pending data for " + name);
        }
        else BubbleNetwork.getInstance().logSevere("Set data for invalid player " + name);
    }

    public static Map<?,?> requestAsync(String name){
        BubbleNetwork.getInstance().logInfo("Requesting data for " + name);
        DataRequestTask task = new DataRequestTask(name);
        taskMap.put(name.toLowerCase(),task);
        task.runTask(BubbleNetwork.getInstance().getPlugin());
        while(taskMap.containsKey(name.toLowerCase())){
            
        }
        BubbleNetwork.getInstance().logInfo("Found data for " + name);
        return task.getResult();
    }

    private String name;
    private Map<?,?> result = null;

    private DataRequestTask(String name) {
        this.name = name;
    }

    public void run(){
        try {
            BubbleNetwork.getInstance().getPacketHub().sendMessage(BubbleNetwork.getInstance().getProxy(),new PlayerDataRequest(getName()));
        } catch (IOException e) {
            result = new HashMap();
        }
    }

    public Map<?,?> getResult(){
        return result;
    }

    public void setResult(Map<?,?> result){
        this.result = result;
    }

    public String getName(){
        return name;
    }
}
