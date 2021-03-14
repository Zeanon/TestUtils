package de.zeanon.testutils.plugin.handlers.tabcompleter;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.World;
import de.zeanon.storagemanagercore.external.browniescollections.GapList;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalRequestUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class TestUtilsTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull Command command, final @NotNull String alias, final @NotNull String @NotNull [] args) {
		if (args.length == 1) {
			if (command.getName().equalsIgnoreCase("tnt")) {
				return this.getCompletions(args[0], "allow", "deny", "-other", "-n", "-north", "-s", "-south");
			} else if (command.getName().equalsIgnoreCase("testblock")) {
				final @NotNull List<String> completions = this.getCompletions(args[0], "-here", "-n", "-north", "-s", "-south");
				completions.addAll(this.getBlocks(args[0], (Player) sender));
				return completions;
			} else if (command.getName().equalsIgnoreCase("testutils")) {
				return this.getCompletions(args[0],
										   "undo",
										   "resetarea",
										   "invertarea",
										   "replacearea",
										   "replacetnt",
										   "registerreset",
										   "registerblock",
										   "deleteblock",
										   "deletefolder",
										   "renameblock",
										   "renamefolder",
										   "registerarea",
										   "deletearea",
										   "update");
			} else if (command.getName().equalsIgnoreCase("stoplag")) {
				this.getCompletions(args[0], "-c", "-here", "-other", "-global");
			}
		} else if (args.length == 2) {
			if (command.getName().equalsIgnoreCase("tnt")) {
				if (args[0].equalsIgnoreCase("-other")
					|| args[0].equalsIgnoreCase("-north")
					|| args[0].equalsIgnoreCase("-n")
					|| args[0].equalsIgnoreCase("-south")
					|| args[0].equalsIgnoreCase("-s")) {
					return this.getCompletions(args[1], "allow", "deny");
				} else if (args[0].equalsIgnoreCase("allow") || args[0].equalsIgnoreCase("deny")) {
					return this.getCompletions(args[1], "-other", "-north", "-n", "-south", "-s");
				}
			} else if (command.getName().equalsIgnoreCase("testutils")) {
				if (args[0].equalsIgnoreCase("update")
					&& GlobalRequestUtils.checkUpdateRequest(((Player) sender).getUniqueId().toString())) {
					return this.getCompletions(args[1], "confirm", "deny");
				} else if (args[0].equalsIgnoreCase("deleteblock")
						   || args[0].equalsIgnoreCase("deletefolder")
						   || args[0].equalsIgnoreCase("renameblock")
						   || args[0].equalsIgnoreCase("renamefolder")
						   || args[0].equalsIgnoreCase("registerblock")) {
					return this.getBlocks(args[1], (Player) sender);
				} else if (args[0].equalsIgnoreCase("resetarea")
						   || args[0].equalsIgnoreCase("invertarea")
						   || args[0].equalsIgnoreCase("replacearea")
						   || args[0].equalsIgnoreCase("replacetnt")) {
					return this.getCompletions(args[1], "-here", "-other", "-north", "-n", "-south", "-s");
				} else if (args[0].equalsIgnoreCase("deletearea")) {
					return this.getRegions(args[1], new BukkitWorld(((Player) sender).getWorld()));
				}
			} else if (command.getName().equalsIgnoreCase("testblock")) {
				if (args[0].equalsIgnoreCase("-here")
					|| args[0].equalsIgnoreCase("-n")
					|| args[0].equalsIgnoreCase("-north")
					|| args[0].equalsIgnoreCase("-s")
					|| args[0].equalsIgnoreCase("-south")) {
					return this.getBlocks(args[1], (Player) sender);
				} else {
					return this.getCompletions(args[1], "-here", "-n", "-north", "-s", "-south");
				}
			}
		}
		return null;
	}

	private List<String> getCompletions(final @NotNull String arg, final @NotNull String... completions) {
		return Arrays.stream(completions)
					 .filter(completion -> completion.startsWith(arg.toLowerCase()))
					 .collect(Collectors.toList());
	}

	private List<String> getBlocks(final @NotNull String arg, final @NotNull Player p) {
		final @NotNull List<String> completions = new GapList<>();

		try {
			@NotNull Path tempDirectory = Paths.get(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/TestBlocks/" + p.getUniqueId().toString());
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
					this.addFileToCompletions(sequence, completions, file, Paths.get(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/TestBlocks/" + p.getUniqueId().toString()));
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
				final @NotNull String relativePath = BaseFileUtils.removeExtension(FilenameUtils.separatorsToUnix(basePath.toRealPath().relativize(file.toPath().toRealPath()).toString()));
				if (!relativePath.equalsIgnoreCase("default")) {
					completions.add(relativePath);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<String> getRegions(final @NotNull String arg, final @NotNull World world) {
		final @NotNull List<String> completions = new GapList<>();
		try {
			for (final @NotNull File regionFolder : BaseFileUtils.listFolders(new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/TestAreas/" + world.getName()))) {
				if (regionFolder.getName().toLowerCase().startsWith(arg.toLowerCase()) && !regionFolder.getName().equalsIgnoreCase(arg)) {
					completions.add(regionFolder.getName());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return completions;
	}
}