package com.skitbet.prison.commands;

import com.skitbet.prison.PrisonPlugin;
import com.skitbet.prison.mine.Mine;
import com.skitbet.prison.mine.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MineCommands implements CommandExecutor, TabCompleter {

    private final MineManager mineManager = PrisonPlugin.getInstance().getMineManager();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§4✕ §cYou need to specify a argument.");
            return true;
        }

        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        String name = args[1];
        Optional<Mine> mineOptional = mineManager.getMine(name);

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "create":
                if (!sender.hasPermission("mine.create")) {
                    sender.sendMessage("§4✕ §cYou don't have enough permissions to use this command.");
                    return true;
                }
                if (args.length < 1) {
                    sender.sendMessage("§4✕ §cYou need to specify a name in order to create a mine.");
                    sender.sendMessage("§fCorrect Usage: §7/mine create <name>");
                    return true;
                }

                if (mineOptional.isPresent()) {
                    sender.sendMessage("§4✕ §cA mine with the name §e\"" + name + "\"§c already exists.");
                    return true;
                }

                mineManager.createMine(name, player);
                player.getInventory().addItem(MineManager.EDIT_WAND());

                player.sendMessage("§aYou have begin the creation of the mine §e" + name);
                player.sendMessage("§eSelect 2 corners with the wand to define where the mine should be.");
                break;
            case "reset":
                if (!sender.hasPermission("mine.reset")) {
                    sender.sendMessage("§4✕ §cYou don't have enough permissions to use this command.");
                    return true;
                }
                if (args.length < 1) {
                    sender.sendMessage("§4✕ §cYou need to specify a name in order to reset a mine.");
                    sender.sendMessage("§fCorrect Usage: §7/mine reset <name>");
                    return true;
                }

                if (!mineOptional.isPresent()) {
                    sender.sendMessage("§4✕ §cA mine with the name §e\"" + name + "\"§c does not exists.");
                    return true;
                }

                mineOptional.get().resetMine();
                player.sendMessage("§cThe mine §e" + mineOptional.get().getName() + " is now resetting!");

                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("mine") || !sender.hasPermission("mine.completions")) return null;

        List<String> completions = Stream.of("create", "reset").collect(Collectors.toList());
        switch (args.length) {
            case 0:
                return completions;
            // Autocomplete using subcommands that start with the given string
            case 1:
                return completions.stream().filter(c -> c.startsWith(args[0].toLowerCase(Locale.ROOT))).collect(Collectors.toList());
            default: {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "create":
                    case "reset":
                        return mineManager.getMines().stream().map(Mine::getName).collect(Collectors.toList());
                    default:
                        return Collections.emptyList();
                }
            }
        }
    }



}
