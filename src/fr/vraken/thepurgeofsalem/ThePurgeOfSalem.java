package fr.vraken.thepurgeofsalem;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import fr.vraken.thepurgeofsalem.EventsClass;
import fr.vraken.thepurgeofsalem.FilesManager;
import fr.vraken.thepurgeofsalem.Title;

import org.bukkit.enchantments.Enchantment;

public class ThePurgeOfSalem extends JavaPlugin 
{
	LootManager lootManager;
	
	// Files
	FilesManager filesManager;
	FileConfiguration teamf;
	FileConfiguration deathf;
	FileConfiguration lootf;

	// Players
	ArrayList<UUID> playersInTeam = new ArrayList<UUID>();
	ArrayList<UUID> playersAlive = new ArrayList<UUID>();
	ArrayList<UUID> playersInLobby = new ArrayList<UUID>();
	ArrayList<UUID> playersSpec = new ArrayList<UUID>();

	// Taupes
	Team taupesTeam;
	boolean taupessetup;
	HashMap<UUID, Integer> taupes = new HashMap<UUID, Integer>();
	Boolean isTaupesTeamDead;
	ArrayList<UUID> aliveTaupes = new ArrayList<UUID>();
	ArrayList<UUID> showedtaupes = new ArrayList<UUID>();
	ArrayList<UUID> claimedtaupes = new ArrayList<UUID>();
	Team assassinTeam;
	boolean assassinPotionUsed;
	boolean assassinTeamUnregistered;
	int evilPower;

	// Hunters
	Team huntersTeam;
	boolean hunterssetup;
	HashMap<UUID, Integer> hunters = new HashMap<UUID, Integer>();
	Boolean isHuntersTeamDead;
	ArrayList<UUID> aliveHunters = new ArrayList<UUID>();
	ArrayList<UUID> showedHunters = new ArrayList<UUID>();
	ArrayList<UUID> claimedHunters = new ArrayList<UUID>();
	Team inquisitorInitialTeam;
	Team seerInitialTeam;
	Team cursedTeam;
	int redemption;
	boolean salvation;
	
	// Supertaupe & Superhunter
	Team supertaupeTeam;
	boolean supertaupesetup;
	boolean superhuntersetup;
	UUID supertaupe;
	UUID superhunter;
	boolean isSupertaupeDead;
	boolean supertaupeConsumed;

	// Scoreboard
	int minute;
	ScoreboardManager sm;
	Scoreboard s;
	Objective obj;
	BukkitTask runnable;
	Objective vie;
	int gameState;
	String objMinute;
	String objSecond;
	String objTxt;
	String countdownObj;
	boolean hasChangedGS;
	int tmpPlayers;
	NumberFormat objFormatter;

	// Gamestates
	boolean meetUp = false;
	boolean gameStarted = false;
	boolean gameEnd = false;
	Location lobbyLocation;
	Location meetupLocation;
	Location respawnLocation;

	// Teams
	ArrayList<Location> locations = new ArrayList<Location>();
	ArrayList<Location> spawnLocations = new ArrayList<Location>();
	Team rose;			// Miliciens
	Team jaune;			// Marchands
	Team violette;		// Taverniers
	Team cyan;			// Forgerons
	Team verte;			// Chasseurs
	Team grise;			// Bourgeois
	HashMap<UUID, Integer> grayTeamCooldown = new HashMap<UUID, Integer>();
	
	// Meetup
	boolean graalSpawned = false;
	Location graalLocation;
	
	// Special loots temple
	ArrayList<UUID> forbiddenPlayers = new ArrayList<UUID>();
	

	public void onEnable() 
	{
		System.out.println("+-------------VrakenThePurgeOfSalem--------------+");
		System.out.println("|             Plugin cree par Vraken             |");
		System.out.println("+------------------------------------------------+");
		
		try 
		{
			filesManager = new FilesManager(this);
		} 
		catch (IOException | InvalidConfigurationException e) {}

		teamf = filesManager.getTeamConfig();
		deathf = filesManager.getDeathConfig();
		lootf = filesManager.getLootConfig();
		
		lootManager = new LootManager(this, filesManager);

		this.sm = Bukkit.getScoreboardManager();
		this.s = this.sm.getMainScoreboard();
		if (this.s.getObjective("ThePurgeOfSalem") != null) 
		{
			this.s.getObjective("ThePurgeOfSalem").unregister();
		}

		getConfig().options().copyDefaults(true);
		teamf.options().copyDefaults(true);
		deathf.options().copyDefaults(true);
		lootf.options().copyDefaults(true);
		saveConfig();

		Bukkit.createWorld(new WorldCreator(getConfig().getString("lobby.world")));
		Bukkit.createWorld(new WorldCreator(getConfig().getString("world")));

		Bukkit.getPluginManager().registerEvents(new EventsClass(this), this);

		this.obj = this.s.registerNewObjective("ThePurgeOfSalem", "dummy");
		this.obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		for (Team team : this.s.getTeams()) 
		{
			team.unregister();
		}

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
		this.grise = this.s.registerNewTeam(teamf.getString("grise.name"));
		this.grise.setPrefix(ChatColor.GRAY.toString());
		this.grise.setSuffix(ChatColor.WHITE.toString());

		this.taupesTeam = this.s.registerNewTeam("Heretiques");
		this.taupesTeam.setPrefix(ChatColor.RED.toString());
		this.taupesTeam.setSuffix(ChatColor.WHITE.toString());
		
		this.assassinTeam = this.s.registerNewTeam("Spectre");
		this.assassinTeam.setPrefix(ChatColor.RED.toString());
		this.assassinTeam.setSuffix(ChatColor.WHITE.toString());
		this.assassinTeam.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);		

		this.supertaupeTeam = this.s.registerNewTeam("Suppot de Satan");
		this.supertaupeTeam.setPrefix(ChatColor.DARK_RED.toString());
		this.supertaupeTeam.setSuffix(ChatColor.WHITE.toString());

		this.huntersTeam = this.s.registerNewTeam("Repurgateurs");
		this.huntersTeam.setPrefix(ChatColor.WHITE.toString());
		this.huntersTeam.setSuffix(ChatColor.WHITE.toString());
		
		this.isTaupesTeamDead = false;
		this.isSupertaupeDead = false;
		this.isHuntersTeamDead = false;
		this.assassinPotionUsed = false;
		this.assassinTeamUnregistered = false;

		if (this.s.getObjective("Vie") == null) 
		{
			this.vie = this.s.registerNewObjective("Vie", "health");
			this.vie.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		}

