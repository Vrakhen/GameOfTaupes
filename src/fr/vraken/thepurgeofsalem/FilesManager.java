package fr.vraken.thepurgeofsalem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FilesManager
{
	public File configf, teamf, deathf, lootf;
	private FileConfiguration config, team, death, loot;
	ThePurgeOfSalem plugin;

	public FilesManager(ThePurgeOfSalem plugin) throws IOException, InvalidConfigurationException
	{
		this.plugin = plugin;
		createFiles();
		addConfigDefault();
		addTeamDefault();
		addLootDefault();
	}

	public FileConfiguration getTeamConfig() 
	{
		return this.team;
	}

	public FileConfiguration getDeathConfig() 
	{
		return this.death;
	}

	public FileConfiguration getLootConfig() 
	{
		return this.loot;
	}

	private void createFiles() throws IOException 
	{
		configf = new File(plugin.getDataFolder(), "config.yml");
		teamf = new File(plugin.getDataFolder(), "team.yml");
		deathf = new File(plugin.getDataFolder(), "death.yml");
		lootf = new File(plugin.getDataFolder(), "loot.yml");

		if (!configf.exists()) 
		{
			configf.createNewFile();
		}
		if (!teamf.exists()) 
		{
			teamf.createNewFile();
		}
		if (!deathf.exists()) 
		{
			deathf.createNewFile();
		}
		if (!lootf.exists()) 
		{
			lootf.createNewFile();
		}

		config = new YamlConfiguration();
		team = new YamlConfiguration();
		death = new YamlConfiguration();
		loot = new YamlConfiguration();
	}

	public void addTeamDefault() throws IOException, InvalidConfigurationException
	{
		team.load(teamf);

		team.addDefault("rose.name", "Miliciens");
		team.addDefault("cyan.name", "Forgerons");
		team.addDefault("jaune.name", "Marchands");
		team.addDefault("violette.name", "Taverniers");
		team.addDefault("verte.name", "Chasseurs");
		team.addDefault("s0.X", Integer.valueOf(500));
		team.addDefault("s0.Y", Integer.valueOf(250));
		team.addDefault("s0.Z", Integer.valueOf(500));
		team.addDefault("s1.X", Integer.valueOf(500));
		team.addDefault("s1.Y", Integer.valueOf(250));
		team.addDefault("s1.Z", Integer.valueOf(-500));
		team.addDefault("s2.X", Integer.valueOf(-500));
		team.addDefault("s2.Y", Integer.valueOf(250));
		team.addDefault("s2.Z", Integer.valueOf(500));
		team.addDefault("s3.X", Integer.valueOf(-500));
		team.addDefault("s3.Y", Integer.valueOf(250));
		team.addDefault("s3.Z", Integer.valueOf(-500));
		team.addDefault("s4.X", Integer.valueOf(0));
		team.addDefault("s4.Y", Integer.valueOf(250));
		team.addDefault("s4.Z", Integer.valueOf(0));

		team.options().copyDefaults(true);
		team.save(teamf);
	}

	public void addConfigDefault() throws FileNotFoundException, IOException, InvalidConfigurationException
	{
		config.load(configf);

		plugin.getConfig().addDefault("worldborder.size", Integer.valueOf(1500));
		plugin.getConfig().addDefault("lobby.world", "lobby");
		plugin.getConfig().addDefault("lobby.X", Integer.valueOf(0));
		plugin.getConfig().addDefault("lobby.Y", Integer.valueOf(100));
		plugin.getConfig().addDefault("lobby.Z", Integer.valueOf(0));
		plugin.getConfig().addDefault("lobby.respawnX", Integer.valueOf(0));
		plugin.getConfig().addDefault("lobby.respawnY", Integer.valueOf(100));
		plugin.getConfig().addDefault("lobby.respawnZ", Integer.valueOf(0));
		plugin.getConfig().addDefault("lobby.meetupX", Integer.valueOf(0));
		plugin.getConfig().addDefault("lobby.meetupY", Integer.valueOf(100));
		plugin.getConfig().addDefault("lobby.meetupZ", Integer.valueOf(0));
		plugin.getConfig().addDefault("world", "world");
		plugin.getConfig().addDefault("options.timecycle", Boolean.valueOf(false));
		plugin.getConfig().addDefault("options.minplayers", Integer.valueOf(20));
		plugin.getConfig().addDefault("options.cooldown", Boolean.valueOf(false));
		plugin.getConfig().addDefault("options.playersperteam", Integer.valueOf(4));
		plugin.getConfig().addDefault("options.nodamagetime", Integer.valueOf(20));
		plugin.getConfig().addDefault("options.pvptime", Integer.valueOf(20));
		plugin.getConfig().addDefault("options.settaupesafter", Integer.valueOf(30));
		plugin.getConfig().addDefault("options.forcereveal", Integer.valueOf(70));
		plugin.getConfig().addDefault("options.superreveal", Integer.valueOf(90));
		plugin.getConfig().addDefault("options.graal", Integer.valueOf(100));
		plugin.getConfig().addDefault("options.supertaupelifetime", Integer.valueOf(5));
		plugin.getConfig().addDefault("options.graaltimetocapture", Integer.valueOf(90));

		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
	}
	
	public void addLootDefault() throws FileNotFoundException, IOException, InvalidConfigurationException
	{
		loot.load(lootf);;
		
		loot.addDefault("items.sword", Integer.valueOf(0));
		loot.addDefault("items.bow", Integer.valueOf(0));
		loot.addDefault("items.arrow", Integer.valueOf(0));
		loot.addDefault("items.boots", Integer.valueOf(0));
		loot.addDefault("items.leggings", Integer.valueOf(0));
		loot.addDefault("items.chestplate", Integer.valueOf(0));
		loot.addDefault("items.helmet", Integer.valueOf(0));
		loot.addDefault("items.axe", Integer.valueOf(0));
		loot.addDefault("items.bucket", Integer.valueOf(0));
		loot.addDefault("items.tnt", Integer.valueOf(0));
		loot.addDefault("items.flintnsteel", Integer.valueOf(0));
		loot.addDefault("items.potion", Integer.valueOf(0));
		loot.addDefault("items.golden_apple", Integer.valueOf(0));
		loot.addDefault("items.pickaxe", Integer.valueOf(0));
		
		
		loot.addDefault("hand.type.wood", Integer.valueOf(0));
		loot.addDefault("hand.type.stone", Integer.valueOf(0));
		loot.addDefault("hand.type.iron", Integer.valueOf(0));
		loot.addDefault("hand.type.diamond", Integer.valueOf(0));
		
		loot.addDefault("hand.sword.sharpness", Integer.valueOf(0));
		loot.addDefault("hand.sword.knockback", Integer.valueOf(0));
		loot.addDefault("hand.sword.unbreaking", Integer.valueOf(0));
		loot.addDefault("hand.sword.bane_of_anthropods", Integer.valueOf(0));
		loot.addDefault("hand.sword.smite", Integer.valueOf(0));
		loot.addDefault("hand.sword.fire_aspect", Integer.valueOf(0));
		
		loot.addDefault("hand.tool.efficiency", Integer.valueOf(0));
		loot.addDefault("hand.tool.unbreaking", Integer.valueOf(0));
		
		
		loot.addDefault("bow.power", Integer.valueOf(0));
		loot.addDefault("bow.punch", Integer.valueOf(0));
		loot.addDefault("bow.unbreaking", Integer.valueOf(0));
		loot.addDefault("bow.infinity", Integer.valueOf(0));
		loot.addDefault("bow.flame", Integer.valueOf(0));
		
		
		loot.addDefault("armor.type.leather", Integer.valueOf(0));
		loot.addDefault("armor.type.chain", Integer.valueOf(0));
		loot.addDefault("armor.type.iron", Integer.valueOf(0));
		loot.addDefault("armor.type.diamond", Integer.valueOf(0));
		
		loot.addDefault("armor.enchant.protection", Integer.valueOf(0));
		loot.addDefault("armor.enchant.projectile_protection", Integer.valueOf(0));
		loot.addDefault("armor.enchant.blast_protection", Integer.valueOf(0));
		loot.addDefault("armor.enchant.unbreaking", Integer.valueOf(0));
		loot.addDefault("armor.enchant.fire_resistance", Integer.valueOf(0));
		
		
		loot.addDefault("bucket.water", Integer.valueOf(0));
		loot.addDefault("bucket.lava", Integer.valueOf(0));
		loot.addDefault("bucket.milk", Integer.valueOf(0));
		
		
		loot.addDefault("enchanted.0", Integer.valueOf(0));
		loot.addDefault("enchanted.1", Integer.valueOf(0));
		loot.addDefault("enchanted.2", Integer.valueOf(0));
		loot.addDefault("enchanted.3", Integer.valueOf(0));
		
		loot.addDefault("enchant_level.1", Integer.valueOf(0));
		loot.addDefault("enchant_level.2", Integer.valueOf(0));
		loot.addDefault("enchant_level.3", Integer.valueOf(0));
		loot.addDefault("enchant_level.4", Integer.valueOf(0));
		loot.addDefault("enchant_level.5", Integer.valueOf(0));
		
		
		loot.addDefault("potion.type.speed", Integer.valueOf(0));
		loot.addDefault("potion.type.slowness", Integer.valueOf(0));
		loot.addDefault("potion.type.regeneration", Integer.valueOf(0));
		loot.addDefault("potion.type.strength", Integer.valueOf(0));
		loot.addDefault("potion.type.instant_health", Integer.valueOf(0));
		loot.addDefault("potion.type.instant_damage", Integer.valueOf(0));
		loot.addDefault("potion.type.fire_resistance", Integer.valueOf(0));
		loot.addDefault("potion.type.invisibility", Integer.valueOf(0));
		loot.addDefault("potion.type.weakness", Integer.valueOf(0));
		loot.addDefault("potion.type.poison", Integer.valueOf(0));
		loot.addDefault("potion.type.absorption", Integer.valueOf(0));
		
		loot.addDefault("potion.splash.true", Integer.valueOf(0));
		loot.addDefault("potion.splash.false", Integer.valueOf(0));
		
		loot.addDefault("potion.extended.true", Integer.valueOf(0));
		loot.addDefault("potion.extended.false", Integer.valueOf(0));
		
		loot.addDefault("potion.level.1", Integer.valueOf(0));
		loot.addDefault("potion.level.2", Integer.valueOf(0));

		loot.options().copyDefaults(true);
		loot.save(lootf);
	}
}
