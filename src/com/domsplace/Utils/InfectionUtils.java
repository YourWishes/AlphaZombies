package com.domsplace.Utils;

import com.domsplace.DataManagers.InfectionConfigManager;
import com.domsplace.InfectionBase;
import com.domsplace.InfectionPlugin;
import com.domsplace.Objects.InfectionMap;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InfectionUtils extends InfectionBase {
    public static InfectionPlugin plugin;
    public static List<InfectionMap> maps;
    
    public static void broadcast(String message) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            msgPlayer(p, message);
        }
        msgConsole(message);
    }
    
    public static void broadcast(String permission, String message) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(!p.hasPermission(permission)) {
                continue;
            }
            
            msgPlayer(p, message);
        }
        msgConsole(message);
    }
    
    public static void broadcast(String permission, List<String> messages) {
        for(String s : messages) {
            broadcast(s);
        }
    }
    public static void broadcast(String permission, String[] messages) {
        for(String s : messages) {
            broadcast(s);
        }
    }
    
    public static void msgConsole(String message) {
        msgPlayer(Bukkit.getConsoleSender(), message);
    }
    
    public static void msgConsole(List<String> messages) {
        for(String message : messages) {
            msgPlayer(Bukkit.getConsoleSender(), message);
        }
    }
    
    public static void msgConsole(String[] messages) {
        for(String message : messages) {
            msgPlayer(Bukkit.getConsoleSender(), message);
        }
    }
    
    public static void msgPlayer(Player player, String message) {
        msgPlayer((CommandSender) player, message);
    }
    
    public static void msgPlayer(CommandSender player, String message) {
        player.sendMessage(ChatPrefix + ChatDefault + message);
    }
    
    public static void msgPlayer(CommandSender player, List<String> messages) {
        for(String s : messages) {
            msgPlayer(player, s);
        }
    }
    
    public static void msgPlayer(CommandSender player, String[] messages) {
        for(String s : messages) {
            msgPlayer(player, s);
        }
    }
    
    public static void Error(String reason, String cause) {
        msgConsole("§fError! §c" + reason + " Caused by " + cause);
    }
    
    public static String ColorString(String msg) {
        String[] andCodes = {"&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f", "&l", "&o", "&n", "&m", "&k", "&r"};
        String[] altCodes = {"§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f", "§l", "§o", "§n", "§m", "§k", "§r"};
        
        for(int x = 0; x < andCodes.length; x++) {
            msg = msg.replaceAll(andCodes[x], altCodes[x]);
        }
        
        return msg;
    }
    
    public static String getStringLocation (Location location) {
        return location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + " " + location.getWorld().getName();
    }
    
    public static String getStringLocation (Chunk chunk) {
        return chunk.getX() + ", " + chunk.getZ() + " : " + chunk.getWorld().getName();
    }
    
    public static boolean canSee(CommandSender player, CommandSender player0) {
        if(!(player instanceof Player)) {
            return true;
        }
        if(!(player0 instanceof Player)) {
            return true;
        }
        
        
        
        if(((Player) player).canSee((Player) player0)) {
            return true;
        }
        return false;
    }
    
    public static long getNow() {
        Date someTime = new Date();
        long currentMS = someTime.getTime();
        return currentMS;
    }

    public static Player getPlayer(CommandSender cs, String string) {
        Player p = Bukkit.getPlayer(string);
        if(p == null) {
            return null;
        }
        
        if(!canSee(cs, p)) {
            p = Bukkit.getPlayerExact(string);
            if(p == null) {
                return null;
            }
        }
        return p;
    }
    
    public static int getRandomNumberBetween(int min, int max) {
        Random foo = new Random();
        int randomNumber = foo.nextInt(max - min) + min;
        return randomNumber;
    }
    
    public static InfectionMap getMapFromSign(Sign sign) {
        for(InfectionMap map : maps) {
            if(map.isSign(sign)) {
                return map;
            }
        }
        return null;
    }
    
    public static InfectionMap getPlayerArena(Player player) {
        for(InfectionMap map : maps) {
            if(map.isPlayerInArena(player)) {
                return map;
            }
        }
        return null;
    }

    public static String removeDecimal(double timeAway) {
        DecimalFormat df = new DecimalFormat("######");
        return df.format(timeAway);
    }
    
    public static boolean isLocationInArea(Location area1, Location area2, Location location) {
        if(location.getWorld() != area1.getWorld()) {
            return false;
        }
        
        List<Block> blocks = getBlocksWithinLocations(area1, area2);
        for(Block b : blocks) {
            String loc = getStringLocation(location.getBlock().getLocation());
            String loc2 = getStringLocation(b.getLocation());
            if(loc.equalsIgnoreCase(loc2)) {
                return true;
            }
        }
        
        return false;
    }
    
    public static double getHigher(double loc1, double loc2) {
        if(loc1 >= loc2) {
            return loc1;
        } else if(loc2 >= loc1) {
            return loc2;
        } else {
            return loc1;
        }
    }
    
    public static double getLower(double loc1, double loc2) {
        if(loc1 <= loc2) {
            return loc1;
        } else if(loc2 <= loc1) {
            return loc2;
        } else {
            return loc1;
        }
    }
    
    public static List<Block> getBlocksWithinLocations(Location location1, Location location2) {
        List<Block> blocks = new ArrayList<Block>();
        
        double lowerX = getLower(location1.getBlockX(), location2.getBlockX());
        double higherX = getHigher(location1.getBlockX(), location2.getBlockX());
        
        double lowerY = getLower(location1.getBlockY(), location2.getBlockY());
        double higherY = getHigher(location1.getBlockY(), location2.getBlockY());
        
        double lowerZ = getLower(location1.getBlockZ(), location2.getBlockZ());
        double higherZ = getHigher(location1.getBlockZ(), location2.getBlockZ());
        
        for(int x = (int) lowerX; x <= higherX; x++) {
            for(int y = (int) lowerY; y <= higherY; y++) {
                for(int z = (int) lowerZ; z <= higherZ; z++) {
                    blocks.add(location1.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        
        return blocks;
    }
    
    public static boolean isLocationInArena(Location location, InfectionMap arena) {
         if(!isLocationInArea(arena.getLocation1(), arena.getLocation2(), location)) {
            return false;
        }
        return true;
    }
    
    public static InfectionMap getMapFromLocation(Location location) {
        for(InfectionMap m : maps) {
            if(isLocationInArena(location, m)) {
                return m;
            }
        }
        return null;
    }

    public static boolean playerHasItems(Player player) {
        for(ItemStack is : player.getInventory().getContents()) {
            if(is != null && is.getType() != Material.AIR) {
                return true;
            }
        }
        
        for(ItemStack is : player.getInventory().getArmorContents()) {
            if(is != null && is.getType() != Material.AIR) {
                return true;
            }
        }
        
        return false;
    }

    public static boolean isVisible(Player t) {
        Player[] playerList = Bukkit.getServer().getOnlinePlayers();
        for(int i = 0; i < playerList.length; i++) {
            Player p = playerList[i];
            
            if(p == t) {
                continue;
            }
            
            if(!canSee(p, t)) {
                return false;
            }
        }
        return true;
    }
    
    public static List<PotionEffect> getZombieEffects() {
        List<PotionEffect> effects = new ArrayList<PotionEffect>();
        
        for(String key : InfectionConfigManager.config.getStringList("zombieeffects.effects")) {
            String[] e = key.split(":");
            try {
                PotionEffect pe = new PotionEffect(PotionEffectType.getByName(e[0]), 80, Integer.parseInt(e[1]), true);
                effects.add(pe);
            } catch(Exception ex) {
                continue;
            }
        }
        
        return effects;
    }
    
    public static List<ItemStack> getHumanItems() {
        List<ItemStack> items = new ArrayList<ItemStack>();
        
        List<String> iss = InfectionConfigManager.config.getStringList("humanitems.items");
        List<String> ess = InfectionConfigManager.config.getStringList("humanitems.enchantments");
        List<String> dss = InfectionConfigManager.config.getStringList("humanitems.health");
        
        for(int i = 0; i < iss.size(); i++) {
            String is = iss.get(i);
            String es = ess.get(i);
            String ds = dss.get(i);
            
            String[] isI = is.split(":");
            String[] esI = es.split(":");
            
            try {
                
                ItemStack item = new ItemStack(Integer.parseInt(isI[0]));
                item.getData().setData(Byte.parseByte(isI[1]));
                item.setAmount(Integer.parseInt(isI[2]));
                
                if(esI.length == 2 && !esI[0].equalsIgnoreCase("NOTHING")) {
                    item.addEnchantment(Enchantment.getByName(esI[0]), Integer.parseInt(esI[1]));
                }
                if(!ds.equalsIgnoreCase("NOTHING")) {
                    item.setDurability(Short.parseShort(ds));
                }
                
                items.add(item);
                
            } catch(Exception ex) {
                continue;
            }
        }
        
        return items;
    }
    
    public static Location getLocationFromString(String string) {
        Location loc = null;
        
        String[] locs = string.split(", ");
        World w = Bukkit.getWorld(locs[2].split(" ")[1]);
        String[] coords = {locs[0], locs[1], locs[2].split(" ")[0]};
        loc = new Location(w, Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
        
        return loc;
    }

    public static Location getLocationFromMemorySection(MemorySection ms) {
        Location loc = new Location(Bukkit.getWorld(ms.getString("world")), ms.getDouble("x"), ms.getDouble("y"), ms.getDouble("z"), ms.getInt("yaw"), ms.getInt("pitch"));
        return loc;
    }
}
