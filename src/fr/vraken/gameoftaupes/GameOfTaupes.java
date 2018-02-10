package fr.vraken.gameoftaupes;

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
import org.bukkit.enchantments.Enchantment;

public class GameOfTaupes extends JavaPlugin {
	// Files
	FilesManager filesManager;
	BossManager bossManager;
	FileConfiguration teamf;
	FileConfiguration bossf;
	FileConfiguration deathf;
	FileConfiguration minigamef;

	// Save
	SaveManager saveManager;
	LoadManager loadManager;

	// Players
	ArrayList<UUID> playersInTeam = new ArrayList<UUID>();
	ArrayList<UUID> playersAlive = new ArrayList<UUID>();
	ArrayList<UUID> playersInLobby = new ArrayList<UUID>();
	ArrayList<UUID> playersSpec = new ArrayList<UUID>();

	// Taupes
	boolean taupessetup;
	HashMap<Integer, Integer> taupesperteam = new HashMap<Integer, Integer>();
	HashMap<Integer, Team> taupesteam = new HashMap<Integer, Team>();
	// HashMap<Integer, Integer> taupesTeamsPlayersNb = new HashMap<Integer,
	// Integer>();
	HashMap<Integer, ArrayList<UUID>> taupes = new HashMap<Integer, ArrayList<UUID>>();
	HashMap<Integer, Boolean> isTaupesTeamDead = new HashMap<Integer, Boolean>();
	ArrayList<UUID> aliveTaupes = new ArrayList<UUID>();
	ArrayList<UUID> showedtaupes = new ArrayList<UUID>();
	ArrayList<UUID> claimedtaupes = new ArrayList<UUID>();
	HashMap<Integer, ArrayList<Integer>> claimedkits = new HashMap<Integer, ArrayList<Integer>>();

	// Supertaupes
	boolean supertaupessetup;
	HashMap<Integer, Team> supertaupesteam = new HashMap<Integer, Team>();
	HashMap<Integer, UUID> supertaupes = new HashMap<Integer, UUID>();
	HashMap<Integer, Boolean> isSupertaupeDead = new HashMap<Integer, Boolean>();
	ArrayList<UUID> aliveSupertaupes = new ArrayList<UUID>();
	ArrayList<UUID> showedsupertaupes = new ArrayList<UUID>();

	// Chest
	ArrayList<Integer> kits = new ArrayList<Integer>();
	Location chestLocation;
	Location redstoneLocation;
	int chestLvl;
	int chestMinute;
	static ArrayList<ItemStack> tmpKits = new ArrayList<ItemStack>();
	boolean finalZone = false;

	// Scoreboard
	int episode;
	int minute;
	boolean retract = false;
	boolean finalretract = false;
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
	int tmpTeams;
	int tmpBorder;
	NumberFormat objFormatter;
	int height = 10;
	int minuteTot = 0;

	// Gamestates
	boolean meetUp = false;
	boolean gameStarted = false;
	boolean gameEnd = false;
	Location lobbyLocation;
	Location meetupLocation;
	Location respawnLocation;

	// Teams
	Location l1;
	Location l2;
	Location l3;
	Location l4;
	Location l5;
	Location l6;
	Location meetupl1;
	Location meetupl2;
	Location meetupl3;
	Location meetupl4;
	Location meetupl5;
	Location meetupl6;
	Team rose;
	Team jaune;
	Team violette;
	Team cyan;
	Team verte;
	Team grise;

	// Announcements
	int revealEpisode;
	int superrevealEpisode;
	int restractEpisode;
	String teamAnnounceString = "L'equipe ";
	String teamChoiceString = "son equipe";

	/*
	 * //Duels UUID provoker; UUID provoked; Location duelSpawn1; Location
	 * duelSpawn2; boolean duelInProgress = false;
	 */

	// Boss
	ArrayList<Integer> bossLoc = new ArrayList<Integer>();

	// Location Reveal
	ArrayList<String> teamReveal = new ArrayList<String>();

	public void onEnable() {
		System.out.println("+-------------VrakenGameOfTaupes--------------+");
		System.out.println("|           Plugin cree par Vraken            |");
		System.out.println("+---------------------------------------------+");
		try {
			filesManager = new FilesManager(this);
		} catch (IOException | InvalidConfigurationException e) {
		}

		teamf = filesManager.getTeamConfig();
		bossf = filesManager.getBossConfig();
		deathf = filesManager.getDeathConfig();
		minigamef = filesManager.getMinigameConfig();

		try {
			saveManager = new SaveManager(this);
		} catch (IOException e) {
		}

		try {
			loadManager = new LoadManager(this);
		} catch (IOException e) {
		}

		this.sm = Bukkit.getScoreboardManager();
		this.s = this.sm.getMainScoreboard();
		if (this.s.getObjective("GameOfTaupes") != null) {
			this.s.getObjective("GameOfTaupes").unregister();
		}

		this.revealEpisode = getConfig().getInt("options.forcereveal") / 20;
		this.superrevealEpisode = getConfig().getInt("options.superreveal") / 20;

		getConfig().options().copyDefaults(true);
		teamf.options().copyDefaults(true);
		bossf.options().copyDefaults(true);
		deathf.options().copyDefaults(true);
		saveConfig();

		Bukkit.createWorld(new WorldCreator(getConfig().getString("lobby.world")));
		Bukkit.createWorld(new WorldCreator(getConfig().getString("world")));

		if (bossf.getBoolean("boss.active")) {
			bossManager = new BossManager(this);
			Bukkit.getPluginManager().registerEvents(new BossEvents(this, bossManager), this);
		}
		Bukkit.getPluginManager().registerEvents(new EventsClass(this), this);

		this.obj = this.s.registerNewObjective("GameOfTaupes", "dummy");
		this.obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		for (Team team : this.s.getTeams()) {
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
		this.grise = this.s.registerNewTeam(teamf.getString("grise.name"));
		this.grise.setPrefix(ChatColor.GRAY.toString());
		this.grise.setSuffix(ChatColor.WHITE.toString());

		for (int i = 0; i < getConfig().getInt("options.taupesteams"); i++) {
			this.taupesteam.put(i, this.s.registerNewTeam("Taupes #" + i));
			this.taupesteam.get(i).setPrefix(ChatColor.RED.toString());
			this.taupesteam.get(i).setSuffix(ChatColor.WHITE.toString());

			this.supertaupesteam.put(i, this.s.registerNewTeam("SuperTaupe #" + i));
			this.supertaupesteam.get(i).setPrefix(ChatColor.DARK_RED.toString());
			this.supertaupesteam.get(i).setSuffix(ChatColor.WHITE.toString());

			this.taupes.put(i, new ArrayList<UUID>());
			this.supertaupes.put(i, null);
			this.taupesperteam.put(i, 0);
			this.isTaupesTeamDead.put(i, false);
			this.isSupertaupeDead.put(i, false);
		}

		if (this.s.getObjective("Vie") == null) {
			this.vie = this.s.registerNewObjective("Vie", "health");
			this.vie.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		}

		this.lobbyLocation = new Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()),
				this.getConfig().getInt("lobby.X"), this.getConfig().getInt("lobby.Y"),
				this.getConfig().getInt("lobby.Z"));

