package tahpie.savage.savagebosses.bosses.abilities;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.bosses.IndividualBoss;

public class Encase extends Ability{ 
	public Encase(ConfigurationSection ability, SavageBosses SB) {
		super(ability, SB);
	}
	@Override
	public boolean apply(EntityDamageByEntityEvent event, IndividualBoss IB) {
		super.apply(event,IB);
		for(LivingEntity target: getTargets(event,IB)) {
			Fireball fireball = (Fireball)IB.getParent().getWorld().spawnEntity(IB.getParent().getLocation(), EntityType.FIREBALL);
			Location loc = fireball.getLocation();
			loc.setPitch(0);
			fireball.teleport(loc);
		}
		return true;
	}
}
