package com.skitbet.prison.mine;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MineManager {

    private HashMap<UUID, Mine> playersInEdit = new HashMap<>();
    private List<Mine> mines = new ArrayList<>();

    public Mine begenMineCreation(String name, Player player) {
        Mine mine = new Mine(name, player.getWorld().getName());
        playersInEdit.put(player.getUniqueId(), mine);

        // Give wand to player
        player.getInventory().addItem(MineManager.EDIT_WAND());
        player.sendMessage("§aYou have begin the creation of the mine §e" + name);
        player.sendMessage("§aSelect 2 corners with the wand to define where the mine should be.");

        return mine;
    }

    public void enterEditMode(Player player, Mine mine) {
        mines.remove(mine);
        playersInEdit.put(player.getUniqueId(), mine);
        player.getInventory().addItem(MineManager.EDIT_WAND());
        player.sendMessage("§aYou have entered edit mode for the mine §e" + mine.getName() + "§a. The mine will not be active until 2 corners are set again.");
        player.sendMessage("§aSelect 2 corners with the wand to define where the mine should be.");
    }

    public void addBlockToMine(Mine mine, Material material, int chance) {
        mine.addBlock(material, chance);
    }

    public Optional<Mine> getMine(String name) {
        return this.mines.stream().filter(mine -> mine.getName().equalsIgnoreCase(name)).findFirst();
    }


    public void handleSetupWand(int button, Block blockClicked, Player player) {
        Mine mine = playersInEdit.get(player.getUniqueId());
        if (mine == null) return;

        switch (button) {
            case 1:
                mine.setCornerOne(blockClicked.getLocation());
                player.sendMessage("§aCorner 1 has been set for the mine §e" + mine.getName());
                break;
            case 2:
                mine.setCornerTwo(blockClicked.getLocation());
                player.sendMessage("§aCorner 2 has been set for the mine §e" + mine.getName());
        }

        if (mine.getCornerOne() != null && mine.getCornerTwo() != null) {
            mines.add(mine);
            playersInEdit.remove(player.getUniqueId());
            player.getInventory().remove(player.getItemInHand());
            player.sendMessage("§eThe mine corners have been set. You can edit them by doing /mine edit!");
        }


    }

    public static ItemStack EDIT_WAND() {
        ItemStack itemStack = new ItemStack(Material.GOLD_AXE);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName("§eMine Edit Wand");

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public List<Mine> getMines() {
        return mines;
    }


}
