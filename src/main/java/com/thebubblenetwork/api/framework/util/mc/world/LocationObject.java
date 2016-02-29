package com.thebubblenetwork.api.framework.util.mc.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.NumberConversions;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright Statement
 * ----------------------
 * Copyright (C) The Bubble Network, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Wrote by Jacob Evans <jacobevansminor@gmail.com>, 01 2016
 * <p>
 * <p>
 * Class information
 * ---------------------
 * Package: com.thebubblenetwork.api.framework.util.mc.world
 * Date-created: 17/01/2016 11:07
 * Project: BubbleFramework
 */
public class LocationObject implements Cloneable,ConfigurationSerializable {
    private double x, y, z;
    private float pitch, yaw;

    public LocationObject(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
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

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public LocationObject add(double x, double y, double z) {
        setX(getX() + x);
        setY(getY() + y);
        setZ(getZ() + z);
        return this;
    }

    public LocationObject subtract(double x, double y, double z) {
        setX(getX() - x);
        setY(getY() - y);
        setZ(getZ() - z);
        return this;
    }

    public Location toLocation(World w) {
        return new Location(w, getX(), getY(), getZ(), getPitch(), getYaw());
    }

    public Map<String, Object> serialize() {
        Map<String,Object> map = new HashMap<>();
        map.put("x",getX());
        map.put("y",getY());
        map.put("z",getZ());
        map.put("yaw",getYaw());
        map.put("pitch",getPitch());
        return map;
    }

    public static LocationObject deserialize(Map<String, Object> args){
        return new LocationObject(
                NumberConversions.toDouble(args.get("x")),
                NumberConversions.toDouble(args.get("y")),
                NumberConversions.toDouble(args.get("z")),
                NumberConversions.toFloat(args.get("yaw")),
                NumberConversions.toFloat(args.get("pitch"))
        );
    }

    @Override
    public String toString(){
        String contents = "";
        for(Map.Entry<String,Object> objectEntry:serialize().entrySet()){
            contents += " " + objectEntry.getKey() + "=" + String.valueOf(((Number)objectEntry.getValue()).intValue());
        }
        return "[" + contents + " ]";
    }


    @Override
    public LocationObject clone() {
        return new LocationObject(getX(), getY(), getZ(), getYaw(), getPitch());
    }
}
