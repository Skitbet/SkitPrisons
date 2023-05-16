package com.skitbet.prison.commands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import com.skitbet.prison.PrisonPlugin;
import com.skitbet.prison.mine.Mine;
import com.skitbet.prison.mine.MineManager;
import org.bukkit.entity.Player;

public class MineCommands {

    @Command(name = "create", desc = "Begin the setup for creating a mine")
    public void createMine(@Sender Player player, String name) {
        Mine mine = PrisonPlugin.getInstance().getMineManager().createMine(name, player);
        player.getInventory().addItem(MineManager.EDIT_WAND());

        player.sendMessage("§eYou have been given the Edit wand, please select the 2 corners of the mine.");
    }

    @Command(name = "reset", desc = "Reset the mines blocks")
    public void resetMine(@Sender Player player, Mine mine) {
        mine.resetMine();
        player.sendMessage("§cThe mine " + mine.getName() + " is now resetting!");
    }

}
