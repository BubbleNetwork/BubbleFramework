package com.thebubblenetwork.api.framework.messages.titlemanager;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSTitles {
    public static String toJSON(String message) {
        return "{\"text\": \"" + message + "\"}";
    }

    public static IChatBaseComponent toICBC(String s){
        return IChatBaseComponent.ChatSerializer.a(s);
    }

    public static void sendPacket(Player p, Packet packet){
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendChat(Player p, ActionType type, String message){
        sendPacket(p,new PacketPlayOutChat(toICBC(toJSON(message)),(byte)type.getData()));
    }

    public static void sendTitle(Player p, AbstractTitleObject object){
        sendPacket(p,object.create());
    }

}
