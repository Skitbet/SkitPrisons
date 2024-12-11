package com.skitbet.prison.mine;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MineManager {

    // The special wand used for editing mines
    public static final ItemStack EDIT_WAND = createEditWand();

    // Maps players to the mines they're currently editing
    private final Map<UUID, Mine> editingPlayers = new HashMap<>();
    // Holds all the mines in the world
    private final List<Mine> mines = new ArrayList<>();

    // Start the creation of a new mine and give the player the wand
    public Mine startMineCreation(String name, Player player) {
        Mine mine = new Mine(name, player.getWorld().getName());
        editingPlayers.put(player.getUniqueId(), mine);

        player.getInventory().addItem(createEditWand());
        player.sendMessage("§aMine creation started: §e" + name);
        player.sendMessage("§aSelect 2 corners with the wand to define the mine area.");

        return mine;
    }

    // Switch a player to edit mode for an existing mine
    public void enterEditMode(Player player, Mine mine) {
        if (mine == null) {
            player.sendMessage("§cMine not found!");
            return;
        }

        // Remove the mine from the list, start editing it
        mines.remove(mine);
        editingPlayers.put(player.getUniqueId(), mine);

        player.getInventory().addItem(createEditWand());
        player.sendMessage("§aEdit mode activated for mine: §e" + mine.getName());
        player.sendMessage("§aSelect 2 corners with the wand to redefine the mine area.");
    }

    // Add a block to the mine with a specific chance of appearing
    public void addBlockToMine(Mine mine, Material material, int chance) {
        if (mine == null) return;
        mine.addBlock(material, chance);
    }

    // Get a mine by its name (if it exists)
    public Optional<Mine> getMine(String name) {
        return mines.stream()
                .filter(mine -> mine.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    // Handle the wand being used on a block (left click/right click)
    public void handleSetupWand(int button, Block blockClicked, Player player) {
        Mine mine = editingPlayers.get(player.getUniqueId());
        if (mine == null) {
            player.sendMessage("§cYou are not editing any mine!");
            return;
        }

        if (blockClicked == null) {
            player.sendMessage("§cInvalid block selection.");
            return;
        }

        // Set corners for the mine based on button pressed (left/right click)
        switch (button) {
            case 1 -> {
                mine.setCornerOne(blockClicked.getLocation());
                player.sendMessage("§aCorner 1 set for mine: §e" + mine.getName());
            }
            case 2 -> {
                mine.setCornerTwo(blockClicked.getLocation());
                player.sendMessage("§aCorner 2 set for mine: §e" + mine.getName());
            }
            default -> player.sendMessage("§cInvalid button! Use left or right click.");
        }

        // Once both corners are set, finalize the mine
        if (mine.getCornerOne() != null && mine.getCornerTwo() != null) {
            mines.add(mine);
            editingPlayers.remove(player.getUniqueId());
            player.getInventory().removeItem(createEditWand());
            player.sendMessage("§eMine corners successfully set! Use §b/mine edit §eto modify them.");
        }
    }

    // Get a list of all the mines
    public List<Mine> getMines() {
        return Collections.unmodifiableList(mines);
    }

    // Create the edit wand item
    public static ItemStack createEditWand() {
        ItemStack wand = new ItemStack(Material.GOLD_AXE);
        ItemMeta meta = wand.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§eMine Edit Wand");
            wand.setItemMeta(meta);
        }

        return wand;
    }
}
