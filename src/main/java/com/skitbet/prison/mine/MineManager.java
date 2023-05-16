package com.skitbet.prison.mine;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MineManager {

    private HashMap<UUID, Mine> playersInEdit = new HashMap<>();
    private List<Mine> mines = new ArrayList<>();

    public Mine createMine(String name, Player player) {
        Mine mine = new Mine(name, player.getWorld().getName());
        playersInEdit.put(player.getUniqueId(), mine);
        return mine;
    }


    public void handleSetupWand(int button, Block blockClicked, Player player) {
        Mine mine = playersInEdit.get(player.getUniqueId());
        if (mine == null) return;

        switch (button) {
            case 1:
                mine.setCornerOne(blockClicked.getLocation());
                player.sendMessage("§eSet Corner 1");
                break;
            case 2:
                mine.setCornerTwo(blockClicked.getLocation());
                player.sendMessage("§eSet Corner 2");

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