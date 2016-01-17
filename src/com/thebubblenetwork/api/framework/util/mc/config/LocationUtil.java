package com.thebubblenetwork.api.framework.util.mc.config;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Jacob on 13/12/2015.
 */
public class LocationUtil {
    public static LocationObject fromConfig(ConfigurationSection section) {
        double x = getVar(section, "x");
        double y = getVar(section, "y");
        double z = getVar(section, "z");
        float pitch = (float) getVar(section, "pitch");
        float yaw = (float) getVar(section, "yaw");
        return new LocationObject(x, y, z, pitch, yaw);
    }

    private static double getVar(ConfigurationSection section, String var) {
        return section.getDouble(var);
    }

    public static class LocationObject {
        private double x, y, z;
        private float pitch, yaw;

        public LocationObject(double x, double y, double z, float pitch, float yaw) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.pitch = pitch;
            this.yaw = yaw;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getZ() {
            return z;
        }

        public void setZ(double z) {
            this.z = z;
        }

        public float getPitch() {
            return pitch;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
        }

        public float getYaw() {
            return yaw;
        }

        public void setYaw(float yaw) {
            this.yaw = yaw;
        }

        public void add(double x,double y,double z){
            setX(getX() + x);
            setY(getY() + y);
            setZ(getZ() + z);
        }

        public void subtract(double x,double y,double z){
            setX(getX() - x);
            setY(getY() - y);
            setZ(getZ() - z);
        }

        public Location toLocation(World w) {
            return new Location(w, getX(), getY(), getZ(), getPitch(), getYaw());
        }
    }
}
