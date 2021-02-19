package de.zeanon.testutils.plugin.handlers;

import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.update.Update;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.NotNull;


public class EventListener implements Listener {

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		final @NotNull String[] args = event.getMessage().replace("worldguard:", "/").split("\\s+");
		if (args[0].equalsIgnoreCase("/rg") || args[0].equalsIgnoreCase("/region") && args[1].equalsIgnoreCase("define") && args[2].toLowerCase().startsWith("testarea_") && (args[2].toLowerCase().endsWith("_north") || args[2].toLowerCase().endsWith("_south"))) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(GlobalMessageUtils.messageHead +
										  ChatColor.RED + "You are not allowed to create a region which starts with '"
										  + ChatColor.DARK_RED + "testarea_"
										  + ChatColor.RED + "' and ends with '"
										  + ChatColor.DARK_RED + args[2].substring(args[2].length() - 6)
										  + "'.");
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Update.updateAvailable(event.getPlayer());
	}

	@EventHandler
	public void onPluginDisable(final @NotNull PluginDisableEvent event) {
		if (event.getPlugin().getName().equalsIgnoreCase("WorldEdit")
			|| event.getPlugin().getName().equalsIgnoreCase("FastAsyncWorldEdit")
			|| event.getPlugin().getName().equalsIgnoreCase("WorldGuard")) {
			TestUtils.getPluginManager().disablePlugin(TestUtils.getInstance());
			TestUtils.getPluginManager().enablePlugin(TestUtils.getInstance());
		}
	}
}