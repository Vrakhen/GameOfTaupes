package fr.vraken.gameoftaupes;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Team;

public class BossEvents implements Listener
{
	int gzombieDead = 0;
	int ccreeperDead = 0;
	int sskeletonDead = 0;
	int endermanDead = 0;
	int spiderDead = 0;
	int witchDead = 0;
	
	int zombielife = 4;
	int creeperlife = 4;
	int skeletonlife = 4;
	int endermanlife = 2;
	int spiderlife = 3;
	int witchlife = 3;
	
	boolean isWitchDead = false;

	ArrayList<Player> slowingPlayers = new ArrayList<Player>();
	ArrayList<Player> resistPlayers = new ArrayList<Player>();
	ArrayList<Player> firePlayers = new ArrayList<Player>();
	
	static GameOfTaupes plugin;
	BossManager bossManager;

	public BossEvents(GameOfTaupes gameoftaupes, BossManager bossManager)
	{
		plugin = gameoftaupes;
		this.bossManager = bossManager;
	}
	
	
	@EventHandler
	public void onBossSummon(PlayerInteractEvent e)
	{
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.CHEST)
		{
			for(int i : this.bossManager.getBossLocations().keySet())
			{
				if(e.getClickedBlock().getLocation().distance(this.bossManager.getShrinesLocation().get(i)) <= 10.0f)
				{
					this.bossManager.summonBoss(i);
					return;
				}
			}
		}
	}

	
	//GOLDEN ZOMBIE MANAGER
	//---------------------
	@EventHandler
	public void onDamageZombie(EntityDamageByEntityEvent e)
	{
		if(e.getEntity().getCustomName() == plugin.bossf.getString("boss.1"))
		{		
			LivingEntity livingzombie = (LivingEntity) e.getEntity();
			Zombie zombie = (Zombie) livingzombie;
			zombie.setTarget((LivingEntity)e.getDamager());
			if(livingzombie.getHealth() - e.getFinalDamage() <= 0)
			{
				e.setCancelled(true);	
				if(gzombieDead < zombielife)
				{
					gzombieDead++;
					livingzombie.setHealth(20.0f);
				}
				else
				{
					Location loc = e.getEntity().getLocation();
					e.getEntity().playEffect(EntityEffect.DEATH);
					e.getEntity().remove();
					plugin.bossManager.aliveBoss.remove(1);
					Entity killer = e.getDamager();
					Player player;
					if(killer instanceof Arrow)
					{
						Arrow arr = (Arrow) killer;
						player = (Player)arr.getShooter();
					}
					else 
					{
						player = (Player) killer;
					}
					
					if(player instanceof Player)
					{
						Team team = plugin.s.getPlayerTeam(player);
						int team_nb = team.getSize();
						ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, team_nb);
						
						player.getWorld().dropItemNaturally(loc, gapple);
						
						PotionEffect abso = new PotionEffect(PotionEffectType.ABSORPTION, 24000, 1, false, false);

						for(OfflinePlayer pl : team.getPlayers())
						{
							LivingEntity livingpl = (LivingEntity) pl;
							livingpl.addPotionEffect(abso);
						}
						
						BroadcastBossDeath(team, e.getEntity().getCustomName());
					}
					else
					{
						BroadcastBossDeath(null, e.getEntity().getCustomName());
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onCombustZombie(EntityCombustEvent e)
	{
		if(e.getEntity().getCustomName() == plugin.bossf.getString("boss.1"))
		{
			e.setCancelled(true);
		}
	}
	  
	
	//CHARGED CREEPER MANAGER
	//-----------------------
	@EventHandler
	public void onDamageCreeper(EntityDamageByEntityEvent e)
	{
		if(e.getEntity().getCustomName() == plugin.bossf.getString("boss.3"))
		{		
			LivingEntity livingcreeper = (LivingEntity) e.getEntity();
			Creeper creeper = (Creeper) livingcreeper;
			creeper.setTarget((LivingEntity)e.getDamager());
			if(livingcreeper.getHealth() - e.getFinalDamage() <= 0)
			{
				e.setCancelled(true);	
				if(ccreeperDead < creeperlife)
				{
					ccreeperDead++;
					livingcreeper.setHealth(20.0f);
				}
				else
				{
					Location loc = e.getEntity().getLocation();
					e.getEntity().playEffect(EntityEffect.DEATH);
					e.getEntity().remove();
					plugin.bossManager.aliveBoss.remove(3);
					Entity killer = e.getDamager();
					Player player;
					if(killer instanceof Arrow)
					{
						Arrow arr = (Arrow) killer;
						player = (Player)arr.getShooter();
					}
					else 
					{
						player = (Player) killer;
					}
					
					if(player instanceof Player)
					{
						Team team = plugin.s.getPlayerTeam(player);
						int team_nb = team.getSize();
						ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, team_nb);
						
						player.getWorld().dropItemNaturally(loc, gapple);
						
						for(OfflinePlayer pl : team.getPlayers())
						{
							LivingEntity livingpl = (LivingEntity) pl;
							livingpl.setHealth(20.0f);
						}
						
						BroadcastBossDeath(team, e.getEntity().getCustomName());
					}
					else
					{
						BroadcastBossDeath(null, e.getEntity().getCustomName());
					}
				}
			}
		}
	}

	@EventHandler
	public void onCreeperExplode(EntityExplodeEvent e)
	{
		if(e.getEntity().getCustomName() == plugin.bossf.getString("boss.3"))
		{
			e.setCancelled(true);
			e.getLocation().getWorld().createExplosion(e.getLocation(), 4.0f, true);
			plugin.bossManager.aliveBoss.remove(3);

			Bukkit.broadcastMessage(plugin.bossf.getString("boss.3") + " a fait exploser un autel de pouvoir ! ");
		}
	}


	//SLOWING SKELETON MANAGER
	//------------------------
	@EventHandler
	public void onDamageSkeleton(EntityDamageByEntityEvent e)
	{
		if(e.getEntity().getCustomName() == plugin.bossf.getString("boss.2"))
		{		
			LivingEntity livingskeleton = (LivingEntity) e.getEntity();
			Skeleton skel = (Skeleton) livingskeleton;
			skel.setTarget((LivingEntity)e.getDamager());
			if(livingskeleton.getHealth() - e.getFinalDamage() <= 0)
			{
				e.setCancelled(true);	
				if(sskeletonDead < skeletonlife)
				{
					sskeletonDead++;
					livingskeleton.setHealth(20.0f);
				}
				else
				{
					Location loc = e.getEntity().getLocation();
					e.getEntity().playEffect(EntityEffect.DEATH);
					e.getEntity().remove();
					plugin.bossManager.aliveBoss.remove(2);
					Entity killer = e.getDamager();
					Player player;
					if(killer instanceof Arrow)
					{
						Arrow arr = (Arrow) killer;
						player = (Player)arr.getShooter();
					}
					else 
					{
						player = (Player) killer;
					}
					
					if(player instanceof Player)
					{
						Team team = plugin.s.getPlayerTeam(player);
						int team_nb = team.getSize();
						ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, team_nb);
						
						player.getWorld().dropItemNaturally(loc, gapple);
						
						for(OfflinePlayer pl : team.getPlayers())
						{
							slowingPlayers.add((Player) pl);
						}
						
						BroadcastBossDeath(team, e.getEntity().getCustomName());
					}
					else
					{
						BroadcastBossDeath(null, e.getEntity().getCustomName());
					}
				}
			}
		}
	}

	@EventHandler
	public void onSkeletonFire(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() instanceof Arrow && e.getEntity() instanceof Player)
		{ 
			Arrow arrow = (Arrow) e.getDamager();
			if(arrow.getShooter() instanceof Skeleton)
			{
				Skeleton skeleton = (Skeleton) arrow.getShooter();
				if(skeleton.getCustomName() == plugin.bossf.getString("boss.2"))
				{
					LivingEntity player = (LivingEntity) e.getEntity();					
					PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 200, 0);
					player.addPotionEffect(slow);
				}
			}
		}
	}

	@EventHandler
	public void onSlowingPlayerHit(EntityDamageByEntityEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			Player player = (Player) e.getEntity();
			
			if(slowingPlayers.contains(player))
			{
				Random rdm = new Random();
				int slowChance = rdm.nextInt(100);
				
				if(slowChance < 25)
				{
					try
					{						
						LivingEntity damager = (LivingEntity) e.getDamager();					
						PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 100, 0);
						damager.addPotionEffect(slow);
					}
					catch(ClassCastException ex) {}
				}
			}
		}
	}
	
	@EventHandler
	public void onCombustSkeleton(EntityCombustEvent e)
	{
		if(e.getEntity().getCustomName() == plugin.bossf.getString("boss.2"))
		{
			e.setCancelled(true);
		}
	}
	  
	

	//GOBELINS MANAGER
	//------------------------
	@EventHandler
	public void onDamageGobelins(EntityDamageByEntityEvent e)
	{
		if(plugin.bossManager.gobelins.contains(e.getEntity()))
		{		
			LivingEntity livingzombie = (LivingEntity) e.getEntity();
			Zombie zomb = (Zombie) livingzombie;
			zomb.setTarget((LivingEntity)e.getDamager());
			if(livingzombie.getHealth() - e.getFinalDamage() <= 0)
			{
				e.setCancelled(true);
				e.getEntity().playEffect(EntityEffect.DEATH);
				e.getEntity().remove();
				plugin.bossManager.gobelins.remove(e.getEntity());
				
				if(plugin.bossManager.gobelins.isEmpty())
				{
					Location loc = e.getEntity().getLocation();
					Entity killer = e.getDamager();
					plugin.bossManager.aliveBoss.remove(5);
					Player player;
					if(killer instanceof Arrow)
					{
						Arrow arr = (Arrow) killer;
						player = (Player)arr.getShooter();
					}
					else 
					{
						player = (Player) killer;
					}
					
					if(player instanceof Player)
					{
						Team team = plugin.s.getPlayerTeam(player);
						int team_nb = team.getSize();
						ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, team_nb);
						
						player.getWorld().dropItemNaturally(loc, gapple);
						
						for(OfflinePlayer pl : team.getPlayers())
						{
							LivingEntity livingpl = (LivingEntity) pl;
							livingpl.setHealth(20.0f);
						}
						
						BroadcastBossDeath(team, e.getEntity().getCustomName());
					}
					else
					{
						BroadcastBossDeath(null, e.getEntity().getCustomName());
					}
				}
			}
		}
	}

	
	//SPIDER MANAGER
	//--------------
	@EventHandler
	public void onDamageSpider(EntityDamageByEntityEvent e)
	{
		if(e.getEntity().getCustomName() == plugin.bossf.getString("boss.4"))
		{		
			LivingEntity livingspider = (LivingEntity) e.getEntity();
			Spider spider = (Spider) livingspider;
			spider.setTarget((LivingEntity)e.getDamager());
			if(livingspider.getHealth() - e.getFinalDamage() <= 0)
			{
				e.setCancelled(true);	
				if(spiderDead < spiderlife)
				{
					spiderDead++;
					livingspider.setHealth(16.0f);
				}
				else
				{
					Location loc = e.getEntity().getLocation();
					e.getEntity().playEffect(EntityEffect.DEATH);
					e.getEntity().remove();
					plugin.bossManager.aliveBoss.remove(4);
					Entity killer = e.getDamager();
					Player player;
					if(killer instanceof Arrow)
					{
						Arrow arr = (Arrow) killer;
						player = (Player)arr.getShooter();
					}
					else 
					{
						player = (Player) killer;
					}
					
					if(player instanceof Player)
					{
						Team team = plugin.s.getPlayerTeam(player);
						int team_nb = team.getSize();
						ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, team_nb);
						
						player.getWorld().dropItemNaturally(loc, gapple);
						
						for(OfflinePlayer pl : team.getPlayers())
						{
							resistPlayers.add((Player) pl);
						}
						
						BroadcastBossDeath(team, e.getEntity().getCustomName());
					}
					else
					{
						BroadcastBossDeath(null, e.getEntity().getCustomName());
					}
				}
			}
		}
	}

	@EventHandler
	public void onSpiderAttack(EntityDamageByEntityEvent e)
	{
		if(e.getDamager().getCustomName() == plugin.bossf.getString("boss.4") && e.getEntity() instanceof Player)
		{
			Block block = e.getEntity().getLocation().getBlock();
			block.setType(Material.WEB);
		}
	}
	
	@EventHandler
	public void onResistPlayerHit(EntityDamageByEntityEvent e)
	{
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player)
		{
			Player player = (Player) e.getEntity();
			
			if(resistPlayers.contains(player))
			{
				e.setDamage(e.getDamage() * 0.75f);
			}
		}
	}
	
	
	//WITCH CONGREGATION MANAGER
	//--------------------------
	@EventHandler
	public void onDamageWitch(EntityDamageByEntityEvent e)
	{
		if(e.getEntity().getCustomName() == plugin.bossf.getString("boss.6"))
		{		
			LivingEntity livingwitch = (LivingEntity) e.getEntity();
			if(livingwitch.getHealth() - e.getFinalDamage() <= 0)
			{
				e.setCancelled(true);	
				if(witchDead < witchlife)
				{
					witchDead++;
					livingwitch.setHealth(20.0f);
				}
				else
				{
					isWitchDead = true;
					e.getEntity().playEffect(EntityEffect.DEATH);
					e.getEntity().remove();
				}
			}
		}
	}
	
	@EventHandler
	public void onNaturalDamageBlaze(EntityDamageEvent e)
	{
		if(plugin.blazes.contains(e.getEntity()) && e.getCause() != DamageCause.ENTITY_ATTACK && e.getCause() != DamageCause.PROJECTILE)
		{ 
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamageBlaze(EntityDamageByEntityEvent e)
	{
		if(plugin.blazes.contains(e.getEntity()))
		{ 
			if(!isWitchDead)
			{
				e.setCancelled(true);
			}
			else 
			{
				LivingEntity bl = (LivingEntity) e.getEntity();
				if(bl.getHealth() - e.getFinalDamage() <= 0)
				{
					plugin.blazes.remove(e.getEntity());			
					if(plugin.blazes.isEmpty())
					{
						Location loc = e.getEntity().getLocation();
						plugin.bossManager.aliveBoss.remove(6);
						Entity killer = e.getDamager();
						Player player;
						if(killer instanceof Arrow)
						{
							Arrow arr = (Arrow) killer;
							player = (Player)arr.getShooter();
						}
						else 
						{
							player = (Player) killer;
						}
						
						if(player instanceof Player)
						{
							Team team = plugin.s.getPlayerTeam(player);
							int team_nb = team.getSize();
							ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, team_nb);
							
							player.getWorld().dropItemNaturally(loc, gapple);
							
							for(OfflinePlayer pl : team.getPlayers())
							{
								firePlayers.add((Player) pl);
							}
							
							BroadcastBossDeath(team, plugin.bossf.getString("boss.6"));
						}
						else
						{
							BroadcastBossDeath(null, e.getEntity().getCustomName());
						}
					}
				}
			}
		}
	}
		
	@EventHandler
	public void onFirePlayerAttack(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() instanceof Player && firePlayers.contains(e.getDamager()))
		{
			//Player player = (Player) e.getEntity();
			Random rdm = new Random();
			int fireChance = rdm.nextInt(100);
			
			if(fireChance < 20)
			{
				LivingEntity damaged = (LivingEntity) e.getEntity();					
				damaged.setFireTicks(40);
			}
		}
	}
		
	@EventHandler
	public void onWitchThrowPotion(ProjectileLaunchEvent e)
	{
		if(e.getEntity() instanceof ThrownPotion)
		{
			ThrownPotion pot = (ThrownPotion) e.getEntity();
			if(pot.getShooter() instanceof Witch)
			{
				Witch witch = (Witch) pot.getShooter();
				if(witch.getCustomName() == plugin.bossf.getString("boss.6"))
				{
					Potion p = new Potion(PotionType.SLOWNESS, 1);
					p.setSplash(true);
					ItemStack slow = new ItemStack(Material.POTION);
					p.apply(slow);
					pot.setItem(slow);
				}
			}
		}
	}
	
	
	//BOOS DEFEAT ANNOUNCEMENT
	//------------------------
	public void BroadcastBossDeath(Team team, String bossName)
	{
		if(team != null)
		{
			Bukkit.broadcastMessage(ChatColor.WHITE + "Les " + team.getPrefix() + team.getName() + ChatColor.WHITE + " ont vaincu " + bossName + " ! ");
		}
		else
		{
			Bukkit.broadcastMessage(bossName + " est mort de manière indigne ! Personne ne profitera donc de ses récompenses ! ");
		}
		for (Player online : Bukkit.getOnlinePlayers())
        {
          online.playSound(online.getLocation(), Sound.AMBIENCE_THUNDER, 10.0F, -10.0F);
        }
	}
}
