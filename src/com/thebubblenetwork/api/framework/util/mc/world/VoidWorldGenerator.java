package com.thebubblenetwork.api.framework.util.mc.world;

import com.thebubblenetwork.api.framework.BubbleNetwork;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 21/12/2015.
 */
public class VoidWorldGenerator extends ChunkGenerator implements Cloneable {
    private static VoidWorldGenerator generator;

    private VoidWorldGenerator() {
    }

    public static String getName() {
        return BubbleNetwork.getInstance().getDescription().getName();
    }

    public static VoidWorldGenerator getGenerator() {
        if (generator == null)
            generator = new VoidWorldGenerator();
        return generator.clone();
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList(new BlockPopulator[0]);
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    @Override
    public byte[] generate(World world, Random rand, int chunkx, int chunkz) {
        return new byte['耀'];
    }


    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0.0D, 50.0D, 0.0D);
    }

    @Override
    public VoidWorldGenerator clone() {
        return new VoidWorldGenerator();
    }
}
