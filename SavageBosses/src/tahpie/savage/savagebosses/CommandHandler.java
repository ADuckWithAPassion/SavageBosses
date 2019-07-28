package tahpie.savage.savagebosses;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import net.minecraft.server.v1_14_R1.WorldServer;
import tahpie.savage.savagebosses.bosses.GenericBoss;
import tahpie.savage.savagebosses.bosses.HoardingPanda;
import tahpie.savage.savagebosses.bosses.Twiggy;

public class CommandHandler implements Listener, CommandExecutor{
	SavageBosses SB;
	public CommandHandler(SavageBosses SB) {
		this.SB = SB;
	}

	public boolean onCommand(CommandSender sender, Command command, String arg2, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
			if(command.getName().equalsIgnoreCase("boss")) {
				if(args.length == 0) {
					SavageUtility.displayMessage(ChatColor.AQUA+"Welcome to Savage Bosses! Use "+ChatColor.GOLD+"/boss help"+ChatColor.AQUA+" to get started.", player);
					return true;
				}
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("help")) {
						SavageUtility.displayMessage(ChatColor.GOLD+"/boss despawn "+ChatColor.AQUA+"- despawn all loaded instances of a certain boss.", player);
						SavageUtility.displayMessage(ChatColor.GOLD+"/boss summon <"+ChatColor.GREEN+"BOSS NAME"+ChatColor.GOLD+"> "+ChatColor.AQUA+"- summons a boss.", player);
						return true;
					}
					else if (args[0].equalsIgnoreCase("despawn")) {
						SavageUtility.displayMessage(ChatColor.AQUA+"All loaded boss and minion instances have been removed", player);
						SavageUtility.despawnBosses();
						//						for(LivingEntity entity : player.getWorld().getLivingEntities()) {
//							if(entity.hasMetadata("boss") || entity.hasMetadata("minion")) {
//								entity.remove();
//							}
//						}
						return true;
					}
				}
				else if(args.length == 2) {
					if (args[0].equalsIgnoreCase("summon")) {
						WorldServer world = ((CraftWorld)(player.getWorld())).getHandle();
						if(args[1].equalsIgnoreCase("twiggy")) {
							new Twiggy(player, world, SB);
							return true;
						}
						else if(args[1].equalsIgnoreCase("hoardingpanda")) {						
							new HoardingPanda(player, world, SB);
							return true;
						}
						else if(args[1].equalsIgnoreCase("lostberserker")) {
							SB.bossMap.get("LostBerserker").spawn(player.getLocation());
							return true;
						}
						else if(args[1].equalsIgnoreCase("corruptedpaladin")) {
							SB.bossMap.get("CorruptedPaladin").spawn(player.getLocation());
							return true;
						}
						else {
							SavageUtility.displayMessage(ChatColor.AQUA+"Invalid boss name.", player);
							return true;
						}
					}
				}
			}
			SavageUtility.displayMessage(ChatColor.AQUA+"Please use correct syntax. "+ChatColor.GOLD+"/boss help.", player);
		}
		return true;
	}
}
