package tahpie.savage.savagebosses.bosses.abilities;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.SavageUtility;
import tahpie.savage.savagebosses.bosses.GenericBoss;
import tahpie.savage.savagebosses.bosses.IndividualBoss;

public class Push extends Ability{
	public Push(ConfigurationSection ability, SavageBosses SB) {
		super(ability, SB);
	}
	@Override
	public boolean apply(EntityDamageByEntityEvent event, IndividualBoss IB) {
		super.apply(event, IB);
		for(LivingEntity target: getTargets(event,IB)) {
			String message = getMessagePrivate(event, IB);
			if(!(ChatColor.stripColor(message).equalsIgnoreCase("null"))) {
				SavageUtility.displayMessage(message,target);
			}

			target.setVelocity(target.getLocation().subtract(IB.getParent().getLocation()).toVector().setY(0.3).normalize().multiply(getPotency()).setY(0.3));	
		}
		return true;
	}
}
