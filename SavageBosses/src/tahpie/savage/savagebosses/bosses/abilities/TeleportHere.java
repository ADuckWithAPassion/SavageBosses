package tahpie.savage.savagebosses.bosses.abilities;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.bosses.IndividualBoss;

public class TeleportHere extends Ability{
	public TeleportHere(ConfigurationSection ability, SavageBosses SB) {
		super(ability, SB);
	}
	@Override
	public boolean apply(EntityDamageByEntityEvent event, IndividualBoss IB) {
		super.apply(event,IB);
		for(LivingEntity target: getTargets(event,IB)) {
			target.teleport(IB.getParent().getLocation());
		}
		return true;
	}
}
