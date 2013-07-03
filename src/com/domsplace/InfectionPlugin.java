package com.domsplace;

import com.domsplace.Commands.CommandAlphaZombies;
import com.domsplace.DataManagers.InfectionConfigManager;
import com.domsplace.DataManagers.InfectionLanguageManager;
import com.domsplace.DataManagers.InfectionMapManager;
import com.domsplace.DataManagers.InfectionPluginManager;
import com.domsplace.Listeners.InfectionAnitBuildListener;
import com.domsplace.Listeners.InfectionListener;
import com.domsplace.Objects.InfectionMap;
import com.domsplace.Utils.InfectionUtils;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class InfectionPlugin extends JavaPlugin {
    
    public static boolean isLoaded = false;
    public static PluginManager pluginManager;
    
    //Commands
    public static CommandAlphaZombies CommandAlphaZombies;
    
    //Listeners
    public static InfectionListener ListenerInfection;
    public static InfectionAnitBuildListener ListenerAntiBuild;
    
    @Override
    public void onEnable() {
        InfectionUtils.plugin = InfectionPlugin.getInfectionPlugin();
        InfectionUtils.maps = new ArrayList<InfectionMap>();
        
        InfectionPluginManager.LoadPluginYML();
        
        pluginManager = Bukkit.getPluginManager();
        
        if(!InfectionConfigManager.LoadConfig()) {
            Disable();
            return;
        }
        if(!InfectionLanguageManager.LoadLanguage()) {
            Disable();
            return;
        }
        
        //Load Commands
        CommandAlphaZombies = new CommandAlphaZombies();
        
        //Register Commands
        getCommand("alphazombies").setExecutor(CommandAlphaZombies);
        
        //Load Listeners
        ListenerInfection = new InfectionListener();
        ListenerAntiBuild = new InfectionAnitBuildListener();
        
        //Register Listeners
        pluginManager.registerEvents(ListenerInfection, this);
        pluginManager.registerEvents(ListenerAntiBuild, this);
        
        InfectionMapManager.LoadAllMaps();
        InfectionMapManager.SaveAllMaps();
        
        //Plugin Successfully Loaded.
        isLoaded = true;
        
        InfectionUtils.broadcast(
            "Infection.admin", 
            "§dLoaded " + InfectionPluginManager.getName() + 
            " version " + InfectionPluginManager.getVersion() + 
            " successfully."
        );
    }
    
    @Override
    public void onDisable() {
        if(!isLoaded) {
            InfectionUtils.Error("Failed to load plugin!", "Check console for more information.");
        }
        
        ListenerInfection.checkInfectionMaps.cancel();
        
        for(InfectionMap map : InfectionUtils.maps) {
            map.EndGame(InfectionBase.ChatError + "Plugin disabled (Perhaps the server's restarting?)");
        }
    }
    
    public void Disable() {
        pluginManager.disablePlugin(this);
    }
    
    //Self Referencing
    public static com.domsplace.InfectionPlugin getInfectionPlugin() {
        try {
            Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("AlphaZombies");

            if (plugin == null || !(plugin instanceof com.domsplace.InfectionPlugin)) {
                return null;
            }

            return (com.domsplace.InfectionPlugin) plugin;
        } catch(NoClassDefFoundError e) {
            return null;
        }
    }
}
