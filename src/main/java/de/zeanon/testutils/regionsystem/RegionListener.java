package de.zeanon.testutils.regionsystem;

import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.regionsystem.flags.Flag;
import de.zeanon.testutils.regionsystem.flags.flagvalues.*;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import de.zeanon.testutils.regionsystem.region.GlobalRegion;
import de.zeanon.testutils.regionsystem.tags.Tag;
import de.zeanon.testutils.regionsystem.tags.tagvalues.CHANGED;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class RegionListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockFromTo(final @NotNull BlockFromToEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE
			|| RegionManager.getGlobalRegion(event.getToBlock().getWorld()).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getToBlock().getLocation())) {
			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockFromToMonitor(final @NotNull BlockFromToEvent event) {
		RegionListener.tagChangedRegions(event.getBlock().getLocation());
		RegionListener.tagChangedRegions(event.getToBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockIgnite(final @NotNull BlockIgniteEvent event) {
		final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(event.getBlock().getWorld());
		if (globalRegion.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		boolean checkGlobal = true;
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			final @Nullable Flag.Value<?> value = region.getFlag(Flag.FIRE);
			if (value != null) {
				checkGlobal = false;
			}

			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE || value == FIRE.DENY) {
				event.setCancelled(true);
				return;
			}
		}

		if (checkGlobal && globalRegion.getFlag(Flag.FIRE) == FIRE.DENY) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockIgniteMonitor(final @NotNull BlockIgniteEvent event) {
		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBurn(final @NotNull BlockBurnEvent event) {
		final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(event.getBlock().getWorld());
		if (globalRegion.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		boolean checkGlobal = true;
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			final @Nullable Flag.Value<?> value = region.getFlag(Flag.FIRE);
			if (value != null) {
				checkGlobal = false;
			}

			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE || value == FIRE.DENY) {
				event.setCancelled(true);
				return;
			}
		}

		if (checkGlobal && globalRegion.getFlag(Flag.FIRE) == FIRE.DENY) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBurnMonitor(final @NotNull BlockBurnEvent event) {
		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPhysics(final @NotNull BlockPhysicsEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPhysicsMonitor(final @NotNull BlockPhysicsEvent event) {
		if (event.getBlock().getType() != event.getChangedType()) {
			RegionListener.tagChangedRegions(event.getBlock().getLocation());
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onLeavesDecay(final @NotNull LeavesDecayEvent event) {
		final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(event.getBlock().getWorld());
		if (globalRegion.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		boolean checkGlobal = true;
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			final @Nullable Flag.Value<?> value = region.getFlag(Flag.LEAVES_DECAY);
			if (value != null) {
				checkGlobal = false;
			}

			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE || value == LEAVES_DECAY.DENY) {
				event.setCancelled(true);
				return;
			}
		}

		if (checkGlobal && globalRegion.getFlag(Flag.LEAVES_DECAY) == LEAVES_DECAY.DENY) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLeavesDecayMonitor(final @NotNull LeavesDecayEvent event) {
		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockForm(final @NotNull BlockFormEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockFormMonitor(final @NotNull BlockFormEvent event) {
		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockSpread(final @NotNull BlockSpreadEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE
			|| RegionManager.getGlobalRegion(event.getSource().getWorld()).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getSource().getLocation())) {
			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockSpreadMonitor(final @NotNull BlockSpreadEvent event) {
		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockExplode(final @NotNull BlockExplodeEvent event) {
		final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(event.getBlock().getWorld());
		if (globalRegion.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}

		event.blockList().removeIf(block -> RegionListener.doNotDestroyBlock(block, globalRegion));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockExplodeMonitor(final @NotNull BlockExplodeEvent event) {
		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityExplode(final @NotNull EntityExplodeEvent event) {
		final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(Objects.notNull(event.getLocation().getWorld()));
		if (globalRegion.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getLocation())) {
			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}

		event.blockList().removeIf(block -> RegionListener.doNotDestroyBlock(block, globalRegion));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityExplodeMonitor(final @NotNull EntityExplodeEvent event) {
		RegionListener.tagChangedRegions(event.getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onExplosionPrime(final @NotNull ExplosionPrimeEvent event) {
		if (RegionManager.getGlobalRegion(event.getEntity().getWorld()).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getEntity().getLocation())) {
			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onExplosionPrimeMonitor(final @NotNull ExplosionPrimeEvent event) {
		RegionListener.tagChangedRegions(event.getEntity().getLocation());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlaceMonitor(final @NotNull BlockPlaceEvent event) {
		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockMultiPlaceMonitor(final @NotNull BlockMultiPlaceEvent event) {
		RegionListener.tagChangedRegions(event.getBlock().getLocation());

		new BukkitRunnable() {
			@Override
			public void run() {
				for (final @NotNull BlockState tempState : event.getReplacedBlockStates()) {
					RegionManager.executeOnApplicableRegions(tempState.getLocation(), definedRegion -> definedRegion.setTag(Tag.CHANGED, CHANGED.TRUE));
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onCanBuild(final @NotNull BlockCanBuildEvent event) {
		if (event.isBuildable() && event.getMaterial() == Material.TNT) {
			if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setBuildable(false);
				event.getBlock().setType(event.getMaterial());
			} else {
				for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
					if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
						event.setBuildable(false);
						event.getBlock().setType(event.getMaterial());
						return;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCanBuildMonitor(final @NotNull BlockCanBuildEvent event) {
		if (event.isBuildable()) {
			RegionListener.tagChangedRegions(event.getBlock().getLocation());
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(final @NotNull BlockBreakEvent event) {
		final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(Objects.notNull(event.getBlock().getWorld()));
		if (globalRegion.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setDropItems(false);
			return;
		}

		boolean checkGlobal = true;
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			final @Nullable Flag.Value<?> value = region.getFlag(Flag.ITEM_DROPS);

			if (value != null) {
				checkGlobal = false;
			}

			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE || value == ITEM_DROPS.DENY) {
				if (event.getBlock().getState() instanceof Container) {
					((Container) event.getBlock().getState()).getInventory().clear();
				}

				event.setDropItems(false);
				return;
			}
		}

		if (checkGlobal && globalRegion.getFlag(Flag.ITEM_DROPS) == ITEM_DROPS.DENY) {
			if (event.getBlock() instanceof InventoryHolder) {
				((InventoryHolder) event.getBlock()).getInventory().clear();
			}
			event.setDropItems(false);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreakMonitor(final @NotNull BlockBreakEvent event) {
		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInteract(final @NotNull PlayerInteractEvent event) {
		if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.TNT && event.getItem() != null && event.getItem().getType() == Material.FLINT_AND_STEEL && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (RegionManager.getGlobalRegion(event.getClickedBlock().getWorld()).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}

			for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getClickedBlock().getLocation())) {
				if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInteractMonitor(final @NotNull PlayerInteractEvent event) {
		if (event.getClickedBlock() != null) {
			RegionListener.tagChangedRegions(event.getClickedBlock().getLocation());
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntitySpawn(final @NotNull EntitySpawnEvent event) {
		if (RegionManager.getGlobalRegion(Objects.notNull(event.getLocation().getWorld())).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getLocation())) {
			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntitySpawnMonitor(final @NotNull EntitySpawnEvent event) {
		RegionListener.tagChangedRegions(event.getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityChangeBlock(final @NotNull EntityChangeBlockEvent event) {
		if (RegionManager.getGlobalRegion(event.getEntity().getWorld()).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityChangeBlockMonitor(final @NotNull EntityChangeBlockEvent event) {
		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPistonExtend(final @NotNull BlockPistonExtendEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull Block block : event.getBlocks()) {
			for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(block.getLocation())) {
				if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPistonExtendMonitor(final @NotNull BlockPistonExtendEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				RegionManager.executeOnApplicableRegions(event.getBlock().getLocation(), definedRegion -> definedRegion.setTag(Tag.CHANGED, CHANGED.TRUE));
				event.getBlocks().forEach(block -> RegionManager.executeOnApplicableRegions(block.getLocation(), definedRegion -> definedRegion.setTag(Tag.CHANGED, CHANGED.TRUE)));
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPistonRetract(final @NotNull BlockPistonRetractEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}

		for (final @NotNull Block block : event.getBlocks()) {
			for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(block.getLocation())) {
				if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPistonRetractMonitor(final @NotNull BlockPistonRetractEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				RegionManager.executeOnApplicableRegions(event.getBlock().getLocation(), definedRegion -> definedRegion.setTag(Tag.CHANGED, CHANGED.TRUE));
				event.getBlocks().forEach(block -> RegionManager.executeOnApplicableRegions(block.getLocation(), definedRegion -> definedRegion.setTag(Tag.CHANGED, CHANGED.TRUE)));
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockGrow(final @NotNull BlockGrowEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockGrowMonitor(final @NotNull BlockGrowEvent event) {
		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockRedstone(final @NotNull BlockRedstoneEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setNewCurrent(event.getOldCurrent());
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setNewCurrent(event.getOldCurrent());
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockRedstoneMonitor(final @NotNull BlockRedstoneEvent event) {
		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockDispense(final @NotNull BlockDispenseEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockDispenseMonitor(final @NotNull BlockDispenseEvent event) {
		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryMoveItem(final @NotNull InventoryMoveItemEvent event) {
		final @Nullable Location source = event.getSource().getLocation();
		if (source != null) {
			if (RegionManager.getGlobalRegion(Objects.notNull(source.getWorld())).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
			}

			for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(source)) {
				if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
					event.setCancelled(true);
					return;
				}
			}
		}

		final @Nullable Location destination = event.getDestination().getLocation();
		if (destination != null) {
			if (RegionManager.getGlobalRegion(Objects.notNull(destination.getWorld())).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setCancelled(true);
				return;
			}

			for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(destination)) {
				if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryMoveItemMonitor(final @NotNull InventoryMoveItemEvent event) {
		final @Nullable Location source = event.getSource().getLocation();
		final @Nullable Location destination = event.getDestination().getLocation();

		if (source != null) {
			RegionListener.tagChangedRegions(source);
		}

		if (destination != null) {
			RegionListener.tagChangedRegions(destination);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerDamage(final @NotNull EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(Objects.notNull(event.getEntity().getWorld()));

			if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
				boolean checkGlobal = true;

				for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getEntity().getLocation())) {
					final @Nullable Flag.Value<?> damageValue = region.getFlag(Flag.DAMAGE);
					final @Nullable Flag.Value<?> fallDamageValue = region.getFlag(Flag.FALL_DAMAGE);

					if (damageValue != null || fallDamageValue != null) {
						checkGlobal = false;
					}

					if (damageValue == DAMAGE.DENY || fallDamageValue == FALL_DAMAGE.DENY) {
						event.setCancelled(true);
						return;
					}
				}

				if (checkGlobal && (globalRegion.getFlag(Flag.DAMAGE) == DAMAGE.DENY || globalRegion.getFlag(Flag.FALL_DAMAGE) == FALL_DAMAGE.DENY)) {
					event.setCancelled(true);
				}
			} else {
				boolean checkGlobal = true;

				for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getEntity().getLocation())) {
					final @Nullable Flag.Value<?> damageValue = region.getFlag(Flag.DAMAGE);

					if (damageValue != null) {
						checkGlobal = false;
					}

					if (damageValue == DAMAGE.DENY) {
						event.setCancelled(true);
						return;
					}
				}

				if (checkGlobal && globalRegion.getFlag(Flag.DAMAGE) == DAMAGE.DENY) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onMobSpawn(final @NotNull CreatureSpawnEvent event) {
		final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(Objects.notNull(event.getEntity().getWorld()));

		boolean checkGlobal = true;

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getEntity().getLocation())) {
			final @Nullable Flag.Value<?> mobSpawn = region.getFlag(Flag.MOB_SPAWN);

			if (mobSpawn != null) {
				checkGlobal = false;
			}

			if (mobSpawn == MOB_SPAWN.DENY
				&& event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.DISPENSE_EGG
				&& event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
				event.setCancelled(true);
				return;
			}
		}

		if (checkGlobal
			&& globalRegion.getFlag(Flag.MOB_SPAWN) == MOB_SPAWN.DENY
			&& event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.DISPENSE_EGG
			&& event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
			event.setCancelled(true);
		}
	}


	private static boolean doNotDestroyBlock(final @NotNull Block block, final @NotNull GlobalRegion globalRegion) {
		boolean ignoreDestroy = true;
		boolean ignoreDrops = true;

		boolean destroy = true;
		boolean dropItems = true;
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(block.getLocation())) {
			final @Nullable Flag.Value<?> tntValue = region.getFlag(Flag.TNT);
			final @Nullable Flag.Value<?> stoplagValue = region.getFlag(Flag.STOPLAG);
			final @Nullable Flag.Value<?> dropValue = region.getFlag(Flag.ITEM_DROPS);

			if (tntValue != null || stoplagValue != null) {
				ignoreDestroy = false;
			}

			if (dropValue != null) {
				ignoreDrops = false;
			}

			if (tntValue == TNT.DENY || stoplagValue == STOPLAG.ACTIVE) {
				destroy = false;
			}

			if (dropValue == ITEM_DROPS.DENY) {
				dropItems = false;
			}
		}

		if (ignoreDestroy) {
			destroy = globalRegion.getFlag(Flag.TNT) == TNT.ALLOW && globalRegion.getFlag(Flag.STOPLAG) == STOPLAG.INACTIVE;
		}

		if (!destroy) {
			return true;
		}

		RegionListener.tagChangedRegions(block.getLocation());


		if (ignoreDrops) {
			dropItems = globalRegion.getFlag(Flag.ITEM_DROPS) == ITEM_DROPS.ALLOW;
		}

		if (!dropItems) {
			if (block.getState() instanceof Container) {
				((Container) block.getState()).getInventory().clear();
			}

			block.setType(Material.AIR);
			RegionListener.tagChangedRegions(block.getLocation());
			return true;
		}

		return false;
	}

	private static void tagChangedRegions(final @NotNull Location location) {
		new BukkitRunnable() {
			@Override
			public void run() {
				RegionManager.executeOnApplicableRegions(location, definedRegion -> definedRegion.setTag(Tag.CHANGED, CHANGED.TRUE));
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}
}