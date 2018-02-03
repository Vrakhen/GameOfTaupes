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
import org.apache.commons.io.FileUtils;

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
		playerf.delete();
		playerf.createNewFile();
		
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
			playerInfo.put(p.getName() + ".name", p.getName());
			playerInfo.put(p.getName() + ".team", plugin.s.getPlayerTeam(p).getName());
			playerInfo.put(p.getName() + ".location.X", p.getLocation().getX());
			playerInfo.put(p.getName() + ".location.Y", p.getLocation().getY());
			playerInfo.put(p.getName() + ".location.Z", p.getLocation().getZ());

			playerInfo.put(p.getName() + ".health", p.getHealth());
			playerInfo.put(p.getName() + ".food", p.getFoodLevel());
			playerInfo.put(p.getName() + ".level", p.getLevel());

			playerInfo.put(p.getName() + ".armor", p.getInventory().getArmorContents());
			playerInfo.put(p.getName() + ".inventory", p.getInventory().getContents());
			
			player.addDefaults(playerInfo);
		}
		
		player.options().copyDefaults(true);
		player.save(playerf);
	}
	
	public void saveGameInfos() throws FileNotFoundException, IOException, InvalidConfigurationException
	{
		gamef.delete();
		gamef.createNewFile();
		
		game.load(gamef);
		HashMap<String, Object> gameInfo = new HashMap<String, Object>();
						
		//Taupes infos
		//------------
		gameInfo.put("taupeSetup", plugin.taupessetup);
		
		//Taupes
		String s = "";
		for(Integer key : plugin.taupes.keySet())
		{
			s += key + ":";
			for(UUID uid : plugin.taupes.get(key))
			{
				s += uid.toString() + "/";						
			}
			if(!plugin.taupes.get(key).isEmpty())
			{
				s = s.substring(0, s.length() - 1);	
			}
			s += ",";
		}
		if(!plugin.taupes.isEmpty())
		{
			s = s.substring(0, s.length() - 1);	
		}	
		gameInfo.put("taupes", s);
		
		//Taupes teams
		s = "";
		for(Integer key : plugin.taupesteam.keySet())
		{
			s += key + ":" + plugin.taupesteam.get(key).getName();
			s += ",";
		}
		if(!plugin.taupesteam.isEmpty())
		{
			s = s.substring(0, s.length() - 1);	
		}	
		gameInfo.put("taupesTeam", s);
		
		//Taupes revealed
		s = "";
		for(UUID uid : plugin.showedtaupes)
		{
			s += uid;
			s += ",";
		}
		if(!plugin.showedtaupes.isEmpty())
		{
			s = s.substring(0, s.length() - 1);	
		}	
		gameInfo.put("taupesShowed", s);
		
		//Taupes alive
		s = "";
		for(UUID uid : plugin.aliveTaupes)
		{
			s += uid;
			s += ",";
		}
		if(!plugin.aliveTaupes.isEmpty())
		{
			s = s.substring(0, s.length() - 1);	
		}	
		gameInfo.put("taupesAlive", s);
		
		//Taupes claimed
		s = "";
		for(UUID uid : plugin.claimedtaupes)
		{
			s += uid;
			s += ",";
		}
		if(!plugin.claimedtaupes.isEmpty())
		{
			s = s.substring(0, s.length() - 1);	
		}	
		gameInfo.put("taupesClaimed", s);
		
		//Claimed kits
		s = "";
		for(Integer key : plugin.claimedkits.keySet())
		{
			s += key + ":";
			for(Integer kit : plugin.claimedkits.get(key))
			{
				s += kit + "/";						
			}
			s = s.substring(0, s.length() - 1);
			s += ",";
		}
		if(!plugin.claimedkits.isEmpty())
		{
			s = s.substring(0, s.length() - 1);	
		}
		gameInfo.put("kitsClaimed", s);
		
		//Taupes teams dead
		s = "";
		for(Integer key : plugin.isTaupesTeamDead.keySet())
		{
			s += key + ":" + plugin.isTaupesTeamDead.get(key);
			s += ",";
		}
		if(!plugin.isTaupesTeamDead.isEmpty())
		{
			s = s.substring(0, s.length() - 1);	
		}
		gameInfo.put("isTaupesTeamDead", s);
		
		
		//Supertaupes infos
		//-----------------
		gameInfo.put("supertaupeSetup", plugin.supertaupessetup);
		
		//Supertaupes
		s = "";
		for(Integer key : plugin.supertaupes.keySet())
		{
			s += key + ":" + plugin.supertaupes.get(key);
			s += ",";
		}
		if(!plugin.supertaupes.isEmpty())
		{
			s = s.substring(0, s.length() - 1);	
		}	
		gameInfo.put("supertaupes", s);
		
		//Supertaupes teams
		s = "";
		for(Integer key : plugin.supertaupesteam.keySet())
		{
			s += key + ":" + plugin.supertaupesteam.get(key).getName();
			s += ",";
		}
		if(!plugin.supertaupesteam.isEmpty())
		{
			s = s.substring(0, s.length() - 1);	
		}
		gameInfo.put("supertaupesTeam", s);
		
		//Supertaupes revealed
		s = "";
		for(UUID uid : plugin.showedsupertaupes)
		{
			s += uid;
			s += ",";
		}
		if(!plugin.showedsupertaupes.isEmpty())
		{
			s = s.substring(0, s.length() - 1);	
		}	
		gameInfo.put("supertaupesShowed", s);
		
		//Supertaupes alive
		s = "";
		for(UUID uid : plugin.aliveSupertaupes)
		{
			s += uid;
			s += ",";
		}
		if(!plugin.aliveSupertaupes.isEmpty())
		{
			s = s.substring(0, s.length() - 1);	
		}	
		gameInfo.put("supertaupesAlive", s);
		
		//Supertaupes teams dead
		s = "";
		for(Integer key : plugin.isSupertaupeDead.keySet())
		{
			s += key + ":" + plugin.isSupertaupeDead.get(key);
			s += ",";
		}
		if(!plugin.isSupertaupeDead.isEmpty())
		{
			s = s.substring(0, s.length() - 1);	
		}
		gameInfo.put("isSupertaupeDead", s);
		
		
		//Scoreboard
		//----------
		gameInfo.put("episode", plugin.episode);
		gameInfo.put("gamestate", plugin.gameState);
		gameInfo.put("minute", plugin.minute + 1);
		gameInfo.put("border", plugin.tmpBorder);
		gameInfo.put("retract", plugin.retract);
		gameInfo.put("finalretract", plugin.finalretract);
			
		
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
	    	   //System.out.println("Directory copied from " + src + "  to " + dest);
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
	    	//System.out.println("File copied from " + src + " to " + dest);
	    }
    }
}
