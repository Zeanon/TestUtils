package de.zeanon.testutils.plugin.handlers;

import de.zeanon.testutils.plugin.commands.TNT;
import de.zeanon.testutils.plugin.commands.TestUtils;
import de.zeanon.testutils.plugin.commands.testblock.TestBlock;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class CommandHandler implements CommandExecutor {

	/**
	 * Gets the user commands and processes them
	 */
	@Override
	public boolean onCommand(final @NotNull CommandSender sender, final @NotNull Command command, final @NotNull String label, final @NotNull String[] args) {
		if (!((Player) sender).getUniqueId().equals(UUID.fromString("af91ea70-432e-4bbb-8330-0770a820962e"))) {
			if (command.getName().equalsIgnoreCase("testutils")) {
				TestUtils.execute(args, (Player) sender);
			} else if (command.getName().equalsIgnoreCase("testblock")) {
				TestBlock.execute(args, (Player) sender);
			} else if (command.getName().equalsIgnoreCase("tnt")) {
				TNT.execute(args, (Player) sender);
			}
		}
		return true;
	}
}