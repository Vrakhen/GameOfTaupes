package fr.vraken.gameoftaupes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LoadManager 
{
	GameOfTaupes plugin;
	public File playerf, gamef, savePath;
	private FileConfiguration player, game;
	
	LoadManager(GameOfTaupes got) throws IOException
	{
		plugin = got;
		savePath = new File(plugin.getDataFolder(), "save");
		
		playerf = new File(savePath, "players.yml");
		gamef = new File(savePath, "game.yml");

		player = new YamlConfiguration();
		game = new YamlConfiguration();
	}
	
	public void loadGameInfos() throws FileNotFoundException, IOException, InvalidConfigurationException
	{		
		player.load(playerf);
		game.load(gamef);
		
		//Game infos
		plugin.gameStarted = true;
		plugin.gameState = game.getInt("gamestate");
		plugin.hasChangedGS = false;
		plugin.tmpBorder = game.getInt("border");
		plugin.minute = game.getInt("minute");
		
		//Taupes infos
		plugin.taupessetup = game.getBoolean("taupeSetup");
		
		String[] pairs = game.getString("taupes").split(",");
		for (int i=0; i < pairs.length; i++) 
		{
		    String[] keyValue = pairs[i].split(":");
		    ArrayList<UUID> taupes = new ArrayList<UUID>();
		    String[] uids = keyValue[1].split("/");
		    for(int j = 0; j < uids.length; ++j)
		    {
		    	taupes.add(UUID.fromString(uids[j]));
		    }
		    plugin.taupes.put(Integer.parseInt(keyValue[0]), taupes);
		    taupes.clear();
		}

		pairs = game.getString("taupesTeam").split(",");
		for (int i=0; i < pairs.length; i++) 
		{
		    String[] keyValue = pairs[i].split(":");
		    plugin.taupesteam.put(Integer.parseInt(keyValue[0]), plugin.s.getTeam(keyValue[1]));
		}

		pairs = game.getString("taupesAlive").split(",");
		for (int i=0; i < pairs.length; i++) 
		{
			plugin.aliveTaupes.add(UUID.fromString(pairs[i]));
		}

		pairs = game.getString("taupesShowed").split(",");
		for (int i=0; i < pairs.length; i++) 
		{
			plugin.showedtaupes.add(UUID.fromString(pairs[i]));
		}

		pairs = game.getString("taupesClaimed").split(",");
		for (int i=0; i < pairs.length; i++) 
		{
			plugin.claimedtaupes.add(UUID.fromString(pairs[i]));
		}

		pairs = game.getString("isTaupesTeamDead").split(",");
		for (int i=0; i < pairs.length; i++) 
		{
		    String[] keyValue = pairs[i].split(":");
		    plugin.isTaupesTeamDead.put(Integer.parseInt(keyValue[0]), Boolean.valueOf(keyValue[1]));
		}

		//Supertaupes infos
		plugin.supertaupessetup = game.getBoolean("supertaupeSetup");

		pairs = game.getString("supertaupes").split(",");
		for (int i=0; i < pairs.length; i++) 
		{
		    String[] keyValue = pairs[i].split(":");
		    plugin.supertaupes.put(Integer.parseInt(keyValue[0]), UUID.fromString(keyValue[1]));
		}

		pairs = game.getString("supertaupesTeam").split(",");
		for (int i=0; i < pairs.length; i++) 
		{
		    String[] keyValue = pairs[i].split(":");
		    plugin.supertaupesteam.put(Integer.parseInt(keyValue[0]), plugin.s.getTeam(keyValue[1]));
		}

		pairs = game.getString("supertaupesAlive").split(",");
		for (int i=0; i < pairs.length; i++) 
		{
			plugin.aliveSupertaupes.add(UUID.fromString(pairs[i]));
		}

		pairs = game.getString("supertaupesShowed").split(",");
		for (int i=0; i < pairs.length; i++) 
		{
			plugin.showedsupertaupes.add(UUID.fromString(pairs[i]));
		}

		pairs = game.getString("isSupertaupeDead").split(",");
		for (int i=0; i < pairs.length; i++) 
		{
		    String[] keyValue = pairs[i].split(":");
		    plugin.isSupertaupeDead.put(Integer.parseInt(keyValue[0]), Boolean.valueOf(keyValue[1]));
		}
	}
	
	public void loadPlayerInfos()
	{
		
	}
}
