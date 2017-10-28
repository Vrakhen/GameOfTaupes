package fr.vraken.gameoftaupes;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;	
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.enchantments.Enchantment;

public class GameOfTaupes extends JavaPlugin
{
  FilesManager filesManager;
  BossManager bossManager;
  FileConfiguration teamf;
  FileConfiguration bossf;
  FileConfiguration deathf;
  
  ArrayList<UUID> playersInTeam = new ArrayList<UUID>();
  HashMap<UUID, Integer> taupesId = new HashMap<UUID, Integer>();
  HashMap<UUID, Integer> supertaupesId = new HashMap<UUID, Integer>();
  int taupeId;
  int supertaupeId;
  ArrayList<UUID> taupes = new ArrayList<UUID>();
  ArrayList<UUID> supertaupes = new ArrayList<UUID>();
  ArrayList<UUID> aliveTaupes = new ArrayList<UUID>();
  ArrayList<UUID> aliveSupertaupes = new ArrayList<UUID>();
  ArrayList<UUID> showedtaupes = new ArrayList<UUID>();
  ArrayList<UUID> showedsupertaupes = new ArrayList<UUID>();
  ArrayList<UUID> claimedtaupes = new ArrayList<UUID>();
  
  boolean taupessetup;
  boolean supertaupessetup;
  boolean isTaupesTeamDead;
  boolean isSuperTaupeDead;
  ArrayList<Integer> kits = new ArrayList<Integer>();
  ArrayList<Integer> claimedkits = new ArrayList<Integer>();
  
  boolean gameStarted = false;
  ScoreboardManager sm;
  Scoreboard s;
  Objective obj;
  int episode;
  int equipes;
  ArrayList<UUID> playersAlive = new ArrayList<UUID>();
  BukkitTask runnable;
  Location l1;
  Location l2;
  Location l3;
  Location l4;
  Location l5;
  Team team;
  Team rose;
  Team jaune;
  Team violette;
  Team cyan;
  Team verte;
  Team taupesteam;
  Team supertaupesteam;
  Objective vie;
  Location chestLocation;
  Location redstoneLocation;
  int chestLvl;
  int chestMinute;
  int gameState;
  int revealEpisode;
  int superrevealEpisode;
  int restractEpisode;
  String objMinute;
  String objSecond;
  String objTxt;
  String countdownObj;
  boolean hasChangedGS;
  int tmpPlayers;
  int tmpTeams;
  boolean isNetherPortalCreated;
  int tmpBorder;
  NumberFormat objFormatter;
  
  String teamAnnounceString = "L'équipe ";
  String teamChoiceString = "son équipe";

  ArrayList<Integer> bossLoc = new ArrayList<Integer>();
  
  UUID provoker;
  UUID provoked;
  Location duelSpawn1;
  Location duelSpawn2;
  boolean duelInProgress = false;
  
