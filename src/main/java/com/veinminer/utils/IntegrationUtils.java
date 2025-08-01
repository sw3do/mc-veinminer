package com.veinminer.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class IntegrationUtils {
    
    private static Boolean worldGuardEnabled = null;
    private static Boolean griefPreventionEnabled = null;
    private static Boolean townyEnabled = null;
    private static Boolean factionsEnabled = null;
    
    public static boolean canBreakWithWorldGuard(Player player, Location location) {
        if (!isWorldGuardEnabled()) {
            return true;
        }
        
        try {
            Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
            if (worldGuard == null || !worldGuard.isEnabled()) {
                return true;
            }
            
            Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
            Object instance = worldGuardClass.getMethod("getInstance").invoke(null);
            Object platform = worldGuardClass.getMethod("getPlatform").invoke(instance);
            Object regionContainer = platform.getClass().getMethod("getRegionContainer").invoke(platform);
            
            Class<?> bukkitAdapterClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
            Object world = bukkitAdapterClass.getMethod("adapt", org.bukkit.World.class).invoke(null, location.getWorld());
            Object bukkitPlayer = bukkitAdapterClass.getMethod("adapt", Player.class).invoke(null, player);
            Object vector = bukkitAdapterClass.getMethod("adapt", Location.class).invoke(null, location);
            
            Object regionManager = regionContainer.getClass().getMethod("get", world.getClass()).invoke(regionContainer, world);
            if (regionManager == null) {
                return true;
            }
            
            Class<?> flagsClass = Class.forName("com.sk89q.worldguard.protection.flags.Flags");
            Object breakFlag = flagsClass.getField("BREAK").get(null);
            
            Object query = regionManager.getClass().getMethod("createQuery").invoke(regionManager);
            Boolean canBreak = (Boolean) query.getClass()
                .getMethod("testState", vector.getClass(), bukkitPlayer.getClass(), breakFlag.getClass())
                .invoke(query, vector, bukkitPlayer, breakFlag);
            
            return canBreak != null ? canBreak : true;
            
        } catch (Exception e) {
            return true;
        }
    }
    
    public static boolean canBreakWithAntiGrief(Player player, Location location) {
        return canBreakWithGriefPrevention(player, location) &&
               canBreakWithTowny(player, location) &&
               canBreakWithFactions(player, location);
    }
    
    public static boolean canBreakWithGriefPrevention(Player player, Location location) {
        if (!isGriefPreventionEnabled()) {
            return true;
        }
        
        try {
            Plugin griefPrevention = Bukkit.getPluginManager().getPlugin("GriefPrevention");
            if (griefPrevention == null || !griefPrevention.isEnabled()) {
                return true;
            }
            
            Class<?> gpClass = Class.forName("me.ryanhamshire.GriefPrevention.GriefPrevention");
            Object instance = gpClass.getMethod("instance").invoke(null);
            Object dataStore = gpClass.getMethod("dataStore").invoke(instance);
            
            Object claim = dataStore.getClass()
                .getMethod("getClaimAt", Location.class, boolean.class, Object.class)
                .invoke(dataStore, location, false, null);
            
            if (claim == null) {
                return true;
            }
            
            String result = (String) claim.getClass()
                .getMethod("allowBreak", Player.class, Material.class)
                .invoke(claim, player, location.getBlock().getType());
            
            return result == null;
            
        } catch (Exception e) {
            return true;
        }
    }
    
    public static boolean canBreakWithTowny(Player player, Location location) {
        if (!isTownyEnabled()) {
            return true;
        }
        
        try {
            Plugin towny = Bukkit.getPluginManager().getPlugin("Towny");
            if (towny == null || !towny.isEnabled()) {
                return true;
            }
            
            Class<?> playerCacheUtilClass = Class.forName("com.palmergames.bukkit.towny.utils.PlayerCacheUtil");
            Object playerCache = playerCacheUtilClass.getMethod("getCachePermission", Player.class, Location.class, Material.class, String.class)
                .invoke(null, player, location, location.getBlock().getType(), "destroy");
            
            return (Boolean) playerCache;
            
        } catch (Exception e) {
            return true;
        }
    }
    
    public static boolean canBreakWithFactions(Player player, Location location) {
        if (!isFactionsEnabled()) {
            return true;
        }
        
        try {
            Plugin factions = Bukkit.getPluginManager().getPlugin("Factions");
            if (factions == null || !factions.isEnabled()) {
                return true;
            }
            
            Class<?> factionsClass = Class.forName("com.massivecraft.factions.Board");
            Object board = factionsClass.getMethod("getInstance").invoke(null);
            
            Class<?> fLocationClass = Class.forName("com.massivecraft.factions.FLocation");
            Object fLocation = fLocationClass.getConstructor(Location.class).newInstance(location);
            
            Object faction = board.getClass().getMethod("getFactionAt", fLocationClass).invoke(board, fLocation);
            
            Class<?> fPlayerClass = Class.forName("com.massivecraft.factions.FPlayers");
            Object fPlayers = fPlayerClass.getMethod("getInstance").invoke(null);
            Object fPlayer = fPlayers.getClass().getMethod("getByPlayer", Player.class).invoke(fPlayers, player);
            
            Boolean canBuild = (Boolean) faction.getClass().getMethod("hasAccess", fPlayer.getClass(), String.class)
                .invoke(faction, fPlayer, "build");
            
            return canBuild != null ? canBuild : true;
            
        } catch (Exception e) {
            return true;
        }
    }
    
    private static boolean isWorldGuardEnabled() {
        if (worldGuardEnabled == null) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
            worldGuardEnabled = plugin != null && plugin.isEnabled();
        }
        return worldGuardEnabled;
    }
    
    private static boolean isGriefPreventionEnabled() {
        if (griefPreventionEnabled == null) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("GriefPrevention");
            griefPreventionEnabled = plugin != null && plugin.isEnabled();
        }
        return griefPreventionEnabled;
    }
    
    private static boolean isTownyEnabled() {
        if (townyEnabled == null) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("Towny");
            townyEnabled = plugin != null && plugin.isEnabled();
        }
        return townyEnabled;
    }
    
    private static boolean isFactionsEnabled() {
        if (factionsEnabled == null) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("Factions");
            factionsEnabled = plugin != null && plugin.isEnabled();
        }
        return factionsEnabled;
    }
    
    public static void resetCache() {
        worldGuardEnabled = null;
        griefPreventionEnabled = null;
        townyEnabled = null;
        factionsEnabled = null;
    }
    
    public static String getIntegratedPlugins() {
        StringBuilder sb = new StringBuilder();
        
        if (isWorldGuardEnabled()) {
            sb.append("WorldGuard ");
        }
        if (isGriefPreventionEnabled()) {
            sb.append("GriefPrevention ");
        }
        if (isTownyEnabled()) {
            sb.append("Towny ");
        }
        if (isFactionsEnabled()) {
            sb.append("Factions ");
        }
        
        return sb.length() > 0 ? sb.toString().trim() : "None";
    }
}