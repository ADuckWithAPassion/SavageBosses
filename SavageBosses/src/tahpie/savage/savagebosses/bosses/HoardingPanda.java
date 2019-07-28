package tahpie.savage.savagebosses.bosses;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_14_R1.DamageSource;
import net.minecraft.server.v1_14_R1.EntityInsentient;
import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.EntityPanda;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.EnumMoveType;
import net.minecraft.server.v1_14_R1.GenericAttributes;
import net.minecraft.server.v1_14_R1.PathfinderGoal;
import net.minecraft.server.v1_14_R1.PathfinderGoalAvoidTarget;
import net.minecraft.server.v1_14_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_14_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_14_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_14_R1.World;
import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.SavageUtility;
import tahpie.savage.savagebosses.questitems.SpecialItem;

public class HoardingPanda extends EntityPanda implements Listener{
	Panda parent;
	int delay = 20*15;
	int counter = 0;
	String name = "Hoarding Panda";
	int roarAOE = 5;
	boolean strongBuff = false;
	boolean finalBuff = false;
	List<Method> abilities = new ArrayList<Method>();
	int abilityCounter = 0;
	HashMap<SpecialItem, Integer> drops;
	SavageBosses SB;
	private double SPAWN_X;
	private double SPAWN_Y;
	private double SPAWN_Z;
	
