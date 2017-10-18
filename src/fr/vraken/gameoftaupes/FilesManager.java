package fr.vraken.gameoftaupes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FilesManager
{
	private File configf, teamf, bossf;
    private FileConfiguration config, team, boss;
    GameOfTaupes plugin;
    
    public FilesManager(GameOfTaupes plugin) throws IOException, InvalidConfigurationException
    {
    	this.plugin = plugin;
        createFiles();
        addConfigDefault();
        addTeamDefault();
        addBossDefault();
    }

    public FileConfiguration getTeamConfig() 
    {
        return this.team;
    }

    public FileConfiguration getBossConfig() 
    {
        return this.boss;
    }

    private void createFiles() throws IOException 
    {
        configf = new File(plugin.getDataFolder(), "config.yml");
        teamf = new File(plugin.getDataFolder(), "team.yml");
        bossf = new File(plugin.getDataFolder(), "boss.yml");

        if (!configf.exists()) 
        {
            configf.createNewFile();
        }
        if (!teamf.exists()) 
        {
            teamf.createNewFile();
        }
        if (!bossf.exists()) 
        {
            bossf.createNewFile();
        }

        config = new YamlConfiguration();
        team = new YamlConfiguration();
        boss = new YamlConfiguration();
    }
    
    public void addTeamDefault() throws IOException, InvalidConfigurationException
    {
        team.load(teamf);
        
        team.addDefault("rose.name", "rose");
        team.addDefault("rose.X", Integer.valueOf(500));
        team.addDefault("rose.Y", Integer.valueOf(250));
        team.addDefault("rose.Z", Integer.valueOf(500));
        team.addDefault("cyan.name", "cyan");
        team.addDefault("cyan.X", Integer.valueOf(500));
        team.addDefault("cyan.Y", Integer.valueOf(250));
        team.addDefault("cyan.Z", Integer.valueOf(-500));
        team.addDefault("jaune.name", "jaune");
        team.addDefault("jaune.X", Integer.valueOf(-500));
        team.addDefault("jaune.Y", Integer.valueOf(250));
        team.addDefault("jaune.Z", Integer.valueOf(500));
        team.addDefault("violette.name", "violette");
        team.addDefault("violette.X", Integer.valueOf(-500));
        team.addDefault("violette.Y", Integer.valueOf(250));
        team.addDefault("violette.Z", Integer.valueOf(-500));
        team.addDefault("verte.name", "verte");
        team.addDefault("verte.X", Integer.valueOf(0));
        team.addDefault("verte.Y", Integer.valueOf(250));
        team.addDefault("verte.Z", Integer.valueOf(0));
        
        team.options().copyDefaults(true);
        team.save(teamf);
    }
    
    public void addBossDefault() throws FileNotFoundException, IOException, InvalidConfigurationException
    {
        boss.load(bossf);

        boss.addDefault("boss.1", "Gothmog");
        boss.addDefault("boss.2", "Lurtz");
        boss.addDefault("boss.3", "le berzerker");
        boss.addDefault("boss.4", "Arachne");
        boss.addDefault("boss.5", "les gobelins");
        boss.addDefault("boss.6", "Saroumane");

        boss.addDefault("temple1.X", Integer.valueOf(250));
        boss.addDefault("temple1.Y", Integer.valueOf(70));
        boss.addDefault("temple1.Z", Integer.valueOf(250));
        boss.addDefault("temple2.X", Integer.valueOf(250));
        boss.addDefault("temple2.Y", Integer.valueOf(70));
        boss.addDefault("temple2.Z", Integer.valueOf(250));
        boss.addDefault("temple3.X", Integer.valueOf(250));
        boss.addDefault("temple3.Y", Integer.valueOf(70));
        boss.addDefault("temple3.Z", Integer.valueOf(250));
        boss.addDefault("temple4.X", Integer.valueOf(250));
        boss.addDefault("temple4.Y", Integer.valueOf(70));
        boss.addDefault("temple4.Z", Integer.valueOf(250));
        boss.addDefault("temple5.X", Integer.valueOf(250));
        boss.addDefault("temple5.Y", Integer.valueOf(70));
        boss.addDefault("temple5.Z", Integer.valueOf(250));
        boss.addDefault("temple6.X", Integer.valueOf(250));
        boss.addDefault("temple6.Y", Integer.valueOf(70));
        boss.addDefault("temple6.Z", Integer.valueOf(250));

        boss.options().copyDefaults(true);
        boss.save(bossf);
    }

    public void addConfigDefault() throws FileNotFoundException, IOException, InvalidConfigurationException
    {
        config.load(configf);

        plugin.getConfig().addDefault("worldborder.size", Integer.valueOf(1500));
        plugin.getConfig().addDefault("worldborder.finalsize", Integer.valueOf(100));
        plugin.getConfig().addDefault("worldborder.retractafter", Integer.valueOf(100));
        plugin.getConfig().addDefault("worldborder.episodestorestract", Integer.valueOf(1));
        plugin.getConfig().addDefault("potions.allowglowstone", Boolean.valueOf(false));
        plugin.getConfig().addDefault("lobby.world", "lobby");
        plugin.getConfig().addDefault("lobby.X", Integer.valueOf(0));
        plugin.getConfig().addDefault("lobby.Y", Integer.valueOf(100));
        plugin.getConfig().addDefault("lobby.Z", Integer.valueOf(0));
        plugin.getConfig().addDefault("chest.X", Integer.valueOf(0));
        plugin.getConfig().addDefault("chest.Y", Integer.valueOf(62));
        plugin.getConfig().addDefault("chest.Z", Integer.valueOf(0));
        plugin.getConfig().addDefault("potions.regeneration", Boolean.valueOf(false));
        plugin.getConfig().addDefault("potions.strength", Boolean.valueOf(false));
        plugin.getConfig().addDefault("world", "world");
        plugin.getConfig().addDefault("world_nether", "world_nether");
        plugin.getConfig().addDefault("options.nodamagetime", Integer.valueOf(20));
        plugin.getConfig().addDefault("options.timecycle", Boolean.valueOf(false));
        plugin.getConfig().addDefault("options.minplayers", Integer.valueOf(20));
        plugin.getConfig().addDefault("options.pvptime", Integer.valueOf(20));
        plugin.getConfig().addDefault("options.cooldown", Boolean.valueOf(false));
        plugin.getConfig().addDefault("options.playersperteam", Integer.valueOf(4));
        plugin.getConfig().addDefault("options.settaupesafter", Integer.valueOf(30));
        plugin.getConfig().addDefault("options.forcereveal", Integer.valueOf(70));
        plugin.getConfig().addDefault("options.supertaupe", Boolean.valueOf(false));
        plugin.getConfig().addDefault("options.setsupertaupesafter", Integer.valueOf(50));
        plugin.getConfig().addDefault("options.superreveal", Integer.valueOf(90));
        
        plugin.getConfig().addDefault("duelspawn1.X", Integer.valueOf(0));
        plugin.getConfig().addDefault("duelspawn1.Y", Integer.valueOf(250));
        plugin.getConfig().addDefault("duelspawn1.Z", Integer.valueOf(0));
        plugin.getConfig().addDefault("duelspawn2.X", Integer.valueOf(0));
        plugin.getConfig().addDefault("duelspawn2.Y", Integer.valueOf(250));
        plugin.getConfig().addDefault("duelspawn2.Z", Integer.valueOf(0));
        
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
    }
}
