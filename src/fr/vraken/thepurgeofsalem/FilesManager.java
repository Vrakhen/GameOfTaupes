package fr.vraken.thepurgeofsalem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FilesManager
{
	public File configf, teamf, deathf;
	private FileConfiguration config, team, death;
	ThePurgeOfSalem plugin;

	public FilesManager(ThePurgeOfSalem plugin) throws IOException, InvalidConfigurationException
	{
		this.plugin = plugin;
		createFiles();
		addConfigDefault();
		addTeamDefault();
	}

	public FileConfiguration getTeamConfig() 
	{
		return this.team;
	}

	public FileConfiguration getDeathConfig() 
	{
		return this.death;
	}

	private void createFiles() throws IOException 
	{
		configf = new File(plugin.getDataFolder(), "config.yml");
		teamf = new File(plugin.getDataFolder(), "team.yml");
		deathf = new File(plugin.getDataFolder(), "death.yml");

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

		config = new YamlConfiguration();
		team = new YamlConfiguration();
		death = new YamlConfiguration();
	}

	public void addTeamDefault() throws IOException, InvalidConfigurationException
	{
		team.load(teamf);

		team.addDefault("rose.name", "Miliciens");
		team.addDefault("cyan.name", "Forgerons");
		team.addDefault("jaune.name", "Marchands");
		team.addDefault("violette.name", "Taverniers");
		team.addDefault("verte.name", "Chasseurs");
		team.addDefault("s1.X", Integer.valueOf(500));
		team.addDefault("s1.Y", Integer.valueOf(250));
		team.addDefault("s1.Z", Integer.valueOf(500));
		team.addDefault("s2.X", Integer.valueOf(500));
		team.addDefault("s2.Y", Integer.valueOf(250));
		team.addDefault("s2.Z", Integer.valueOf(-500));
		team.addDefault("s3.X", Integer.valueOf(-500));
		team.addDefault("s3.Y", Integer.valueOf(250));
		team.addDefault("s3.Z", Integer.valueOf(500));
		team.addDefault("s4.X", Integer.valueOf(-500));
		team.addDefault("s4.Y", Integer.valueOf(250));
		team.addDefault("s4.Z", Integer.valueOf(-500));
		team.addDefault("s5.X", Integer.valueOf(0));
		team.addDefault("s5.Y", Integer.valueOf(250));
		team.addDefault("s5.Z", Integer.valueOf(0));

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
}
