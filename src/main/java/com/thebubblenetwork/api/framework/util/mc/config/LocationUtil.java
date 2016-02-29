package com.thebubblenetwork.api.framework.util.mc.config;

import com.thebubblenetwork.api.framework.util.mc.world.LocationObject;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Jacob on 13/12/2015.
 */
public class LocationUtil {
    public static LocationObject fromConfig(ConfigurationSection section) {
        if(section == null)throw new IllegalArgumentException("Section cannot be null");
        double x = getVar(section, "x");
        double y = getVar(section, "y");
        double z = getVar(section, "z");
        float pitch = (float) getVar(section, "pitch");
        float yaw = (float) getVar(section, "yaw");
        return new LocationObject(x, y, z, yaw, pitch);
    }

    private static double getVar(ConfigurationSection section, String var) {
        return section.getDouble(var);
    }
}
