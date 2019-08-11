package tahpie.savage.savagebosses.bosses.abilities;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import tahpie.savage.savagebosses.SavageUtility;
import tahpie.savage.savagebosses.bosses.IndividualBoss;

public enum Targets {

	Self {
		@Override
		List<LivingEntity> get(Ability ability, EntityDamageByEntityEvent event, IndividualBoss IB) {
			return Arrays.asList(IB.getParent());
		}
	},
	AttackingEnemy {
		@Override
		List<LivingEntity> get(Ability ability, EntityDamageByEntityEvent event, IndividualBoss IB) {
			return Arrays.asList((LivingEntity)event.getDamager());
		}
	},
	AttackedEnemy {
		@Override
		List<LivingEntity> get(Ability ability, EntityDamageByEntityEvent event, IndividualBoss IB) {
			return Arrays.asList((LivingEntity)event.getEntity());
		}
	},
	AreaEnemy {
		@Override
		List<LivingEntity> get(Ability ability, EntityDamageByEntityEvent event, IndividualBoss IB) {
			return SavageUtility.getNearbyEntities((Entity)IB.getParent(), ability.getRange(), org.bukkit.entity.Player.class, false);
		}
	};
	abstract List<LivingEntity> get(Ability ability, EntityDamageByEntityEvent event, IndividualBoss IB);
}
