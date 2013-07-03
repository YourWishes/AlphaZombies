package com.domsplace.Listeners;

import com.domsplace.Commands.CommandAlphaZombies;
import com.domsplace.InfectionBase;
import static com.domsplace.InfectionBase.gK;
import com.domsplace.Objects.InfectionMap;
import com.domsplace.Utils.InfectionUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InfectionAnitBuildListener extends InfectionBase implements Listener {
    
    public InfectionAnitBuildListener() {
    }
    
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInteract(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null) {
            return;
        }
        
        Block clicked = e.getClickedBlock();
        if(clicked.getType() == null) {
            return;
        }
        
        InfectionMap m = InfectionUtils.getMapFromLocation(clicked.getLocation());
        if(m == null) {
            
            if(clicked.getType() != Material.WALL_SIGN && clicked.getType() != Material.SIGN_POST) {
                return;
            }

            Sign sign = (Sign) clicked.getState();
            if(sign == null) {
                return;
            }
            
            m = InfectionUtils.getMapFromSign(sign);
            
            if(m == null) {
                return;
            }
            
            e.setCancelled(true);
            e.getPlayer().sendMessage(gK("nointeract"));
            
            return;
        }
        
        e.setCancelled(true);
        e.getPlayer().sendMessage(gK("nointeract"));
    }
    
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInteract(BlockBreakEvent e) {
        if(e.getBlock() == null) {
            return;
        }
        
        Block clicked = e.getBlock();
        if(clicked.getType() == null) {
            return;
        }
        
        InfectionMap m = InfectionUtils.getMapFromLocation(clicked.getLocation());
        if(m == null) {
            
            if(clicked.getType() != Material.WALL_SIGN && clicked.getType() != Material.SIGN_POST) {
                return;
            }

            Sign sign = (Sign) clicked.getState();
            if(sign == null) {
                return;
            }
            
            m = InfectionUtils.getMapFromSign(sign);
            
            if(m == null) {
                if(CommandAlphaZombies.sign.containsValue(clicked)) {
                    e.getPlayer().sendMessage(gK("nointeract"));
                    e.setCancelled(true);
                }
                return;
            }
            
            e.setCancelled(true);
            e.getPlayer().sendMessage(gK("nointeract"));
            
            return;
        }
        
        e.setCancelled(true);
        e.getPlayer().sendMessage(gK("nointeract"));
    }
    
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInteract(BlockPlaceEvent e) {
        if(e.getBlockPlaced() == null) {
            return;
        }
        
        List<Block> blocks = new ArrayList<Block>();
        blocks.add(e.getBlock());
        blocks.add(e.getBlockAgainst());
        blocks.add(e.getBlockPlaced());
        
        for(Block clicked : blocks) {
            if(clicked.getType() == null) {
                continue;
            }

            InfectionMap m = InfectionUtils.getMapFromLocation(clicked.getLocation());
            if(m == null) {

                if(clicked.getType() != Material.WALL_SIGN && clicked.getType() != Material.SIGN_POST) {
                    continue;
                }

                Sign sign = (Sign) clicked.getState();
                if(sign == null) {
                    continue;
                }

                m = InfectionUtils.getMapFromSign(sign);

                if(m == null) {
                    continue;
                }

                e.setCancelled(true);
                e.getPlayer().sendMessage(gK("nointeract"));

                return;
            }

            e.setCancelled(true);
            e.getPlayer().sendMessage(gK("nointeract"));
            return;
        }
    }
}
