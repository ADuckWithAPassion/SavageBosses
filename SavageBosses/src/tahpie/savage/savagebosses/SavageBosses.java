package tahpie.savage.savagebosses;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_8_R3.EntitySkeleton;
import tahpie.savage.savagebosses.bosses.Boss;
import tahpie.savage.savagebosses.bosses.GenericBoss;
import tahpie.savage.savagebosses.bosses.NMSUtils;
import tahpie.savage.savagebosses.bosses.Twiggy;
import tahpie.savage.savagebosses.questitems.SpecialItem;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class SavageBosses extends JavaPlugin implements Listener{
	
	private File itemConfigFile;
	private File bossConfigFile;
	
	private YamlConfiguration itemConfig;
	private YamlConfiguration bossConfig;
	
	HashMap<String, SpecialItem> itemMap = new HashMap<String, SpecialItem>();
	HashMap<String, GenericBoss> customBossMap = new HashMap<String, GenericBoss>();
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		Objects.requireNonNull(this.getCommand("boss")).setExecutor(new CommandHandler(this));
        getServer().getPluginManager().registerEvents(new Boss(this), this);
		getLogger().info("enabled!");
		NMSUtils.registerEntity("Skeleton", 51, EntitySkeleton.class, Twiggy.class);
		createItemConfig();
		createBossConfig();
		loadCustomItems();
		loadCustomBosses();
	}

	@Override
	public void onDisable() {
		SavageUtility.despawnBosses();
	}
	
    private void createItemConfig() {
        itemConfigFile = new File(getDataFolder(), "items.yml");
        if (!itemConfigFile.exists()) {
            boolean fileMade = itemConfigFile.getParentFile().mkdirs();
            if(fileMade) {
            	Log.info("MAKING SAVAGE BOSSES ITEMS.YML");
            }
            saveResource("items.yml", false);
        }

        itemConfig = new YamlConfiguration();
        try {
            itemConfig.load(itemConfigFile);

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    private void createBossConfig() {
        bossConfigFile = new File(getDataFolder(), "bosses.yml");
        if (!bossConfigFile.exists()) {
            boolean fileMade = bossConfigFile.getParentFile().mkdirs();
            if(fileMade) {
            	Log.info("MAKING SAVAGE BOSSES BOSSES.YML");
            }
            saveResource("bosses.yml", false);
        }

        bossConfig = new YamlConfiguration();
        try {
            bossConfig.load(bossConfigFile);

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public void loadCustomItems() {
    	for(String itemKey: itemConfig.getKeys(false)) {
    		Log.info(itemKey);
			String name = itemConfig.getString(itemKey+".name");
    		Log.info(name);
			String mat = itemConfig.getString(itemKey+".mat");
    		HashMap<String, Integer> enchantmentList = new HashMap<String, Integer>();
    		for (String key : itemConfig.getConfigurationSection(itemKey+".enchantmentList").getKeys(false)) {
    			Log.info(key);
    			enchantmentList.put(key, itemConfig.getInt(itemKey+".enchantmentList."+key));
    			}
    		int rarityInt = itemConfig.getInt(itemKey+".rarityInt");
    		String explosionString = itemConfig.getString(itemKey+".explosionString");
    		String tag = itemConfig.getString(itemKey+".tag");
    		String boss = itemConfig.getString(itemKey+".boss");
    		String killer = itemConfig.getString(itemKey+".killer");
    		int chance = itemConfig.getInt(itemKey+".chance");
    		String effect = itemConfig.getString(itemKey+".effect");
    		SpecialItem item = new SpecialItem(name, mat, enchantmentList, rarityInt, explosionString, tag, effect);
    		item.setChance(chance);
    		item.setBoss(boss);
    		itemMap.put(name, item);
    	}
    }
    public void loadCustomBosses() {
    	for(String name: bossConfig.getKeys(false)) {
    		customBossMap.put(name, new GenericBoss(bossConfig.getConfigurationSection(name), this));
    	}
    }
    public HashMap<String, SpecialItem> getItems(){
    	return itemMap;
    }
    public HashMap<String, GenericBoss> getCustomBosses(){
    	return customBossMap;
    }
}
