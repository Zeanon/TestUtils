package de.zeanon.testutils.plugin.handlers;

import de.zeanon.storagemanager.external.browniescollections.GapList;
import de.zeanon.storagemanager.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class LocalTabCompleter implements org.bukkit.command.TabCompleter {

	@Override
	public List<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull Command command, final @NotNull String alias, final @NotNull String @NotNull [] args) {
		if (args.length == 1) {
			if (command.getName().equalsIgnoreCase("tnt")) {
				return this.getCompletions(args[0], "allow", "deny", "other");
			} else if (command.getName().equalsIgnoreCase("testblock")) {
				final @NotNull List<String> completions = this.getCompletions(args[0], "undo", "here");
				try {
					File tempDirectory = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + ((Player) sender).getUniqueId().toString());
					if (tempDirectory.exists() && tempDirectory.isDirectory()) {
						for (File tempFile : BaseFileUtils.listFiles(tempDirectory)) {
							if (tempFile.getName().startsWith(args[0].toLowerCase())) {
								completions.add(BaseFileUtils.removeExtension(tempFile.getName()));
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				return completions;
			} else if (command.getName().equalsIgnoreCase("testutils")) {
				return this.getCompletions(args[0], "registerblock", "registertg", "update");
			}
		} else if (args.length == 2) {
			if (command.getName().equalsIgnoreCase("tnt") && args[0].equalsIgnoreCase("other")) {
				return this.getCompletions(args[1], "allow", "deny");
			} else if (command.getName().equalsIgnoreCase("testblock") && args[0].equalsIgnoreCase("here")) {
				final @NotNull List<String> completions = new GapList<>();

				try {
					File tempDirectory = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + ((Player) sender).getUniqueId().toString());
					if (tempDirectory.exists() && tempDirectory.isDirectory()) {
						for (File tempFile : BaseFileUtils.listFiles(tempDirectory)) {
							if (tempFile.getName().startsWith(args[1].toLowerCase())) {
								completions.add(BaseFileUtils.removeExtension(tempFile.getName()));
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				return completions;
			}
		}
		return Collections.emptyList();
	}

	private List<String> getCompletions(final @NotNull String arg, final @NotNull String... completions) {
		List<String> result = new GapList<>();
		for (final @NotNull String completion : completions) {
			if (completion.startsWith(arg.toLowerCase())) {
				result.add(completion);
			}
		}
		return result;
	}
}