package de.zeanon.testutils.plugin.commands;

import de.zeanon.testutils.plugin.commands.testarea.*;
import de.zeanon.testutils.plugin.commands.testblock.*;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class TestUtils {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("undo")) {
						Undo.undo(p);
					} else if (args[0].equalsIgnoreCase("registerblock")) {
						RegisterBlock.execute(args, p);
					} else if (args[0].equalsIgnoreCase("registerreset")) {
						RegisterReset.execute(args, p);
					} else if (args[0].equalsIgnoreCase("resetarea")) {
						ResetArea.execute(args, p);
					} else if (args[0].equalsIgnoreCase("invertarea")) {
						InvertArea.execute(args, p);
					} else if (args[0].equalsIgnoreCase("deleteblock")) {
						DeleteBlock.execute(args, p);
					} else if (args[0].equalsIgnoreCase("deletefolder")) {
						DeleteFolder.execute(args, p);
					} else if (args[0].equalsIgnoreCase("renameblock")) {
						RenameBlock.execute(args, p);
					} else if (args[0].equalsIgnoreCase("renamefolder")) {
						RenameFolder.execute(args, p);
					} else if (args[0].equalsIgnoreCase("registerarea")) {
						RegisterArea.execute(args, p);
					} else if (args[0].equalsIgnoreCase("deletearea")) {
						DeleteArea.execute(args, p);
					} else if (args[0].equalsIgnoreCase("update")) {
						Update.execute(args, p);
					} else {
						p.sendMessage(ChatColor.DARK_AQUA + "Invalid sub-command '" + ChatColor.GOLD + args[0] + "'.");
					}
				} else {
					p.sendMessage(ChatColor.DARK_AQUA + "Missing argument.");
				}
			}
		}.runTaskAsynchronously(de.zeanon.testutils.TestUtils.getInstance());
	}
}