package tahpie.savage.savagebosses.events;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import net.minecraft.server.v1_14_R1.WorldServer;
import tahpie.savage.savagebosses.SavageBosses;
import tahpie.savage.savagebosses.SavageUtility;
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
				if(args.length != 2) {
					SavageUtility.displayMessage("Please use correct syntax. /boss summon <Boss Name>.", player);
					return true;
				}
				if (args[0].equalsIgnoreCase("summon") && args.length == 2) {
					//check boss exists
					WorldServer world = ((CraftWorld)(player.getWorld())).getHandle();
					new Twiggy(player, world, SB);
				}
			}
		}
		return true;
	}
}
