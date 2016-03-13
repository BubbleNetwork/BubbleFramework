package com.thebubblenetwork.api.framework.messages.titlemanager.types;


import com.thebubblenetwork.api.framework.messages.titlemanager.AbstractTitleObject;
import com.thebubblenetwork.api.framework.messages.titlemanager.TitleType;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;

public class TimingTitle extends AbstractTitleObject{
    private TimingTicks timing;

    public TimingTitle(TimingTicks timing) {
        super(TitleType.TIMES);
        this.timing = timing;
    }

    public PacketPlayOutTitle create(){
        return new PacketPlayOutTitle(timing.getTicksIn(),timing.getTicksShow(),timing.getTicksOut());
    }
}
