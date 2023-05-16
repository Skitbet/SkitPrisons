package com.skitbet.prison;

import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import com.skitbet.prison.commands.MineCommands;
import com.skitbet.prison.commands.provider.MineProvider;
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

        CommandService commandService = Drink.get(this);
        commandService.bind(Mine.class).toProvider(new MineProvider());
        commandService.register(new MineCommands(), "mine");
        commandService.registerCommands();
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
