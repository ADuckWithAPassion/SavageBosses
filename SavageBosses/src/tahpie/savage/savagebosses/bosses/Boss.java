package tahpie.savage.savagebosses.bosses;

import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class Boss implements Listener{
	public Boss() {
		
	}
	@EventHandler
	public void clearRandomDrops(EntityDeathEvent event) {
		if(event.getEntity().hasMetadata("boss")) {
			event.getDrops().clear();	
		}
		Log.info(event.getEntityType());
	}
}
