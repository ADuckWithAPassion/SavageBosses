package tahpie.savage.savagebosses.bosses;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Boss implements Listener{
	Random random;
	public Boss() {
		random = new Random();
	}
	@EventHandler
	public void clearRandomDrops(EntityDeathEvent event) {
		if(event.getEntity().hasMetadata("boss")) {
			event.getDrops().clear();	
		}
	}
	@EventHandler
	public void fireworkExplosion(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if(item.hasItemMeta()) {
			if(random.nextInt(100)+1>=20){
				return;
			}
			ItemMeta meta = item.getItemMeta();
			if(meta.hasLore()) {
				List<String> lore = meta.getLore();
				if(lore.size() >= 5) {
					Log.info(lore.get(5));
					if(lore.get(5).contains("Chance to make a")) {
						String[] sentence = lore.get(5).split(" ");
						String colour = ChatColor.stripColor(sentence[sentence.length-2].toUpperCase().replace(" ", "_"));
						Log.info(colour);
						Firework firework = player.getWorld().spawn(player.getLocation().add(0.5, 1, 0.5), Firework.class);
						FireworkMeta data = (FireworkMeta) firework.getFireworkMeta();
						data.addEffects(FireworkEffect.builder().withColor(DyeColor.valueOf(colour).getFireworkColor()).with(Type.BALL_LARGE).build());
						data.setPower(1);
						firework.setFireworkMeta(data);
				}
			}
		}
	}
}
}
