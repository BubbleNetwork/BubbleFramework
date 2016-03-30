package com.thebubblenetwork.api.framework.event;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.checks.access.IViolationInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerViolationSendEvent extends Event implements Cancellable{
    private static HandlerList handlerList = new HandlerList();


    private Player player;
    private boolean cancelled = false;
    private CheckType type;
    private IViolationInfo info;

    public PlayerViolationSendEvent(Player player, CheckType type, IViolationInfo info){
        this.player = player;
        this.type = type;
        this.info = info;
    }

    public Player getPlayer() {
        return player;
    }

    public IViolationInfo getInfo() {
        return info;
    }

    public CheckType getType() {
        return type;
    }

    public boolean isCancelled(){
        return cancelled;
    }

    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers(){
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
