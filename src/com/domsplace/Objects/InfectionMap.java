package com.domsplace.Objects;

import com.domsplace.InfectionBase;
import static com.domsplace.InfectionBase.InvincibleTime;
import static com.domsplace.InfectionBase.gK;
import com.domsplace.Utils.InfectionUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class InfectionMap extends InfectionBase {
    
    private Location location1;
    private Location location2;
    private List<Location> spawnLocations;
    private Sign sign;
    private Location spawn;
    
    private Map<Player, Integer> scores;
    private List<Player> infectedPlayers;
    private List<Player> players;
    private int maxPlayers = MaxPlayers;
    
    private boolean isGameRunning;      //IS the game running
    private long gameStartTime = 0L;    //When did the CURRENT game start.
    private long gameReadyTime = 0L;    //When did the game reach a ready state (more than 2 players in arena)
    private long gameLengthTime = 300000;
    private String mapName = "Infection";
    
    private Scoreboard scoreboard;
    private ScoreboardManager sbManager;
    private Objective scoreObjective;
    
    private int LockZombieTimer = 0;
    private long gameDelay = 1000;
    
    public InfectionMap() {
        spawnLocations = new ArrayList<Location>();
        Reset();
        
        sbManager = Bukkit.getScoreboardManager();
        scoreboard = sbManager.getNewScoreboard();
        
        scoreObjective = scoreboard.registerNewObjective("InfectionScores", "dummy");
        scoreObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreObjective.setDisplayName(ChatDefault + " = " + ChatImportant + "Scores" + ChatDefault + " = ");
    }
    
    private void Reset() {
        scores = new HashMap<Player, Integer>();
        infectedPlayers = new ArrayList<Player>();
        players = new ArrayList<Player>();
        
        isGameRunning = false;
        gameStartTime = 0L;
        gameReadyTime = 0L;
        this.LockZombieTimer = 0;
    }
    
    public void tick() {
        FixSign();
        
        long now = InfectionUtils.getNow();
        long gameEnd = gameStartTime + gameLengthTime;
        
        if(!this.isGameRunning()) {
            if(this.getCurrentPlayers() < 2) {
                this.gameReadyTime = 0L;
                FixSign();
                return;
            } else if(this.gameReadyTime <= 0L) {
                this.gameReadyTime = now;
            }
            
            long startGameTime = this.gameReadyTime + gameDelay;
            
            if(now >= startGameTime) {
                StartGame();
                FixSign();
                return;
            }
            
            long l = ((this.gameReadyTime + gameDelay - now) / 1000);
            
            double timeAway = Math.ceil(Double.parseDouble(l + ""));
            
            if(timeAway == 30) {
                this.SendMessage(gK("startingin").replaceAll("%n%", ((int) timeAway) + ""));
            } else if(timeAway == 20) {
                this.SendMessage(gK("startingin").replaceAll("%n%", ((int) timeAway) + ""));
            } else if(timeAway == 15) {
                this.SendMessage(gK("startingin").replaceAll("%n%", ((int) timeAway) + ""));
            } else if(timeAway == 10) {
                this.SendMessage(gK("startingin").replaceAll("%n%", ((int) timeAway) + ""));
            } else if(timeAway <= 5) {
                this.SendMessage(gK("startingin").replaceAll("%n%", InfectionUtils.removeDecimal(timeAway)));
            }
            FixSign();
            return;
        }
        
        if(this.LockZombieTimer <= InvincibleTime) {
            this.LockZombieTimer++;
        }
        
        if(this.LockZombieTimer == InvincibleTime) {
            this.SendMessage(gK("pvpnowon"));
        }
        
        if(this.areAllPlayersInfected()) {
            this.EndGame(gK("allplayersinfected"));
            FixSign();
            return;
        }
        
        for(Player p : this.getinfectedPlayers()) {
            p.addPotionEffects(InfectionUtils.getZombieEffects());
        }
        
        if(now >= gameEnd) {
            this.EndGame(gK("timeout"));
        }
    }
    
    public InfectionMap StartGame() {
        isGameRunning = true;
        Player infectedPlayer = this.infectRandomPlayer();
        
        this.SendMessage(gK("gamestarted", infectedPlayer));
        this.SendMessage(gK("timebefore").replaceAll("%n%", InvincibleTime + ""));
        
        this.gameStartTime = InfectionUtils.getNow();
        
        List<String> messages = new ArrayList<String>();
        messages.add("New Infection game started on " + this.getMapName() + ". Players:");
        for(Player p : this.getPlayers()) {
            messages.add(p.getName());
            
            if(!this.isPlayerInfected(p)) {
                this.GiveHumanItems(p);
            }
        }
        InfectionUtils.msgConsole(messages);
        
        FixSign();
        return this;
    }
    
    public InfectionMap EndGame(String reason) {
        isGameRunning = false;
        //Send Scores//
        List<String> scoreMessages = new ArrayList<String>();
        scoreMessages.add(reason);
        
        for(Player p : this.getPlayers()) {
            scoreMessages.add(ChatImportant + p.getName() + ChatDefault + " : " + ChatImportant + this.getScore(p));
        }
        this.SendMessage(scoreMessages);
        InfectionUtils.msgConsole(scoreMessages);
        
        List<Player> plyrs = new ArrayList<Player>(this.getPlayers());
        
        Reset();
        
        for(Player p : plyrs) {
            if(HubSpawn == null) {
                HubSpawn = this.getRandomSpawnLocation().getWorld().getSpawnLocation();
            }
            p.setScoreboard(sbManager.getNewScoreboard());
            this.scoreboard.resetScores(p);
            
            if(p.isDead()) {
                continue;
            }
            p.teleport(HubSpawn);
            this.ResetInventory(p);
        }
        
        FixSign();
        return this;
    }
    
    public boolean areAllPlayersInfected() {
        if(this.getinfectedPlayers().size() == this.getPlayers().size()) {
            return true;
        }
        return false;
    }
    
    public List<Location> getSpawnLocations() {
        return this.spawnLocations;
    }
    
    public Location getSpawn() {
        return this.spawn;
    }
    
    public InfectionMap setSpawn(Location spawn) {
        if(this.getSpawn() != null && this.getSpawnLocations().contains(spawn)) {
            this.getSpawnLocations().remove(spawn);
        }
        
        this.spawn = spawn;
        
        this.getSpawnLocations().add(spawn);
        
        return this;
    }
    
    public Location getRandomSpawnLocation() {
        Location l = this.getSpawnLocations().get(InfectionUtils.getRandomNumberBetween(0, this.getSpawnLocations().size()));
        
        return l;
    }
    
    public void SendMessage(String message) {
        for(Player p : this.getPlayers()) {
            InfectionUtils.msgPlayer(p, message);
        }
    }
    public void SendMessage(List<String> messages) {
        for(Player p : this.getPlayers()) {
            InfectionUtils.msgPlayer(p, messages);
        }
    }
    
    public boolean isGameFull() {
        int cPlayers = this.getCurrentPlayers();
        if(cPlayers >= this.getMaxPlayers()) {
            return true;
        }
        return false;
    }
    
    public int getCurrentPlayers() {
        return this.getPlayers().size();
    }
    
    public List<Player> getPlayers() {
        return this.players;
    }
    
    public InfectionMap addPlayer(Player player) {
        return addPlayer(true, player);
    }
    
    public InfectionMap addPlayer(boolean shouldBroadcast, Player player) {
        this.getPlayers().add(player); 
        this.SendMessage(gK("joinedarena", player));
        this.scores.put(player, 0);
        player.teleport(this.getSpawn());
        
        player.setScoreboard(scoreboard);
        scoreboard.resetScores(player);
        Score score = this.scoreObjective.getScore(player);
        score.setScore(this.getScore(player));
        
        FixSign();
        return this;
    }
    
    public List<Player> getinfectedPlayers() {
        return this.infectedPlayers;
    }
    
    public Player infect(Player player) {
        return infect(true, player);
    }
    
    public Player infect(boolean shouldBroadcast, Player player) {
        this.infectedPlayers.add(player);
        
        if(shouldBroadcast) {
            this.SendMessage(gK("playerinfected", player));
        }
        
        return player;
    }
    
    public Player infect(boolean shouldBroadcast, Player infected, Player infectee) {
        if(shouldBroadcast) {
            this.SendMessage(gK("playerinfectedbyplayer", infected, infectee));
        }
        return infect(false, infected);
    }
    
    public Player infectRandomPlayer() {
        Player p = this.getPlayers().get(InfectionUtils.getRandomNumberBetween(0, this.getPlayers().size()));
        
        return infect(false, p);
    }
    
    public InfectionMap removePlayer(Player player) {
        return removePlayer(true, player);
    }
    
    public InfectionMap removePlayer(boolean shouldBroadcast, Player player) {
        this.scores.remove(player);
        
        player.setScoreboard(sbManager.getNewScoreboard());
        
        if(this.infectedPlayers.contains(player)) {
            this.infectedPlayers.remove(player);
        }
        
        if(shouldBroadcast) {
            this.SendMessage(gK("leftgame", player));
        }
        
        //Check if that was the last infected Player, if so I need to infect a new player
        if(this.infectedPlayers.size() <= 0 && this.isGameRunning()) {
            Player p = this.infectRandomPlayer();
            p.setHealth(0);
            if(shouldBroadcast) {
                this.SendMessage(gK("noinfectedplayersnewinfected", p));
            }
        }
        this.players.remove(player);
        if(HubSpawn == null) {
            HubSpawn = this.getRandomSpawnLocation().getWorld().getSpawnLocation();
        }
        
        if(!player.isDead()) {
            player.teleport(HubSpawn);
        }
        
        if(this.getCurrentPlayers() < 2 && this.isGameRunning()) {
            this.EndGame(gK("notenoughplayers"));
        }
        
        this.ResetInventory(player);
        return this;
    }
    
    public int getMaxPlayers() {
        return this.maxPlayers;
    }
    
    public InfectionMap setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        return this;
    }
    
    public int getScore(Player player) {
        FixScoreboard();
        return this.scores.get(player);
    }
    
    public void FixScoreboard() {
        for(Player player : this.getPlayers()) {
            player.setScoreboard(scoreboard);
            scoreboard.resetScores(player);
            player.setScoreboard(scoreboard);
            Score sc = this.scoreObjective.getScore(player);
            sc.setScore(this.scores.get(player));

            scoreObjective.setDisplayName(ChatDefault + " = " + ChatImportant + "Scores (" + this.getinfectedPlayers().size() + "/" + this.getCurrentPlayers() + ")" + ChatDefault + " = ");
        }
    }
    
    public InfectionMap setScore(Player player, int score) {
        this.scores.put(player, score);
        
        FixScoreboard();
        
        return this;
    }
    
    public InfectionMap addScore(Player player, int amount) {
        this.setScore(player, this.getScore(player) + amount);
        return this;
    }
    
    public Location getLocation1() {
        return this.location1;
    }
    
    public Location getLocation2() {
        return this.location2;
    }
    
    public Sign getSign() {
        return this.sign;
    }
    
    public InfectionMap setLocation1(Location location) {
        this.location1 = location;
        return this;
    }
    
    public InfectionMap setLocation2(Location location) {
        this.location2 = location;
        return this;
    }
    
    public InfectionMap setSign(Sign location) {
        this.sign = location;
        FixSign();
        return this;
    }

    public boolean isGameRunning() {
        return this.isGameRunning;
    }
    
    public boolean isPlayerInArena(Player p) {
        return this.getPlayers().contains(p);
    }
    
    public boolean isPlayerInfected(Player p) {
        return this.getinfectedPlayers().contains(p);
    }
    
    public boolean isSign(Sign sign) {
        if(sign.equals(this.getSign())) {
            return true;
        }
        return false;
    }
    
    public String getMapName() {
        return this.mapName;
    }
    
    public InfectionMap setName(String name) {
        this.mapName = name;
        return this;
    }
    
    public void FixSign() {
        this.sign.setLine(0, this.getMapName());
        this.sign.setLine(1, this.getCurrentPlayers() + "/" + this.getMaxPlayers());
        
        if(this.isGameRunning()) {
            this.sign.setLine(3, ChatColor.GREEN + "Game running.");
            this.sign.setLine(2, "");
        } else {
            this.sign.setLine(2, "Right Click!");
            this.sign.setLine(3, ChatColor.DARK_GREEN + "Game waiting.");
        }
        
        this.sign.update();
    }

    public boolean isZombieFrozen() {
        if(this.LockZombieTimer < InvincibleTime) {
            return true;
        }
        return false;
    }

    public boolean isSameSide(Player p, Player killer) {
        if(this.isPlayerInfected(p) && this.isPlayerInfected(killer)) {
            return true;
        }
        
        if(!this.isPlayerInfected(p) && !this.isPlayerInfected(killer)) {
            return true;
        }
        
        return false;
    }
    
    public void ResetInventory(Player player) {
        player.getInventory().clear();
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
    }
    
    public void GiveHumanItems(Player player) {
        for(ItemStack is : InfectionUtils.getHumanItems()) {
            player.getInventory().addItem(is);
        }
    }
    
    public YamlConfiguration getArenaAsYML() {
        YamlConfiguration yml = new YamlConfiguration();
        
        yml.set("location.position1", InfectionUtils.getStringLocation(this.location1));
        yml.set("location.position2", InfectionUtils.getStringLocation(this.location2));
        yml.set("location.sign", InfectionUtils.getStringLocation(this.sign.getLocation()));
        yml.set("maxplayers", this.getMaxPlayers());
        yml.set("name", this.getMapName());
        yml.set("location.spawn.x", this.getSpawn().getX());
        yml.set("location.spawn.y", this.getSpawn().getY());
        yml.set("location.spawn.z", this.getSpawn().getZ());
        yml.set("location.spawn.world", this.getSpawn().getWorld().getName());
        yml.set("location.spawn.yaw", this.getSpawn().getYaw());
        yml.set("location.spawn.pitch", this.getSpawn().getPitch());
        
        for(int i = 0; i < this.spawnLocations.size(); i++) {
            Location l = this.spawnLocations.get(i);
            if(l.equals(this.getSpawn())) {
                continue;
            }
            
            yml.set("spawns.spawn" + i + ".x", l.getX());
            yml.set("spawns.spawn" + i + ".y", l.getY());
            yml.set("spawns.spawn" + i + ".z", l.getZ());
            yml.set("spawns.spawn" + i + ".world", l.getWorld().getName());
            yml.set("spawns.spawn" + i + ".yaw", l.getYaw());
            yml.set("spawns.spawn" + i + ".pitch", l.getPitch());
        }
        
        return yml;
    }

    public void addSpawnLocation(Location loc) {
        this.getSpawnLocations().add(loc);
    }
}
