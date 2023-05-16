package com.skitbet.prison.listeners;

import com.skitbet.prison.PrisonPlugin;
import com.skitbet.prison.mine.MineManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListeners implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (event.getItem().isSimilar(MineManager.EDIT_WAND())) {
            switch (event.getAction()) {
                case LEFT_CLICK_BLOCK:
                    PrisonPlugin.getInstance().getMineManager().handleSetupWand(1, event.getClickedBlock(), event.getPlayer());
                    break;
                case RIGHT_CLICK_BLOCK:
                    PrisonPlugin.getInstance().getMineManager().handleSetupWand(2, event.getClickedBlock(), event.getPlayer());
            }
        }
    }

}
