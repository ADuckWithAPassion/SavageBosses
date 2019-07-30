package tahpie.savage.savagebosses.bosses.abilities;

import java.util.List;

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

import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.SavageUtility;
import tahpie.savage.savagebosses.bosses.GenericBoss;
import tahpie.savage.savagebosses.bosses.IndividualBoss;

public class Whip extends Ability{
	public Whip(ConfigurationSection ability, SavageBosses SB) {
		super(ability, SB);
	}
	@Override
	public boolean apply(EntityDamageByEntityEvent event, IndividualBoss IB) {
		super.apply(event,IB);
		for(LivingEntity target: getTargets(event,IB)) {
			target.setVelocity(IB.getParent().getLocation().subtract(target.getLocation()).toVector().setY(0.1).normalize().setY(0.1).multiply(getPotency()));
			target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*getDuration(), 1));
		}
		return true;
	}
}
