package fr.vraken.thepurgeofsalem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.bukkit.World;
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

import fr.vraken.gameoftaupes.EventsClass;
import fr.vraken.gameoftaupes.FilesManager;
import fr.vraken.gameoftaupes.GameOfTaupes;
import fr.vraken.gameoftaupes.Title;

import org.bukkit.enchantments.Enchantment;

public class ThePurgeOfSalem extends JavaPlugin 
{
	// Files
	FilesManager filesManager;
	FileConfiguration teamf;
	FileConfiguration deathf;

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

	// Hunters
	Team huntersTeam;
	boolean hunterssetup;
	HashMap<UUID, Integer> hunters = new HashMap<UUID, Integer>();
	Boolean isHuntersTeamDead;
	ArrayList<UUID> aliveHunters = new ArrayList<UUID>();
	ArrayList<UUID> showedHunters = new ArrayList<UUID>();
	ArrayList<UUID> claimedHunters = new ArrayList<UUID>();
	
	// Supertaupe & Superhunter
	Team supertaupeTeam;
	boolean supertaupesetup;
	UUID supertaupe;
	UUID superhunter;
	Boolean isSupertaupeDead;
	int supertaupeLifetime;

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
	Team rose;
	Team jaune;
	Team violette;
	Team cyan;
	Team verte;
	
	// Meetup
	boolean graalSpawned = false;
	Location graalLocation;
	

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

		this.sm = Bukkit.getScoreboardManager();
		this.s = this.sm.getMainScoreboard();
		if (this.s.getObjective("ThePurgeOfSalem") != null) 
		{
			this.s.getObjective("ThePurgeOfSalem").unregister();
		}

		getConfig().options().copyDefaults(true);
		teamf.options().copyDefaults(true);
		deathf.options().copyDefaults(true);
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

		this.taupesTeam = this.s.registerNewTeam("Heretiques");
		this.taupesTeam.setPrefix(ChatColor.RED.toString());
		this.taupesTeam.setSuffix(ChatColor.WHITE.toString());

		this.supertaupeTeam = this.s.registerNewTeam("Suppot de Satan");
		this.supertaupeTeam.setPrefix(ChatColor.DARK_RED.toString());
		this.supertaupeTeam.setSuffix(ChatColor.WHITE.toString());

		this.huntersTeam = this.s.registerNewTeam("Repurgateurs");
		this.huntersTeam.setPrefix(ChatColor.RED.toString());
		this.huntersTeam.setSuffix(ChatColor.WHITE.toString());
		
		this.isTaupesTeamDead = false;
		this.isSupertaupeDead = false;
		this.isHuntersTeamDead = false;

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
				GameOfTaupes.this.minute = minutes;

