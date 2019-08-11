package tahpie.savage.savagebosses.bosses.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.bosses.IndividualBoss;

public class BigFireball extends Ability{
	SavageBosses SB;
	public BigFireball(ConfigurationSection ability, SavageBosses SB) {
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
			fireball = (Fireball)parent.getWorld().spawnEntity(parent.getEyeLocation(), EntityType.FIREBALL);
			dir = parent.getEyeLocation().getDirection().normalize();
			spawn = parent.getLocation().clone();
			task = Bukkit.getScheduler().scheduleSyncRepeatingTask(SB, this, 0, 5);
		}
		fireball(LivingEntity parent, LivingEntity target, SavageBosses SB){
			fireball = (Fireball)parent.getWorld().spawnEntity(parent.getEyeLocation(), EntityType.FIREBALL);
			dir = parent.getEyeLocation().getDirection().normalize();
			spawn = parent.getLocation().clone();
			task = Bukkit.getScheduler().scheduleSyncRepeatingTask(SB, this, 0, 5);
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
				fireball.setVelocity(dir.multiply(0.7));									
			}
		}
		public void finalize() {
			Log.info("GC: FIREBALL");
		}
	}
}
