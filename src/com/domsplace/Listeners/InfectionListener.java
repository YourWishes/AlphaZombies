package com.domsplace.Listeners;

import com.domsplace.Commands.CommandAlphaZombies;
import com.domsplace.InfectionBase;
import static com.domsplace.InfectionBase.gK;
import com.domsplace.Objects.InfectionMap;
import com.domsplace.Utils.InfectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitTask;

public class InfectionListener extends InfectionBase implements Listener {
    
    public BukkitTask checkInfectionMaps;
    
    public InfectionListener() {
        checkInfectionMaps = Bukkit.getServer().getScheduler().runTaskTimer(this.getPlugin(), new Runnable() {
            public void run() {
                for(InfectionMap map : InfectionUtils.maps) {
                    map.tick();
                }
            }
        }, 60L, 20L);
    }
    
    
    @EventHandler(ignoreCancelled=true)
    public void onInteract(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        if(e.getClickedBlock() == null) {
            return;
        }
        
        Block clicked = e.getClickedBlock();
        if(clicked.getType() == null) {
            return;
        }
        
        if(clicked.getType() != Material.WALL_SIGN && clicked.getType() != Material.SIGN_POST) {
            return;
        }
        
        Sign sign = (Sign) clicked.getState();
        if(sign == null) {
            return;
        }
        
        InfectionMap map = InfectionUtils.getMapFromSign(sign);
        if(map == null) {
            return;
        }
        e.setCancelled(true);
        
        if(InfectionUtils.playerHasItems(e.getPlayer())) {
            e.getPlayer().sendMessage(gK("storeitems"));
            return;
        }
        
        if(!InfectionUtils.isVisible(e.getPlayer())) {
            e.getPlayer().sendMessage(gK("unvanishplease"));
            return;
        }
        
        if(map.isGameRunning()) {
            InfectionUtils.msgPlayer(e.getPlayer(), gK("gameinprogress"));
            return;
        }
        
        if(map.isGameFull()) {
            InfectionUtils.msgPlayer(e.getPlayer(), gK("gamefull"));
            return;
        }
        
        if(InfectionUtils.getPlayerArena(e.getPlayer()) != null) {
            InfectionUtils.msgPlayer(e.getPlayer(), gK("alreadyinarena"));
            return;
        }
        map.addPlayer(e.getPlayer());
    }
    
    @EventHandler
    public void onPlayerLeft(PlayerQuitEvent e) {
        onPlayerGone(e.getPlayer());
    }
    
    @EventHandler
    public void onPlayerKicked(PlayerKickEvent e) {
        onPlayerGone(e.getPlayer());
    }
    
    public void onPlayerGone(Player p) {
        InfectionMap map = InfectionUtils.getPlayerArena(p);
        
        if(map == null) {
            return;
        }
        
        map.removePlayer(p);
    }
    
    @EventHandler(ignoreCancelled=true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        InfectionMap map = InfectionUtils.getPlayerArena(e.getPlayer());
        
        if(map == null) {
            return;
        }
        
        if(!e.getMessage().toLowerCase().replaceAll(" ", "").equalsIgnoreCase("/leave")) {
            return;
        }
        
        e.setCancelled(true);
        map.removePlayer(e.getPlayer());
    }
    
    @EventHandler(ignoreCancelled=true)
    public void onPlayerMove(PlayerMoveEvent e) {
        InfectionMap map = InfectionUtils.getPlayerArena(e.getPlayer());
        
        if(map == null) {
            return;
        }
        
        if(!map.isGameRunning()) {
            e.setTo(map.getSpawn());
            return;
        }
        
        if(!map.isPlayerInfected(e.getPlayer())) {
            return;
        }
        
        if(!map.isZombieFrozen()) {
            return;
        }
        e.setTo(map.getSpawn());
    }
    
    @EventHandler(ignoreCancelled=true)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        InfectionMap map = InfectionUtils.getPlayerArena(e.getPlayer());
        
        if(map == null) {
            return;
        }
        
