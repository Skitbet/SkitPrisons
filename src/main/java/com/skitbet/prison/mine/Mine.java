package com.skitbet.prison.mine;

import com.skitbet.prison.PrisonPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Mine {

    private final String name;
    private final Map<Material, Integer> materialAndChanceMap;
    private final String worldName;

    private Location cornerOne;
    private Location cornerTwo;

    // Constructor to set up a mine with a name and world
    public Mine(String name, String worldName) {
        this.name = name;
        this.worldName = worldName;
        this.materialAndChanceMap = new HashMap<>();
    }

    // Set the first corner for the mine (corner 1)
    public void setCornerOne(Location cornerOne) {
        this.cornerOne = cornerOne;
    }

    // Set the second corner for the mine (corner 2)
    public void setCornerTwo(Location cornerTwo) {
        this.cornerTwo = cornerTwo;
    }

    // Reset the mine area, replacing all blocks within the defined corners
    public void resetMine() {
        if (cornerOne == null || cornerTwo == null) {
            return;  // Make sure both corners are set before we try to reset the mine
        }

        // Schedule the task to reset the mine after a short delay
        PrisonPlugin.getInstance().getServer().getScheduler().runTaskLater(PrisonPlugin.getInstance(), () -> {
            World world = cornerOne.getWorld();
            if (world == null) return;

            // Get the coordinates of the "top" and "bottom" corners based on the player's selection
            int topBlockX = Math.max(cornerOne.getBlockX(), cornerTwo.getBlockX());
            int topBlockY = Math.max(cornerOne.getBlockY(), cornerTwo.getBlockY());
            int topBlockZ = Math.max(cornerOne.getBlockZ(), cornerTwo.getBlockZ());
            int bottomBlockX = Math.min(cornerOne.getBlockX(), cornerTwo.getBlockX());
            int bottomBlockY = Math.min(cornerOne.getBlockY(), cornerTwo.getBlockY());
            int bottomBlockZ = Math.min(cornerOne.getBlockZ(), cornerTwo.getBlockZ());

            // Loop through all the blocks in the selection area
            for (int y = bottomBlockY; y <= topBlockY; y++) {
                for (int x = bottomBlockX; x <= topBlockX; x++) {
                    for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                        Block block = world.getBlockAt(x, y, z);
                        // Schedule the block change
                        PrisonPlugin.getInstance().getServer().getScheduler().runTask(PrisonPlugin.getInstance(), () -> {
                            block.setType(getMaterialToPlace());
                        });
                    }
                }
            }
        }, 1L);
    }

    // Randomly choose the material to place based on the chances in materialAndChanceMap
    private Material getMaterialToPlace() {
        Material baseMaterial = Material.STONE;  // Default material
        // Go through each material and chance to see if it should be placed
        for (Map.Entry<Material, Integer> entry : materialAndChanceMap.entrySet()) {
            if (shouldPlaceBlock(entry.getValue())) {
                baseMaterial = entry.getKey();
            }
        }
        return baseMaterial;
    }

    // Check if a block should be placed based on its chance
    private boolean shouldPlaceBlock(int chance) {
        return new Random().nextInt(100) >= chance;  // If the random number is greater than or equal to the chance
    }

    // Getter for the mine's name
    public String getName() {
        return name;
    }

    // Getter for the first corner location
    public Location getCornerOne() {
        return cornerOne;
    }

    // Getter for the second corner location
    public Location getCornerTwo() {
        return cornerTwo;
    }

    // Add a block material with a chance for the mine
    public void addBlock(Material material, int chance) {
        materialAndChanceMap.put(material, chance);
    }

    // Getter for the material and chance map
    public Map<Material, Integer> getMaterialAndChanceMap() {
        return materialAndChanceMap;
    }

    // Check if the mine has a certain material
    public boolean hasBlock(Material material) {
        return materialAndChanceMap.containsKey(material);
    }
}