  ArrayList<UUID> playerReveal = new ArrayList<UUID>();
  ArrayList<String> teamReveal = new ArrayList<String>();
  
  
  public void onEnable()
  {
    System.out.println("+-------------VrakenGameOfTaupes--------------+");
    System.out.println("|           Plugin cree par Vraken            |");
    System.out.println("+---------------------------------------------+");
    try 
    {
		filesManager = new FilesManager(this);
	}
    catch (IOException | InvalidConfigurationException e) {}
    
    teamf = filesManager.getTeamConfig();
    bossf = filesManager.getBossConfig();
    deathf = filesManager.getDeathConfig();
    
    this.sm = Bukkit.getScoreboardManager();
    this.s = this.sm.getMainScoreboard();
    if (this.s.getObjective("Game Of Taupes") != null) 
    {
      this.s.getObjective("Game Of Taupes").unregister();
    }

    this.revealEpisode = getConfig().getInt("options.forcereveal") / 20;
    this.superrevealEpisode = getConfig().getInt("options.superreveal") / 20;
    
    getConfig().options().copyDefaults(true);
    teamf.options().copyDefaults(true);
    bossf.options().copyDefaults(true);
    deathf.options().copyDefaults(true);
    saveConfig();
    
    Bukkit.createWorld(new WorldCreator(getConfig().getString("world")));
    
    bossManager = new BossManager(this);
    Bukkit.getPluginManager().registerEvents(new EventsClass(this), this);
    Bukkit.getPluginManager().registerEvents(new BossEvents(this, bossManager), this);
    
    this.obj = this.s.registerNewObjective("Game Of Taupes", "dummy");
    this.obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    for (Team team : this.s.getTeams()) 
    {
      team.unregister();
    }
    ShapedRecipe craft = new ShapedRecipe(new ItemStack(Material.SPECKLED_MELON));
    craft.shape(new String[] { "***", "*x*", "***" });
    craft.setIngredient('*', Material.GOLD_INGOT);
    craft.setIngredient('x', Material.MELON);
    Bukkit.addRecipe(craft);

    ShapedRecipe craft2 = new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE));
    craft2.shape(new String[] { "***", "*x*", "***" });
    craft2.setIngredient('*', Material.GOLD_INGOT);
    craft2.setIngredient('x', Material.SKULL_ITEM);
    Bukkit.addRecipe(craft2);

    ShapelessRecipe craft4 = new ShapelessRecipe(new ItemStack(Material.NETHER_STAR));
    craft4.addIngredient(Material.ROTTEN_FLESH);
    craft4.addIngredient(Material.BONE);
    craft4.addIngredient(Material.SPIDER_EYE);
    craft4.addIngredient(Material.SULPHUR);
    Bukkit.addRecipe(craft4);


    this.rose = this.s.registerNewTeam(teamf.getString("rose.name"));
    this.rose.setPrefix(ChatColor.LIGHT_PURPLE.toString());
    this.rose.setSuffix(ChatColor.WHITE.toString());
    this.cyan = this.s.registerNewTeam(teamf.getString("cyan.name"));
    this.cyan.setPrefix(ChatColor.DARK_AQUA.toString());
    this.cyan.setSuffix(ChatColor.WHITE.toString());
    this.jaune = this.s.registerNewTeam(teamf.getString("jaune.name"));
    this.jaune.setPrefix(ChatColor.YELLOW.toString());
    this.jaune.setSuffix(ChatColor.WHITE.toString());
    this.violette = this.s.registerNewTeam(teamf.getString("violette.name"));
    this.violette.setPrefix(ChatColor.DARK_PURPLE.toString());
    this.violette.setSuffix(ChatColor.WHITE.toString());
    this.verte = this.s.registerNewTeam(teamf.getString("verte.name"));
    this.verte.setPrefix(ChatColor.GREEN.toString());
    this.verte.setSuffix(ChatColor.WHITE.toString());
    
    this.taupesteam = this.s.registerNewTeam("Taupes");
    this.taupesteam.setPrefix(ChatColor.RED.toString());
    this.taupesteam.setSuffix(ChatColor.WHITE.toString());
    this.supertaupesteam = this.s.registerNewTeam("SuperTaupe");
    this.supertaupesteam.setPrefix(ChatColor.DARK_RED.toString());
    this.supertaupesteam.setSuffix(ChatColor.WHITE.toString());
    
    if (this.s.getObjective("Vie") == null)
    {
      this.vie = this.s.registerNewObjective("Vie", "health");
      this.vie.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }
    super.onEnable();
  }
  
  public void startgame()
  {
	this.gameStarted = true;
	this.gameState = 0;
	this.hasChangedGS = false;
	this.chestLvl = 1;
	this.taupessetup = false;
	this.supertaupessetup = false;
	this.isTaupesTeamDead = false;
	this.isSuperTaupeDead = false;
	this.isNetherPortalCreated = false;	  
    EventsClass.rushIsStart = true;
    
    setSpawnLocations();
    setDuelSpawnLocations();
    
    List<UUID> players = new ArrayList<UUID>();
    List<Team> teamstoshuffle = new ArrayList<Team>();

    for (Team teams : this.s.getTeams()) 
    {
      if (!teams.getName().equals("Taupes") && !teams.getName().equals("SuperTaupe")) 
      {
        teamstoshuffle.add(teams);
      }
    }
    
	String world = getConfig().getString("world");
	Boolean istimecycle = getConfig().getBoolean("options.timecycle");
    Bukkit.getWorld(world).setGameRuleValue("doDaylightCycle", Boolean.valueOf(istimecycle).toString());
	Bukkit.getWorld(getConfig().getString("world")).setStorm(false);
	Bukkit.getWorld(getConfig().getString("world")).setThundering(false);
	Bukkit.getWorld(getConfig().get("world").toString()).setTime(5000L);
	
	Collections.shuffle(teamstoshuffle);
    Collections.shuffle(players);
    
    players.clear();
    
    clearTeams();
    
	this.episode += 1;
	
	
	//SCOREBOARD INITIALIZATION
	//-------------------------
	this.objFormatter = new DecimalFormat("00");
	initScoreboard();    
	
    
    //CLEARING INVENTORY AND STATUS OF EVERY PLAYER THEN TELEPORTING HIM TO HIS SPAWN
    //-------------------------------------------------------------------------------
    clearPlayers();
    
    
    //SPAWNING CHEST
    //--------------
    this.chestLocation = new Location(Bukkit.getWorld(getConfig().get("world").toString()), getConfig().getInt("chest.X"), getConfig().getInt("chest.Y"), getConfig().getInt("chest.Z"));
    this.redstoneLocation = new Location(Bukkit.getWorld(getConfig().get("world").toString()), getConfig().getInt("chest.X"), getConfig().getInt("chest.Y") - 5, getConfig().getInt("chest.Z"));
    this.chestMinute = 0;
    
    
    //TAUPES SETTING
    //--------------
    setTaupes();
    
    
    //SUPERTAUPE SETTING
    //------------------
    setSuperTaupe();
    
    
    //INVENTORY CLEANING
    //------------------
    for (Player p : Bukkit.getOnlinePlayers())
    {
      this.playersAlive.add(p.getUniqueId());
      EventsClass.alive.add(p.getUniqueId());
      p.getInventory().clear();
    }
    
    
    //RUNNABLE TASKS DURING ALL GAME
    //------------------------------
    this.runnable = new BukkitRunnable()
    {
      int minutes = 20;
      int seconds = 0;      
      
      public void run()
      {
    	//TESTING IF BOSS HAS DESPAWNED
    	//-----------------------------
    	//TODO testIfBossDespawn();
    	  
    	
    	//SCOREBOARD RESET AT EVERY SECOND
    	//--------------------------------
        NumberFormat formatter = new DecimalFormat("00");
        String minute = formatter.format(this.minutes);
        String second = formatter.format(this.seconds);
        GameOfTaupes.this.s.resetScores(minute + ":" + second);   
        GameOfTaupes.this.s.resetScores(ChatColor.WHITE + "Episode " + GameOfTaupes.this.episode);
        GameOfTaupes.this.s.resetScores("" + ChatColor.WHITE + GameOfTaupes.this.tmpPlayers + ChatColor.GRAY + " joueurs");    
        GameOfTaupes.this.s.resetScores(ChatColor.WHITE + "Border : " + tmpBorder + " x " + tmpBorder);      
        GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);            
          
        
    	//TEAMS UNREGISTRATION IF NO PLAYER IN IT
        //---------------------------------------
        unregisterTeam();
    	
        
        //TAUPES TEAMS UNREGISTRATION 
        //---------------------------
        unregisterTaupeTeam();
        
        
    	//VICTORY IF LAST TEAM STANDING
    	//-----------------------------
        checkVictory();
        
        
        //UPDATE COMPASS TARGET
        //---------------------
        updateCompassTarget();
        
        if (this.seconds == 0)
        {
          //CHEST SPAWN
          //-----------
          if(GameOfTaupes.this.taupessetup && this.minutes == 15 - chestMinute)     
          {
            spawnChest();
          }
          
          
          //SPAWNING MINIBOSS
          //-----------------        
          if(this.minutes == 15 && GameOfTaupes.this.episode == 1)
          {
          	GameOfTaupes.this.bossManager.activateShrine(4);
          }
          if(this.minutes == 5 && GameOfTaupes.this.episode == 1)
          {
          	GameOfTaupes.this.bossManager.activateShrine(5);
          }
          if(this.minutes == 15 && GameOfTaupes.this.episode == 2)
          {
          	GameOfTaupes.this.bossManager.activateShrine(6);
          }
          if(this.minutes == 5 && GameOfTaupes.this.episode == 2)
          {
          	GameOfTaupes.this.bossManager.activateShrine(1);
          }
          if(this.minutes == 15 && GameOfTaupes.this.episode == 3)
          {
          	GameOfTaupes.this.bossManager.activateShrine(2);
          }
          if(this.minutes == 5 && GameOfTaupes.this.episode == 3)
          {
          	GameOfTaupes.this.bossManager.activateShrine(3);
          }
          
          
          //EPISODE CHANGE ANNOUNCEMENT AT BEGINNING
          //----------------------------------------
          if (this.minutes == 0)
          {
        	  if(GameOfTaupes.this.taupessetup)
        	  {
              	Random rand = new Random();
                chestMinute = rand.nextInt(11);
        	  }
            
        	GameOfTaupes.this.episode += 1;
            Bukkit.broadcastMessage(ChatColor.AQUA + 
              "------------- Episode " + GameOfTaupes.this.episode + 
              " -------------");
            
            this.seconds = 59;
            this.minutes = 19;
          }
          else
          {
            this.seconds = 59;
            this.minutes -= 1;
          }
        }
        else
        {
          this.seconds -= 1;
        }    
        
        
        //WRITING SCOREBOARD
    	//------------------
        writeScoreboard(this.minutes, this.seconds);
      }
    }.runTaskTimer(this, 0L, 20L);
    
    
    //REVEALING A PLAYER'S LOCATION
    //-----------------------------
    new BukkitRunnable()
    {
    	public void run()
    	{
    		RevealPlayerLocation(false);
    	}
    	
    }.runTaskTimer(this, 6000, 12000);
    
    getServer().getWorld(getConfig().getString("world"))
    	.getWorldBorder()
    	.setSize(getConfig().getDouble("worldborder.size"));
    
    
    //PVP ENABLE
    //----------
    new BukkitRunnable()
    {
      public void run()
      {
        EventsClass.pvp = true;
        Bukkit.broadcastMessage(ChatColor.RED + 
          "Le pvp est maintenant actif !");

        
        //Updating scoreboard status
        GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
        GameOfTaupes.this.gameState++;
        GameOfTaupes.this.objMinute = objFormatter.format(GameOfTaupes.this.getConfig().getInt("options.settaupesafter") - GameOfTaupes.this.getConfig().getInt("options.pvptime") - 1);
        GameOfTaupes.this.objSecond = "59";
        GameOfTaupes.this.objTxt = "Taupes : ";
        GameOfTaupes.this.hasChangedGS = true;        
        GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":" + GameOfTaupes.this.objSecond;
      }
    }.runTaskLater(this, 1200 * getConfig().getInt("options.pvptime"));
    
    
    //TAUPES ANNOUNCEMENT
    //-------------------
    new BukkitRunnable()
    {
      public void run()
      {
    	taupeAnnouncement();        

        //Updating scoreboard status
    	GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
    	GameOfTaupes.this.gameState++;
    	GameOfTaupes.this.objMinute = objFormatter.format(GameOfTaupes.this.getConfig().getInt("options.setsupertaupesafter") - GameOfTaupes.this.getConfig().getInt("options.settaupesafter") - 1);
    	GameOfTaupes.this.objSecond = "59";
    	GameOfTaupes.this.objTxt = "Supertaupe : ";
    	GameOfTaupes.this.hasChangedGS = true;
    	GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":" + GameOfTaupes.this.objSecond;
      }
    }.runTaskLater(this, 1200 * getConfig().getInt("options.settaupesafter"));
    
    
    //SUPERTAUPE ANNOUNCEMENT
    //-------------------
    new BukkitRunnable()
    {
      public void run()
      {
    	supertaupeAnnouncement();

        //Updating scoreboard status
    	GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
    	GameOfTaupes.this.gameState++;
    	GameOfTaupes.this.objMinute = objFormatter.format(GameOfTaupes.this.getConfig().getInt("worldborder.retractafter") - GameOfTaupes.this.getConfig().getInt("options.setsupertaupesafter") - 1);
    	GameOfTaupes.this.objSecond = "59";
    	GameOfTaupes.this.objTxt = "World border : ";
    	GameOfTaupes.this.hasChangedGS = true;        
    	GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":" + GameOfTaupes.this.objSecond;
      }
    }.runTaskLater(this, 1200 * getConfig().getInt("options.setsupertaupesafter"));
    
    
    //TAUPES REVEAL
    //-------------
    new BukkitRunnable()
    {
      public void run()
      {
    	forceReveal();

        //Updating scoreboard status
    	GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
    	GameOfTaupes.this.gameState++;
    	if(!GameOfTaupes.this.getConfig().getBoolean("options.supertaupe"))
    	{
    		return;
    	}
    	GameOfTaupes.this.objMinute = objFormatter.format(GameOfTaupes.this.getConfig().getInt("options.superreveal") - GameOfTaupes.this.getConfig().getInt("options.forcereveal") - 1);
    	GameOfTaupes.this.objSecond = "59";
    	GameOfTaupes.this.objTxt = "Supertaupe reveal : ";
    	GameOfTaupes.this.hasChangedGS = true;        
    	GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":" + GameOfTaupes.this.objSecond;
      }
    }.runTaskLater(this, 1200 * getConfig().getInt("options.forcereveal"));
    
    
    //SUPERTAUPE REVEAL
    //------------
    new BukkitRunnable()
    {
      public void run()
      {
    	superReveal();

        //Updating scoreboard status
    	GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
    	GameOfTaupes.this.gameState++;
      }
    }.runTaskLater(this, 1200 * getConfig().getInt("options.superreveal"));
    
    
    //WORLDBORDER SHRINKING
    //---------------------
    new BukkitRunnable()
    {
      public void run()
      {
        //spawnPortal();
    	  
        //UPDATING SCOREBOARD STATUS
        GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
        GameOfTaupes.this.gameState++;
        GameOfTaupes.this.objMinute = objFormatter.format(GameOfTaupes.this.getConfig().getInt("options.forcereveal") - GameOfTaupes.this.getConfig().getInt("worldborder.retractafter") - 1);
        GameOfTaupes.this.objSecond = "59";
        GameOfTaupes.this.objTxt = "Taupes reveal : ";
        GameOfTaupes.this.hasChangedGS = true;        
        GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":" + GameOfTaupes.this.objSecond;      
      

        
        getServer().getWorld(getConfig().getString("world"))
        	.getWorldBorder()
        	.setSize(getConfig().getDouble("worldborder.finalsize"), 1200 * getConfig().getInt("worldborder.episodestorestract"));
      }
    }.runTaskLater(this, 1200 * getConfig().getInt("worldborder.retractafter"));
    
	/*
    for (Player online : Bukkit.getOnlinePlayers()) 
    {
      online.setScoreboard(this.s);
    }*/
  }
  
  
  //PLAYER INGAME COMMANDS
  //----------------------
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if ((sender instanceof Player))
    {
      Player player = (Player)sender;
      String message;
      
      //TAUPES CHAT
      //-----------
      if (cmd.getName().equalsIgnoreCase("t") && this.taupessetup)
      {
        if (this.aliveTaupes.contains(player.getUniqueId())) {
          for (UUID taupe : this.taupes)
          {
            message = StringUtils.join(args, ' ', 0, 
              args.length);
            
            Bukkit.getPlayer(taupe).sendMessage(
              ChatColor.GOLD + "(Taupes) " + ChatColor.RED + 
              "<" + player.getName() + "(" + 
              player.getScoreboard().getPlayerTeam(player).getName() + 
              ")> " + ChatColor.WHITE + message);
          }
        } 
        else 
        {
          player.sendMessage(ChatColor.RED + 
            "Vous n'êtes pas une taupe !");
        }
        return true;
      }
      
      //TAUPES REVEAL
      //-------------
      if (cmd.getName().equalsIgnoreCase("reveal") && this.taupessetup)
      {
        if (this.taupes.contains(player.getUniqueId()))
        {
          if (this.showedtaupes.contains(player.getUniqueId()))
          {
            player.sendMessage(ChatColor.RED + 
              "Vous vous êtes déjà révélé !");
          }
          else
          {
            PlayerInventory inventory = player.getInventory();
            inventory.addItem(new ItemStack[] { new ItemStack(Material.GOLDEN_APPLE, 1) });
            
            this.taupesteam.addPlayer(player);
            this.showedtaupes.add(player.getUniqueId());
            for (Player online : Bukkit.getOnlinePlayers())
            {
              online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
            }
            Bukkit.broadcastMessage(ChatColor.RED + player.getName() + " a révélé qu'il était une taupe !");
          }
        } 
        else 
        {
          player.sendMessage(ChatColor.RED + 
            "Vous n'êtes pas une taupe !");
        }
        return true;
      }      
      
      //SUPERTAUPE REVEAL
      //-----------------
      if (cmd.getName().equalsIgnoreCase("superreveal") && this.supertaupessetup)
      {
        if (this.supertaupes.contains(player.getUniqueId()))
        {
          if (this.showedsupertaupes.contains(player.getUniqueId()))
          {
            player.sendMessage(ChatColor.RED + 
                "Vous vous êtes déjà révélé !");
          }
          else if (!this.showedtaupes.contains(player.getUniqueId()))
          {
            player.sendMessage(ChatColor.RED + 
                "Vous devez d'abord vous révéler en tant que taupe !");
          }
          else
          {
            PlayerInventory inventory = player.getInventory();
            inventory.addItem(new ItemStack[] { new ItemStack(Material.GOLDEN_APPLE, 2) });
            
            this.aliveTaupes.remove(player.getUniqueId());
            this.supertaupesteam.addPlayer(player);
            this.showedsupertaupes.add(player.getUniqueId());
            for (Player online : Bukkit.getOnlinePlayers())
            {
              online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
              online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
            }
            Bukkit.broadcastMessage(ChatColor.DARK_RED + player.getName() + " a révélé qu'il était la supertaupe !");
          }
        }
        else 
        {
          player.sendMessage(ChatColor.RED + 
            "Vous n'êtes pas la supertaupe !");
        }
        return true;
      }
      
      //TAUPES CLAIM KIT
      //----------------
      if (cmd.getName().equalsIgnoreCase("claim") && this.taupessetup)
      {
        if (this.taupes.contains(player.getUniqueId()))
        {
          if(!this.claimedtaupes.contains(player.getUniqueId()))
          {
        	claimKit(player);            
          }
          else
          {
        	  player.sendMessage(ChatColor.RED + 
                  "Vous avez déjà claim votre kit de taupe !");
          }
        } 
        else 
        {
            player.sendMessage(ChatColor.RED + 
               "Vous n'êtes pas une taupe !");
        }
        return true;
      }
      
      //SPECTATE DUEL REQUEST
      //---------------------
      if(cmd.getName().equalsIgnoreCase("duel") && !this.duelInProgress && this.gameStarted)
      {
    	  if(!this.playersAlive.contains(player.getUniqueId()))
    	  {
    		  Player provoked = Bukkit.getPlayer(args[0]);
    		  if(!this.playersAlive.contains(provoked.getUniqueId()))
    		  {
    			  if(this.provoker != null)
    			  {
        			  Bukkit.getPlayer(this.provoked).sendMessage("Demande de duel expirée");
        			  Bukkit.getPlayer(this.provoker).sendMessage("Demande de duel expirée");
    			  }
    			  this.provoked = provoked.getUniqueId();
    			  this.provoker = player.getUniqueId();
    			  provoked.sendMessage(player.getName() + " vous a défié en duel ! /accept pour accepter ou /decline pour refuser !");
    		  }
    	  }
      }
      
      //SPECTATE DUEL ACCEPT
      //--------------------
      if(cmd.getName().equalsIgnoreCase("accept"))
      {
    	  if(!this.playersAlive.contains(player.getUniqueId()) && this.provoked == player.getUniqueId())
    	  {
    		  this.duelInProgress = true;
    		  Bukkit.getPlayer(this.provoked).teleport(this.duelSpawn1);
    		  Bukkit.getPlayer(this.provoker).teleport(this.duelSpawn2);
    		  Bukkit.getPlayer(this.provoked).setGameMode(GameMode.ADVENTURE);
    		  Bukkit.getPlayer(this.provoker).setGameMode(GameMode.ADVENTURE);
    	  }
      }
      
      //SPECTATE DUEL DECLINE
      //---------------------
      if(cmd.getName().equalsIgnoreCase("decline"))
      {
    	  if(!this.playersAlive.contains(player.getUniqueId()) && this.provoked == player.getUniqueId())
    	  {
			  Bukkit.getPlayer(provoker).sendMessage(player.getName() + " a refusé le duel. Le lâche !");
    		  this.provoked = null;
    		  this.provoker = null;
    	  }
      }
      
      //SPECTATE DUEL SPECTATE
      //----------------------
      if(cmd.getName().equalsIgnoreCase("duelspectate") && this.duelInProgress && !this.playersAlive.contains(player.getUniqueId()))
      {
		  player.teleport(this.duelSpawn1);
      }
      
      //ADMIN MANUAL START
      //------------------
      if (cmd.getName().equalsIgnoreCase("start") && player.isOp())
      {
        startgame();
        return true;
      }
      return true;
    }
    return false;
  }
  
  
  
  //UTILITY FUNCTIONS
  //-----------------
  
  public void setSpawnLocations()
  {
	    this.l1 = new Location(Bukkit.getWorld(getConfig().get("world").toString()), 
	    		this.teamf.getInt("rose.X"), 
	    		this.teamf.getInt("rose.Y"), 
	    		this.teamf.getInt("rose.Z"));
	    this.l2 = new Location(Bukkit.getWorld(getConfig().get("world").toString()), 
	    	    this.teamf.getInt("cyan.X"), 
	    	    this.teamf.getInt("cyan.Y"), 
	    	    this.teamf.getInt("cyan.Z"));
	    this.l3 = new Location(Bukkit.getWorld(getConfig().get("world").toString()), 
	    	    this.teamf.getInt("jaune.X"), 
	    	    this.teamf.getInt("jaune.Y"), 
	    	    this.teamf.getInt("jaune.Z"));
	    this.l4 = new Location(Bukkit.getWorld(getConfig().get("world").toString()), 
	    	    this.teamf.getInt("violette.X"), 
	    	    this.teamf.getInt("violette.Y"), 
	    	    this.teamf.getInt("violette.Z"));
	    this.l5 = new Location(Bukkit.getWorld(getConfig().get("world").toString()), 
	    	    this.teamf.getInt("verte.X"), 
	    	    this.teamf.getInt("verte.Y"), 
	    	    this.teamf.getInt("verte.Z"));
	    	    	  
  }
  
  public void setDuelSpawnLocations()
  {
	  this.duelSpawn1 = new Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()), 
	      this.getConfig().getInt("duelspawn1.X"), this.getConfig().getInt("duelspawn1.Y"), this.getConfig().getInt("duelspawn1.Z"));
	  this.duelSpawn2 = new Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()), 
	      this.getConfig().getInt("duelspawn2.X"), this.getConfig().getInt("duelspawn2.Y"), this.getConfig().getInt("duelspawn2.Z"));
	    
  }
  
  public void initScoreboard()
  {
	    this.s.getObjective(this.obj.getDisplayName())
	      .getScore(ChatColor.WHITE + "Episode " + this.episode)
	      .setScore(0);
	    this.s.getObjective(this.obj.getDisplayName())
	      .getScore("" + ChatColor.WHITE + EventsClass.alive.size() + ChatColor.GRAY + " joueurs")
	      .setScore(-1);
	    this.tmpBorder = (int)getServer().getWorld(getConfig().getString("world")).getWorldBorder().getSize();
	    this.s.getObjective(this.obj.getDisplayName())
	      .getScore(ChatColor.WHITE + "Border : " + tmpBorder + " x " + tmpBorder)
	      .setScore(-3);
	    NumberFormat objFormatter = new DecimalFormat("00");
	    this.objMinute = objFormatter.format(this.getConfig().getInt("options.pvptime"));
	    this.objSecond = "00";
	    this.objTxt = "PvP : ";
	    this.countdownObj = this.objTxt + this.objMinute + ":" + this.objSecond;
	    this.tmpPlayers = EventsClass.alive.size();
	    this.tmpTeams = this.s.getTeams().size();	  
  }
  
  public void clearPlayers()
  {
	  for (Player p : Bukkit.getOnlinePlayers())
	    {
		  if(!this.playersInTeam.contains(p.getUniqueId()))
		  {
			  p.kickPlayer("Vous n'avez pas choisi d'équipe, tant pis pour vous !");
			  continue;
		  }
		  
	      p.getInventory().clear();
	      p.getInventory().setHelmet(null);
	      p.getInventory().setChestplate(null);
	      p.getInventory().setLeggings(null);
	      p.getInventory().setBoots(null);
	      p.setExp(0.0f);
	      p.setLevel(0);
	      p.getActivePotionEffects().clear();
	      p.setGameMode(GameMode.SURVIVAL);
	      p.setHealth(20.0D);
	      p.setFoodLevel(40);

	      if (this.s.
	    		  getPlayerTeam(p).getName().equals(teamf.getString("rose.name"))) 
	      {
	        p.teleport(this.l1);
	      } 
	      else if (this.s.getPlayerTeam(p).getName().equals(teamf.getString("jaune.name"))) 
	      {
	        p.teleport(this.l2);
	      } 
	      else if (this.s.getPlayerTeam(p).getName().equals(teamf.getString("violette.name"))) 
	      {
	        p.teleport(this.l3);
	      } 
	      else if (this.s.getPlayerTeam(p).getName().equals(teamf.getString("cyan.name"))) 
	      {
	        p.teleport(this.l4);
	      } 
	      else if (this.s.getPlayerTeam(p).getName().equals(teamf.getString("verte.name"))) 
	      {
	        p.teleport(this.l5);
	      }
	      p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 
	    		  20 * getConfig().getInt("options.nodamagetime"), 4));
	    }
  }
  
  public void clearTeams()
  {
	    for (Team teams : this.s.getTeams()) 
	    {
	      if (!teams.getName().equalsIgnoreCase("Taupes")) 
	      {
	    	  if(teams.getName().equalsIgnoreCase("SuperTaupe") && !getConfig().getBoolean("options.supertaupe"))
	    	  {
	    	     teams.unregister();
	    	  }
	    	  else if(teams.getSize() == 0 && !teams.getName().equalsIgnoreCase("SuperTaupe"))
	    	  {
	    	     teams.unregister(); 
	    	  }
	      }
	    }	  
  }
  
  public void setTaupes()
  {
	    List<UUID> players2;
	    int psize;
        Random random = new Random(System.currentTimeMillis());
	    for (Team team : this.s.getTeams()) 
	    {
	      if (team.getPlayers().size() >= 1)
	      {
	        players2 = new ArrayList<UUID>();
	        for (OfflinePlayer player2 : team.getPlayers())
	        {
	          players2.add(player2.getUniqueId());
	        }
	        psize = random.nextInt(players2.size());
	        
	        UUID p = (UUID)players2.get(psize);
	        
	        this.taupes.add(p);
	        this.aliveTaupes.add(p);
	        this.taupeId += 1;
	        this.taupesId.put(p, Integer.valueOf(this.taupeId));
	      }
	    }
  }
  
  public void setSuperTaupe()
  {
    if(getConfig().getBoolean(("options.supertaupe")))
    {
	   Random random = new Random(System.currentTimeMillis());
	   int taupeIndex = random.nextInt(this.taupes.size());
	   UUID spId = this.taupes.get(taupeIndex);
	   this.supertaupes.add(spId);
	   this.aliveSupertaupes.add(spId);
	   this.supertaupeId += 1;
	   this.supertaupesId.put(spId, Integer.valueOf(this.supertaupeId));
	}
 }
  
  public void checkVictory()
  {
	  if(GameOfTaupes.this.s.getTeams().size() == 2
			  && (GameOfTaupes.this.s.getTeams().contains(GameOfTaupes.this.taupesteam)
					  || GameOfTaupes.this.s.getTeams().contains(GameOfTaupes.this.supertaupesteam)))
	  {
		  if (GameOfTaupes.this.aliveTaupes.size() == 0
				  && !GameOfTaupes.this.isTaupesTeamDead)
		  {
			  GameOfTaupes.this.isTaupesTeamDead = true;
			  Bukkit.broadcastMessage(ChatColor.RED + "L'équipe des taupes a été éliminée ! ");
			  GameOfTaupes.this.taupesteam.unregister();
		  }
		  if (GameOfTaupes.this.aliveSupertaupes.size() == 0
				  && !GameOfTaupes.this.isSuperTaupeDead
				  && GameOfTaupes.this.getConfig().getBoolean("options.supertaupe"))
		  {
			  GameOfTaupes.this.isSuperTaupeDead = true;
			  Bukkit.broadcastMessage(ChatColor.DARK_RED + "La supertaupe a été éliminée ! ");
			  GameOfTaupes.this.supertaupesteam.unregister();
		  }
	  }
	  else if(GameOfTaupes.this.s.getTeams().size() == 3
			  && GameOfTaupes.this.s.getTeams().contains(GameOfTaupes.this.taupesteam)
			  && GameOfTaupes.this.s.getTeams().contains(GameOfTaupes.this.supertaupesteam))
	  {
		  if (GameOfTaupes.this.aliveTaupes.size() == 0
				  && !GameOfTaupes.this.isTaupesTeamDead
				  && GameOfTaupes.this.aliveSupertaupes.size() == 0
				  && !GameOfTaupes.this.isSuperTaupeDead
				  && GameOfTaupes.this.getConfig().getBoolean("options.supertaupe"))
		  {
			  GameOfTaupes.this.isTaupesTeamDead = true;
			  Bukkit.broadcastMessage(ChatColor.RED + "L'équipe des taupes a été éliminée ! ");
			  GameOfTaupes.this.taupesteam.unregister();
			  GameOfTaupes.this.isSuperTaupeDead = true;
			  Bukkit.broadcastMessage(ChatColor.DARK_RED + "La supertaupe a été éliminée ! ");
			  GameOfTaupes.this.supertaupesteam.unregister();
		  }
	  }


	  if (GameOfTaupes.this.s.getTeams().size() == 1) 
	  {
		  for (Team lastteam : GameOfTaupes.this.s.getTeams())
		  {
			  Bukkit.broadcastMessage(GameOfTaupes.this.teamAnnounceString 
					  + lastteam.getPrefix() 
					  + lastteam.getName() 
					  + ChatColor.RESET 
					  + " a gagné ! ");

			  Bukkit.getScheduler().cancelAllTasks();
		  }
	  }
	  else if(GameOfTaupes.this.s.getTeams().size() == 0)
	  {
		  Bukkit.broadcastMessage("Toutes les équipes ont été éliminées, personne n'a gagné ! ");
		  Bukkit.getScheduler().cancelAllTasks();
	  }
  }
  
  public void unregisterTeam()
  {
      for(Team teams : GameOfTaupes.this.s.getTeams())
      {
      	//NORMAL TEAM UNREGISTRATION
      	if(teams.getSize() == 0 
      			&& !teams.getName().equalsIgnoreCase("Taupes") 
      			&& !teams.getName().equalsIgnoreCase("SuperTaupe"))
      	{
      		Bukkit.broadcastMessage(GameOfTaupes.this.teamAnnounceString 
          							+ teams.getPrefix() 
          							+ teams.getName() 
          							+ ChatColor.RESET 
          							+ " a été éliminée ! ");
      		teams.unregister();
      	}       	
      }	  
  }
  
  public void unregisterTaupeTeam()
  {
      if (GameOfTaupes.this.aliveTaupes.size() == 0
    		  && !GameOfTaupes.this.isTaupesTeamDead
    		  && GameOfTaupes.this.showedtaupes.size() == GameOfTaupes.this.taupeId)
      {
    	  GameOfTaupes.this.isTaupesTeamDead = true;
    	  Bukkit.broadcastMessage(ChatColor.RED + "L'équipe des taupes a été éliminée ! ");
    	  GameOfTaupes.this.taupesteam.unregister();
      }
      if (GameOfTaupes.this.aliveSupertaupes.size() == 0
    		  && !GameOfTaupes.this.isSuperTaupeDead
    		  && GameOfTaupes.this.showedsupertaupes.size() == GameOfTaupes.this.supertaupeId
    		  && GameOfTaupes.this.getConfig().getBoolean("options.supertaupe"))
      {
    	  GameOfTaupes.this.isSuperTaupeDead = true;
    	  Bukkit.broadcastMessage(ChatColor.DARK_RED + "La supertaupe a été éliminée ! ");
    	  GameOfTaupes.this.supertaupesteam.unregister();
      }
  }
  
  public void spawnChest()
  {
	Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "Un trésor est apparu ! Allez vite le chercher avant que vos adversaires ne s'en emparent ! ");  
	try 
	{
		Bukkit.getPlayer("Spec").performCommand("dmarker add chest icon:chest x:" + GameOfTaupes.this.chestLocation.getX() + " y:" + GameOfTaupes.this.chestLocation.getY() + " z:" + GameOfTaupes.this.chestLocation.getZ() + " world:" + GameOfTaupes.this.chestLocation.getWorld().getName());
	}
	catch(Exception ex) {}
  
    Block chestBlock = Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.chestLocation);
    if(chestBlock.getType() != Material.TRAPPED_CHEST)
    {
        chestBlock.setType(Material.TRAPPED_CHEST);
    }
    Block redstoneBlock = Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation);
    if(redstoneBlock.getType() != Material.REDSTONE_BLOCK)
    {
    	redstoneBlock.setType(Material.REDSTONE_BLOCK);
    }
	org.bukkit.block.Chest chest = (org.bukkit.block.Chest) chestBlock.getState();
	Inventory inv = chest.getInventory();
	inv.clear();
	kits.clear();
	
	Random rdm = new Random();
	int chestKit;
	ItemStack item = new ItemStack(Material.DIAMOND, 3);
	
	if(GameOfTaupes.this.chestLvl == 1)
	{
	  int chestPosition = 12;
	  for(int i = 0; i < 3; i++)
	  {
		while(true)
		{
		  chestKit = rdm.nextInt(7);
		  if(!GameOfTaupes.this.kits.contains(chestKit))
		  {
			  GameOfTaupes.this.kits.add(chestKit);
			break;
		  }
		}
		switch(chestKit)
		{
		case 0:
			break;
		case 1:
			item.setAmount(8);
			item.setType(Material.GOLD_INGOT);
			break;
		case 2:
			item.setAmount(1);
			item.setType(Material.APPLE);
			break;
		case 3:
			item.setAmount(1);
			item.setType(Material.BOOK);
			break;
		case 4:
			item.setAmount(24);
			item.setType(Material.IRON_INGOT);
			break;
		case 5:
			item.setAmount(1);
			item.setType(Material.BOW);
			break;
		case 6:
			item.setAmount(32);
			item.setType(Material.ARROW);
			break;
		}
		inv.setItem(chestPosition, item);
		chestPosition++;
	  }
	}	
	else if(GameOfTaupes.this.chestLvl == 2)
	{
	  int chestPosition = 12;
	  for(int i = 0; i < 2; i++)
	  {
		while(true)
		{
		  chestKit = rdm.nextInt(9);
		  if(!GameOfTaupes.this.kits.contains(chestKit))
		  {
			  GameOfTaupes.this.kits.add(chestKit);
			  break;
		  }
		}
		switch(chestKit)
		{
		case 0:
			item.setAmount(8);
			break;
		case 1:
			item.setAmount(24);
			item.setType(Material.GOLD_INGOT);
			break;
		case 2:
			item.setAmount(3);
			item.setType(Material.APPLE);
			break;
		case 3:
			item.setAmount(4);
			item.setType(Material.BOOK);
			break;
		case 4:
			item.setAmount(48);
			item.setType(Material.IRON_INGOT);
			break;
		case 5:
			item.setAmount(1);
			item.setType(Material.BOW);
			inv.setItem(chestPosition, item);
			chestPosition++;
			item.setAmount(32);
			item.setType(Material.ARROW);
			break;
		case 6:
			item.setAmount(1);
			item.setType(Material.ENCHANTMENT_TABLE);
			break;
		case 7:
			item.setAmount(1);
			item.setType(Material.BLAZE_ROD);
			break;
		case 8:
			item.setAmount(5);
			item.setType(Material.NETHER_WARTS);
			break;
		}
		inv.setItem(chestPosition, item);
		chestPosition++;
	  }
	}	
	else if(GameOfTaupes.this.chestLvl >= 3)
	{
		chestKit = rdm.nextInt(4);
		switch(chestKit)
		{
		case 0:
			item.setAmount(24);
			break;
		case 1:
			item.setAmount(5);
			item.setType(Material.GOLDEN_APPLE);
			break;
		case 2:
			item.setAmount(1);
			item.setType(Material.BOW);
			inv.setItem(12, item);
			inv.setItem(14, item);
			item.setAmount(64);
			item.setType(Material.ARROW);
			break;
		case 3:
			item.setAmount(5);
			item.setType(Material.NETHER_WARTS);
			inv.setItem(12, item);
			item.setAmount(2);
			item.setType(Material.BLAZE_ROD);
			break;
		}
		inv.setItem(13, item);
	}
	GameOfTaupes.this.chestLvl++;	
  }
  
  public void writeScoreboard(int minutes, int seconds)
  {      
      NumberFormat formatter2 = new DecimalFormat("00");
      String minute2 = ((NumberFormat)formatter2).format(minutes);
      String second2 = ((NumberFormat)formatter2).format(seconds);
      
      GameOfTaupes.this.s.getObjective(GameOfTaupes.this.obj.getDisplayName())
        .getScore(ChatColor.WHITE + "Episode " + GameOfTaupes.this.episode)
        .setScore(0);
      GameOfTaupes.this.s.getObjective(GameOfTaupes.this.obj.getDisplayName())
        .getScore("" + ChatColor.WHITE + EventsClass.alive.size() + ChatColor.GRAY + " joueurs")
        .setScore(-1);
      GameOfTaupes.this.tmpBorder = (int)getServer().getWorld(getConfig().getString("world")).getWorldBorder().getSize();
      GameOfTaupes.this.s.getObjective(GameOfTaupes.this.obj.getDisplayName())
        .getScore(ChatColor.WHITE + "Border : " + tmpBorder + " x " + tmpBorder)
        .setScore(-3);

      GameOfTaupes.this.tmpPlayers = EventsClass.alive.size();

      if(GameOfTaupes.this.gameState < 6)
      {
        if(!GameOfTaupes.this.hasChangedGS)
        {
          int min = Integer.parseInt(GameOfTaupes.this.objMinute);
          int sec = Integer.parseInt(GameOfTaupes.this.objSecond);
           
          if (sec == 0)
          {              
        	  GameOfTaupes.this.objSecond = "59";
        	  GameOfTaupes.this.objMinute = formatter2.format(min - 1);
          }
          else
          {
        	  GameOfTaupes.this.objSecond = formatter2.format(sec - 1);
          }
          
          GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":" + GameOfTaupes.this.objSecond;
        }
        else
        {
        	GameOfTaupes.this.hasChangedGS = false;
        }
        
        GameOfTaupes.this.s.getObjective(GameOfTaupes.this.obj.getDisplayName())
          .getScore(ChatColor.WHITE + GameOfTaupes.this.countdownObj)
          .setScore(-4);
      }       

      GameOfTaupes.this.s.getObjective(GameOfTaupes.this.obj.getDisplayName())
        .getScore(minute2 + ":" + second2)
        .setScore(-5);
  }
  
  public void taupeAnnouncement()
  {
      for (Team team : GameOfTaupes.this.s.getTeams()) 
      {
        for (OfflinePlayer p2 : team.getPlayers())
        {
          if (GameOfTaupes.this.taupes.contains(p2.getUniqueId())) 
          {
        	if(p2.isOnline()){
	            p2.getPlayer().sendMessage(ChatColor.RED + 
	              "-------Annonce IMPORTANTE------");
	            p2.getPlayer().sendMessage(ChatColor.GOLD + 
	              "Vous êtes la taupe de votre équipe !");
	            p2.getPlayer().sendMessage(ChatColor.GOLD + 
	              "Pour parler avec les autres taupes, exécutez la commande /t < message>");
	            p2.getPlayer().sendMessage(ChatColor.GOLD + 
	              "Si vous voulez dévoiler votre vraie identité, exécutez la commande /reveal");
	            p2.getPlayer().sendMessage(ChatColor.GOLD + 
	              "Pour obtenir votre kit de taupe, exécutez la commande /claim");
	            p2.getPlayer().sendMessage(ChatColor.GOLD + "Votre but : " + 
	              ChatColor.DARK_RED + 
	              "Tuer les membres de votre \"équipe\"");
	            p2.getPlayer().sendMessage(ChatColor.RED + 
	              "-------------------------------");
	            Title.sendTitle(p2.getPlayer(), "Vous êtes la taupe !", 
	              "Ne le dites à personne !");
	    		
        	}
          }
        }
      }
      GameOfTaupes.this.taupessetup = true;
  }
  
  public void supertaupeAnnouncement()
  {
      for (Team team : GameOfTaupes.this.s.getTeams()) 
      {
        for (OfflinePlayer p2 : team.getPlayers())
        {
          if (GameOfTaupes.this.supertaupes.contains(p2.getUniqueId())) 
          {
        	  if(p2.isOnline()){
	            p2.getPlayer().sendMessage(ChatColor.RED + 
	              "-------Annonce IMPORTANTE------");
	            p2.getPlayer().sendMessage(ChatColor.GOLD + 
	              "Vous êtes la supertaupe !");
	            p2.getPlayer().sendMessage(ChatColor.GOLD + 
	              "Si vous voulez dévoiler votre vraie identité exécutez la commande /superreveal");
	            p2.getPlayer().sendMessage(ChatColor.GOLD + "Votre but : " + 
	              ChatColor.DARK_RED + 
	              "Tuer tous les autres joueurs !");
	            p2.getPlayer().sendMessage(ChatColor.RED + 
	              "-------------------------------");
	            Title.sendTitle(p2.getPlayer(), "Vous êtes la supertaupe !", 
	              "Ne le dites à personne !");	  		  
        	  }
          }
        }
      }
      GameOfTaupes.this.supertaupessetup = true;
  }
  
  public void forceReveal()
  {
  	for(UUID taupe : GameOfTaupes.this.taupes) 
  	{
  	  if(!GameOfTaupes.this.showedtaupes.contains(taupe))
  	  {
  		GameOfTaupes.this.taupesteam.addPlayer(Bukkit.getPlayer(taupe));
  		GameOfTaupes.this.showedtaupes.add(taupe);
        Bukkit.broadcastMessage(ChatColor.RED 
                + Bukkit.getPlayer(taupe).getName() 
                + " a révélé qu'il était une taupe !");
        
    	for (Player online : Bukkit.getOnlinePlayers()) 
    	{
    		online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
    	}
  	  }
  	}

  	GameOfTaupes.this.taupessetup = true;
  }
  
  public void superReveal()
  {
	for(UUID supertaupe : GameOfTaupes.this.supertaupes) 
    {
	  if(!GameOfTaupes.this.showedsupertaupes.contains(supertaupe))
	  {
		GameOfTaupes.this.aliveTaupes.remove(supertaupe);
		GameOfTaupes.this.supertaupesteam.addPlayer(Bukkit.getPlayer(supertaupe));
		GameOfTaupes.this.showedsupertaupes.add(supertaupe);
        Bukkit.broadcastMessage(ChatColor.DARK_RED 
                + Bukkit.getPlayer(supertaupe).getName() 
                + " a révélé qu'il était la supertaupe !");

    	for (Player online : Bukkit.getOnlinePlayers()) 
    	{
    		online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
    		online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
    	}
      }
    }

	GameOfTaupes.this.supertaupessetup = true;  
  }
  
  public void claimKit(Player player)
  {
	Random random = new Random();
    int kitnumber;
    while(true)
    {
      kitnumber = random.nextInt(8);
      if(!GameOfTaupes.this.claimedkits.contains(kitnumber))
      {
    	  GameOfTaupes.this.claimedkits.add(kitnumber);
    	  break;
      }
    }
    ItemStack kit = new ItemStack(Material.GOLDEN_APPLE, 4);
    Location loc = player.getLocation();
    loc.add(player.getEyeLocation().getDirection().normalize());
    switch(kitnumber)
    {
      case(0):
      	kit.setAmount(3);
    	kit.setType(Material.TNT);
       	player.getWorld().dropItemNaturally(loc, kit);
       	kit.setAmount(1);
    	kit.setType(Material.FLINT_AND_STEEL);
       	player.getWorld().dropItemNaturally(loc, kit);
       	break;
      case(1):
       	kit.setAmount(3);
    	kit.setType(Material.MONSTER_EGG);
    	kit.setDurability((short)61);
       	player.getWorld().dropItemNaturally(loc, kit);
       	break; 
      case(2):
       	kit.setAmount(32);
    	kit.setType(Material.ARROW);
       	player.getWorld().dropItemNaturally(loc, kit);
       	kit.setAmount(1);
    	kit.setType(Material.BOW);
    	kit.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
       	player.getWorld().dropItemNaturally(loc, kit);
       	break;
      case(3):
       	kit.setAmount(1);
       	kit.setType(Material.POTION);
       	Potion potion = new Potion(1);
    	potion.setType(PotionType.INVISIBILITY);
    	potion.setHasExtendedDuration(true);
    	potion.setSplash(true);
    	potion.apply(kit);
       	player.getWorld().dropItemNaturally(loc, kit);
    	potion.setType(PotionType.SPEED);
    	potion.setHasExtendedDuration(true);
    	potion.setSplash(true);
    	potion.apply(kit);
       	player.getWorld().dropItemNaturally(loc, kit);
    	potion.setType(PotionType.INSTANT_DAMAGE);
    	potion.setSplash(true);
    	potion.apply(kit);
       	player.getWorld().dropItemNaturally(loc, kit);
       	break;
      case(4):
       	kit.setAmount(1);
    	kit.setType(Material.DIAMOND_PICKAXE);
    	kit.addEnchantment(Enchantment.DIG_SPEED, 1);
    	kit.addEnchantment(Enchantment.DURABILITY, 1);
       	player.getWorld().dropItemNaturally(loc, kit);
       	break;
      case(5):
       	kit.setAmount(1);
    	kit.setType(Material.DIAMOND_CHESTPLATE);
    	kit.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
       	player.getWorld().dropItemNaturally(loc, kit);
       	break;
      case(6):
        kit.setAmount(8);
      	kit.setType(Material.ENDER_PEARL);
        player.getWorld().dropItemNaturally(loc, kit);
        kit.setAmount(1);
      	kit.setType(Material.DIAMOND_BOOTS);
      	kit.addEnchantment(Enchantment.PROTECTION_FALL, 4);
      	kit.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        player.getWorld().dropItemNaturally(loc, kit);
        break;
      default:
       	player.getWorld().dropItemNaturally(loc, kit);
       	break;
    }
    this.claimedtaupes.add(player.getUniqueId());
  }
  
  public void updateCompassTarget()
	{
		for(Team team : GameOfTaupes.this.s.getTeams())
		{
			for(OfflinePlayer player : team.getPlayers())
			{
				try
				{
					Player pl = (Player) player;
					
					if(!GameOfTaupes.this.playersAlive.contains(player.getUniqueId()))
					{
						return;
					}

		            double shortestDistance = 99999;
		            Location nearestBoss = GameOfTaupes.this.chestLocation;
		            
		            for(int i : GameOfTaupes.this.bossManager.aliveBoss.keySet())
		            {
		            	//Bukkit.broadcastMessage("" + GameOfTaupes.this.bossManager.getShrinesLocation().get(GameOfTaupes.this.bossManager.aliveBoss.get(i)).toString());
		                double distanceToPlayer = GameOfTaupes.this.bossManager.getShrinesLocation().get(GameOfTaupes.this.bossManager.aliveBoss.get(i)).distance(pl.getLocation());
		                if (distanceToPlayer < shortestDistance) 
		                {
		                	nearestBoss = GameOfTaupes.this.bossManager.getShrinesLocation().get(GameOfTaupes.this.bossManager.aliveBoss.get(i));
		                    shortestDistance = distanceToPlayer;
		                }
		            }
		           
		            pl.setCompassTarget(nearestBoss);
				} 
				catch(ClassCastException e) {}				
			}
		}
	}	

  public void RevealPlayerLocation(boolean reset)
  {	 
	boolean foundTeam = false;
	int n = 0;
	int tIdx;
	Player p = null;
	Team t = null;
	Random rdm = new Random();
		
	while(n < 20)
	{
		++n;
		tIdx = rdm.nextInt(GameOfTaupes.this.s.getTeams().size());
		t = (Team) GameOfTaupes.this.s.getTeams().toArray()[tIdx];
			
		if(t.getSize() == 0)
		{
			continue;
		}
			
		if(!GameOfTaupes.this.teamReveal.contains(t.getName()))
		{
			for(OfflinePlayer op : t.getPlayers())
			{
				if(op.isOnline())
				{
					GameOfTaupes.this.teamReveal.add(t.getName());
					p = (Player) op;
	       			foundTeam = true;
	       			break;
				}
			}
		}
		
		if(foundTeam)
		{
			break;
		}
	}
		
	if(foundTeam)
	{
		Bukkit.broadcastMessage(ChatColor.GOLD + "Le Grand Oeil a détecté une armée en " 
	  		+ (int)p.getLocation().getX() 
	  		+ " / "
	  		+ (int)p.getLocation().getZ()
	  		+ " ! ");
	}
	else
	{
		if(reset)
		{
			return;
		}
		GameOfTaupes.this.teamReveal.clear();
		RevealPlayerLocation(true);
	}
  }
  
  
  //TODO
  public void testIfBossDespawn()
  {
  }
}
