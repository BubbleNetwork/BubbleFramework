package com.thebubblenetwork.api.framework.event;

import com.thebubblenetwork.api.global.bubblepackets.messaging.messages.response.ServerListResponse;
import com.thebubblenetwork.api.global.type.ServerType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class ServerListUpdateEvent extends Event{
    private static HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    private ServerType type;
    private List<ServerListResponse.EncapsulatedServer> servers;

    public ServerListUpdateEvent(ServerType type, List<ServerListResponse.EncapsulatedServer> servers) {
        this.type = type;
        this.servers = servers;
    }

    public ServerType getType() {
        return type;
    }

    public List<ServerListResponse.EncapsulatedServer> getServers() {
        return servers;
    }

    public HandlerList getHandlers(){
        return handlerList;
    }
}
