package com.thebubblenetwork.api.framework;

import com.thebubblenetwork.api.framework.util.mc.world.VoidWorldGenerator;
import com.thebubblenetwork.api.global.file.DownloadUtil;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

/**
 * Copyright Statement
 * ----------------------
 * Copyright (C) The Bubble Network, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Wrote by Jacob Evans <jacobevansminor@gmail.com>, 01 2016
 * <p/>
 * <p/>
 * Class information
 * ---------------------
 * Package: com.thebubblenetwork.api.framework
 * Date-created: 29/01/2016 17:25
 * Project: BubbleFramework
 */
public class P extends JavaPlugin {
    private BubbleNetwork network;

    public P() {
        super();
        network = new BubbleNetwork(this);
    }

    public void onLoad() {
        network.onLoad();
    }

    public void onEnable() {
        network.onEnable();
    }

    public void onDisable() {
        try {
            DownloadUtil.download(getFile(), "BubbleFramework.jar", network.getFileConnection());
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Could not update ", e);
        }
        network.onDisable();
    }

    public ChunkGenerator getDefaultWorldGenerator(String s1, String s2) {
        return VoidWorldGenerator.getGenerator();
    }

    public File getFile() {
        return super.getFile();
    }
}
