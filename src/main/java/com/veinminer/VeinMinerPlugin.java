package com.veinminer;

import com.veinminer.commands.VeinMinerCommand;
import com.veinminer.config.ConfigManager;
import com.veinminer.listeners.BlockBreakListener;
import com.veinminer.managers.VeinMinerManager;
import com.veinminer.utils.VersionUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class VeinMinerPlugin extends JavaPlugin {
    
    private static VeinMinerPlugin instance;
    private ConfigManager configManager;
    private VeinMinerManager veinMinerManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        getLogger().info("VeinMiner is starting up...");
        getLogger().info("Detected Minecraft version: " + VersionUtils.getServerVersion());
        
        initializeManagers();
        registerCommands();
        registerListeners();
        
        getLogger().info("VeinMiner has been enabled successfully!");
    }
    
    @Override
    public void onDisable() {
        if (veinMinerManager != null) {
            veinMinerManager.shutdown();
        }
        getLogger().info("VeinMiner has been disabled!");
    }
    
    private void initializeManagers() {
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        veinMinerManager = new VeinMinerManager(this);
    }
    
    private void registerCommands() {
        getCommand("veinminer").setExecutor(new VeinMinerCommand(this));
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
    }
    
    public static VeinMinerPlugin getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public VeinMinerManager getVeinMinerManager() {
        return veinMinerManager;
    }
    
    public void reload() {
        configManager.loadConfig();
        veinMinerManager.reload();
        getLogger().info("VeinMiner configuration reloaded!");
    }
}