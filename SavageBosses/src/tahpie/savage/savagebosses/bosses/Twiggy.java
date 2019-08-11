package tahpie.savage.savagebosses.bosses;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSkeleton;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EnchantmentManager;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.IRangedEntity;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.Navigation;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_8_R3.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.v1_8_R3.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_8_R3.World;
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
		super(world);
		this.SB = SB;
		setLocation(player.getLocation().getX(), player.getLocation().getY(),player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
		((CraftLivingEntity) this.getBukkitEntity()).setRemoveWhenFarAway(false);
		world.addEntity(this, SpawnReason.CUSTOM);
		parent = (CraftSkeleton) this.getBukkitEntity();
		parent.getEquipment().setItemInHand(new org.bukkit.inventory.ItemStack(Material.DIAMOND_AXE));
		parent.getEquipment().setBoots(new org.bukkit.inventory.ItemStack(Material.DIAMOND_BOOTS));
		parent.getEquipment().setLeggings(new org.bukkit.inventory.ItemStack(Material.IRON_LEGGINGS));
		parent.getEquipment().setChestplate(new org.bukkit.inventory.ItemStack(Material.CHAINMAIL_CHESTPLATE));
		org.bukkit.inventory.ItemStack head = new org.bukkit.inventory.ItemStack(Material.SKULL_ITEM, 0, (short) SkullType.SKELETON.ordinal());
		SkullMeta meta = ((SkullMeta)head.getItemMeta());
		parent.setMetadata("boss", new FixedMetadataValue(SB, true));
		meta.setOwner("ThePoup");
		head.setItemMeta(meta);
		parent.getEquipment().setHelmet(new org.bukkit.inventory.ItemStack(head));
		parent.setCustomName(ChatColor.DARK_RED+name);
		parent.setCustomNameVisible(true);
		try {
			Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
			bField.setAccessible(true);
			Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
			cField.setAccessible(true);
			bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
			bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
			cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
			cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
		} 
		catch (Exception exc) {
			exc.printStackTrace();
		}

		this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
		this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.0D, false));
		this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
		this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
		this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
		this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));


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
//		for(Entry<String, SpecialItem> item:SB.getItems().entrySet()) {
//			if(item.getValue().getBoss().equalsIgnoreCase(name)) {
//				runningProbability += item.getValue().getChance();
//				drops.put(item.getValue(), runningProbability);
//				Log.info(runningProbability);
//			}
//		}
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		getAttributeInstance(GenericAttributes.maxHealth).setValue(60.0);
		getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(30.0D);
		getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.35);
		getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(4);
		getAttributeInstance(GenericAttributes.c).setValue(10.0);

		//this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0f));
		//this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
		//this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
		//this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<EntityHuman>(this, EntityHuman.class, true));

	}
	@Override
	public boolean r(Entity entity){
		float f = (float)getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
		int i = 0;
		if ((entity instanceof EntityLiving)){
			f += EnchantmentManager.a(bA(), ((EntityLiving)entity).getMonsterType());
			i += EnchantmentManager.a(this);
		
		}
		i=3;
		LivingEntity target = ((LivingEntity)entity.getBukkitEntity());
		if(random.nextInt(100)+1<=20) {
			target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*3, 2));
			SavageUtility.displayMessage(ChatColor.DARK_RED+name+ChatColor.GREEN+" Slashes at your hamstring, applying a strong slow.", target); // 20% chance to slow
		}
		boolean flag = entity.damageEntity(DamageSource.mobAttack(this), f);
		if (flag){
			if (i > 0){
				entity.g(-MathHelper.sin(this.yaw * 3.1415927F / 180.0F) * i * 0.5F, 0.5D, MathHelper.cos(this.yaw * 3.1415927F / 180.0F) * i * 0.5F);
				this.motX *= 0.6D;
				this.motZ *= 0.6D;
			}
			int j = EnchantmentManager.getFireAspectEnchantmentLevel(this);
			if (j > 0){
				EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(getBukkitEntity(), entity.getBukkitEntity(), j * 4);
				Bukkit.getPluginManager().callEvent(combustEvent);
				if (!combustEvent.isCancelled()) {
					entity.setOnFire(combustEvent.getDuration());
				}
			}
			a(this, entity);
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
	public void K() { // goes once a tick
		super.K();
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
					PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.CRIT_MAGIC,true, (float) (l.getX()), (float) (l.getY()+2), (float) (l.getZ()), 0, 0, 0, 0, 1);
					for(Player online : Bukkit.getOnlinePlayers()) {						
						((CraftPlayer)online).getHandle().playerConnection.sendPacket(packet);	
					}    			
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
			target.playSound(target.getLocation(), Sound.LAVA, 25, 25);
			PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SPELL,true, (float) (target.getLocation().getX()), (float) (target.getLocation().getY()+2), (float) (target.getLocation().getZ()), 0, 0, 0, 0, 1);
			for(Player online : Bukkit.getOnlinePlayers()) {						
				((CraftPlayer)online).getHandle().playerConnection.sendPacket(packet);	
			}
			Skeleton minion = (Skeleton) target.getWorld().spawnEntity(target.getLocation(), EntityType.SKELETON);
			minion.setSkeletonType(SkeletonType.WITHER);
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
	protected void dropDeathLoot(boolean flag, int i) {
		super.dropDeathLoot(flag, i); 
		org.bukkit.inventory.ItemStack drop = getBossDrop();
		if(drop.hasItemMeta() && drop.getItemMeta().hasLore()) {
			SavageUtility.broadcastMessage(ChatColor.GOLD+"Take this, great warrior.", parent.getLocation(), 15);			
		}
		parent.getWorld().dropItemNaturally(parent.getLocation(), drop);		
	}
	public org.bukkit.inventory.ItemStack getBossDrop() {
		//    	SpecialItem item = new SpecialItem("Twiggy's Axe", "DIAMOND_AXE", enchantmentList, 5, "BLUE", "It feels hot to touch.", "Twiggy", "TahPie", 50, null);
		int number = random.nextInt(100);
		for(Entry<SpecialItem, Integer> item: drops.entrySet()) {
			Log.info(item.getValue());
			if(item.getValue() > number) {
				return item.getKey().getItem("TahPie","Twiggy");
			}
		}
		return(new org.bukkit.inventory.ItemStack(Material.BONE));
	}
}
