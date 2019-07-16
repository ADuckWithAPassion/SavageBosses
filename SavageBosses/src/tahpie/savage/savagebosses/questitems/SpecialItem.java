package tahpie.savage.savagebosses.questitems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
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
	ItemStack parent;
	String name;
	String mat;
	int rarityInt;
	ChatColor explosionColour;
	String explosionString;
	String tag;
	String boss;
	String killer;
	int chance;
	String effect;
	ItemMeta meta;
	List<String> lore;
	String rarity;
	ChatColor colour;
	List<ChatColor> rarityColours = Arrays.asList(ChatColor.WHITE, ChatColor.DARK_GREEN, ChatColor.BLUE, ChatColor.YELLOW, ChatColor.DARK_PURPLE, ChatColor.GOLD); 
	List<String> rarityNames = Arrays.asList("Common", "Uncommon", "Magical", "Rare", "Epic", "Legendary");
	HashMap<String, Integer> enchantmentList;
	
	public SpecialItem(String name, String mat, HashMap<String, Integer> enchantmentList, int rarityInt, String explosionString, String tag, String boss, String killer, int chance, String effect) {
		this.name = name;
		this.mat = mat;
		this.enchantmentList = enchantmentList;
		this.rarityInt = rarityInt;
		this.explosionString = explosionString;
		this.tag = tag;
		this.boss = boss;
		this.killer = killer;
		this.chance = chance;
		this.effect = effect;
		
		setMat(mat);
		// lore formats
		// ["", Rarity + Item Name, -------, Dropped by, Killer] <= 2
		// ["", Rarity + Item Name, -------, Dropped by, Killer, explosion] <= 4
		// ["", Rarity + Item Name, -------, Dropped by, Killer, explosion, tag] = 5
	}
	public void setName(String name) {
		this.name = name;
		meta.setDisplayName(colour+name);
		parent.setItemMeta(meta);
	}
	public void setMat(String mat) {
		this.mat = mat;
		parent = new ItemStack(Material.getMaterial(mat));
		meta = parent.getItemMeta();
		if(lore == null) {
			lore = new ArrayList<String>();
			lore.add(0, "");
			lore.add(1, "");
			lore.add(2, ChatColor.GRAY+StringUtils.repeat("-",20));
			lore.add(3, "");
			lore.add(4, "");
			lore.add(5, "");
			lore.add(6, "");
			lore.add(7, "");
		}		
		setRarityInt(rarityInt);
		setName(name);
		setEnchants(enchantmentList);
		setExplosionColour(explosionString);
		setTag(tag);
		setBoss(boss);
		setKiller(killer);
		setChance(chance);
		setEffect(effect);
		lore.set(1,colour+rarity+" "+WordUtils.capitalizeFully(mat.replace("_", " ")));

	}
	public void setEnchants(HashMap<String, Integer> enchantmentList) {
		for(Enchantment enchantment: parent.getEnchantments().keySet()) {
			parent.removeEnchantment(enchantment);
		}
		for(String enchantName: enchantmentList.keySet()) {
			addEnchant(enchantName, enchantmentList.get(enchantName));
		}
		meta = parent.getItemMeta();
	}
	public void addEnchant(String enchantName, int enchantLevel) { // example ["DAMAGE_ALL",3]
		Enchantment enchantment = Enchantment.getByName(enchantName);
		parent.addUnsafeEnchantment(enchantment, enchantLevel);
	}
	public void setRarityInt(int rarityInt) {
		this.rarityInt = rarityInt;
		rarity = rarityNames.get(rarityInt);
		colour = rarityColours.get(rarityInt);
		setName(name);
	}
	public void setExplosionColour(String explosionString) {
		explosionColour = ChatColor.valueOf(explosionString);
	}
	public void setTag(String tag) {
		this.tag = tag;
		lore.set(7, ChatColor.GOLD+tag);
	}
	public void setBoss(String boss) {
		lore.set(3,ChatColor.LIGHT_PURPLE+"Dropped by: "+ChatColor.AQUA+boss);
	}
	public void setKiller(String player) {
		if(player == null) {
			lore.set(4,ChatColor.LIGHT_PURPLE+"The killer of this boss: "+ChatColor.RED+"Unknown Causes");
		}
		else {
			lore.set(4,ChatColor.LIGHT_PURPLE+"The killer of this boss: "+ChatColor.RED+player);	
		}
		if(rarityInt >= 3) {
			lore.set(5,"Chance to make a "+explosionColour+WordUtils.capitalizeFully(explosionString.replace("_", " "))+" explosion!");
		}
		if(rarityInt ==5) {
			lore.set(6, "");
			setTag(tag);
		}
		meta.setLore(lore);
		parent.setItemMeta(meta);
	}
	public void setChance(int chance) {
		this.chance = chance;
	}
	public void setEffect(String effect) {
		this.effect = effect;
	}
	public void applyMetadata(ItemMeta finalMeta) {
		parent.setItemMeta(finalMeta);
	}
	public void applyLore() {
		List<String> finalLore = new ArrayList<String>();
		ItemMeta finalMeta = parent.getItemMeta();
		finalLore.add(lore.get(0));
		finalLore.add(lore.get(1));
		finalLore.add(lore.get(2));
		finalLore.add(lore.get(3));
		finalLore.add(lore.get(4));
		if(rarityInt>=3) {
			finalLore.add(lore.get(5));
		}
		if(rarityInt>=5) {
			finalLore.add(lore.get(6));
			finalLore.add(lore.get(7));
		}
		finalMeta.setLore(finalLore);
		applyMetadata(finalMeta);
	}
	public ItemStack getItem() {
		applyLore();
		return parent;
	}
}

