package com.domsplace.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class InfectionWorldeditUtils {
    public static boolean isWorldeditLoaded() {
        try {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
            if(plugin == null) {
                return false;
            }
            
            if(!(plugin instanceof com.sk89q.worldedit.bukkit.WorldEditPlugin)) {
                return false;
            }
            
            return ((com.sk89q.worldedit.bukkit.WorldEditPlugin) plugin) != null;
        } catch(NoClassDefFoundError e) {
            return false;
        }
    }
    
    public static com.sk89q.worldedit.bukkit.WorldEditPlugin getWorldEdit() {
        try {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
            if(plugin == null) {
                return null;
            }
            
            if(!(plugin instanceof com.sk89q.worldedit.bukkit.WorldEditPlugin)) {
                return null;
            }
            
            return (com.sk89q.worldedit.bukkit.WorldEditPlugin) plugin;
        } catch(NoClassDefFoundError e) {
            return null;
        }
    }
    
    public static Location getWandPosition1(Player player) {
        if(getWorldEdit() == null) {
            return null;
        }
        
        if(getWorldEdit().getSelection(player) == null) {
            return null;
        }
        
        if(getWorldEdit().getSelection(player).getMinimumPoint() == null) {
            return null;
        }
        return getWorldEdit().getSelection(player).getMinimumPoint();
    }
    
    public static Location getWandPosition2(Player player) {
        if(getWorldEdit() == null) {
            return null;
        }
        
        if(getWorldEdit().getSelection(player) == null) {
            return null;
        }
        
        if(getWorldEdit().getSelection(player).getMaximumPoint() == null) {
            return null;
        }
        return getWorldEdit().getSelection(player).getMaximumPoint();
    }
}
