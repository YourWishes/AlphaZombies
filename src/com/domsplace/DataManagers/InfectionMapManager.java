package com.domsplace.DataManagers;

import com.domsplace.InfectionBase;
import com.domsplace.Objects.InfectionMap;
import com.domsplace.Utils.InfectionUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

public class InfectionMapManager {
    
    public static void SaveMap(InfectionMap map) {
        try {
            File f = new File(InfectionUtils.plugin.getDataFolder() + "/maps/");
            
            if(!f.exists()) {
                f.mkdir();
            }
            
            f = new File(InfectionUtils.plugin.getDataFolder() + "/maps/" + map.getMapName() + ".yml");
            if(!f.exists()) {
                f.createNewFile();
            }
            
            map.getArenaAsYML().save(f);
        } catch(Exception ex) {
            InfectionUtils.Error("Failed to save map \"" + map.getMapName() + "\"", ex.getLocalizedMessage());
        }
    }
    
    public static void SaveAllMaps() {
        for(InfectionMap map : InfectionUtils.maps) {
            SaveMap(map);
        }
    }
    
    public static void LoadMap(String name) {
        try {
            File f = new File(InfectionUtils.plugin.getDataFolder() + "/maps/");

            if(!f.exists()) {
                f.mkdir();
            }

            f = new File(InfectionUtils.plugin.getDataFolder() + "/maps/" + name);

            YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);

            InfectionMap map = new InfectionMap();
            map.setMaxPlayers(yml.getInt("maxplayers"));
            map.setName(yml.getString("name"));
            map.setLocation1(InfectionUtils.getLocationFromString(yml.getString("location.position1")));
            map.setLocation2(InfectionUtils.getLocationFromString(yml.getString("location.position2")));
            map.setSign((Sign) InfectionUtils.getLocationFromString(yml.getString("location.sign")).getBlock().getState());
            map.setSpawn(InfectionUtils.getLocationFromMemorySection((MemorySection) yml.get("location.spawn")));
            
            if(yml.contains("spawns")) {
                for(String s : ((MemorySection) yml.get("spawns")).getKeys(false)) {
                    Location loc = InfectionUtils.getLocationFromMemorySection((MemorySection) yml.get("spawns." + s));
                    map.addSpawnLocation(loc);
                }
            }

            InfectionUtils.maps.add(map);
        } catch(Exception ex) {
            InfectionUtils.Error("Failed to load Map " + name.replaceAll(".yml", ""), ex.getMessage());
        }
    }
    
    public static void LoadAllMaps() {
        File f = new File(InfectionUtils.plugin.getDataFolder() + "/maps/");
        if(!f.exists()) {
            f.mkdir();
        }
        
        List<String> names = new ArrayList<String>(Arrays.asList(f.list()));
        for(String s : names) {
            LoadMap(s);
        }
    }
}
