package tahpie.savage.savagebosses.bosses.abilities;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.SavageUtility;
import tahpie.savage.savagebosses.bosses.GenericBoss;
import tahpie.savage.savagebosses.bosses.IndividualBoss;

public class Stun extends Ability{
	public Stun(ConfigurationSection ability, SavageBosses SB) {
		super(ability, SB);
	}
	@Override
	public boolean apply(EntityDamageByEntityEvent event, IndividualBoss IB){
		super.apply(event, IB);
		for(LivingEntity target: getTargets(event,IB)) {
			if(target instanceof Player) {		
				String message = getMessagePrivate(event, IB);
				if(!(ChatColor.stripColor(message).equalsIgnoreCase("null"))) {
					SavageUtility.displayMessage(message,target);
				}
				savageclasses.Stun.stunPlayer((Player)target, getDuration()*1000);
			}	
		}
		return false;
	}
}