		this.lobbyLocation = new Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()),
				this.getConfig().getInt("lobby.X"), this.getConfig().getInt("lobby.Y"),
				this.getConfig().getInt("lobby.Z"));

		this.respawnLocation = new Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()),
				this.getConfig().getInt("lobby.respawnX"), this.getConfig().getInt("lobby.respawnY"),
				this.getConfig().getInt("lobby.respawnZ"));

		this.meetupLocation = new Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()),
				this.getConfig().getInt("lobby.meetupX"), this.getConfig().getInt("lobby.meetupY"),
				this.getConfig().getInt("lobby.meetupZ"));

		setSpawnLocations();

		super.onEnable();
	}

	public void startGame()
	{
		this.gameStarted = true;
		this.gameState = 0;
		this.hasChangedGS = false;
		this.taupessetup = false;
		this.supertaupesetup = false;

		String world = getConfig().getString("world");
		Boolean istimecycle = getConfig().getBoolean("options.timecycle");
		Bukkit.getWorld(world).setGameRuleValue("doDaylightCycle", Boolean.valueOf(istimecycle).toString());
		Bukkit.getWorld(getConfig().getString("world")).setStorm(false);
		Bukkit.getWorld(getConfig().getString("world")).setThundering(false);
		Bukkit.getWorld(getConfig().get("world").toString()).setTime(5000L);

		clearTeams();		

		
		for(OfflinePlayer player : grise.getPlayers())
		{
			grayTeamCooldown.put(player.getUniqueId(), 0);	
		}
		

		// SCOREBOARD INITIALIZATION
		// -------------------------
		this.objFormatter = new DecimalFormat("00");
		initScoreboard(this.gameState);

		// TAUPES SETTING
		// --------------
		setTaupes();

		// HUNTERS SETTING
		// --------------
		setHunters();

		// CLEARING INVENTORY AND STATUS OF EVERY PLAYER THEN TELEPORTING HIM TO HIS
		// SPAWN
		// -------------------------------------------------------------------------
		clearPlayers();

		// RUNNABLE TASKS DURING ALL GAME
		// ------------------------------
		this.runnable = new BukkitRunnable() 
		{
			int minutes = 0;
			int seconds = 0;

			public void run() 
			{
				ThePurgeOfSalem.this.minute = minutes;

				// SCOREBOARD RESET AT EVERY SECOND
				// --------------------------------
				NumberFormat formatter = new DecimalFormat("00");
				String minute = formatter.format(this.minutes);
				String second = formatter.format(this.seconds);
				ThePurgeOfSalem.this.s.resetScores(minute + ":" + second);
				ThePurgeOfSalem.this.s.resetScores("" + ChatColor.WHITE + ThePurgeOfSalem.this.tmpPlayers + ChatColor.GRAY + " joueurs");
				ThePurgeOfSalem.this.s.resetScores(ChatColor.WHITE + ThePurgeOfSalem.this.countdownObj);

				if (this.seconds == 59) 
				{
					this.seconds = 0;
					++this.minutes;
				}
				else
				{
					++this.seconds;
				}

				// WRITING SCOREBOARD
				// ------------------
				writeScoreboard(this.minutes, this.seconds);
				
				
				for(UUID uid : grayTeamCooldown.keySet())
				{
					if(grayTeamCooldown.get(uid) > 0)
						grayTeamCooldown.put(uid, grayTeamCooldown.get(uid) - 1);
				}
				
			}
		}.runTaskTimer(this, 0L, 20L);

		getServer().getWorld(getConfig().getString("world")).getWorldBorder()
				.setSize(getConfig().getDouble("worldborder.size"));


		// PVP ENABLE
		// ----------
		new BukkitRunnable() 
		{
			public void run() 
			{
				EventsClass.pvp = true;
				Bukkit.broadcastMessage(ChatColor.RED + "Le pvp est maintenant actif !");

				// Updating scoreboard status
				ThePurgeOfSalem.this.s.resetScores(ChatColor.WHITE + ThePurgeOfSalem.this.countdownObj);
				ThePurgeOfSalem.this.gameState++;
				ThePurgeOfSalem.this.objMinute = objFormatter
						.format(ThePurgeOfSalem.this.getConfig().getInt("options.settaupesafter")
								- ThePurgeOfSalem.this.getConfig().getInt("options.pvptime") - 1);
				ThePurgeOfSalem.this.objSecond = "59";
				ThePurgeOfSalem.this.objTxt = "Roles : ";
				ThePurgeOfSalem.this.hasChangedGS = true;
				ThePurgeOfSalem.this.countdownObj = ThePurgeOfSalem.this.objTxt + ThePurgeOfSalem.this.objMinute + ":"
						+ ThePurgeOfSalem.this.objSecond;
			}
		}.runTaskLater(this, 1200 * getConfig().getInt("options.pvptime"));

		// TAUPES ANNOUNCEMENT
		// -------------------
		new BukkitRunnable() 
		{
			public void run() 
			{
				taupeAnnouncement();
				hunterAnnouncement();

				// Updating scoreboard status
				ThePurgeOfSalem.this.s.resetScores(ChatColor.WHITE + ThePurgeOfSalem.this.countdownObj);
				ThePurgeOfSalem.this.gameState++;
				ThePurgeOfSalem.this.objMinute = objFormatter
						.format(ThePurgeOfSalem.this.getConfig().getInt("options.forcereveal")
								- ThePurgeOfSalem.this.getConfig().getInt("options.settaupesafter") - 1);
				ThePurgeOfSalem.this.objSecond = "59";
				ThePurgeOfSalem.this.objTxt = "Revelation des roles : ";
				ThePurgeOfSalem.this.hasChangedGS = true;
				ThePurgeOfSalem.this.countdownObj = ThePurgeOfSalem.this.objTxt + ThePurgeOfSalem.this.objMinute + ":"
						+ ThePurgeOfSalem.this.objSecond;
			}
		}.runTaskLater(this, 1200 * getConfig().getInt("options.settaupesafter"));

		// TAUPES REVEAL
		// -------------
		new BukkitRunnable() 
		{
			public void run() 
			{
				forceReveal(true);

				// Updating scoreboard status
				ThePurgeOfSalem.this.s.resetScores(ChatColor.WHITE + ThePurgeOfSalem.this.countdownObj);
				ThePurgeOfSalem.this.gameState++;
				
				ThePurgeOfSalem.this.objMinute = objFormatter
						.format(ThePurgeOfSalem.this.getConfig().getInt("options.superreveal")
								- ThePurgeOfSalem.this.getConfig().getInt("options.forcereveal") - 1);
				ThePurgeOfSalem.this.objSecond = "59";
				ThePurgeOfSalem.this.objTxt = "Suppot de Satan : ";
				ThePurgeOfSalem.this.hasChangedGS = true;
				ThePurgeOfSalem.this.countdownObj = ThePurgeOfSalem.this.objTxt + ThePurgeOfSalem.this.objMinute + ":"
						+ ThePurgeOfSalem.this.objSecond;
			}
		}.runTaskLater(this, 1200 * getConfig().getInt("options.forcereveal"));

		// SUPERTAUPE REVEAL
		// -----------------
		new BukkitRunnable() 
		{
			public void run() 
			{
				setSupertaupe();
				supertaupeAnnouncement();
				supertaupeReveal(true);

				// Updating scoreboard status
				ThePurgeOfSalem.this.s.resetScores(ChatColor.WHITE + ThePurgeOfSalem.this.countdownObj);
				ThePurgeOfSalem.this.gameState++;
				ThePurgeOfSalem.this.objMinute = objFormatter
						.format(ThePurgeOfSalem.this.getConfig().getInt("options.graal")
								- ThePurgeOfSalem.this.getConfig().getInt("options.superreveal") - 1);
				ThePurgeOfSalem.this.objSecond = "59";
				ThePurgeOfSalem.this.objTxt = "Apparition du Graal : ";
				ThePurgeOfSalem.this.hasChangedGS = true;
				ThePurgeOfSalem.this.countdownObj = ThePurgeOfSalem.this.objTxt + ThePurgeOfSalem.this.objMinute + ":"
						+ ThePurgeOfSalem.this.objSecond;

				new BukkitRunnable() 
				{
					public void run() 
					{
						setSuperhunter();
						superhunterAnnouncement();
						superhunterReveal();
					}
				}.runTaskLater(ThePurgeOfSalem.this, 20 * 10);

				
				new BukkitRunnable() 
				{
					public void run() 
					{
						Bukkit.getPlayer(ThePurgeOfSalem.this.supertaupe).setHealth(0);

						ThePurgeOfSalem.this.supertaupeConsumed = true;;
						supertaupeDeath();
						resetSuperhunter();
					}
				}.runTaskLater(ThePurgeOfSalem.this, 1200 * getConfig().getInt("options.supertaupelifetime"));
				
				
			}
		}.runTaskLater(this, 1200 * getConfig().getInt("options.superreveal"));
		
		// GRAAL SPAWNING
		// --------------
		new BukkitRunnable() 
		{
			public void run() 
			{
				spawnGraal();
				announceGraal();

				// Updating scoreboard status
				ThePurgeOfSalem.this.s.resetScores(ChatColor.WHITE + ThePurgeOfSalem.this.countdownObj);
				ThePurgeOfSalem.this.gameState++;
				ThePurgeOfSalem.this.hasChangedGS = true;
			}
		}.runTaskLater(this, 1200 * getConfig().getInt("options.graal"));
	}

	public void stopGame() 
	{
		for (Player p : Bukkit.getOnlinePlayers()) 
		{
			if (!ThePurgeOfSalem.this.playersInLobby.contains(p.getUniqueId())) 
			{
				ThePurgeOfSalem.this.playersInLobby.add(p.getUniqueId());

				p.setGameMode(GameMode.ADVENTURE);
				p.teleport(lobbyLocation);
			}

			p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		}

		ThePurgeOfSalem.this.gameEnd = true;
	}
	
	
	// PLAYER INGAME COMMANDS
	// ----------------------
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if ((sender instanceof Player)) 
		{
			Player player = (Player) sender;
			String message;

			// TAUPES AND HUNTERS CHAT
			// -----------------------
			if (cmd.getName().equalsIgnoreCase("t") && this.taupessetup) 
			{
				if (this.aliveTaupes.contains(player.getUniqueId())
						&& this.taupes.containsKey(player.getUniqueId())) 
				{
					for (Map.Entry<UUID, Integer> taupe : this.taupes.entrySet()) 
					{
						if (ThePurgeOfSalem.this.supertaupe == taupe.getKey()
								&& ThePurgeOfSalem.this.supertaupesetup) 
						{
							continue;
						}
						
						String role = "";
						switch(taupe.getValue())
						{
						case 0:
							role = "Sorciere";
							break;
						case 1:
							role = "Alchimiste";
							break;
						case 2:
							role = "Assassin";
							break;
						case 3:
							role = "Mercenaire";
							break;
						case 4:
							role = "Druide";
							break;
						}

						message = StringUtils.join(args, ' ', 0, args.length);

						String content = ChatColor.GOLD + "(" + role + ") " + ChatColor.RED + "<"
								+ player.getName();

						if (!ThePurgeOfSalem.this.s.getPlayerTeam(Bukkit.getOfflinePlayer(taupe.getKey())).getName()
								.contains("eretique")) 
						{
							content += "(" + player.getScoreboard().getPlayerTeam(player).getName() + ")";
						}

						content += "> " + ChatColor.WHITE + message;

						Bukkit.getPlayer(taupe.getKey()).sendMessage(content);
					}
					return true;
				}
				else if (this.aliveHunters.contains(player.getUniqueId())
						&& this.hunters.containsKey(player.getUniqueId())) 
				{
					if(this.hunters.get(player.getUniqueId()) == 0)
					{
						player.sendMessage(ChatColor.RED + "Vous etes l'inquisiteur, vous ne pouvez pas communiquer avec les autres repurgateurs ! ");
						return true;
					}
					
					for (Map.Entry<UUID, Integer> hunter : this.hunters.entrySet()) 
					{	
						if(hunter.getValue() == 0)
						{
							continue;
						}	
						
						String role = "";
						switch(hunter.getValue())
						{
						case 1:
							role = "Vengeur";
							break;
						case 2:
							role = "Guerisseur";
							break;
						case 3:
							role = "Protecteur";
							break;
						case 4:
							role = "Martyre";
							break;
						}

						message = StringUtils.join(args, ' ', 0, args.length);

						String content = ChatColor.GOLD + "(" + role + ") " + ChatColor.RED + "<"
								+ player.getName();

						if (!ThePurgeOfSalem.this.s.getPlayerTeam(Bukkit.getOfflinePlayer(hunter.getKey())).getName()
								.contains("epurgateur")) 
						{
							content += "(" + player.getScoreboard().getPlayerTeam(player).getName() + ")";
						}

						content += "> " + ChatColor.WHITE + message;

						Bukkit.getPlayer(hunter.getKey()).sendMessage(content);
					}
					return true;
				}
				player.sendMessage(ChatColor.RED + "Vous n'etes ni un heretique ni un repurgateur !");
				return true;
			}

			// TAUPES AND HUNTERS REVEAL
			// -------------------------
			if (cmd.getName().equalsIgnoreCase("reveal") && this.taupessetup) 
			{
				if (this.taupes.containsKey(player.getUniqueId())) 
				{
					int roleIdx = this.taupes.get(player.getUniqueId());
					
					if (this.showedtaupes.contains(player.getUniqueId())) 
					{
						player.sendMessage(ChatColor.RED + "Vous vous etes deja revele !");
					} 
					else 
					{
						PlayerInventory inventory = player.getInventory();
						inventory.addItem(new ItemStack[] { new ItemStack(Material.GOLDEN_APPLE, 1) });

						this.taupesTeam.addPlayer(player);
						this.showedtaupes.add(player.getUniqueId());
						for (Player online : Bukkit.getOnlinePlayers()) 
						{
							online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
						}
						
						String role = "";
						switch(roleIdx)
						{
						case 0:
							role = "la sorciere";
							break;
						case 1:
							role = "l'alchimiste";
							break;
						case 2:
							role = "l'assassin";
							break;
						case 3:
							role = "le mercenaire";
							break;
						case 4:
							role = "le druide";
							break;
						}
						
						Bukkit.broadcastMessage(
								ChatColor.RED + player.getName() + " a revele qu'il etait un heretique : " + role + " !");

						unregisterTeam();
						unregisterTaupeTeam();
						unregisterHunterTeam();
						unregisterSupertaupeTeam();
						checkVictory();
					}
					return true;
				}
				else if (this.hunters.containsKey(player.getUniqueId())) 
				{
					int roleIdx = this.hunters.get(player.getUniqueId());
					
					if (this.showedHunters.contains(player.getUniqueId())) 
					{
						player.sendMessage(ChatColor.RED + "Vous vous etes deja revele !");
					} 
					else 
					{
						PlayerInventory inventory = player.getInventory();
						inventory.addItem(new ItemStack[] { new ItemStack(Material.GOLDEN_APPLE, 1) });

						this.huntersTeam.addPlayer(player);
						this.showedHunters.add(player.getUniqueId());
						for (Player online : Bukkit.getOnlinePlayers()) 
						{
							online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
						}
						
						String role = "";
						switch(roleIdx)
						{
						case 0:
							role = "l'inquisiteur";
							break;
						case 1:
							role = "le vengeur";
							break;
						case 2:
							role = "le guerisseur";
							break;
						case 3:
							role = "le protecteur";
							break;
						case 4:
							role = "le martyre";
							break;
						}
						
						Bukkit.broadcastMessage(
								ChatColor.RED + player.getName() + " a revele qu'il etait un repurgateur : " + role + " !");

						unregisterTeam();
						unregisterTaupeTeam();
						unregisterHunterTeam();
						unregisterSupertaupeTeam();
						checkVictory();
					}
					return true;
				}

				player.sendMessage(ChatColor.RED + "Vous n'etes ni un heretique ni un repurgateur !");
				return true;
			}

			// TAUPES AND HUNTERS CLAIM KIT
			// ----------------------------
			if (cmd.getName().equalsIgnoreCase("claim") && this.taupessetup) 
			{
				if (this.taupes.containsKey(player.getUniqueId())) 
				{
					if (!this.claimedtaupes.contains(player.getUniqueId())) 
					{
						claimKit(player, this.taupes.get(player.getUniqueId()));
					} 
					else 
					{
						player.sendMessage(ChatColor.RED + "Vous avez deja claim votre kit !");
					}
					return true;
				}
				else if (this.hunters.containsKey(player.getUniqueId())) 
				{
					if (!this.claimedHunters.contains(player.getUniqueId())) 
					{
						claimPower(player, this.hunters.get(player.getUniqueId()));
					}
					else if(this.hunters.get(player.getUniqueId()) == 4)
					{
						player.sendMessage(ChatColor.RED + "Vous etes le martyre, vous ne pouvez pas utiliser votre pouvoir !");
					}
					else 
					{
						player.sendMessage(ChatColor.RED + "Vous avez deja utilise votre pouvoir !");
					}
					return true;
				}
				player.sendMessage(ChatColor.RED + "Vous n'etes ni un heretique ni un repurgateur !");
				return true;
			}

			// ADMIN MEETUP
			// ------------
			if (cmd.getName().equalsIgnoreCase("gotmeetup") && player.isOp() && !this.gameStarted)
			{
				this.meetUp = true;
				for (Player p : Bukkit.getOnlinePlayers())
				{
					p.teleport(this.meetupLocation);
				}
				return true;
			}

			// ADMIN START
			// -----------
			if (cmd.getName().equalsIgnoreCase("gotstart") && player.isOp() && !this.gameStarted)
			{
				startGame();
				return true;
			}

			// ADMIN STOP
			// ----------
			if (cmd.getName().equalsIgnoreCase("gotstop") && player.isOp() && this.gameStarted)
			{
				stopGame();
				return true;
			}

			// DEAD PLAYER RETURN TO LOBBY
			// ---------------------------
			if (cmd.getName().equalsIgnoreCase("gotlobby") && this.playersSpec.contains(player.getUniqueId())
					&& this.gameStarted && !this.gameEnd) {
				this.playersSpec.remove(player.getUniqueId());
				this.playersInLobby.add(player.getUniqueId());
				player.setGameMode(GameMode.ADVENTURE);
				player.teleport(new Location(Bukkit.getWorld(this.getConfig().getString("lobby.world")),
						this.getConfig().getInt("lobby.respawnX"), this.getConfig().getInt("lobby.respawnY"),
						this.getConfig().getInt("lobby.respawnZ")));
				return true;
			}

			// DEAD PLAYER SPEC
			// ----------------
			if (cmd.getName().equalsIgnoreCase("gotspec") && this.playersInLobby.contains(player.getUniqueId())
					&& this.gameStarted && !this.gameEnd) {
				this.playersSpec.add(player.getUniqueId());
				this.playersInLobby.remove(player.getUniqueId());
				player.setGameMode(GameMode.SPECTATOR);
				player.teleport(new Location(Bukkit.getWorld(this.getConfig().getString("world")), 0, 120, 0));
				return true;
			}
		}
		return false;
	}

	
	// UTILITY FUNCTIONS
	// -----------------
	

	public void setSpawnLocations()
	{
		for(int i = 0; i < 6; ++i)
		{
			Location l = new Location(Bukkit.getWorld(getConfig().get("world").toString()),
					this.teamf.getInt("s"+ i + ".X"),
					this.teamf.getInt("s"+ i + ".Y"),
					this.teamf.getInt("s"+ i + ".Z"));
			
			this.locations.add(l);
		}
		
		ArrayList<Integer> loc = new ArrayList<Integer>();
		
		Random rdm = new Random();
		int l;
		
		for(int i = 0; i < 6; ++i)
		{
			while(true)
			{
				l = rdm.nextInt(5);
				if(!loc.contains(l))
				{
					break;
				}
			}
			loc.add(l);
			this.spawnLocations.add(this.locations.get(l));
		}
	}

	
	public void initScoreboard(int gs)
	{
		this.s.getObjective(this.obj.getDisplayName())
				.getScore("" + ChatColor.WHITE + this.playersAlive.size() + ChatColor.GRAY + " joueurs").setScore(-1);

		NumberFormat objFormatter = new DecimalFormat("00");

		switch (gs)
		{
		case 0:
			this.objMinute = objFormatter.format(this.getConfig().getInt("options.pvptime") - this.minute);
			this.objSecond = "00";
			this.objTxt = "PvP : ";
			this.countdownObj = this.objTxt + this.objMinute + ":" + this.objSecond;
			break;
		case 1:
			this.objMinute = objFormatter.format(this.getConfig().getInt("options.settaupesafter") - this.minute);
			this.objSecond = "00";
			this.objTxt = "Roles : ";
			this.countdownObj = this.objTxt + this.objMinute + ":" + this.objSecond;
			break;
		case 2:
			this.objMinute = objFormatter.format(this.getConfig().getInt("options.forcereveal") - this.minute);
			this.objSecond = "00";
			this.objTxt = "Revelation : ";
			this.countdownObj = this.objTxt + this.objMinute + ":" + this.objSecond;
			break;
		case 3:
			this.objMinute = objFormatter.format(this.getConfig().getInt("options.superreveal") - this.minute);
			this.objSecond = "00";
			this.objTxt = "Possession par Satan : ";
			this.countdownObj = this.objTxt + this.objMinute + ":" + this.objSecond;
			break;
		case 4:
			this.objMinute = objFormatter.format(this.getConfig().getInt("options.graal") - this.minute);
			this.objSecond = "00";
			this.objTxt = "Apparition du Graal : ";
			this.countdownObj = this.objTxt + this.objMinute + ":" + this.objSecond;
			break;
		}
	}

	
	public void clearPlayers()
	{
		ThePurgeOfSalem.this.playersInLobby.clear();
		ThePurgeOfSalem.this.playersSpec.clear();

		for (Player p : Bukkit.getOnlinePlayers())
		{
			if (!this.playersInTeam.contains(p.getUniqueId()))
			{
				p.kickPlayer("Vous n'avez pas choisi d'equipe. Tant pis pour vous !");
				continue;
			}

			this.playersAlive.add(p.getUniqueId());

			p.getInventory().clear();
			p.getInventory().setHelmet(null);
			p.getInventory().setChestplate(null);
			p.getInventory().setLeggings(null);
			p.getInventory().setBoots(null);
			p.setExp(0.0f);
			p.setLevel(0);
			
			ItemStack stuff = new ItemStack(Material.STONE_PICKAXE, 1);			
			stuff.addEnchantment(Enchantment.DURABILITY, 3);
			p.getInventory().addItem(stuff);
			
			stuff.setType(Material.STONE_SPADE);	
			stuff.addEnchantment(Enchantment.DURABILITY, 3);
			p.getInventory().addItem(stuff);			

			for (PotionEffect potion : p.getActivePotionEffects())
			{
				p.removePotionEffect(potion.getType());
			}

			p.setGameMode(GameMode.SURVIVAL);
			p.setHealth(20.0D);
			p.setFoodLevel(40);

			if (this.s.getPlayerTeam(p).getName().equals(teamf.getString("rose.name")))
			{
				p.teleport(this.spawnLocations.get(0));
			} 
			else if (this.s.getPlayerTeam(p).getName().equals(teamf.getString("jaune.name")))
			{
				p.teleport(this.spawnLocations.get(1));
			}
			else if (this.s.getPlayerTeam(p).getName().equals(teamf.getString("violette.name")))
			{
				p.teleport(this.spawnLocations.get(2));
			}
			else if (this.s.getPlayerTeam(p).getName().equals(teamf.getString("cyan.name")))
			{
				p.teleport(this.spawnLocations.get(3));
			}
			else if (this.s.getPlayerTeam(p).getName().equals(teamf.getString("verte.name")))
			{
				p.teleport(this.spawnLocations.get(4));
			}
			else if (this.s.getPlayerTeam(p).getName().equals(teamf.getString("grise.name")))
			{
				p.teleport(this.spawnLocations.get(5));
			}
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,
					20 * getConfig().getInt("options.nodamagetime"), 4));
		}
	}

	
	public void clearTeams()
	{
		for (Team teams : this.s.getTeams())
		{
			if (teams.getName().contains("eretique")
					|| teams.getName().contains("uppot")
					|| teams.getName().contains("epurgateur")
					|| teams.getName().contains("pectre"))
			{
				continue;
			}

			if(teams.getSize() == 0)
			{
				teams.unregister();
			}
		}
	}

	
	public void setTaupes()
	{
		ArrayList<UUID> players = new ArrayList<UUID>();
		int psize;
		int rsize;
		UUID p;
		Random random = new Random(System.currentTimeMillis());
		
		for (Team team : this.s.getTeams())
		{
			if (team.getPlayers().size() >= 1)
			{
				players.clear();

				for (OfflinePlayer player : team.getPlayers())
				{
					players.add(player.getUniqueId());
				}
				
				psize = random.nextInt(players.size());
				p = players.get(psize);
				
				while (true)
				{
					rsize = random.nextInt(5);
					if (!this.taupes.containsValue(rsize))
					{
						break;
					}
				}
				this.taupes.put(p, rsize);
				this.aliveTaupes.add(p);
			}
		}
	}


	public void setHunters() 
	{
		ArrayList<UUID> players = new ArrayList<UUID>();
		int psize;
		int rsize;
		UUID p;
		Random random = new Random(System.currentTimeMillis());
		for (Team team : this.s.getTeams()) 
		{
			if (team.getPlayers().size() >= 1) 
			{
				players.clear();

				for (OfflinePlayer player : team.getPlayers()) 
				{
					players.add(player.getUniqueId());
				}
				
				while (true) 
				{
					psize = random.nextInt(players.size());
					p = players.get(psize);
					if (!this.taupes.containsKey(p)) 
					{
						break;
					}
				}
					
				while (true)
				{
					rsize = random.nextInt(5);
					if (!this.hunters.containsValue(rsize))
					{
						break;
					}
				}
				this.hunters.put(p, rsize);
				this.aliveHunters.add(p);
				
				if(rsize == 0)
				{
					ThePurgeOfSalem.this.inquisitorInitialTeam = team;
				}
			}
		}
	}

	
	public void setSupertaupe()
	{
		Random rdm = new Random();
		UUID chosen = null;
		int tidx, pidx;
		
		if(ThePurgeOfSalem.this.aliveTaupes.size() > 0)
		{
			pidx = rdm.nextInt(ThePurgeOfSalem.this.aliveTaupes.size()); 
			chosen = ThePurgeOfSalem.this.aliveTaupes.get(pidx);
		}
		else
		{
			ArrayList<Team> teams = new ArrayList<Team>();
			
			for(Team team : ThePurgeOfSalem.this.s.getTeams())
			{
				if(!team.getName().contains("eretique")
						&& !team.getName().contains("uppot")
						&& !team.getName().contains("epurgateur")
						&& !team.getName().contains("pectre"))
				{
					teams.add(team);
				}
			}
			
			tidx = rdm.nextInt(teams.size());
			pidx = rdm.nextInt(teams.get(tidx).getSize());
			
			int i = 0;
			for(OfflinePlayer p : teams.get(tidx).getPlayers())
			{
			    if (i == pidx)
			    {
			        chosen = p.getUniqueId();
			        break;
			    }
			    ++i;
			}
		}
		
		ThePurgeOfSalem.this.supertaupe = chosen;

		Bukkit.getPlayer(chosen).addPotionEffect(
			new PotionEffect(
					PotionEffectType.INCREASE_DAMAGE,
					1200 * ThePurgeOfSalem.this.getConfig().getInt("options.supertaupelifetime"), 
					0));
		Bukkit.getPlayer(chosen).addPotionEffect(
				new PotionEffect(
						PotionEffectType.SPEED,
						1200 * ThePurgeOfSalem.this.getConfig().getInt("options.supertaupelifetime"), 
						0));
	}
	
	
	public void setSuperhunter()
	{
		Random rdm = new Random();
		UUID chosen = null;
		int tidx, pidx;
		
		if(ThePurgeOfSalem.this.aliveHunters.size() > 0)
		{
			pidx = rdm.nextInt(ThePurgeOfSalem.this.aliveHunters.size()); 
			chosen = ThePurgeOfSalem.this.aliveHunters.get(pidx);
		}
		else
		{
			ArrayList<Team> teams = new ArrayList<Team>();
			
			for(Team team : ThePurgeOfSalem.this.s.getTeams())
			{
				if(!team.getName().contains("eretique")
						&& !team.getName().contains("uppot")
						&& !team.getName().contains("epurgateur")
						&& !team.getName().contains("pectre"))
				{
					teams.add(team);
				}
			}
			
			tidx = rdm.nextInt(teams.size());
			pidx = rdm.nextInt(teams.get(tidx).getSize());
			
			int i = 0;
			for(OfflinePlayer p : teams.get(tidx).getPlayers())
			{
			    if (i == pidx)
			    {
			        chosen = p.getUniqueId();
			        break;
			    }
			    ++i;
			}
		}
		
		ThePurgeOfSalem.this.superhunter = chosen;

		Bukkit.getPlayer(chosen).addPotionEffect(
			new PotionEffect(
					PotionEffectType.FIRE_RESISTANCE,
					1200 * ThePurgeOfSalem.this.getConfig().getInt("options.supertaupelifetime"), 
					0));
	}
	

	public void checkVictory() 
	{
		Team lastTeam = null;
		
		if(ThePurgeOfSalem.this.s.getTeams().size() > 1)
		{
			return;
		}
		
		if(ThePurgeOfSalem.this.s.getTeams().size() == 1)
		{
			for(Team team : ThePurgeOfSalem.this.s.getTeams())
			{
				lastTeam = team;
			}
		}

		forceReveal(false);
		supertaupeReveal(false);
		announceWinner(lastTeam);
	}


	public void announceWinner(Team team) 
	{
		if (team == null) 
		{
			Bukkit.broadcastMessage("Toutes les equipes ont ete eliminees, personne n'a gagne ! ");
			Bukkit.getScheduler().cancelAllTasks();
			return;
		}

		Bukkit.broadcastMessage("L'equipe " + team.getPrefix() + team.getName()
				+ ChatColor.RESET + " a gagne ! ");

		Bukkit.getScheduler().cancelAllTasks();

		ThePurgeOfSalem.this.playersAlive.clear();
		ThePurgeOfSalem.this.gameEnd = true;
	}


	public void unregisterTeam() 
	{
		for (Team team : ThePurgeOfSalem.this.s.getTeams()) 
		{
			if (team.getSize() == 0 
					&& !team.getName().contains("eretique")
					&& !team.getName().contains("uppot")
					&& !team.getName().contains("epurgateur")
					&& !team.getName().contains("pectre"))
			{
				Bukkit.broadcastMessage("La guilde des " + team.getPrefix() + team.getName()
						+ ChatColor.RESET + " a ete eliminee ! ");
				team.unregister();
			}
		}
	}


	public void unregisterTaupeTeam() 
	{
		if(ThePurgeOfSalem.this.aliveTaupes.size() == 0
				&& ThePurgeOfSalem.this.taupes.keySet().size() == ThePurgeOfSalem.this.showedtaupes.size())
		{
			ThePurgeOfSalem.this.isTaupesTeamDead = true;
			Bukkit.broadcastMessage(ChatColor.RED + "Les heretiques ont ete eradiques ! ");
			ThePurgeOfSalem.this.taupesTeam.unregister();
		}
		
		if(ThePurgeOfSalem.this.assassinPotionUsed
				&& !ThePurgeOfSalem.this.assassinTeamUnregistered)
		{
			ThePurgeOfSalem.this.assassinTeam.unregister();
			ThePurgeOfSalem.this.assassinTeamUnregistered = true;
		}
	}


	public void unregisterHunterTeam() 
	{
		if(ThePurgeOfSalem.this.aliveHunters.size() == 0
				&& ThePurgeOfSalem.this.hunters.keySet().size() == ThePurgeOfSalem.this.showedHunters.size())
		{
			ThePurgeOfSalem.this.isHuntersTeamDead = true;
			Bukkit.broadcastMessage(ChatColor.RED + "Les repurgateurs ont ete elimines ! ");
			ThePurgeOfSalem.this.huntersTeam.unregister();
		}
	}


	public void unregisterSupertaupeTeam()
	{
		if(!ThePurgeOfSalem.this.playersAlive.contains(ThePurgeOfSalem.this.supertaupe)
				&& ThePurgeOfSalem.this.supertaupesetup)
		{
			ThePurgeOfSalem.this.isSupertaupeDead = true;
			ThePurgeOfSalem.this.supertaupeTeam.unregister();
		}
	}


	public void writeScoreboard(int minutes, int seconds)
	{
		NumberFormat formatter2 = new DecimalFormat("00");
		String minute2 = ((NumberFormat) formatter2).format(minutes);
		String second2 = ((NumberFormat) formatter2).format(seconds);

		ThePurgeOfSalem.this.s.getObjective(ThePurgeOfSalem.this.obj.getDisplayName())
				.getScore("" + ChatColor.WHITE + this.playersAlive.size() + ChatColor.GRAY + " joueurs").setScore(-1);
		
		ThePurgeOfSalem.this.tmpPlayers = this.playersAlive.size();

		if (ThePurgeOfSalem.this.gameState < 5)
		{
			if (!ThePurgeOfSalem.this.hasChangedGS)
			{
				int min = Integer.parseInt(ThePurgeOfSalem.this.objMinute);
				int sec = Integer.parseInt(ThePurgeOfSalem.this.objSecond);

				if (sec == 0)
				{
					ThePurgeOfSalem.this.objSecond = "59";
					ThePurgeOfSalem.this.objMinute = formatter2.format(min - 1);
				}
				else
				{
					ThePurgeOfSalem.this.objSecond = formatter2.format(sec - 1);
				}

				ThePurgeOfSalem.this.countdownObj = ThePurgeOfSalem.this.objTxt + ThePurgeOfSalem.this.objMinute + ":"
						+ ThePurgeOfSalem.this.objSecond;
			}
			else
			{
				ThePurgeOfSalem.this.hasChangedGS = false;
			}

			ThePurgeOfSalem.this.s.getObjective(ThePurgeOfSalem.this.obj.getDisplayName())
					.getScore(ChatColor.WHITE + ThePurgeOfSalem.this.countdownObj).setScore(-4);
		}

		ThePurgeOfSalem.this.s.getObjective(ThePurgeOfSalem.this.obj.getDisplayName()).getScore(minute2 + ":" + second2)
				.setScore(-5);
	}


	public void taupeAnnouncement()
	{
		OfflinePlayer taupe;
		for (int i = 0; i < ThePurgeOfSalem.this.taupes.size(); i++)
		{
			for (UUID uid : ThePurgeOfSalem.this.taupes.keySet())
			{
				taupe = Bukkit.getOfflinePlayer(uid);
				if (taupe.isOnline())
				{
					Player player = Bukkit.getPlayer(uid);
					
					player.sendMessage(ChatColor.RED + "-------Annonce importante------");
					
					switch(ThePurgeOfSalem.this.taupes.get(uid))
					{
					case 0:
						Title.sendTitle(player, "HERETIQUE", "Sorciere");	
						player.sendMessage(ChatColor.GOLD + "Vous etes la sorciere !");
						break;
					case 1:
						Title.sendTitle(player, "HERETIQUE", "Alchimiste");
						player.sendMessage(ChatColor.GOLD + "Vous etes l'alchimiste !");
						break;
					case 2:
						Title.sendTitle(player, "HERETIQUE", "Assassin");
						player.sendMessage(ChatColor.GOLD + "Vous etes l'assassin !");
						break;
					case 3:
						Title.sendTitle(player, "HERETIQUE", "Mercenaire");
						player.sendMessage(ChatColor.GOLD + "Vous etes le mercenaire !");
						break;
					case 4:
						Title.sendTitle(player, "HERETIQUE", "Chaman");
						player.sendMessage(ChatColor.GOLD + "Vous etes le chaman !");
						break;
					case 5:
						Title.sendTitle(player, "HERETIQUE", "Voyante");
						player.sendMessage(ChatColor.GOLD + "Vous etes la voyante !");
						break;
					}					

					if(ThePurgeOfSalem.this.taupes.get(uid) != 5)
					{
						player.sendMessage(ChatColor.GOLD + "Pour parler avec les autres heretiques, executez la commande /t < message>");
					}

					player.sendMessage(ChatColor.GOLD + "Si vous voulez devoiler votre vraie identite, executez la commande /reveal");
					player.sendMessage(ChatColor.GOLD + "Pour obtenir votre kit d'heretique ou activer votre pouvoir, executez la commande /claim");
					player.sendMessage(ChatColor.RED + "-------------------------------");	
				}
			}
		}
		ThePurgeOfSalem.this.taupessetup = true;
	}


	public void hunterAnnouncement() 
	{
		OfflinePlayer hunter;
		
		for (int i = 0; i < ThePurgeOfSalem.this.hunters.size(); i++) 
		{
			for (UUID uid : ThePurgeOfSalem.this.hunters.keySet()) 
			{
				hunter = Bukkit.getOfflinePlayer(uid);
				if (hunter.isOnline())
				{
					Player player = Bukkit.getPlayer(uid);
					
					player.sendMessage(ChatColor.RED + "-------Annonce IMPORTANTE------");
					
					switch(ThePurgeOfSalem.this.hunters.get(uid))
					{
					case 0:
						Title.sendTitle(player, "REPURGATEUR", "Inquisiteur");
						player.sendMessage(ChatColor.GOLD + "Vous etes un repurgateur : l'inquisiteur !");
						break;
					case 1:
						Title.sendTitle(player, "REPURGATEUR", "Venguer");
						player.sendMessage(ChatColor.GOLD + "Vous etes un repurgateur : le vengeur !");
						break;
					case 2:
						Title.sendTitle(player, "REPURGATEUR", "Guerisseur");
						player.sendMessage(ChatColor.GOLD + "Vous etes un repurgateur : le guerisseur !");
						break;
					case 3:
						Title.sendTitle(player, "REPURGATEUR", "Protecteur");
						player.sendMessage(ChatColor.GOLD + "Vous etes un repurgateur : le protecteur !");
						break;
					case 4:
						Title.sendTitle(player, "REPURGATEUR", "Martyre");
						player.sendMessage(ChatColor.GOLD + "Vous etes un repurgateur : le martyre !");
						break;
					case 5:
						Title.sendTitle(player, "REPURGATEUR", "Salvateur");
						player.sendMessage(ChatColor.GOLD + "Vous etes un repurgateur : le salvateur !");
						break;
					}

					if(ThePurgeOfSalem.this.hunters.get(uid) != 0)
					{
						player.sendMessage(ChatColor.GOLD + "Pour parler avec les autres repurgateurs, executez la commande /t < message>");
					}
					
					player.sendMessage(ChatColor.GOLD + "Si vous voulez devoiler votre vraie identite, executez la commande /reveal");

					if(ThePurgeOfSalem.this.hunters.get(uid) != 4)
					{
						player.sendMessage(ChatColor.GOLD + "Pour utiliser votre pouvoir divin, executez la commande /claim");
					}
										
					player.sendMessage(ChatColor.RED + "-------------------------------");	
						
				}
			}
		}
		ThePurgeOfSalem.this.hunterssetup = true;
	}


	public void supertaupeAnnouncement()
	{
		OfflinePlayer player;
		
		player = Bukkit.getOfflinePlayer(ThePurgeOfSalem.this.supertaupe);
		if (player.isOnline())
		{
			player.getPlayer().sendMessage(ChatColor.RED + "-------Annonce IMPORTANTE------");
			player.getPlayer().sendMessage(ChatColor.GOLD + "Satan vous a choisi pour etre son serviteur !");
			player.getPlayer().sendMessage(ChatColor.GOLD
					+ "Vous etes plus fort, plus rapide et vous enflammez les ennemis que vous touchez ! ");
			player.getPlayer().sendMessage(
					ChatColor.GOLD + "Utilisez votre nouvelle puissance pour tuer tous ces miserables mortels ! ");
			player.getPlayer().sendMessage(
					ChatColor.GOLD + "Depechez-vous ! Dans 5 minutes vous mourrez consume par le demon ! ");
			player.getPlayer().sendMessage(ChatColor.RED + "-------------------------------");
			Title.sendTitle(player.getPlayer(), "Satan vous a choisi !", "Annihilez tous ces faibles mortels !");
		}
		ThePurgeOfSalem.this.supertaupesetup = true;
		ThePurgeOfSalem.this.supertaupeConsumed = false;
	}


	public void superhunterAnnouncement()
	{
		OfflinePlayer player;
		
		player = Bukkit.getOfflinePlayer(ThePurgeOfSalem.this.superhunter);
		if (player.isOnline())
		{
			player.getPlayer().sendMessage(ChatColor.RED + "-------Annonce IMPORTANTE------");
			player.getPlayer().sendMessage(ChatColor.GOLD + "Dieu vous a choisi pour etre son champion !");
			player.getPlayer().sendMessage(ChatColor.GOLD
					+ "Vous etes immunise contre les flammes des enfers ! ");
			player.getPlayer().sendMessage(
					ChatColor.GOLD + "Utilisez votre nouvelle puissance pour detruire le suppot de Satan ! ");
			player.getPlayer().sendMessage(ChatColor.RED + "-------------------------------");
			Title.sendTitle(player.getPlayer(), "Dieu vous a choisi !", "Traquez le suppot de Satan !");
		}
		ThePurgeOfSalem.this.superhuntersetup = true;
	}

	
	public void forceReveal(boolean check) 
	{
		for (Map.Entry<UUID, Integer> entry : ThePurgeOfSalem.this.taupes.entrySet()) 
		{
			if (!ThePurgeOfSalem.this.showedtaupes.contains(entry.getKey())) 
			{
				ThePurgeOfSalem.this.showedtaupes.add(entry.getKey());
				ThePurgeOfSalem.this.taupesTeam.addPlayer(Bukkit.getOfflinePlayer(entry.getKey()));
				
				switch(entry.getValue())
				{
				case 0:
					Bukkit.broadcastMessage(ChatColor.RED + Bukkit.getOfflinePlayer(entry.getKey()).getName()
							+ " a revele qu'il etait un heretique : la sorciere !");
					break;
				case 1:
					Bukkit.broadcastMessage(ChatColor.RED + Bukkit.getOfflinePlayer(entry.getKey()).getName()
							+ " a revele qu'il etait un heretique : l'alchimiste !");
					break;
				case 2:
					Bukkit.broadcastMessage(ChatColor.RED + Bukkit.getOfflinePlayer(entry.getKey()).getName()
							+ " a revele qu'il etait un heretique : l'assassin !");
					break;
				case 3:
					Bukkit.broadcastMessage(ChatColor.RED + Bukkit.getOfflinePlayer(entry.getKey()).getName()
							+ " a revele qu'il etait un heretique : le mercenaire !");
					break;
				case 4:
					Bukkit.broadcastMessage(ChatColor.RED + Bukkit.getOfflinePlayer(entry.getKey()).getName()
							+ " a revele qu'il etait un heretique : le chaman !");
					break;
				case 5:
					Bukkit.broadcastMessage(ChatColor.RED + Bukkit.getOfflinePlayer(entry.getKey()).getName()
							+ " a revele qu'il etait un heretique : la voyante !");
					break;
				}
			}
		}

		for (Map.Entry<UUID, Integer> entry : ThePurgeOfSalem.this.hunters.entrySet()) 
		{
			if (!ThePurgeOfSalem.this.showedHunters.contains(entry.getKey())) 
			{
				ThePurgeOfSalem.this.showedHunters.add(entry.getKey());
				ThePurgeOfSalem.this.huntersTeam.addPlayer(Bukkit.getOfflinePlayer(entry.getKey()));
				
				switch(entry.getValue())
				{
				case 0:
					Bukkit.broadcastMessage(ChatColor.WHITE + Bukkit.getOfflinePlayer(entry.getKey()).getName()
							+ " a revele qu'il etait un repurgateur : l'inquisiteur !");
					break;
				case 1:
					Bukkit.broadcastMessage(ChatColor.WHITE + Bukkit.getOfflinePlayer(entry.getKey()).getName()
							+ " a revele qu'il etait un repurgateur : le vengeur !");
					break;
				case 2:
					Bukkit.broadcastMessage(ChatColor.WHITE + Bukkit.getOfflinePlayer(entry.getKey()).getName()
							+ " a revele qu'il etait un repurgateur : le guerisseur !");
					break;
				case 3:
					Bukkit.broadcastMessage(ChatColor.WHITE + Bukkit.getOfflinePlayer(entry.getKey()).getName()
							+ " a revele qu'il etait un repurgateur : le protecteur !");
					break;
				case 4:
					Bukkit.broadcastMessage(ChatColor.WHITE + Bukkit.getOfflinePlayer(entry.getKey()).getName()
							+ " a revele qu'il etait un repurgateur : le martyre !");
					break;
				case 5:
					Bukkit.broadcastMessage(ChatColor.WHITE + Bukkit.getOfflinePlayer(entry.getKey()).getName()
							+ " a revele qu'il etait un repurgateur : le salvateur !");
					break;
				}
			}
		}

		for (Player online : Bukkit.getOnlinePlayers()) 
		{
			online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
		}

		unregisterTeam();
		unregisterTaupeTeam();
		unregisterHunterTeam();
		unregisterSupertaupeTeam();

		if (check) 
		{
			checkVictory();
		}
	}


	public void supertaupeReveal(boolean check) 
	{
		 UUID uid = ThePurgeOfSalem.this.supertaupe;

		 if(ThePurgeOfSalem.this.taupes.get(uid) == 2)
		 {
			 ThePurgeOfSalem.this.assassinPotionUsed = true;
		 }		 
		 
		ThePurgeOfSalem.this.aliveTaupes.remove(uid);
		ThePurgeOfSalem.this.supertaupeTeam.addPlayer(Bukkit.getOfflinePlayer(uid));
		
		Bukkit.broadcastMessage(ChatColor.DARK_RED + Bukkit.getOfflinePlayer(uid).getName()
				+ " est possede par le Diable !");

		for (Player online : Bukkit.getOnlinePlayers())
		{
			online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
			online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
		}

		unregisterTeam();
		unregisterTaupeTeam();
		unregisterHunterTeam();

		if (check)
		{
			checkVictory();
		}
	}


	public void superhunterReveal()
	{
		UUID uid = ThePurgeOfSalem.this.superhunter;
		
		Bukkit.broadcastMessage(ChatColor.WHITE + Bukkit.getOfflinePlayer(uid).getName()
				+ " a ete choisi par Dieu pour combattre Satan !");

		for (Player online : Bukkit.getOnlinePlayers())
		{
			online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
			online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
		}
	}
	
	
	public void supertaupeDeath()
	{
		if(ThePurgeOfSalem.this.supertaupeConsumed)
		{
			Bukkit.broadcastMessage(ChatColor.DARK_RED 
					+ "Le suppot de Satan est mort, consume par le demon !");
		}
		else
		{
			Bukkit.broadcastMessage(ChatColor.DARK_RED 
					+ "Le suppot de Satan est mort !");
		}

		for (Player online : Bukkit.getOnlinePlayers())
		{
			online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
			online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
		}
		
		resetSuperhunter();
	}
	
	
	public void resetSuperhunter()
	{
		if(ThePurgeOfSalem.this.playersAlive.contains(ThePurgeOfSalem.this.superhunter))
		{
			OfflinePlayer superhunter = Bukkit.getPlayer(ThePurgeOfSalem.this.superhunter);
			
			for (PotionEffect potion : superhunter.getPlayer().getActivePotionEffects())
			{
				if(potion.getType().equals(PotionEffectType.FIRE_RESISTANCE))
				{
					superhunter.getPlayer().removePotionEffect(potion.getType());
					break;
				}
			}
			
			superhunter.getPlayer().sendMessage("Le suppot de Satan est mort, vous sentez le pouvoir qui vous habite se retirer ! ");
		}
	}
	
	
	public void claimKit(Player player, int role) 
	{
		ItemStack kit = new ItemStack(Material.GOLDEN_APPLE, 10);
		Location loc = player.getLocation();
		loc.add(player.getEyeLocation().getDirection().normalize());
		Potion potion = new Potion(1);
		
		switch (role)
		{
		case (0):
			kit.setAmount(2);
			kit.setType(Material.POTION);
			potion.setType(PotionType.SLOWNESS);
			potion.setHasExtendedDuration(false);
			potion.setSplash(true);
			potion.apply(kit);
			player.getWorld().dropItemNaturally(loc, kit);

			potion.setType(PotionType.WEAKNESS);
			potion.setHasExtendedDuration(false);
			potion.setSplash(true);
			potion.apply(kit);
			player.getWorld().dropItemNaturally(loc, kit);
			
			kit.setAmount(1);
			potion.setType(PotionType.POISON);
			potion.setHasExtendedDuration(false);
			potion.setSplash(true);
			potion.apply(kit);
			player.getWorld().dropItemNaturally(loc, kit);
			break;
		case (1):
			player.getWorld().dropItemNaturally(loc, kit);
			break;
		case (2):
			kit.setAmount(32);
			kit.setType(Material.ARROW);
			player.getWorld().dropItemNaturally(loc, kit);
			
			kit.setAmount(1);
			kit.setType(Material.BOW);
			kit.addEnchantment(Enchantment.ARROW_DAMAGE, 1);

			kit.setType(Material.POTION);
			potion.setType(PotionType.POISON);
			potion.setHasExtendedDuration(false);
			potion.setSplash(true);
			potion.apply(kit);
			player.getWorld().dropItemNaturally(loc, kit);
			
			potion.setType(PotionType.INVISIBILITY);
			potion.setHasExtendedDuration(true);
			potion.setSplash(false);
			potion.apply(kit);
			player.getWorld().dropItemNaturally(loc, kit);
			break;
		case (3):
			kit.setAmount(1);
			kit.setType(Material.IRON_BOOTS);
			kit.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
			player.getWorld().dropItemNaturally(loc, kit);

			kit.setType(Material.IRON_LEGGINGS);
			kit.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
			player.getWorld().dropItemNaturally(loc, kit);

			kit.setType(Material.IRON_CHESTPLATE);
			kit.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
			player.getWorld().dropItemNaturally(loc, kit);

			kit.setType(Material.IRON_HELMET);
			kit.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
			player.getWorld().dropItemNaturally(loc, kit);

			kit.setType(Material.IRON_SWORD);
			kit.addEnchantment(Enchantment.DAMAGE_ALL, 1);
			player.getWorld().dropItemNaturally(loc, kit);
			break;
		case (4):
			kit.setAmount(2);
			kit.setType(Material.POTION);
			potion.setType(PotionType.REGEN);
			potion.setLevel(2);
			potion.setHasExtendedDuration(false);
			potion.setSplash(true);
			potion.apply(kit);
			player.getWorld().dropItemNaturally(loc, kit);
			
			kit.setAmount(5);
			kit.setType(Material.MONSTER_EGG);
			kit.setDurability((short) 95);
			player.getWorld().dropItemNaturally(loc, kit);
			break;
		case 5:
			String repurgators = ChatColor.WHITE + "Les repurgateurs sont :";
			
			player.addPotionEffect(
					new PotionEffect(
							PotionEffectType.BLINDNESS,
							20 * 5, 
							1));
			for(UUID hunter : ThePurgeOfSalem.this.aliveHunters)
			{
				Team team = ThePurgeOfSalem.this.s.getPlayerTeam(Bukkit.getOfflinePlayer(hunter));
				if(team.getName() == ThePurgeOfSalem.this.seerInitialTeam.getName())
				{
					continue;
				}
				repurgators += " " + team.getPrefix() + Bukkit.getOfflinePlayer(hunter).getName() 
						+ ChatColor.WHITE + ",";
			}
			repurgators = repurgators.substring(0, repurgators.length() - 1);
			player.sendMessage(repurgators);
			break;
		}
		
		this.claimedtaupes.add(player.getUniqueId());
	}


	public void claimPower(Player player, int role)
	{
		switch(role)
		{
		case 0:
			String heretics = ChatColor.WHITE + "Les heretiques sont :";
			
			player.addPotionEffect(
					new PotionEffect(
							PotionEffectType.BLINDNESS,
							20 * 5, 
							1));
			for(UUID taupe : ThePurgeOfSalem.this.aliveTaupes)
			{
				Team team = ThePurgeOfSalem.this.s.getPlayerTeam(Bukkit.getOfflinePlayer(taupe));
				if(team.getName() == ThePurgeOfSalem.this.inquisitorInitialTeam.getName())
				{
					continue;
				}
				heretics += " " + team.getPrefix() + Bukkit.getOfflinePlayer(taupe).getName() 
						+ ChatColor.WHITE + ",";
			}
			heretics = heretics.substring(0, heretics.length() - 1);
			player.sendMessage(heretics);
			break;
		case 1:
			for(UUID hunter : ThePurgeOfSalem.this.aliveHunters)
			{				
				Bukkit.getPlayer(hunter).addPotionEffect(
					new PotionEffect(
							PotionEffectType.INCREASE_DAMAGE,
							20 * 10, 
							0,
							hunter == player.getUniqueId(),
							hunter == player.getUniqueId()));
			}
			break;
		case 2:
			for(UUID hunter : ThePurgeOfSalem.this.aliveHunters)
			{				
				Bukkit.getPlayer(hunter).addPotionEffect(
					new PotionEffect(
							PotionEffectType.REGENERATION,
							20 * 10, 
							1,
							hunter == player.getUniqueId(),
							hunter == player.getUniqueId()));
			}			
			break;
		case 3:
			for(UUID hunter : ThePurgeOfSalem.this.aliveHunters)
			{				
				Bukkit.getPlayer(hunter).addPotionEffect(
					new PotionEffect(
							PotionEffectType.DAMAGE_RESISTANCE,
							20 * 10, 
							1,
							hunter == player.getUniqueId(),
							hunter == player.getUniqueId()));
			}	
			break;
		case 5:
			player.addPotionEffect(
					new PotionEffect(
							PotionEffectType.BLINDNESS,
							20 * 5, 
							1));
			preventRepurgatorDeath();
			break;
		}
	}
	
	
	public void preventRepurgatorDeath()
	{
		salvation = true;
		
		new BukkitRunnable() 
		{
			public void run() 
			{
				ThePurgeOfSalem.this.salvation = false;
			}
		}.runTaskLater(this, 20 * 30);
	}


	public void spawnGraal()
	{
		int it = 0;
		boolean tooClose = true;
		Random rdm = new Random();
		int x, z;
		Location l = new Location(Bukkit.getWorld(getConfig().get("world").toString()),
				0, 0, 0);
		Location playerLoc;
		
		while(it < 20)
		{
			tooClose = false;
			
			x = rdm.nextInt(ThePurgeOfSalem.this.getConfig().getInt("worldborder.size"))
					 - ThePurgeOfSalem.this.getConfig().getInt("worldborder.size") / 2;
			z = rdm.nextInt(ThePurgeOfSalem.this.getConfig().getInt("worldborder.size"))
					 - ThePurgeOfSalem.this.getConfig().getInt("worldborder.size") / 2;
			
			l.setX(x);
			l.setZ(z);
			l.setY(l.getWorld().getHighestBlockYAt(l));
			
			for(UUID uid : ThePurgeOfSalem.this.playersAlive)
			{
				playerLoc = Bukkit.getPlayer(uid).getLocation();
				if(l.distance(playerLoc) < 100)
				{
					tooClose = true;
				}
			}
			
			if(!tooClose)
			{
				break;
			}
			
			++it;
		}
		
		if(tooClose)
		{
			l.setX(0);
			l.setZ(0);
			l.setY(l.getWorld().getHighestBlockYAt(l));
			ThePurgeOfSalem.this.graalLocation = l;
		}
		else
		{
			ThePurgeOfSalem.this.graalLocation = l;
		}

		ThePurgeOfSalem.this.graalLocation.getBlock().setType(Material.GOLD_PLATE);
		l.setY(l.getY() + 2);
		l.getBlock().setType(Material.BEACON);
		
		ThePurgeOfSalem.this.graalSpawned = true;
	}
	
	
	public void announceGraal()
	{
		Bukkit.broadcastMessage(ChatColor.GOLD
				+ "Le Graal est apparu en "
				+ (int)ThePurgeOfSalem.this.graalLocation.getX()
				+ " / "
				+ (int)ThePurgeOfSalem.this.graalLocation.getZ()
				+ " ! ");

		Bukkit.broadcastMessage(ChatColor.GOLD
				+ "Depechez-vous de nous en emparer avant que d'autres ne le fassent ! ");
	}
	
}
