package com.domsplace.Utils;

import com.domsplace.DataManagers.InfectionLanguageManager;
import com.domsplace.InfectionBase;
import org.bukkit.OfflinePlayer;

public class InfectionLanguageUtils extends InfectionBase {
    
    public static String getKey(String key) {
        String l = InfectionLanguageManager.defaultLanguage.getString(key);
        if(InfectionLanguageManager.language.contains(key)) {
            l = InfectionLanguageManager.language.getString(key);
        }
        
        if(l == null) {
            l = "§cVillages message not set! Contact Server administrator! MISKEY: " + key;
            return l;
        }
        
        l = l.replaceAll("%e%", ChatError).replaceAll("%d%", ChatDefault).replaceAll("%i%", ChatImportant);
        l = InfectionUtils.ColorString(l);
        
        return l;
    }
    
    public static String getKey(String key, OfflinePlayer player) {
        String l = getKey(key);
        
        l = l.replaceAll("%p%", player.getName());
        
        return l;
    }
    
    public static String getKey(String key, OfflinePlayer player, OfflinePlayer player2) {
        String l = getKey(key, player);
        
        l = l.replaceAll("%p2%", player2.getName());
        
        return l;
    }
}