		this.meetupLocation = new Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()),
				this.getConfig().getInt("lobby.meetupX"), this.getConfig().getInt("lobby.meetupY"),
				this.getConfig().getInt("lobby.meetupZ"));

		this.respawnLocation = new Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()),
				this.getConfig().getInt("lobby.respawnX"), this.getConfig().getInt("lobby.respawnY"),
				this.getConfig().getInt("lobby.respawnZ"));

		setSpawnLocations();
		// setDuelSpawnLocations();

		super.onEnable();
	}

	public void startGame() {
		this.gameStarted = true;
		this.gameState = 0;
		this.hasChangedGS = false;
		this.chestLvl = 1;
		this.taupessetup = false;
		this.supertaupessetup = false;
		this.tmpBorder = this.getConfig().getInt("worldborder.size");

		String world = getConfig().getString("world");
		Boolean istimecycle = getConfig().getBoolean("options.timecycle");
		Bukkit.getWorld(world).setGameRuleValue("doDaylightCycle", Boolean.valueOf(istimecycle).toString());
		Bukkit.getWorld(getConfig().getString("world")).setStorm(false);
		Bukkit.getWorld(getConfig().getString("world")).setThundering(false);
		Bukkit.getWorld(getConfig().get("world").toString()).setTime(5000L);

		clearTeams();

		int nbTeams = this.s.getTeams().size() - getConfig().getInt("options.taupesteams");
		if (getConfig().getBoolean("options.supertaupe")) {
			nbTeams -= getConfig().getInt("options.taupesteams");
		}
		int taupesTot = nbTeams * getConfig().getInt("options.taupesperteam");
		int taupesMin = taupesTot / getConfig().getInt("options.taupesteams");
		int taupesLeft = taupesTot % getConfig().getInt("options.taupesteams");

		for (int i = 0; i < getConfig().getInt("options.taupesperteam"); i++) {
			if (taupesLeft > 0) {
				this.taupesperteam.put(i, taupesMin);
				taupesLeft--;
				continue;
			}

			this.taupesperteam.put(i, taupesMin);
		}

		this.episode += 1;

		// SCOREBOARD INITIALIZATION
		// -------------------------
		this.objFormatter = new DecimalFormat("00");
		initScoreboard(this.gameState);

		// SPAWNING CHEST
		// --------------
		this.chestLocation = new Location(Bukkit.getWorld(getConfig().get("world").toString()),
				getConfig().getInt("chest.X"), getConfig().getInt("chest.Y"), getConfig().getInt("chest.Z"));
		this.redstoneLocation = new Location(Bukkit.getWorld(getConfig().get("world").toString()),
				getConfig().getInt("chest.X"), getConfig().getInt("chest.Y") - 5, getConfig().getInt("chest.Z"));
		this.chestMinute = 0;

		// TAUPES SETTING
		// --------------
		setTaupes();

		// SUPERTAUPE SETTING
		// ------------------
		setSuperTaupe();

		// CLEARING INVENTORY AND STATUS OF EVERY PLAYER THEN TELEPORTING HIM TO HIS
		// SPAWN
		// -------------------------------------------------------------------------------
		clearPlayers();

		// RUNNABLE TASKS DURING ALL GAME
		// ------------------------------
		this.runnable = new BukkitRunnable() {
			int minutes = 20;
			int seconds = 0;

			public void run() {
				// TESTING IF BOSS HAS DESPAWNED
				// -----------------------------
				// TODO testIfBossDespawn();

				GameOfTaupes.this.minute = minutes;

				// SCOREBOARD RESET AT EVERY SECOND
				// --------------------------------
				NumberFormat formatter = new DecimalFormat("00");
				String minute = formatter.format(this.minutes);
				String second = formatter.format(this.seconds);
				GameOfTaupes.this.s.resetScores(minute + ":" + second);
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + "Episode " + GameOfTaupes.this.episode);
				GameOfTaupes.this.s
						.resetScores("" + ChatColor.WHITE + GameOfTaupes.this.tmpPlayers + ChatColor.GRAY + " joueurs");
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + "Border : " + tmpBorder + " x " + tmpBorder);
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);

				// UPDATE COMPASS TARGET
				// ---------------------
				if (GameOfTaupes.this.bossf.getBoolean("boss.active")) {
					updateCompassTarget();
				}

				if (this.seconds == 0) {
					// CHEST SPAWN
					// -----------
					if (!GameOfTaupes.this.getConfig().getBoolean("chest.random") && GameOfTaupes.this.taupessetup
							&& this.minutes == 15 - chestMinute) {
						spawnChest();
					}

					// SPAWNING MINIBOSS
					// -----------------
					if (GameOfTaupes.this.bossf.getBoolean("boss.active")) {
						if (this.minutes == 15 && GameOfTaupes.this.episode == 1) {
							GameOfTaupes.this.bossManager.activateShrine(1);
						}
						if (this.minutes == 5 && GameOfTaupes.this.episode == 1) {
							GameOfTaupes.this.bossManager.activateShrine(2);
						}
						if (this.minutes == 15 && GameOfTaupes.this.episode == 2) {
							GameOfTaupes.this.bossManager.activateShrine(3);
						}
						if (this.minutes == 5 && GameOfTaupes.this.episode == 2) {
							GameOfTaupes.this.bossManager.activateShrine(4);
						}
						if (this.minutes == 15 && GameOfTaupes.this.episode == 3) {
							GameOfTaupes.this.bossManager.activateShrine(5);
						}
						if (this.minutes == 5 && GameOfTaupes.this.episode == 3) {
							GameOfTaupes.this.bossManager.activateShrine(6);
						}
					}

					// EPISODE CHANGE ANNOUNCEMENT AT BEGINNING
					// ----------------------------------------
					if (this.minutes == 0) {
						if (GameOfTaupes.this.taupessetup) {
							Random rand = new Random();
							chestMinute = rand.nextInt(11);
						}

						GameOfTaupes.this.episode += 1;
						Bukkit.broadcastMessage(ChatColor.AQUA + "------------- Episode " + GameOfTaupes.this.episode
								+ " -------------");

						this.seconds = 59;
						this.minutes = 19;
					} else {
						this.seconds = 59;
						this.minutes -= 1;
					}
				} else {
					/*
					 * if((this.minutes == 19|| this.minutes == 14 || this.minutes == 9 ||
					 * this.minutes == 4) && this.seconds == 59) { try {
					 * GameOfTaupes.this.saveManager.saveGameInfos(); } catch (IOException |
					 * InvalidConfigurationException e) {}
					 * 
					 * try { GameOfTaupes.this.saveManager.savePlayersInfos(); } catch (IOException
					 * | InvalidConfigurationException e) {}
					 * 
					 * // The world to copy World source = Bukkit.getWorld("world"); File
					 * sourceFolder = source.getWorldFolder();
					 * 
					 * // The world to overwrite when copying File savePath = new
					 * File(GameOfTaupes.this.getDataFolder(), "world-save");
					 * 
					 * try { GameOfTaupes.this.saveManager.copyMapFolder(sourceFolder, savePath); }
					 * catch (IOException e) {} }
					 */

					this.seconds -= 1;
				}

				// WRITING SCOREBOARD
				// ------------------
				writeScoreboard(this.minutes, this.seconds);
			}
		}.runTaskTimer(this, 0L, 20L);

		// REVEALING A PLAYER'S LOCATION
		// -----------------------------
		new BukkitRunnable() {
			public void run() {
				RevealPlayerLocation(false);
			}
		}.runTaskTimer(this, 6000, 12000);

		getServer().getWorld(getConfig().getString("world")).getWorldBorder()
				.setSize(getConfig().getDouble("worldborder.size"));

		// SPAWN CHEST
		// -----------
		if (this.getConfig().getBoolean("chest.random")) {
			new BukkitRunnable() {
				public void run() {
					if (GameOfTaupes.this.finalZone) {
						this.cancel();
					}

					Random rdm = new Random();
					int x = rdm.nextInt(GameOfTaupes.this.tmpBorder) - GameOfTaupes.this.tmpBorder / 2;
					int z = rdm.nextInt(GameOfTaupes.this.tmpBorder) - GameOfTaupes.this.tmpBorder / 2;
					int y = Bukkit.getWorld(GameOfTaupes.this.getConfig().getString("world")).getHighestBlockYAt(
							new Location(Bukkit.getWorld(GameOfTaupes.this.getConfig().getString("world")), x, 0, z));
					GameOfTaupes.this.chestLocation.setX(x);
					GameOfTaupes.this.chestLocation.setY(y);
					GameOfTaupes.this.chestLocation.setZ(z);
					GameOfTaupes.this.redstoneLocation.setX(x);
					GameOfTaupes.this.redstoneLocation.setY(120);
					GameOfTaupes.this.redstoneLocation.setZ(z);
					GameOfTaupes.this.chestLvl = (GameOfTaupes.this.episode > 3) ? 3 : GameOfTaupes.this.episode;

					spawnChest();
				}

			}.runTaskTimer(this, 6000, 1200 * this.getConfig().getInt("chest.timer"));

		}

		// PVP ENABLE
		// ----------
		new BukkitRunnable() {
			public void run() {
				EventsClass.pvp = true;
				Bukkit.broadcastMessage(ChatColor.RED + "Le pvp est maintenant actif !");

				// Updating scoreboard status
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
				GameOfTaupes.this.gameState++;
				GameOfTaupes.this.objMinute = objFormatter
						.format(GameOfTaupes.this.getConfig().getInt("options.settaupesafter")
								- GameOfTaupes.this.getConfig().getInt("options.pvptime") - 1);
				GameOfTaupes.this.objSecond = "59";
				GameOfTaupes.this.objTxt = "Taupes : ";
				GameOfTaupes.this.hasChangedGS = true;
				GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":"
						+ GameOfTaupes.this.objSecond;
			}
		}.runTaskLater(this, 1200 * getConfig().getInt("options.pvptime"));

		// TAUPES ANNOUNCEMENT
		// -------------------
		new BukkitRunnable() {
			public void run() {
				taupeAnnouncement();

				// Updating scoreboard status
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
				GameOfTaupes.this.gameState++;
				GameOfTaupes.this.objMinute = objFormatter
						.format(GameOfTaupes.this.getConfig().getInt("options.setsupertaupesafter")
								- GameOfTaupes.this.getConfig().getInt("options.settaupesafter") - 1);
				GameOfTaupes.this.objSecond = "59";
				GameOfTaupes.this.objTxt = "Supertaupe : ";
				GameOfTaupes.this.hasChangedGS = true;
				GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":"
						+ GameOfTaupes.this.objSecond;
			}
		}.runTaskLater(this, 1200 * getConfig().getInt("options.settaupesafter"));

		// SUPERTAUPE ANNOUNCEMENT
		// -------------------
		new BukkitRunnable() {
			public void run() {
				supertaupeAnnouncement();

				// Updating scoreboard status
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
				GameOfTaupes.this.gameState++;
				GameOfTaupes.this.objMinute = objFormatter
						.format(GameOfTaupes.this.getConfig().getInt("worldborder.retractafter")
								- GameOfTaupes.this.getConfig().getInt("options.setsupertaupesafter") - 1);
				GameOfTaupes.this.objSecond = "59";
				GameOfTaupes.this.objTxt = "World border : ";
				GameOfTaupes.this.hasChangedGS = true;
				GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":"
						+ GameOfTaupes.this.objSecond;
			}
		}.runTaskLater(this, 1200 * getConfig().getInt("options.setsupertaupesafter"));

		// TAUPES REVEAL
		// -------------
		new BukkitRunnable() {
			public void run() {
				forceReveal(true);

				// Updating scoreboard status
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
				GameOfTaupes.this.gameState++;
				if (!GameOfTaupes.this.getConfig().getBoolean("options.supertaupe")) {
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
		new BukkitRunnable() {
			public void run() {
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

		// WORLDBORDER SHRINK
		// ------------------
		new BukkitRunnable() {
			public void run() {
				// UPDATING SCOREBOARD STATUS
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
				GameOfTaupes.this.gameState++;
				GameOfTaupes.this.objMinute = objFormatter
						.format(GameOfTaupes.this.getConfig().getInt("options.forcereveal")
								- GameOfTaupes.this.getConfig().getInt("worldborder.retractafter") - 1);
				GameOfTaupes.this.objSecond = "59";
				GameOfTaupes.this.objTxt = "Taupes reveal : ";
				GameOfTaupes.this.hasChangedGS = true;
				GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":"
						+ GameOfTaupes.this.objSecond;

				GameOfTaupes.this.retract = true;

				Bukkit.broadcastMessage("La carte est en train de r�tr�cir ! " + "D�p�chez-vous d'aller entre -"
						+ (int) (getConfig().getDouble("worldborder.finalsize") / 2) + " et "
						+ (int) (getConfig().getDouble("worldborder.finalsize") / 2) + " ! ");

				getServer().getWorld(getConfig().getString("world")).getWorldBorder().setSize(
						getConfig().getDouble("worldborder.finalsize"),
						1200 * getConfig().getInt("worldborder.episodestorestract"));
			}
		}.runTaskLater(this, 1200 * getConfig().getInt("worldborder.retractafter"));

		// FINAL SHRINK
		// ------------
		new BukkitRunnable() {
			public void run() {
				// UPDATING SCOREBOARD STATUS
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
				GameOfTaupes.this.gameState++;

				Bukkit.broadcastMessage(
						"R�tr�cissement final de la carte ! Il ne restera bient�t aucun endroit o� se cacher !");

				GameOfTaupes.this.finalZone = true;

				getServer().getWorld(getConfig().getString("world")).getWorldBorder().setSize(1, 60 * 10);
			}
		}.runTaskLater(this, 1200 * getConfig().getInt("worldborder.finalretract"));

		// FINAL FINAL SHRINK
		// ------------------
		new BukkitRunnable() {
			public void run() {
				new BukkitRunnable() {
					public void run() {
						Block block = Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(0,
								GameOfTaupes.this.height, 0);
						block.setType(Material.STATIONARY_WATER);
						++GameOfTaupes.this.height;
					}

				}.runTaskTimer(GameOfTaupes.this, 0, 40);

			}
		}.runTaskLater(this, 1200 * (getConfig().getInt("worldborder.finalretract") + 20));
	}

	public void stopGame() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!GameOfTaupes.this.playersInLobby.contains(p.getUniqueId())) {
				GameOfTaupes.this.playersInLobby.add(p.getUniqueId());

				p.setGameMode(GameMode.ADVENTURE);
				p.teleport(lobbyLocation);
			}

			p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		}

		GameOfTaupes.this.gameEnd = true;
	}

	public void loadGame() throws FileNotFoundException, IOException, InvalidConfigurationException {
		String world = getConfig().getString("world");
		Boolean istimecycle = getConfig().getBoolean("options.timecycle");
		Bukkit.getWorld(world).setGameRuleValue("doDaylightCycle", Boolean.valueOf(istimecycle).toString());
		Bukkit.getWorld(getConfig().getString("world")).setStorm(false);
		Bukkit.getWorld(getConfig().getString("world")).setThundering(false);
		Bukkit.getWorld(getConfig().get("world").toString()).setTime(5000L);

		this.loadManager.loadPlayerInfos();
		this.loadManager.loadGameInfos();

		unregisterTeam();
		unregisterTaupeTeam();

		getServer().getWorld(getConfig().getString("world")).getWorldBorder().setSize(this.tmpBorder);

		// SCOREBOARD INITIALIZATION
		// -------------------------
		this.objFormatter = new DecimalFormat("00");
		initScoreboard(this.gameState);

		// SPAWNING CHEST
		// --------------
		this.chestLocation = new Location(Bukkit.getWorld(getConfig().get("world").toString()),
				getConfig().getInt("chest.X"), getConfig().getInt("chest.Y"), getConfig().getInt("chest.Z"));
		this.redstoneLocation = new Location(Bukkit.getWorld(getConfig().get("world").toString()),
				getConfig().getInt("chest.X"), getConfig().getInt("chest.Y") - 5, getConfig().getInt("chest.Z"));
		this.chestMinute = 0;

		this.minuteTot = this.episode * 20 - this.minute;
		int minuteToReveal = (this.minuteTot - 5) % 10;
		int minuteToChest = (this.minuteTot - 5) % this.getConfig().getInt("chest.timer");

		// REVEALING A PLAYER'S LOCATION
		// -----------------------------
		new BukkitRunnable() {
			public void run() {
				RevealPlayerLocation(false);
			}
		}.runTaskTimer(this, minuteToReveal * 1200, 12000);

		// SPAWN CHEST
		// -----------
		if (this.getConfig().getBoolean("chest.random")) {
			new BukkitRunnable() {
				public void run() {
					if (GameOfTaupes.this.finalZone) {
						this.cancel();
					}

					Random rdm = new Random();
					int x = rdm.nextInt(GameOfTaupes.this.tmpBorder) - GameOfTaupes.this.tmpBorder / 2;
					int z = rdm.nextInt(GameOfTaupes.this.tmpBorder) - GameOfTaupes.this.tmpBorder / 2;
					int y = Bukkit.getWorld(GameOfTaupes.this.getConfig().getString("world")).getHighestBlockYAt(
							new Location(Bukkit.getWorld(GameOfTaupes.this.getConfig().getString("world")), x, 0, z));
					GameOfTaupes.this.chestLocation.setX(x);
					GameOfTaupes.this.chestLocation.setY(y);
					GameOfTaupes.this.chestLocation.setZ(z);
					GameOfTaupes.this.redstoneLocation.setX(x);
					GameOfTaupes.this.redstoneLocation.setY(120);
					GameOfTaupes.this.redstoneLocation.setZ(z);
					GameOfTaupes.this.chestLvl = (GameOfTaupes.this.episode > 3) ? 3 : GameOfTaupes.this.episode;

					spawnChest();
				}

			}.runTaskTimer(this, minuteToChest * 1200, 1200 * this.getConfig().getInt("chest.timer"));
		}

		if (this.minuteTot < getConfig().getInt("options.pvptime")) {
			// PVP ENABLE
			// ----------
			new BukkitRunnable() {
				public void run() {
					EventsClass.pvp = true;
					Bukkit.broadcastMessage(ChatColor.RED + "Le pvp est maintenant actif !");

					// Updating scoreboard status
					GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
					GameOfTaupes.this.gameState++;
					GameOfTaupes.this.objMinute = objFormatter
							.format(GameOfTaupes.this.getConfig().getInt("options.settaupesafter")
									- GameOfTaupes.this.getConfig().getInt("options.pvptime") - 1);
					GameOfTaupes.this.objSecond = "59";
					GameOfTaupes.this.objTxt = "Taupes : ";
					GameOfTaupes.this.hasChangedGS = true;
					GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":"
							+ GameOfTaupes.this.objSecond;
				}
			}.runTaskLater(this, 1200 * (getConfig().getInt("options.pvptime") - this.minuteTot));

		} else {
			EventsClass.pvp = false;
		}

		if (this.minuteTot < getConfig().getInt("options.settaupesafter")) {
			// TAUPES ANNOUNCEMENT
			// -------------------
			new BukkitRunnable() {
				public void run() {
					taupeAnnouncement();

					// Updating scoreboard status
					GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
					GameOfTaupes.this.gameState++;
					GameOfTaupes.this.objMinute = objFormatter
							.format(GameOfTaupes.this.getConfig().getInt("options.setsupertaupesafter")
									- GameOfTaupes.this.getConfig().getInt("options.settaupesafter") - 1);
					GameOfTaupes.this.objSecond = "59";
					GameOfTaupes.this.objTxt = "Supertaupe : ";
					GameOfTaupes.this.hasChangedGS = true;
					GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":"
							+ GameOfTaupes.this.objSecond;
				}
			}.runTaskLater(this, 1200 * (getConfig().getInt("options.settaupesafter") - this.minuteTot));

		}

		if (this.minuteTot < getConfig().getInt("options.setsupertaupesafter")) {
			// SUPERTAUPES ANNOUNCEMENT
			// -------------------
			new BukkitRunnable() {
				public void run() {
					supertaupeAnnouncement();

					// Updating scoreboard status
					GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
					GameOfTaupes.this.gameState++;
					GameOfTaupes.this.objMinute = objFormatter
							.format(GameOfTaupes.this.getConfig().getInt("worldborder.retractafter")
									- GameOfTaupes.this.getConfig().getInt("options.setsupertaupesafter") - 1);
					GameOfTaupes.this.objSecond = "59";
					GameOfTaupes.this.objTxt = "World border : ";
					GameOfTaupes.this.hasChangedGS = true;
					GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":"
							+ GameOfTaupes.this.objSecond;
				}
			}.runTaskLater(this, 1200 * (getConfig().getInt("options.setsupertaupesafter") - this.minuteTot));

		}

		if (this.minuteTot < getConfig().getInt("options.forcereveal")) {
			// TAUPES REVEAL
			// -------------
			new BukkitRunnable() {
				public void run() {
					forceReveal(true);

					// Updating scoreboard status
					GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
					GameOfTaupes.this.gameState++;
					if (!GameOfTaupes.this.getConfig().getBoolean("options.supertaupe")) {
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
			}.runTaskLater(this, 1200 * (getConfig().getInt("options.forcereveal") - this.minuteTot));

		}

		if (this.minuteTot < getConfig().getInt("options.superreveal")) {
			// SUPERTAUPE REVEAL
			// ------------
			new BukkitRunnable() {
				public void run() {
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
			}.runTaskLater(this, 1200 * (getConfig().getInt("options.superreveal") - this.minuteTot));

		}

		if (this.minuteTot < getConfig().getInt("worldborder.retractafter")) {
			// WORLDBORDER SHRINK
			// ------------------
			new BukkitRunnable() {
				public void run() {
					// UPDATING SCOREBOARD STATUS
					GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
					GameOfTaupes.this.gameState++;
					GameOfTaupes.this.objMinute = objFormatter
							.format(GameOfTaupes.this.getConfig().getInt("options.forcereveal")
									- GameOfTaupes.this.getConfig().getInt("worldborder.retractafter") - 1);
					GameOfTaupes.this.objSecond = "59";
					GameOfTaupes.this.objTxt = "Taupes reveal : ";
					GameOfTaupes.this.hasChangedGS = true;
					GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":"
							+ GameOfTaupes.this.objSecond;

					GameOfTaupes.this.retract = true;

					Bukkit.broadcastMessage("La carte est en train de rétrécir ! " + "Dépêchez-vous d'aller entre -"
							+ (int) (getConfig().getDouble("worldborder.finalsize") / 2) + " et "
							+ (int) (getConfig().getDouble("worldborder.finalsize") / 2) + " ! ");

					getServer().getWorld(getConfig().getString("world")).getWorldBorder().setSize(
							getConfig().getDouble("worldborder.finalsize"),
							1200 * getConfig().getInt("worldborder.episodestorestract"));
				}
			}.runTaskLater(this, 1200 * (getConfig().getInt("worldborder.retractafter") - this.minuteTot));

		}

		if (this.minuteTot < getConfig().getInt("worldborder.finalretract")) {
			// FINAL SHRINK
			// ------------
			new BukkitRunnable() {
				public void run() {
					// UPDATING SCOREBOARD STATUS
					GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);
					GameOfTaupes.this.gameState++;

					Bukkit.broadcastMessage(
							"Rétrécissement final de la carte ! Il ne restera bientôt aucun endroit où se cacher !");

					GameOfTaupes.this.finalZone = true;

					getServer().getWorld(getConfig().getString("world")).getWorldBorder().setSize(1, 60 * 10);
				}
			}.runTaskLater(this, 1200 * (getConfig().getInt("worldborder.finalretract") - this.minuteTot));

		}

		if (this.minuteTot < getConfig().getInt("worldborder.finalretract") + 20) {
			// FINAL FINAL SHRINK
			// ------------------
			new BukkitRunnable() {
				public void run() {
					new BukkitRunnable() {
						public void run() {
							Block block = Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(0,
									GameOfTaupes.this.height, 0);
							block.setType(Material.STATIONARY_WATER);
							++GameOfTaupes.this.height;
						}

					}.runTaskTimer(GameOfTaupes.this, 0, 40);

				}
			}.runTaskLater(this, 1200 * (getConfig().getInt("worldborder.finalretract") + 20 - this.minuteTot));
		}

		if (this.retract) {
			getServer().getWorld(getConfig().getString("world")).getWorldBorder().setSize(
					getConfig().getDouble("worldborder.finalsize"),
					60 * (20 * getConfig().getInt("worldborder.episodestorestract") - this.minuteTot
							+ getConfig().getInt("worldborder.retractafter")));

		}

		if (this.finalretract) {
			getServer().getWorld(getConfig().getString("world")).getWorldBorder().setSize(1,
					60 * (10 - this.minuteTot + getConfig().getInt("worldborder.finalretract")));

		}

		// RUNNABLE TASKS DURING ALL GAME
		// ------------------------------
		this.runnable = new BukkitRunnable() {
			int minutes = GameOfTaupes.this.minute;
			int seconds = 0;

			public void run() {
				// TESTING IF BOSS HAS DESPAWNED
				// -----------------------------
				// TODO testIfBossDespawn();

				GameOfTaupes.this.minute = minutes;

				// SCOREBOARD RESET AT EVERY SECOND
				// --------------------------------
				NumberFormat formatter = new DecimalFormat("00");
				String minute = formatter.format(this.minutes);
				String second = formatter.format(this.seconds);
				GameOfTaupes.this.s.resetScores(minute + ":" + second);
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + "Episode " + GameOfTaupes.this.episode);
				GameOfTaupes.this.s
						.resetScores("" + ChatColor.WHITE + GameOfTaupes.this.tmpPlayers + ChatColor.GRAY + " joueurs");
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + "Border : " + tmpBorder + " x " + tmpBorder);
				GameOfTaupes.this.s.resetScores(ChatColor.WHITE + GameOfTaupes.this.countdownObj);

				// UPDATE COMPASS TARGET
				// ---------------------
				if (GameOfTaupes.this.bossf.getBoolean("boss.active")) {
					updateCompassTarget();
				}

				if (this.seconds == 0) {
					// CHEST SPAWN
					// -----------
					if (!GameOfTaupes.this.getConfig().getBoolean("chest.random") && GameOfTaupes.this.taupessetup
							&& this.minutes == 15 - chestMinute) {
						spawnChest();
					}

					// SPAWNING MINIBOSS
					// -----------------
					if (GameOfTaupes.this.bossf.getBoolean("boss.active")) {
						if (this.minutes == 15 && GameOfTaupes.this.episode == 1) {
							GameOfTaupes.this.bossManager.activateShrine(1);
						}
						if (this.minutes == 5 && GameOfTaupes.this.episode == 1) {
							GameOfTaupes.this.bossManager.activateShrine(2);
						}
						if (this.minutes == 15 && GameOfTaupes.this.episode == 2) {
							GameOfTaupes.this.bossManager.activateShrine(3);
						}
						if (this.minutes == 5 && GameOfTaupes.this.episode == 2) {
							GameOfTaupes.this.bossManager.activateShrine(4);
						}
						if (this.minutes == 15 && GameOfTaupes.this.episode == 3) {
							GameOfTaupes.this.bossManager.activateShrine(5);
						}
						if (this.minutes == 5 && GameOfTaupes.this.episode == 3) {
							GameOfTaupes.this.bossManager.activateShrine(6);
						}
					}

					// EPISODE CHANGE ANNOUNCEMENT AT BEGINNING
					// ----------------------------------------
					if (this.minutes == 0) {
						if (GameOfTaupes.this.taupessetup) {
							Random rand = new Random();
							chestMinute = rand.nextInt(11);
						}

						GameOfTaupes.this.episode += 1;
						Bukkit.broadcastMessage(ChatColor.AQUA + "------------- Episode " + GameOfTaupes.this.episode
								+ " -------------");

						this.seconds = 59;
						this.minutes = 19;
					} else {
						this.seconds = 59;
						this.minutes -= 1;
					}
				} else {
					if ((this.minutes == 19 || this.minutes == 14 || this.minutes == 9 || this.minutes == 4)
							&& this.seconds == 59) {
						try {
							GameOfTaupes.this.saveManager.saveGameInfos();
						} catch (IOException | InvalidConfigurationException e) {
							System.out.println("Save game info failed");
						}

						try {
							GameOfTaupes.this.saveManager.savePlayersInfos();
						} catch (IOException | InvalidConfigurationException e) {
							System.out.println("Save players info failed");
						}

						// The world to copy
						World source = Bukkit.getWorld("world");
						File sourceFolder = source.getWorldFolder();

						// The world to overwrite when copying
						File savePath = new File(GameOfTaupes.this.getDataFolder(), "world-save");

						try {
							GameOfTaupes.this.saveManager.copyMapFolder(sourceFolder, savePath);
						} catch (IOException e) {
							System.out.println("Save map failed");
						}
					}

					this.seconds -= 1;
				}

				// WRITING SCOREBOARD
				// ------------------
				writeScoreboard(this.minutes, this.seconds);
			}
		}.runTaskTimer(this, 0L, 20L);
	}

	// PLAYER INGAME COMMANDS
	// ----------------------
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if ((sender instanceof Player)) {
			Player player = (Player) sender;
			String message;

			// TAUPES CHAT
			// -----------
			if (cmd.getName().equalsIgnoreCase("t") && this.taupessetup) {
				for (int i = 0; i < this.getConfig().getInt("options.taupesteams"); i++) {
					if (this.aliveTaupes.contains(player.getUniqueId())
							&& this.taupes.get(i).contains(player.getUniqueId())) {
						for (UUID taupe : this.taupes.get(i)) {
							if (GameOfTaupes.this.showedsupertaupes.contains(taupe)) {
								continue;
							}

							message = StringUtils.join(args, ' ', 0, args.length);

							String content = ChatColor.GOLD + "(Taupes #" + i + ") " + ChatColor.RED + "<"
									+ player.getName();

							if (!GameOfTaupes.this.s.getPlayerTeam(Bukkit.getOfflinePlayer(taupe)).getName()
									.contains("aupe")) {
								content += "(" + player.getScoreboard().getPlayerTeam(player).getName() + ")";
							}

							content += "> " + ChatColor.WHITE + message;

							Bukkit.getPlayer(taupe).sendMessage(content);
						}
						return true;
					}
				}
				player.sendMessage(ChatColor.RED + "Vous n'etes pas une taupe !");
				return true;
			}

			// TAUPES REVEAL
			// -------------
			if (cmd.getName().equalsIgnoreCase("reveal") && this.taupessetup) {
				for (int i = 0; i < this.getConfig().getInt("options.taupesteams"); i++) {
					if (this.taupes.get(i).contains(player.getUniqueId())) {
						if (this.showedtaupes.contains(player.getUniqueId())) {
							player.sendMessage(ChatColor.RED + "Vous vous etes deja revele !");
						} else {
							PlayerInventory inventory = player.getInventory();
							inventory.addItem(new ItemStack[] { new ItemStack(Material.GOLDEN_APPLE, 1) });

							this.taupesteam.get(i).addPlayer(player);
							this.showedtaupes.add(player.getUniqueId());
							for (Player online : Bukkit.getOnlinePlayers()) {
								online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
							}
							Bukkit.broadcastMessage(
									ChatColor.RED + player.getName() + " a revele qu'il etait une taupe !");

							unregisterTeam();
							unregisterTaupeTeam();
							checkVictory();
						}
						return true;
					}
				}

				player.sendMessage(ChatColor.RED + "Vous n'etes pas une taupe !");
				return true;
			}

			// SUPERTAUPE REVEAL
			// -----------------
			if (cmd.getName().equalsIgnoreCase("superreveal") && this.supertaupessetup) {
				if (this.supertaupes.containsValue(player.getUniqueId())) {
					int key = -1;

					for (int i = 0; i < this.getConfig().getInt("options.taupesteams"); i++) {
						if (this.supertaupes.get(i) == player.getUniqueId()) {
							key = i;
							break;
						}
					}

					if (this.showedsupertaupes.contains(player.getUniqueId())) {
						player.sendMessage(ChatColor.RED + "Vous vous etes deja revele !");
					} else if (!this.showedtaupes.contains(player.getUniqueId())) {
						player.sendMessage(ChatColor.RED + "Vous devez d'abord vous reveler en tant que taupe !");
					} else {
						PlayerInventory inventory = player.getInventory();
						inventory.addItem(new ItemStack[] { new ItemStack(Material.GOLDEN_APPLE, 2) });

						this.aliveTaupes.remove(player.getUniqueId());
						this.supertaupesteam.get(key).addPlayer((OfflinePlayer) player);
						this.showedsupertaupes.add(player.getUniqueId());
						for (Player online : Bukkit.getOnlinePlayers()) {
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

			// TAUPES CLAIM KIT
			// ----------------
			if (cmd.getName().equalsIgnoreCase("claim") && this.taupessetup) {
				for (int i = 0; i < this.getConfig().getInt("options.taupesteams"); i++) {
					if (this.taupes.get(i).contains(player.getUniqueId())) {
						if (!this.claimedtaupes.contains(player.getUniqueId())) {
							claimKit(player);
						} else {
							player.sendMessage(ChatColor.RED + "Vous avez deja claim votre kit de taupe !");
						}
						return true;
					}
				}
				player.sendMessage(ChatColor.RED + "Vous n'etes pas une taupe !");
				return true;
			}

			// ADMIN MEETUP
			// ------------
			if (cmd.getName().equalsIgnoreCase("gotmeetup") && player.isOp() && !this.gameStarted) {
				this.meetUp = true;
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.teleport(this.meetupLocation);
				}
				return true;
			}

			// ADMIN START
			// -----------
			if (cmd.getName().equalsIgnoreCase("gotstart") && player.isOp() && !this.gameStarted) {
				startGame();
				return true;
			}

			// ADMIN STOP
			// ----------
			if (cmd.getName().equalsIgnoreCase("gotstop") && player.isOp() && this.gameStarted) {
				stopGame();
				return true;
			}

			// ADMIN LOAD
			// ----------
			if (cmd.getName().equalsIgnoreCase("gotload") && player.isOp()) {
				try {
					loadGame();
				} catch (Exception e) {
				}
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
	public void setSpawnLocations() {
		this.l1 = new Location(Bukkit.getWorld(getConfig().get("world").toString()), this.teamf.getInt("rose.X"),
				this.teamf.getInt("rose.Y"), this.teamf.getInt("rose.Z"));
		this.l2 = new Location(Bukkit.getWorld(getConfig().get("world").toString()), this.teamf.getInt("cyan.X"),
				this.teamf.getInt("cyan.Y"), this.teamf.getInt("cyan.Z"));
		this.l3 = new Location(Bukkit.getWorld(getConfig().get("world").toString()), this.teamf.getInt("jaune.X"),
				this.teamf.getInt("jaune.Y"), this.teamf.getInt("jaune.Z"));
		this.l4 = new Location(Bukkit.getWorld(getConfig().get("world").toString()), this.teamf.getInt("violette.X"),
				this.teamf.getInt("violette.Y"), this.teamf.getInt("violette.Z"));
		this.l5 = new Location(Bukkit.getWorld(getConfig().get("world").toString()), this.teamf.getInt("verte.X"),
				this.teamf.getInt("verte.Y"), this.teamf.getInt("verte.Z"));
		this.l6 = new Location(Bukkit.getWorld(getConfig().get("world").toString()), this.teamf.getInt("grise.X"),
				this.teamf.getInt("grise.Y"), this.teamf.getInt("grise.Z"));

		if (this.getConfig().getBoolean("options.meetupteamtp")) {
			this.meetupl1 = new Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()),
					this.teamf.getInt("rose.meetupX"), this.teamf.getInt("rose.meetupY"),
					this.teamf.getInt("rose.meetupZ"));
			this.meetupl2 = new Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()),
					this.teamf.getInt("cyan.meetupX"), this.teamf.getInt("cyan.meetupY"),
					this.teamf.getInt("cyan.meetupZ"));
			this.meetupl3 = new Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()),
					this.teamf.getInt("jaune.meetupX"), this.teamf.getInt("jaune.meetupY"),
					this.teamf.getInt("jaune.meetupZ"));
			this.meetupl4 = new Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()),
					this.teamf.getInt("violette.meetupX"), this.teamf.getInt("violette.meetupY"),
					this.teamf.getInt("violette.meetupZ"));
			this.meetupl5 = new Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()),
					this.teamf.getInt("verte.meetupX"), this.teamf.getInt("verte.meetupY"),
					this.teamf.getInt("verte.meetupZ"));
			this.meetupl6 = new Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()),
					this.teamf.getInt("grise.meetupX"), this.teamf.getInt("grise.meetupY"),
					this.teamf.getInt("grise.meetupZ"));
		}

	}

	/*
	 * public void setDuelSpawnLocations() { this.duelSpawn1 = new
	 * Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()),
	 * this.getConfig().getInt("duelspawn1.X"),
	 * this.getConfig().getInt("duelspawn1.Y"),
	 * this.getConfig().getInt("duelspawn1.Z")); this.duelSpawn2 = new
	 * Location(Bukkit.getWorld(getConfig().get("lobby.world").toString()),
	 * this.getConfig().getInt("duelspawn2.X"),
	 * this.getConfig().getInt("duelspawn2.Y"),
	 * this.getConfig().getInt("duelspawn2.Z"));
	 * 
	 * }
	 */

	public void initScoreboard(int gs) {
		this.s.getObjective(this.obj.getDisplayName()).getScore(ChatColor.WHITE + "Episode " + this.episode)
				.setScore(0);

		this.s.getObjective(this.obj.getDisplayName())
				.getScore("" + ChatColor.WHITE + this.playersAlive.size() + ChatColor.GRAY + " joueurs").setScore(-1);

		this.tmpBorder = (int) getServer().getWorld(getConfig().getString("world")).getWorldBorder().getSize();
		this.s.getObjective(this.obj.getDisplayName())
				.getScore(ChatColor.WHITE + "Border : " + tmpBorder + " x " + tmpBorder).setScore(-3);

		NumberFormat objFormatter = new DecimalFormat("00");

		switch (gs) {
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
			this.objMinute = objFormatter.format(this.getConfig().getInt("worldborder.retractafter") - this.minuteTot);
			this.objSecond = "00";
			this.objTxt = "World border : ";
			this.countdownObj = this.objTxt + this.objMinute + ":" + this.objSecond;
			break;
		case 4:
			this.objMinute = objFormatter.format(this.getConfig().getInt("options.forcereveal") - this.minuteTot);
			this.objSecond = "00";
			this.objTxt = "Taupes reveal : ";
			this.countdownObj = this.objTxt + this.objMinute + ":" + this.objSecond;
			break;
		case 5:
			this.objMinute = objFormatter.format(this.getConfig().getInt("options.superreveal") - this.minuteTot);
			this.objSecond = "00";
			this.objTxt = "Supertaupes reveal : ";
			this.countdownObj = this.objTxt + this.objMinute + ":" + this.objSecond;
			break;
		case 6:
			this.objMinute = objFormatter.format(this.getConfig().getInt("worldborder.finalretract") - this.minuteTot);
			this.objSecond = "00";
			this.objTxt = "Final shrink : ";
			this.countdownObj = this.objTxt + this.objMinute + ":" + this.objSecond;
			break;
		}
	}

	public void clearPlayers() {
		GameOfTaupes.this.playersInLobby.clear();
		GameOfTaupes.this.playersSpec.clear();

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!this.playersInTeam.contains(p.getUniqueId())) {
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

			for (PotionEffect potion : p.getActivePotionEffects()) {
				p.removePotionEffect(potion.getType());
			}

			p.setGameMode(GameMode.SURVIVAL);
			p.setHealth(20.0D);
			p.setFoodLevel(40);

			if (this.s.getPlayerTeam(p).getName().equals(teamf.getString("rose.name"))) {
				p.teleport(this.l1);
			} else if (this.s.getPlayerTeam(p).getName().equals(teamf.getString("jaune.name"))) {
				p.teleport(this.l2);
			} else if (this.s.getPlayerTeam(p).getName().equals(teamf.getString("violette.name"))) {
				p.teleport(this.l3);
			} else if (this.s.getPlayerTeam(p).getName().equals(teamf.getString("cyan.name"))) {
				p.teleport(this.l4);
			} else if (this.s.getPlayerTeam(p).getName().equals(teamf.getString("verte.name"))) {
				p.teleport(this.l5);
			} else if (this.s.getPlayerTeam(p).getName().equals(teamf.getString("grise.name"))) {
				p.teleport(this.l6);
			}
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,
					20 * getConfig().getInt("options.nodamagetime"), 4));

			if (this.getConfig().getBoolean("options.haste")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0));
			}
		}
	}

	public void clearTeams() {
		for (Team teams : this.s.getTeams()) {
			if (!teams.getName().contains("Taupes")) {
				if (teams.getName().contains("SuperTaupe") && !getConfig().getBoolean("options.supertaupe")) {
					teams.unregister();
				} else if (teams.getSize() == 0 && !teams.getName().contains("SuperTaupe")) {
					teams.unregister();
				}
			}
		}
	}

	public void setTaupes() {
		ArrayList<UUID> players = new ArrayList<UUID>();
		ArrayList<UUID> taupes = new ArrayList<UUID>();
		ArrayList<Integer> teams = new ArrayList<Integer>();
		int psize;
		int tsize;
		UUID p;
		Random random = new Random(System.currentTimeMillis());
		for (Team team : this.s.getTeams()) {
			if (team.getPlayers().size() >= 1) {
				players.clear();
				taupes.clear();
				teams.clear();

				for (OfflinePlayer player : team.getPlayers()) {
					players.add(player.getUniqueId());
				}

				for (int i = 0; i < this.getConfig().getInt("options.taupesperteam"); i++) {
					while (true) {
						psize = random.nextInt(players.size());
						p = players.get(psize);
						if (!taupes.contains(p)) {
							break;
						}
					}
					while (true) {
						tsize = random.nextInt(this.getConfig().getInt("options.taupesteams"));
						if (!teams.contains(tsize)) {
							if (this.taupes.get(tsize).size() < this.taupesperteam.get(tsize)) {
								break;
							}
						}
					}

					teams.add(tsize);
					taupes.add(p);
					if (!this.taupes.containsKey(tsize)) {
						this.taupes.put(tsize, new ArrayList<UUID>());
					}
					this.taupes.get(tsize).add(p);
					this.aliveTaupes.add(p);
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

	public void checkVictory() {
		Team lastTeam = null;
		int teamsAlive = 0;
		for (Team team : GameOfTaupes.this.s.getTeams()) {
			if (!team.getName().contains("aupe")) {
				teamsAlive++;
				if (teamsAlive > 1) {
					return;
				}
				lastTeam = team;
			}
		}

		for (int i = 0; i < GameOfTaupes.this.taupes.size(); i++) {
			for (UUID uid : GameOfTaupes.this.taupes.get(i)) {
				if (GameOfTaupes.this.playersAlive.contains(uid) && GameOfTaupes.this.supertaupes.get(i) != uid) {
					teamsAlive++;
					if (teamsAlive > 1) {
						return;
					}
					lastTeam = GameOfTaupes.this.taupesteam.get(i);
					break;
				}
			}

			if (GameOfTaupes.this.aliveSupertaupes.contains(GameOfTaupes.this.supertaupes.get(i))) {
				teamsAlive++;
				if (teamsAlive > 1) {
					return;
				}
				lastTeam = GameOfTaupes.this.supertaupesteam.get(i);
			}
		}

		if (teamsAlive == 1 || teamsAlive == 0) {
			forceReveal(false);
			superReveal(false);
			announceWinner(lastTeam);
		}
	}

	public void announceWinner(Team team) {
		if (team == null) {
			Bukkit.broadcastMessage("Toutes les equipes ont ete eliminees, personne n'a gagne ! ");
			Bukkit.getScheduler().cancelAllTasks();
			return;
		}

		Bukkit.broadcastMessage(GameOfTaupes.this.teamAnnounceString + team.getPrefix() + team.getName()
				+ ChatColor.RESET + " a gagne ! ");

		Bukkit.getScheduler().cancelAllTasks();

		GameOfTaupes.this.playersAlive.clear();
	}

	public void unregisterTeam() {
		for (Team teams : GameOfTaupes.this.s.getTeams()) {
			// NORMAL TEAM UNREGISTRATION
			if (teams.getSize() == 0 && !teams.getName().contains("Taupes")
					&& !teams.getName().contains("SuperTaupe")) {
				Bukkit.broadcastMessage(GameOfTaupes.this.teamAnnounceString + teams.getPrefix() + teams.getName()
						+ ChatColor.RESET + " a ete eliminee ! ");
				teams.unregister();
			}
		}
	}

	public void unregisterTaupeTeam() {
		for (int i = 0; i < this.getConfig().getInt("options.taupesteams"); ++i) {
			UUID supertaupe = GameOfTaupes.this.supertaupes.get(i);

			boolean dead = true;
			int showed = 0;
			for (UUID uid : GameOfTaupes.this.taupes.get(i)) {
				if (GameOfTaupes.this.aliveTaupes.contains(uid)) {
					dead = false;
				}
				if (GameOfTaupes.this.showedtaupes.contains(uid)) {
					showed++;
				}
			}

			if (dead && !GameOfTaupes.this.isTaupesTeamDead.get(i)
					&& GameOfTaupes.this.taupes.get(i).size() == showed) {
				GameOfTaupes.this.isTaupesTeamDead.put(i, true);
				Bukkit.broadcastMessage(ChatColor.RED + "L'equipe des taupes #" + i + " a ete eliminee ! ");
				GameOfTaupes.this.taupesteam.get(i).unregister();
			}
			if (!GameOfTaupes.this.isSupertaupeDead.get(i) && GameOfTaupes.this.showedsupertaupes.contains(supertaupe)
					&& !GameOfTaupes.this.aliveSupertaupes.contains(supertaupe)
					&& GameOfTaupes.this.getConfig().getBoolean("options.supertaupe")) {
				GameOfTaupes.this.isSupertaupeDead.put(i, true);
				Bukkit.broadcastMessage(ChatColor.DARK_RED + "La supertaupe #" + i + " a ete eliminee ! ");
				GameOfTaupes.this.supertaupesteam.get(i).unregister();
			}
		}
	}

	public void spawnChest() {
		Bukkit.broadcastMessage(ChatColor.DARK_GREEN
				+ "Un tresor est apparu ! Allez vite le chercher avant que vos adversaires ne s'en emparent ! ");
		try {
			Bukkit.getPlayer("Spec")
					.performCommand("dmarker add chest icon:chest x:" + GameOfTaupes.this.chestLocation.getX() + " y:"
							+ GameOfTaupes.this.chestLocation.getY() + " z:" + GameOfTaupes.this.chestLocation.getZ()
							+ " world:" + GameOfTaupes.this.chestLocation.getWorld().getName());
		} catch (Exception ex) {
		}

		Block chestBlock = Bukkit.getWorld(getConfig().get("world").toString())
				.getBlockAt(GameOfTaupes.this.chestLocation);
		if (chestBlock.getType() != Material.TRAPPED_CHEST) {
			chestBlock.setType(Material.TRAPPED_CHEST);
		}
		/*
		 * Block redstoneBlock =
		 * Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.
		 * this.redstoneLocation); if(redstoneBlock.getType() !=
		 * Material.REDSTONE_BLOCK) { redstoneBlock.setType(Material.REDSTONE_BLOCK); }
		 */
		// Bukkit.broadcastMessage(chestBlock.getLocation().getX() + " / " +
		// chestBlock.getLocation().getZ());

		if (GameOfTaupes.this.getConfig().getBoolean("chest.random")) {
			Block redstoneBlock = Bukkit.getWorld(getConfig().get("world").toString())
					.getBlockAt(GameOfTaupes.this.redstoneLocation);
			int x = (int) redstoneLocation.getX();
			int y = (int) redstoneLocation.getY();
			int z = (int) redstoneLocation.getZ();

			redstoneBlock.setType(Material.REDSTONE_BLOCK);

			redstoneLocation.setY(y + 1);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setX(x + 1);
			redstoneLocation.setY(y + 1);
			redstoneLocation.setZ(z);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setX(x - 1);
			redstoneLocation.setY(y + 1);
			redstoneLocation.setZ(z);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setX(x);
			redstoneLocation.setY(y + 1);
			redstoneLocation.setZ(z + 1);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setX(x);
			redstoneLocation.setY(y + 1);
			redstoneLocation.setZ(z - 1);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);

			redstoneLocation.setX(x);
			redstoneLocation.setY(y + 2);
			redstoneLocation.setZ(z);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setX(x + 1);
			redstoneLocation.setY(y + 2);
			redstoneLocation.setZ(z);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setX(x - 1);
			redstoneLocation.setY(y + 2);
			redstoneLocation.setZ(z);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setX(x);
			redstoneLocation.setY(y + 2);
			redstoneLocation.setZ(z + 1);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setX(x);
			redstoneLocation.setY(y + 2);
			redstoneLocation.setZ(z - 1);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setX(x + 2);
			redstoneLocation.setY(y + 2);
			redstoneLocation.setZ(z);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setX(x - 2);
			redstoneLocation.setY(y + 2);
			redstoneLocation.setZ(z);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setX(x);
			redstoneLocation.setY(y + 2);
			redstoneLocation.setZ(z + 2);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setX(x);
			redstoneLocation.setY(y + 2);
			redstoneLocation.setZ(z - 2);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setX(x + 1);
			redstoneLocation.setY(y + 2);
			redstoneLocation.setZ(z + 1);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setX(x + 1);
			redstoneLocation.setY(y + 2);
			redstoneLocation.setZ(z - 1);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setX(x - 1);
			redstoneLocation.setY(y + 2);
			redstoneLocation.setZ(z + 1);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setX(x - 1);
			redstoneLocation.setY(y + 2);
			redstoneLocation.setZ(z - 1);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);

			redstoneLocation.setX(x);
			redstoneLocation.setY(y + 3);
			redstoneLocation.setZ(z);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setY(y + 4);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setY(y + 5);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
			redstoneLocation.setY(y + 6);
			Bukkit.getWorld(getConfig().get("world").toString()).getBlockAt(GameOfTaupes.this.redstoneLocation)
					.setType(Material.REDSTONE_BLOCK);
		}

		org.bukkit.block.Chest chest = (org.bukkit.block.Chest) chestBlock.getState();
		Inventory inv = chest.getInventory();
		inv.clear();
		kits.clear();

		if (GameOfTaupes.this.getConfig().getBoolean("chest.random")) {
			Random rdm = new Random();
			int chestPosition = 12;
			int chestKit;
			ItemStack item = new ItemStack(Material.DIAMOND, 8);

			for (int i = 0; i < 2; i++) {
				while (true) {
					chestKit = rdm.nextInt(9);
					if (!GameOfTaupes.this.kits.contains(chestKit)) {
						GameOfTaupes.this.kits.add(chestKit);
						break;
					}
				}

				switch (chestKit) {
				case 0:
					inv.setItem(chestPosition++, item);
					break;
				case 1:
					item.setAmount(32);
					item.setType(Material.IRON_INGOT);
					inv.setItem(chestPosition++, item);
					break;
				case 2:
					item.setAmount(2);
					item.setType(Material.GOLDEN_APPLE);
					inv.setItem(chestPosition++, item);
					break;
				case 3:
					item.setAmount(10);
					item.setType(Material.EXP_BOTTLE);
					inv.setItem(chestPosition++, item);
					break;
				case 4:
					item.setAmount(1);
					item.setType(Material.ENCHANTMENT_TABLE);
					inv.setItem(chestPosition++, item);
					item.setAmount(32);
					item.setType(Material.LAPIS_ORE);
					inv.setItem(chestPosition++, item);
					break;
				case 5:
					item.setAmount(2);
					item.setType(Material.GOLDEN_APPLE);
					inv.setItem(chestPosition++, item);
					break;
				case 6:
					Potion potion1 = new Potion(2);
					potion1.setType(PotionType.SPEED);
					potion1.setHasExtendedDuration(false);
					potion1.setSplash(true);
					inv.setItem(chestPosition++, potion1.toItemStack(2));
					chestPosition++;
					break;
				case 7:
					Potion potion2 = new Potion(3);
					potion2.setType(PotionType.INSTANT_DAMAGE);
					potion2.setSplash(true);
					potion2.setLevel(2);
					;
					inv.setItem(chestPosition++, potion2.toItemStack(1));
					break;
				case 8:
					item.setAmount(1);
					item.setType(Material.BOW);
					inv.setItem(chestPosition++, item);
					item.setAmount(32);
					item.setType(Material.ARROW);
					inv.setItem(chestPosition++, item);
					break;
				}
			}

			return;
		}

		Random rdm = new Random();
		int chestKit;
		ItemStack item = new ItemStack(Material.DIAMOND, 3);

		if (GameOfTaupes.this.chestLvl == 1) {
			int chestPosition = 12;
			for (int i = 0; i < 3; i++) {
				while (true) {
					chestKit = rdm.nextInt(7);
					if (!GameOfTaupes.this.kits.contains(chestKit)) {
						GameOfTaupes.this.kits.add(chestKit);
						break;
					}
				}
				switch (chestKit) {
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
					item.setType(Material.BOW);
					inv.setItem(chestPosition++, item);
					item.setAmount(32);
					item.setType(Material.ARROW);
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
				inv.setItem(chestPosition++, item);
			}
		} else if (GameOfTaupes.this.chestLvl == 2) {
			int chestPosition = 12;
			for (int i = 0; i < 2; i++) {
				while (true) {
					chestKit = rdm.nextInt(9);
					if (!GameOfTaupes.this.kits.contains(chestKit)) {
						GameOfTaupes.this.kits.add(chestKit);
						break;
					}
				}
				switch (chestKit) {
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
		} else if (GameOfTaupes.this.chestLvl >= 3) {
			chestKit = rdm.nextInt(4);
			switch (chestKit) {
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
	}

	public void writeScoreboard(int minutes, int seconds) {
		NumberFormat formatter2 = new DecimalFormat("00");
		String minute2 = ((NumberFormat) formatter2).format(minutes);
		String second2 = ((NumberFormat) formatter2).format(seconds);

		GameOfTaupes.this.s.getObjective(GameOfTaupes.this.obj.getDisplayName())
				.getScore(ChatColor.WHITE + "Episode " + GameOfTaupes.this.episode).setScore(0);
		GameOfTaupes.this.s.getObjective(GameOfTaupes.this.obj.getDisplayName())
				.getScore("" + ChatColor.WHITE + this.playersAlive.size() + ChatColor.GRAY + " joueurs").setScore(-1);
		GameOfTaupes.this.tmpBorder = (int) getServer().getWorld(getConfig().getString("world")).getWorldBorder()
				.getSize();
		GameOfTaupes.this.s.getObjective(GameOfTaupes.this.obj.getDisplayName())
				.getScore(ChatColor.WHITE + "Border : " + tmpBorder + " x " + tmpBorder).setScore(-3);

		GameOfTaupes.this.tmpPlayers = this.playersAlive.size();

		if (GameOfTaupes.this.gameState < 7) {
			if (!GameOfTaupes.this.hasChangedGS) {
				int min = Integer.parseInt(GameOfTaupes.this.objMinute);
				int sec = Integer.parseInt(GameOfTaupes.this.objSecond);

				if (sec == 0) {
					GameOfTaupes.this.objSecond = "59";
					GameOfTaupes.this.objMinute = formatter2.format(min - 1);
				} else {
					GameOfTaupes.this.objSecond = formatter2.format(sec - 1);
				}

				GameOfTaupes.this.countdownObj = GameOfTaupes.this.objTxt + GameOfTaupes.this.objMinute + ":"
						+ GameOfTaupes.this.objSecond;
			} else {
				GameOfTaupes.this.hasChangedGS = false;
			}

			GameOfTaupes.this.s.getObjective(GameOfTaupes.this.obj.getDisplayName())
					.getScore(ChatColor.WHITE + GameOfTaupes.this.countdownObj).setScore(-4);
		}

		GameOfTaupes.this.s.getObjective(GameOfTaupes.this.obj.getDisplayName()).getScore(minute2 + ":" + second2)
				.setScore(-5);
	}

	public void taupeAnnouncement() {
		OfflinePlayer taupe;
		for (int i = 0; i < GameOfTaupes.this.taupes.size(); i++) {
			for (UUID uid : GameOfTaupes.this.taupes.get(i)) {
				taupe = Bukkit.getOfflinePlayer(uid);
				if (taupe.isOnline()) {
					taupe.getPlayer().sendMessage(ChatColor.RED + "-------Annonce IMPORTANTE------");
					taupe.getPlayer().sendMessage(ChatColor.GOLD + "Vous etes une taupe de votre equipe !");
					taupe.getPlayer().sendMessage(
							ChatColor.GOLD + "Pour parler avec les autres taupes, executez la commande /t < message>");
					taupe.getPlayer().sendMessage(ChatColor.GOLD
							+ "Si vous voulez devoiler votre vraie identite, executez la commande /reveal");
					taupe.getPlayer().sendMessage(
							ChatColor.GOLD + "Pour obtenir votre kit de taupe, executez la commande /claim");
					taupe.getPlayer().sendMessage(ChatColor.GOLD + "Votre but : " + ChatColor.DARK_RED
							+ "Tuer les membres de votre \"equipe\"");
					taupe.getPlayer().sendMessage(ChatColor.RED + "-------------------------------");
					Title.sendTitle(taupe.getPlayer(), "Vous etes une taupe !", "Ne le dites a personne !");

				}
			}
		}
		GameOfTaupes.this.taupessetup = true;
	}

	public void supertaupeAnnouncement() {
		if (!GameOfTaupes.this.getConfig().getBoolean("options.supertaupe")) {
			return;
		}

		OfflinePlayer player;
		for (int i = 0; i < GameOfTaupes.this.supertaupes.size(); i++) {
			player = Bukkit.getOfflinePlayer(GameOfTaupes.this.supertaupes.get(i));
			if (player.isOnline()) {
				player.getPlayer().sendMessage(ChatColor.RED + "-------Annonce IMPORTANTE------");
				player.getPlayer().sendMessage(ChatColor.GOLD + "Vous etes la supertaupe !");
				player.getPlayer().sendMessage(ChatColor.GOLD
						+ "Si vous voulez devoiler votre vraie identite executez la commande /superreveal");
				player.getPlayer().sendMessage(
						ChatColor.GOLD + "Votre but : " + ChatColor.DARK_RED + "Tuer tous les autres joueurs !");
				player.getPlayer().sendMessage(ChatColor.RED + "-------------------------------");
				Title.sendTitle(player.getPlayer(), "Vous etes la supertaupe !", "Ne le dites a personne !");
			}
		}
		GameOfTaupes.this.supertaupessetup = true;
	}

	public void forceReveal(boolean check) {
		for (int i = 0; i < GameOfTaupes.this.taupes.size(); i++) {
			for (UUID taupe : GameOfTaupes.this.taupes.get(i)) {
				if (!GameOfTaupes.this.showedtaupes.contains(taupe)) {
					GameOfTaupes.this.taupesteam.get(i).addPlayer(Bukkit.getOfflinePlayer(taupe));
					GameOfTaupes.this.showedtaupes.add(taupe);
					Bukkit.broadcastMessage(ChatColor.RED + Bukkit.getOfflinePlayer(taupe).getName()
							+ " a revele qu'il etait une taupe !");
				}
			}
		}

		for (Player online : Bukkit.getOnlinePlayers()) {
			online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
		}

		GameOfTaupes.this.taupessetup = true;

		unregisterTeam();
		unregisterTaupeTeam();

		if (check) {
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

		if (check) {
			checkVictory();
		}
	}

	public void claimKit(Player player) {
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

	public void updateCompassTarget() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!GameOfTaupes.this.playersAlive.contains(player.getUniqueId())) {
				return;
			}

			double shortestDistance = 99999;
			Location nearestBoss = GameOfTaupes.this.chestLocation;

			for (int i : GameOfTaupes.this.bossManager.aliveBoss.keySet()) {
				double distanceToPlayer = GameOfTaupes.this.bossManager.getShrinesLocation()
						.get(GameOfTaupes.this.bossManager.aliveBoss.get(i)).distance(player.getLocation());
				if (distanceToPlayer < shortestDistance) {
					nearestBoss = GameOfTaupes.this.bossManager.getShrinesLocation()
							.get(GameOfTaupes.this.bossManager.aliveBoss.get(i));
					shortestDistance = distanceToPlayer;
				}
			}

			player.setCompassTarget(nearestBoss);
		}
	}

	public void RevealPlayerLocation(boolean reset) {
		boolean foundTeam = false;
		int n = 0;
		int tIdx;
		Player p = null;
		Team t = null;
		Random rdm = new Random();

		while (n < 20) {
			++n;
			tIdx = rdm.nextInt(GameOfTaupes.this.s.getTeams().size());
			t = (Team) GameOfTaupes.this.s.getTeams().toArray()[tIdx];

			if (t.getSize() == 0) {
				continue;
			}

			if (!GameOfTaupes.this.teamReveal.contains(t.getName())) {
				for (OfflinePlayer op : t.getPlayers()) {
					if (op.isOnline()) {
						GameOfTaupes.this.teamReveal.add(t.getName());
						p = (Player) op;
						foundTeam = true;
						break;
					}
				}
			}

			if (foundTeam) {
				break;
			}
		}

		if (foundTeam) {
			Bukkit.broadcastMessage(ChatColor.GOLD + "Le Grand Oeil a detecte une armee en "
					+ (int) p.getLocation().getX() + " / " + (int) p.getLocation().getZ() + " ! ");
		} else {
			if (reset) {
				return;
			}
			GameOfTaupes.this.teamReveal.clear();
			RevealPlayerLocation(true);
		}
	}

	// TODO
	public void testIfBossDespawn() {
	}
}
