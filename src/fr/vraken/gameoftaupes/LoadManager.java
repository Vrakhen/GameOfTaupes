package fr.vraken.gameoftaupes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
		game.load(gamef);
		
		//Game infos
		plugin.gameStarted = true;
		plugin.gameState = game.getInt("gamestate");
		plugin.hasChangedGS = false;
		plugin.tmpBorder = game.getInt("border");
		plugin.minute = game.getInt("minute");
		plugin.retract = game.getBoolean("retract");
		plugin.finalretract = game.getBoolean("finalretract");
		
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
	
	@SuppressWarnings("unchecked")
	public void loadPlayerInfos() throws FileNotFoundException, IOException, InvalidConfigurationException
	{
		player.load(playerf);
		
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(player.getString(p.getName() + ".name") == null)
			{
				p.kickPlayer("Vous n'étiez pas présent à la dernière partie ! ");
				continue;
			}

			p.setGameMode(GameMode.SURVIVAL);
			p.setHealth(player.getDouble(p.getName() + ".health"));
			p.setHealth(player.getInt(p.getName() + ".food"));
			p.setHealth(player.getDouble(p.getName() + ".exp"));
			plugin.s.getTeam(player.getString(p.getName() + "team")).addPlayer(p);

	        ItemStack[] content = ((List<ItemStack>) player.get("inventory.armor")).toArray(new ItemStack[0]);
	        p.getInventory().setArmorContents(content);
	        content = ((List<ItemStack>) player.get("inventory.content")).toArray(new ItemStack[0]);
	        p.getInventory().setContents(content);
			
			p.teleport(new Location(Bukkit.getWorld(plugin.getConfig().get("world").toString()), 
					player.getDouble(p.getName() + ".location.X"),
					player.getDouble(p.getName() + ".location.Y"),
					player.getDouble(p.getName() + ".location.Z")));
			
			plugin.playersAlive.add(p.getUniqueId());
			
			for(int i = 0; i < plugin.taupes.size(); ++i)
			{
				if(plugin.taupes.get(i).contains(p.getUniqueId()))
				{
					plugin.aliveTaupes.add(p.getUniqueId());
					if(plugin.supertaupes.get(i) == p.getUniqueId())
					{
						plugin.aliveSupertaupes.add(p.getUniqueId());
					}
				}
			}
		}
	}
}
