package tahpie.savage.savagebosses.bosses.abilities;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.bosses.IndividualBoss;

public class SummonAllies extends Ability{
	public SummonAllies(ConfigurationSection ability, SavageBosses SB) {
		super(ability, SB);
	}
	@Override
	public boolean apply(EntityDamageByEntityEvent event, IndividualBoss IB) {
		super.apply(event,IB);
		for(LivingEntity target: getTargets(event,IB)) {
			target.getWorld().spawnEntity(target.getLocation(), EntityType.ZOMBIE);
			target.getWorld().spawnEntity(target.getLocation(), EntityType.ZOMBIE);
			target.getWorld().spawnEntity(target.getLocation(), EntityType.ZOMBIE);
		}
		return true;
	}
}
