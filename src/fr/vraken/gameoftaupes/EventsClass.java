package fr.vraken.gameoftaupes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftShapedRecipe;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;


public class EventsClass implements Listener
{
	static GameOfTaupes plugin;
	static boolean rushIsStart = false;
	static boolean countdownIsStart = false;
	static ArrayList<UUID> alive = new ArrayList<UUID>();
	public static boolean pvp = false;
	ItemStack playerSkull = new ItemStack(Material.SKULL_ITEM, 1, (short)3);

	public EventsClass(GameOfTaupes gameoftaupes)
	{
		plugin = gameoftaupes;
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
				"    Choisir " + plugin.teamChoiceString);

		addItem(inv, ChatColor.LIGHT_PURPLE, DyeColor.PINK, plugin.teamf.getString("rose.name"), 0);
		addItem(inv, ChatColor.YELLOW, DyeColor.YELLOW, plugin.teamf.getString("jaune.name"), 1);
		addItem(inv, ChatColor.DARK_PURPLE, DyeColor.PURPLE, plugin.teamf.getString("violette.name"), 2);
		addItem(inv, ChatColor.AQUA, DyeColor.CYAN, plugin.teamf.getString("cyan.name"), 3);
		addItem(inv, ChatColor.GREEN, DyeColor.GREEN, plugin.teamf.getString("verte.name"), 4);