				// SCOREBOARD RESET AT EVERY SECOND
				// --------------------------------
				NumberFormat formatter = new DecimalFormat("00");
				String minute = formatter.format(this.minutes);
				String second = formatter.format(this.seconds);
				GameOfTaupes.this.s.resetScores(minute + ":" + second);
				GameOfTaupes.this.s.resetScores("" + ChatColor.WHITE + GameOfTaupes.this.tmpPlayers + ChatColor.GRAY + " joueurs");
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);

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
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
				GameOfTaupes.this.gameState++;
				GameOfTaupes.this.objMinute = objFormatter
						.format(GameOfTaupes.this.getConfig().getInt("options.settaupesafter")
								- GameOfTaupes.this.getConfig().getInt("options.pvptime") - 1);
				GameOfTaupes.this.objSecond = "59";
				GameOfTaupes.this.objTxt = "Roles : ";
				GameOfTaupes.this.hasChangedGS = true;
				GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":"
						+ GameOfTaupes.this.objSecond;
			}
		}.runTaskLater(this, 1200 * getConfig().getInt("options.pvptime"));

		// TAUPES ANNOUNCEMENT
		// -------------------
		new BukkitRunnable() 
		{
			public void run() 
			{
				taupeAnnouncement();

				// Updating scoreboard status
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
				GameOfTaupes.this.gameState++;
				GameOfTaupes.this.objMinute = objFormatter
						.format(GameOfTaupes.this.getConfig().getInt("options.setsupertaupesafter")
								- GameOfTaupes.this.getConfig().getInt("options.settaupesafter") - 1);
				GameOfTaupes.this.objSecond = "59";
				GameOfTaupes.this.objTxt = "Support de Satan : ";
				GameOfTaupes.this.hasChangedGS = true;
				GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":"
						+ GameOfTaupes.this.objSecond;
			}
		}.runTaskLater(this, 1200 * getConfig().getInt("options.settaupesafter"));

		// SUPERTAUPE ANNOUNCEMENT
		// -------------------
		new BukkitRunnable() 
		{
			public void run() 
			{
				supertaupeAnnouncement();

				// Updating scoreboard status
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
				GameOfTaupes.this.gameState++;
				GameOfTaupes.this.objMinute = objFormatter
						.format(GameOfTaupes.this.getConfig().getInt("worldborder.retractafter")
								- GameOfTaupes.this.getConfig().getInt("options.setsupertaupesafter") - 1);
				GameOfTaupes.this.objSecond = "59";
				GameOfTaupes.this.objTxt = "Graal : ";
				GameOfTaupes.this.hasChangedGS = true;
				GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":"
						+ GameOfTaupes.this.objSecond;
			}
		}.runTaskLater(this, 1200 * getConfig().getInt("options.setsupertaupesafter"));

		// TAUPES REVEAL
		// -------------
		new BukkitRunnable() 
		{
			public void run() 
			{
				forceReveal(true);

				// Updating scoreboard status
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
				GameOfTaupes.this.gameState++;
				if (!GameOfTaupes.this.getConfig().getBoolean("options.supertaupe")) 
				{
					return;
				}
				
				GameOfTaupes.this.objMinute = objFormatter
						.format(GameOfTaupes.this.getConfig().getInt("options.superreveal")
								- GameOfTaupes.this.getConfig().getInt("options.forcereveal") - 1);
				GameOfTaupes.this.objSecond = "59";
				GameOfTaupes.this.objTxt = "Supertaupe reveal : ";
				GameOfTaupes.this.hasChangedGS = true;
				GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":"
						+ GameOfTaupes.this.objSecond;
			}
		}.runTaskLater(this, 1200 * getConfig().getInt("options.forcereveal"));

		// SUPERTAUPE REVEAL
		// ------------
		new BukkitRunnable() 
		{
			public void run() 
			{
				superReveal(true);

				// Updating scoreboard status
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
				GameOfTaupes.this.gameState++;
				GameOfTaupes.this.objMinute = objFormatter
						.format(GameOfTaupes.this.getConfig().getInt("worldborder.finalretract")
								- GameOfTaupes.this.getConfig().getInt("options.superreveal") - 1);
				GameOfTaupes.this.objSecond = "59";
				GameOfTaupes.this.objTxt = "Final shrink : ";
				GameOfTaupes.this.hasChangedGS = true;
				GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":"
						+ GameOfTaupes.this.objSecond;
			}
		}.runTaskLater(this, 1200 * getConfig().getInt("options.superreveal"));
	}

	public void stopGame() 
	{
		for (Player p : Bukkit.getOnlinePlayers()) 
		{
			if (!GameOfTaupes.this.playersInLobby.contains(p.getUniqueId())) 
			{
				GameOfTaupes.this.playersInLobby.add(p.getUniqueId());

				p.setGameMode(GameMode.ADVENTURE);
				p.teleport(lobbyLocation);
			}

			p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		}

		GameOfTaupes.this.gameEnd = true;
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
				for (int i = 0; i < this.getConfig().getInt("options.taupesteams"); i++) 
				{
					if (this.aliveTaupes.contains(player.getUniqueId())
							&& this.taupes.get(i).contains(player.getUniqueId())) 
					{
						for (UUID taupe : this.taupes.get(i)) 
						{
							if (GameOfTaupes.this.showedsupertaupes.contains(taupe)) 
							{
								continue;
							}

							message = StringUtils.join(args, ' ', 0, args.length);

							String content = ChatColor.GOLD + "(Taupes #" + i + ") " + ChatColor.RED + "<"
									+ player.getName();

							if (!GameOfTaupes.this.s.getPlayerTeam(Bukkit.getOfflinePlayer(taupe)).getName()
									.contains("aupe")) 
							{
								content += "(" + player.getScoreboard().getPlayerTeam(player).getName() + ")";
							}

							content += "> " + ChatColor.WHITE + message;

							Bukkit.getPlayer(taupe).sendMessage(content);
						}
						return true;
					}
				}
				for (int i = 0; i < this.getConfig().getInt("options.huntersteams"); i++) 
				{
					if (this.aliveHunters.contains(player.getUniqueId())
							&& this.hunters.get(i).contains(player.getUniqueId())) 
					{
						for (UUID hunter : this.hunters.get(i)) 
						{
							if (GameOfTaupes.this.huntersRoles.get(i).get(hunter) == 0) 
							{
								player.sendMessage(ChatColor.RED + "Vous etes l'inquisiteur, vous ne pouvez pas communiquer avec les autres chasseurs ! ");
								continue;
							}

							message = StringUtils.join(args, ' ', 0, args.length);

							String content = ChatColor.GOLD + "(Chasseurs #" + i + ") " + ChatColor.RED + "<"
									+ player.getName();

							if (!GameOfTaupes.this.s.getPlayerTeam(Bukkit.getOfflinePlayer(hunter)).getName()
									.contains("unter")) 
							{
								content += "(" + player.getScoreboard().getPlayerTeam(player).getName() + ")";
							}

							content += "> " + ChatColor.WHITE + message;

							Bukkit.getPlayer(hunter).sendMessage(content);
						}
						return true;
					}
				}
				player.sendMessage(ChatColor.RED + "Vous n'etes ni une taupe ni un chasseur !");
				return true;
			}

			// TAUPES AND HUNTERS REVEAL
			// -------------------------
			if (cmd.getName().equalsIgnoreCase("reveal") && this.taupessetup) 
			{
				for (int i = 0; i < this.getConfig().getInt("options.taupesteams"); i++) 
				{
					if (this.taupes.get(i).contains(player.getUniqueId())) 
					{
						if (this.showedtaupes.contains(player.getUniqueId())) 
						{
							player.sendMessage(ChatColor.RED + "Vous vous etes deja revele !");
						} 
						else 
						{
							PlayerInventory inventory = player.getInventory();
							inventory.addItem(new ItemStack[] { new ItemStack(Material.GOLDEN_APPLE, 1) });

							this.taupesteam.get(i).addPlayer(player);
							this.showedtaupes.add(player.getUniqueId());
							for (Player online : Bukkit.getOnlinePlayers()) 
							{
								online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
							}
							Bukkit.broadcastMessage(
									ChatColor.RED + player.getName() + " a revele qu'il etait une taupe !");

							unregisterTeam();
							unregisterTaupeTeam();
							unregisterHunterTeam();
							checkVictory();
						}
						return true;
					}
				}
				for (int i = 0; i < this.getConfig().getInt("options.huntersteams"); i++) 
				{
					if (this.hunters.get(i).contains(player.getUniqueId())) 
					{
						if (this.showedHunters.contains(player.getUniqueId())) 
						{
							player.sendMessage(ChatColor.RED + "Vous vous etes deja revele !");
						} 
						else 
						{
							PlayerInventory inventory = player.getInventory();
							inventory.addItem(new ItemStack[] { new ItemStack(Material.GOLDEN_APPLE, 1) });

							this.huntersteam.get(i).addPlayer(player);
							this.showedHunters.add(player.getUniqueId());
							for (Player online : Bukkit.getOnlinePlayers()) 
							{
								online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
							}
							Bukkit.broadcastMessage(
									ChatColor.RED + player.getName() + " a revele qu'il etait un chasseur !");

							unregisterTeam();
							unregisterTaupeTeam();
							unregisterHunterTeam();
							checkVictory();
						}
						return true;
					}
				}

				player.sendMessage(ChatColor.RED + "Vous n'etes ni une taupe ni un chasseur !");
				return true;
			}

			// SUPERTAUPE REVEAL
			// -----------------
			if (cmd.getName().equalsIgnoreCase("superreveal") && this.supertaupessetup) 
			{
				if (this.supertaupes.containsValue(player.getUniqueId())) 
				{
					int key = -1;

					for (int i = 0; i < this.getConfig().getInt("options.taupesteams"); i++) 
					{
						if (this.supertaupes.get(i) == player.getUniqueId()) 
						{
							key = i;
							break;
						}
					}

					if (this.showedsupertaupes.contains(player.getUniqueId())) 
					{
						player.sendMessage(ChatColor.RED + "Vous vous etes deja revele !");
					} 
					else if (!this.showedtaupes.contains(player.getUniqueId())) 
					{
						player.sendMessage(ChatColor.RED + "Vous devez d'abord vous reveler en tant que taupe !");
					} 
					else 
					{
						PlayerInventory inventory = player.getInventory();
						inventory.addItem(new ItemStack[] { new ItemStack(Material.GOLDEN_APPLE, 2) });

						this.aliveTaupes.remove(player.getUniqueId());
						this.supertaupesteam.get(key).addPlayer((OfflinePlayer) player);
						this.showedsupertaupes.add(player.getUniqueId());
						for (Player online : Bukkit.getOnlinePlayers()) 
						{
							online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
							online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
						}
						Bukkit.broadcastMessage(
								ChatColor.DARK_RED + player.getName() + " a revele qu'il etait une supertaupe !");

						unregisterTeam();
						unregisterTaupeTeam();
						checkVictory();
					}
					return true;
				}
				player.sendMessage(ChatColor.RED + "Vous n'etes pas la supertaupe !");
				return true;
			}

			// TAUPES AND HUNTERS CLAIM KIT
			// ----------------------------
			if (cmd.getName().equalsIgnoreCase("claim") && this.taupessetup) 
			{
				for (int i = 0; i < this.getConfig().getInt("options.taupesteams"); i++) 
				{
					if (this.taupes.get(i).contains(player.getUniqueId())) 
					{
						if (!this.claimedtaupes.contains(player.getUniqueId())) 
						{
							claimKit(player);
						} 
						else 
						{
							player.sendMessage(ChatColor.RED + "Vous avez deja claim votre kit de taupe !");
						}
						return true;
					}
				}
				for (int i = 0; i < this.getConfig().getInt("options.huntersteams"); i++) 
				{
					if (this.hunters.get(i).contains(player.getUniqueId())) 
					{
						if (!this.claimedHunters.contains(player.getUniqueId())) 
						{
							claimPower(player, i, this.huntersRoles.get(i).get(player.getUniqueId()));
						} 
						else if(this.huntersRoles.get(i).get(player.getUniqueId()) == 4)
						{
							player.sendMessage(ChatColor.RED + "Vous etes le martyre, vous ne pouvez pas utiliser votre pouvoir !");
						}
						else 
						{
							player.sendMessage(ChatColor.RED + "Vous avez deja utilise votre pouvoir !");
						}
						return true;
					}
				}
				player.sendMessage(ChatColor.RED + "Vous n'etes ni une taupe ni un chasseur !");
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
				l = rdm.nextInt(6);
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
			this.objMinute = objFormatter.format(this.getConfig().getInt("options.pvptime") - this.minuteTot);
			this.objSecond = "00";
			this.objTxt = "PvP : ";
			this.countdownObj = this.objTxt + this.objMinute + ":" + this.objSecond;
			break;
		case 1:
			this.objMinute = objFormatter.format(this.getConfig().getInt("options.settaupesafter") - this.minuteTot);
			this.objSecond = "00";
			this.objTxt = "Taupes : ";
			this.countdownObj = this.objTxt + this.objMinute + ":" + this.objSecond;
			break;
		case 2:
			this.objMinute = objFormatter
					.format(this.getConfig().getInt("options.setsupertaupesafter") - this.minuteTot);
			this.objSecond = "00";
			this.objTxt = "Supertaupes : ";
			this.countdownObj = this.objTxt + this.objMinute + ":" + this.objSecond;
			break;
		case 3:
			this.objMinute = objFormatter.format(this.getConfig().getInt("options.forcereveal") - this.minuteTot);
			this.objSecond = "00";
			this.objTxt = "Taupes reveal : ";
			this.countdownObj = this.objTxt + this.objMinute + ":" + this.objSecond;
			break;
		case 4:
			this.objMinute = objFormatter.format(this.getConfig().getInt("options.superreveal") - this.minuteTot);
			this.objSecond = "00";
			this.objTxt = "Supertaupes reveal : ";
			this.countdownObj = this.objTxt + this.objMinute + ":" + this.objSecond;
			break;
		}
	}

	
	public void clearPlayers()
	{
		GameOfTaupes.this.playersInLobby.clear();
		GameOfTaupes.this.playersSpec.clear();

		for (Player p : Bukkit.getOnlinePlayers())
		{
			if (!this.playersInTeam.contains(p.getUniqueId()))
			{
				p.kickPlayer("Vous n'avez pas choisi d'equipe. Tant pis pour vous !");
				continue;
			}

			this.playersAlive.add(p.getUniqueId());
			// EventsClass.alive.add(p.getUniqueId());

			p.getInventory().clear();
			p.getInventory().setHelmet(null);
			p.getInventory().setChestplate(null);
			p.getInventory().setLeggings(null);
			p.getInventory().setBoots(null);
			p.setExp(0.0f);
			p.setLevel(0);

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

	
	public void clearTeams() {
		for (Team teams : this.s.getTeams()) {
			if (!teams.getName().contains("Taupes") && !teams.getName().contains("Hunters")) {
				if (teams.getName().contains("SuperTaupe") && !getConfig().getBoolean("options.supertaupe")) {
					teams.unregister();
				} else if (teams.getSize() == 0 && !teams.getName().contains("SuperTaupe")) {
					teams.unregister();
				}
			}
		}
	}

	
	public void setTaupes()
	{
		ArrayList<UUID> players = new ArrayList<UUID>();
		ArrayList<UUID> taupes = new ArrayList<UUID>();
		ArrayList<Integer> teams = new ArrayList<Integer>();
		int psize;
		int tsize;
		int rsize;
		UUID p;
		Random random = new Random(System.currentTimeMillis());
		for (Team team : this.s.getTeams())
		{
			if (team.getPlayers().size() >= 1)
			{
				players.clear();
				taupes.clear();
				teams.clear();

				for (OfflinePlayer player : team.getPlayers())
				{
					players.add(player.getUniqueId());
				}
				
				psize = random.nextInt(players.size());
				p = players.get(psize);
				
				while (true)
				{
					rsize = random.nextInt(6);
					if (!this.taupesRoles.get(tsize).containsValue(rsize))
					{
						break;
					}
				}
				this.taupesRoles.get(tsize).put(p, rsize);

					teams.add(tsize);
					taupes.add(p);
					if (!this.taupes.containsKey(tsize))
					{
						this.taupes.put(tsize, new ArrayList<UUID>());
						this.taupesRoles.put(tsize, new HashMap<UUID, Integer>());
					}
					this.taupes.get(tsize).add(p);
					this.aliveTaupes.add(p);
					this.teamoftaupes.put(p, tsize);
				}
			}
		}
	}

	
	public void setSuperTaupe() {
		if (getConfig().getBoolean(("options.supertaupe"))) {
			Random random = new Random(System.currentTimeMillis());

			for (int i = 0; i < getConfig().getInt("options.taupesteams"); i++) {
				int taupeIndex = random.nextInt(this.taupes.get(i).size());
				UUID spId = this.taupes.get(i).get(taupeIndex);
				this.supertaupes.put(i, spId);
				this.aliveSupertaupes.add(spId);
			}
		}
	}

	
	public void setHunters() 
	{
		ArrayList<UUID> players = new ArrayList<UUID>();
		ArrayList<UUID> hunters = new ArrayList<UUID>();
		ArrayList<Integer> teams = new ArrayList<Integer>();
		int psize;
		int hsize;
		int rsize;
		UUID p;
		Random random = new Random(System.currentTimeMillis());
		for (Team team : this.s.getTeams()) 
		{
			if (team.getPlayers().size() >= 1) 
			{
				players.clear();
				hunters.clear();
				teams.clear();

				for (OfflinePlayer player : team.getPlayers()) 
				{
					players.add(player.getUniqueId());
				}

				for (int i = 0; i < this.getConfig().getInt("options.huntersperteam"); i++) 
				{
					while (true) 
					{
						psize = random.nextInt(players.size());
						p = players.get(psize);
						if (!hunters.contains(p) && !this.aliveTaupes.contains(p)) 
						{
							break;
						}
					}
					while (true) 
					{
						hsize = random.nextInt(this.getConfig().getInt("options.huntersteams"));
						if (!teams.contains(hsize)) 
						{
							if (this.hunters.get(hsize).size() < this.huntersperteam.get(hsize)) 
							{
								break;
							}
						}
					}

					teams.add(hsize);
					hunters.add(p);
					if (!this.hunters.containsKey(hsize)) 
					{
						this.hunters.put(hsize, new ArrayList<UUID>());
						this.huntersRoles.put(hsize, new HashMap<UUID, Integer>());
					}
					this.hunters.get(hsize).add(p);
					this.aliveHunters.add(p);
					
					while (true)
					{
						rsize = random.nextInt(this.nbHuntersRoles);
						if (!this.huntersRoles.get(hsize).containsValue(rsize))
						{
							break;
						}
					}
					this.huntersRoles.get(hsize).put(p, rsize);
				}
			}
		}
	}
	
	
	public void checkVictory() 
	{
		Team lastTeam = null;
		int teamsAlive = 0;
		for (Team team : GameOfTaupes.this.s.getTeams()) 
		{
			if (!team.getName().contains("aupe") && !team.getName().contains("unter")) 
			{
				teamsAlive++;
				if (teamsAlive > 1) 
				{
					return;
				}
				lastTeam = team;
			}
		}

		for (int i = 0; i < GameOfTaupes.this.taupes.size(); i++) 
		{
			for (UUID uid : GameOfTaupes.this.taupes.get(i)) 
			{
				if (GameOfTaupes.this.playersAlive.contains(uid) && GameOfTaupes.this.supertaupes.get(i) != uid) 
				{
					teamsAlive++;
					if (teamsAlive > 1) 
					{
						return;
					}
					lastTeam = GameOfTaupes.this.taupesteam.get(i);
					break;
				}
			}

			if (GameOfTaupes.this.aliveSupertaupes.contains(GameOfTaupes.this.supertaupes.get(i))) 
			{
				teamsAlive++;
				if (teamsAlive > 1) 
				{
					return;
				}
				lastTeam = GameOfTaupes.this.supertaupesteam.get(i);
			}
		}

		for (int i = 0; i < GameOfTaupes.this.hunters.size(); i++) 
		{
			for (UUID uid : GameOfTaupes.this.hunters.get(i)) 
			{
				if (GameOfTaupes.this.playersAlive.contains(uid)) 
				{
					teamsAlive++;
					if (teamsAlive > 1) 
					{
						return;
					}
					lastTeam = GameOfTaupes.this.huntersteam.get(i);
					break;
				}
			}
		}

		if (teamsAlive == 1 || teamsAlive == 0) 
		{
			forceReveal(false);
			superReveal(false);
			announceWinner(lastTeam);
		}
	}

	
	public void announceWinner(Team team) 
	{
		if (team == null) 
		{
			Bukkit.broadcastMessage("Toutes les equipes ont ete eliminees, personne n'a gagne ! ");
			Bukkit.getScheduler().cancelAllTasks();
			return;
		}

		Bukkit.broadcastMessage(GameOfTaupes.this.teamAnnounceString + team.getPrefix() + team.getName()
				+ ChatColor.RESET + " a gagne ! ");

		Bukkit.getScheduler().cancelAllTasks();

		GameOfTaupes.this.playersAlive.clear();
	}

	
	public void unregisterTeam() 
	{
		for (Team teams : GameOfTaupes.this.s.getTeams()) 
		{
			// NORMAL TEAM UNREGISTRATION
			if (teams.getSize() == 0 && !teams.getName().contains("Taupes")
					&& !teams.getName().contains("SuperTaupe")) 
			{
				Bukkit.broadcastMessage(GameOfTaupes.this.teamAnnounceString + teams.getPrefix() + teams.getName()
						+ ChatColor.RESET + " a ete eliminee ! ");
				teams.unregister();
			}
		}
	}

	
	public void unregisterTaupeTeam() 
	{
		for (int i = 0; i < this.getConfig().getInt("options.taupesteams"); ++i) 
		{
			UUID supertaupe = GameOfTaupes.this.supertaupes.get(i);

			boolean dead = true;
			int showed = 0;
			for (UUID uid : GameOfTaupes.this.taupes.get(i)) 
			{
				if (GameOfTaupes.this.aliveTaupes.contains(uid)) 
				{
					dead = false;
				}
				if (GameOfTaupes.this.showedtaupes.contains(uid)) 
				{
					showed++;
				}
			}

			if (dead && !GameOfTaupes.this.isTaupesTeamDead.get(i)
					&& GameOfTaupes.this.taupes.get(i).size() == showed) 
			{
				GameOfTaupes.this.isTaupesTeamDead.put(i, true);
				Bukkit.broadcastMessage(ChatColor.RED + "L'equipe des taupes #" + i + " a ete eliminee ! ");
				GameOfTaupes.this.taupesteam.get(i).unregister();
			}
			if (!GameOfTaupes.this.isSupertaupeDead.get(i) && GameOfTaupes.this.showedsupertaupes.contains(supertaupe)
					&& !GameOfTaupes.this.aliveSupertaupes.contains(supertaupe)
					&& GameOfTaupes.this.getConfig().getBoolean("options.supertaupe")) 
			{
				GameOfTaupes.this.isSupertaupeDead.put(i, true);
				Bukkit.broadcastMessage(ChatColor.DARK_RED + "La supertaupe #" + i + " a ete eliminee ! ");
				GameOfTaupes.this.supertaupesteam.get(i).unregister();
			}
		}
	}

	
	public void unregisterHunterTeam() 
	{
		for (int i = 0; i < this.getConfig().getInt("options.huntersteams"); ++i) 
		{
			boolean dead = true;
			int showed = 0;
			for (UUID uid : GameOfTaupes.this.hunters.get(i)) 
			{
				if (GameOfTaupes.this.aliveHunters.contains(uid)) 
				{
					dead = false;
				}
				if (GameOfTaupes.this.showedHunters.contains(uid)) 
				{
					showed++;
				}
			}

			if (dead && !GameOfTaupes.this.isHuntersTeamDead.get(i)
					&& GameOfTaupes.this.hunters.get(i).size() == showed) 
			{
				GameOfTaupes.this.isHuntersTeamDead.put(i, true);
				Bukkit.broadcastMessage(ChatColor.WHITE + "L'equipe des chasseurs #" + i + " a ete eliminee ! ");
				GameOfTaupes.this.huntersteam.get(i).unregister();
			}
		}
	}

	
	public void writeScoreboard(int minutes, int seconds)
	{
		NumberFormat formatter2 = new DecimalFormat("00");
		String minute2 = ((NumberFormat) formatter2).format(minutes);
		String second2 = ((NumberFormat) formatter2).format(seconds);

		GameOfTaupes.this.s.getObjective(GameOfTaupes.this.obj.getDisplayName())
				.getScore("" + ChatColor.WHITE + this.playersAlive.size() + ChatColor.GRAY + " joueurs").setScore(-1);
		
		GameOfTaupes.this.tmpPlayers = this.playersAlive.size();

		if (GameOfTaupes.this.gameState < 7)
		{
			if (!GameOfTaupes.this.hasChangedGS)
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

				GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":"
						+ GameOfTaupes.this.objSecond;
			}
			else
			{
				GameOfTaupes.this.hasChangedGS = false;
			}

			GameOfTaupes.this.s.getObjective(GameOfTaupes.this.obj.getDisplayName())
					.getScore(ChatColor.WHITE + GameOfTaupes.this.countdownObj).setScore(-4);
		}

		GameOfTaupes.this.s.getObjective(GameOfTaupes.this.obj.getDisplayName()).getScore(minute2 + ":" + second2)
				.setScore(-5);
	}

	
	public void taupeAnnouncement()
	{
		OfflinePlayer taupe;
		for (int i = 0; i < GameOfTaupes.this.taupes.size(); i++)
		{
			for (UUID uid : GameOfTaupes.this.taupes.get(i))
			{
				taupe = Bukkit.getOfflinePlayer(uid);
				if (taupe.isOnline())
				{
					roleAnnouncement(uid, 0, GameOfTaupes.this.taupesRoles.get(i).get(uid), false);
				}
			}
		}
		GameOfTaupes.this.taupessetup = true;
	}

	
	public void hunterAnnouncement() 
	{
		OfflinePlayer hunter;
		
		for (int i = 0; i < GameOfTaupes.this.hunters.size(); i++) 
		{
			for (UUID uid : GameOfTaupes.this.hunters.get(i)) 
			{
				hunter = Bukkit.getOfflinePlayer(uid);
				if (hunter.isOnline()) 
				{
					roleAnnouncement(uid, 0, GameOfTaupes.this.huntersRoles.get(i).get(uid), false);
				}
			}
		}
		GameOfTaupes.this.hunterssetup = true;
	}

	
	public void supertaupeAnnouncement()
	{
		OfflinePlayer player;
		for (int i = 0; i < GameOfTaupes.this.supertaupes.size(); i++)
		{
			player = Bukkit.getOfflinePlayer(GameOfTaupes.this.supertaupes.get(i));
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
		}
		GameOfTaupes.this.supertaupessetup = true;
	}
	
	
	public void roleAnnouncement(UUID uid, int status, int role, boolean chosen)
	{
		String s1 = "", s2 = "", s3 = "", s4 = "", s5 = "", s6 = ""; 
		String title1 = "", title2 = "";
		
		Player player = Bukkit.getPlayer(uid);
		
		player.sendMessage(ChatColor.RED + "-------Annonce IMPORTANTE------");	
		
		if(status == 0)
		{
			switch(role)
			{
			case 0:
				s1 = ChatColor.GOLD + "Vous etes un heretique : la sorciere !";
				s2 = ChatColor.GOLD + "Pour parler avec les autres heretiques, executez la commande /t < message>";
				s3 = ChatColor.GOLD	+ "Si vous voulez devoiler votre vraie identite, executez la commande /reveal";
				s4 = ChatColor.GOLD + "Pour obtenir votre kit d'heretique, executez la commande /claim";
				s5 = ChatColor.GOLD + "Votre but : " + ChatColor.DARK_RED + "Tuer les membres de votre \"equipe\"";
				title1 = "Vous etes un heretique !";
				title2 = "Ne le dites a personne !";
				break;
			case 1:
				s1 = ChatColor.GOLD + "Vous etes un heretique : l'alchimiste !";
				s2 = ChatColor.GOLD + "Pour parler avec les autres heretiques, executez la commande /t < message>";
				s3 = ChatColor.GOLD	+ "Si vous voulez devoiler votre vraie identite, executez la commande /reveal";
				s4 = ChatColor.GOLD + "Pour obtenir votre kit d'heretique, executez la commande /claim";
				s5 = ChatColor.GOLD + "Votre but : " + ChatColor.DARK_RED + "Tuer les membres de votre \"equipe\"";
				title1 = "Vous etes un heretique !";
				title2 = "Ne le dites a personne !";
				break;
			case 2:
				s1 = ChatColor.GOLD + "Vous etes un heretique : l'assassin !";
				s2 = ChatColor.GOLD + "Pour parler avec les autres heretiques, executez la commande /t < message>";
				s3 = ChatColor.GOLD	+ "Si vous voulez devoiler votre vraie identite, executez la commande /reveal";
				s4 = ChatColor.GOLD + "Pour obtenir votre kit d'heretique, executez la commande /claim";
				s5 = ChatColor.GOLD + "Votre but : " + ChatColor.DARK_RED + "Tuer les membres de votre \"equipe\"";
				title1 = "Vous etes un heretique !";
				title2 = "Ne le dites a personne !";
				break;
			case 3:
				s1 = ChatColor.GOLD + "Vous etes un heretique : le mercenaire !";
				s2 = ChatColor.GOLD + "Pour parler avec les autres heretiques, executez la commande /t < message>";
				s3 = ChatColor.GOLD	+ "Si vous voulez devoiler votre vraie identite, executez la commande /reveal";
				s4 = ChatColor.GOLD + "Pour obtenir votre kit d'heretique, executez la commande /claim";
				s5 = ChatColor.GOLD + "Votre but : " + ChatColor.DARK_RED + "Tuer les membres de votre \"equipe\"";
				title1 = "Vous etes un heretique !";
				title2 = "Ne le dites a personne !";
				break;
			case 4:
				s1 = ChatColor.GOLD + "Vous etes un heretique : le druide !";
				s2 = ChatColor.GOLD + "Pour parler avec les autres heretiques, executez la commande /t < message>";
				s3 = ChatColor.GOLD	+ "Si vous voulez devoiler votre vraie identite, executez la commande /reveal";
				s4 = ChatColor.GOLD + "Pour obtenir votre kit d'heretique, executez la commande /claim";
				s5 = ChatColor.GOLD + "Votre but : " + ChatColor.DARK_RED + "Tuer les membres de votre \"equipe\"";
				title1 = "Vous etes un heretique !";
				title2 = "Ne le dites a personne !";
				break;
				
				
			case 10:
				s1 = ChatColor.GOLD + "Vous etes un chasseur d'heretique : l'inquisiteur !";
				s3 = ChatColor.GOLD	+ "Si vous voulez devoiler votre vraie identite, executez la commande /reveal";
				s4 = ChatColor.GOLD + "Pour utiliser votre pouvoir divin, executez la commande /claim";
				s5 = ChatColor.GOLD + "Votre but : " + ChatColor.DARK_RED + "Tuer les heretique PUIS les autres villageois";
				s6 = ChatColor.GOLD + "Faites attention cependant ! Si vous tuez un villageois avant d'avoir elimine tous les heretiques, toute votre equipe perdra de la vie !";
				title1 = "Vous etes un chasseur d'heretique !";
				title2 = "Ne le dites a personne !";
				break;
			case 11:
				s1 = ChatColor.GOLD + "Vous etes un chasseur d'heretique : le venguer !";
				s2 = ChatColor.GOLD + "Pour parler avec les autres heretiques, executez la commande /t < message>";
				s3 = ChatColor.GOLD	+ "Si vous voulez devoiler votre vraie identite, executez la commande /reveal";
				s4 = ChatColor.GOLD + "Pour utiliser votre pouvoir divin, executez la commande /claim";
				s5 = ChatColor.GOLD + "Votre but : " + ChatColor.DARK_RED + "Tuer les heretique PUIS les autres villageois";
				s6 = ChatColor.GOLD + "Faites attention cependant ! Si vous tuez un villageois avant d'avoir elimine tous les heretiques, toute votre equipe perdra de la vie !";
				title1 = "Vous etes un chasseur d'heretique !";
				title2 = "Ne le dites a personne !";
				break;
			case 12:
				s1 = ChatColor.GOLD + "Vous etes un chasseur d'heretique : le guerisseur !";
				s2 = ChatColor.GOLD + "Pour parler avec les autres heretiques, executez la commande /t < message>";
				s3 = ChatColor.GOLD	+ "Si vous voulez devoiler votre vraie identite, executez la commande /reveal";
				s4 = ChatColor.GOLD + "Pour utiliser votre pouvoir divin, executez la commande /claim";
				s5 = ChatColor.GOLD + "Votre but : " + ChatColor.DARK_RED + "Tuer les heretique PUIS les autres villageois";
				s6 = ChatColor.GOLD + "Faites attention cependant ! Si vous tuez un villageois avant d'avoir elimine tous les heretiques, toute votre equipe perdra de la vie !";
				title1 = "Vous etes un chasseur d'heretique !";
				title2 = "Ne le dites a personne !";
				break;
			case 13:
				s1 = ChatColor.GOLD + "Vous etes un chasseur d'heretique : le protecteur !";
				s2 = ChatColor.GOLD + "Pour parler avec les autres heretiques, executez la commande /t < message>";
				s3 = ChatColor.GOLD	+ "Si vous voulez devoiler votre vraie identite, executez la commande /reveal";
				s4 = ChatColor.GOLD + "Pour utiliser votre pouvoir divin, executez la commande /claim";
				s5 = ChatColor.GOLD + "Votre but : " + ChatColor.DARK_RED + "Tuer les heretique PUIS les autres villageois";
				s6 = ChatColor.GOLD + "Faites attention cependant ! Si vous tuez un villageois avant d'avoir elimine tous les heretiques, toute votre equipe perdra de la vie !";
				title1 = "Vous etes un chasseur d'heretique !";
				title2 = "Ne le dites a personne !";
				break;
			case 14:
				s1 = ChatColor.GOLD + "Vous etes un chasseur d'heretique : le martyre !";
				s2 = ChatColor.GOLD + "Pour parler avec les autres heretiques, executez la commande /t < message>";
				s3 = ChatColor.GOLD	+ "Si vous voulez devoiler votre vraie identite, executez la commande /reveal";
				s5 = ChatColor.GOLD + "Votre but : " + ChatColor.DARK_RED + "Tuer les heretique PUIS les autres villageois";
				s6 = ChatColor.GOLD + "Faites attention cependant ! Si vous tuez un villageois avant d'avoir elimine tous les heretiques, toute votre equipe perdra de la vie !";
				title1 = "Vous etes un chasseur d'heretique !";
				title2 = "Ne le dites a personne !";
				break;
			}
		}	
		
		player.sendMessage(s1);
		player.sendMessage(s2);
		player.sendMessage(s3);
		player.sendMessage(s4);
		player.sendMessage(s5);
		player.sendMessage(s6);
		
		player.sendMessage(ChatColor.RED + "-------------------------------");		
		
		if(status == 0 
				|| status == 1)
		{
			Title.sendTitle(player, title1, title2);	
		}
	}

	
	public void forceReveal(boolean check) 
	{
		for (int i = 0; i < GameOfTaupes.this.taupes.size(); i++) 
		{
			for (UUID taupe : GameOfTaupes.this.taupes.get(i)) 
			{
				if (!GameOfTaupes.this.showedtaupes.contains(taupe)) 
				{
					GameOfTaupes.this.taupesteam.get(i).addPlayer(Bukkit.getOfflinePlayer(taupe));
					GameOfTaupes.this.showedtaupes.add(taupe);
					Bukkit.broadcastMessage(ChatColor.RED + Bukkit.getOfflinePlayer(taupe).getName()
							+ " a revele qu'il etait une taupe !");
				}
			}
		}

		for (Player online : Bukkit.getOnlinePlayers()) 
		{
			online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
		}

		GameOfTaupes.this.taupessetup = true;

		unregisterTeam();
		unregisterTaupeTeam();
		unregisterHunterTeam();

		if (check) 
		{
			checkVictory();
		}
	}

	
	public void superReveal(boolean check) {
		UUID uid;
		for (int i = 0; i < GameOfTaupes.this.supertaupes.size(); i++) {
			uid = GameOfTaupes.this.supertaupes.get(i);
			if (!GameOfTaupes.this.showedsupertaupes.contains(uid)) {
				GameOfTaupes.this.aliveTaupes.remove(uid);
				GameOfTaupes.this.supertaupesteam.get(i).addPlayer(Bukkit.getOfflinePlayer(uid));
				GameOfTaupes.this.showedsupertaupes.add(uid);
				Bukkit.broadcastMessage(ChatColor.DARK_RED + Bukkit.getOfflinePlayer(uid).getName()
						+ " a revele qu'il etait une supertaupe !");
			}
		}

		for (Player online : Bukkit.getOnlinePlayers()) {
			online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
			online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
		}

		GameOfTaupes.this.supertaupessetup = true;

		unregisterTeam();
		unregisterTaupeTeam();
		unregisterHunterTeam();

		if (check) {
			checkVictory();
		}
	}

	
	public void claimKit(Player player) 
	{
		Random random = new Random();
		int kitnumber;
		int taupeteam = 0;

		for (int i = 0; i < GameOfTaupes.this.getConfig().getInt("options.taupesteams"); i++) {
			if (GameOfTaupes.this.taupes.get(i).contains(player.getUniqueId())) {
				taupeteam = i;
				break;
			}
		}

		while (true) {
			kitnumber = random.nextInt(8);
			if (!GameOfTaupes.this.claimedkits.containsKey(taupeteam)) {
				GameOfTaupes.this.claimedkits.put(taupeteam, new ArrayList<Integer>());
			}
			if (!GameOfTaupes.this.claimedkits.get(taupeteam).contains(kitnumber)) {
				GameOfTaupes.this.claimedkits.get(taupeteam).add(kitnumber);
				break;
			}
		}
		ItemStack kit = new ItemStack(Material.GOLDEN_APPLE, 4);
		Location loc = player.getLocation();
		loc.add(player.getEyeLocation().getDirection().normalize());
		switch (kitnumber) {
		case (0):
			kit.setAmount(3);
			kit.setType(Material.TNT);
			player.getWorld().dropItemNaturally(loc, kit);
			kit.setAmount(1);
			kit.setType(Material.FLINT_AND_STEEL);
			player.getWorld().dropItemNaturally(loc, kit);
			break;
		case (1):
			kit.setAmount(3);
			kit.setType(Material.MONSTER_EGG);
			kit.setDurability((short) 61);
			player.getWorld().dropItemNaturally(loc, kit);
			break;
		case (2):
			kit.setAmount(32);
			kit.setType(Material.ARROW);
			player.getWorld().dropItemNaturally(loc, kit);
			kit.setAmount(1);
			kit.setType(Material.BOW);
			kit.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
			player.getWorld().dropItemNaturally(loc, kit);
			break;
		case (3):
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
		case (4):
			kit.setAmount(1);
			kit.setType(Material.DIAMOND_PICKAXE);
			kit.addEnchantment(Enchantment.DIG_SPEED, 1);
			kit.addEnchantment(Enchantment.DURABILITY, 1);
			player.getWorld().dropItemNaturally(loc, kit);
			break;
		case (5):
			kit.setAmount(1);
			kit.setType(Material.DIAMOND_CHESTPLATE);
			kit.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
			player.getWorld().dropItemNaturally(loc, kit);
			break;
		case (6):
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

	

	public void claimPower(Player player, int hteam, int role)
	{
		switch(role)
		{
		case 0:
			
			break;
		case 1:
			
			break;
		case 2:
			
			break;
		case 3:
			
			break;
		}
	}

}
