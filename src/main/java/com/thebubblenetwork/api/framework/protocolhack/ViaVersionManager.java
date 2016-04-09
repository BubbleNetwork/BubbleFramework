package com.thebubblenetwork.api.framework.protocolhack;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.util.plugindownloader.AbstractPluginDownloader;

import java.io.File;

/**
 * The Bubble Network 2016
 * BubbleFramework
 * 09/04/2016 {08:54}
 * Created April 2016
 */
public class ViaVersionManager extends AbstractPluginDownloader{
    public ViaVersionManager() {
        super(BubbleNetwork.getInstance(), "ViaVersion.jar", new File(BubbleNetwork.getInstance().getPlugin().getDataFolder(),"ViaVersion.jar"));
    }

    public Unsafe unsafe(){
        return new Unsafe() {
            public ViaHook getHook() {
                if(getPlugin() != null && getPlugin().isEnabled())return new ViaHook(getPlugin());
                throw new IllegalArgumentException("Plugin is not enabled");
            }
        };
    }

    interface Unsafe{
        ViaHook getHook();
    }
}
