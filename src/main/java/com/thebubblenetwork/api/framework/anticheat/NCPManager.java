package com.thebubblenetwork.api.framework.anticheat;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import com.thebubblenetwork.api.framework.util.plugindownloader.AbstractPluginDownloader;
import com.thebubblenetwork.api.global.file.DownloadUtil;
import com.thebubblenetwork.api.global.file.FileUTIL;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class NCPManager extends AbstractPluginDownloader{
    private CheatHandle handle;
    private File directory;
    private File config;

    public NCPManager(BubbleNetwork network) {
        super(network, "NoCheatPlus.jar", new File(network.getPlugin().getDataFolder().getParent(), "NoCheatPlus.jar"));
        directory = new File(getJar().getParent(), "NoCheatPlus");
        config = new File(directory, "config.yml");
    }

    @Override
    public void load() {
        if(!directory.exists()){
            directory.mkdir();
        }
        try {
            DownloadUtil.download(config, "ncpconfig.yml", getNetwork().getFileConnection());
        } catch (Exception e) {
            getNetwork().getLogger().log(Level.WARNING, "Failed to download AntiCheat configuration", e);
        }
        super.load();
    }

    @Override
    public void enable() {
        super.enable();
        handle = new CheatHandle(getPlugin());
    }

    @Override
    public void disable() {
        handle = null;
        super.disable();
    }

    @Override
    public void clearUp() {
        FileUTIL.deleteDir(directory);
        super.clearUp();
    }

    public Unsafe getUnsafe(){
        return new Unsafe() {
            public CheatHandle getHandle() {
                if(handle == null)throw new IllegalArgumentException("Handle not found");
                return handle;
            }
        };
    }

    interface Unsafe{
        CheatHandle getHandle();
    }
}
