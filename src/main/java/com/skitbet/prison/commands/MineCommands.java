package com.skitbet.prison.commands;

import com.skitbet.prison.PrisonPlugin;
import com.skitbet.prison.mine.Mine;
import com.skitbet.prison.mine.MineManager;
import org.bukkit.Material;
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

    // Some helpful messages for users
    private static final String NO_PERMISSION = "§4✕ §cYou don't have permission to use this command.";
    private static final String INVALID_SENDER = "§4✕ §cThis command can only be used by players.";
    private static final String INVALID_USAGE = "§4✕ §cInvalid usage! §fCorrect Usage: ";
    private static final String MINE_EXISTS = "§4✕ §cA mine with the name §e\"%s\"§c already exists.";
    private static final String MINE_NOT_FOUND = "§4✕ §cNo mine found with the name §e\"%s\"§c.";
    private static final String BLOCK_ALREADY_ADDED = "§4✕ §cBlock type §e\"%s\"§c is already part of the mine.";
    private static final String INVALID_BLOCK = "§4✕ §cInvalid block type: §e\"%s\"§c.";
    private static final String INVALID_CHANCE = "§4✕ §cChance must be a number between 1 and 100.";

    // Help message to show available commands
    private static final String HELP_MESSAGE = "§aAvailable subcommands:\n" +
            "§7/mine create <name> §f- Create a new mine.\n" +
            "§7/mine reset <name> §f- Reset an existing mine.\n" +
            "§7/mine edit <name> §f- Enter edit mode for a mine.\n" +
            "§7/mine addblock <name> <block> <chance> §f- Add a block type to a mine.";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player, if not, send an error message
        if (!(sender instanceof Player)) {
            sender.sendMessage(INVALID_SENDER);
            return true;
        }
        Player player = (Player) sender;

        // If no subcommand is provided, show the help message
        if (args.length == 0) {
            player.sendMessage(HELP_MESSAGE);
            return true;
        }

        String subCommand = args[0].toLowerCase(Locale.ROOT);
        // Handle different subcommands
        switch (subCommand) {
            case "create":
                handleCreateMine(player, args);
                break;
            case "reset":
                handleResetMine(player, args);
                break;
            case "edit":
                handleEditMine(player, args);
                break;
            case "addblock":
                handleAddBlock(player, args);
                break;
            default:
                // If the subcommand is unknown, show an error message with valid options
                player.sendMessage("§4✕ §cUnknown subcommand: §e\"" + subCommand + "\".\n" + HELP_MESSAGE);
                break;
        }
        return true;
    }

    // Handle the 'create' subcommand
    private void handleCreateMine(Player player, String[] args) {
        if (!player.hasPermission("mine.create")) {
            player.sendMessage(NO_PERMISSION);
            return;
        }
        if (args.length < 2) {
            player.sendMessage(INVALID_USAGE + "§7/mine create <name>");
            return;
        }

        String name = args[1];
        // Check if mine already exists
        if (mineManager.getMine(name).isPresent()) {
            player.sendMessage(String.format(MINE_EXISTS, name));
            return;
        }

        mineManager.startMineCreation(name, player);
        player.sendMessage("§aMine §e\"" + name + "\"§a has been created! Start configuring it now.");
    }

    // Handle the 'reset' subcommand
    private void handleResetMine(Player player, String[] args) {
        if (!player.hasPermission("mine.reset")) {
            player.sendMessage(NO_PERMISSION);
            return;
        }
        if (args.length < 2) {
            player.sendMessage(INVALID_USAGE + "§7/mine reset <name>");
            return;
        }

        String name = args[1];
        Optional<Mine> mine = mineManager.getMine(name);
        if (!mine.isPresent()) {
            player.sendMessage(String.format(MINE_NOT_FOUND, name));
            return;
        }

        mine.get().resetMine();
        player.sendMessage("§aThe mine §e\"" + name + "\"§a is now resetting!");
    }

    // Handle the 'edit' subcommand
    private void handleEditMine(Player player, String[] args) {
        if (!player.hasPermission("mine.edit")) {
            player.sendMessage(NO_PERMISSION);
            return;
        }
        if (args.length < 2) {
            player.sendMessage(INVALID_USAGE + "§7/mine edit <name>");
            return;
        }

        String name = args[1];
        Optional<Mine> mine = mineManager.getMine(name);
        if (!mine.isPresent()) {
            player.sendMessage(String.format(MINE_NOT_FOUND, name));
            return;
        }

        mineManager.enterEditMode(player, mine.get());
        player.sendMessage("§aYou are now editing the mine §e\"" + name + "\"§a.");
    }

    // Handle the 'addblock' subcommand
    private void handleAddBlock(Player player, String[] args) {
        if (!player.hasPermission("mine.addblock")) {
            player.sendMessage(NO_PERMISSION);
            return;
        }
        if (args.length < 4) {
            player.sendMessage(INVALID_USAGE + "§7/mine addblock <name> <block> <chance>");
            return;
        }

        String name = args[1];
        Optional<Mine> mine = mineManager.getMine(name);
        if (!mine.isPresent()) {
            player.sendMessage(String.format(MINE_NOT_FOUND, name));
            return;
        }

        String blockType = args[2];
        Material material;
        try {
            material = Material.valueOf(blockType.toUpperCase(Locale.ROOT)); // Try to get the material
        } catch (IllegalArgumentException e) {
            player.sendMessage(String.format(INVALID_BLOCK, blockType));
            return;
        }

        // Parse chance and validate it
        int chance;
        try {
            chance = Integer.parseInt(args[3]);
            if (chance < 1 || chance > 100) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(INVALID_CHANCE);
            return;
        }

        if (mine.get().hasBlock(material)) {
            player.sendMessage(String.format(BLOCK_ALREADY_ADDED, blockType));
            return;
        }

        mineManager.addBlockToMine(mine.get(), material, chance);
        player.sendMessage("§aBlock §e\"" + material.name() + "\"§a with a chance of §e" + chance + "%§a has been added to the mine §e\"" + name + "\"§a.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // If the player doesn't have permission for auto-complete, don't provide it
        if (!command.getName().equalsIgnoreCase("mine") || !sender.hasPermission("mine.completions")) return null;

        List<String> completions = Stream.of("create", "reset", "edit", "addblock").collect(Collectors.toList());
        switch (args.length) {
            case 1:
                // Show available subcommands based on the first argument
                return completions.stream().filter(c -> c.startsWith(args[0].toLowerCase(Locale.ROOT))).collect(Collectors.toList());
            case 2:
                // If editing, resetting, or adding a block, show available mine names
                if (Stream.of("reset", "edit", "addblock").anyMatch(args[0].toLowerCase(Locale.ROOT)::equals)) {
                    return mineManager.getMines().stream().map(Mine::getName).collect(Collectors.toList());
                }
                break;
            default:
                return Collections.emptyList();
        }
        return Collections.emptyList();
    }
}
