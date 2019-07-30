package tahpie.savage.savagebosses.bosses;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.EntityEffect;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.block.CraftCreatureSpawner;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPanda;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_14_R1.EntityTypes;
import savageclasses.Marksman;
import savageclasses.Cooldown;
import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.SavageUtility;

public class Boss implements Listener{
	Random random;
	private List<DamageCause> disallowedDamage;
	private List<DamageCause> reducedDamage;
	private Cooldown globalItemCooldown;
	public static HashMap<LivingEntity,IndividualBoss> bosses = new HashMap<LivingEntity, IndividualBoss>();
	private SavageBosses SB;
	
	public Boss(SavageBosses SB) {
		this.SB = SB;
		random = new Random();
		disallowedDamage = Arrays.asList(DamageCause.BLOCK_EXPLOSION,DamageCause.ENTITY_EXPLOSION, DamageCause.LAVA, DamageCause.FALL);
		reducedDamage = Arrays.asList(DamageCause.PROJECTILE, DamageCause.FIRE);
		globalItemCooldown = new Cooldown(1000*6);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(SB, new ensureNearbySpawn(), 20*10, 20*10);
	}
	public static void registerBoss(IndividualBoss boss) {
		bosses.put(boss.getParent(), boss);
	}
	public static void unRegisterBoss(IndividualBoss boss) {
		bosses.remove(boss.getParent());
	}
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof LivingEntity && event.getDamager() instanceof LivingEntity) {
			LivingEntity damaged = (LivingEntity)event.getEntity();
			LivingEntity damager = (LivingEntity)event.getDamager();
			if(bosses.containsKey(damager)){
				IndividualBoss boss = bosses.get(damager);
				if(boss.queuedOnAttackAbilities.size() >= 1) {
					boss.queuedOnAttackAbilities.get(0).apply(event, boss);
					boss.queuedOnAttackAbilities.remove(0);
				}	
			}
			else if(bosses.containsKey(damaged)) {
				IndividualBoss boss = bosses.get(damaged);
				if(boss.queuedOnAttackedAbilities.size() >= 1) {
					boss.queuedOnAttackedAbilities.get(0).apply(event, boss);
					boss.queuedOnAttackedAbilities.remove(0);
				}
			}
		}
	}
	@EventHandler
	public void onSpawn(SpawnerSpawnEvent event) {
		CreatureSpawner spawner = event.getSpawner();
		CraftCreatureSpawner craftSpawner = ((CraftCreatureSpawner)spawner);
		craftSpawner.setSpawnCount(2);
		Entity entity = event.getEntity();
		Block block = spawner.getWorld().getBlockAt(spawner.getLocation().subtract(0, 1, 0));
		if(block.getBlockData().getMaterial().equals(Material.OAK_SIGN)) {
			Sign sign = (Sign)block.getState(); 
			String text = sign.getLine(0);
			if(SB.getBosses().containsKey(text)) {
				int count = 0;
				for(Entity nearby : entity.getNearbyEntities(10, 10, 10)) {
					if(nearby instanceof LivingEntity) {
						count++;
					}
				}
				if(count <= 5) {
					new IndividualBoss(SB.getBosses().get(text), entity.getLocation(), SB);					
				}
				entity.remove();
			}
		}
	}
	@EventHandler
	public void onDeath(EntityDeathEvent event){
		if(event.getEntity() instanceof LivingEntity) {
			if(event.getEntity().hasMetadata("boss")) {
				event.getDrops().clear();	
			}
			Log.info(event.getDrops());
			LivingEntity dead = (LivingEntity)event.getEntity();
			if(bosses.containsKey(dead)){
				event.setDroppedExp(bosses.get(dead).getExp());
                int xp = (int) bosses.get(dead).getExp();
                ((ExperienceOrb)dead.getWorld().spawn(dead.getLocation(), ExperienceOrb.class)).setExperience(xp);
				bosses.get(dead).getDrop();
				bosses.get(dead).destroy();				
			}
		}
	}
	@EventHandler
	public void preventCertainDamage(EntityDamageEvent event) {
		if(event.getEntity().hasMetadata("boss")) {
			if(disallowedDamage.contains(event.getCause())) {
				event.setCancelled(true);
			}
			else if(reducedDamage.contains(event.getCause())) {
				event.setDamage(event.getDamage()*0.1);
			}
			CraftEntity craftEntity = (CraftEntity)event.getEntity();
			if(craftEntity.getHandle() instanceof HoardingPanda) {
				HoardingPanda panda = (HoardingPanda) ((CraftPanda) event.getEntity()).getHandle();
				panda.hurt(event);
			}
			if(craftEntity.getHandle() instanceof Twiggy) {
				Log.info("B");
			}
		}
	}
	@EventHandler
	public void fireworkExplosion(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if(item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			if(meta.hasLore()) {
				List<String> lore = meta.getLore();
				if(lore.size() >= 6) {
					if(event.getAction() == Action.LEFT_CLICK_AIR ||event.getAction() == Action.LEFT_CLICK_BLOCK) {
						if(lore.get(5).contains("Chance to make a")) {
							if(random.nextInt(100)+1>=20){
								return;
							}
							String[] sentence = lore.get(5).split(" ");
							String colour = ChatColor.stripColor(sentence[sentence.length-2].toUpperCase().replace(" ", "_"));
							Log.info(colour);
							Firework firework = player.getWorld().spawn(player.getLocation().add(0.5, 1, 0.5), Firework.class);
							FireworkMeta data = (FireworkMeta) firework.getFireworkMeta();
							data.addEffects(FireworkEffect.builder().withColor(DyeColor.valueOf(colour).getFireworkColor()).with(Type.BALL_LARGE).build());
							data.setPower(1);
							firework.setFireworkMeta(data);
						}	
					}
					if(event.getAction() == Action.RIGHT_CLICK_AIR ||event.getAction() == Action.RIGHT_CLICK_BLOCK) {
						if(lore.get(5).contains("reveal")) {
							reveal(item, player);
						}
						else if(lore.get(5).contains("buff")) {
							bardsBuff(item, player);
						}
						else if(lore.get(5).contains("invincibility")) {
							paladinsAura(item, player);
						}
						else if(lore.get(5).contains("fire ticks")){
							pyrosFlame(item, player);
						}
						else if(lore.get(5).contains("ride")) {
							beastsEscape(item, player);
						}
						else if(lore.get(5).contains("leap")) {
							marksmansLeap(item, player);
						}
					}
				}
			}
		}
	}
	public class ensureNearbySpawn implements Runnable{
		public ensureNearbySpawn() {
		}
		@Override
		public void run() {
			for(Entry<LivingEntity, IndividualBoss> entry: new HashMap<LivingEntity, IndividualBoss>(bosses).entrySet()) {
				if(entry.getValue().getSpawn().distance(entry.getKey().getLocation())>=20) {
					SavageUtility.broadcastMessage(ChatColor.DARK_GRAY+entry.getValue().getName()+": I have wandered too far...", entry.getKey().getLocation(), 15);
					entry.getValue().destroy();
				}
				else if(entry.getKey().getTicksLived() >= 20*100) {
					SavageUtility.broadcastMessage(ChatColor.DARK_GRAY+entry.getValue().getName()+": I am too old...", entry.getKey().getLocation(), 15);
					entry.getValue().destroy();	
				}
			}	
		}
	}
	private void marksmansLeap(ItemStack item, Player player) {
		if(globalItemCooldown.isOnCooldown(player)) {
            SavageUtility.displayMessage(ChatColor.RED + "You cannot use " + ChatColor.GOLD + "Custom Items " + ChatColor.RED + "again for " + ChatColor.AQUA + globalItemCooldown.getRemainingTime(player) + ChatColor.RED + " seconds.", player);
			return;
		}
		item.setAmount(item.getAmount()-1);
        SavageUtility.broadcastMessage(player.getName() + "&6 leaps away!", player.getLocation(), 15);
        Vector v = player.getLocation().getDirection();
        double leapVelocity = 4.444444444444445;
        double leapYVelocity = 1.4285714285714286;

        v.setY(0).normalize().multiply(leapVelocity).setY(leapYVelocity);
        player.setVelocity(v);

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_SMALL_FALL, 25.0F, 25.0F);
        player.playEffect(EntityEffect.WOLF_SMOKE);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 60, 2));

        Marksman.isLeaping.add(player.getName());
        globalItemCooldown.addCooldown(player);
        
		SavageUtility.broadcastMessage(ChatColor.GOLD+player.getName()+ChatColor.AQUA+" Uses "+ChatColor.DARK_PURPLE+"Marksman's Leap"+ChatColor.AQUA+" to fly across the skies.", player.getLocation(), 15);		
	}
	private void beastsEscape(ItemStack item, Player player) {
		if(globalItemCooldown.isOnCooldown(player)) {
            SavageUtility.displayMessage(ChatColor.RED + "You cannot use " + ChatColor.GOLD + "Custom Items " + ChatColor.RED + "again for " + ChatColor.AQUA + globalItemCooldown.getRemainingTime(player) + ChatColor.RED + " seconds.", player);
			return;
		}
		item.setAmount(item.getAmount()-1);
		SavageUtility.broadcastMessage(ChatColor.GOLD+player.getName()+ChatColor.AQUA+" Uses "+ChatColor.DARK_PURPLE+"Beast's Escape"+ChatColor.AQUA+" to summon a fast Pwnda.", player.getLocation(), 15);
        new Beast(EntityTypes.PANDA, ((CraftWorld)(player.getWorld())).getHandle(), player);
	}
	private void pyrosFlame(ItemStack item, Player player) {
		if(globalItemCooldown.isOnCooldown(player)) {
            SavageUtility.displayMessage(ChatColor.RED + "You cannot use " + ChatColor.GOLD + "Custom Items " + ChatColor.RED + "again for " + ChatColor.AQUA + globalItemCooldown.getRemainingTime(player) + ChatColor.RED + " seconds.", player);
			return;
		}
		Player target = (Player) SavageUtility.getInfront(player, 15, Player.class, "notInParty");
		if(target == null) {
			SavageUtility.displayMessage(ChatColor.RED + "You must have a player target not in your party.",player);
			return;
		}
		SavageUtility.broadcastMessage(ChatColor.GOLD+player.getName()+ChatColor.AQUA+" Casts "+ChatColor.DARK_PURPLE+"Pyro's Flame"+ChatColor.AQUA+" to inflict a weak burn on "+ChatColor.GOLD+target.getName()+ChatColor.AQUA+".", player.getLocation(), 15);
		target.setFireTicks(20*4);
		globalItemCooldown.addCooldown(player);
	}
	public void reveal(ItemStack item, Player player) {
		if(globalItemCooldown.isOnCooldown(player)) {
            SavageUtility.displayMessage(ChatColor.RED + "You cannot use " + ChatColor.GOLD + "Custom Items " + ChatColor.RED + "again for " + ChatColor.AQUA + globalItemCooldown.getRemainingTime(player) + ChatColor.RED + " seconds.", player);
			return;
		}
		item.setAmount(item.getAmount()-1);
		SavageUtility.broadcastMessage(ChatColor.GOLD+player.getName()+ChatColor.AQUA+" Uses "+ChatColor.DARK_PURPLE+"Assassin's Bane"+ChatColor.AQUA+" to reveal nearby assassins.", player.getLocation(), 15);
		for(Entity nearby: SavageUtility.getNearbyEntities(player, 15, Player.class, false)) {
			if(!(SavageUtility.getPartyMembers((Player)nearby).contains(player))) {
				((Player)nearby).damage(1);
				((Player)nearby).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20*5, 1));
				SavageUtility.displayMessage(ChatColor.AQUA+"Your skin glows brightly.", nearby);
			}
		}
		globalItemCooldown.addCooldown(player);
	}
	public void paladinsAura(ItemStack item, Player player) {
		if(globalItemCooldown.isOnCooldown(player)) {
            SavageUtility.displayMessage(ChatColor.RED + "You cannot use " + ChatColor.GOLD + "Custom Items " + ChatColor.RED + "again for " + ChatColor.AQUA + globalItemCooldown.getRemainingTime(player) + ChatColor.RED + " seconds.", player);
			return;
		}
		item.setAmount(item.getAmount()-1);
		SavageUtility.broadcastMessage(ChatColor.GOLD+player.getName()+ChatColor.AQUA+" Uses "+ChatColor.DARK_PURPLE+"Paladin's Aura"+ChatColor.AQUA+" to gain invincibility for 5 seconds.", player.getLocation(), 15);
		((CraftPlayer)player).setNoDamageTicks(20*5);
		globalItemCooldown.addCooldown(player);
	}
	public void bardsBuff(ItemStack item, Player player) {
		if(globalItemCooldown.isOnCooldown(player)) {
            SavageUtility.displayMessage(ChatColor.RED + "You cannot use " + ChatColor.GOLD + "Custom Items " + ChatColor.RED + "again for " + ChatColor.AQUA + globalItemCooldown.getRemainingTime(player) + ChatColor.RED + " seconds.", player);
			return;
		}
		item.setAmount(item.getAmount()-1);
		SavageUtility.broadcastMessage(ChatColor.GOLD+player.getName()+ChatColor.AQUA+" Uses "+ChatColor.DARK_PURPLE+"Bard's Buff"+ChatColor.AQUA+" to buff his party.", player.getLocation(), 15);
		for(Entity nearby: SavageUtility.getPartyMembers(player)) {
			if(nearby.getWorld().equals(player.getWorld()) && nearby.getLocation().distance(player.getLocation())<= 30) {
				Player target = (Player)nearby;
				target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*30, 1));
				target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*30, 1));
				target.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*30, 1));
				target.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*30, 0));
				target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*30, 1));
				SavageUtility.displayMessage(ChatColor.GOLD+"You received a powerful "+ChatColor.DARK_PURPLE+"Bard's Buff"+ChatColor.GOLD+".", target);		
			}
			else {
				SavageUtility.displayMessage(ChatColor.GOLD+player.getName()+ChatColor.AQUA+" Uses "+ChatColor.DARK_PURPLE+"Bard's Buff"+ChatColor.AQUA+" but you are too far to receive the buff.", nearby);
			}
		}
		globalItemCooldown.addCooldown(player);
	}
}
