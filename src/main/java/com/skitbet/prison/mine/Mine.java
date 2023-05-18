package com.skitbet.prison.mine;

import com.skitbet.prison.PrisonPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
                            block.setType(getMaterialToPlace());
                        }, 1L);
                    }
                }

                try {
                    Thread.sleep(1000L); // Using thread sleep because java is retard and wont wait for another bukkit runnable, also dont wanna waste threads
                    currentY++;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }, 1L);
    }

    public Material getMaterialToPlace() {
        Material mat = Material.STONE; // Stone is base material mines have
        for (Material minesMat : matAndChanceMap.keySet()) {
            if (shouldPlaceBlock(matAndChanceMap.get(minesMat))) mat = minesMat;
        }
        return mat;
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

    public void addBlock(Material mat, int chance) {
        matAndChanceMap.put(mat, chance);
    }

    public HashMap<Material, Integer> getMatAndChanceMap() {
        return matAndChanceMap;
    }

    public boolean hasBlock(Material material) {
        return matAndChanceMap.containsKey(material);
    }
}
