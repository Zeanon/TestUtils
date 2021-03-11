package de.zeanon.testutils.plugin.handlers.tabcompleter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class StoplagTabCompleter {

	public @NotNull List<String> onTab(final @NotNull String buffer) {
		final @NotNull String message = buffer.replaceAll("\\s+", " ");
		final @NotNull String[] args = message.split(" ");
		final boolean argumentEnded = message.endsWith(" ");
		if ((args.length == 2 && !argumentEnded) || (args.length == 1 && argumentEnded)) {
			if (argumentEnded) {
				return Arrays.asList("-c", "here", "other", "global");
			} else {
				return StoplagTabCompleter.getCompletions(args[1], "-c", "here", "other", "global");
			}
		} else if (args.length == 3 && !argumentEnded || args.length == 2) {
			if (argumentEnded) {
				if (!args[1].equalsIgnoreCase("-c")) {
					return Collections.singletonList("-c");
				} else {
					return Collections.emptyList();
				}
			} else {
				if (!args[1].equalsIgnoreCase("-c")) {
					return StoplagTabCompleter.getCompletions(args[2], "-c");
				} else {
					return Collections.emptyList();
				}
			}
		} else {
			return Collections.emptyList();
		}
	}

	private List<String> getCompletions(final @NotNull String arg, final @NotNull String... completions) {
		return Arrays.stream(completions)
					 .filter(completion -> completion.startsWith(arg.toLowerCase()))
					 .collect(Collectors.toList());
	}
}