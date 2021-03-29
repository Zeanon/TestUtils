package de.zeanon.testutils.plugin.handlers;

import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.commands.Stoplag;
import de.zeanon.testutils.plugin.update.Update;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.ScoreBoard;
import de.zeanon.testutils.plugin.utils.region.Region;
import de.zeanon.testutils.plugin.utils.region.RegionManager;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


public class EventListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onCommand(final @NotNull PlayerCommandPreprocessEvent event) {
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

	@EventHandler
	public void onJoin(final @NotNull PlayerJoinEvent event) {
		Update.updateAvailable(event.getPlayer());
		ScoreBoard.initialize(event.getPlayer());
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
	public void onBlockFromTo(final @NotNull BlockFromToEvent event) {
		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (tempRegion.stoplag()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			tempRegion.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockIgnite(final @NotNull BlockIgniteEvent event) {
		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (tempRegion.stoplag() || !tempRegion.fire()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			tempRegion.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBurn(final @NotNull BlockBurnEvent event) {
		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (tempRegion.stoplag() || !tempRegion.fire()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			tempRegion.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPhysics(final @NotNull BlockPhysicsEvent event) {
		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (tempRegion.stoplag()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			tempRegion.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onLeavesDecay(final @NotNull LeavesDecayEvent event) {
		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (tempRegion.stoplag()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			tempRegion.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockForm(final @NotNull BlockFormEvent event) {
		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (tempRegion.stoplag()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			tempRegion.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockSpread(final @NotNull BlockSpreadEvent event) {
		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (tempRegion.stoplag()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			tempRegion.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockExplode(final @NotNull BlockExplodeEvent event) {
		final @NotNull List<Region> applicableRegions = RegionManager.getApplicableRegions(event.getBlock().getLocation());
		if (applicableRegions.isEmpty()) {
			event.blockList().clear();
		} else {
			for (final @NotNull Region region : applicableRegions) {
				if (region.stoplag() && !region.tnt()) {
					event.blockList().clear();
					event.setCancelled(true);
					return;
				}

				if (region.stoplag()) {
					event.setCancelled(true);
					return;
				}

				if (!region.tnt()) {
					event.blockList().clear();
					return;
				}
			}

			event.blockList().removeIf(block -> {
				final @NotNull List<Region> internalApplicableRegions = RegionManager.getApplicableRegions(block.getLocation());
				if (internalApplicableRegions.isEmpty()) {
					return true;
				} else {
					boolean remove = false;
					for (final @NotNull Region region : internalApplicableRegions) {
						region.setHasChanged(true);

						if (!region.tnt()) {
							remove = true;
							break;
						}
					}
					return remove;
				}
			});

			for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
				tempRegion.setHasChanged(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityExplode(final @NotNull EntityExplodeEvent event) {
		final @NotNull List<Region> applicableRegions = RegionManager.getApplicableRegions(event.getLocation());
		if (applicableRegions.isEmpty()) {
			System.out.println("HIIIIII");
			event.blockList().clear();
		} else {
			for (final @NotNull Region region : applicableRegions) {
				if (region.stoplag() && !region.tnt()) {
					event.blockList().clear();
					event.setCancelled(true);
					return;
				}

				if (region.stoplag()) {
					event.setCancelled(true);
					return;
				}

				if (!region.tnt()) {
					event.blockList().clear();
					return;
				}
			}

			event.blockList().removeIf(block -> {
				final @NotNull List<Region> internalApplicableRegions = RegionManager.getApplicableRegions(block.getLocation());
				if (internalApplicableRegions.isEmpty()) {
					return true;
				} else {
					boolean remove = false;
					for (final @NotNull Region region : internalApplicableRegions) {
						region.setHasChanged(true);

						if (!region.tnt()) {
							remove = true;
							break;
						}
					}
					return remove;
				}
			});

			for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getLocation())) {
				tempRegion.setHasChanged(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onExplosionPrime(final @NotNull ExplosionPrimeEvent event) {
		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getEntity().getLocation())) {
			if (tempRegion.stoplag()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getEntity().getLocation())) {
			tempRegion.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(final @NotNull BlockMultiPlaceEvent event) {
		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (tempRegion.stoplag() && event.getBlock().getType() == Material.TNT) {
				event.setCancelled(true);
				new BukkitRunnable() {
					@Override
					public void run() {
						event.getBlock().setType(Material.TNT, false);
					}
				}.runTaskLater(TestUtils.getInstance(), 1);
			}
		}

		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			tempRegion.setHasChanged(true);
		}

		for (final @NotNull BlockState tempState : event.getReplacedBlockStates()) {
			for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(tempState.getLocation())) {
				tempRegion.setHasChanged(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(final @NotNull BlockPlaceEvent event) {
		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (tempRegion.stoplag() && event.getBlock().getType() == Material.TNT) {
				event.setCancelled(true);
				new BukkitRunnable() {
					@Override
					public void run() {
						event.getBlock().setType(Material.TNT, false);
					}
				}.runTaskLater(TestUtils.getInstance(), 1);
			}
		}

		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			tempRegion.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(final @NotNull BlockBreakEvent event) {
		for (final @NotNull Region tempRegion : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			tempRegion.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInteract(final @NotNull PlayerInteractEvent event) {
		if (event.getClickedBlock() != null) {
			for (final @NotNull Region tempRegion : RegionManager.getRegions()) {
				if (!tempRegion.tnt() && event.getClickedBlock().getType() == Material.TNT && event.getItem() != null && event.getItem().getType() == Material.FLINT_AND_STEEL) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	//TODO Drops verhindern
}