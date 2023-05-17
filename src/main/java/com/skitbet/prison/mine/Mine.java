package com.skitbet.prison.mine;

import com.skitbet.prison.PrisonPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Mine {

    private final String name;

    private HashMap<Material, Integer> matAndChanceMap;

    private final String worldName;
    private Location cornerOne;
    private Location cornerTwo;

    public Mine(String name, String worldName) {
        this.name = name;
        this.worldName = worldName;

        matAndChanceMap = new HashMap<>();
    }

    public void setCornerOne(Location cornerOne) {
        this.cornerOne = cornerOne;
    }

    public void setCornerTwo(Location cornerTwo) {
        this.cornerTwo = cornerTwo;
    }

    public void resetMine() {

        if (cornerOne == null || cornerTwo == null) return;

        PrisonPlugin.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(PrisonPlugin.getInstance(), () -> {
            World world = cornerOne.getWorld();
            int topBlockX = Math.max(cornerOne.getBlockX(), cornerTwo.getBlockX());
            int topBlockY = Math.max(cornerOne.getBlockY(), cornerTwo.getBlockY());
            int topBlockZ = Math.max(cornerOne.getBlockZ() + 1, cornerTwo.getBlockZ() + 1) ;
            int bottomBlockX = Math.min(cornerOne.getBlockX(), cornerTwo.getBlockX());
            int bottomBlockY = Math.min(cornerOne.getBlockY(), cornerTwo.getBlockY());
            int bottomBlockZ = Math.min(cornerOne.getBlockZ(), cornerTwo.getBlockZ());

            int currentY = bottomBlockY;

            for (int y = bottomBlockY; y <= topBlockY ; y++) {
                for (int x = bottomBlockX; x <= topBlockX; x++) {
                    for (int z = bottomBlockZ; z < topBlockZ; z++) {
                        int finalZ = z;
                        int finalX = x;
                        int finalCurrentY = currentY;
                        PrisonPlugin.getInstance().getServer().getScheduler().runTaskLater(PrisonPlugin.getInstance(), () -> {
                            Block block = world.getBlockAt(finalX, finalCurrentY, finalZ);
                            block.setType(Material.BEDROCK);
                        }, 1L);
                    }
                }

                try {
                    Thread.sleep(1000L);
                    currentY++;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }, 1L);
    }

    public boolean shouldPlaceBlock(int chance) {
        return new Random().nextInt(100) >= chance;
    }

    public String getName() {
        return name;
    }

    public Location getCornerOne() {
        return cornerOne;
    }

    public Location getCornerTwo() {
        return cornerTwo;
    }
}
