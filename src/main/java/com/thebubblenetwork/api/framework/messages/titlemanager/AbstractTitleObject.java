package com.thebubblenetwork.api.framework.messages.titlemanager;

import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;

public abstract class AbstractTitleObject {
    private TitleType type;

    public AbstractTitleObject(TitleType type){
        this.type = type;
    }

    public TitleType getType(){
        return type;
    }

    public abstract PacketPlayOutTitle create();
}
