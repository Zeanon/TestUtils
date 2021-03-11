package de.zeanon.testutils.plugin.handlers.tabcompleter.tablistener;

import de.zeanon.testutils.plugin.handlers.tabcompleter.StoplagTabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;
import org.jetbrains.annotations.NotNull;


public class SpigotStoplagTabListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onTab(final @NotNull TabCompleteEvent event) {
		System.out.println(event.getBuffer());
		if (event.getBuffer().toLowerCase().startsWith("/stoplag")) {
			event.setCompletions(StoplagTabCompleter.onTab(event.getBuffer()));
		}
	}
}