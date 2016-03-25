package com.thebubblenetwork.api.event;

import com.thebubblenetwork.api.framework.player.BukkitBubblePlayer;
import com.thebubblenetwork.api.global.data.PlayerData;
import com.thebubblenetwork.api.global.player.BubblePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class PlayerDataReceivedEvent extends Event{
    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private PlayerData data;

    public PlayerDataReceivedEvent(Player player, PlayerData data) {
        super(true);
        this.player = player;
        this.data = data;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerData getData() {
        return data;
    }

    public BubblePlayer getFake(){
        return new BukkitBubblePlayer(getPlayer().getUniqueId(), getData());
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
