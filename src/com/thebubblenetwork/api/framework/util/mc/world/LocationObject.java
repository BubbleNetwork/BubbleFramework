package com.thebubblenetwork.api.framework.util.mc.world;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Copyright Statement
 * ----------------------
 * Copyright (C) The Bubble Network, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Wrote by Jacob Evans <jacobevansminor@gmail.com>, 01 2016
 *
 *
 * Class information
 * ---------------------
 * Package: com.thebubblenetwork.api.framework.util.mc.world
 * Date-created: 17/01/2016 11:07
 * Project: BubbleFramework
 */
public class LocationObject implements Cloneable{
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


    public Location toLocation(World w) {
        return new Location(w, getX(), getY(), getZ(), getPitch(), getYaw());
    }

    @Override
    public LocationObject clone(){
        return new LocationObject(getX(),getY(),getZ(),getPitch(),getYaw());
    }
}
