package com.veinminer.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {
    
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final boolean SUPPORTS_HEX = VersionUtils.isNewerVersion(1, 16);
    
    public static void sendMessage(CommandSender sender, String message) {
        if (sender == null || message == null || message.isEmpty()) {
            return;
        }
        
        String formattedMessage = formatMessage(message);
        sender.sendMessage(formattedMessage);
    }
    
    public static void sendMessage(Player player, String message) {
        if (player == null || !player.isOnline() || message == null || message.isEmpty()) {
            return;
        }
        
        String formattedMessage = formatMessage(message);
        player.sendMessage(formattedMessage);
    }
    
    public static String formatMessage(String message) {
        if (message == null) {
            return "";
        }
        
        String formatted = message;
        
        if (SUPPORTS_HEX) {
            formatted = translateHexColorCodes(formatted);
        }
        
        formatted = ChatColor.translateAlternateColorCodes('&', formatted);
        
        return formatted;
    }
    
    private static String translateHexColorCodes(String message) {
        if (!SUPPORTS_HEX) {
            return message;
        }
        
        try {
            Matcher matcher = HEX_PATTERN.matcher(message);
            StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
            
            while (matcher.find()) {
                String group = matcher.group(1);
                matcher.appendReplacement(buffer, 
                    ChatColor.COLOR_CHAR + "x" +
                    ChatColor.COLOR_CHAR + group.charAt(0) +
                    ChatColor.COLOR_CHAR + group.charAt(1) +
                    ChatColor.COLOR_CHAR + group.charAt(2) +
                    ChatColor.COLOR_CHAR + group.charAt(3) +
                    ChatColor.COLOR_CHAR + group.charAt(4) +
                    ChatColor.COLOR_CHAR + group.charAt(5)
                );
            }
            
            matcher.appendTail(buffer);
            return buffer.toString();
        } catch (Exception e) {
            return message;
        }
    }
    
    public static String stripColors(String message) {
        if (message == null) {
            return "";
        }
        
        return ChatColor.stripColor(formatMessage(message));
    }
    
    public static void sendActionBar(Player player, String message) {
        if (player == null || !player.isOnline() || message == null) {
            return;
        }
        
        try {
            if (VersionUtils.isNewerVersion(1, 11)) {
                player.spigot().sendMessage(
                    net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                    net.md_5.bungee.api.chat.TextComponent.fromLegacyText(formatMessage(message))
                );
            }
        } catch (Exception e) {
            sendMessage(player, message);
        }
    }
    
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player == null || !player.isOnline()) {
            return;
        }
        
        try {
            if (VersionUtils.isNewerVersion(1, 11)) {
                player.sendTitle(
                    formatMessage(title != null ? title : ""),
                    formatMessage(subtitle != null ? subtitle : ""),
                    fadeIn, stay, fadeOut
                );
            }
        } catch (Exception e) {
            if (title != null && !title.isEmpty()) {
                sendMessage(player, title);
            }
            if (subtitle != null && !subtitle.isEmpty()) {
                sendMessage(player, subtitle);
            }
        }
    }
    
    public static void broadcast(String message, String permission) {
        String formattedMessage = formatMessage(message);
        
        if (permission == null || permission.isEmpty()) {
            org.bukkit.Bukkit.broadcastMessage(formattedMessage);
        } else {
            org.bukkit.Bukkit.broadcast(formattedMessage, permission);
        }
    }
    
    public static String formatPlaceholders(String message, Object... placeholders) {
        if (message == null || placeholders == null) {
            return message;
        }
        
        String result = message;
        for (int i = 0; i < placeholders.length; i++) {
            result = result.replace("{" + i + "}", String.valueOf(placeholders[i]));
        }
        
        return result;
    }
    
    public static String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        } else {
            return String.format("%ds", seconds);
        }
    }
    
    public static String formatNumber(long number) {
        if (number >= 1_000_000_000) {
            return String.format("%.1fB", number / 1_000_000_000.0);
        } else if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000) {
            return String.format("%.1fK", number / 1_000.0);
        } else {
            return String.valueOf(number);
        }
    }
}