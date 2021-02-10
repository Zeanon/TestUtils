package de.zeanon.testutils.plugin.handlers;

import de.zeanon.storagemanagercore.external.browniescollections.GapList;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class LocalTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull Command command, final @NotNull String alias, final @NotNull String @NotNull [] args) {
		if (args.length == 0) {
			if (command.getName().equalsIgnoreCase("tnt")) {
				return Arrays.asList("allow", "deny", "other", "info");
			} else if (command.getName().equalsIgnoreCase("testblock")) {
				final @NotNull List<String> completions = Arrays.asList("undo", "here");
				try {
					File tempDirectory = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + ((Player) sender).getUniqueId().toString());
					if (tempDirectory.exists() && tempDirectory.isDirectory()) {
						for (File tempFile : BaseFileUtils.listFiles(tempDirectory)) {
							completions.add(BaseFileUtils.removeExtension(tempFile.getName()));
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				return completions;
			} else if (command.getName().equalsIgnoreCase("testutils")) {
				return Arrays.asList("registerblock", "deleteblock", "registertg", "update");
			}
		} else if (args.length == 1) {
			if (command.getName().equalsIgnoreCase("tnt")) {
				return this.getCompletions(args[0], "allow", "deny", "other", "info");
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
				return this.getCompletions(args[0], "registerblock", "deleteblock", "registertg", "update");
			}
		} else if (args.length == 2) {
			if (command.getName().equalsIgnoreCase("tnt")) {
				if (args[0].equalsIgnoreCase("other")) {
					return this.getCompletions(args[1], "allow", "deny", "info");
				} else if (args[0].equalsIgnoreCase("allow") || args[0].equalsIgnoreCase("deny") || args[0].equalsIgnoreCase("info")) {
					return this.getCompletions(args[1], "other");
				}
			} else if (command.getName().equalsIgnoreCase("testutils") && args[0].equalsIgnoreCase("deleteblock")) {
				return this.getBlocks(args[1], (Player) sender);
			} else if (command.getName().equalsIgnoreCase("testblock") && args[0].equalsIgnoreCase("here")) {
				return this.getBlocks(args[1], (Player) sender);
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

	private List<String> getBlocks(final @NotNull String arg, final @NotNull Player p) {
		final @NotNull List<String> completions = new GapList<>();

		try {
			File tempDirectory = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString());
			if (tempDirectory.exists() && tempDirectory.isDirectory()) {
				for (File tempFile : BaseFileUtils.listFilesOfType(tempDirectory, "schem")) {
					if (tempFile.getName().startsWith(arg.toLowerCase())) {
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