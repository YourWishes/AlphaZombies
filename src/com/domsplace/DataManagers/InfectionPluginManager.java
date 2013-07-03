package com.domsplace.DataManagers;

import com.domsplace.Utils.InfectionUtils;
import java.io.InputStream;
import org.bukkit.configuration.file.YamlConfiguration;

public class InfectionPluginManager {
    
    public static InputStream PluginYMLIS;
    public static YamlConfiguration PluginYML;
    
    public static void LoadPluginYML() {
        PluginYMLIS = InfectionUtils.plugin.getResource("plugin.yml");
        PluginYML = YamlConfiguration.loadConfiguration(PluginYMLIS);
    }
    
    public static String getVersion() {
        return PluginYML.getString("version");
    }
    
    public static String getName() {
        return PluginYML.getString("name");
    }
    
}
