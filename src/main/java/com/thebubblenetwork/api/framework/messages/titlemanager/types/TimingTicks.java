package com.thebubblenetwork.api.framework.messages.titlemanager.types;

import org.apache.commons.lang.Validate;

import java.util.concurrent.TimeUnit;

public class TimingTicks {
    private static int wrap(TimeUnit unit, long n){
        long ticks = unit.toMillis(n)/50L;
        Validate.isTrue(ticks > 0);
        if(ticks > Integer.MAX_VALUE)return Integer.MAX_VALUE;
        return (int)ticks;
    }

    private int in,show,out;

    public TimingTicks(TimeUnit unit, long in, long show, long out){
        this.in = wrap(unit,in);
        this.show = wrap(unit,show);
        this.out = wrap(unit,out);
    }

    public int getTicksIn() {
        return in;
    }

    public int getTicksShow() {
        return show;
    }

    public int getTicksOut() {
        return out;
    }
}
