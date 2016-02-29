package com.thebubblenetwork.api.framework.util.reflection;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Jacob on 10/12/2015.
 */
public class ReflectionUTIL {
    public static String getMCVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
    }

    public static Class<?> getNMSClass(String s) throws ClassNotFoundException {
        return getClassMC("net.minecraft.server.", s);
    }

    public static Class<?> getCraftClass(String s) throws ClassNotFoundException {
        return getClassMC("org.bukkit.craftbukkit.", s);
    }

    private static Class<?> getClassMC(String s, String s2) throws ClassNotFoundException {
        return Class.forName(s + getMCVersion() + s2);
    }


    public static Field getField(Class<?> c, String field, boolean declared) throws NoSuchFieldException {
        return declared ? c.getDeclaredField(field) : c.getField(field);
    }

    public static void set(Field f, Object o, Object set) {
        f.setAccessible(true);
        try {
            f.set(o, set);
        } catch (IllegalAccessException e) {
            //Cannot happen
        }
    }

    public static Method getMethod(Class<?> c, String method, boolean declared, Class<?>... args) throws
            NoSuchMethodException {
        return declared ? c.getDeclaredMethod(method, args) : c.getMethod(method, args);
    }

    public static Object invoke(Method m, Object o, Object... methodargs) throws InvocationTargetException {
        m.setAccessible(true);
        try {
            return m.invoke(o, methodargs);
        } catch (IllegalAccessException e) {
            //Cannot happen
        }
        return null;
    }

    public static Constructor<?> getConstructor(Class<?> c, boolean declared, Class<?>... constructorargs) throws
            NoSuchMethodException {
        return declared ? c.getDeclaredConstructor(constructorargs) : c.getConstructor(constructorargs);
    }
}
