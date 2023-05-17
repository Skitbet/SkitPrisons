package com.skitbet.prison;

import com.skitbet.prison.commands.MineCommands;
import com.skitbet.prison.listeners.PlayerListeners;
import com.skitbet.prison.mine.Mine;
import com.skitbet.prison.mine.MineManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PrisonPlugin extends JavaPlugin {

    private static PrisonPlugin instance;
    private MineManager mineManager;

    @Override
    public void onEnable() {

        instance = this;
        mineManager = new MineManager();

        getServer().getPluginManager().registerEvents(new PlayerListeners(), this);

        getCommand("mine").setExecutor(new MineCommands());
    }

    @Override
    public void onDisable() {

    }

    public static PrisonPlugin getInstance() {
        return instance;
    }

    public MineManager getMineManager() {
        return mineManager;
    }
}
