package tahpie.savage.savagebosses.bosses.abilities;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
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

public class SwitchHealth extends Ability{
	public SwitchHealth(ConfigurationSection ability, SavageBosses SB) {
		super(ability, SB);
	}
	@Override
	public boolean apply(EntityDamageByEntityEvent event, IndividualBoss IB) {
		super.apply(event,IB);
		for(LivingEntity target: getTargets(event,IB)) {
			double from = IB.getParent().getHealth();
			double to = target.getHealth();
			IB.getParent().setHealth(Math.min(IB.getParent().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),to));
			target.setHealth(Math.min(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue(), from));
		}
		return true;
	}
}
