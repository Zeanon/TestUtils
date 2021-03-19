package de.zeanon.testutils.plugin.handlers;

import de.zeanon.testutils.plugin.commands.Backup;
import de.zeanon.testutils.plugin.commands.TNT;
import de.zeanon.testutils.plugin.commands.TestUtils;
import de.zeanon.testutils.plugin.commands.testblock.TestBlock;
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
		if (command.getName().equalsIgnoreCase("testutils")) {
			TestUtils.execute(args, (Player) sender);
		} else if (command.getName().equalsIgnoreCase("testblock")) {
			TestBlock.execute(args, (Player) sender);
		} else if (command.getName().equalsIgnoreCase("tnt")) {
			TNT.execute(args, (Player) sender);
		} else if (command.getName().equalsIgnoreCase("backup")) {
			Backup.execute(args, (Player) sender);
		}
		return true;
	}
}