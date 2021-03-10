package de.zeanon.testutils.plugin.handlers;

import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.commands.Stoplag;
import de.zeanon.testutils.plugin.update.Update;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.jetbrains.annotations.NotNull;


public class EventListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		final @NotNull String[] args = event.getMessage().replace("worldguard:", "").split("\\s+");
		if ((args[0].equalsIgnoreCase("/rg") || args[0].equalsIgnoreCase("/region"))
			&& (args[1].equalsIgnoreCase("define") || args[1].equalsIgnoreCase("d") || args[1].equalsIgnoreCase("create"))
			&& args[2].toLowerCase().startsWith("testarea_")
			&& (args[2].toLowerCase().endsWith("_north") || args[2].toLowerCase().endsWith("_south"))) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "You are not allowed to create a region which starts with '"
										  + ChatColor.DARK_RED + "testarea_"
										  + ChatColor.RED + "' and ends with '"
										  + ChatColor.DARK_RED + args[2].substring(args[2].length() - 6)
										  + "'.");
		} else if (args[0].equalsIgnoreCase("/stoplag")) {
			event.setCancelled(true);
			Stoplag.execute(args, event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onTab(final @NotNull TabCompleteEvent event) {
		if (event.getBuffer().toLowerCase().startsWith("/stoplag")) {
			event.setCompletions(Stoplag.onTab(event.getBuffer()));
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

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockFromTo(BlockFromToEvent event) {
		if (Stoplag.inStoplagRegion(event.getBlock().getLocation()) || Stoplag.inStoplagRegion(event.getToBlock().getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (Stoplag.inStoplagRegion(Objects.notNull(event.getIgnitingBlock()).getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent event) {
		if (Stoplag.inStoplagRegion(Objects.notNull(event.getIgnitingBlock()).getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (Stoplag.inStoplagRegion(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onLeavesDecay(LeavesDecayEvent event) {
		if (Stoplag.inStoplagRegion(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockForm(BlockFormEvent event) {
		if (Stoplag.inStoplagRegion(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockSpread(BlockSpreadEvent event) {
		if (Stoplag.inStoplagRegion(event.getSource().getLocation()) || Stoplag.inStoplagRegion(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockExplode(BlockExplodeEvent event) {
		boolean remove = false;
		for (final @NotNull Block block : event.blockList()) {
			if (Stoplag.inStoplagRegion(block.getLocation())) {
				remove = true;
				break;
			}
		}

		if (remove || Stoplag.inStoplagRegion(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (Stoplag.inStoplagRegion(event.getLocation())) {
			event.getEntity().remove();
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onExplosionPrime(ExplosionPrimeEvent event) {
		if (Stoplag.inStoplagRegion(event.getEntity().getLocation())) {
			event.getEntity().remove();
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (Stoplag.inStoplagRegion(event.getLocation())) {
			event.getEntity().remove();
			event.setCancelled(true);
		}
	}
}