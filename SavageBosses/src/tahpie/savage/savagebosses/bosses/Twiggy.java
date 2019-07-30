package tahpie.savage.savagebosses.bosses;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_14_R1.Blocks;
import net.minecraft.server.v1_14_R1.DamageSource;
import net.minecraft.server.v1_14_R1.EnchantmentManager;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.EntityIronGolem;
import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.EntityPig;
import net.minecraft.server.v1_14_R1.EntityPigZombie;
import net.minecraft.server.v1_14_R1.EntitySkeleton;
import net.minecraft.server.v1_14_R1.EntityTurtle;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.EntityVillagerAbstract;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.GenericAttributes;
import net.minecraft.server.v1_14_R1.IRangedEntity;
import net.minecraft.server.v1_14_R1.ItemAxe;
import net.minecraft.server.v1_14_R1.ItemStack;
import net.minecraft.server.v1_14_R1.ItemToolMaterial;
import net.minecraft.server.v1_14_R1.Items;
import net.minecraft.server.v1_14_R1.MathHelper;
import net.minecraft.server.v1_14_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_14_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_14_R1.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.v1_14_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_14_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_14_R1.PathfinderGoalRandomStrollLand;
import net.minecraft.server.v1_14_R1.PathfinderGoalZombieAttack;
import net.minecraft.server.v1_14_R1.Vec3D;
import net.minecraft.server.v1_14_R1.World;
import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.SavageUtility;
import tahpie.savage.savagebosses.questitems.SpecialItem;

public class Twiggy extends EntitySkeleton implements IRangedEntity {
	Skeleton parent;
	int delay = 20*10;
	int counter = 0;
	String name = "Twiggy";
	int curseAOE = 4;
	List<Method> abilities = new ArrayList<Method>();
	int abilityCounter = 0;
	SavageBosses SB;
	HashMap<SpecialItem, Integer> drops;
	
