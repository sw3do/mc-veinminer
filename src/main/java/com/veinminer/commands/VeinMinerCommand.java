package com.veinminer.commands;

import com.veinminer.VeinMinerPlugin;
import com.veinminer.config.ConfigManager;
import com.veinminer.managers.VeinMinerManager;
import com.veinminer.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VeinMinerCommand implements CommandExecutor, TabCompleter {
    
    private final VeinMinerPlugin plugin;
    private final ConfigManager config;
    private final VeinMinerManager veinMinerManager;
    
    public VeinMinerCommand(VeinMinerPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.veinMinerManager = plugin.getVeinMinerManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "toggle":
                return handleToggleCommand(sender);
            case "reload":
                return handleReloadCommand(sender);
            case "info":
                return handleInfoCommand(sender);
            case "help":
                sendHelpMessage(sender);
                return true;
            case "status":
                return handleStatusCommand(sender);
            default:
                MessageUtils.sendMessage(sender, "&cUnknown command. Use /veinminer help for available commands.");
                return true;
        }
    }
    
    private boolean handleToggleCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, "&cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("veinminer.use")) {
            MessageUtils.sendMessage(player, config.getString("messages.no-permission", 
                "&cYou don't have permission to use vein miner!"));
            return true;
        }
        
        veinMinerManager.toggleVeinMiner(player);
        
        if (veinMinerManager.hasVeinMinerEnabled(player)) {
            MessageUtils.sendMessage(player, config.getString("messages.vein-miner-toggled-on", 
                "&aVein miner enabled!"));
        } else {
            MessageUtils.sendMessage(player, config.getString("messages.vein-miner-toggled-off", 
                "&cVein miner disabled!"));
        }
        
        return true;
    }
    
    private boolean handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("veinminer.admin")) {
            MessageUtils.sendMessage(sender, "&cYou don't have permission to reload the configuration!");
            return true;
        }
        
        try {
            plugin.reload();
            MessageUtils.sendMessage(sender, config.getString("messages.config-reloaded", 
                "&aVeinMiner configuration reloaded successfully!"));
        } catch (Exception e) {
            MessageUtils.sendMessage(sender, "&cError reloading configuration: " + e.getMessage());
            plugin.getLogger().severe("Error reloading configuration: " + e.getMessage());
        }
        
        return true;
    }
    
    private boolean handleInfoCommand(CommandSender sender) {
        MessageUtils.sendMessage(sender, "&6=== VeinMiner Information ===");
        MessageUtils.sendMessage(sender, "&eVersion: &f" + plugin.getDescription().getVersion());
        MessageUtils.sendMessage(sender, "&eAuthor: &f" + plugin.getDescription().getAuthors().get(0));
        MessageUtils.sendMessage(sender, "&eStatus: &f" + (config.isEnabled() ? "&aEnabled" : "&cDisabled"));
        MessageUtils.sendMessage(sender, "&eMax Blocks: &f" + config.getMaxBlocks());
        MessageUtils.sendMessage(sender, "&eMax Distance: &f" + config.getMaxDistance());
        MessageUtils.sendMessage(sender, "&eRequire Sneaking: &f" + (config.isRequireSneaking() ? "&aYes" : "&cNo"));
        MessageUtils.sendMessage(sender, "&eAllowed Blocks: &f" + config.getAllowedBlocks().size());
        MessageUtils.sendMessage(sender, "&eAllowed Tools: &f" + config.getAllowedTools().size());
        
        if (sender instanceof Player) {
            Player player = (Player) sender;
            boolean playerEnabled = veinMinerManager.hasVeinMinerEnabled(player);
            MessageUtils.sendMessage(sender, "&eYour Status: &f" + (playerEnabled ? "&aEnabled" : "&cDisabled"));
        }
        
        return true;
    }
    
    private boolean handleStatusCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, "&cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        boolean enabled = veinMinerManager.hasVeinMinerEnabled(player);
        
        MessageUtils.sendMessage(player, "&6=== Your VeinMiner Status ===");
        MessageUtils.sendMessage(player, "&eVein Miner: &f" + (enabled ? "&aEnabled" : "&cDisabled"));
        MessageUtils.sendMessage(player, "&ePermission: &f" + (player.hasPermission("veinminer.use") ? "&aGranted" : "&cDenied"));
        MessageUtils.sendMessage(player, "&eAdmin Permission: &f" + (player.hasPermission("veinminer.admin") ? "&aGranted" : "&cDenied"));
        MessageUtils.sendMessage(player, "&eBypass Permission: &f" + (player.hasPermission("veinminer.bypass") ? "&aGranted" : "&cDenied"));
        
        return true;
    }
    
    private void sendHelpMessage(CommandSender sender) {
        MessageUtils.sendMessage(sender, "&6=== VeinMiner Commands ===");
        MessageUtils.sendMessage(sender, "&e/veinminer toggle &7- Toggle vein mining on/off");
        MessageUtils.sendMessage(sender, "&e/veinminer status &7- Check your vein miner status");
        MessageUtils.sendMessage(sender, "&e/veinminer info &7- Show plugin information");
        MessageUtils.sendMessage(sender, "&e/veinminer help &7- Show this help message");
        
        if (sender.hasPermission("veinminer.admin")) {
            MessageUtils.sendMessage(sender, "&c/veinminer reload &7- Reload configuration");
        }
        
        MessageUtils.sendMessage(sender, "&7Aliases: &e/vm, /veins");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("toggle", "status", "info", "help");
            
            if (sender.hasPermission("veinminer.admin")) {
                subCommands = new ArrayList<>(subCommands);
                subCommands.add("reload");
            }
            
            String input = args[0].toLowerCase();
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(input)) {
                    completions.add(subCommand);
                }
            }
        }
        
        return completions;
    }
}