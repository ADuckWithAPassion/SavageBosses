package tahpie.savage.savagebosses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;

public class SavageUtility {
	public SavageUtility() {}
	
	public static void displayMessage(String message, Entity target) {
		target.sendMessage(ChatColor.DARK_GRAY+"["+ChatColor.DARK_BLUE+"SR"+ChatColor.DARK_GRAY+"] "+message);
	}
	public static void broadcastMessage(String message, Location loc, int range) {
		for(Entity nearby: loc.getWorld().getNearbyEntities(loc, range, range, range)) {
			nearby.sendMessage(ChatColor.DARK_GRAY+"["+ChatColor.DARK_BLUE+"SR"+ChatColor.DARK_GRAY+"] "+message);
		}
	}
	public static List<Entity> getNearbyEntities(Entity target, int range, Class<? extends Entity> entityType, boolean includeSelf) {
		List<Entity> entities = new ArrayList<Entity>();
		for(Entity nearby: target.getWorld().getEntitiesByClass(entityType)){
			if(nearby == target && !includeSelf) {
				continue;
			}
			if(target.getLocation().distance(nearby.getLocation()) <= range) {
				entities.add(nearby);
			}
		}
		return entities;
	}
	public static List<Entity> getNearbyEntities(Location target, int range, Class<? extends Entity> entityType) {
		List<Entity> entities = new ArrayList<Entity>();
		for(Entity nearby: target.getWorld().getEntitiesByClass(entityType)){
			if(target.distance(nearby.getLocation()) <= range) {
				entities.add(nearby);
			}
		}
		return entities;
	}
	public static boolean removeNegativeBuffs(LivingEntity target) {
	    ArrayList<PotionEffectType> negativeEffects = new ArrayList<PotionEffectType>(Arrays.asList(PotionEffectType.BLINDNESS,PotionEffectType.CONFUSION,PotionEffectType.HUNGER,PotionEffectType.POISON,PotionEffectType.SLOW,PotionEffectType.SLOW_DIGGING,PotionEffectType.WEAKNESS,PotionEffectType.WITHER));
	    boolean removed = false;
	    for(PotionEffectType effect: negativeEffects) {
	    	target.removePotionEffect(effect);
	    	removed = true;
	    }
	    return removed;
	}
	public static boolean removePositiveBuffs(LivingEntity target) {
	    ArrayList<PotionEffectType> negativeEffects = new ArrayList<PotionEffectType>(Arrays.asList(PotionEffectType.REGENERATION,PotionEffectType.INCREASE_DAMAGE,PotionEffectType.SPEED,PotionEffectType.DAMAGE_RESISTANCE,PotionEffectType.FIRE_RESISTANCE,PotionEffectType.FAST_DIGGING));
	    boolean removed = false;
	    for(PotionEffectType effect: negativeEffects) {
	    	target.removePotionEffect(effect);
	    	removed = true;
	    }
	    return removed;
	}
}
