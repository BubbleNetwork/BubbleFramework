package com.thebubblenetwork.api.framework.messages.titlemanager;

import com.thebubblenetwork.api.framework.util.reflection.ReflectionUTIL;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jacob on 10/12/2015.
 */
public class TitleMessanger {
    private static String toJSON(String message) {
        return "{\"text\": \"" + message + "\"}";
    }

    private static Object createComponentRaw(String json) throws Throwable {
        try {
            return ReflectionUTIL.invoke(getICBC, null, json);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private static Object createComponent(String messsage) throws Throwable {
        return createComponentRaw(toJSON(messsage));
    }

    private static Object createTitleTiming(Object action, Object ichatbasecomponent, int in, int show, int out) throws Throwable {
        PacketPlayOutTitleConstructorTiming.setAccessible(true);
        try {
            return PacketPlayOutTitleConstructorTiming.newInstance(action, ichatbasecomponent, in, show, out);
        } catch (InstantiationException e) {
            throw e.getCause();
        } catch (IllegalAccessException e) {
            //Cannot happen
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }

        return null;
    }

    private static Object createTitleNormal(Object action, Object ichatbasecomponent) throws Throwable {
        PacketPlayOutTitleConstructorNormal.setAccessible(true);
        try {
            return PacketPlayOutTitleConstructorNormal.newInstance(action, ichatbasecomponent);
        } catch (InstantiationException e) {
            throw e.getCause();
        } catch (IllegalAccessException e) {
            //Cannot happen
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }

        return null;
    }

    private static Object createTitle(String s, Object action, int in, int show, int out) throws Throwable {
        return createTitleTiming(createComponent(s), action, in, show, out);
    }

    private static Object createTitle(String s, Object action) throws Throwable {
        return createTitleNormal(createComponent(s), action);
    }

    private static Object createAction(Object o) throws Throwable {

        try {
            return PacketPlayOutChatConstructor.newInstance(o, 2);
        } catch (InstantiationException e) {
            throw e.getCause();
        } catch (IllegalAccessException e) {
            //Cannot happen
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
        return null;
    }


    private static Object createAction(String s) throws Throwable {
        return createAction(createComponent(s));
    }


    private static void sendPacketEntity(Object player, Object packet) throws Throwable {
        playerconnectionField.setAccessible(true);
        try {
            ReflectionUTIL.invoke(sendPacket, playerconnectionField.get(player), packet);
        } catch (IllegalAccessException ex) {
            //Cannot happen
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    private static void sendPacketCraft(Object player, Object packet) throws Throwable {
        sendPacketEntity(ReflectionUTIL.invoke(getHandle, player), packet);
    }


    private static void sendPacket(Player p, Object packet) throws Throwable {
        if (CraftPlayer.isInstance(p)) {
            sendPacketCraft(p, packet);
        } else {
            throw new IllegalArgumentException("Player is not instance");
        }
    }


    public static void sendTitle(Player p, String message, ProtcolInjectorReflection action, int in, int show, int out) throws Throwable {
        sendPacket(p, createTitle(message, action.get(), in, show, out));
    }

    public static void sendTitleNoTiming(Player p, String message, ProtcolInjectorReflection action) throws Throwable {
        sendPacket(p, createTitle(message, action.get()));
    }

    public static void sendActionBar(Player p, String message) throws Throwable {
        sendPacket(p, createAction(message));
    }

    private static Class<?> IChatBaseComponent, CraftPlayer, Packet, PacketPlayOutChat, PacketPlayOutTitle, EntityPlayer, PlayerConnection, ChatSerializer, EnumTitleAction;
    private static Method getICBC, getHandle, sendPacket;
    private static Field playerconnectionField, TITLE, SUBTITLE, CLEAR;
    private static Constructor<?> PacketPlayOutTitleConstructorTiming, PacketPlayOutChatConstructor, PacketPlayOutTitleConstructorNormal;

    static {
        try {
            //Needed classes
            Packet = ReflectionUTIL.getNMSClass("Packet");
            IChatBaseComponent = ReflectionUTIL.getNMSClass("IChatBaseComponent");
            PacketPlayOutChat = ReflectionUTIL.getNMSClass("PacketPlayOutChat");
            EntityPlayer = ReflectionUTIL.getNMSClass("EntityPlayer");
            PlayerConnection = ReflectionUTIL.getNMSClass("PlayerConnection");
            CraftPlayer = ReflectionUTIL.getCraftClass("entity.CraftPlayer");
            EnumTitleAction = ReflectionUTIL.getNMSClass("PacketPlayOutTitle$EnumTitleAction");
            PacketPlayOutTitle = ReflectionUTIL.getNMSClass("PacketPlayOutTitle");

            //Classes needed for methods and fields
            ChatSerializer = ReflectionUTIL.getNMSClass("IChatBaseComponent$ChatSerializer");

            //Fields
            playerconnectionField = ReflectionUTIL.getField(EntityPlayer, "playerConnection", true);
            TITLE = ReflectionUTIL.getField(EnumTitleAction, "TITLE", true);
            SUBTITLE = ReflectionUTIL.getField(EnumTitleAction, "SUBTITLE", true);
            CLEAR = ReflectionUTIL.getField(EnumTitleAction, "CLEAR", true);

            //Methods
            getICBC = ReflectionUTIL.getMethod(ChatSerializer, "a", true, String.class);
            getHandle = ReflectionUTIL.getMethod(CraftPlayer, "getHandle", true);
            sendPacket = ReflectionUTIL.getMethod(PlayerConnection, "sendPacket", true, Packet);

            PacketPlayOutChatConstructor = ReflectionUTIL.getConstructor(PacketPlayOutChat, true, IChatBaseComponent, int.class);
            PacketPlayOutTitleConstructorTiming = ReflectionUTIL.getConstructor(PacketPlayOutTitle, true, EnumTitleAction, IChatBaseComponent, int.class, int.class, int.class);
            PacketPlayOutTitleConstructorNormal = ReflectionUTIL.getConstructor(PacketPlayOutTitle, true, EnumTitleAction, IChatBaseComponent);
        } catch (Throwable ex) {
            Logger.getGlobal().log(Level.WARNING, "Could not setup titlemanager", ex);
        }
    }

    public enum ProtcolInjectorReflection {
        TITLE, SUBTITLE, CLEAR;

        protected Object get() throws IllegalAccessException {
            switch (this) {
                case TITLE:
                    TitleMessanger.TITLE.setAccessible(true);
                    return TitleMessanger.TITLE.get(null);
                case SUBTITLE:
                    TitleMessanger.SUBTITLE.setAccessible(true);
                    return TitleMessanger.SUBTITLE.get(null);
                case CLEAR:
                    TitleMessanger.CLEAR.setAccessible(true);
                    return TitleMessanger.CLEAR.get(null);
                default:
                    return null;
            }
        }
    }
}
