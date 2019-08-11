package tahpie.savage.savagebosses.bosses.abilities;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import tahpie.savage.savagebosses.SavageBosses;
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
			IB.getParent().setHealth(Math.max(from,Math.min(IB.getParent().getMaxHealth(),to)));
			target.setHealth(Math.min(target.getMaxHealth(), from));
		}
		return true;
	}
}
