package de.zeanon.testutils.plugin.handlers;

import de.zeanon.storagemanagercore.external.browniescollections.GapList;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalRequestUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class LocalTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull Command command, final @NotNull String alias, final @NotNull String @NotNull [] args) {
		if (args.length == 1) {
			if (command.getName().equalsIgnoreCase("tnt")) {
				return this.getCompletions(args[0], "allow", "deny", "other", "info");
			} else if (command.getName().equalsIgnoreCase("testblock")) {
				final @NotNull List<String> completions = this.getCompletions(args[0], "undo", "here");
				completions.addAll(this.getBlocks(args[0], (Player) sender));
				return completions;
			} else if (command.getName().equalsIgnoreCase("testutils")) {
				return this.getCompletions(args[0], "registerblock", "deleteblock", "deletefolder", "renameblock", "renamefolder", "registertg", "update");
			}
		} else if (args.length == 2) {
			if (command.getName().equalsIgnoreCase("tnt")) {
				if (args[0].equalsIgnoreCase("other")) {
					return this.getCompletions(args[1], "allow", "deny", "info");
				} else if (args[0].equalsIgnoreCase("allow") || args[0].equalsIgnoreCase("deny") || args[0].equalsIgnoreCase("info")) {
					return this.getCompletions(args[1], "other");
				}
			} else if (command.getName().equalsIgnoreCase("testutils")) {
				if (args[0].equalsIgnoreCase("update") && GlobalRequestUtils.checkUpdateRequest(((Player) sender).getUniqueId().toString())) {
					return this.getCompletions(args[1], "confirm", "deny");
				} else if (args[0].equalsIgnoreCase("deleteblock") || args[0].equalsIgnoreCase("deletefolder") || args[0].equalsIgnoreCase("renameblock") || args[0].equalsIgnoreCase("renamefolder") || args[0].equalsIgnoreCase("registerblock")) {
					return this.getBlocks(args[1], (Player) sender);
				}
			} else if (command.getName().equalsIgnoreCase("testblock")) {
				if (args[0].equalsIgnoreCase("here")) {
					return this.getBlocks(args[1], (Player) sender);
				} else {
					return this.getCompletions(args[1], "here");
				}
			}
		}
		return Collections.emptyList();
	}

	private List<String> getCompletions(final @NotNull String arg, final @NotNull String... completions) {
		return Arrays.stream(completions)
					 .filter(completion -> completion.startsWith(arg.toLowerCase()))
					 .collect(Collectors.toList());
	}

	private List<String> getBlocks(final @NotNull String arg, final @NotNull Player p) {
		final @NotNull List<String> completions = new GapList<>();

		try {
			@NotNull Path tempDirectory = Paths.get(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString());
			final @NotNull String[] pathArgs = arg.split("/");
			if (!arg.endsWith("/")) {
				for (int i = 0; i < pathArgs.length - 1; i++) {
					tempDirectory = tempDirectory.resolve(pathArgs[i]);
				}
			} else {
				tempDirectory = tempDirectory.resolve(arg);
			}

			final @NotNull File pathFile = tempDirectory.toFile();
			if (pathFile.exists() && pathFile.isDirectory()) {
				final @NotNull String sequence = arg.endsWith("/") ? "" : pathArgs[pathArgs.length - 1];
				for (final @NotNull File file : BaseFileUtils.listFilesOfTypeAndFolders(pathFile, false, "schem")) {
					this.addFileToCompletions(sequence, completions, file, Paths.get(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString()));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return completions;
	}

	private void addFileToCompletions(final @NotNull String sequence, final @NotNull List<String> completions, final @NotNull File file, final @NotNull Path basePath) {
		try {
			if (file.getName().toLowerCase().startsWith(sequence.toLowerCase()) && !file.getName().equalsIgnoreCase(sequence)) {
				final @NotNull String path = BaseFileUtils.removeExtension(FilenameUtils.separatorsToUnix(basePath.toRealPath().relativize(file.toPath().toRealPath()).toString()));
				completions.add(path);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}