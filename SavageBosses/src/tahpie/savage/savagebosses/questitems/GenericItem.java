package tahpie.savage.savagebosses.questitems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_8_R3.Enchantment;


public class GenericItem {
	static double random;
	private static List<String> colours = Arrays.asList("AQUA","BLACK","BLUE","DARK_BLUE","DARK_PURPLE","RED","GOLD","GOLD","DARK_GRAY","GREEN","LIGHT_PURPLE","WHITE","YELLOW","GRAY");
	
	public static ItemStack getItem(String boss, int difficulty) {
		Log.info(colours);
		random = Math.random();
		Log.info(random);
		//1,    2,     3,     4,     5,     6 (Boss difficulty)
		//80%,  100%,  100%,  100%,  100%,  100% (If you get a drop, chance it will be COMMON or better)
		//60%,  100%,  100%,  100%,  100%,  100% (If you get a drop, chance it will be UNCOMMON or better)
		//10%,  20%,   30%,   40%,   50%,   60% (If you get a drop, chance it will be MAGICAL or better)
		//1%,   3%,    5%,    10%,   15%,   25% (If you get a drop, chance it will be RARE or better)
		//0%,   0%,    1%,    3%,    5%,    10% (If you get a drop, chance it will be EPIC or better)
		//0%,   0%,    0%,    0%,    1%,    5% (If you get a drop, chance it will be LEGENDARY or better)
		List<Double>common    = Arrays.asList(0.80,1.00,1.00,1.00,1.00,1.00);
		List<Double>uncommon  = Arrays.asList(0.60,1.00,1.00,1.00,1.00,1.00);
		List<Double>magical   = Arrays.asList(0.10,0.20,0.30,0.40,0.50,0.60); // These values are cumulative probabilities 
		List<Double>rare      = Arrays.asList(0.01,0.03,0.05,0.10,0.15,0.25); // Example: 15% chance to get a rare (or better) from a level 5 boss
		List<Double>epic      = Arrays.asList(0.00,0.00,0.01,0.03,0.05,0.10); // Values should be decreasing further down the list (for obvious reasons)
		List<Double>legendary = Arrays.asList(0.00,0.00,0.00,0.00,0.01,0.05); // Example: 0% chance to get an legendary from a level 1 boss
		int rarity = -1; // trash
		if(random<=common.get(difficulty-1)) {
			rarity = 0; // common
			if(random<=uncommon.get(difficulty-1)) {
				rarity = 1;	// uncommon
				if(random<=magical.get(difficulty-1)) {
					rarity = 2; // magical
					if(random<=rare.get(difficulty-1)) {
						rarity = 3; // rare
						if(random<=epic.get(difficulty-1)) {
							rarity = 4; // legendary
							if(random<=legendary.get(difficulty-1)) {
								rarity = 5;
							}	
						}	
					}	
				}	
			}
		}
		else { // NO DROP
			
		}
		//1,    2,     3,     4,     5,     6 (Boss difficulty)
		//10%,  10%,   10%,  10%,  10%,  10% (If you get a drop, chance it will be a SWORD)
		//10%,  10%,   10%,  10%,  10%,  10% (If you get a drop, chance it will be a PICKAXE)
		//10%,  10%,   10%,  10%,  10%,  10% (If you get a drop, chance it will be a AXE)
		//10%,  10%,   10%,  10%,  10%,  10% (If you get a drop, chance it will be a SPADE)
		//10%,  10%,   10%,  10%,  10%,  10% (If you get a drop, chance it will be a HELMET)
		//10%,  10%,   10%,  10%,  10%,  10% (If you get a drop, chance it will be a CHESTPLATE)
		//10%,  10%,   10%,  10%,  10%,  10% (If you get a drop, chance it will be a LEGGINGS)
		//10%,  10%,   10%,  10%,  10%,  10% (If you get a drop, chance it will be a BOOTS)
		//0%,   0%,    0%,   0%,   0%,   0% (If you get a drop, chance it will be a BOW)
		//0%,   0%,    0%,   0%,   0%,   0% (If you get a drop, chance it will be a FISHING ROD)

		List<Double>sword      = Arrays.asList(0.30,0.30,0.30,0.30,0.30,0.30);
		List<Double>PICKAXE    = Arrays.asList(0.10,0.10,0.10,0.10,0.10,0.10);
		List<Double>axe        = Arrays.asList(0.10,0.10,0.10,0.10,0.10,0.10); // These values are NOT cumulative probabilities 
		List<Double>SPADE      = Arrays.asList(0.10,0.10,0.10,0.10,0.10,0.10); // These are exact probabilities
		List<Double>helmet     = Arrays.asList(0.10,0.10,0.10,0.10,0.10,0.10); // Example: 10% chance to get a helmet from a level 1 boss
		List<Double>chestplate = Arrays.asList(0.10,0.10,0.10,0.10,0.10,0.10); // Example: 10% chance to get a sword from a level 5 boss
		List<Double>leggings   = Arrays.asList(0.10,0.10,0.10,0.10,0.10,0.10); // Columns must sum up to 1.00
		List<Double>boots      = Arrays.asList(0.10,0.10,0.10,0.10,0.10,0.10); // No requirements for increasing/decreasing
		List<Double>bow        = Arrays.asList(0.00,0.00,0.00,0.00,0.00,0.00);
		List<Double>fishing    = Arrays.asList(0.00,0.00,0.00,0.00,0.00,0.00); 
		String type = null;
		random = Math.random();
		Log.info(random);
		List<List<Double>>cycle = Arrays.asList(sword,PICKAXE,axe,SPADE,helmet,chestplate,leggings,boots,bow,fishing);
		List<String>cycleNames = Arrays.asList("Sword","PICKAXE","Axe","SPADE","Helmet","Chestplate","Leggings","Boots","Bow","Fishing");
		int i = 0;
		boolean found = false;
		double cumulative = 0;
		while(!found) {
			if(random<=cycle.get(i).get(difficulty-1)+cumulative) { // get the type of item based on their exact probability
				type = cycleNames.get(i);
				found = true;
			}
			else {
				cumulative+=cycle.get(i).get(difficulty-1);
				i++;				
			}
		}
		//1,    2,     3,     4,     5,     6 (Boss difficulty)
		//100%, 100%,  100%,  100%,  100%,  100% (If you get a drop, chance it will be WOOD/STONE/CHAINMAIL/IRON or better)
		//30%,  50%,   80%,   90%,   100%,  100% (If you get a drop, chance it will be IRON/GOLD/LEATHER or better)
		//1%,   5%,    10%,   15%,   20%,   35% (If you get a drop, chance it will be DIAMOND or better)
		List<Double>bad    = Arrays.asList(1.00,1.00,1.00,1.00,1.00,1.00); // Top row must be 1.00
		List<Double>medium = Arrays.asList(0.30,0.50,0.80,0.90,1.00,1.00); // These values are cumulative probabilities 
		List<Double>good   = Arrays.asList(0.01,0.05,0.10,0.15,0.20,0.35); // Example: 15% chance to get diamond (or better) from a level 4 boss
		String material = null;
		if(random<=bad.get(difficulty-1)) {
			if(Arrays.asList("Sword","PICKAXE","Axe","SPADE").contains(type)) {
				if(Math.random()<=0.5) {
					material = "WOOD";
				}
				else {
					material = "STONE";
				}				
			}
			else if(Arrays.asList("Helmet","Chestplate","Leggings","Boots").contains(type)) {
				if(Math.random()<=0.5) {
					material = "CHAINMAIL";
				}
				else {
					material = "Iron";
				}
			}
			else if(Arrays.asList("Bow").contains(type)) {
				material = "Bow";
			}
			else if(Arrays.asList("Fishing").contains(type)) {
				material = "Fishing";
			}
			if(random<=medium.get(difficulty-1)) {
				if(Arrays.asList("Sword","PICKAXE","Axe","SPADE").contains(type)) {
					if(Math.random()<=0.5) {
						material = "Iron";
					}
					else {
						material = "GOLD";
					}
				}
				else if(Arrays.asList("Helmet","Chestplate","Leggings","Boots").contains(type)) {
					double rand = Math.random();
					if(rand<=0.33) {
						material = "Iron";
					}
					else if(rand<=0.66) {
						material = "GOLD";
					}
					else {
						material = "Leather";
					}
				}
				if(random<=good.get(difficulty-1)) {
					if(Arrays.asList("Sword","PICKAXE","Axe","SPADE","Helmet","Chestplate","Leggings","Boots").contains(type)) {
						material = "Diamond";
					}
				}
			}
		}
		HashMap<String, Integer> enchantmentList = new HashMap<String, Integer>();
		double rand = Math.random();
		double rand2 = Math.random();
		String prefix = null;
		if(rarity == 0) {
			if(rand2<=0.33) {
				prefix = "Broken ";						
			}
			else if(rand2<=0.66) {
				prefix = "Unusable ";
			}
			else {
				prefix = "Chipped ";
			}
		}
		else if(rarity == 1) {
			if(Arrays.asList("Sword","Axe").contains(type)) {
				if(rand<=0.05) {
					enchantmentList.put("DAMAGE_ALL", 1);
					if(rand2<=0.33) {
						prefix = "Jagged ";						
					}
					else if(rand2<=0.66) {
						prefix = "Carved ";
					}
					else {
						prefix = "Rough ";
					}
				}
				else if(rand<=0.25) {
					enchantmentList.put("DAMAGE_UNDEAD", 1);
					if(rand2<=0.33) {
						prefix = "Rotting ";						
					}
					else if(rand2<=0.66) {
						prefix = "Crumbling ";
					}
					else {
						prefix = "Bent ";
					}
				}
				else if(rand<=0.45) {
					enchantmentList.put("DAMAGE_ARTHROPODS", 1);
					if(rand2<=0.33) {
						prefix = "Wobbly ";						
					}
					else if(rand2<=0.66) {
						prefix = "Soft ";
					}
					else {
						prefix = "Cheap ";
					}
				}
				else if(rand<=0.50) {
					enchantmentList.put("KNOCKBACK", 1);
					if(rand2<=0.33) {
						prefix = "Shaky ";						
					}
					else if(rand2<=0.66) {
						prefix = "Bouncy ";
					}
					else {
						prefix = "Reflective ";
					}
				}
				else if(rand<=0.55) {
					enchantmentList.put("LOOT_BONUS_MOBS", 1);
					if(rand2<=0.33) {
						prefix = "Lucky ";						
					}
					else if(rand2<=0.66) {
						prefix = "Flimsy ";
					}
					else {
						prefix = "Smooth ";
					}
				}
				else {
				enchantmentList.put("DURABILITY", 1);
					if(rand2<=0.33) {
						prefix = "Solid ";						
					}
					else if(rand2<=0.66) {
						prefix = "Unbreaking ";
					}
					else {
						prefix = "Makeshift ";
					}
				}
			}
			if(Arrays.asList("Helmet","Chestplate","Leggings","Boots").contains(type)) {
				if(rand<=0.15) {
					enchantmentList.put("DURABILITY", 1);
					if(rand2<=0.33) {
						prefix = "Solid ";				
					}
					else if(rand2<=0.66) {
						prefix = "Unbreaking ";
					}
					else {
						prefix = "Makeshift ";
					}
				}
				else if(rand<=0.25) {
					enchantmentList.put("PROTECTION_ENVIRONMENTAL", 1);
					if(rand2<=0.33) {
						prefix = "Weak ";						
					}
					else if(rand2<=0.66) {
						prefix = "Porous ";
					}
					else {
						prefix = "Chipped ";
					}
				}
				else if(rand<=0.55) {
					enchantmentList.put("PROTECTION_PROJECTILE", 1);
					if(rand2<=0.33) {
						prefix = "Flimsy ";						
					}
					else if(rand2<=0.66) {
						prefix = "Delicate ";
					}
					else {
						prefix = "Worn out ";
					}
				}
				else if(rand<=0.70) {
					enchantmentList.put("PROTECTION_FIRE", 1);
					if(rand2<=0.33) {
						prefix = "Inefficient ";						
					}
					else if(rand2<=0.66) {
						prefix = "Feeble ";
					}
					else {
						prefix = "Warm ";
					}
				}
				else{
					enchantmentList.put("PROTECTION_EXPLOSIONS", 1);
					if(rand2<=0.33) {
						prefix = "Hollow ";						
					}
					else if(rand2<=0.66) {
						prefix = "Inferior ";
					}
					else {
						prefix = "Ineffective ";
					}
				}
			}
			if(Arrays.asList("PICKAXE","SPADE").contains(type)) {
				if(rand<=0.05) {
					enchantmentList.put("DIG_SPEED", 1);
					if(rand2<=0.33) {
						prefix = "Poor ";						
					}
					else if(rand2<=0.66) {
						prefix = "Slow ";
					}
					else {
						prefix = "Split ";
					}
				}
				else if(rand<=0.25) {
					enchantmentList.put("LOOT_BONUS_BLOCKS", 1);
					if(rand2<=0.33) {
						prefix = "Unfortunate ";						
					}
					else if(rand2<=0.66) {
						prefix = "Charmless ";
					}
					else {
						prefix = "Torn ";
					}
				}
				
				else {
					enchantmentList.put("DURABILITY", 1);
					if(rand2<=0.33) {
						prefix = "Solid ";						
					}
					else if(rand2<=0.66) {
						prefix = "Unbreaking ";
					}
					else {
						prefix = "Makeshift ";
					}
				}
			}
		}
		else if(rarity == 2) {
			if(Arrays.asList("Sword","Axe").contains(type)) {
				if(rand<=0.10) {
					enchantmentList.put("DAMAGE_ALL", 1);
					enchantmentList.put("KNOCKBACK", 1);
					if(rand2<=0.33) {
						prefix = "Blunt ";						
					}
					else if(rand2<=0.66) {
						prefix = "Dangerous ";
					}
					else {
						prefix = "Old ";
					}
				}
				else if(rand<=0.20) {
					enchantmentList.put("DAMAGE_UNDEAD", 2);
					if(rand2<=0.33) {
						prefix = "Scary ";						
					}
					else if(rand2<=0.66) {
						prefix = "Rotten ";
					}
					else {
						prefix = "Discarded ";
					}
				}
				else if(rand<=0.45) {
					enchantmentList.put("DAMAGE_ARTHROPODS", 2);
					if(rand2<=0.33) {
						prefix = "Crushed ";						
					}
					else if(rand2<=0.66) {
						prefix = "Oozing ";
					}
					else {
						prefix = "Webbed ";
					}
				}
				else if(rand<=0.50) {
					enchantmentList.put("KNOCKBACK", 1);
					enchantmentList.put("DURABILITY", 1);
					if(rand2<=0.33) {
						prefix = "Worthless ";		
					}
					else if(rand2<=0.66) {
						prefix = "Traded ";
					}
					else {
						prefix = "Aged ";
					}
				}
				else if(rand<=0.55) {
					enchantmentList.put("LOOT_BONUS_MOBS", 2);
					if(rand2<=0.33) {
						prefix = "Farmer's ";						
					}
					else if(rand2<=0.66) {
						prefix = "Pirate's ";
					}
					else {
						prefix = "Stolen ";
					}
				}
				else {
					enchantmentList.put("DURABILITY", 2);
					if(rand2<=0.33) {
						prefix = "Reinforced ";						
					}
					else if(rand2<=0.66) {
						prefix = "Lasting ";
					}
					else {
						prefix = "Tough ";
					}
				}
			}
			if(Arrays.asList("Helmet","Chestplate","Leggings","Boots").contains(type)) {
				if(rand<=0.15) {
					enchantmentList.put("DURABILITY", 2);
					if(rand2<=0.33) {
						prefix = "Resistant ";				
					}
					else if(rand2<=0.66) {
						prefix = "Unbending ";
					}
					else {
						prefix = "New ";
					}
				}
				else if(rand<=0.25) {
					enchantmentList.put("PROTECTION_ENVIRONMENTAL", 1);
					enchantmentList.put("DURABILITY", 2);
					if(rand2<=0.33) {
						prefix = "Protective ";						
					}
					else if(rand2<=0.66) {
						prefix = "Useful ";
					}
					else {
						prefix = "Defending ";
					}
				}
				else if(rand<=0.55) {
					enchantmentList.put("PROTECTION_PROJECTILE", 2);
					enchantmentList.put("PROTECTION_FIRE", 1);
					if(rand2<=0.33) {
						prefix = "Absorbent ";						
					}
					else if(rand2<=0.66) {
						prefix = "Defensive ";
					}
					else {
						prefix = "Sparkling ";
					}
				}
				else if(rand<=0.70) {
					enchantmentList.put("PROTECTION_FIRE", 3);
					if(rand2<=0.33) {
						prefix = "Fireman's ";						
					}
					else if(rand2<=0.66) {
						prefix = "Insulated ";
					}
					else {
						prefix = "Warm ";
					}
				}
				else {
					enchantmentList.put("PROTECTION_EXPLOSIONS", 3);
					if(rand2<=0.33) {
						prefix = "Creeper's Bane ";						
					}
					else if(rand2<=0.66) {
						prefix = "Raider's Delight ";
					}
					else {
						prefix = "Cushioned ";
					}
				}
			}
			if(Arrays.asList("PICKAXE","SPADE").contains(type)) {
				if(rand<=0.05) {
					enchantmentList.put("DIG_SPEED", 2);
					if(rand2<=0.33) {
						prefix = "Effective ";						
					}
					else if(rand2<=0.66) {
						prefix = "Speedy ";
					}
					else {
						prefix = "Efficient ";
					}
				}
				else if(rand<=0.25) {
					enchantmentList.put("LOOT_BONUS_BLOCKS", 2);
					if(rand2<=0.33) {
						prefix = "Discovering ";						
					}
					else if(rand2<=0.66) {
						prefix = "Explorer's ";
					}
					else {
						prefix = "Precious ";
					}
				}
				
				else {
					enchantmentList.put("DURABILITY", 3);
					if(rand2<=0.33) {
						prefix = "Everlasting ";						
					}
					else if(rand2<=0.66) {
						prefix = "Unused ";
					}
					else {
						prefix = "Valuable ";
					}
				}
			}
		}
		else if(rarity == 3) {
			if(Arrays.asList("Sword","Axe").contains(type)) {
				if(rand<=0.10) {
					enchantmentList.put("DAMAGE_ALL", 3);
					enchantmentList.put("LOOT_BONUS_MOBS", 1);
					enchantmentList.put("DURABILITY", 1);
					
					if(rand2<=0.33) {
						prefix = "Knight's ";						
					}
					else if(rand2<=0.66) {
						prefix = "Noble's ";
					}
					else {
						prefix = "Lord's ";
					}
				}
				else if(rand<=0.20) {
					enchantmentList.put("DAMAGE_UNDEAD", 4);
					if(rand2<=0.33) {
						prefix = "Zombie Slicing ";						
					}
					else if(rand2<=0.66) {
						prefix = "Bone Crushing ";
					}
					else {
						prefix = "Infected";
					}
				}
				else if(rand<=0.45) {
					enchantmentList.put("DAMAGE_ARTHROPODS", 4);
					if(rand2<=0.33) {
						prefix = "Spider Dissecting ";						
					}
					else if(rand2<=0.66) {
						prefix = "Arachne's Bane ";
					}
					else {
						prefix = "Spider Swatting ";
					}
				}
				else if(rand<=0.50) {
					enchantmentList.put("LOOT_BONUS_MOBS", 3);
					enchantmentList.put("DURABILITY", 3);
					if(rand2<=0.33) {
						prefix = "Grinding ";		
					}
					else if(rand2<=0.66) {
						prefix = "Ancient ";
					}
					else {
						prefix = "Incising ";
					}
				}
				else {
					enchantmentList.put("DURABILITY", 5);
					enchantmentList.put("DAMAGE_ALL", 1);
					enchantmentList.put("KNOCKBACK", 2);
					if(rand2<=0.33) {
						prefix = "Glowing ";						
					}
					else if(rand2<=0.66) {
						prefix = "Shatterproof ";
					}
					else {
						prefix = "Imperishable ";
					}
				}
			}
			if(Arrays.asList("Helmet","Chestplate","Leggings","Boots").contains(type)) {
				if(rand<=0.15) {
					enchantmentList.put("DURABILITY", 3);
					enchantmentList.put("PROTECTION_ENVIRONMENTAL", 2);
					if(rand2<=0.33) {
						prefix = "Riot Police's ";				
					}
					else if(rand2<=0.66) {
						prefix = "Resilient ";
					}
					else {
						prefix = "King's ";
					}
				}
				else if(rand<=0.25) {
					enchantmentList.put("PROTECTION_ENVIRONMENTAL", 3);
					enchantmentList.put("PROTECTION_FIRE", 3);
					enchantmentList.put("PROTECTION_EXPLOSIONS", 3);
					if(rand2<=0.33) {
						prefix = "Turtle Shell ";						
					}
					else if(rand2<=0.66) {
						prefix = "Blessed ";
					}
					else {
						prefix = "Holy ";
					}
				}
				else {
					enchantmentList.put("PROTECTION_ENVIRONMENTAL", 1);
					enchantmentList.put("DURABILITY", 4);
					if(rand2<=0.33) {
						prefix = "Unique ";						
					}
					else if(rand2<=0.66) {
						prefix = "Queen's ";
					}
					else {
						prefix = "Paladin's Blessed ";
					}
				}
			}
			if(Arrays.asList("PICKAXE","SPADE").contains(type)) {
				if(rand<=0.05) {
					enchantmentList.put("DIG_SPEED", 4);
					enchantmentList.put("DURABILITY", 4);
					if(rand2<=0.33) {
						prefix = "Polished ";						
					}
					else if(rand2<=0.66) {
						prefix = "Sanic's ";
					}
					else {
						prefix = "Distinct ";
					}
				}
				else if(rand<=0.25) {
					enchantmentList.put("LOOT_BONUS_BLOCKS", 4);
					enchantmentList.put("DURABLITY", 1);
					enchantmentList.put("DIG_SPEED", 2);
					if(rand2<=0.33) {
						prefix = "Xrayer's ";						
					}
					else if(rand2<=0.66) {
						prefix = "Prospector's ";
					}
					else {
						prefix = "Gemmed ";
					}
				}
				
				else {
					enchantmentList.put("SILK_TOUCH", 1);
					enchantmentList.put("DIG_SPEED", 4);
					if(rand2<=0.33) {
						prefix = "Sculpting ";						
					}
					else if(rand2<=0.66) {
						prefix = "Precise ";
					}
					else {
						prefix = "Silky ";
					}
				}
			}
		}
		else if(rarity == 4) {
			if(Arrays.asList("Sword","Axe").contains(type)) {
				if(rand<=0.10) {
					enchantmentList.put("DAMAGE_ALL", 4);
					enchantmentList.put("LOOT_BONUS_MOBS", 1);
					enchantmentList.put("DURABILITY", 1);
					enchantmentList.put("FIRE_ASPECT", 1);
					
					if(rand2<=0.33) {
						prefix = "TahPie's Melted ";						
					}
					else if(rand2<=0.66) {
						prefix = "Assassin's ";
					}
					else {
						prefix = "Reaper's ";
					}
				}
				else if(rand<=0.20) {
					enchantmentList.put("DAMAGE_UNDEAD", 5);
					enchantmentList.put("DAMAGE_ARTHROPODS", 5);
					enchantmentList.put("DURABILITY", 3);
					enchantmentList.put("LOOT_BONUS_MOBS", 3);
					if(rand2<=0.33) {
						prefix = "Boss Destroying ";						
					}
					else if(rand2<=0.66) {
						prefix = "Soul Stealing ";
					}
					else {
						prefix = "Adventure's Enfused ";
					}
				}
				else if(rand<=0.45) {
					enchantmentList.put("KNOCKBACK", 4);
					enchantmentList.put("FIRE_ASPECT", 3);
					if(rand2<=0.33) {
						prefix = "Repelling ";						
					}
					else if(rand2<=0.66) {
						prefix = "Forceful ";
					}
					else {
						prefix = "Seared ";
					}
				}
				else {
					enchantmentList.put("DURABILITY", 5);
					enchantmentList.put("DAMAGE_ALL", 3);
					if(rand2<=0.33) {
						prefix = "Unmatchable ";						
					}
					else if(rand2<=0.66) {
						prefix = "Dragon Scaled ";
					}
					else {
						prefix = "Deceptively Powerful ";
					}
				}
			}
			if(Arrays.asList("Helmet","Chestplate","Leggings","Boots").contains(type)) {
				if(rand<=0.15) {
					enchantmentList.put("DURABILITY", 3);
					enchantmentList.put("PROTECTION_ENVIRONMENTAL", 5);
					if(rand2<=0.33) {
						prefix = "God's Shield ";				
					}
					else if(rand2<=0.66) {
						prefix = "Paladin's Aura ";
					}
					else {
						prefix = "The Ruling ";
					}
				}
				else {
					enchantmentList.put("PROTECTION_PROJECTILE", 10);
					enchantmentList.put("DURABILITY", 3);
					if(rand2<=0.33) {
						prefix = "Bullet Proof ";						
					}
					else if(rand2<=0.66) {
						prefix = "Marksman's Bane ";
					}
					else {
						prefix = "Impassable Barrier ";
					}
				}
			}
			if(Arrays.asList("PICKAXE","SPADE").contains(type)) {
				if(rand<=0.05) {
					enchantmentList.put("DIG_SPEED", 4);
					enchantmentList.put("DURABILITY", 4);
					enchantmentList.put("LOOT_BONUS_BLOCKS", 4);
					if(rand2<=0.33) {
						prefix = "Landscaper's ";						
					}
					else if(rand2<=0.66) {
						prefix = "Trenching ";
					}
					else {
						prefix = "Extrodinary ";
					}
				}
				else {
					enchantmentList.put("SILK_TOUCH", 1);
					enchantmentList.put("DIG_SPEED", 5);
					enchantmentList.put("DURABILITY", 3);
					if(rand2<=0.33) {
						prefix = "One Of A Kind ";						
					}
					else if(rand2<=0.66) {
						prefix = "Undiscovered ";
					}
					else {
						prefix = "Emperor's ";
					}
				}
			}
		}
		else {
			prefix = "None"; 
		}
		Log.info(prefix+" "+WordUtils.capitalizeFully(material+" "+type));
		Log.info(material.toUpperCase()+"_"+type.toUpperCase());
		Log.info(enchantmentList);
		Log.info(rarity);
		if(rarity>=0) {
			String suffix = "";
			String explosionString = null;
			if(rarity>=3) {
		        explosionString = colours.get(new Random().nextInt(colours.size())); 
				suffix = " Of The "+WordUtils.capitalizeFully(explosionString.replace("_", " "));
			}
			SpecialItem item = new SpecialItem(prefix+WordUtils.capitalizeFully(material+" "+type)+suffix+".", material.toUpperCase()+"_"+type.toUpperCase(), enchantmentList, rarity, explosionString, null, null);			
			return item.getItem("TahPie", boss);
		}
		return new ItemStack(Material.DIAMOND);
	}
}
