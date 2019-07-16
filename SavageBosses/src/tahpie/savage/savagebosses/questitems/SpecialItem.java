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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpecialItem {
	ItemStack parent;
	String name;
	Material material;
	int rarityInt;
	String explosion;
	String tag;
	String boss;
	String killer;
	int chance;
	String effect;
	ItemMeta meta;
	List<String> lore;
	String rarity;
	ChatColor colour;
	List<ChatColor> rarityColours = Arrays.asList(ChatColor.GRAY, ChatColor.DARK_GREEN, ChatColor.BLUE, ChatColor.YELLOW, ChatColor.DARK_PURPLE, ChatColor.GOLD); 
	List<String> rarityNames = Arrays.asList("Common", "Uncommon", "Magical", "Rare", "Epic", "Legendary");

	public SpecialItem(String name, Material material, List<List<Object>> enchants, int rarityInt, String explosion, String tag, String boss, String killer, int chance, String effect) {
		parent = new ItemStack(material);
		meta = parent.getItemMeta();
		rarity = rarityNames.get(rarityInt);
		colour = rarityColours.get(rarityInt);
		meta.setDisplayName(colour+name);
		if(enchants != null) {
			for(List<Object> enchantment: enchants) {
				Enchantment enchantName = EnchantmentWrapper.getByKey(NamespacedKey.minecraft((String)enchantment.get(0))); // non deprecated way to get enchant
				parent.addEnchantment(enchantName, (int)enchantment.get(1)); 
			}			
		}
		lore = new ArrayList<>();
		lore.add("");
		lore.add(colour+rarity+" "+material.name().toLowerCase().replace("_", " "));
		lore.add(ChatColor.GRAY+StringUtils.repeat("-",20));
		lore.add(ChatColor.LIGHT_PURPLE+"Dropped by: "+ChatColor.AQUA+boss);
		lore.add(ChatColor.LIGHT_PURPLE+"The killer of this boss: "+ChatColor.RED+killer);
		if(rarityInt >= 3) {
			lore.add("Chance to make a "+colour+colour.toString().toLowerCase().replace("_", " ")+" explosion!");
		}
		meta.setLore(lore);
		parent.setItemMeta(meta);
	}
	public ItemStack getItem() {
		return parent;
	}
}

