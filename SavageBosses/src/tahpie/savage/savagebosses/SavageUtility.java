package tahpie.savage.savagebosses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.gmail.nossr50.api.PartyAPI;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import net.md_5.bungee.api.ChatColor;
import tahpie.savage.savagebosses.bosses.Boss;

public class SavageUtility {
	private static List<UUID> bossUUIDs = new ArrayList<UUID>();
	private static List<UUID> customMobUUIDs = new ArrayList<UUID>();

	public SavageUtility() {}
	
	public static void displayMessage(String message, Entity target) {
		target.sendMessage(ChatColor.DARK_GRAY+"["+ChatColor.BLUE+"SR"+ChatColor.DARK_GRAY+"] "+message);
	}
	public static void broadcastMessage(String message, Location loc, int range) {
		for(Entity nearby: loc.getWorld().getNearbyEntities(loc, range, range, range)) {
			nearby.sendMessage(ChatColor.DARK_GRAY+"["+ChatColor.BLUE+"SR"+ChatColor.DARK_GRAY+"] "+message);
		}
	}
	public static List<LivingEntity> getNearbyEntities(Entity target, int range, Class<? extends Entity> entityType, boolean includeSelf) {
		List<LivingEntity> entities = new ArrayList<LivingEntity>();
		for(Entity nearby: target.getWorld().getEntitiesByClass(entityType)){
			if(nearby == target && !includeSelf) {
				continue;
			}
			if(target.getLocation().distance(nearby.getLocation()) <= range) {
				entities.add((LivingEntity)nearby);
			}
		}
		return entities;
	}
	public static List<LivingEntity> getNearbyEntities(Location target, int range, Class<? extends Entity> entityType) {
		List<LivingEntity> entities = new ArrayList<LivingEntity>();
		for(Entity nearby: target.getWorld().getEntitiesByClass(entityType)){
			if(target.distance(nearby.getLocation()) <= range) {
				entities.add((LivingEntity)nearby);
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
	public static void removeInventoryItems(PlayerInventory inv, Material type, int amount) { // from bukkit forums
	    for (ItemStack is : inv.getContents()) {
	        if (is != null && is.getType() == type) {
	            int newamount = is.getAmount() - amount;
	            if (newamount > 0) {
	                is.setAmount(newamount);
	                return;
	            } else {
	            	is.setAmount(0);
	                amount = -newamount;
	                if (amount == 0) {
	                	return;
	                }
	            }
	        }
	    }
	}
    public static Entity getInfront(Player player, int range, Class<? extends Entity> type, String conditions) {
    	Entity target = null;
    	double targetDistanceSquared = 0.0D;
        Vector l = player.getEyeLocation().toVector();Vector n = player.getLocation().getDirection().normalize();
        double cos45 = Math.cos(0.7853981633974483D); // represents cos of angle Beta in the visual demonstration

    	for (Entity other : player.getWorld().getEntitiesByClass(type)){
            if(player==other) {
            	continue;
            }
            if (other instanceof Player && (conditions.equalsIgnoreCase("inParty") && !(PartyAPI.inSameParty(player, (Player)other))) || (conditions.equalsIgnoreCase("notInParty") && (PartyAPI.inSameParty(player, (Player)other)))){
        		continue;
            }
            if ((target == null) || (targetDistanceSquared > other.getLocation().distanceSquared(player.getLocation()))) {
            	Vector t = other.getLocation().add(0.0D, 1.0D, 0.0D).toVector().subtract(l);
            	if ((n.clone().crossProduct(t).lengthSquared() < 1.0D) && (t.normalize().dot(n) >= cos45)) {
            		target = other;
            		targetDistanceSquared = target.getLocation().distanceSquared(player.getLocation());
                }
            }
        }
    	if(targetDistanceSquared<=range*range) {
    		return target;
    	}
    	else {
    		return null;
    	}
    } 
    static boolean isPVPAllowed(LivingEntity player){
        RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        return set.testState(null, Flags.PVP);
    }

    public static List<Player> getPartyMembers(Player player) {
    	if(PartyAPI.inParty(player)) {
    		return(PartyAPI.getOnlineMembers(player));
    	}
    	return(Arrays.asList(player));
    }

	public static void despawnBosses() {
		for(UUID bossID: getBossUUIDs()) {
			Log.info("Removing: "+bossID+", "+Bukkit.getEntity(bossID).getName());
			Bukkit.getEntity(bossID).remove();
			Log.info("REMOVED");
		}
		for(UUID customMobID: getCustomMobIDs()) {
			Log.info("Removing: "+customMobID+", "+Bukkit.getEntity(customMobID).getName());
			Boss.bosses.get(Bukkit.getEntity(customMobID)).destroy();
			Log.info("REMOVED");
		}
	}
	private static List<UUID> getCustomMobIDs() {
		return new ArrayList<UUID>(customMobUUIDs );
	}

	public static List<UUID> getBossUUIDs() {
		return new ArrayList<UUID>(bossUUIDs);
	}

	public static void addBossUUID(UUID uniqueID) {
		bossUUIDs.add(uniqueID);
	}

	public static void removeBossUUID(UUID uniqueID) {
		bossUUIDs.remove(uniqueID);
	}

	public static void addCustomMobID(UUID uniqueId) {
		customMobUUIDs.add(uniqueId);
	}
	public static void removeCustomMobID(UUID uniqueId) {
		customMobUUIDs.remove(uniqueId);
	}
}
