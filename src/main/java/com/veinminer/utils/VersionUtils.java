package com.veinminer.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VersionUtils {
    
    private static final String SERVER_VERSION;
    private static final int VERSION_NUMBER;
    private static final Map<Material, Material[]> TOOL_BLOCK_MAP;
    
    static {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String[] parts = packageName.split("\\.");
        SERVER_VERSION = parts.length > 3 ? parts[3] : "v1_21_R1";
        VERSION_NUMBER = getVersionNumber();
        TOOL_BLOCK_MAP = initializeToolBlockMap();
    }
    
    private static int getVersionNumber() {
        try {
            String version = SERVER_VERSION.substring(1);
            String[] parts = version.split("_");
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            return major * 100 + minor;
        } catch (Exception e) {
            return 1200;
        }
    }
    
    private static Map<Material, Material[]> initializeToolBlockMap() {
        Map<Material, Material[]> map = new HashMap<>();
        
        Material[] pickaxeBlocks = {
            Material.STONE, Material.COBBLESTONE, Material.DEEPSLATE,
            Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE,
            Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.LAPIS_ORE,
            Material.REDSTONE_ORE, Material.NETHER_QUARTZ_ORE, Material.NETHER_GOLD_ORE
        };
        
        Material[] axeBlocks = {
            Material.OAK_LOG, Material.BIRCH_LOG, Material.SPRUCE_LOG,
            Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG
        };
        
        Material[] shovelBlocks = {
            Material.DIRT, Material.SAND, Material.GRAVEL, Material.CLAY
        };
        
        addToolsToMap(map, pickaxeBlocks, "PICKAXE");
        addToolsToMap(map, axeBlocks, "AXE");
        addToolsToMap(map, shovelBlocks, "SHOVEL");
        
        if (VERSION_NUMBER >= 1170) {
            addDeepslateOres(map);
        }
        
        if (VERSION_NUMBER >= 1190) {
            addNewLogs(map);
        }
        
        if (VERSION_NUMBER >= 1200) {
            addCopperOres(map);
        }
        
        return map;
    }
    
    private static void addToolsToMap(Map<Material, Material[]> map, Material[] blocks, String toolType) {
        Material[] tools = {
            getMaterial("WOODEN_" + toolType),
            getMaterial("STONE_" + toolType),
            getMaterial("IRON_" + toolType),
            getMaterial("GOLDEN_" + toolType),
            getMaterial("DIAMOND_" + toolType),
            getMaterial("NETHERITE_" + toolType)
        };
        
        for (Material block : blocks) {
            if (block != null) {
                map.put(block, tools);
            }
        }
    }
    
    private static void addDeepslateOres(Map<Material, Material[]> map) {
        Material[] pickaxes = {
            getMaterial("WOODEN_PICKAXE"), getMaterial("STONE_PICKAXE"),
            getMaterial("IRON_PICKAXE"), getMaterial("GOLDEN_PICKAXE"),
            getMaterial("DIAMOND_PICKAXE"), getMaterial("NETHERITE_PICKAXE")
        };
        
        Material[] deepslateOres = {
            getMaterial("DEEPSLATE_COAL_ORE"), getMaterial("DEEPSLATE_IRON_ORE"),
            getMaterial("DEEPSLATE_GOLD_ORE"), getMaterial("DEEPSLATE_DIAMOND_ORE"),
            getMaterial("DEEPSLATE_EMERALD_ORE"), getMaterial("DEEPSLATE_LAPIS_ORE"),
            getMaterial("DEEPSLATE_REDSTONE_ORE")
        };
        
        for (Material ore : deepslateOres) {
            if (ore != null) {
                map.put(ore, pickaxes);
            }
        }
    }
    
    private static void addNewLogs(Map<Material, Material[]> map) {
        Material[] axes = {
            getMaterial("WOODEN_AXE"), getMaterial("STONE_AXE"),
            getMaterial("IRON_AXE"), getMaterial("GOLDEN_AXE"),
            getMaterial("DIAMOND_AXE"), getMaterial("NETHERITE_AXE")
        };
        
        Material mangroveLog = getMaterial("MANGROVE_LOG");
        Material cherryLog = getMaterial("CHERRY_LOG");
        
        if (mangroveLog != null) {
            map.put(mangroveLog, axes);
        }
        if (cherryLog != null) {
            map.put(cherryLog, axes);
        }
    }
    
    private static void addCopperOres(Map<Material, Material[]> map) {
        Material[] pickaxes = {
            getMaterial("WOODEN_PICKAXE"), getMaterial("STONE_PICKAXE"),
            getMaterial("IRON_PICKAXE"), getMaterial("GOLDEN_PICKAXE"),
            getMaterial("DIAMOND_PICKAXE"), getMaterial("NETHERITE_PICKAXE")
        };
        
        Material copperOre = getMaterial("COPPER_ORE");
        Material deepslateCopperOre = getMaterial("DEEPSLATE_COPPER_ORE");
        
        if (copperOre != null) {
            map.put(copperOre, pickaxes);
        }
        if (deepslateCopperOre != null) {
            map.put(deepslateCopperOre, pickaxes);
        }
    }
    
    private static Material getMaterial(String name) {
        try {
            return Material.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    public static String getServerVersion() {
        return SERVER_VERSION;
    }
    

    
    public static boolean canBreakBlock(Material blockType, Material toolType) {
        Material[] validTools = TOOL_BLOCK_MAP.get(blockType);
        if (validTools == null) {
            return true;
        }
        
        for (Material validTool : validTools) {
            if (validTool != null && validTool == toolType) {
                return true;
            }
        }
        
        return false;
    }
    
    public static void damageTool(ItemStack tool, int damage, Player player, boolean preventBreaking) {
        if (tool == null || tool.getType() == Material.AIR) {
            return;
        }
        
        ItemMeta meta = tool.getItemMeta();
        if (!(meta instanceof Damageable)) {
            return;
        }
        
        Damageable damageable = (Damageable) meta;
        int currentDamage = damageable.getDamage();
        int maxDurability = tool.getType().getMaxDurability();
        
        if (maxDurability <= 0) {
            return;
        }
        
        int unbreakingLevel = 0;
        if (meta.hasEnchant(Enchantment.DURABILITY)) {
            unbreakingLevel = meta.getEnchantLevel(Enchantment.DURABILITY);
        }
        
        int actualDamage = calculateActualDamage(damage, unbreakingLevel);
        int newDamage = currentDamage + actualDamage;
        
        if (preventBreaking && newDamage >= maxDurability) {
            newDamage = maxDurability - 1;
        }
        
        damageable.setDamage(newDamage);
        tool.setItemMeta(meta);
        
        if (newDamage >= maxDurability && !preventBreaking) {
            tool.setAmount(0);
        }
    }
    
    private static int calculateActualDamage(int damage, int unbreakingLevel) {
        if (unbreakingLevel <= 0) {
            return damage;
        }
        
        int actualDamage = 0;
        for (int i = 0; i < damage; i++) {
            if (Math.random() < (1.0 / (unbreakingLevel + 1))) {
                actualDamage++;
            }
        }
        
        return actualDamage;
    }
    
    public static Collection<ItemStack> getBlockDrops(Block block, ItemStack tool, Player player) {
        try {
            if (VERSION_NUMBER >= 1130) {
                return block.getDrops(tool, player);
            } else {
                return block.getDrops(tool);
            }
        } catch (Exception e) {
            return Collections.singletonList(new ItemStack(block.getType(), 1));
        }
    }
    
    public static boolean isNewerVersion(int major, int minor) {
        return VERSION_NUMBER >= (major * 100 + minor);
    }
    
    public static boolean hasEnchantment(ItemStack item, String enchantmentName) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        try {
            Enchantment enchantment = Enchantment.getByName(enchantmentName);
            if (enchantment == null) {
                enchantment = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(enchantmentName.toLowerCase()));
            }
            return enchantment != null && item.getItemMeta().hasEnchant(enchantment);
        } catch (Exception e) {
            return false;
        }
    }
}