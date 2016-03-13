package com.thebubblenetwork.api.framework.messages.titlemanager.types;

import com.thebubblenetwork.api.framework.messages.titlemanager.AbstractTitleObject;
import com.thebubblenetwork.api.framework.messages.titlemanager.TitleType;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;

public class ResetTitle extends AbstractTitleObject{
    public ResetTitle() {
        super(TitleType.RESET);
    }

    public PacketPlayOutTitle create(){
        return new PacketPlayOutTitle();
    }
}
