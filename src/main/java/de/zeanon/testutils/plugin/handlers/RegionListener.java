package de.zeanon.testutils.plugin.handlers;

import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.enums.flags.Flag;
import de.zeanon.testutils.plugin.utils.enums.flags.flagvalues.*;
import de.zeanon.testutils.plugin.utils.region.DefinedRegion;
import de.zeanon.testutils.plugin.utils.region.GlobalRegion;
import de.zeanon.testutils.plugin.utils.region.RegionManager;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class RegionListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockFromTo(final @NotNull BlockFromToEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}

		RegionListener.regionHasChanged(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockIgnite(final @NotNull BlockIgniteEvent event) {
		final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(event.getBlock().getWorld());
		if (globalRegion.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		final @NotNull List<DefinedRegion> regions = RegionManager.getApplicableRegions(event.getBlock().getLocation());
		if (regions.isEmpty() && globalRegion.get(Flag.FIRE) == FIRE.DENY) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : regions) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE || region.get(Flag.FIRE) == FIRE.DENY) {
				event.setCancelled(true);
				return;
			}
		}

		RegionListener.regionHasChanged(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBurn(final @NotNull BlockBurnEvent event) {
		final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(event.getBlock().getWorld());
		if (globalRegion.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		final @NotNull List<DefinedRegion> regions = RegionManager.getApplicableRegions(event.getBlock().getLocation());
		if (regions.isEmpty() && globalRegion.get(Flag.FIRE) == FIRE.DENY) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : regions) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE || region.get(Flag.FIRE) == FIRE.DENY) {
				event.setCancelled(true);
				return;
			}
		}

		RegionListener.regionHasChanged(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPhysics(final @NotNull BlockPhysicsEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}

		RegionListener.regionHasChanged(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onLeavesDecay(final @NotNull LeavesDecayEvent event) {
		final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(event.getBlock().getWorld());
		if (globalRegion.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		final @NotNull List<DefinedRegion> regions = RegionManager.getApplicableRegions(event.getBlock().getLocation());
		if (regions.isEmpty() && globalRegion.get(Flag.LEAVES_DECAY) == LEAVES_DECAY.DENY) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE || region.get(Flag.LEAVES_DECAY) == LEAVES_DECAY.DENY) {
				event.setCancelled(true);
				return;
			}
		}

		RegionListener.regionHasChanged(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockForm(final @NotNull BlockFormEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}

		RegionListener.regionHasChanged(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockSpread(final @NotNull BlockSpreadEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}

		RegionListener.regionHasChanged(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockExplode(final @NotNull BlockExplodeEvent event) {
		final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(event.getBlock().getWorld());
		if (globalRegion.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		final @NotNull List<DefinedRegion> applicableRegions = RegionManager.getApplicableRegions(event.getBlock().getLocation());
		if (applicableRegions.isEmpty() && globalRegion.get(Flag.TNT) == TNT.DENY) {
			event.blockList().clear();
			return;
		}

		for (final @NotNull DefinedRegion region : applicableRegions) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE && region.get(Flag.TNT) == TNT.DENY) {
				event.blockList().clear();
				event.setCancelled(true);
				return;
			}

			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}

			if (region.get(Flag.TNT) == TNT.DENY) {
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

					if (region.get(Flag.TNT) == TNT.DENY) {
						remove = true;
						break;
					}
				}
				return remove;
			}
		});

		RegionListener.regionHasChanged(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityExplode(final @NotNull EntityExplodeEvent event) {
		final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(Objects.notNull(event.getLocation().getWorld()));
		if (globalRegion.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		final @NotNull List<DefinedRegion> applicableRegions = RegionManager.getApplicableRegions(event.getLocation());
		if (applicableRegions.isEmpty() && globalRegion.get(Flag.TNT) == TNT.DENY) {
			event.blockList().clear();
			return;
		}

		for (final @NotNull DefinedRegion region : applicableRegions) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE && region.get(Flag.TNT) == TNT.DENY) {
				event.blockList().clear();
				event.setCancelled(true);
				return;
			}

			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}

			if (region.get(Flag.TNT) == TNT.DENY) {
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

					if (region.get(Flag.TNT) == TNT.DENY) {
						remove = true;
						break;
					}
				}
				return remove;
			}
		});

		RegionListener.regionHasChanged(event.getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onExplosionPrime(final @NotNull ExplosionPrimeEvent event) {
		if (RegionManager.getGlobalRegion(event.getEntity().getWorld()).get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getEntity().getLocation())) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockMultiPlace(final @NotNull BlockMultiPlaceEvent event) {
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			region.setHasChanged(true);
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				for (final @NotNull BlockState tempState : event.getReplacedBlockStates()) {
					for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(tempState.getLocation())) {
						region.setHasChanged(true);
					}
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(final @NotNull BlockPlaceEvent event) {
		RegionListener.regionHasChanged(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onCanBuild(final @NotNull BlockCanBuildEvent event) {
		if (event.isBuildable()) {
			if (event.getMaterial() == Material.TNT) {
				if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
					event.setBuildable(false);
					event.getBlock().setType(Material.TNT);
				} else {
					for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
						if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
							event.setBuildable(false);
							event.getBlock().setType(Material.TNT);
							break;
						}
					}
				}
			}

			RegionListener.regionHasChanged(event.getBlock().getLocation());
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(final @NotNull BlockBreakEvent event) {
		final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(Objects.notNull(event.getBlock().getWorld()));
		if (globalRegion.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setDropItems(false);
			RegionListener.regionHasChanged(event.getBlock().getLocation());
			return;
		}

		final @NotNull List<DefinedRegion> applicableRegions = RegionManager.getApplicableRegions(event.getBlock().getLocation());
		if (applicableRegions.isEmpty() && globalRegion.get(Flag.ITEM_DROPS) == ITEM_DROPS.DENY) {
			event.setDropItems(false);
			RegionListener.regionHasChanged(event.getBlock().getLocation());
			return;
		}

		for (final @NotNull DefinedRegion region : applicableRegions) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE || region.get(Flag.ITEM_DROPS) == ITEM_DROPS.DENY) {
				event.setDropItems(false);
			}
		}

		RegionListener.regionHasChanged(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInteract(final @NotNull PlayerInteractEvent event) {
		if (event.getClickedBlock() != null) {
			if (RegionManager.getGlobalRegion(event.getClickedBlock().getWorld()).get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}

			if (event.getClickedBlock().getType() == Material.TNT && event.getItem() != null && event.getItem().getType() == Material.FLINT_AND_STEEL) {
				for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getClickedBlock().getLocation())) {
					if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
						event.setCancelled(true);
						return;
					}
				}
			}

			RegionListener.regionHasChanged(event.getClickedBlock().getLocation());
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntitySpawn(final @NotNull EntitySpawnEvent event) {
		if (RegionManager.getGlobalRegion(Objects.notNull(event.getLocation().getWorld())).get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getLocation())) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
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

		RegionListener.regionHasChanged(event.getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityChangeBlock(final @NotNull EntityChangeBlockEvent event) {
		if (RegionManager.getGlobalRegion(event.getEntity().getWorld()).get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}

		RegionListener.regionHasChanged(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPistonExtend(final @NotNull BlockPistonExtendEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}

		RegionListener.regionHasChanged(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPistonRetract(final @NotNull BlockPistonRetractEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}

		RegionListener.regionHasChanged(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockGrow(final @NotNull BlockGrowEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}

		RegionListener.regionHasChanged(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockRedstoneEvent(final @NotNull BlockRedstoneEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setNewCurrent(event.getOldCurrent());
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setNewCurrent(event.getOldCurrent());
				return;
			}
		}

		RegionListener.regionHasChanged(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockDispense(final @NotNull BlockDispenseEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}

		RegionListener.regionHasChanged(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryMoveItem(final @NotNull InventoryMoveItemEvent event) {
		final @Nullable Location destination = event.getDestination().getLocation();
		if (destination != null) {
			if (RegionManager.getGlobalRegion(Objects.notNull(destination.getWorld())).get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}

			for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(destination)) {
				if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
					event.setCancelled(true);
					return;
				}
			}

			RegionListener.regionHasChanged(destination);
		}

		final @Nullable Location source = event.getSource().getLocation();
		if (source != null) {
			if (RegionManager.getGlobalRegion(Objects.notNull(source.getWorld())).get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
			}

			for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(source)) {
				if (region.get(Flag.STOPLAG) == STOPLAG.ACTIVE) {
					event.setCancelled(true);
					return;
				}
			}

			RegionListener.regionHasChanged(source);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerDamage(final @NotNull EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(Objects.notNull(event.getEntity().getWorld()));
			final @NotNull List<DefinedRegion> regions = RegionManager.getApplicableRegions(event.getEntity().getLocation());
			if (regions.isEmpty() && (globalRegion.get(Flag.DAMAGE) == DAMAGE.DENY
									  || (event.getCause() == EntityDamageEvent.DamageCause.FALL
										  && globalRegion.get(Flag.FALL_DAMAGE) == FALL_DAMAGE.DENY))) {
				event.setCancelled(true);
				return;
			}

			for (final @NotNull DefinedRegion region : regions) {
				if (region.get(Flag.DAMAGE) == DAMAGE.DENY
					|| (event.getCause() == EntityDamageEvent.DamageCause.FALL
						&& region.get(Flag.FALL_DAMAGE) == FALL_DAMAGE.DENY)) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	private static void regionHasChanged(final @NotNull Location location) {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(location)) {
					region.setHasChanged(true);
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}
}