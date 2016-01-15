package com.thebubblenetwork.api.framework.messages.titlemanager;

import com.thebubblenetwork.api.framework.util.reflection.ReflectionUTIL;
import org.bukkit.entity.Player;
import org.spigotmc.ProtocolInjector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Jacob on 10/12/2015.
 */
public class TitleMessanger {
    private static Class<?> ichatbasecomponent, craftplayer, packet, packetplayoutchat, entityplayer,
            entityplayerconnection, chatserializer, packetinjector = ProtocolInjector.PacketTitle.class, packetaction
            = ProtocolInjector.PacketTitle.Action.class;
    ;
    private static Method geticbc, getentityplayer, sendPacket;
    private static Field playerconnectionfield;
    private static Constructor<?> packetinjectorconstructor, packetplayoutchatconstructor, packettitletimesconstructor;

    static {
        try {
            //Needed classes
            packet = ReflectionUTIL.getNMSClass("Packet");
            ichatbasecomponent = ReflectionUTIL.getNMSClass("IChatBaseComponent");
            packetplayoutchat = ReflectionUTIL.getNMSClass("PacketPlayOutChat");
            entityplayer = ReflectionUTIL.getNMSClass("EntityPlayer");
            entityplayerconnection = ReflectionUTIL.getNMSClass("PlayerConnection");
            craftplayer = ReflectionUTIL.getCraftClass("entity.CraftPlayer");

            //Classes needed for methods and fields
            chatserializer = ReflectionUTIL.getNMSClass("ChatSerializer");

            //Fields
            playerconnectionfield = ReflectionUTIL.getField(entityplayer, "playerConnection", true);

            //Methods
            geticbc = ReflectionUTIL.getMethod(chatserializer, "a", true, String.class);
            getentityplayer = ReflectionUTIL.getMethod(craftplayer, "getHandle", true);
            sendPacket = ReflectionUTIL.getMethod(entityplayerconnection, "sendPacket", true, packet);

            packetinjectorconstructor = ReflectionUTIL.getConstructor(packetinjector, true, packetaction,
                                                                      ichatbasecomponent);
            packetplayoutchatconstructor = ReflectionUTIL.getConstructor(packetplayoutchat, true, ichatbasecomponent,int.class);
            packettitletimesconstructor = ReflectionUTIL.getConstructor(packetinjector, true, packetaction, int
                    .class, int.class, int.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String toJSON(String message) {
        return "{\"text\": \"" + message + "\"}";
    }

    private static Object createComponentRaw(String json) throws Throwable {
        try {
            return ReflectionUTIL.invoke(geticbc, null, json);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    /*
        private static IChatBaseComponent createComponentRaw(String json){
            return ChatSerializer.a(json);
        }
    */
    private static Object createComponent(String messsage) throws Throwable {
        return createComponentRaw(toJSON(messsage));
    }

    /*
        private static IChatBaseComponent createComponent(String message){
            return createComponentRaw(toJSON(message));
        }
    */
    private static Object createTitle(Object chatBaseComponent, ProtocolInjector.PacketTitle.Action action) throws
            Exception {
        return packetinjectorconstructor.newInstance(action, chatBaseComponent);
    }

    /*

    private static ProtocolInjector.PacketTitle createTitle(IChatBaseComponent chatBaseComponent, ProtocolInjector
    .PacketTitle.Action action) {
        return new ProtocolInjector.PacketTitle(action, chatBaseComponent);
    }*/

    private static Object createTitle(String s, ProtocolInjector.PacketTitle.Action action) throws Throwable {
        return createTitle(createComponent(s), action);
    }

    /*
        private static ProtocolInjector.PacketTitle createTitle(String s,ProtocolInjector.PacketTitle.Action action){
            return createTitle(createComponent(s),action);
        }
    */

    private static Object createTitle(int in, int show, int out, ProtocolInjector.PacketTitle.Action action) throws
            Throwable {
        packettitletimesconstructor.setAccessible(true);
        try {
            return packettitletimesconstructor.newInstance(action, in, show, out);
        } catch (InstantiationException e) {
            throw e.getCause();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }

        return null;
    }


    private static Object createAction(Object o) throws Throwable {

        try {
            return packetplayoutchatconstructor.newInstance(o,2);
        } catch (InstantiationException e) {
            throw e.getCause();
        } catch (IllegalAccessException e) {
            //Cannot happen
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
        return null;
    }

    /*
        private static PacketPlayOutChat createAction(IChatBaseComponent chatBaseComponent){
            return new PacketPlayOutChat(chatBaseComponent);
        }
    */
    private static Object createAction(String s) throws Throwable {
        return createAction(createComponent(s));
    }

    /*
        private static PacketPlayOutChat createAction(String s){
            return createAction(createComponent(s));
        }
    */
    private static void sendPacketEntity(Object player, Object packet) throws Throwable {
        playerconnectionfield.setAccessible(true);
        try {
            ReflectionUTIL.invoke(sendPacket, playerconnectionfield.get(player), packet);
        } catch (IllegalAccessException ex) {
            //Cannot happen
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    /*
        private static void sendPacket(EntityPlayer player,Packet packet){
            player.playerConnection.sendPacket(packet);
        }
    */
    private static void sendPacketCraft(Object player, Object packet) throws Throwable {
        sendPacketEntity(ReflectionUTIL.invoke(getentityplayer, player), packet);
    }

    /*
        public static void sendPacket(CraftPlayer player,Packet packet){
            sendPacket(player.getHandle(),packet);
        }
    */
    private static void sendPacket(Player p, Object packet) throws Throwable {
        if (craftplayer.isInstance(p))
            sendPacketCraft(p, packet);
        else
            throw new Exception("Player is not instance");
    }

    /*
        public static void sendPacket(Player p,Packet packet){
            sendPacket((CraftPlayer)p,packet);
        }
    */
    public static void sendTitle(Player p, ProtocolInjector.PacketTitle.Action action, String message) throws
            Throwable {
        sendPacket(p, createTitle(message, action));
    }

    public static void sendTitle(Player p, ProtocolInjector.PacketTitle.Action action, int in, int show, int out)
            throws Throwable {
        sendPacket(p, createTitle(in, show, out, action));
    }

    /*
        public static void sendTitle(Player p,ProtocolInjector.PacketTitle.Action action,String message){
            sendPacket(p,createTitle(message,action));
        }
    */
    public static void sendActionBar(Player p, String message) throws Throwable {
        sendPacket(p, createAction(message));
    }
/*
    public static void sendActionBar(Player p,String message){
        sendPacket(p,createAction(message));
    }
*/
    /*
    public static void sendTitle(Player player, String message){
        IChatBaseComponent icbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        ProtocolInjector.PacketTitle title = new ProtocolInjector.PacketTitle(ProtocolInjector.PacketTitle.Action
        .TITLE, icbc);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(title);
    }

    public static void sendSubtitle(Player player, String message){
        IChatBaseComponent icbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        ProtocolInjector.PacketTitle subtitle = new ProtocolInjector.PacketTitle(ProtocolInjector.PacketTitle.Action
        .SUBTITLE, icbc);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(subtitle);
    }

    public static void sendAction(Player player, String message){
        IChatBaseComponent icbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat bar = new PacketPlayOutChat(icbc);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(bar);
    }
    */
}
