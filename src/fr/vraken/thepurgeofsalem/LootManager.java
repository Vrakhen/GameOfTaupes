package fr.vraken.thepurgeofsalem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class LootManager 
{
	ThePurgeOfSalem plugin;
	FilesManager files;
	
	HashMap<String, ArrayList<Integer>> items = new HashMap<String, ArrayList<Integer>>();
	HashMap<String, ArrayList<Integer>> hand_types = new HashMap<String, ArrayList<Integer>>();
	HashMap<String, ArrayList<Integer>> sword_enchants = new HashMap<String, ArrayList<Integer>>();
	HashMap<String, ArrayList<Integer>> tool_enchants = new HashMap<String, ArrayList<Integer>>();
	HashMap<String, ArrayList<Integer>> bow_enchants = new HashMap<String, ArrayList<Integer>>();
	HashMap<String, ArrayList<Integer>> armor_types = new HashMap<String, ArrayList<Integer>>();
	HashMap<String, ArrayList<Integer>> armor_enchants = new HashMap<String, ArrayList<Integer>>();
	HashMap<String, ArrayList<Integer>> bucket_types = new HashMap<String, ArrayList<Integer>>();
	HashMap<String, ArrayList<Integer>> enchant_number = new HashMap<String, ArrayList<Integer>>();
	HashMap<String, ArrayList<Integer>> enchant_level = new HashMap<String, ArrayList<Integer>>();
	HashMap<String, ArrayList<Integer>> potion_types = new HashMap<String, ArrayList<Integer>>();
	HashMap<String, ArrayList<Integer>> potion_splash = new HashMap<String, ArrayList<Integer>>();
	HashMap<String, ArrayList<Integer>> potion_extended = new HashMap<String, ArrayList<Integer>>();
	HashMap<String, ArrayList<Integer>> potion_level = new HashMap<String, ArrayList<Integer>>();
	
	public LootManager(ThePurgeOfSalem plugin, FilesManager filesManager)
	{
		this.plugin = plugin;
		this.files = filesManager;
		
		createLootTables();
	}
	
	public void createLootTables()
	{
		createTable("items", this.items);
		createTable("hand.type", this.hand_types);
		createTable("hand.sword", this.sword_enchants);
		createTable("hand.tool", this.tool_enchants);
		createTable("bow", this.bow_enchants);
		createTable("armor.type", this.armor_types);
		createTable("armor.enchant", this.armor_enchants);
		createTable("bucket", this.bucket_types);
		createTable("enchanted", this.enchant_number);
		createTable("enchant_level", this.enchant_level);
		createTable("potion.type", this.potion_types);
		createTable("potion.splash", this.potion_splash);
		createTable("potion.extended", this.potion_extended);
		createTable("potion.level", this.potion_level);
	}
	
	public void createTable(String tableKey, HashMap<String, ArrayList<Integer>> table)
	{
		String currItem = "";
		String prevItem = "";
		boolean first = true;
		
		for(String key : files.getLootConfig().getConfigurationSection(tableKey).getKeys(false))
		{
			currItem = key.replace(tableKey + ".", "");
			
			table.put(currItem, new ArrayList<Integer>());
			
			if(first)
			{
				table.get(currItem).add(0);
				first = false;
			}
			else
			{
				table.get(prevItem).add(files.getLootConfig().getInt(key) - 1);
				table.get(currItem).add(files.getLootConfig().getInt(key));
			}
			
			prevItem = currItem;
		}
		
		table.get(prevItem).add(99);
	}

	public String getRandomEntry(HashMap<String, ArrayList<Integer>> table)
	{
		String type = "";

		Random rdm = new Random();
		int nloot = rdm.nextInt(100);

		for(Map.Entry<String, ArrayList<Integer>> entry : table.entrySet())
		{			
			if(nloot >= entry.getValue().get(0)
					&& nloot <= entry.getValue().get(1))
			{
				return entry.getKey();
			}
		}
		
		return type;
	}
	
	public ItemStack getLoot()
	{
		ItemStack loot = new ItemStack(Material.AIR, 1);
		
		String it = getRandomEntry(this.items);
		
		switch(it)
		{
		case "sword":
			loot = getSword();
			break;
		case "pickaxe":
			loot = getPickaxe();
			break;
		case "axe":
			loot = getAxe();
			break;
		case "bow":
			loot = getBow();
			break;
		case "arrow":
			loot.setType(Material.ARROW);
			loot.setAmount(5);
			break;
		case "bucket":
			loot = getBucket();
			break;
		case "boots":
			loot = getBoots();
			break;
		case "leggings":
			loot = getLeggings();
			break;
		case "chestplate":
			loot = getChestplate();
			break;
		case "helmet":
			loot = getHelmet();
			break;
		case "flintnsteel":
			loot.setType(Material.FLINT_AND_STEEL);
			break;
		case "tnt":
			loot.setType(Material.TNT);
			loot.setAmount(2);
			break;
		case "potion":
			loot = getPotion();
			break;
		case "golden_apple":
			loot.setType(Material.GOLDEN_APPLE);
			break;
		}
		
		return loot;
	}	

	public ItemStack switchLoot(ItemStack loot)
	{
		loot = new ItemStack(Material.AIR, 1);
		
		String it = getRandomEntry(this.items);
		
		switch(it)
		{
		case "sword":
			loot = getSword();
			break;
		case "pickaxe":
			loot = getPickaxe();
			break;
		case "axe":
			loot = getAxe();
			break;
		case "bow":
			loot = getBow();
			break;
		case "arrow":
			loot.setType(Material.ARROW);
			loot.setAmount(5);
			break;
		case "bucket":
			loot = getBucket();
			break;
		case "boots":
			loot = getBoots();
			break;
		case "leggings":
			loot = getLeggings();
			break;
		case "chestplate":
			loot = getChestplate();
			break;
		case "helmet":
			loot = getHelmet();
			break;
		case "flintnsteel":
			loot.setType(Material.FLINT_AND_STEEL);
			break;
		case "tnt":
			loot.setType(Material.TNT);
			loot.setAmount(2);
			break;
		case "potion":
			loot = getPotion();
			break;
		case "golden_apple":
			loot.setType(Material.GOLDEN_APPLE);
			break;
		}
		
		return loot;
	}	
	
	public ItemStack getSword()
	{
		ItemStack sword = new ItemStack(Material.AIR);
		
		switch(getRandomEntry(this.hand_types))
		{
		case "wood":
			sword.setType(Material.WOOD_SWORD);
			break;
		case "stone":
			sword.setType(Material.STONE_SWORD);
			break;
		case "iron":
			sword.setType(Material.IRON_SWORD);
			break;
		case "diamond":
			sword.setType(Material.DIAMOND_SWORD);
			break;
		}
		
		int enchants = getEnchantNumber();
		
		for(int i = 0; i < enchants; ++i)
		{
			switch(getRandomEntry(this.sword_enchants))
			{
			case "sharpness":
				sword.addEnchantment(Enchantment.DAMAGE_ALL, getEnchantLevel(5));
				break;
			case "knockback":
				sword.addEnchantment(Enchantment.KNOCKBACK, getEnchantLevel(2));
				break;
			case "unbreaking":
				sword.addEnchantment(Enchantment.DURABILITY, getEnchantLevel(3));
				break;
			case "bane_of_anthropods":
				sword.addEnchantment(Enchantment.DAMAGE_ARTHROPODS, getEnchantLevel(5));
				break;
			case "smite":
				sword.addEnchantment(Enchantment.DAMAGE_UNDEAD, getEnchantLevel(5));
				break;
			case "fire_aspect":
				sword.addEnchantment(Enchantment.FIRE_ASPECT, getEnchantLevel(2));
				break;
			}
		}
		
		return sword;
	}
	
	public ItemStack getPickaxe()
	{
		ItemStack pickaxe = new ItemStack(Material.AIR);
		
		switch(getRandomEntry(this.hand_types))
		{
		case "wood":
			pickaxe.setType(Material.WOOD_PICKAXE);
			break;
		case "stone":
			pickaxe.setType(Material.STONE_PICKAXE);
			break;
		case "iron":
			pickaxe.setType(Material.IRON_PICKAXE);
			break;
		case "diamond":
			pickaxe.setType(Material.DIAMOND_PICKAXE);
			break;
		}
		
		int enchants = getEnchantNumber();
		
		for(int i = 0; i < enchants; ++i)
		{
			switch(getRandomEntry(this.tool_enchants))
			{
			case "unbreaking":
				pickaxe.addEnchantment(Enchantment.DURABILITY, getEnchantLevel(3));
				break;
			case "efficiency":
				pickaxe.addEnchantment(Enchantment.DIG_SPEED, getEnchantLevel(5));
				break;
			}
		}
		
		return pickaxe;
	}
	
	public ItemStack getAxe()
	{
		ItemStack axe = new ItemStack(Material.AIR);
		
		switch(getRandomEntry(this.hand_types))
		{
		case "wood":
			axe.setType(Material.WOOD_AXE);
			break;
		case "stone":
			axe.setType(Material.STONE_AXE);
			break;
		case "iron":
			axe.setType(Material.IRON_AXE);
			break;
		case "diamond":
			axe.setType(Material.DIAMOND_AXE);
			break;
		}
		
		int enchants = getEnchantNumber();
		
		for(int i = 0; i < enchants; ++i)
		{
			switch(getRandomEntry(this.tool_enchants))
			{
			case "unbreaking":
				axe.addEnchantment(Enchantment.DURABILITY, getEnchantLevel(3));
				break;
			case "efficiency":
				axe.addEnchantment(Enchantment.DIG_SPEED, getEnchantLevel(5));
				break;
			}
		}
		
		return axe;
	}

	public ItemStack getBow()
	{
		ItemStack bow = new ItemStack(Material.BOW);
		
		int enchants = getEnchantNumber();
		
		for(int i = 0; i < enchants; ++i)
		{
			switch(getRandomEntry(this.bow_enchants))
			{
			case "power":
				bow.addEnchantment(Enchantment.ARROW_DAMAGE, getEnchantLevel(5));
				break;
			case "punch":
				bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, getEnchantLevel(2));
				break;
			case "unbreaking":
				bow.addEnchantment(Enchantment.DURABILITY, getEnchantLevel(3));
				break;
			case "infinity":
				bow.addEnchantment(Enchantment.ARROW_INFINITE, getEnchantLevel(1));
				break;
			case "flame":
				bow.addEnchantment(Enchantment.ARROW_FIRE, getEnchantLevel(1));
				break;
			}
		}
		
		return bow;
	}

	public ItemStack getBucket()
	{
		ItemStack bucket = new ItemStack(Material.AIR);
		
		switch(getRandomEntry(this.bucket_types))
		{
		case "water":
			bucket.setType(Material.WATER_BUCKET);
			break;
		case "lava":
			bucket.setType(Material.LAVA_BUCKET);
			break;
		case "milk":
			bucket.setType(Material.MILK_BUCKET);
			break;
		}
		
		return bucket;
	}

	public ItemStack getBoots()
	{
		ItemStack boots = new ItemStack(Material.AIR);
		
		switch(getRandomEntry(this.armor_types))
		{
		case "leather":
			boots.setType(Material.LEATHER_BOOTS);
			break;
		case "chain":
			boots.setType(Material.CHAINMAIL_BOOTS);
			break;
		case "iron":
			boots.setType(Material.IRON_BOOTS);
			break;
		case "diamond":
			boots.setType(Material.DIAMOND_BOOTS);
			break;
		}
		
		int enchants = getEnchantNumber();
		
		for(int i = 0; i < enchants; ++i)
		{
			switch(getRandomEntry(this.armor_enchants))
			{
			case "protection":
				boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getEnchantLevel(4));
				break;
			case "projectile_protection":
				boots.addEnchantment(Enchantment.PROTECTION_PROJECTILE, getEnchantLevel(4));
				break;
			case "blast_protection":
				boots.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, getEnchantLevel(4));
				break;
			case "unbreaking":
				boots.addEnchantment(Enchantment.DURABILITY, getEnchantLevel(3));
				break;
			case "fire_resistance":
				boots.addEnchantment(Enchantment.PROTECTION_FIRE, getEnchantLevel(4));
				break;
			}
		}
		
		return boots;
	}

	public ItemStack getLeggings()
	{
		ItemStack leggings = new ItemStack(Material.AIR);
		
		switch(getRandomEntry(this.armor_types))
		{
		case "leather":
			leggings.setType(Material.LEATHER_LEGGINGS);
			break;
		case "chain":
			leggings.setType(Material.CHAINMAIL_LEGGINGS);
			break;
		case "iron":
			leggings.setType(Material.IRON_LEGGINGS);
			break;
		case "diamond":
			leggings.setType(Material.DIAMOND_LEGGINGS);
			break;
		}
		
		int enchants = getEnchantNumber();
		
		for(int i = 0; i < enchants; ++i)
		{
			switch(getRandomEntry(this.armor_enchants))
			{
			case "protection":
				leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getEnchantLevel(4));
				break;
			case "projectile_protection":
				leggings.addEnchantment(Enchantment.PROTECTION_PROJECTILE, getEnchantLevel(4));
				break;
			case "blast_protection":
				leggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, getEnchantLevel(4));
				break;
			case "unbreaking":
				leggings.addEnchantment(Enchantment.DURABILITY, getEnchantLevel(3));
				break;
			case "fire_resistance":
				leggings.addEnchantment(Enchantment.PROTECTION_FIRE, getEnchantLevel(4));
				break;
			}
		}
		
		return leggings;
	}

	public ItemStack getChestplate()
	{
		ItemStack chestplate = new ItemStack(Material.AIR);
		
		switch(getRandomEntry(this.armor_types))
		{
		case "leather":
			chestplate.setType(Material.LEATHER_CHESTPLATE);
			break;
		case "chain":
			chestplate.setType(Material.CHAINMAIL_CHESTPLATE);
			break;
		case "iron":
			chestplate.setType(Material.IRON_CHESTPLATE);
			break;
		case "diamond":
			chestplate.setType(Material.DIAMOND_CHESTPLATE);
			break;
		}
		
		int enchants = getEnchantNumber();
		
		for(int i = 0; i < enchants; ++i)
		{
			switch(getRandomEntry(this.armor_enchants))
			{
			case "protection":
				chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getEnchantLevel(4));
				break;
			case "projectile_protection":
				chestplate.addEnchantment(Enchantment.PROTECTION_PROJECTILE, getEnchantLevel(4));
				break;
			case "blast_protection":
				chestplate.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, getEnchantLevel(4));
				break;
			case "unbreaking":
				chestplate.addEnchantment(Enchantment.DURABILITY, getEnchantLevel(3));
				break;
			case "fire_resistance":
				chestplate.addEnchantment(Enchantment.PROTECTION_FIRE, getEnchantLevel(4));
				break;
			}
		}
		
		return chestplate;
	}

	public ItemStack getHelmet()
	{
		ItemStack helmet = new ItemStack(Material.AIR);
		
		switch(getRandomEntry(this.armor_types))
		{
		case "leather":
			helmet.setType(Material.LEATHER_HELMET);
			break;
		case "chain":
			helmet.setType(Material.CHAINMAIL_HELMET);
			break;
		case "iron":
			helmet.setType(Material.IRON_HELMET);
			break;
		case "diamond":
			helmet.setType(Material.DIAMOND_HELMET);
			break;
		}
		
		int enchants = getEnchantNumber();
		
		for(int i = 0; i < enchants; ++i)
		{
			switch(getRandomEntry(this.armor_enchants))
			{
			case "protection":
				helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getEnchantLevel(4));
				break;
			case "projectile_protection":
				helmet.addEnchantment(Enchantment.PROTECTION_PROJECTILE, getEnchantLevel(4));
				break;
			case "blast_protection":
				helmet.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, getEnchantLevel(4));
				break;
			case "unbreaking":
				helmet.addEnchantment(Enchantment.DURABILITY, getEnchantLevel(3));
				break;
			case "fire_resistance":
				helmet.addEnchantment(Enchantment.PROTECTION_FIRE, getEnchantLevel(4));
				break;
			}
		}
		
		return helmet;
	}
	
	public ItemStack getPotion()
	{
		boolean extended = true;
		int max = 1;
		
		ItemStack stack = new ItemStack(Material.POTION);
		Potion potion = new Potion(1);
		
		switch(getRandomEntry(this.potion_types))
		{
		case "speed":
			potion.setType(PotionType.SPEED);
			extended = true;
			max = 2;
			break;
		case "slowness":
			potion.setType(PotionType.SLOWNESS);
			extended = true;
			max = 2;
			break;
		case "regeneration":
			potion.setType(PotionType.REGEN);
			extended = true;
			max = 2;
			break;
		case "strength":
			potion.setType(PotionType.STRENGTH);
			extended = true;
			max = 2;
			break;
		case "instant_heal":
			potion.setType(PotionType.INSTANT_HEAL);
			extended = false;
			max = 2;
			break;
		case "instant_damage":
			potion.setType(PotionType.INSTANT_DAMAGE);
			extended = false;
			max = 2;
			break;
		case "fire_resistance":
			potion.setType(PotionType.FIRE_RESISTANCE);
			extended = true;
			max = 1;
			break;
		case "invisibility":
			potion.setType(PotionType.INVISIBILITY);
			extended = true;
			max = 1;
			break;
		case "weakness":
			potion.setType(PotionType.WEAKNESS);
			extended = true;
			max = 1;
			break;
		case "poison":
			potion.setType(PotionType.POISON);
			extended = true;
			max = 2;
			break;
		}
			
		if(extended)
		{
			potion.setHasExtendedDuration(getPotionExtended());
		}
		potion.setSplash(getPotionSplash());
		potion.setLevel(max);
		potion.apply(stack);
		
		return stack;
	}
	
	public int getEnchantNumber()
	{
		return Integer.parseInt(getRandomEntry(enchant_number));
	}

	public int getEnchantLevel(int max)
	{
		int level = Integer.parseInt(getRandomEntry(enchant_level));
		return (level > max) ? max - 1 : level - 1;
	}

	public boolean getPotionSplash()
	{
		return Boolean.parseBoolean(getRandomEntry(potion_splash));
	}

	public boolean getPotionExtended()
	{
		return Boolean.parseBoolean(getRandomEntry(this.potion_extended));
	}

	public int getPotionLevel(int max)
	{
		int level = Integer.parseInt(getRandomEntry(potion_level));
		return (level > max) ? max - 1 : level - 1;
	}
	
}