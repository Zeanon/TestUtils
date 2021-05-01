package de.zeanon.testutils.plugin.handlers;

import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.commands.countingwand.Countingwand;
import de.zeanon.testutils.plugin.update.Update;
import de.zeanon.testutils.plugin.utils.ScoreBoard;
import de.zeanon.testutils.regionsystem.region.Region;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.NotNull;


public class EventListener implements Listener {

	@EventHandler
	public void onBlockBreak(final @NotNull BlockBreakEvent event) {
		if (!Countingwand.isCountingwand(event.getPlayer().getInventory().getItemInMainHand())) {
			return;
		}

		event.setCancelled(true);
		Countingwand.checkSelection(Region.Point.fromLocation(event.getBlock().getLocation()), true, event.getPlayer());
	}

	@EventHandler
	public void onPlayerInteract(final @NotNull PlayerInteractEvent event) {
		if (!Countingwand.isCountingwand(event.getItem())) {
			return;
		}

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		event.setCancelled(true);
		Countingwand.checkSelection(Region.Point.fromLocation(Objects.notNull(event.getClickedBlock()).getLocation()), false, event.getPlayer());
	}

	@EventHandler
	public void onJoin(final @NotNull PlayerJoinEvent event) {
		Update.updateAvailable(event.getPlayer());
		ScoreBoard.register(event.getPlayer());
	}

	@EventHandler
	public void onLeave(final @NotNull PlayerQuitEvent event) {
		ScoreBoard.unregister(event.getPlayer());
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