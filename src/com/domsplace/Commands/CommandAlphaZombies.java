package com.domsplace.Commands;

import com.domsplace.DataManagers.InfectionConfigManager;
import com.domsplace.DataManagers.InfectionLanguageManager;
import com.domsplace.DataManagers.InfectionMapManager;
import com.domsplace.DataManagers.InfectionPluginManager;
import com.domsplace.InfectionBase;
import static com.domsplace.InfectionBase.ChatError;
import com.domsplace.Objects.InfectionMap;
import com.domsplace.Utils.InfectionUtils;
import com.domsplace.Utils.InfectionWorldeditUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAlphaZombies extends InfectionBase implements CommandExecutor {
    public static HashMap<Player, Integer> step;
    public static HashMap<Player, Location> position1;
    public static HashMap<Player, Location> position2;
    public static HashMap<Player, Block> sign;
    public static HashMap<Player, Location> spawn;
    public static HashMap<Player, List<Location>> spawns;
    
    public CommandAlphaZombies() {
        step = new HashMap<Player, Integer>();
        position1 = new HashMap<Player, Location>();
        position2 = new HashMap<Player, Location>();
        sign = new HashMap<Player, Block>();
        spawn = new HashMap<Player, Location>();
        spawns = new HashMap<Player, List<Location>>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("alphazombies")) {
            if(args.length ==0) {
                InfectionUtils.msgPlayer(sender, InfectionPluginManager.getName() + " version " + InfectionPluginManager.getVersion() + ".");
                return true;
            }
            
            String lbl = args[0].toLowerCase();
            
            if(lbl.equalsIgnoreCase("reload")) {
                InfectionUtils.msgPlayer(sender, "Reloading config...");
                InfectionConfigManager.LoadConfig();
                InfectionLanguageManager.LoadLanguage();
                
                List<InfectionMap> maps = new ArrayList<InfectionMap>(InfectionUtils.maps);
                for(InfectionMap map : maps) {
                    map.EndGame("Plugin Reloading.");
                    map = null;
                }
                
                InfectionUtils.maps = new ArrayList<InfectionMap>();
                InfectionMapManager.LoadAllMaps();
                
                return true;
            }
            
            if(lbl.equalsIgnoreCase("new")) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(ChatError + "Only players can run this command.");
                    return false;
                }
                
                Player p = (Player) sender;
                
                //Try to get WorldEdit
                if(!InfectionWorldeditUtils.isWorldeditLoaded()) {
                    sender.sendMessage(ChatError + "WorldEdit isn't loaded! can't make an arena.");
                    return true;
                }
                
                /*
                if(step.containsKey(p) && step.get(p) >= 0) {
                    sender.sendMessage(ChatError + "You're already making an arena.");
                    return true;
                }*/
                
                step.remove(p);
                position1.remove(p);
                position2.remove(p);
                
                step.put(p, 0);
                sender.sendMessage(ChatDefault + "Set your WorldEdit wand positions then type /" + label + " setlocation");
                return true;
            }
            
            if(lbl.equalsIgnoreCase("setlocation")) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(ChatError + "Only players can run this command.");
                    return false;
                }
                
                Player p = (Player) sender;
                
                if(!step.containsKey(p) || step.get(p) != 0) {
                    sender.sendMessage(ChatError + "Please run /" + label + " new");
                    return true;
                }
                
                Location wand1 = InfectionWorldeditUtils.getWandPosition1(p);
                Location wand2 = InfectionWorldeditUtils.getWandPosition2(p);
                
                if(wand1 == null || wand2 == null) {
                    sender.sendMessage(ChatError + "Please select a Worldedit region.");
                    return true;
                }
                
                List<Block> blocksInRegion = InfectionUtils.getBlocksWithinLocations(wand1, wand2);
                for(Block b : blocksInRegion) {
                    InfectionMap tryMap = InfectionUtils.getMapFromLocation(b.getLocation());
                    if(tryMap == null) {
                        continue;
                    }
                    sender.sendMessage(ChatError + "Can't make an arena here, it overlaps another arena.");
                    return true;
                }
                
                position1.remove(p);
                position1.put(p, wand1);
                position2.remove(p);
                position2.put(p, wand2);
                
                step.remove(p);
                step.put(p, 1);
                
                sender.sendMessage(ChatError + "Now place a sign where you want the lobby to be.");
                return true;
            }
            
            if(lbl.equalsIgnoreCase("setspawn")) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(ChatError + "Only players can run this command.");
                    return false;
                }
                
                Player p = (Player) sender;
                
                if(!step.containsKey(p) || step.get(p) != 2) {
                    sender.sendMessage(ChatError + "Please run /" + label + " new");
                    return true;
                }
                
                Location s = p.getLocation();
                
                spawn.remove(p);
                spawn.put(p, s);
                
                step.remove(p);
                step.put(p, 3);
                sender.sendMessage(ChatDefault + "Now type " + ChatImportant + "/" + label + " addspawn " + ChatDefault + "for each spawn location.");
                return true;
            }
            
            if(lbl.equalsIgnoreCase("addspawn")) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(ChatError + "Only players can run this command.");
                    return false;
                }
                
                Player p = (Player) sender;
                
                if(!step.containsKey(p) || step.get(p)< 3) {
                    sender.sendMessage(ChatError + "Please run /" + label + " new");
                    return true;
                }
                
                if(!spawns.containsKey(p)) {
                    spawns.put(p, new ArrayList<Location>());
                }
                
                List<Location> sp = spawns.get(p);
                
                sp.add(p.getLocation());
                
                step.remove(p);
                step.put(p, 4);
                
                sender.sendMessage(ChatDefault + "Added a new spawn location. When you're done, type " + ChatImportant + "/" + label + " save");
                return true;
            }
            
            if(lbl.equalsIgnoreCase("save")) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(ChatError + "Only players can run this command.");
                    return false;
                }
                
                Player p = (Player) sender;
                
                if(!step.containsKey(p) || step.get(p) != 4) {
                    sender.sendMessage(ChatError + "Please run /" + label + " new");
                    return true;
                }
                
                if(args.length < 2) {
                    sender.sendMessage(ChatError + "Please enter a name for the arena.");
                    return true;
                }
                
                int size = 16;
                
                if(args.length == 3) {
                    try {
                        size = Integer.parseInt(args[2]);
                    } catch(Exception ex) {
                        sender.sendMessage(ChatError + "Size of arena must be a number.");
                        return true;
                    }
                }
                
                String name = args[1];
                
                if(!name.matches("^[a-zA-Z0-9]*$")) {
                    sender.sendMessage(ChatError + "Name must only contain letters and numbers.");
                    return true;
                }
                
                if(name.length() > 12) {
                    sender.sendMessage(ChatError + "Name too long. Please keep it 12 or less characters long.");
                    return true;
                }
                
                InfectionMap map = new InfectionMap();
                map.setName(name);
                map.setMaxPlayers(size);
                map.setLocation1(position1.get(p));
                map.setLocation2(position2.get(p));
                map.setSign((Sign) sign.get(p).getState());
               
                for(Location loc : spawns.get(p)) {
                    map.addSpawnLocation(loc);
                }
                map.setSpawn(spawn.get(p));
                
                InfectionUtils.maps.add(map);
                sender.sendMessage(ChatDefault + "Made arena.");
                InfectionMapManager.SaveAllMaps();
                return true;
            }
            
            sender.sendMessage(ChatError + "Invalid argument.");
            return false;
        }
        
        return false;
    }
}
