package de.zeanon.testutils.plugin.handlers.tabcompleter;

import de.zeanon.storagemanagercore.external.browniescollections.GapList;
import de.zeanon.testutils.plugin.commands.backup.Backup;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class BackupTabCompleter implements TabCompleter {

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if (command.getName().equals("backup")) {

		}
		return null;
	}

	private @NotNull List<String> getListCompletions(final @NotNull String[] args, final @NotNull Backup.ModifierBlock modifierBlock, final @NotNull String uuid) {
		final @NotNull List<String> result = new GapList<>();
		if (args.length == 2) {
			//TestUtilsTabCompleter.getCompletions(args[1], "-n", "-north", "-s", "-south", "-here", "-other", "-manual", "-startup", "-hourly", "-daily");
		}
		return null;
	}
}
