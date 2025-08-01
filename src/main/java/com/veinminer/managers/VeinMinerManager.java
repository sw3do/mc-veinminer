package com.veinminer.managers;

import com.veinminer.VeinMinerPlugin;
import com.veinminer.config.ConfigManager;
import com.veinminer.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VeinMinerManager {
    
    private final VeinMinerPlugin plugin;
    private final ConfigManager config;
    private final Map<UUID, Boolean> playerToggleStates;
    private final Map<UUID, Long> lastVeinMineTime;
    private final Queue<VeinMiningTask> processingQueue;
    private BukkitTask processingTask;
    
    public VeinMinerManager(VeinMinerPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.playerToggleStates = new ConcurrentHashMap<>();
        this.lastVeinMineTime = new ConcurrentHashMap<>();
        this.processingQueue = new ConcurrentLinkedQueue<>();
        
        startProcessingTask();
    }
    
    public boolean canVeinMine(Player player, Block block, ItemStack tool) {
        if (!config.isEnabled()) {
            return false;
        }
        
        if (!hasVeinMinerEnabled(player)) {
            return false;
        }
        
        if (!player.hasPermission("veinminer.use")) {
            return false;
        }
        
        if (config.isRequireSneaking() && !player.isSneaking()) {
            return false;
        }
        
        if (!config.getAllowedBlocks().contains(block.getType())) {
            return false;
        }
        
        if (config.isRequireCorrectTool() && !isCorrectTool(block.getType(), tool)) {
            return false;
        }
        
        if (isOnCooldown(player)) {
            return false;
        }
        
        return true;
    }
    
    public void processVeinMining(Player player, Block startBlock, ItemStack tool) {
        if (!canVeinMine(player, startBlock, tool)) {
            return;
        }
        
        setLastVeinMineTime(player);
        
        VeinMiningTask task = new VeinMiningTask(player, startBlock, tool);
        
        if (config.getBoolean("performance.async-processing", true)) {
            processingQueue.offer(task);
        } else {
            task.execute();
        }
    }
    
    private void startProcessingTask() {
        int maxProcessingTime = config.getInt("performance.max-processing-time", 50);
        
        processingTask = new BukkitRunnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                
                while (!processingQueue.isEmpty() && 
                       (System.currentTimeMillis() - startTime) < maxProcessingTime) {
                    VeinMiningTask task = processingQueue.poll();
                    if (task != null) {
                        task.execute();
                    }
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }
    
    public Set<Block> findConnectedBlocks(Block startBlock, Material targetMaterial, int maxBlocks, int maxDistance) {
        Set<Block> connectedBlocks = ConcurrentHashMap.newKeySet();
        Queue<Block> toCheck = new ConcurrentLinkedQueue<>();
        Set<Location> checked = ConcurrentHashMap.newKeySet();
        
        toCheck.offer(startBlock);
        checked.add(startBlock.getLocation());
        
        while (!toCheck.isEmpty() && connectedBlocks.size() < maxBlocks) {
            Block current = toCheck.poll();
            
            if (current.getType() == targetMaterial || current.equals(startBlock)) {
                connectedBlocks.add(current);
                
                for (Block neighbor : getNeighbors(current, maxDistance)) {
                    Location neighborLoc = neighbor.getLocation();
                    
                    if (!checked.contains(neighborLoc) && 
                        neighbor.getType() == targetMaterial &&
                        isWithinDistance(startBlock.getLocation(), neighborLoc, maxDistance)) {
                        
                        toCheck.offer(neighbor);
                        checked.add(neighborLoc);
                    }
                }
            }
        }
        
        return connectedBlocks;
    }
    
    private List<Block> getNeighbors(Block block, int maxDistance) {
        List<Block> neighbors = new Vector<>();
        Location center = block.getLocation();
        
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    
                    Location neighborLoc = center.clone().add(x, y, z);
                    if (isWithinDistance(center, neighborLoc, maxDistance)) {
                        neighbors.add(neighborLoc.getBlock());
                    }
                }
            }
        }
        
        return neighbors;
    }
    
    private boolean isWithinDistance(Location center, Location target, int maxDistance) {
        return center.distance(target) <= maxDistance;
    }
    
    private boolean isCorrectTool(Material blockType, ItemStack tool) {
        if (tool == null || tool.getType() == Material.AIR) {
            return false;
        }
        
        if (!config.getAllowedTools().contains(tool.getType())) {
            return false;
        }
        
        return VersionUtils.canBreakBlock(blockType, tool.getType());
    }
    
    private boolean isOnCooldown(Player player) {
        Long lastTime = lastVeinMineTime.get(player.getUniqueId());
        if (lastTime == null) {
            return false;
        }
        
        long cooldown = config.getLong("cooldown", 100);
        return (System.currentTimeMillis() - lastTime) < cooldown;
    }
    
    private void setLastVeinMineTime(Player player) {
        lastVeinMineTime.put(player.getUniqueId(), System.currentTimeMillis());
    }
    
    public boolean hasVeinMinerEnabled(Player player) {
        return playerToggleStates.getOrDefault(player.getUniqueId(), true);
    }
    
    public void toggleVeinMiner(Player player) {
        UUID playerId = player.getUniqueId();
        boolean currentState = playerToggleStates.getOrDefault(playerId, true);
        playerToggleStates.put(playerId, !currentState);
    }
    
    public void reload() {
        playerToggleStates.clear();
        lastVeinMineTime.clear();
        processingQueue.clear();
    }
    
    public void shutdown() {
        if (processingTask != null) {
            processingTask.cancel();
        }
        processingQueue.clear();
    }
    
    private class VeinMiningTask {
        private final Player player;
        private final Block startBlock;
        private final ItemStack tool;
        private final Material originalBlockType;
        
        public VeinMiningTask(Player player, Block startBlock, ItemStack tool) {
            this.player = player;
            this.startBlock = startBlock;
            this.tool = tool.clone();
            this.originalBlockType = startBlock.getType();
        }
        
        public void execute() {
            if (!player.isOnline()) {
                return;
            }
            
            if (!originalBlockType.isSolid()) {
                return;
            }
            
            Material blockType = originalBlockType;
            int maxBlocks = config.getBlockLimit(blockType);
            int maxDistance = config.getMaxDistance();
            
            Set<Block> blocksToMine = findConnectedBlocks(startBlock, blockType, maxBlocks, maxDistance);
            
            if (blocksToMine.isEmpty()) {
                return;
            }
            
            int batchSize = config.getInt("performance.batch-size", 10);
            List<Block> blockList = new Vector<>(blocksToMine);
            
            new BukkitRunnable() {
                int index = 0;
                
                @Override
                public void run() {
                    if (!player.isOnline() || index >= blockList.size()) {
                        cancel();
                        return;
                    }
                    
                    int endIndex = Math.min(index + batchSize, blockList.size());
                    
                    for (int i = index; i < endIndex; i++) {
                        Block block = blockList.get(i);
                        if (block.getType() == blockType) {
                            mineBlock(player, block, tool);
                        }
                    }
                    
                    index = endIndex;
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }
        
        private void mineBlock(Player player, Block block, ItemStack tool) {
            if (config.isConsumeDurability()) {
                ItemStack playerTool = player.getInventory().getItemInMainHand();
                if (playerTool.getType() == tool.getType()) {
                    VersionUtils.damageTool(playerTool, 1, player, config.isPreventToolBreaking());
                }
            }
            
            if (config.isDropItems()) {
                Collection<ItemStack> drops = VersionUtils.getBlockDrops(block, tool, player);
                for (ItemStack drop : drops) {
                    block.getWorld().dropItemNaturally(block.getLocation(), drop);
                }
            }
            
            block.setType(Material.AIR);
        }
    }
}