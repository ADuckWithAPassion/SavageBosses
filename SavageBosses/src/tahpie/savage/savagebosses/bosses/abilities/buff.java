package tahpie.savage.savagebosses.bosses.abilities;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.SavageUtility;
import tahpie.savage.savagebosses.bosses.GenericBoss;
import tahpie.savage.savagebosses.bosses.IndividualBoss;

public class Buff extends Ability{
	public Buff(ConfigurationSection ability, SavageBosses SB) {
		super(ability, SB);
	}
	@Override
	public boolean apply(EntityDamageByEntityEvent event, IndividualBoss IB) {
		super.apply(event,IB);
		for(LivingEntity target: getTargets(event,IB)) {
			for(String buff: getBuffs()) {
				String message = getMessagePrivate(event,IB);
				if(!(ChatColor.stripColor(message).equalsIgnoreCase("null"))) {
					SavageUtility.displayMessage(message,target);
				}

				PotionEffect effect = new PotionEffect(PotionEffectType.getByName(buff.split(",")[0]), getDuration()*20, Integer.parseInt(buff.split(",")[1].replaceAll(" ", ""))-1);
				target.addPotionEffect(effect);
			}	
		}
		return true;
	}
}
