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
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.citizensnpcs.api.CitizensAPI;
import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.SavageUtility;

public class Boss implements Listener{
	Random random;
	private List<DamageCause> disallowedDamage;
	private List<DamageCause> reducedDamage;
	private apallo.savage.savageclasses.Cooldown globalItemCooldown;
	public static HashMap<LivingEntity,IndividualBoss> bosses = new HashMap<LivingEntity, IndividualBoss>();
	private SavageBosses SB;
	List<ChatColor> explosionColours = Arrays.asList(ChatColor.AQUA, ChatColor.BLACK, ChatColor.BLUE, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE, ChatColor.RED, ChatColor.GOLD, ChatColor.DARK_GRAY, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.WHITE, ChatColor.YELLOW, ChatColor.GRAY);
	List<DyeColor> explosionFireworks = Arrays.asList(DyeColor.CYAN,DyeColor.BLACK,DyeColor.LIGHT_BLUE, DyeColor.BLUE, DyeColor.GREEN, DyeColor.PURPLE, DyeColor.RED, DyeColor.ORANGE, DyeColor.GRAY,DyeColor.LIME, DyeColor.PINK, DyeColor.WHITE, DyeColor.YELLOW, DyeColor.SILVER);
	HashMap<String, DyeColor> explosionDye = new HashMap<String, DyeColor>();

	public Boss(SavageBosses SB) {
		explosionDye.put("AQUA", DyeColor.CYAN);
		explosionDye.put("BLACK", DyeColor.BLACK);
		explosionDye.put("BLUE", DyeColor.LIGHT_BLUE);
		explosionDye.put("DARK_BLUE", DyeColor.BLUE);
		explosionDye.put("DARK_PURPLE", DyeColor.PURPLE);
		explosionDye.put("RED", DyeColor.RED);
		explosionDye.put("GOLD", DyeColor.ORANGE);
		explosionDye.put("DARK_GRAY", DyeColor.GRAY);
		explosionDye.put("GREEN", DyeColor.GREEN);
		explosionDye.put("LIGHT_PURPLE", DyeColor.PINK);
		explosionDye.put("WHITE", DyeColor.WHITE);
		explosionDye.put("YELLOW", DyeColor.YELLOW);
		explosionDye.put("GRAY", DyeColor.SILVER);

		this.SB = SB;
		random = new Random();
		disallowedDamage = Arrays.asList(DamageCause.BLOCK_EXPLOSION,DamageCause.ENTITY_EXPLOSION, DamageCause.LAVA, DamageCause.FALL);
		reducedDamage = Arrays.asList(DamageCause.PROJECTILE, DamageCause.FIRE);
		globalItemCooldown = new apallo.savage.savageclasses.Cooldown(1000*6);
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
		//CraftCreatureSpawner craftSpawner = ((CraftCreatureSpawner)spawner);
		Entity entity = event.getEntity();
		Block block = spawner.getWorld().getBlockAt(spawner.getLocation().subtract(0, 1, 0));
		if(block.getType().equals(Material.SIGN_POST) && (block.getWorld().getName().equals("RPG"))) {
			Sign sign = (Sign)block.getState(); 
			String text = sign.getLine(0);
			if(SB.getCustomBosses().containsKey(text)) {
				int count = 0;
				for(Entity nearby : entity.getNearbyEntities(10, 10, 10)) {
					if(nearby instanceof LivingEntity) {
						count++;
					}
				}
				if(count <= 5) {
					new IndividualBoss(SB.getCustomBosses().get(text), entity.getLocation(), SB);					
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
			//CraftEntity craftEntity = (CraftEntity)event.getEntity();
			//			if(craftEntity.getMetadata("boss").equals(true)) {
			//				Twiggy twiggy = (Twiggy) ((Twiggy) event.getEntity()).getHandle();
			//				twiggy.hurt(event);
			//			}
		}
	}
	@EventHandler
	public void customEffects(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInHand();
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
							data.addEffects(FireworkEffect.builder().withColor(explosionDye.get(colour).getFireworkColor()).with(Type.BALL_LARGE).build());
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
				Log.info(new HashMap<LivingEntity, IndividualBoss>(bosses).entrySet());
				Log.info(entry);
				if(entry.getValue().getSpawn().distance(entry.getKey().getLocation())>=30) {
					SavageUtility.broadcastMessage(ChatColor.DARK_GRAY+entry.getValue().getName()+": I have wandered too far...", entry.getKey().getLocation(), 15);
					entry.getValue().destroy();
				}
				else if(entry.getKey().getTicksLived() >= 20*60) {
					SavageUtility.broadcastMessage(ChatColor.DARK_GRAY+entry.getValue().getName()+": I am too old...", entry.getKey().getLocation(), 15);
					entry.getValue().destroy();	
				}
				if(entry.getValue().getSpawn().distance(entry.getKey().getLocation())>=30) {
					Log.info("TOO FAR");
					SavageUtility.broadcastMessage(ChatColor.DARK_GRAY+entry.getValue().getName()+": I have wandered too far...", entry.getKey().getLocation(), 15);
					entry.getValue().destroy();
				}
				else {
					boolean found = false;
					for(Player nearby: entry.getKey().getLocation().getWorld().getEntitiesByClass(Player.class)) {
						if(!CitizensAPI.getNPCRegistry().isNPC(nearby) && nearby.getLocation().distance(entry.getKey().getLocation())<=30) {
							found = true;
						}
					}
					if(!found) {
						Log.info("NO NEARBY");
						SavageUtility.broadcastMessage(ChatColor.DARK_GRAY+entry.getValue().getName()+": I see no enemies...", entry.getKey().getLocation(), 15);
						entry.getValue().destroy();		
					}
				}
			}
			for(BossInterface entry: SavageUtility.getBosses()) {
				Log.info(entry);
				if(entry.getSpawn().distance(entry.getLocation())>=30) {
					Log.info("TOO FAR");
					SavageUtility.broadcastMessage(ChatColor.DARK_GRAY+entry.getName()+": I have wandered too far...", entry.getLocation(), 15);
					entry.remove();
				}
				else {
					boolean found = false;
					for(Player nearby: entry.getLocation().getWorld().getEntitiesByClass(Player.class)) {
						if(!CitizensAPI.getNPCRegistry().isNPC(nearby) && nearby.getLocation().distance(entry.getLocation())<=30) {
							found = true;
						}
					}
					if(!found) {
						Log.info("NO NEARBY");
						SavageUtility.broadcastMessage(ChatColor.DARK_GRAY+entry.getName()+": I see no enemies...", entry.getLocation(), 15);
						entry.remove();		
					}
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

		player.playSound(player.getLocation(), Sound.FALL_SMALL, 25.0F, 25.0F);
		player.playEffect(EntityEffect.WOLF_SMOKE);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 2));
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 60, 2));

