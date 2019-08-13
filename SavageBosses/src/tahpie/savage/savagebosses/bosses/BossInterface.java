package tahpie.savage.savagebosses.bosses;

import org.bukkit.Location;

public interface BossInterface {
	public Location getSpawn();
	public String getName();
	public void remove();
	public Location getLocation();
}
