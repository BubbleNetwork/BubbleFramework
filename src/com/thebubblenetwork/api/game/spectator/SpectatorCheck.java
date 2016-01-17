package com.thebubblenetwork.api.game.spectator;

import com.thebubblenetwork.api.game.GameListener;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.Callable;

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
 * Package: com.thebubblenetwork.api.game.spectator
 * Date-created: 17/01/2016 19:46
 * Project: BubbleFramework
 */
public class SpectatorCheck
    implements Callable<Object>
{
    private UUID u;
    public SpectatorCheck(UUID u){
        this.u = u;
    }
    public SpectatorCheck(Player p){
        this(p.getUniqueId());
    }

    public Boolean call(){
        return GameListener.isSpectating(u);
    }
}
