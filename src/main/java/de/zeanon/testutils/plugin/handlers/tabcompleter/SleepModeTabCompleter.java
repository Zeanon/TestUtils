package de.zeanon.testutils.plugin.handlers.tabcompleter;

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
		if (command.getName().equals("testutils")) {
			if (args.length == 1) {
				if ("update".startsWith(args[0].toLowerCase())) {
					return Collections.singletonList("update");
				} else {
					return null;
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("update")) {
					if ("-confirm".startsWith(args[1].toLowerCase())) {
						return Collections.singletonList("-confirm");
					} else if ("-deny".startsWith(args[1].toLowerCase())) {
						return Collections.singletonList("-deny");
					} else {
						return null;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
