package tahpie.savage.savagebosses.questitems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

public class SpecialItem {

	List<ChatColor> rarityColours = Arrays.asList(ChatColor.WHITE, ChatColor.DARK_GREEN, ChatColor.BLUE, ChatColor.YELLOW, ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.DARK_PURPLE); 
	
	List<String> rarityNames = Arrays.asList("Common", "Uncommon", "Magical", "Rare", "Epic", "Legendary", "Special");
	private ItemStack parent;
	private String boss;
	ItemMeta meta;
	String rarity;
	ArrayList<String> lore = new ArrayList<String>();
	String line1 = " ";
	String line2 = "";
	String line3 = ChatColor.GRAY+StringUtils.repeat("-",20);
	String line4 = "";
	String line5 = "";
	String line6 = "";
	String line7 = "";
	String line8 = "";
	private double chance;
	
	public SpecialItem(String name, String mat, HashMap<String, Integer> enchantmentList, int rarityInt, String explosionString, String tag, String effect) {
		this.parent = new ItemStack(Material.getMaterial(mat));
		meta = parent.getItemMeta();
			
		rarity = rarityNames.get(rarityInt);
		ChatColor colour = rarityColours.get(rarityInt);
		
		meta.setDisplayName(colour+name);
		parent.setItemMeta(meta);
		
		for(Enchantment enchantment: parent.getEnchantments().keySet()) {
			parent.removeEnchantment(enchantment);
		}
		for(String enchantName: enchantmentList.keySet()) {
			addEnchant(enchantName, enchantmentList.get(enchantName));
		}
		meta = parent.getItemMeta();
		
		ChatColor explosionColour;
		try {
			explosionColour = ChatColor.valueOf(explosionString);		
		}
		catch(NullPointerException error) {
			explosionColour = null;
		}
		if(tag != null) {
			line8 = ChatColor.GOLD+tag;	
		}
				
		line2 = colour+rarity+" "+WordUtils.capitalizeFully(parent.getType().name().replace("_", " "));
		
		if(explosionColour != null) {
			line6 = "Chance to make a "+explosionColour+WordUtils.capitalizeFully(explosionString.replace("_", " "))+" explosion!";
		}
		else if(effect != null) {
			line6 = ChatColor.translateAlternateColorCodes('&',effect);
		}

	}

	public void addEnchant(String enchantName, int enchantLevel) { // example ["DAMAGE_ALL",3]
		Enchantment enchantment = Enchantment.getByName(enchantName);
		parent.addUnsafeEnchantment(enchantment, enchantLevel);
	}
	public void applyLore() {
		List<String> lines = Arrays.asList(line1, line2, line3, line4, line5, line6, line7,line8);
		for(String line : lines) {
			if(line != "") {
				lore.add(line);
			}
		}
		
		meta.setLore(lore);
		parent.setItemMeta(meta);

	}
	public void setBoss(String boss) {
		line4 = ChatColor.LIGHT_PURPLE+"Dropped by: "+ChatColor.AQUA+boss;
		this.boss = boss;
	}
	public void setKiller(String killer) {
		Player player = Bukkit.getPlayer(killer);
		if(player == null) {
			line5 = ChatColor.LIGHT_PURPLE+"The killer of this boss: "+ChatColor.RED+"Unknown Causes";
		}
		else {
			line5 = ChatColor.LIGHT_PURPLE+"The killer of this boss: "+ChatColor.RED+player.getName();	
		}
	}
	public void setChance(double chance) {
		this.chance = chance;
	}
	public double getChance() {
		return chance;
	}
	public String getBoss() {
		return boss;
	}
	public ItemStack getItem(String killer, String boss) {
		setKiller(killer);
		setBoss(boss);
		applyLore();
		if(rarity == "Common") {
			Log.info(parent.getDurability());
			parent.setDurability((short)new Random().nextInt(parent.getType().getMaxDurability()));
		}
		return parent;
	}
}