		p.openInventory(inv);
	}

	public static void CountDown(String name, int sec, int min)
	{
		if (!countdownIsStart) {
			new BukkitRunnable()
			{        
				public void run()
				{
					throw new Error("Unresolved compilation problem: \n\tNo enclosing instance of the type EventsClass is accessible in scope\n");
				}
			}.runTaskTimer(plugin, 0L, 20L);
		}
	}

	static void xpSound()
	{
		for(Team teams : plugin.s.getTeams())
		{
			for (OfflinePlayer p : teams.getPlayers()) 
			{
				p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.ORB_PICKUP, 1.0F, 0.0F);
			} 
		}   
	}

	public void ClearDrops(String world)
	{
		World w = Bukkit.getServer().getWorld(world);
		if (w == null) {
			return;
		}
		for (Entity e : w.getEntities()) {
			if (e.getType() == EntityType.DROPPED_ITEM) {
				e.remove();
			}
		}
	}

	static void xpLevel(int level)
	{
		for(Team teams : plugin.s.getTeams())
		{
			for (OfflinePlayer p : teams.getPlayers()) 
			{
				p.getPlayer().setLevel(level);
			} 
		}
	}

	static void witherSound()
	{
		for(Team teams : plugin.s.getTeams())
		{
			for (OfflinePlayer p : teams.getPlayers()) 
			{
				p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.WITHER_DEATH, 10.0F, 10.0F);
			} 
		} 
	}

	@EventHandler
	public void ChoiceTeam(InventoryClickEvent e)
	{
		Player p = (Player)e.getWhoClicked();
		if (e.getInventory().getName().equals(ChatColor.GOLD + "    Choisir " + plugin.teamChoiceString) && e.getCurrentItem().getType() == Material.BANNER)
		{
			BannerMeta banner = (BannerMeta)e.getCurrentItem().getItemMeta();

			if (banner.getBaseColor() == DyeColor.PINK) 
			{
				if (plugin.rose.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam"))
				{
					p.sendMessage(plugin.rose.getPrefix() + "�" + 
							ChatColor.RESET + 
							" Vous avez rejoint " + plugin.teamf.getString("rose.name"));
					plugin.rose.addPlayer(p);
					if(!plugin.playersInTeam.contains(p.getUniqueId()))
					{
						plugin.playersInTeam.add(p.getUniqueId());
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Cette équipe est complète !");
				}
			}
			if (banner.getBaseColor() == DyeColor.CYAN) {
				if (plugin.cyan.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam"))
				{
					p.sendMessage(plugin.cyan.getPrefix() + "�" + 
							ChatColor.RESET + 
							" Vous avez rejoint " + plugin.teamf.getString("cyan.name"));
					plugin.cyan.addPlayer(p);
					if(!plugin.playersInTeam.contains(p.getUniqueId()))
					{
						plugin.playersInTeam.add(p.getUniqueId());
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Cette équipe est complète !");
				}
			}
			if (banner.getBaseColor() == DyeColor.YELLOW) {
				if (plugin.jaune.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam"))
				{
					p.sendMessage(plugin.jaune.getPrefix() + "�" + 
							ChatColor.RESET + 
							" Vous avez rejoint " + plugin.teamf.getString("jaune.name"));
					plugin.jaune.addPlayer(p);
					if(!plugin.playersInTeam.contains(p.getUniqueId()))
					{
						plugin.playersInTeam.add(p.getUniqueId());
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Cette équipe est complète !");
				}
			}
			if (banner.getBaseColor() == DyeColor.PURPLE) {
				if (plugin.violette.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam"))
				{
					p.sendMessage(plugin.violette.getPrefix() + "�" + 
							ChatColor.RESET + 
							" Vous avez rejoint " + plugin.teamf.getString("violette.name"));
					plugin.violette.addPlayer(p);
					if(!plugin.playersInTeam.contains(p.getUniqueId()))
					{
						plugin.playersInTeam.add(p.getUniqueId());
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Cette équipe est complète !");
				}
			}
			if (banner.getBaseColor() == DyeColor.GREEN) {
				if (plugin.verte.getPlayers().size() < plugin.getConfig().getInt("options.playersperteam"))
				{
					p.sendMessage(plugin.verte.getPrefix() + "�" + 
							ChatColor.RESET + 
							" Vous avez rejoint " + plugin.teamf.getString("verte.name"));
					plugin.verte.addPlayer(p);
					if(!plugin.playersInTeam.contains(p.getUniqueId()))
					{
						plugin.playersInTeam.add(p.getUniqueId());
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Cette équipe est complète !");
				}
			}
			e.setCancelled(true);
			openTeamInv(p);
		}
	}

	@EventHandler
	public void Regen(EntityRegainHealthEvent e)
	{
		if (e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();

		if (!rushIsStart)
		{
			plugin.getServer().createWorld(
					new WorldCreator(plugin.getConfig().get("lobby.world")
							.toString()));
			p.teleport(new Location(Bukkit.getWorld(plugin.getConfig()
					.get("lobby.world").toString()), plugin.getConfig().getInt(
							"lobby.X"), plugin.getConfig().getInt("lobby.Y"), plugin
					.getConfig().getInt("lobby.Z")));
			p.getInventory().setItem(0, new ItemStack(Material.BANNER, 1));
			p.setGameMode(GameMode.ADVENTURE);

			ItemMeta meta1 = p.getInventory().getItem(0).getItemMeta();
			meta1.setDisplayName(ChatColor.GOLD + "Choisir " + plugin.teamChoiceString);
			p.getInventory().getItem(0).setItemMeta(meta1);

			e.setJoinMessage(ChatColor.BLUE + p.getName() + ChatColor.YELLOW + 
					" a rejoint la partie  " + ChatColor.GRAY + "(" + 
					ChatColor.YELLOW + Bukkit.getOnlinePlayers().size() + "/" + 
					Bukkit.getMaxPlayers() + ChatColor.GRAY + ")");
			if ((!countdownIsStart) && 
					(plugin.getConfig().getBoolean("options.cooldown")))
			{
				CountDown("GameOfTaupes", 60, 
						plugin.getConfig().getInt("options.minplayers"));
				countdownIsStart = true;
			}
		}
		else if (!alive.contains(p.getUniqueId()))
		{
			e.setJoinMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + 
					p.getName() + " a rejoint la partie  ");
			p.setGameMode(GameMode.SPECTATOR);
			p.teleport(new Location(Bukkit.getWorld(EventsClass.plugin.getConfig().get("lobby.world").toString()), EventsClass.plugin.getConfig().getInt("lobby.X"), EventsClass.plugin.getConfig().getInt("lobby.Y"), EventsClass.plugin.getConfig().getInt("lobby.Z")));p.setGameMode(GameMode.SPECTATOR);

			Title.sendTitle(p, "Pensez à vous mute sur Mumble !", "Par fairplay, assurez-vous que les joueurs en vie ne peuvent pas vous entendre !");
		}
	}

	@EventHandler
	public void OnPlayerRespawn(PlayerRespawnEvent e)
	{
		Player p = e.getPlayer();
		Title.sendTitle(p, "Pensez à vous mute sur Mumble !", "Par fairplay, assurez-vous que les joueurs en vie ne peuvent pas vous entendre !");

	}

	@EventHandler
	public void Options(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		Action a = e.getAction();
		if ((a.equals(Action.RIGHT_CLICK_AIR)
				|| a.equals(Action.RIGHT_CLICK_BLOCK))
				&& p.getItemInHand().getType() == Material.BANNER
				&& p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Choisir " + plugin.teamChoiceString))
		{
			e.setCancelled(true);      
			openTeamInv(p);
		}
	}

	@EventHandler
	public void PlayerImmunity(EntityDamageEvent e)
	{
		if(plugin.taupessetup)
		{
			return;
		}

		try
		{
			Player player = (Player)e.getEntity();
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
		catch(Exception ex){}
	}

	@EventHandler
	public void PlayerDeath(PlayerDeathEvent e)
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

			alive.remove(player.getUniqueId());  
			plugin.playersAlive.remove(player.getUniqueId());

			e.getDrops().add(new ItemStack(Material.SKULL_ITEM));
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

			for(int i = 0; i < plugin.getConfig().getInt("options.taupesteams"); i++)
			{
				if (plugin.taupes.get(i).contains(player.getUniqueId())) 
				{
					plugin.aliveTaupes.get(i).remove(player.getUniqueId());
				}

				if (plugin.supertaupes.contains(player.getUniqueId())) 
				{
					plugin.aliveSupertaupes.remove(player.getUniqueId());
				}
			}

			new BukkitRunnable()
			{
				public void run()
				{
					Bukkit.getPlayer("Spec").performCommand("dynmap hide " + player.getName());
					Bukkit.getPlayer("Spec").performCommand("tp " + player.getName() + " 0 500 0");
				}
			}.runTaskLater(plugin, 60);
		}
		else if(plugin.duelInProgress)
		{
			String victor;
			String loser;
			if(plugin.provoked == player.getUniqueId())
			{
				victor = Bukkit.getPlayer(plugin.provoker).getName();
				loser = Bukkit.getPlayer(plugin.provoked).getName();
			}
			else
			{
				victor = Bukkit.getPlayer(plugin.provoked).getName();
				loser = Bukkit.getPlayer(plugin.provoker).getName();
			}
			for (Player pl : Bukkit.getOnlinePlayers()) 
			{
				if(!plugin.playersAlive.contains(pl.getUniqueId()))
				{
					pl.sendMessage(victor + " a remporté son duel contre " + loser + " !");
				}
			}
			plugin.duelInProgress = false;
			Bukkit.getPlayer(plugin.provoked).setGameMode(GameMode.SPECTATOR);
			Bukkit.getPlayer(plugin.provoker).setGameMode(GameMode.SPECTATOR);
			plugin.provoked = null;
			plugin.provoker = null;
		}
	}

	@EventHandler
	public void BrewCancel(BrewEvent e)
	{
		BrewerInventory bi = e.getContents();
		if ((bi.getIngredient().getType().equals(Material.GLOWSTONE_DUST)) && 
				(!plugin.getConfig().getBoolean("potions.allowglowstone")))
		{
			for (HumanEntity player : bi.getViewers()) {
				player.sendMessage(ChatColor.RED + 
						"Les potions de niveau 2 sont interdites !");
			}
			e.setCancelled(true);
		}
		else if ((bi.getIngredient().getType().equals(Material.BLAZE_POWDER)) && 
				(!plugin.getConfig().getBoolean("potions.strength")))
		{
			e.setCancelled(true);
			for (HumanEntity player : bi.getViewers()) {
				player.sendMessage(ChatColor.RED + 
						"Les potions de force sont interdites !");
			}
		}
		else if ((bi.getIngredient().getType().equals(Material.GHAST_TEAR)) && 
				(!plugin.getConfig().getBoolean("potions.regeneration")))
		{
			e.setCancelled(true);
			for (HumanEntity player : bi.getViewers()) {
				player.sendMessage(ChatColor.RED + 
						"Les potions de régénération sont interdites !");
			}
		}
	}

	@EventHandler
	public void RespawTp(PlayerRespawnEvent e)
	{
		final Player p = e.getPlayer();
		new BukkitRunnable()
		{
			public void run()
			{
				p.teleport(new Location(Bukkit.getWorld(EventsClass.plugin.getConfig().get("lobby.world").toString()), EventsClass.plugin.getConfig().getInt("lobby.X"), EventsClass.plugin.getConfig().getInt("lobby.Y"), EventsClass.plugin.getConfig().getInt("lobby.Z")));p.setGameMode(GameMode.SPECTATOR);
			}
		}.runTaskLater(plugin, 4L);
	}

	@EventHandler
	public void CancelDrop(PlayerDropItemEvent e)
	{
		Player p = e.getPlayer();
		if (p.getWorld().equals(Bukkit.getWorld(plugin.getConfig().get("lobby.world").toString()))) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void CancelPVP(EntityDamageEvent e)
	{
		if (e.getEntity().getWorld().equals(Bukkit.getWorld(plugin.getConfig().get("lobby.world").toString()))) 
		{
			if(!plugin.duelInProgress)
			{
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void CancelPVp2(EntityDamageByEntityEvent e)
	{
		if ((!pvp) && ((e.getDamager() instanceof Player)) && 
				((e.getEntity() instanceof Player))) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void CancelCraft(PrepareItemCraftEvent e)
	{
		CraftShapedRecipe craft = new CraftShapedRecipe(new ItemStack(Material.SPECKLED_MELON));
		craft.shape(new String[] { "abc", "def", "ghi" });
		craft.setIngredient('a', Material.GOLD_NUGGET);
		craft.setIngredient('b', Material.GOLD_NUGGET);
		craft.setIngredient('c', Material.GOLD_NUGGET);
		craft.setIngredient('d', Material.GOLD_NUGGET);
		craft.setIngredient('f', Material.GOLD_NUGGET);
		craft.setIngredient('g', Material.GOLD_NUGGET);
		craft.setIngredient('h', Material.GOLD_NUGGET);
		craft.setIngredient('i', Material.GOLD_NUGGET);
		craft.setIngredient('e', Material.MELON);
		Bukkit.addRecipe(craft);
		if (((CraftShapedRecipe)e.getRecipe()).getIngredientMap().equals(craft.getIngredientMap()))
		{
			e.getInventory().setResult(new ItemStack(Material.AIR));
			for (HumanEntity p : e.getViewers()) {
				p.sendMessage("Ce craft a été modifié !");
			}
		}
	}

	@EventHandler
	public void OnPlayerOpenTreasureChest(PlayerInteractEvent e)
	{
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.TRAPPED_CHEST)
		{
			if(e.getClickedBlock().getLocation().distance(plugin.chestLocation) <= 10.0f)
			{
				Bukkit.getPlayer("Spec").performCommand("dmarker delete chest");
			}
		}
	}
}