	public HoardingPanda(Player player, World world,SavageBosses SB){
		super(EntityTypes.PANDA, world);
		this.SB = SB;
		SPAWN_X = player.getLocation().getX();
		SPAWN_Y = player.getLocation().getY();
		SPAWN_Z = player.getLocation().getZ();
		spawn();
		parent = (Panda)this.getBukkitEntity();
		world.addEntity(this);
		SavageUtility.addBossUUID(this.getUniqueID());
		parent.setMetadata("boss", new FixedMetadataValue(SB, true));
		parent.setCustomName(ChatColor.DARK_RED+name);
		parent.setCustomNameVisible(true);
		parent.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 5000*20, 0));
		this.loadDrops();
		this.loadAbiltiies();
	}
	public void loadAbiltiies() {
	    try {
			abilities.add(this.getClass().getDeclaredMethod("teleportToEnemy"));
			abilities.add(this.getClass().getDeclaredMethod("curseEnemy"));
			abilities.add(this.getClass().getDeclaredMethod("buffSelf"));
			abilities.add(this.getClass().getDeclaredMethod("teleportHereEnemy"));
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			Log.info("ABILITY TYPO");
		}
	}
	public void loadDrops() {
		drops = new HashMap<SpecialItem, Integer>();
		int runningProbability = 0;
		for(Entry<String, SpecialItem> item:SB.getItems().entrySet()) {
			if(item.getValue().getBoss().equalsIgnoreCase(name)) {
				runningProbability += item.getValue().getChance();
				drops.put(item.getValue(), runningProbability);
			}
		}
	}
	public void spawn() {
		this.enderTeleportAndLoad(SPAWN_X,SPAWN_Y,SPAWN_Z);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(60.0);
        getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(20.0D);
        getAttributeInstance(GenericAttributes.ARMOR).setValue(15);
        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25);
        getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(4);
        getAttributeInstance(GenericAttributes.ATTACK_KNOCKBACK).setValue(10.0);
        getAttributeInstance(GenericAttributes.KNOCKBACK_RESISTANCE).setValue(10.0);
	}


    @Override
    protected void initPathfinder() {
    	super.initPathfinder();
    	this.setMainGene(Gene.WORRIED);    	
    }
    
    @Override
    public void entityBaseTick() { // goes once a tick
    	super.entityBaseTick();
    	counter++;
    	
    	if(this.getHealth() <= this.getMaxHealth()/10 && finalBuff == false) {
    		finalBuff = true;
    		counter=-10;
    		delay = 4*20;
    		SavageUtility.broadcastMessage(ChatColor.DARK_PURPLE+"[WARNING] "+ChatColor.GOLD+"The "+ChatColor.DARK_RED+name+ChatColor.GOLD+" screeches for his children to protect him.", parent.getLocation(), 15);
			parent.getLocation().getWorld().spawnParticle(Particle.SPIT, parent.getLocation(), 1, 0, 0, 0);		
			parent.getLocation().getWorld().playSound(parent.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1, 1);
    		parent.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 20*3, 5));
    		parent.setHealth(parent.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()/3);
			teleportToEnemy();
			curseEnemy();
    		buffSelf();
    		abilities.remove(0);
    		abilities.remove(1);
    		abilities.remove(1);
    	}
    	
    	else if(this.getHealth() <= this.getMaxHealth()/2 && strongBuff == false) {
    		strongBuff = true;
    		SavageUtility.broadcastMessage(ChatColor.GOLD+"The Hoarding Panda gets angry...", parent.getLocation(), 15);
			parent.getLocation().getWorld().spawnParticle(Particle.VILLAGER_ANGRY, parent.getLocation(), 1, 0, 0, 0);		
			parent.getLocation().getWorld().playSound(parent.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1, 1);
    		delay = 5*20;
    		counter = delay;
    		parent.setHealth(parent.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

            this.goalSelector = new PathfinderGoalSelector(world != null && world.getMethodProfiler() != null ? world.getMethodProfiler() : null);
            this.targetSelector = new PathfinderGoalSelector(world != null && world.getMethodProfiler() != null ? world.getMethodProfiler() : null);
            this.setMainGene(Gene.AGGRESSIVE);
//            super.initPathfinder();

            this.targetSelector.a(1, new f(this, new Class[0]).a(new Class[0]));
            this.goalSelector.a(3, new b(this, 1.2000000476837158, true));
            //this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<EntityHuman>(this, EntityHuman.class, true)); TEMP
            //this.targetSelector.a(0, new PathfinderGoalHurtByTarget(this, new Class[0]));
            //this.goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
            //this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        }
    	
    	if(counter>=delay) { // use a scheduled ability 
    		counter = 0;
    		try {
				abilities.get(abilityCounter % abilities.size()).invoke(this); //reflection is slow but easy to code. (executes ability)
		        parent.setNoDamageTicks(20*1);

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				Log.info("INVALID METHOD???");
				Log.info("ENTITY: "+name);
				Log.info("ABILITY COUNTER: "+abilityCounter);
				Log.info("ABILITIES: "+abilities);
				Log.info("ALIVE: "+parent.isDead());
			}
    		abilityCounter++;
    	}
    }
    
    public void teleportToEnemy() {
    	List<LivingEntity> nearbyEntities = SavageUtility.getNearbyEntities(parent, 15, Player.class, false);
    	if(nearbyEntities.size() == 0) {
    		return;
    	}
        org.bukkit.entity.Player target = (Player)nearbyEntities.get(random.nextInt(nearbyEntities.size())); // select random player
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*8, 0));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20*2, 3));
        target.damage(4);
        target.getWorld().createExplosion(target.getLocation(), 1);
        parent.teleport(target);
        SavageUtility.broadcastMessage(ChatColor.DARK_RED+name+ChatColor.GREEN+" Teleports to a random nearby enemy.", parent.getLocation(), 20);
        SavageUtility.displayMessage(ChatColor.DARK_RED+name+ChatColor.GREEN+" Teleports to you, inflicting minor damage.", target);
    }
    public void curseEnemy() {
    	List<LivingEntity> nearbyEntities = SavageUtility.getNearbyEntities(parent, 15, Player.class, false);
    	Location l = parent.getLocation();
    	int x = l.getBlockX() - roarAOE;
    	int y = l.getBlockY() - roarAOE;
    	int z = l.getBlockZ() - roarAOE;
    	
    	for (int i = x; i < x + 2*roarAOE; i++){
    		for (int j = y; j < y + 2*roarAOE; j++){
    			for (int k = z; k < z + 2*roarAOE; k++){
    				if(x % 2 == 0 && y % 2 ==0 && z % 2 ==0) { // reduce possible client lag
    					Location newL = new Location(l.getWorld(), i, j, k); 
        				l.getWorld().spawnParticle(Particle.SQUID_INK, newL, 1, 0, 0, 0);		
    				}
    			}
    		}
    	}
    	for(org.bukkit.entity.Entity nearby: nearbyEntities) {
    		Player target = (Player)nearby;
    		target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 3*20, 2));
    		target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1*20, 0));
    		target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 3));
    		target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3*20, 3));
    		target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 3*20, 0));
    		target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3*20, 0));
    		target.damage(3);
    		if(target.getLocation().distance(parent.getLocation())>=1) {
    			try {
        			target.setVelocity(target.getLocation().subtract(parent.getLocation()).toVector().normalize().setY(1));
        		}
        		catch(IllegalArgumentException error) {
        			error.printStackTrace();
        			Log.info("Random bug, not too sure what causes this. Safely held in try/catch so server will not crash.");
        			Log.info(parent);
        			Log.info(parent.getLocation());
        			Log.info(target.getLocation());
        		}	
    		}
    		target.playSound(target.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 25, 25);
    		target.getWorld().spawnParticle(Particle.SPELL, target.getLocation(), 1, 0, 0, 0);  
    		//Panda minion = (Panda) target.getWorld().spawnEntity(target.getLocation(), EntityType.PANDA);
			new HoardingPandaChild(EntityTypes.PANDA, world,target,SB);
    	}
        SavageUtility.broadcastMessage(ChatColor.DARK_RED+name+ChatColor.GREEN+" Inflicts a weak curse to all nearby enemies.", parent.getLocation(), 20);
    }
    public void buffSelf() {
    	parent.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*10, 1));
    	parent.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*10, 2));
    	parent.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*10, 2));
    	parent.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*10, 2));
    	parent.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*10, 1));
    	parent.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20*10, 2));
    	
        SavageUtility.broadcastMessage(ChatColor.DARK_RED+name+ChatColor.GREEN+" Is enraged; it sharply buffs itself.", parent.getLocation(), 20);
    }
    public void teleportHereEnemy() {
    	List<LivingEntity> nearbyEntities = SavageUtility.getNearbyEntities(parent, 15, Player.class, false);
    	for(org.bukkit.entity.Entity nearby: nearbyEntities) {
    		Player target = (Player)nearby;
    		target.teleport(parent);
            target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*8, 0));
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20*2, 3));
            target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20*2, 0));
            target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20*2, 0));
            target.damage(4);
            parent.addPassenger(target);
            if(SavageUtility.removePositiveBuffs(target)) {
            	SavageUtility.displayMessage(ChatColor.DARK_RED+name+ChatColor.GREEN+" Removes any positive buffs.", target);
            }
    	}
        SavageUtility.broadcastMessage(ChatColor.DARK_RED+name+ChatColor.GREEN+" Teleports all nearby enemies to itself.", parent.getLocation(), 20);
    }
    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i2, boolean flag) {
    	super.dropDeathLoot(damagesource, i2, flag); 
    	org.bukkit.inventory.ItemStack drop = getBossDrop();
    	SavageUtility.broadcastMessage(ChatColor.GOLD+"Take this, great warrior.", parent.getLocation(), 15);
    	parent.getWorld().dropItemNaturally(parent.getLocation(), drop);

    }
    public org.bukkit.inventory.ItemStack getBossDrop() {
//    	SpecialItem item = new SpecialItem("Twiggy's Axe", "DIAMOND_AXE", enchantmentList, 5, "BLUE", "It feels hot to touch.", "Twiggy", "TahPie", 50, null);
    	int number = random.nextInt(100)+1;
    	Log.info(number);
    	for(Entry<SpecialItem, Integer> item: drops.entrySet()) {
    		Log.info(item);
        	parent.getWorld().dropItemNaturally(parent.getLocation(), item.getKey().getItem());
    		if(item.getValue() >= number) {
    			return item.getKey().getItem();
    		}
    	}
    	return(new org.bukkit.inventory.ItemStack(Material.BAMBOO));
    }
    @Override
    public void die() {
    	SavageUtility.removeBossUUID(this.getUniqueID());
    	super.die();
    }
    
    static class f
    extends PathfinderGoalHurtByTarget { // slightly modified pathfinder event from EntityPanda
        private final EntityPanda a;

        public f(EntityPanda entitypanda, Class<?> ... aclass) {
            super(entitypanda, aclass);
            this.a = entitypanda;
        }

        @Override
        protected void a(EntityInsentient entityinsentient, EntityLiving entityliving) {
            if (entityinsentient instanceof EntityPanda && ((EntityPanda)entityinsentient).dS()) { // checks angry
                entityinsentient.setGoalTarget(entityliving, EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY, true); // set target
            }
        }
    }
    static class b
    extends PathfinderGoalMeleeAttack {
        private final EntityPanda d;

        public b(EntityPanda entitypanda, double d0, boolean flag) {
            super(entitypanda, d0, flag);
            this.d = entitypanda;
        }

        @Override
        public boolean a() {
            return this.d.er() && super.a();
        }
    }

	public boolean hurt(EntityDamageEvent event) { // triggers when boss takes damage
    	if(event.getCause().equals(DamageCause.SUFFOCATION) || event.getCause().equals(DamageCause.VOID)) {
    		SavageUtility.broadcastMessage(ChatColor.DARK_RED+name+ChatColor.GREEN+" Has teleported back to their spawn", parent.getLocation(), 15);
    		spawn();
    		return false; 
    	}
    	Log.info(event.getDamage());
    	if(event.getDamage() >= 7) {
    		event.setDamage(7);
    	}
    	if(event.getDamage() >= 4) {
    		if(random.nextInt(100)+1 <= 20) {
    			this.teleportToEnemy();
    			return false;
    		}
    	}
    	return true;
    }


}
