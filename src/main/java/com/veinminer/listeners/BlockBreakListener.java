package com.veinminer.listeners;

import com.veinminer.VeinMinerPlugin;
import com.veinminer.config.ConfigManager;
import com.veinminer.managers.VeinMinerManager;
import com.veinminer.utils.IntegrationUtils;
import com.veinminer.utils.MessageUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {
    
    private final VeinMinerPlugin plugin;
    private final ConfigManager config;
    private final VeinMinerManager veinMinerManager;
    
    public BlockBreakListener(VeinMinerPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.veinMinerManager = plugin.getVeinMinerManager();
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack tool = player.getInventory().getItemInMainHand();
        
        if (!shouldProcessVeinMining(player, block, tool)) {
            return;
        }
        
        if (!hasPermissionToVeinMine(player, block)) {
            return;
        }
        
        if (!canVeinMineInLocation(player, block)) {
            MessageUtils.sendMessage(player, config.getString("messages.protected-area", 
                "&cYou cannot vein mine in this protected area!"));
            return;
        }
        
        veinMinerManager.processVeinMining(player, block, tool);
    }
    
    private boolean shouldProcessVeinMining(Player player, Block block, ItemStack tool) {
        if (!config.isEnabled()) {
            return false;
        }
        
        if (!veinMinerManager.hasVeinMinerEnabled(player)) {
            return false;
        }
        
        if (!config.getAllowedBlocks().contains(block.getType())) {
            return false;
        }
        
        if (config.isRequireSneaking() && !player.isSneaking()) {
            return false;
        }
        
        return true;
    }
    
    private boolean hasPermissionToVeinMine(Player player, Block block) {
        if (!player.hasPermission("veinminer.use")) {
            if (config.getBoolean("debug.enabled", false)) {
                MessageUtils.sendMessage(player, config.getString("messages.no-permission", 
                    "&cYou don't have permission to use vein miner!"));
            }
            return false;
        }
        
        if (player.hasPermission("veinminer.bypass")) {
            return true;
        }
        
        String blockPermission = "veinminer.blocks." + block.getType().name().toLowerCase();
        if (!player.hasPermission(blockPermission)) {
            if (config.getBoolean("debug.enabled", false)) {
                MessageUtils.sendMessage(player, 
                    "&cYou don't have permission to vein mine " + block.getType().name() + "!");
            }
            return false;
        }
        
        return true;
    }
    
    private boolean canVeinMineInLocation(Player player, Block block) {
        if (player.hasPermission("veinminer.bypass")) {
            return true;
        }
        
        if (config.isWorldGuardIntegration()) {
            if (!IntegrationUtils.canBreakWithWorldGuard(player, block.getLocation())) {
                return false;
            }
        }
        
        if (config.isAntiGriefIntegration()) {
            if (!IntegrationUtils.canBreakWithAntiGrief(player, block.getLocation())) {
                return false;
            }
        }
        
        return true;
    }
}