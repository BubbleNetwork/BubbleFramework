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
    private PunishmentData punishmentData;
    private PunishmentData beforeUpdate;

    public PlayerDataReceivedEvent(Player player, PlayerData data, PunishmentData punishmentData) {
        super(true);
        this.player = player;
        this.data = data;
        this.punishmentData = punishmentData;
        this.before = BukkitBubblePlayer.getObject(player.getUniqueId()).getData();
        this.beforeUpdate = BukkitBubblePlayer.getObject(player.getUniqueId()).getPunishmentData();
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerData getData() {
        return data;
    }

    public PunishmentData getPunishmentData() {
        return punishmentData;
    }

    public BukkitBubblePlayer getAfter(){
        return new BukkitBubblePlayer(getPlayer().getUniqueId(), getData(), getPunishmentData());
    }

    public BukkitBubblePlayer getBefore(){
        //Make sure this cannot be modified
        return new BukkitBubblePlayer(getPlayer().getUniqueId(), new PlayerData(before.getRaw()), new PunishmentData(beforeUpdate.getRaw()));
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
