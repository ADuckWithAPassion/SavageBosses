package tahpie.savage.savagebosses;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import tahpie.savage.savagebosses.bosses.Boss;
import tahpie.savage.savagebosses.events.CommandHandler;
import tahpie.savage.savagequests.CommandQuest;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SavageBosses extends JavaPlugin implements Listener{
	
	private File itemConfigFile;
	private YamlConfiguration itemConfig;


	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		Objects.requireNonNull(this.getCommand("boss")).setExecutor(new CommandHandler(this));
        getServer().getPluginManager().registerEvents(new Boss(), this);
		getLogger().info("enabled!");
		createItemConfig();
	}

    public void saveClassStatsConfig() {
        try {
            getClassStatsConfig().save(itemConfigFile);
        } catch (IOException ex){

        }
    }

    public FileConfiguration getClassStatsConfig() {
        return itemConfig;
    }

    private void createItemConfig() {
        itemConfigFile = new File(getDataFolder(), "item.yml");
        if (!itemConfigFile.exists()) {
            boolean fileMade = itemConfigFile.getParentFile().mkdirs();
            saveResource("item.yml", false);
        }

        itemConfig = new YamlConfiguration();
        try {
            itemConfig.load(itemConfigFile);

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
	
}
