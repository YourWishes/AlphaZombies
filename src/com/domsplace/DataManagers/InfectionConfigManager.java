package com.domsplace.DataManagers;

import com.domsplace.InfectionBase;
import com.domsplace.Utils.InfectionUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

public class InfectionConfigManager {
    public static YamlConfiguration config;
    public static File configFile;
    
    public static boolean LoadConfig() {
        try {
            if(!InfectionUtils.plugin.getDataFolder().exists()){
                InfectionUtils.plugin.getDataFolder().mkdir();
            }
            configFile = new File(InfectionUtils.plugin.getDataFolder() + "/config.yml");

            if(!configFile.exists()) {
                configFile.createNewFile();
            }
            
            config = YamlConfiguration.loadConfiguration(configFile);
            
            if(!config.contains("colors.prefix")) {
                config.set("colors.prefix", "&6[&cInfection&6]");
            }
            if(!config.contains("colors.error")) {
                config.set("colors.error", "&c");
            }
            if(!config.contains("colors.default")) {
                config.set("colors.default", "&6");
            }
            if(!config.contains("colors.important")) {
                config.set("colors.important", "&c");
            }
            if(!config.contains("colors.human")) {
                config.set("colors.human", "&a");
            }
            if(!config.contains("colors.zombie")) {
                config.set("colors.zombie", "&4");
            }
            if(!config.contains("colors.chatprefix")) {
                config.set("colors.chatprefix", "&6[&c%i%&6] ");
            }
            
            if(!config.contains("humanitems.items")) {
                List<String> items = new ArrayList<String>();
                items.add("268:0:1");
                items.add("261:0:1");
                items.add("262:0:64");
                items.add("297:0:10");
                config.set("humanitems.items", items);
            }
            if(!config.contains("humanitems.enchantments")) {
                List<String> items = new ArrayList<String>();
                items.add("DAMAGE_ALL:3");
                items.add("NOTHING");
                items.add("NOTHING");
                items.add("NOTHING");
                config.set("humanitems.enchantments", items);
            }
            if(!config.contains("humanitems.health")) {
                List<String> items = new ArrayList<String>();
                items.add("30");
                items.add("NOTHING");
                items.add("NOTHING");
                items.add("NOTHING");
                config.set("humanitems.health", items);
            }
            
            if(!config.contains("zombieeffects.effects")) {
                List<String> effects = new ArrayList<String>();
                
                effects.add("INCREASE_DAMAGE:2");
                effects.add("SPEED:1");
                
                config.set("zombieeffects.effects", effects);
            }
            
            if(!config.contains("spawn")) {
                Location s = Bukkit.getWorlds().get(0).getSpawnLocation();
                config.set("spawn.x", s.getX());
                config.set("spawn.y", s.getY());
                config.set("spawn.z", s.getZ());
                config.set("spawn.world", s.getWorld().getName());
                config.set("spawn.yaw", s.getYaw());
                config.set("spawn.pitch", s.getPitch());
            }
            
            InfectionBase.ChatError = InfectionUtils.ColorString(config.getString("colors.error"));
            InfectionBase.ChatPrefix = InfectionUtils.ColorString(config.getString("colors.prefix")) + " ";
            InfectionBase.ChatDefault = InfectionUtils.ColorString(config.getString("colors.default"));
            InfectionBase.ChatImportant = InfectionUtils.ColorString(config.getString("colors.important"));
            InfectionBase.HumanColor = InfectionUtils.ColorString(config.getString("colors.human"));
            InfectionBase.ZombieColor = InfectionUtils.ColorString(config.getString("colors.zombie"));
            InfectionBase.PlayerChatPrefix = InfectionUtils.ColorString(config.getString("colors.chatprefix"));
            InfectionBase.HubSpawn = InfectionUtils.getLocationFromMemorySection((MemorySection) config.get("spawn"));
            
            saveConfig();
        } catch (Exception ex) {
            InfectionUtils.Error("Failed to load config.", ex.getLocalizedMessage());
            return false;
        }
        return true;
    }
    
    public static void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException ex) {
            InfectionUtils.Error("Failed to save config.", ex.getLocalizedMessage());
        }
    }
}
