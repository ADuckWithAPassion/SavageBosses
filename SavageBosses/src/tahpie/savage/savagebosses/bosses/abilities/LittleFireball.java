package tahpie.savage.savagebosses.bosses.abilities;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.SavageUtility;
import tahpie.savage.savagebosses.bosses.GenericBoss;
import tahpie.savage.savagebosses.bosses.IndividualBoss;

public class LittleFireball extends Ability{
	SavageBosses SB;
	public LittleFireball(ConfigurationSection ability, SavageBosses SB) {
		super(ability, SB);
		this.SB = SB;
	}
	@Override
	public boolean apply(EntityDamageByEntityEvent event, IndividualBoss IB) {
		super.apply(event,IB);
		for(LivingEntity target: getTargets(event,IB)) {
			new fireball(IB.getParent(),target, SB);
		}
		return true;
	}
	class fireball implements Runnable {
		Fireball fireball;
		Location spawn;
		Vector dir;
		int task;
		LivingEntity target;
		boolean homing;
		fireball(LivingEntity parent, SavageBosses SB){
			fireball = (Fireball)parent.getWorld().spawnEntity(parent.getEyeLocation(), EntityType.SMALL_FIREBALL);
			dir = parent.getEyeLocation().getDirection().normalize();
			spawn = parent.getLocation().clone();
			task = Bukkit.getScheduler().scheduleSyncRepeatingTask(SB, this, 0, 10);
		}
		fireball(LivingEntity parent, LivingEntity target, SavageBosses SB){
			fireball = (Fireball)parent.getWorld().spawnEntity(parent.getEyeLocation(), EntityType.SMALL_FIREBALL);
			dir = parent.getEyeLocation().getDirection().normalize();
			spawn = parent.getLocation().clone();
			task = Bukkit.getScheduler().scheduleSyncRepeatingTask(SB, this, 0, 10);
			this.target = target;
		}
		@Override
		public void run() {
			if(fireball.getLocation().distance(spawn) >= 100 || fireball.isDead()) {
				Bukkit.getScheduler().cancelTask(task);
				fireball.remove();
			}
			else {
				if(target != null) {
					dir = target.getLocation().subtract(fireball.getLocation()).toVector().normalize();
				}
				fireball.setVelocity(dir);									
			}
		}
		public void finalize() {
			Log.info("GC: FIREBALL");
		}
	}
}
