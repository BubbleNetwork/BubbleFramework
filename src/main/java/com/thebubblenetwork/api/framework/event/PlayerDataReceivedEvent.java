package com.thebubblenetwork.api.framework.event;

import com.thebubblenetwork.api.framework.player.BukkitBubblePlayer;
import com.thebubblenetwork.api.global.data.PlayerData;
import com.thebubblenetwork.api.global.data.PunishmentData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class PlayerDataReceivedEvent extends Event{
    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private PlayerData data;
    private PlayerData before;

    public PlayerDataReceivedEvent(Player player, PlayerData data) {
        super(true);
        this.player = player;
        this.data = data;
        this.before = BukkitBubblePlayer.getObject(player.getUniqueId()).getData();
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerData getData() {
        return data;
    }

    public BukkitBubblePlayer getAfter(){
        return new BukkitBubblePlayer(getPlayer().getUniqueId(), getData());
    }

    public BukkitBubblePlayer getBefore(){
        //Make sure this cannot be modified
        return new BukkitBubblePlayer(getPlayer().getUniqueId(), new PlayerData(before.getRaw()));
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
