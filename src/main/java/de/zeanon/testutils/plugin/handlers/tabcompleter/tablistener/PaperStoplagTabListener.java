package de.zeanon.testutils.plugin.handlers.tabcompleter.tablistener;

import de.zeanon.testutils.plugin.handlers.tabcompleter.StoplagTabCompleter;
import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;
import org.jetbrains.annotations.NotNull;


public class PaperStoplagTabListener implements Listener {


	@EventHandler(priority = EventPriority.HIGH)
	public void onTab(final @NotNull TabCompleteEvent event) {
		if (event.getBuffer().toLowerCase().startsWith("/stoplag")) {
			List<String> completions = StoplagTabCompleter.onTab(event.getBuffer());
			if (completions.isEmpty()) {
				event.setCancelled(true);
			} else {
				event.setCompletions(completions);
			}
		}
	}
}