        e.setRespawnLocation(map.getSpawn());
    }
    
    @EventHandler(ignoreCancelled=true)
    public void onTeleport(PlayerTeleportEvent e) {
        InfectionMap map = InfectionUtils.getPlayerArena(e.getPlayer());
        InfectionMap tMap = InfectionUtils.getMapFromLocation(e.getTo());
        
        if(map == null) {
            if(tMap == null) {
                return;
            }
            
            e.setTo(e.getTo().getWorld().getSpawnLocation());
            InfectionUtils.msgPlayer(e.getPlayer(), gK("canttptoarena"));
            
            return;
        }
        
        if(tMap != null) {
            return;
        }
        
        e.setTo(map.getSpawn());
        InfectionUtils.msgPlayer(e.getPlayer(), gK("canttpoutarena"));
    }
    
    @EventHandler(ignoreCancelled=true)
    public void onPlayerWalk(PlayerMoveEvent e) {
        InfectionMap map = InfectionUtils.getMapFromLocation(e.getTo());
        
        if(map == null) {
            InfectionMap m = InfectionUtils.getPlayerArena(e.getPlayer());
            
            if(m == null) {
                return;
            }
            
            e.setTo(e.getFrom());
            e.getPlayer().getVelocity().multiply(-10);
            InfectionUtils.msgPlayer(e.getPlayer(), gK("cantleavearena"));
            if(InfectionUtils.getMapFromLocation(e.getTo()) == null) {
                e.setTo(m.getSpawn());
            }
            
            return;
        }
        
        InfectionMap m = InfectionUtils.getPlayerArena(e.getPlayer());
        
        if(m != null) {
            return;
        }
        
        e.setTo(e.getFrom());
        
        if(InfectionUtils.getMapFromLocation(e.getFrom()) != null) {
            e.setTo(e.getTo().getWorld().getSpawnLocation());
        }
        
        e.getPlayer().getVelocity().multiply(-100);
        e.getPlayer().getWorld().playEffect(e.getTo(), Effect.ENDER_SIGNAL, 1, 1);
        InfectionUtils.msgPlayer(e.getPlayer(), gK("cantwalkinto"));
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        
        InfectionMap m = InfectionUtils.getPlayerArena(p);
        
        if(m == null) {
            return;
        }
        
        e.setDeathMessage(null);
        
        Player killer = p.getKiller();
        
        if(!m.isPlayerInfected(p) && killer == null) {
            m.infect(p);
            m.addScore(p, -1);
            return;
        }
        
        if(!m.isPlayerInfected(p)) {
            m.infect(true, p, killer);
            m.addScore(killer, 1);
            return;
        }
        
        if(killer != null) {
            m.SendMessage(gK("killedzombie", killer).replaceAll("%p2%", p.getName()));
            m.addScore(killer, 1);
            return;
        }
        
        m.addScore(p, -1);
    }
    
    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent e) {
        if(e.getEntity() == null) {
            return;
        }
        
        if(e.getDamager() == null) {
            return;
        }
        
        if(e.getEntity().getType() != EntityType.PLAYER) {
            return;
        }
        
        if(e.getDamager().getType() != EntityType.PLAYER) {
            return;
        }
        
        Player player = (Player) e.getEntity();
        Player killer = (Player) e.getDamager();
        
        InfectionMap m = InfectionUtils.getPlayerArena(player);
        
        if(m == null) {
            return;
        }
        
        if(!m.isGameRunning()) {
            e.setCancelled(true);
            return;
        }
        
        if(m.isSameSide(player, killer)) {
            e.setCancelled(true);
            return;
        }
        
        if(m.isZombieFrozen()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerItemPickup(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        InfectionMap m = InfectionUtils.getPlayerArena(p);
        
        if(m == null) {
            return;
        }
        
        if(m.isPlayerInfected(p)) {
            e.setCancelled(true);
            return;
        }
    }
    
    @EventHandler(ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent e) {
        if(e.getPlayer() == null) {
            return;
        }
        
        if(!CommandAlphaZombies.step.containsKey(e.getPlayer()) || CommandAlphaZombies.step.get(e.getPlayer()) != 1) {
            return;
        }
        
        if(e.getBlockPlaced() == null) {
            return;
        }
        
        if(e.getBlockPlaced().getType() == null) {
            return;
        }
        
        Block clicked = e.getBlockPlaced();
        
        if(clicked.getType() != Material.WALL_SIGN && clicked.getType() != Material.SIGN_POST) {
            return;
        }
        
        CommandAlphaZombies.sign.put(e.getPlayer(), clicked);
        CommandAlphaZombies.step.remove(e.getPlayer());
        CommandAlphaZombies.step.put(e.getPlayer(), 2);
        e.getPlayer().sendMessage(ChatDefault + "Now stand where you want the spawn to be and type /alphazombies setspawn");
    }
}
