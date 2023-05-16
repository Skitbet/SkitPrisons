package com.skitbet.prison.mine;

import com.skitbet.prison.PrisonPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.concurrent.atomic.AtomicInteger;

public class Mine {

    private final String name;


    private final String worldName;
    private Location cornerOne;
    private Location cornerTwo;

    public Mine(String name, String worldName) {
        this.name = name;
        this.worldName = worldName;
    }

    public void setCornerOne(Location cornerOne) {
        this.cornerOne = cornerOne;
    }

    public void setCornerTwo(Location cornerTwo) {
        this.cornerTwo = cornerTwo;
    }

    public void resetMine() {

        if (cornerOne == null || cornerTwo == null) return;

        PrisonPlugin.getInstance().getServer().getScheduler().runTaskLater(PrisonPlugin.getInstance(), () -> {
            World world = cornerOne.getWorld();
            int topBlockX = Math.max(cornerOne.getBlockX(), cornerTwo.getBlockX());
            int topBlockY = Math.max(cornerOne.getBlockY(), cornerTwo.getBlockY());
            int topBlockZ = Math.max(cornerOne.getBlockZ(), cornerTwo.getBlockZ());
            int bottomBlockX = Math.min(cornerOne.getBlockX(), cornerTwo.getBlockX());
            int bottomBlockY = Math.min(cornerOne.getBlockY(), cornerTwo.getBlockY());
            int bottomBlockZ = Math.min(cornerOne.getBlockZ(), cornerTwo.getBlockZ());

            AtomicInteger currentY = new AtomicInteger(bottomBlockY);

            for (int x = bottomBlockX; x <= topBlockX; x++) {
                for (int z = bottomBlockZ; z < topBlockZ; z++) {
                    for (int y = bottomBlockY; y <= topBlockY ; y++) {
                        int finalX = x;
                        int finalZ = z;
                        System.out.println(currentY.get());
                        Block block = world.getBlockAt(finalX, currentY.get(), finalZ);
                        block.setType(Material.BEDROCK);

                        PrisonPlugin.getInstance().getServer().getScheduler()
                                .runTaskLaterAsynchronously(PrisonPlugin.getInstance(), currentY::getAndIncrement, 20L);

                    }
                }
            }
        }, 1L);


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
