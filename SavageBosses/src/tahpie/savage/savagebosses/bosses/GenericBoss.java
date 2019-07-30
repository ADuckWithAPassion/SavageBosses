package tahpie.savage.savagebosses.bosses;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.print.CancelablePrintJob;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.bosses.abilities.Ability;

public class GenericBoss implements Listener{
	private String name;
	private String type;
	private ConfigurationSection abilitiesSection;
	private int delay;
	private String weapon;
	private String helmet;
	private int baseHealth;
	private String chest;
	private String legs;
	private String boots;
	private int difficulty;
	private List<?> drops;
	private SavageBosses SB;
	private List<Ability> abilities = new ArrayList<Ability>();
	List<Ability> queuedCastableAbilities = new ArrayList<Ability>();
	List<Ability> queuedOnAttackAbilities = new ArrayList<Ability>();
	List<Ability> queuedOnAttackedAbilities = new ArrayList<Ability>();
	private int xp;
	
	public GenericBoss(ConfigurationSection section, SavageBosses SB) {
		this.SB = SB;

		name = section.getString("name");
		type = section.getString("type");
		abilitiesSection = section.getConfigurationSection("abilities");
		delay = section.getInt("delay");
		baseHealth = section.getInt("baseHealth");
		weapon = section.getString("weapon");
		helmet = section.getString("helmet");
		chest = section.getString("chest");
		legs = section.getString("legs");
		boots = section.getString("boots");
		difficulty = section.getInt("difficulty");
		drops = section.getList("drops");
		xp = section.getInt("xp");
		
		if(name == null) {
			name = "SetMeAName";
		}
		if(type == null) {
			type = "zombie";
		}
		if(delay == 0) {
			delay = 7;
		}
		if(baseHealth == 0) {
			baseHealth = 50;
		}
		if(difficulty == 0) {
			difficulty = 1;
		}
		if(weapon == null) {
			weapon = "AIR";
		}
		if(helmet == null) {
			helmet = "AIR";
		}
		if(chest == null) {
			chest = "AIR";
		}
		if(legs == null) {
			legs = "AIR";
		}
		if(boots == null) {
			boots = "AIR";
		}

		loadAbilities(abilitiesSection);
	}
	public void loadAbilities(ConfigurationSection abilitiesSection) {
		if(abilitiesSection != null) {
			for(String abilityID: abilitiesSection.getKeys(false)) {
				ConfigurationSection abilitySection = abilitiesSection.getConfigurationSection(abilityID);
				try {
					Class<?> abilityClass = Class.forName("tahpie.savage.savagebosses.bosses.abilities."+abilitySection.getString("ability"));
					Constructor con = abilityClass.getConstructor(ConfigurationSection.class, SavageBosses.class);
					Ability ability = (Ability) con.newInstance(abilitySection, SB);
					abilities.add(ability);
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					Log.info(abilitySection.getString("ability") + " DOES NOT EXIST");
					e.printStackTrace();
				}
			}
		}
	}
	public void spawn(Location loc) {
		new IndividualBoss(this,loc, SB);
	}
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public List<Ability> getAbilities() {
		return abilities;
	}
	public String getHelmet() {
		return helmet;
	}
	public String getChest() {
		return chest;
	}
	public String getLegs() {
		return legs;
	}
	public String getBoots() {
		return boots;
	}
	public String getWeapon() {
		return weapon;
	}
	public double getHealth() {
		return baseHealth;
	}
	public ItemStack getDrop() {
		double cumProbability = 0;
		double number = Math.random();
		for(Object details: drops) {
			HashMap detailMap = (HashMap)details;
			String itemName = detailMap.get("item").toString();
			Double itemProbability = Double.valueOf(detailMap.get("probability").toString());
			cumProbability += itemProbability;
			if(cumProbability >= number) {
				return(new ItemStack(Material.getMaterial(itemName)));
			}
		}
		return(new ItemStack(Material.AIR));
	}
	public int getDifficulty() {
		return difficulty;
	}
	public int getExp() {
		return xp;
	}
}