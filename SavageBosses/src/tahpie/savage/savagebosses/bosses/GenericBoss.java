package tahpie.savage.savagebosses.bosses;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.print.CancelablePrintJob;

import org.bukkit.Bukkit;
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

import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.bosses.abilities.Ability;

public class GenericBoss implements Listener{
	private String name;
	private String world;
	private List<?> coords;
	private String type;
	private ConfigurationSection abilitiesSection;
	private int respawn;
	private int delay;
	private String weapon;
	private String helmet;
	private int baseHealth;
	private String chest;
	private String legs;
	private String boots;
	private int difficulty;
	private LivingEntity parent;
	private SavageBosses SB;
	private List<Ability> abilities = new ArrayList<Ability>();
	List<Ability> queuedCastableAbilities = new ArrayList<Ability>();
	List<Ability> queuedOnAttackAbilities = new ArrayList<Ability>();
	List<Ability> queuedOnAttackedAbilities = new ArrayList<Ability>();
	private Location spawnLocation;
	
	public GenericBoss(ConfigurationSection section, SavageBosses SB) {
		this.SB = SB;

		name = section.getString("name");
		world = section.getString("world");
		coords = section.getList("location");
		type = section.getString("type");
		abilitiesSection = section.getConfigurationSection("abilities");
		respawn = section.getInt("respawn");
		delay = section.getInt("delay");
		baseHealth = section.getInt("baseHealth");
		weapon = section.getString("weapon");
		helmet = section.getString("helmet");
		chest = section.getString("chest");
		legs = section.getString("legs");
		boots = section.getString("boots");
		difficulty = section.getInt("difficulty");
		
		loadAbilities(abilitiesSection);
	}
	public void loadAbilities(ConfigurationSection abilitiesSection) {
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
	public int getRespawn() {
		return respawn;
	}
}