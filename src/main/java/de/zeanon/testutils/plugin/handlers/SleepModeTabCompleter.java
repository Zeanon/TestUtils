package de.zeanon.testutils.plugin.handlers;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class SleepModeTabCompleter implements TabCompleter {

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if (command.getName().equalsIgnoreCase("testutils")) {
			if (args.length == 0) {
				return Collections.singletonList("update");
			} else if (args.length == 1) {
				if ("update".startsWith(args[0].toLowerCase())) {
					return Collections.singletonList("update");
				} else {
					return Collections.emptyList();
				}
			} else {
				return Collections.emptyList();
			}
		} else {
			return Collections.emptyList();
		}
	}
}
