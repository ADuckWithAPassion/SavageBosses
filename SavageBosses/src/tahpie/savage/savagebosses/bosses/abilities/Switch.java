package tahpie.savage.savagebosses.bosses.abilities;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.bosses.IndividualBoss;

public class Switch extends Ability{
	public Switch(ConfigurationSection ability, SavageBosses SB) {
		super(ability, SB);
	}
	@Override
	public boolean apply(EntityDamageByEntityEvent event, IndividualBoss IB) {
		super.apply(event,IB);
		for(LivingEntity target: getTargets(event,IB)) {
			Location from = IB.getParent().getLocation().clone();
			Location to = target.getLocation().clone();
			IB.getParent().teleport(to);
			target.teleport(from);
		}
		return true;
	}
}
