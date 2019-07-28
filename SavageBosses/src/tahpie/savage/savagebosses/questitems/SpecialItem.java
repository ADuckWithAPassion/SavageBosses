package tahpie.savage.savagebosses.questitems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NameAlreadyBoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpecialItem {

	List<ChatColor> rarityColours = Arrays.asList(ChatColor.WHITE, ChatColor.DARK_GREEN, ChatColor.BLUE, ChatColor.YELLOW, ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.DARK_PURPLE); 
	List<String> rarityNames = Arrays.asList("Common", "Uncommon", "Magical", "Rare", "Epic", "Legendary", "Special");
	private ItemStack parent;
	private String boss;
	private int chance; 
	
	public SpecialItem(String name, String mat, HashMap<String, Integer> enchantmentList, int rarityInt, String explosionString, String tag, String boss, String killer, int chance, String effect) {
		this.parent = new ItemStack(Material.getMaterial(mat));
		this.boss = boss;
		this.chance = chance;
		
		ItemMeta meta = parent.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		String line1 = " ";
		String line2 = "";
		String line3 = ChatColor.GRAY+StringUtils.repeat("-",20);
		String line4 = "";
		String line5 = "";
		String line6 = "";
		String line7 = "";
		String line8 = "";
		
		String rarity = rarityNames.get(rarityInt);
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
		
		line4 = ChatColor.LIGHT_PURPLE+"Dropped by: "+ChatColor.AQUA+boss;
		
		line2 = colour+rarity+" "+WordUtils.capitalizeFully(parent.getType().name().replace("_", " "));
		
		Player player = Bukkit.getPlayer(killer);
		if(player == null) {
			line5 = ChatColor.LIGHT_PURPLE+"The killer of this boss: "+ChatColor.RED+"Unknown Causes";
		}
		else {
			line5 = ChatColor.LIGHT_PURPLE+"The killer of this boss: "+ChatColor.RED+player.getName();	
		}
		
		if(explosionColour != null) {
			line6 = "Chance to make a "+explosionColour+WordUtils.capitalizeFully(explosionString.replace("_", " "))+" explosion!";
		}
		else if(effect != null) {
			line6 = ChatColor.translateAlternateColorCodes('&',effect);
		}
		List<String> lines = Arrays.asList(line1, line2, line3, line4, line5, line6, line7,line8);
		for(String line : lines) {
			if(line != "") {
				lore.add(line);
			}
		}
		
		meta.setLore(lore);
		parent.setItemMeta(meta);
	}

	public void addEnchant(String enchantName, int enchantLevel) { // example ["DAMAGE_ALL",3]
		Enchantment enchantment = Enchantment.getByName(enchantName);
		parent.addUnsafeEnchantment(enchantment, enchantLevel);
	}

	public String getBoss() {
		return boss;
	}
	public int getChance() {
		return chance;
	}
	
	public ItemStack getItem() {
		return parent;
	}
}

