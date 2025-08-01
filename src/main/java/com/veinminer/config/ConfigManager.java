package com.veinminer.config;

import com.veinminer.VeinMinerPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {
    
    private final VeinMinerPlugin plugin;
    private FileConfiguration config;
    private File configFile;
    
    private boolean enabled;
    private int maxBlocks;
    private int maxDistance;
    private boolean requireSneaking;
    private boolean requireCorrectTool;
    private boolean dropItems;
    private boolean consumeDurability;
    private boolean preventToolBreaking;
    private Set<Material> allowedBlocks;
    private Set<Material> allowedTools;
    private Map<Material, Integer> blockLimits;
    private boolean worldGuardIntegration;
    private boolean antiGriefIntegration;
    
    public ConfigManager(VeinMinerPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
    }
    
    public void loadConfig() {
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        
        config = YamlConfiguration.loadConfiguration(configFile);
        
        enabled = config.getBoolean("enabled", true);
        maxBlocks = config.getInt("max-blocks", 64);
        maxDistance = config.getInt("max-distance", 3);
        requireSneaking = config.getBoolean("require-sneaking", true);
        requireCorrectTool = config.getBoolean("require-correct-tool", true);
        dropItems = config.getBoolean("drop-items", true);
        consumeDurability = config.getBoolean("consume-durability", true);
        preventToolBreaking = config.getBoolean("prevent-tool-breaking", true);
        worldGuardIntegration = config.getBoolean("integrations.worldguard", true);
        antiGriefIntegration = config.getBoolean("integrations.anti-grief", true);
        
        loadAllowedBlocks();
        loadAllowedTools();
        loadBlockLimits();
    }
    
    private void loadAllowedBlocks() {
        allowedBlocks = new HashSet<>();
        List<String> blockList = config.getStringList("allowed-blocks");
        
        if (blockList.isEmpty()) {
            setDefaultAllowedBlocks();
        } else {
            for (String blockName : blockList) {
                try {
                    Material material = Material.valueOf(blockName.toUpperCase());
                    allowedBlocks.add(material);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid block type in config: " + blockName);
                }
            }
        }
    }
    
    private void setDefaultAllowedBlocks() {
        allowedBlocks.addAll(Arrays.asList(
            Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
            Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE,
            Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
            Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
            Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
            Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE,
            Material.NETHER_GOLD_ORE, Material.NETHER_QUARTZ_ORE,
            Material.ANCIENT_DEBRIS,
            Material.OAK_LOG, Material.BIRCH_LOG, Material.SPRUCE_LOG,
            Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
            Material.MANGROVE_LOG, Material.CHERRY_LOG,
            Material.STONE, Material.COBBLESTONE, Material.DEEPSLATE,
            Material.SAND, Material.GRAVEL, Material.DIRT
        ));
    }
    
    private void loadAllowedTools() {
        allowedTools = new HashSet<>();
        List<String> toolList = config.getStringList("allowed-tools");
        
        if (toolList.isEmpty()) {
            setDefaultAllowedTools();
        } else {
            for (String toolName : toolList) {
                try {
                    Material material = Material.valueOf(toolName.toUpperCase());
                    allowedTools.add(material);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid tool type in config: " + toolName);
                }
            }
        }
    }
    
    private void setDefaultAllowedTools() {
        allowedTools.addAll(Arrays.asList(
            Material.WOODEN_PICKAXE, Material.STONE_PICKAXE,
            Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE,
            Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE,
            Material.WOODEN_AXE, Material.STONE_AXE,
            Material.IRON_AXE, Material.GOLDEN_AXE,
            Material.DIAMOND_AXE, Material.NETHERITE_AXE,
            Material.WOODEN_SHOVEL, Material.STONE_SHOVEL,
            Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL,
            Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL
        ));
    }
    
    private void loadBlockLimits() {
        blockLimits = new HashMap<>();
        if (config.contains("block-limits")) {
            for (String key : config.getConfigurationSection("block-limits").getKeys(false)) {
                try {
                    Material material = Material.valueOf(key.toUpperCase());
                    int limit = config.getInt("block-limits." + key);
                    blockLimits.put(material, limit);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid block type in block-limits: " + key);
                }
            }
        }
    }
    
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config file: " + e.getMessage());
        }
    }
    
    public boolean isEnabled() { return enabled; }
    public int getMaxBlocks() { return maxBlocks; }
    public int getMaxDistance() { return maxDistance; }
    public boolean isRequireSneaking() { return requireSneaking; }
    public boolean isRequireCorrectTool() { return requireCorrectTool; }
    public boolean isDropItems() { return dropItems; }
    public boolean isConsumeDurability() { return consumeDurability; }
    public boolean isPreventToolBreaking() { return preventToolBreaking; }
    public Set<Material> getAllowedBlocks() { return allowedBlocks; }
    public Set<Material> getAllowedTools() { return allowedTools; }
    public Map<Material, Integer> getBlockLimits() { return blockLimits; }
    public boolean isWorldGuardIntegration() { return worldGuardIntegration; }
    public boolean isAntiGriefIntegration() { return antiGriefIntegration; }
    
    public int getBlockLimit(Material material) {
        return blockLimits.getOrDefault(material, maxBlocks);
    }
    
    public String getString(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }
    
    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }
    
    public int getInt(String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }
    
    public long getLong(String path, long defaultValue) {
        return config.getLong(path, defaultValue);
    }
}