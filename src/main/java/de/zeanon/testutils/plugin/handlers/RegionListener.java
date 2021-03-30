package de.zeanon.testutils.plugin.handlers;

import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.region.DefinedRegion;
import de.zeanon.testutils.plugin.utils.region.RegionManager;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class RegionListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockFromTo(final @NotNull BlockFromToEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.stoplag()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			region.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockIgnite(final @NotNull BlockIgniteEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.stoplag() || !region.fire()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			region.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBurn(final @NotNull BlockBurnEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.stoplag() || !region.fire()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			region.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPhysics(final @NotNull BlockPhysicsEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.stoplag()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			region.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onLeavesDecay(final @NotNull LeavesDecayEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.stoplag()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			region.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockForm(final @NotNull BlockFormEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.stoplag()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			region.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockSpread(final @NotNull BlockSpreadEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.stoplag()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			region.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockExplode(final @NotNull BlockExplodeEvent event) {
		final @NotNull List<DefinedRegion> applicableRegions = RegionManager.getApplicableRegions(event.getBlock().getLocation());
		if (applicableRegions.isEmpty()) {
			event.blockList().clear();
		} else {
			for (final @NotNull DefinedRegion region : applicableRegions) {
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
				final @NotNull List<DefinedRegion> internalApplicableRegions = RegionManager.getApplicableRegions(block.getLocation());
				if (internalApplicableRegions.isEmpty()) {
					return true;
				} else {
					boolean remove = false;
					for (final @NotNull DefinedRegion region : internalApplicableRegions) {
						region.setHasChanged(true);

						if (!region.tnt()) {
							remove = true;
							break;
						}
					}
					return remove;
				}
			});

			for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
				region.setHasChanged(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityExplode(final @NotNull EntityExplodeEvent event) {
		final @NotNull List<DefinedRegion> applicableRegions = RegionManager.getApplicableRegions(event.getLocation());
		if (applicableRegions.isEmpty()) {
			event.blockList().clear();
		} else {
			for (final @NotNull DefinedRegion region : applicableRegions) {
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
				final @NotNull List<DefinedRegion> internalApplicableRegions = RegionManager.getApplicableRegions(block.getLocation());
				if (internalApplicableRegions.isEmpty()) {
					return true;
				} else {
					boolean remove = false;
					for (final @NotNull DefinedRegion region : internalApplicableRegions) {
						region.setHasChanged(true);

						if (!region.tnt()) {
							remove = true;
							break;
						}
					}
					return remove;
				}
			});

			for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getLocation())) {
				region.setHasChanged(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onExplosionPrime(final @NotNull ExplosionPrimeEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getEntity().getLocation())) {
			if (region.stoplag()) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(final @NotNull BlockMultiPlaceEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			region.setHasChanged(true);
		}

		for (final @NotNull BlockState tempState : event.getReplacedBlockStates()) {
			for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(tempState.getLocation())) {
				region.setHasChanged(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(final @NotNull BlockPlaceEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			region.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onCanBuild(final @NotNull BlockCanBuildEvent event) {
		if (event.isBuildable()) {
			for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
				if (region.stoplag() && event.getMaterial() == Material.TNT) {
					event.setBuildable(false);
					event.getBlock().setType(Material.TNT);
					break;
				}
			}

			for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
				region.setHasChanged(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(final @NotNull BlockBreakEvent event) {
		final @NotNull List<DefinedRegion> applicableRegions = RegionManager.getApplicableRegions(event.getBlock().getLocation());
		if (applicableRegions.isEmpty()) {
			event.setCancelled(true);
			event.getBlock().setType(Material.AIR);
		} else {
			for (final @NotNull DefinedRegion region : applicableRegions) {
				if (region.stoplag() || !region.itemDrops()) {
					event.setCancelled(true);
					event.getBlock().setType(Material.AIR);
				}
			}
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			region.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInteract(final @NotNull PlayerInteractEvent event) {
		if (event.getClickedBlock() != null) {
			for (final @NotNull DefinedRegion region : RegionManager.getRegions()) {
				if (!region.tnt() && event.getClickedBlock().getType() == Material.TNT && event.getItem() != null && event.getItem().getType() == Material.FLINT_AND_STEEL) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntitySpawn(final @NotNull EntitySpawnEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getLocation())) {
			if (region.stoplag()) {
				event.setCancelled(true);
				if (event.getEntityType() == EntityType.PRIMED_TNT) {
					new BukkitRunnable() {
						@Override
						public void run() {
							event.getLocation().getBlock().setType(Material.TNT, false);
						}
					}.runTaskLater(TestUtils.getInstance(), 1);
					return;
				}
			}
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getLocation())) {
			region.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityChangeBlock(final @NotNull EntityChangeBlockEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.stoplag()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			region.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPistonExtend(final @NotNull BlockPistonExtendEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.stoplag()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			region.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPistonRetract(final @NotNull BlockPistonRetractEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.stoplag()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			region.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockGrow(final @NotNull BlockGrowEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.stoplag()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			region.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockRedstoneEvent(final @NotNull BlockRedstoneEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.stoplag()) {
				event.setNewCurrent(event.getOldCurrent());
				return;
			}
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			region.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockDispense(final @NotNull BlockDispenseEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.stoplag()) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			region.setHasChanged(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryMoveItem(final @NotNull InventoryMoveItemEvent event) {
		final @Nullable Location destination = event.getDestination().getLocation();
		if (destination != null) {
			for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(destination)) {
				if (region.stoplag()) {
					event.setCancelled(true);
					return;
				}
			}

			for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(destination)) {
				region.setHasChanged(true);
			}
		}

		final @Nullable Location initiation = event.getInitiator().getLocation();
		if (initiation != null) {
			for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(initiation)) {
				if (region.stoplag()) {
					event.setCancelled(true);
					return;
				}
			}

			for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(initiation)) {
				region.setHasChanged(true);
			}
		}
	}
}