		//apallo.savage.savageclasses.Marksman.isLeaping.add(player.getName());
		globalItemCooldown.addCooldown(player);

		SavageUtility.broadcastMessage(ChatColor.GOLD+player.getName()+ChatColor.AQUA+" Uses "+ChatColor.DARK_PURPLE+"Marksman's Leap"+ChatColor.AQUA+" to fly across the skies.", player.getLocation(), 15);		
	}
	//	private void beastsEscape(ItemStack item, Player player) {
	//		if(globalItemCooldown.isOnCooldown(player)) {
	//            SavageUtility.displayMessage(ChatColor.RED + "You cannot use " + ChatColor.GOLD + "Custom Items " + ChatColor.RED + "again for " + ChatColor.AQUA + globalItemCooldown.getRemainingTime(player) + ChatColor.RED + " seconds.", player);
	//			return;
	//		}
	//		item.setAmount(item.getAmount()-1);
	//		SavageUtility.broadcastMessage(ChatColor.GOLD+player.getName()+ChatColor.AQUA+" Uses "+ChatColor.DARK_PURPLE+"Beast's Escape"+ChatColor.AQUA+" to summon a fast Pwnda.", player.getLocation(), 15);
	//        new Beast(EntityTypes.PANDA, ((CraftWorld)(player.getWorld())).getHandle(), player);
	//	}
	private void pyrosFlame(ItemStack item, Player player) {
		if(globalItemCooldown.isOnCooldown(player)) {
			SavageUtility.displayMessage(ChatColor.RED + "You cannot use " + ChatColor.GOLD + "Custom Items " + ChatColor.RED + "again for " + ChatColor.AQUA + globalItemCooldown.getRemainingTime(player) + ChatColor.RED + " seconds.", player);
			return;
		}
		Player target = (Player) apallo.savage.savageclasses.SavageUtility.getTarget(player, 15, false);
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
				nearby.getWorld().playSound(nearby.getLocation(), Sound.ANVIL_BREAK, 10, 10);
				SavageUtility.displayMessage(ChatColor.AQUA+"You make a loud noise.", nearby);
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
		removeItems(player, item);
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
	public void removeItems(Player player, ItemStack item) {
		Log.info(item.getAmount());
		Log.info(item);
		if(item.getAmount()>1) {
			item.setAmount(item.getAmount()-1);
		}
		else {
			player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));			
		}
		Log.info(item.getAmount());
		Log.info(item);
	}
}
