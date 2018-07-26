package fr.vraken.thepurgeofsalem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import fr.vraken.thepurgeofsalem.Title;


public class EventsClass implements Listener
{
	static ThePurgeOfSalem plugin;
	public static boolean pvp = false;

	ArrayList<ItemStack> loots = new ArrayList<ItemStack>();
	HashMap<Integer, ArrayList<ItemStack>> specialLoots = new HashMap<Integer, ArrayList<ItemStack>>();
	ArrayList<Integer> spawnedLoots = new ArrayList<Integer>();
	
	UUID capturingPlayer = null;
	int capturingTimer = 0;
	BukkitTask capturingTask;
	BukkitTask capturedTask;

	public EventsClass(ThePurgeOfSalem plugin)
	{
		EventsClass.plugin = plugin;
		
		loots.add(new ItemStack(Material.LEATHER_BOOTS, 1));
		loots.add(new ItemStack(Material.LEATHER_BOOTS, 1));
		loots.add(new ItemStack(Material.LEATHER_BOOTS, 1));
		loots.add(new ItemStack(Material.IRON_BOOTS, 1));
		loots.add(new ItemStack(Material.LEATHER_LEGGINGS, 1));
		loots.add(new ItemStack(Material.LEATHER_LEGGINGS, 1));
		loots.add(new ItemStack(Material.LEATHER_LEGGINGS, 1));
		loots.add(new ItemStack(Material.IRON_LEGGINGS, 1));
		loots.add(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
		loots.add(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
		loots.add(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
		loots.add(new ItemStack(Material.IRON_CHESTPLATE, 1));
		loots.add(new ItemStack(Material.LEATHER_HELMET, 1));
		loots.add(new ItemStack(Material.LEATHER_HELMET, 1));
		loots.add(new ItemStack(Material.LEATHER_HELMET, 1));
		loots.add(new ItemStack(Material.IRON_HELMET, 1));
		loots.add(new ItemStack(Material.STONE_SWORD, 1));
		loots.add(new ItemStack(Material.STONE_SWORD, 1));
		loots.add(new ItemStack(Material.STONE_SWORD, 1));
		loots.add(new ItemStack(Material.IRON_SWORD, 1));
		loots.add(new ItemStack(Material.BOW, 1));
		loots.add(new ItemStack(Material.ARROW, 5));
		loots.add(new ItemStack(Material.ARROW, 5));
		loots.add(new ItemStack(Material.GOLDEN_APPLE, 1));
		loots.add(new ItemStack(Material.GOLDEN_APPLE, 1));
		loots.add(new ItemStack(Material.WATER_BUCKET, 1));
		loots.add(new ItemStack(Material.MILK_BUCKET, 1));
		loots.add(new ItemStack(Material.LAVA_BUCKET, 1));
		loots.add(new ItemStack(Material.FLINT_AND_STEEL, 1));
		loots.add(new ItemStack(Material.TNT, 2));
		
		int i = 0;
		ArrayList<ItemStack> speLoot = new ArrayList<ItemStack>();
		ItemStack bow = new ItemStack(Material.BOW, 1);
		bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
		speLoot.add(bow);
		speLoot.add(bow);
		speLoot.add(bow);
		speLoot.add(bow);
		speLoot.add(bow);
		speLoot.add(new ItemStack(Material.ARROW, 64));
		speLoot.add(new ItemStack(Material.ARROW, 64));
		speLoot.add(new ItemStack(Material.ARROW, 64));
		speLoot.add(new ItemStack(Material.ARROW, 64));
		speLoot.add(new ItemStack(Material.ARROW, 64));
		specialLoots.put(i++ , speLoot);
		
		speLoot.clear();
		ItemStack sword = new ItemStack(Material.IRON_SWORD, 1);
		bow.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		speLoot.add(sword);
		speLoot.add(sword);
		speLoot.add(sword);
		speLoot.add(sword);
		speLoot.add(sword);
		specialLoots.put(i++ , speLoot);
		
		speLoot.clear();
		ItemStack chest = new ItemStack(Material.IRON_CHESTPLATE, 1);
		bow.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		speLoot.add(chest);
		speLoot.add(chest);
		speLoot.add(chest);
		speLoot.add(chest);
		speLoot.add(chest);
		specialLoots.put(i++ , speLoot);
		
		speLoot.clear();
		speLoot.add(new ItemStack(Material.GOLDEN_APPLE, 20));
		specialLoots.put(i++ , speLoot);
		
		speLoot.clear();
		speLoot.add(new ItemStack(Material.IRON_BOOTS, 1));
		speLoot.add(new ItemStack(Material.IRON_LEGGINGS, 1));
		speLoot.add(new ItemStack(Material.IRON_CHESTPLATE, 1));
		speLoot.add(new ItemStack(Material.IRON_HELMET, 1));
		speLoot.add(new ItemStack(Material.IRON_SWORD, 1));
		speLoot.add(new ItemStack(Material.BOW, 1));
		speLoot.add(new ItemStack(Material.ARROW, 10));
		speLoot.add(new ItemStack(Material.GOLDEN_APPLE, 1));
		speLoot.add(new ItemStack(Material.IRON_BOOTS, 1));
		speLoot.add(new ItemStack(Material.IRON_LEGGINGS, 1));
		speLoot.add(new ItemStack(Material.IRON_CHESTPLATE, 1));
		speLoot.add(new ItemStack(Material.IRON_HELMET, 1));
		speLoot.add(new ItemStack(Material.IRON_SWORD, 1));
		speLoot.add(new ItemStack(Material.BOW, 1));
		speLoot.add(new ItemStack(Material.ARROW, 10));
		speLoot.add(new ItemStack(Material.GOLDEN_APPLE, 1));
		speLoot.add(new ItemStack(Material.IRON_BOOTS, 1));
		speLoot.add(new ItemStack(Material.IRON_LEGGINGS, 1));
		speLoot.add(new ItemStack(Material.IRON_CHESTPLATE, 1));
		speLoot.add(new ItemStack(Material.IRON_HELMET, 1));
		speLoot.add(new ItemStack(Material.IRON_SWORD, 1));
		speLoot.add(new ItemStack(Material.BOW, 1));
		speLoot.add(new ItemStack(Material.ARROW, 10));
		speLoot.add(new ItemStack(Material.GOLDEN_APPLE, 1));
		speLoot.add(new ItemStack(Material.IRON_BOOTS, 1));
		speLoot.add(new ItemStack(Material.IRON_LEGGINGS, 1));
		speLoot.add(new ItemStack(Material.IRON_CHESTPLATE, 1));
		speLoot.add(new ItemStack(Material.IRON_HELMET, 1));
		speLoot.add(new ItemStack(Material.IRON_SWORD, 1));
		speLoot.add(new ItemStack(Material.BOW, 1));
		speLoot.add(new ItemStack(Material.ARROW, 10));
		speLoot.add(new ItemStack(Material.GOLDEN_APPLE, 1));
		specialLoots.put(i++ , speLoot);
	}

	public static void addItem(Inventory inv, ChatColor ccolor, DyeColor color, String Name, int slot)
	{
		ItemStack team = new ItemStack(Material.BANNER);
		BannerMeta meta = (BannerMeta)team.getItemMeta();
		meta.setDisplayName(ccolor + Name);
		if (color.equals(DyeColor.PINK))
		{
			List<String> lore = new ArrayList<String>();
			for (OfflinePlayer pl : plugin.s.getTeam(plugin.teamf.getString("rose.name")).getPlayers()) {
				lore.add(ChatColor.LIGHT_PURPLE + "- " + pl.getName());
			}
			meta.setLore(lore);
		}
		else if (color.equals(DyeColor.YELLOW))
		{
			List<String> lore = new ArrayList<String>();
			for (OfflinePlayer pl : plugin.s.getTeam(plugin.teamf.getString("jaune.name")).getPlayers()) {
				lore.add(ChatColor.YELLOW + "- " + pl.getName());
			}
			meta.setLore(lore);
		}
		else if (color.equals(DyeColor.PURPLE))
		{
			List<String> lore = new ArrayList<String>();
			for (OfflinePlayer pl : plugin.s.getTeam(plugin.teamf.getString("violette.name")).getPlayers()) {
				lore.add(ChatColor.DARK_PURPLE + "- " + pl.getName());
			}
			meta.setLore(lore);
		}
		else if (color.equals(DyeColor.CYAN))
		{
			List<String> lore = new ArrayList<String>();
			for (OfflinePlayer pl : plugin.s.getTeam(plugin.teamf.getString("cyan.name")).getPlayers()) {
				lore.add(ChatColor.AQUA + "- " + pl.getName());
			}
			meta.setLore(lore);
		}
		else if (color.equals(DyeColor.GREEN))
		{
			List<String> lore = new ArrayList<String>();
			for (OfflinePlayer pl : plugin.s.getTeam(plugin.teamf.getString("verte.name")).getPlayers()) {
				lore.add(ChatColor.GREEN + "- " + pl.getName());
			}
			meta.setLore(lore);
		}
		meta.setBaseColor(color);

		team.setItemMeta(meta);
		inv.setItem(slot, team);
	}

	public static void openTeamInv(Player p)
	{
		Inventory inv = Bukkit.createInventory(p, 9, ChatColor.GOLD + 
				"    Choisir sa guilde");

		addItem(inv, ChatColor.LIGHT_PURPLE, DyeColor.PINK, plugin.teamf.getString("rose.name"), 0);
		addItem(inv, ChatColor.YELLOW, DyeColor.YELLOW, plugin.teamf.getString("jaune.name"), 1);
		addItem(inv, ChatColor.DARK_PURPLE, DyeColor.PURPLE, plugin.teamf.getString("violette.name"), 2);
		addItem(inv, ChatColor.AQUA, DyeColor.CYAN, plugin.teamf.getString("cyan.name"), 3);
		addItem(inv, ChatColor.GREEN, DyeColor.GREEN, plugin.teamf.getString("verte.name"), 4);
		addItem(inv, ChatColor.WHITE, DyeColor.WHITE, "Quitter son �quipe", 6);

		p.openInventory(inv);
	}

	@EventHandler
	public void OnChatEvent(AsyncPlayerChatEvent e)
	{
		Team team = e.getPlayer().getScoreboard().getPlayerTeam(e.getPlayer());
		String format = team.getPrefix() + "<%s> " + ChatColor.WHITE + "%s";
		e.setFormat(format);
	}

	@EventHandler
	public void ChoiceTeam(InventoryClickEvent e)
	{
		if (e.getCurrentItem() == null)
		{
			return;
        }
		
		Player p = (Player)e.getWhoClicked();
		if (e.getInventory().getName().equals(ChatColor.GOLD + "    Choisir sa guilde ") && e.getCurrentItem().getType() == Material.BANNER)
		{
			BannerMeta banner = (BannerMeta)e.getCurrentItem().getItemMeta();

			if (banner.getBaseColor() == DyeColor.PINK) 
			{
				if (plugin.rose.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam"))
				{
					p.sendMessage(plugin.rose.getPrefix() + 
							" Vous avez rejoint " + plugin.teamf.getString("rose.name"));
					plugin.rose.addPlayer(p);
					if(!plugin.playersInTeam.contains(p.getUniqueId()))
					{
						plugin.playersInTeam.add(p.getUniqueId());
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Cette equipe est complete !");
				}
			}
			if (banner.getBaseColor() == DyeColor.CYAN) {
				if (plugin.cyan.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam"))
				{
					p.sendMessage(plugin.cyan.getPrefix() + 
							" Vous avez rejoint " + plugin.teamf.getString("cyan.name"));
					plugin.cyan.addPlayer(p);
					if(!plugin.playersInTeam.contains(p.getUniqueId()))
					{
						plugin.playersInTeam.add(p.getUniqueId());
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Cette equipe est complete !");
				}
			}
			if (banner.getBaseColor() == DyeColor.YELLOW) {
				if (plugin.jaune.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam"))
				{
					p.sendMessage(plugin.jaune.getPrefix() + 
							" Vous avez rejoint " + plugin.teamf.getString("jaune.name"));
					plugin.jaune.addPlayer(p);
					if(!plugin.playersInTeam.contains(p.getUniqueId()))
					{
						plugin.playersInTeam.add(p.getUniqueId());
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Cette equipe est complete !");
				}
			}
			if (banner.getBaseColor() == DyeColor.PURPLE) {
				if (plugin.violette.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam"))
				{
					p.sendMessage(plugin.violette.getPrefix() + 
							" Vous avez rejoint " + plugin.teamf.getString("violette.name"));
					plugin.violette.addPlayer(p);
					if(!plugin.playersInTeam.contains(p.getUniqueId()))
					{
						plugin.playersInTeam.add(p.getUniqueId());
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Cette equipe est complete !");
				}
			}
			if (banner.getBaseColor() == DyeColor.GREEN) {
				if (plugin.verte.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam"))
				{
					p.sendMessage(plugin.verte.getPrefix() + 
							" Vous avez rejoint " + plugin.teamf.getString("verte.name"));
					plugin.verte.addPlayer(p);
					if(!plugin.playersInTeam.contains(p.getUniqueId()))
					{
						plugin.playersInTeam.add(p.getUniqueId());
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Cette equipe est complete !");
				}
			}
			if (banner.getBaseColor() == DyeColor.WHITE) 
			{				
				if(plugin.playersInTeam.contains(p.getUniqueId()))
				{
					plugin.playersInTeam.remove(p.getUniqueId());
					plugin.s.getPlayerTeam(p).removePlayer(p);
					p.sendMessage("Vous avez quitte votre equipe !");
				}
			}
			e.setCancelled(true);
			openTeamInv(p);
		}
	}

	@EventHandler
	public void CancelRegen(EntityRegainHealthEvent e)
	{
		if (e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();

		if (!plugin.gameStarted)
		{
			p.teleport(plugin.lobbyLocation);
			
			p.setGameMode(GameMode.ADVENTURE);
			
			p.getInventory().setItem(0, new ItemStack(Material.BANNER, 1));
			ItemMeta meta1 = p.getInventory().getItem(0).getItemMeta();
			meta1.setDisplayName(ChatColor.GOLD + "Choisir sa guilde");
			p.getInventory().getItem(0).setItemMeta(meta1);

			e.setJoinMessage(ChatColor.BLUE + p.getName() + 
					ChatColor.YELLOW + " a rejoint la partie  " + 
					ChatColor.GRAY + "(" + 
					ChatColor.YELLOW + Bukkit.getOnlinePlayers().size() + "/" + 
					Bukkit.getMaxPlayers() + 
					ChatColor.GRAY + ")");

			plugin.playersInLobby.add(p.getUniqueId());
		}
		else if (!plugin.gameEnd)
		{		
			if (!plugin.playersAlive.contains(p.getUniqueId()))
			{
				p.setGameMode(GameMode.SPECTATOR);
				p.teleport(new Location(
						Bukkit.getWorld(plugin.getConfig().getString("world")), 
						0, 120, 0));					
				
				plugin.playersSpec.add(p.getUniqueId());
				
				e.setJoinMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + 
						p.getName() + " a rejoint la partie  ");
			}
		}
		else if (plugin.gameEnd)
		{
			p.teleport(plugin.respawnLocation);
			p.setGameMode(GameMode.ADVENTURE);

			e.setJoinMessage(ChatColor.BLUE + p.getName() + 
					ChatColor.YELLOW + " a rejoint la partie  " + 
					ChatColor.GRAY + "(" + 
					ChatColor.YELLOW + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + 
					ChatColor.GRAY + ")");

			plugin.playersInLobby.add(p.getUniqueId());
		}
	}
	
	@EventHandler
	public void OnPlayerDisconnect(PlayerQuitEvent e)
	{
		if(plugin.playersInLobby.contains(e.getPlayer().getUniqueId()))
		{
			plugin.playersInLobby.remove(e.getPlayer().getUniqueId());
		}
		else if(plugin.playersSpec.contains(e.getPlayer().getUniqueId()))
		{
			plugin.playersSpec.remove(e.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void OnPlayerRespawn(PlayerRespawnEvent e)
	{
		Player p = e.getPlayer();
		if(!plugin.gameStarted)
		{
			e.setRespawnLocation(plugin.respawnLocation);

			p.setGameMode(GameMode.ADVENTURE);
			
			p.getInventory().setItem(0, new ItemStack(Material.BANNER, 1));
			ItemMeta meta1 = p.getInventory().getItem(0).getItemMeta();
			meta1.setDisplayName(ChatColor.GOLD + "Choisir sa guilde");
			p.getInventory().getItem(0).setItemMeta(meta1);

			if(!plugin.playersInLobby.contains(p.getUniqueId()))
			{
				plugin.playersInLobby.add(p.getUniqueId());
			}
		}
		else if(!plugin.gameEnd)
		{
			if(plugin.playersInLobby.contains(p.getUniqueId()))
			{
				p.setGameMode(GameMode.ADVENTURE);
				e.setRespawnLocation(plugin.respawnLocation);
			}
			else
			{
				p.setGameMode(GameMode.SPECTATOR);
				e.setRespawnLocation(new Location(
						Bukkit.getWorld(plugin.getConfig().getString("world")), 
						0, 120, 0));
				
				if(!plugin.playersSpec.contains(p.getUniqueId()))
				{
					plugin.playersSpec.add(p.getUniqueId());
				}
				
				Title.sendTitle(p, 
						"Pensez a vous mute sur Mumble !", 
						"Par fairplay, assurez-vous que les joueurs en vie ne puissent pas vous entendre !");
			}
		}
		else if(plugin.gameEnd)
		{
			e.setRespawnLocation(plugin.respawnLocation);
			p.setGameMode(GameMode.ADVENTURE);
			
			if(!plugin.playersInLobby.contains(p.getUniqueId()))
			{
				plugin.playersInLobby.add(p.getUniqueId());
			}
		}
	}

	@EventHandler
	public void Options(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		Action a = e.getAction();
		if ((a.equals(Action.RIGHT_CLICK_AIR)
				|| a.equals(Action.RIGHT_CLICK_BLOCK))
				&& p.getItemInHand().getType() == Material.BANNER
				&& p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Choisir sa guilde"))
		{
			e.setCancelled(true);      
			openTeamInv(p);
		}
	}
	
	@EventHandler
	public void PlayerImmunityBeforeTaupes(EntityDamageEvent e)
	{
		if(!plugin.gameStarted || plugin.gameEnd)
		{
			return;
		}
		
		if(plugin.taupessetup)
		{
			return;
		}

		if(e.getEntity() instanceof Player)
		{
			Player player = (Player)e.getEntity();
			if(plugin.playersInLobby.contains(player.getUniqueId()))
			{
				return;
			}
			if(!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
			{
				boolean lethal = (player.getHealth() - e.getFinalDamage()) < 1;
				if(lethal)
				{
					e.setCancelled(true);
					player.setHealth(1.0);
				}
			}
		}
	}
	
	@EventHandler
	public void PlayerDeathInGame(PlayerDeathEvent e)
	{
		Player player = e.getEntity();

		if(plugin.playersAlive.contains(player.getUniqueId()))
		{	    	    
			plugin.deathf.addDefault(player.getName(), (String)e.getDeathMessage());
			plugin.deathf.options().copyDefaults(true);
			try 
			{
				plugin.deathf.save(plugin.filesManager.deathf);
			}
			catch (IOException e1) {}

			plugin.playersAlive.remove(player.getUniqueId());

			e.getDrops().add(new ItemStack(Material.GOLDEN_APPLE));

			Team team = player.getScoreboard().getPlayerTeam(player);
			team.removePlayer(player);

			e.setDeathMessage(team.getPrefix() 
					+ e.getEntity().getName() 
					+ " est mort !");

			for (Player pl : Bukkit.getOnlinePlayers()) 
			{
				pl.playSound(pl.getLocation(), Sound.WITHER_DEATH, 10.0F, 10.0F);
			}
			
			if (plugin.taupes.containsKey(player.getUniqueId())) 
			{
				plugin.aliveTaupes.remove(player.getUniqueId());
				
				if(plugin.taupes.get(player.getUniqueId()) == 2)
				{
					plugin.assassinPotionUsed = true;
				}
			}
			
			if (plugin.hunters.containsKey(player.getUniqueId())) 
			{
				plugin.aliveHunters.remove(player.getUniqueId());
				
				if(plugin.hunters.get(player.getUniqueId()) == 4)
				{
					if(player.getKiller() != null)
					{
						plugin.cursedTeam = plugin.s.getPlayerTeam(player.getKiller());
					}
				}
			}
			
			if(plugin.supertaupe == player.getUniqueId())
			{
				plugin.supertaupeConsumed = false;
				plugin.supertaupeDeath();
			}

			new BukkitRunnable()
			{
				public void run()
				{	
					plugin.unregisterTeam();
					plugin.unregisterTaupeTeam();
					plugin.unregisterHunterTeam();
					plugin.unregisterSupertaupeTeam();
					plugin.checkVictory();
				}
			}.runTaskLater(plugin, 60);	
		}
	}
	
	@EventHandler
	public void CancelPVPInGame(EntityDamageByEntityEvent e)
	{		
		if (!pvp && 
				e.getDamager() instanceof Player && 
				e.getEntity() instanceof Player) 
		{
			Player player = (Player)e.getEntity();
			if(plugin.playersInLobby.contains(player.getUniqueId()))
			{
				return;
			}
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void OnAssassinDrinkInvisibilityPotion(PlayerItemConsumeEvent e)
	{
		UUID uid = e.getPlayer().getUniqueId();
		
		if(plugin.taupes.get(uid) != 2)
		{
			return;
		}
		
		if(plugin.assassinPotionUsed)
		{
			return;
		}
		
		if(e.getItem().getType().equals(Material.POTION))
		{
			Potion potion = Potion.fromItemStack(e.getItem());
			PotionType type = potion.getType();
			
			if(type.equals(PotionType.INVISIBILITY))
			{
				plugin.assassinTeam.addPlayer(Bukkit.getOfflinePlayer(uid));
				
				new BukkitRunnable()
				{
					public void run()
					{
						if(!plugin.assassinPotionUsed)
						{
							plugin.taupesTeam.addPlayer(Bukkit.getOfflinePlayer(uid));
							plugin.assassinPotionUsed = true;
							plugin.unregisterTaupeTeam();
						}
					}
				}.runTaskLater(plugin, 20 * 60 * 8);	
			}
		}
	}

	@EventHandler
	public void OnWolfSpawned(CreatureSpawnEvent e)
	{
		if(!e.getSpawnReason().equals(SpawnReason.SPAWNER_EGG))
		{
			return;
		}
		
		if(!e.getEntityType().equals(EntityType.WOLF))
		{
			return;
		}
		
		Wolf wolf = (Wolf) e.getEntity();

		UUID uid = null;
		boolean angry = false;
		
		for(UUID id : plugin.taupes.keySet())
		{
			uid = id;
			if(plugin.taupes.get(id) == 4)
			{
				if(!plugin.aliveTaupes.contains(uid))
				{
					angry = true;
				}
				break;
			}
		}

		wolf.setAdult();
		if(angry)
		{
			wolf.setAngry(true);
		}
		else
		{
			wolf.setAngry(false);
			wolf.setOwner(Bukkit.getOfflinePlayer(uid));
		}
	}
	
	@EventHandler
	public void OnCursedTeamTakingDamage(EntityDamageByEntityEvent e)
	{
		if (e.getDamager() instanceof Player 
				&& e.getEntity() instanceof Player) 
		{
			Player damaged = (Player) e.getEntity();
			Player damager = (Player) e.getDamager();
			
			if(plugin.s.getPlayerTeam(damager) == plugin.huntersTeam)
			{
				if(plugin.s.getPlayerTeam(damaged) == plugin.cursedTeam)
				{
					e.setDamage(e.getDamage() * 1.5);
				}
			}
		}
	}

	@EventHandler
	public void OnBlacksmithTeamDamaged(EntityDamageByEntityEvent e)
	{
		if (e.getDamager() instanceof Player 
				&& e.getEntity() instanceof Player) 
		{
			Player damaged = (Player) e.getEntity();
			
			if(plugin.s.getPlayerTeam(damaged) == plugin.cyan)
			{
				e.setDamage(e.getDamage() * 0.8);
			}
		}
	}

	@EventHandler
	public void OnMiliciaTeamDamage(EntityDamageByEntityEvent e)
	{
		if (e.getDamager() instanceof Player 
				&& e.getEntity() instanceof Player) 
		{
			Player damager = (Player) e.getDamager();
			
			if(plugin.s.getPlayerTeam(damager) == plugin.rose
					&& damager.getItemInHand().getType() == Material.IRON_SWORD)
			{
				e.setDamage(e.getDamage() * 1.2);
			}
		}
	}

	@EventHandler
	public void OnHunterTeamDamage(EntityDamageByEntityEvent e)
	{
		if (e.getDamager() instanceof Player 
				&& e.getEntity() instanceof Player) 
		{
			Player damager = (Player) e.getDamager();
			
			if(plugin.s.getPlayerTeam(damager) == plugin.verte
					&& damager.getItemInHand().getType() == Material.BOW)
			{
				e.setDamage(e.getDamage() * 1.2);
			}
		}
	}
	
	@EventHandler
	public void OnPlayerStepOnPressurePlate(PlayerInteractEvent e)
	{
		if(e.getAction().equals(Action.PHYSICAL))
		{
			if(e.getClickedBlock().getType() == Material.GOLD_PLATE)
			{
				if(capturingPlayer == null)
				{
					capturingPlayer = e.getPlayer().getUniqueId();
					
					Bukkit.broadcastMessage(plugin.s.getPlayerTeam(e.getPlayer()).getPrefix() 
							+ e.getPlayer().getName() 
							+ ChatColor.GOLD
							+ " a commence a capturer le Graal ! ");
					Bukkit.broadcastMessage(ChatColor.GOLD 
							+ "Depechez-vous de l'arreter avant qu'il ne soit trop tard ! ");
					
					capturingTask = new BukkitRunnable() 
					{
						public void run() 
						{		
							if(capturingTimer % 10 == 0)
							{
								int time = plugin.getConfig().getInt("options.graaltimetocapture") - capturingTimer;
								Bukkit.broadcastMessage(ChatColor.GOLD 
										+ "Le Graal sera capture dans "
										+ time
										+ " secondes ! ");
							}
							
							++capturingTimer;
						}
					}.runTaskTimer(plugin, 0, 20);
					
					capturedTask = new BukkitRunnable() 
					{
						public void run() 
						{
							Bukkit.broadcastMessage(plugin.s.getPlayerTeam(Bukkit.getOfflinePlayer(capturingPlayer)).getPrefix() 
									+ Bukkit.getOfflinePlayer(capturingPlayer).getName() 
									+ ChatColor.GOLD
									+ " a capture le Graal ! ");
							
							Team team = plugin.s.getPlayerTeam(Bukkit.getOfflinePlayer(capturingPlayer));
							
							plugin.announceWinner(team);
						}
					}.runTaskLater(plugin, 20 * plugin.getConfig().getInt("options.graaltimetocapture"));
				}
			}
		}
	}

	@EventHandler
	public void OnPlayerLeavePressurePlate(PlayerMoveEvent e)
	{
		if(capturingPlayer == null)
		{
			return;
		}
		
		if(e.getPlayer().getUniqueId() == capturingPlayer)
		{
			Location loc = e.getTo();
			loc.setY(loc.getY() - 1);
			if(loc.getWorld().getBlockAt(loc).getType() != Material.GOLD_PLATE)
			{
				capturingPlayer = null;
				capturingTimer = 0;
				capturingTask.cancel();
				capturedTask.cancel();
				
				Bukkit.broadcastMessage(ChatColor.GOLD 
						+ "La capture du Graal a ete interrompue ! ");
			}
		}
	}

	@EventHandler
	public void OnPlayerDisconnectFromPressurePlate(PlayerQuitEvent e)
	{
		if(capturingPlayer == null)
		{
			return;
		}
		
		if(e.getPlayer().getUniqueId() == capturingPlayer)
		{
			capturingPlayer = null;
			capturingTimer = 0;
			capturingTask.cancel();
			capturedTask.cancel();
				
			Bukkit.broadcastMessage(ChatColor.GOLD 
					+ "La capture du Graal a ete interrompue ! ");
		}
	}

	@EventHandler
	public void OnInnkeeperEatGoldenApple(PlayerItemConsumeEvent e)
	{
		UUID uid = e.getPlayer().getUniqueId();
		
		if(plugin.s.getPlayerTeam(Bukkit.getOfflinePlayer(uid)) == plugin.violette)
		{
			if(e.getItem().getType().equals(Material.GOLDEN_APPLE))
			{
				Bukkit.getPlayer(uid).setHealth(Bukkit.getPlayer(uid).getHealth() + 2);
			}
		}
	}

	@EventHandler
    public void OnPlayerOpenLootChest(InventoryOpenEvent e)
	{
        if (e.getInventory().getHolder() instanceof Chest)
        {
        	Random rdm = new Random();
        	int kit;
        	int max = 3;
        	
        	if(plugin.s.getPlayerTeam((OfflinePlayer) e.getPlayer()) == plugin.jaune)
        	{
        		++max;
        	}
        	
        	for(int i = 0; i < max; ++i)
        	{
        		kit = rdm.nextInt(loots.size());
        		e.getInventory().addItem(loots.get(kit));
        	}
        	
        	Chest chest = (Chest) e.getInventory().getHolder();
        	chest.getBlock().breakNaturally();
        }
    }

	@EventHandler
    public void OnPlayerOpenSpecialLootChest(InventoryOpenEvent e)
	{
        if (e.getInventory().getHolder() instanceof DoubleChest)
        {
        	Random rdm = new Random();
        	int kit;
        	
        	while(true)
        	{
        		kit = rdm.nextInt(specialLoots.size());
        		
        		if(!spawnedLoots.contains(kit))
        		{
        			break;
        		}
        	}
        	
        	for(int i = 0; i < specialLoots.get(kit).size(); ++i)
        	{
        		e.getInventory().addItem(specialLoots.get(kit).get(i));
        	}
        	
        	DoubleChest chest = (DoubleChest) e.getInventory().getHolder();
        	Chest lchest = (Chest) chest.getLeftSide().getInventory().getHolder();
        	Chest rchest = (Chest) chest.getRightSide().getInventory().getHolder();
        	
        	lchest.getBlock().breakNaturally();
        	rchest.getBlock().breakNaturally();
        	
        	Team team = plugin.s.getPlayerTeam(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
        	
        	for(OfflinePlayer player : team.getPlayers())
        	{
        		plugin.forbiddenPlayers.add(player.getUniqueId());
        	}
        }
    }

	@EventHandler
	public void OnForbiddenPlayerInteract(PlayerInteractEvent e)
	{
		if(!e.getAction().equals(Action.PHYSICAL))
		{
			return;
		}
		
		if(!e.getClickedBlock().getType().equals(Material.STONE_PLATE))
		{
			return;
		}
		
		if(plugin.forbiddenPlayers.contains(e.getPlayer().getUniqueId()))
		{
			e.getPlayer().sendMessage(ChatColor.RED 
					+ "Vous avez déjà profité de la bénédiction d'un temple ! ");
			
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void OnPlayerCaptureGraal(PlayerInteractEvent e)
	{
		if(!e.getAction().equals(Action.PHYSICAL))
		{
			return;
		}
		
		if(!e.getClickedBlock().getType().equals(Material.GOLD_PLATE))
		{
			return;
		}
		
		if(capturingPlayer == null)
		{
			capturingPlayer = e.getPlayer().getUniqueId();
			capturingTimer = 0;
			
			Bukkit.broadcastMessage(plugin.s.getPlayerTeam(e.getPlayer()).getPrefix()
					+ e.getPlayer().getName()
					+ ChatColor.GOLD
					+ " a commence a capturer le Graal ! ");			
			Bukkit.broadcastMessage("Depechez-vous de l'en empecher ! ");
			
			capturingTask = new BukkitRunnable()
			{
				public void run()
				{
					if(capturingTimer % 10 == 0)
					{
						int time = plugin.getConfig().getInt("options.graaltimetocapture") - capturingTimer;
						Bukkit.broadcastMessage("Temps restant : " + time + " secondes ! ");
					}
					
					++capturingTimer;
				}
			}.runTaskTimer(plugin, 0, 20);
			
			capturedTask = new BukkitRunnable() 
			{
				public void run()
				{
					Bukkit.broadcastMessage(plugin.s.getPlayerTeam(e.getPlayer()).getPrefix()
							+ e.getPlayer().getName()
							+ ChatColor.GOLD
							+ " a capture le Graal ! ");
					
					plugin.announceWinner(plugin.s.getPlayerTeam(e.getPlayer()));
				}
			}.runTaskLater(plugin, 20 * plugin.getConfig().getInt("options.graaltimetocapture"));
		}
	}

	@EventHandler
	public void OnPlayerCapturingMove(PlayerMoveEvent e)
	{
		if(capturingPlayer == null)
		{
			return;
		}
		
		if(e.getPlayer().getUniqueId() != capturingPlayer)
		{
			return;
		}
		
		Location loc = e.getTo();
		loc.setY(loc.getY() - 1);
		
		if(!loc.getBlock().getType().equals(Material.GOLD_PLATE))
		{
			capturingPlayer = null;
			capturingTimer = 0;
			
			capturingTask.cancel();
			capturedTask.cancel();
		}
	}

	@EventHandler
	public void OnPlayerCapturingDisconnect(PlayerQuitEvent e)
	{
		if(capturingPlayer == null)
		{
			return;
		}
		
		if(e.getPlayer().getUniqueId() != capturingPlayer)
		{
			return;
		}
			
		capturingPlayer = null;
		capturingTimer = 0;
			
		capturingTask.cancel();
		capturedTask.cancel();
	}
	
}
