package com.domsplace.DataManagers;

import com.domsplace.Utils.InfectionUtils;
import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;

public class InfectionLanguageManager {
    
    public static File languageFile;
    public static YamlConfiguration language;
    public static YamlConfiguration defaultLanguage;
    
    public static boolean LoadLanguage() {
        try {
            languageFile = new File(InfectionUtils.plugin.getDataFolder() + "/messages.yml");
            if(!languageFile.exists()) {
                languageFile.createNewFile();
            }

            language = YamlConfiguration.loadConfiguration(languageFile);
            defaultLanguage = YamlConfiguration.loadConfiguration(languageFile);

            //Create Default Values
            cDV("nointeract", "%e%You cannot interact here.");
            cDV("joinedarena", "%i%%p% %d%joined the arena.");
            cDV("playerinfectedbyplayer", "%i%%p2% %d%infected %i%%p%%d%.");
            cDV("playerinfected", "%i%%p% %d%is now infected.");
            cDV("leftgame", "%i%%p% %d%left the Infection game.");
            cDV("noinfectedplayersnewinfected", "%i%%p% %d%was infected since all infected players left the game.");
            cDV("gamestarted", "%d%Game started! %i%%p%%d% is the infected player.");
            cDV("timebefore", "%e%You have %n% seconds before the zombie is loose!");
            cDV("allplayersinfected", "%d%Game Over, all players were infected. Scores:");
            cDV("timeout", "%d%Game over, the time ended. Scores:");
            cDV("startingin", "%d%Game starting in %i%%n%%d% seconds.");
            cDV("gameinprogress", "%e%You can't join a game in progress.");
            cDV("gamefull", "%e%You can't join the game, it's full.");
            cDV("alreadyinarena", "%e%You are already in an arena.");
            cDV("pvpnowon", "%d%The Zombie is now free! %i%Good luck!");
            cDV("notenoughplayers", "Game Over, not enough players to continue.");
            cDV("canttptoarena", "%e%Can't TP into an Arena.");
            cDV("canttpoutarena", "%e%Can't TP out of the Arena.");
            cDV("cantwalkinto", "%e%Can't walk into an Arena.");
            cDV("storeitems", "%e%Please store or remove your items before entering the arena.");
            cDV("unvanishplease", "%e%Please unvanish before entering.");
            cDV("killedzombie", "%i%%p% %d%killed the Zombie %i%%p2%%d%!");
            cDV("cantleavearena", "%e%Can't walk out of the arena.");
            
            //Save YML
            language.save(languageFile);
            
            return true;
        } catch(Exception ex) {
            InfectionUtils.Error("Failed to load messages.yml", ex.getLocalizedMessage());
            return false;
        }
    }
    
    public static void cDV(String key, Object defaultValue) {
        if(!language.contains(key)) {
            language.set(key, defaultValue);
        }
        defaultLanguage.set(key, defaultValue);
    }
    
}
