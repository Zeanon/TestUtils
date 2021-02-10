package de.zeanon.testutils.plugin.handlers;

import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.update.Update;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.jetbrains.annotations.NotNull;


public class WakeupListener implements Listener {

	@EventHandler
	public void onPluginEnable(final @NotNull PluginEnableEvent event) {
		if (event.getPlugin().getName().equalsIgnoreCase("WorldEdit")
			|| event.getPlugin().getName().equalsIgnoreCase("FastAsyncWorldEdit")
			|| event.getPlugin().getName().equalsIgnoreCase("WorldGuard")) {
			TestUtils.getPluginManager().disablePlugin(TestUtils.getInstance());
			TestUtils.getPluginManager().enablePlugin(TestUtils.getInstance());
		}
	}

	@EventHandler
	public void onJoin(final @NotNull PlayerJoinEvent event) {
		Update.updateAvailable(event.getPlayer());
	}
}