package tahpie.savage.savagebosses.bosses;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.bosses.abilities.Ability;

public class IndividualBoss {

	private LivingEntity parent;
	private cycle abilityCycle;
	private GenericBoss GB;
	private SavageBosses SB;
	private Location spawnLocation;
	List<Ability> queuedCastableAbilities = new ArrayList<Ability>();
	List<Ability> queuedOnAttackAbilities = new ArrayList<Ability>();
	List<Ability> queuedOnAttackedAbilities = new ArrayList<Ability>();
	List<Ability> abilities = new ArrayList<Ability>();
	
	public IndividualBoss(GenericBoss GB, Location spawnLocation, SavageBosses SB) {
		this.GB = GB;
		this.SB = SB;
		this.spawnLocation = spawnLocation;
		try {
			parent = (LivingEntity)spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.valueOf(GB.getType().toUpperCase()));
			parent.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(GB.getHealth());
			parent.setHealth(parent.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
			Log.info(GB.getHealth());
		}
		catch(NullPointerException error) {
			Log.info("ENTITY TYPE NOT FOUND");
		}
		applyArmour();
	 for(Ability ability: GB.getAbilities()) {
			ability.addToQueue(3,this);
		}
		Boss.registerBoss(this);
		abilityCycle = new cycle(3, this, SB);
		}
	
	public LivingEntity getParent() {
		return parent;
	}
	public String getName() {
		return GB.getName();
	}
	public void applyArmour() {
		parent.getEquipment().setHelmet(new ItemStack(Material.getMaterial(GB.getHelmet())));
		parent.getEquipment().setChestplate(new ItemStack(Material.getMaterial(GB.getChest())));
		parent.getEquipment().setLeggings(new ItemStack(Material.getMaterial(GB.getLegs())));
		parent.getEquipment().setBoots(new ItemStack(Material.getMaterial(GB.getBoots())));
		parent.getEquipment().setItemInMainHand(new ItemStack(Material.getMaterial(GB.getWeapon())));
	}
	public void addAbilityToQueue(Ability ability) {
		abilities.add(ability);
		if(ability.getTrigger().equalsIgnoreCase("onAbilityCycle")) {
			if(!(queuedCastableAbilities .contains(ability))) {
				queuedCastableAbilities.add(ability);
			}	
		}
		else if(ability.getTrigger().equalsIgnoreCase("onAttack")) {
			if(!(queuedOnAttackAbilities.contains(ability))) {
				queuedOnAttackAbilities.add(ability);
			}	
		}
		else if(ability.getTrigger().equalsIgnoreCase("onAttacked")) {
			if(!(queuedOnAttackedAbilities.contains(ability))) {
				queuedOnAttackedAbilities.add(ability);
			}	
		}
	}
	class cycle implements Runnable{
		int task;
		int delay;
		IndividualBoss IB;
		public cycle(int delay, IndividualBoss IB, SavageBosses SB) {
			this.IB = IB;
			this.delay = delay;
			task = Bukkit.getScheduler().scheduleSyncRepeatingTask(SB, this, delay*20, delay*20);	
		}
		public void cancel() {
			Bukkit.getScheduler().cancelTask(task);	
		}
		@Override
		public void run() {
			if(parent.isDead()) {
				destroy();
				return;
			}
			if(queuedCastableAbilities.size() >= 1) {
				queuedCastableAbilities.get(0).apply(null,IB);	
				queuedCastableAbilities.remove(0);
			}
		}
	}
	public void destroy() {
		for(Ability ability: GB.getAbilities()) {
			ability.remove(this);
		}
		abilityCycle.cancel();
		Boss.unRegisterBoss(this);
		parent.remove();
		return;
	}
	protected void finalize() {
		Log.info("Garbage Collected: "+getName());
	}

	public Location getSpawn() {
		return spawnLocation;
	}

	public void getDrop() {
		ItemStack drop = GB.getDrop();
		if(!(drop.getType().equals(Material.AIR))) {
			parent.getWorld().dropItemNaturally(parent.getLocation(), drop);
		}
	}
}