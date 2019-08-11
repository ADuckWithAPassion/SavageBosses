package tahpie.savage.savagebosses.bosses.abilities;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.SavageUtility;
import tahpie.savage.savagebosses.bosses.IndividualBoss;

public abstract class Ability {
	private String name;
	private String trigger;
	private String target;
	private int range;
	private int duration;
	private double chance;
	private int cooldown;
	private List<String> debuffs;
	private List<String> buffs;
	private String messageBroadcast;
	private String messagePrivate;
	private List<String> conditions;
	private double potency;
	private SavageBosses SB;
	private HashMap<IndividualBoss, addToQueue> queueMap = new HashMap<IndividualBoss, addToQueue>();
	public Ability(ConfigurationSection ability, SavageBosses SB) {
		this.SB = SB;
		name = ability.getString("ability");
		trigger = ability.getString("trigger");
		target = ability.getString("target");
		range = ability.getInt("range");
		duration = ability.getInt("duration");
		chance = ability.getDouble("chance");
		cooldown = ability.getInt("cooldown");
		debuffs = ability.getStringList("debuffs");
		buffs = ability.getStringList("buffs");
		potency = ability.getDouble("potency");
		messageBroadcast = ability.getString("messageBroadcast");
		messagePrivate = ability.getString("messagePrivate");
		conditions = ability.getStringList("conditions");
		setDefaults();
	}
	public void setDefaults() {
	}
	public String getName() {
		return name;
	}
	public String getTrigger() {
		return trigger;
	}
	public String getTarget() {
		return target;
	}
	public int getRange() {
		return range;
	}
	public int getDuration() {
		return duration;
	}
	public double getChance() {
		return chance;
	}
	public int getCooldown() {
		return cooldown;
	}
	public List<String> getDebuffs() {
		return debuffs;
	}
	public List<String> getBuffs() {
		return buffs;
	}
	public double getPotency() {
		return potency;
	}
	public String getMessageBroadcast(EntityDamageByEntityEvent event, IndividualBoss IB) {
		String newMessage = messageBroadcast;
		newMessage = ChatColor.GREEN+newMessage;
		newMessage = newMessage.replaceAll("NAME", ChatColor.DARK_RED+IB.getName()+ChatColor.GREEN);
		String names = "";
		for(LivingEntity le: getTargets(event,IB)) {
			names = names+le.getName()+", ";
		}
		if(names.length() > 0) {
			names = names.substring(0, names.length()-2);	
		}
		else {
			name = "Nobody";
		}
		newMessage = newMessage.replaceAll("TARGET", ChatColor.GOLD+names+ChatColor.GREEN);	
		return newMessage;
	}
	public String getMessagePrivate(EntityDamageByEntityEvent event, IndividualBoss IB) {
		String newMessage = messagePrivate;
		newMessage = ChatColor.GREEN+newMessage;
		newMessage = newMessage.replaceAll("NAME", ChatColor.DARK_RED+IB.getName()+ChatColor.GREEN);
		if(event != null) {
			newMessage = newMessage.replaceAll("TARGET", ChatColor.GOLD+getTargets(event,IB).toString()+ChatColor.GREEN);	
		}
		return newMessage;
	}
	public List<String> getConditions() {
		return conditions;
	}
	public Ability getAbility() {
		return this;
	}
	public class addToQueue implements Runnable{
		int task;
		IndividualBoss IB;
		public addToQueue(int cooldown, IndividualBoss IB) {
			this.IB = IB;
			task = Bukkit.getScheduler().scheduleSyncDelayedTask(SB, this, cooldown*20);	
		}
		public void cancel() {
			Bukkit.getScheduler().cancelTask(task);
		}
		@Override
		public void run() {
			if(IB.getParent().isDead()) {
				cancel();
				return;
			}
			IB.addAbilityToQueue(getAbility());		
		}
	}
	public List<LivingEntity> getTargets(EntityDamageByEntityEvent event, IndividualBoss IB){
		try {
			return Targets.valueOf(this.getTarget()).get(this, event, IB);
		}
		catch(Error error) {
			error.printStackTrace(); 
		}
		return null;
	}
	public void addToQueue(int cooldown, IndividualBoss IB) {
		queueMap.put(IB, new addToQueue(cooldown,IB));		
	}
	public boolean apply(EntityDamageByEntityEvent event, IndividualBoss IB) {
		String message = getMessageBroadcast(event, IB);
		if(!(ChatColor.stripColor(message).equalsIgnoreCase("null"))) {
			SavageUtility.broadcastMessage(message, IB.getParent().getLocation(), getRange());			
		}
		addToQueue(getCooldown(), IB);
		return true;
	}
	public addToQueue getQueueMap(IndividualBoss IB) {
		return queueMap.get(IB);
	}
	public void remove(IndividualBoss IB) {
		if(queueMap.containsKey(IB)) {
			queueMap.get(IB).cancel();
			queueMap.remove(IB);			
		}
	}
}
