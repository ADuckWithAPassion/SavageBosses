package tahpie.savage.savagebosses;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import savageclasses.Gladiator;
import tahpie.savage.savagebosses.bosses.Boss;
import tahpie.savage.savagebosses.events.CommandHandler;
import tahpie.savage.savagequests.CommandQuest;

import java.io.File;
import java.util.Objects;

public class SavageBosses extends JavaPlugin implements Listener{
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		Objects.requireNonNull(this.getCommand("boss")).setExecutor(new CommandHandler(this));
        getServer().getPluginManager().registerEvents(new Boss(), this);
		getLogger().info("enabled!");
		
	}

}
