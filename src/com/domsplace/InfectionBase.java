package com.domsplace;

import com.domsplace.Utils.InfectionLanguageUtils;
import com.domsplace.Utils.InfectionUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

public class InfectionBase {
    public static String ChatError = ChatColor.RED + "";
    public static String ChatDefault = ChatColor.GRAY + "";
    public static String ChatImportant = ChatColor.BLUE + "";
    public static String ChatPrefix = "§6[§cInfection§6] ";
    public static String HumanColor = ChatColor.GREEN + "";
    public static String ZombieColor = ChatColor.DARK_RED + "";
    public static String PlayerChatPrefix = "§6[§c%v%§6] ";
    
    public static int InvincibleTime = 10;
    public static int MaxPlayers = 16;
    
    public static Location HubSpawn;
    
    public static String gK(String key) {
        return InfectionLanguageUtils.getKey(key);
    }
    
    public static String gK(String key, OfflinePlayer player) {
        return InfectionLanguageUtils.getKey(key, player);
    }
    
    public static String gK(String key, OfflinePlayer player, OfflinePlayer player2) {
        return InfectionLanguageUtils.getKey(key, player, player2);
    }
    
    public static void debug(String message) {
        InfectionUtils.broadcast("§a[§bDEBUG§a] §d" + message);
    }
    
    public InfectionPlugin getPlugin() {
        return InfectionUtils.plugin;
    }
}