	public Twiggy(Player player, World world,SavageBosses SB){
		super(EntityTypes.SKELETON, world);
		this.SB = SB;
		this.enderTeleportAndLoad(player.getLocation().getX(),player.getLocation().getY() , player.getLocation().getZ());
		parent = (Skeleton)this.getBukkitEntity();
		world.addEntity(this);
		parent.getEquipment().setItemInMainHand(new org.bukkit.inventory.ItemStack(Material.DIAMOND_AXE));
		parent.getEquipment().setBoots(new org.bukkit.inventory.ItemStack(Material.DIAMOND_BOOTS));
		parent.getEquipment().setLeggings(new org.bukkit.inventory.ItemStack(Material.IRON_LEGGINGS));
		parent.getEquipment().setChestplate(new org.bukkit.inventory.ItemStack(Material.CHAINMAIL_CHESTPLATE));
		org.bukkit.inventory.ItemStack head = new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = ((SkullMeta)head.getItemMeta());
		parent.setMetadata("boss", new FixedMetadataValue(SB, true));
		meta.setOwner("ThePoup");
		head.setItemMeta(meta);
		parent.getEquipment().setHelmet(new org.bukkit.inventory.ItemStack(head));
		parent.setCustomName(ChatColor.DARK_RED+name);
		parent.setCustomNameVisible(true);
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
			// TODO Auto-generated catch block
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
				Log.info(runningProbability);
			}
		}
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(60.0);
        getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(30.0D);
        getAttributeInstance(GenericAttributes.ARMOR).setValue(5);
        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25);
        getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(4);
        getAttributeInstance(GenericAttributes.ATTACK_KNOCKBACK).setValue(10.0);
	}


    @Override
    protected void initPathfinder() {
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0f));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<EntityHuman>(this, EntityHuman.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<EntityIronGolem>(this, EntityIronGolem.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<EntityTurtle>(this, EntityTurtle.class, 10, true, false, EntityTurtle.bz));
    }
    
    @Override
    public boolean C(Entity entity) {
        boolean flag;
        int i2;
        float f2 = (float)this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
        float f1 = (float)this.getAttributeInstance(GenericAttributes.ATTACK_KNOCKBACK).getValue();
        if (entity instanceof EntityLiving) {
            f2 += EnchantmentManager.a(this.getItemInMainHand(), ((EntityLiving)entity).getMonsterType());
            f1 += (float)EnchantmentManager.b(this);
        }
    	LivingEntity target = ((LivingEntity)entity.getBukkitEntity());
        if(random.nextInt(100)+1<=20) {
        	target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*3, 2));
        	SavageUtility.displayMessage(ChatColor.DARK_RED+name+ChatColor.GREEN+" Slashes at your hamstring, applying a strong slow.", target); // 20% chance to slow
        }
        if (flag = entity.damageEntity(DamageSource.mobAttack(this), f2)) {
            if (f1 > 0.0f && entity instanceof EntityLiving) {
                //((EntityLiving)entity).a(this, f1 * 0.5f, (double)MathHelper.sin(this.yaw * 0.017453292f), -MathHelper.cos(this.yaw * 0.017453292f)); origial nms code
                this.setMot(this.getMot().d(0.6, 1.0, 0.6)); // not sure what this does
                
                Vec3D vec3d = entity.getMot(); // 0.017453292 is pi/180 (used in conversion to radians) 
                Vec3D vec3d1 = new Vec3D((double)MathHelper.sin(this.yaw * 0.017453292f), 0.0, -MathHelper.cos(this.yaw * 0.017453292f)).d().a((double)f1*(0.5f)); // knockback logic 
                entity.setMot(vec3d.x / 2.0 - vec3d1.x, 0.8, vec3d.z / 2.0 - vec3d1.z); // basically apply force in direction attacker is looking (0.5 added for knock up effect) 
            }
            if (entity instanceof EntityHuman) {
                ItemStack itemstack1;
                EntityHuman entityhuman = (EntityHuman)entity;
                ItemStack itemstack = this.getItemInMainHand();
                ItemStack itemStack = itemstack1 = entityhuman.isHandRaised() ? entityhuman.dm() : ItemStack.a;
                if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem() instanceof ItemAxe && itemstack1.getItem() == Items.SHIELD) { 
                    float f22 = 0.25f + (float)EnchantmentManager.getDigSpeedEnchantmentLevel(this) * 0.05f;
                    if (this.random.nextFloat() < f22) {
                        entityhuman.getCooldownTracker().a(Items.SHIELD, 100);
                        this.world.broadcastEntityEffect(entityhuman, (byte)30);
                    } // I thought this triggered a cooldown when you block an attack, but apparently not? 
                }
            }
            this.a(this, entity);
        }
        return flag;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f2) { // triggers when boss takes damage
    	boolean toReturn = super.damageEntity(damagesource, f2);
    	if(f2>=3) { // prevent things like snowball damage from triggering this ability
    		if(random.nextInt(100)+1<=20) { //prevent boss from getting combo'd to hard in corner
    			teleportToEnemy();		
    		}
    	}
    	return toReturn;
    }
    
    @Override
    public void entityBaseTick() { // goes once a tick
    	super.entityBaseTick();
    	counter++;
    	
    	if(counter>=delay) { // use a scheduled ability 
    		counter = 0;
    		try {
				abilities.get(abilityCounter % abilities.size()).invoke(this); //reflection is slow but easy to code. (executes ability)
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.info("INVALID METHOD???");
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
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*5, 0));
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
    	int x = l.getBlockX() - curseAOE;
    	int y = l.getBlockY() - curseAOE;
    	int z = l.getBlockZ() - curseAOE;
    	
    	for (int i = x; i < x + 2*curseAOE; i++){
    		for (int j = y; j < y + 2*curseAOE; j++){
    			for (int k = z; k < z + 2*curseAOE; k++){
    				Location newL = new Location(l.getWorld(), i, j, k); 
    				l.getWorld().spawnParticle(Particle.CRIT_MAGIC, newL, 1, 0, 0, 0);
    			}
    		}
    	}
    	for(org.bukkit.entity.Entity nearby: nearbyEntities) {
    		Player target = (Player)nearby;
    		target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3*20, 0));
    		target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 3*20, 0));
    		target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 3));
    		target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3*20, 3));
    		target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 3*20, 0));
    		target.damage(3);
    		target.playSound(target.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 25, 25);
    		target.getWorld().spawnParticle(Particle.SPELL, target.getLocation(), 1, 0, 0, 0);  
    		org.bukkit.entity.Entity minion = target.getWorld().spawnEntity(target.getLocation(), EntityType.WITHER_SKELETON);
			minion.setCustomName(ChatColor.LIGHT_PURPLE+"Twiggy's Minion");
			minion.setCustomNameVisible(true);
			minion.setMetadata("minion", new FixedMetadataValue(SB, true));

			
    	}
        SavageUtility.broadcastMessage(ChatColor.DARK_RED+name+ChatColor.GREEN+" Inflicts a weak curse to all nearby enemies.", parent.getLocation(), 20);
    }
    public void buffSelf() {
    	parent.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*10, 2));
    	parent.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*10, 2));
    	parent.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*10, 2));
    	parent.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*10, 2));
    	parent.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*10, 1));
    	parent.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20*10, 2));
    	
        SavageUtility.broadcastMessage(ChatColor.DARK_RED+name+ChatColor.GREEN+" Is enraged; it sharply buffs itself.", parent.getLocation(), 20);
    }
    public void teleportHereEnemy() {
    	List<LivingEntity> nearbyEntities = SavageUtility.getNearbyEntities(parent, 15, Player.class, false);
    	for(org.bukkit.entity.Entity nearby: nearbyEntities) {
    		Player target = (Player)nearby;
    		target.teleport(parent);
            target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*5, 0));
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20*2, 3));
            target.damage(4);
            if(SavageUtility.removePositiveBuffs(target)) {
            	SavageUtility.displayMessage(ChatColor.DARK_RED+name+ChatColor.GREEN+" Removes any positive buffs.", target);
            }
    	}
        SavageUtility.broadcastMessage(ChatColor.DARK_RED+name+ChatColor.GREEN+" Teleports all nearby enemies to itself.", parent.getLocation(), 20);
    }
    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i2, boolean flag) {
    	if(damagesource.equals(DamageSource.playerAttack(killer))) {
    		Log.info("HI");
    	}
    	super.dropDeathLoot(damagesource, i2, flag); 
    	if(random.nextInt(100)+1<=50) {
    		org.bukkit.inventory.ItemStack drop = getBossDrop();
    		SavageUtility.broadcastMessage(ChatColor.GOLD+"Take this, great warrior.", parent.getLocation(), 15);
    		parent.getWorld().dropItemNaturally(parent.getLocation(), drop);		
    	}
    }
    public org.bukkit.inventory.ItemStack getBossDrop() {
//    	SpecialItem item = new SpecialItem("Twiggy's Axe", "DIAMOND_AXE", enchantmentList, 5, "BLUE", "It feels hot to touch.", "Twiggy", "TahPie", 50, null);
    	int number = random.nextInt(100)+1;
    	Log.info(number);
    	for(Entry<SpecialItem, Integer> item: drops.entrySet()) {
    		Log.info(item.getValue());
    		if(item.getValue() <= number) {
    			return item.getKey().getItem();
    		}
    	}
    	return(new org.bukkit.inventory.ItemStack(Material.BONE));
    }
}
