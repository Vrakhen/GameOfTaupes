package fr.vraken.gameoftaupes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.apache.commons.io.FileUtils;
import fr.vraken.gameoftaupes.InventoryToBase64;

public class SaveManager 
{
	GameOfTaupes plugin;
	public File playerf, gamef, savePath;
	private FileConfiguration player, game;
	
	SaveManager(GameOfTaupes got) throws IOException
	{
		plugin = got;
		createFiles();
	}
	
	private void createFiles() throws IOException 
	{
		savePath = new File(plugin.getDataFolder(), "save");
		if(!savePath.exists())
		{
			savePath.mkdir();
		}
		
		playerf = new File(savePath, "players.yml");
		gamef = new File(savePath, "game.yml");
		
		if (!playerf.exists()) 
		{
			playerf.createNewFile();
		}
		if (!gamef.exists()) 
		{
			gamef.createNewFile();
		}

		player = new YamlConfiguration();
		game = new YamlConfiguration();
	}
	
	public void savePlayersInfos() throws FileNotFoundException, IOException, InvalidConfigurationException
	{
		player.load(playerf);
		HashMap<String, Object> playerInfo = new HashMap<String, Object>();
		Player p;
		
		for(UUID uid : plugin.playersAlive)
		{
			p = Bukkit.getPlayer(uid);
			
			if(!p.isOnline())
			{
				continue;
			}
			
			playerInfo.clear();
			playerInfo.put(p.getName() + ".location.X", p.getLocation().getX());
			playerInfo.put(p.getName() + ".location.Y", p.getLocation().getY());
			playerInfo.put(p.getName() + ".location.Z", p.getLocation().getZ());

			playerInfo.put(p.getName() + ".health", p.getHealth());
			playerInfo.put(p.getName() + ".food", p.getFoodLevel());
			playerInfo.put(p.getName() + ".exp", p.getExp());

			for(PotionEffect pot : p.getActivePotionEffects())
			{
				playerInfo.put(p.getName() + ".potion." + pot.getType().getName(), pot.getAmplifier());
				playerInfo.put(p.getName() + ".potion." + pot.getType().getName(), pot.getDuration());
			}

			playerInfo.put(p.getName() + ".inventory", InventoryToBase64.toBase64(p.getInventory()));
			
			player.addDefaults(playerInfo);
		}
		
		player.options().copyDefaults(true);
		player.save(playerf);
	}
	
	public void saveGameInfos() throws FileNotFoundException, IOException, InvalidConfigurationException
	{
		game.load(gamef);
		HashMap<String, Object> gameInfo = new HashMap<String, Object>();
						
		gameInfo.put("playersAlive", plugin.playersAlive.toString());
		
		//Taupes infos
		gameInfo.put("taupeSetup", plugin.taupessetup);
		
		String s = "";
		for(Integer key : plugin.taupes.keySet())
		{
			s += key + ":";
			for(UUID uid : plugin.taupes.get(key))
			{
				s += uid.toString() + "/";						
			}
			s = s.substring(0, s.length() - 1);
			s += ",";
		}
		s = s.substring(0, s.length() - 1);		
		gameInfo.put("taupes", s);
		
		s = "";
		for(Integer key : plugin.taupesteam.keySet())
		{
			s += key + ":" + plugin.taupesteam.get(key).getName();
			s += ",";
		}
		s = s.substring(0, s.length() - 1);		
		gameInfo.put("taupesTeam", s);
		
		gameInfo.put("taupesAlive", plugin.aliveTaupes.toString());
		gameInfo.put("taupesShowed", plugin.showedtaupes.toString());
		gameInfo.put("taupesClaimed", plugin.claimedtaupes.toString());
		
		s = "";
		for(Integer key : plugin.claimedkits.keySet())
		{
			s += key + ":";
			for(Integer kit : plugin.claimedkits.get(key))
			{
				s += kit.toString() + "/";						
			}
			s = s.substring(0, s.length() - 1);
			s += ",";
		}
		s = s.substring(0, s.length() - 1);		
		gameInfo.put("kitsClaimed", s);
		
		gameInfo.put("isTaupesTeamDead", plugin.isTaupesTeamDead.toString());
		
		//Supertaupes infos
		gameInfo.put("supertaupeSetup", plugin.supertaupessetup);
		gameInfo.put("supertaupes", plugin.supertaupes.toString());
		
		s = "";
		for(Integer key : plugin.supertaupesteam.keySet())
		{
			s += key + ":" + plugin.supertaupesteam.get(key).getName();
			s += ",";
		}
		s = s.substring(0, s.length() - 1);		
		gameInfo.put("supertaupesTeam", s);		

		gameInfo.put("supertaupesAlive", plugin.aliveSupertaupes.toString());
		gameInfo.put("supertaupesShowed", plugin.showedsupertaupes.toString());
		gameInfo.put("isSupertaupeDead", plugin.isSupertaupeDead.toString());
		
		//Scoreboard
		gameInfo.put("episode", plugin.episode);
		gameInfo.put("gamestate", plugin.gameState);
		gameInfo.put("minute", plugin.minute);
		gameInfo.put("border", plugin.tmpBorder);
			
		game.addDefaults(gameInfo);
			
		game.options().copyDefaults(true);
		game.save(gamef);
	}
	
	public void copyMapFolder(File src, File dest) throws IOException
	{
	    if(src.isDirectory())
	    {
	    	//if directory not exists, create it
	    	if(!dest.exists())
	    	{
	    	   dest.mkdir();
	    	   System.out.println("Directory copied from " + src + "  to " + dest);
	    	}
	    	else
	    	{
	    		FileUtils.cleanDirectory(dest);
	    	}

	    	//list all the directory contents
	    	String files[] = src.list();

	    	for (String file : files) 
	    	{
	    	   //construct the src and dest file structure
	    	   File srcFile = new File(src, file);
	    	   File destFile = new File(dest, file);
	    	   //recursive copy
	    	   copyMapFolder(srcFile,destFile);
	    	}
    	}
	    else
	    {
    		//if file, then copy it
    		//Use bytes stream to support all file types
    		InputStream in = new FileInputStream(src);
   	        OutputStream out = new FileOutputStream(dest);

   	        byte[] buffer = new byte[1024];
	    	
   	        int length;
   	        //copy the file content in bytes
   	        while ((length = in.read(buffer)) > 0)
   	        {
   	    	   out.write(buffer, 0, length);
   	        }

   	        in.close();
	    	out.close();
	    	System.out.println("File copied from " + src + " to " + dest);
	    }
    }
}
