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

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockFromTo(final @NotNull BlockFromToEvent event) {
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

		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockIgnite(final @NotNull BlockIgniteEvent event) {
		final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(event.getBlock().getWorld());
		if (globalRegion.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		boolean ignore = true;
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			final @Nullable Flag.Value<?> value = region.getFlag(Flag.FIRE);
			if (value != null) {
				ignore = false;
			}

			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE || value == FIRE.DENY) {
				event.setCancelled(true);
				return;
			}
		}

		if (ignore && globalRegion.getFlag(Flag.FIRE) == FIRE.DENY) {
			event.setCancelled(true);
			return;
		}

		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBurn(final @NotNull BlockBurnEvent event) {
		final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(event.getBlock().getWorld());
		if (globalRegion.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		boolean ignore = true;
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			final @Nullable Flag.Value<?> value = region.getFlag(Flag.FIRE);
			if (value != null) {
				ignore = false;
			}

			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE || value == FIRE.DENY) {
				event.setCancelled(true);
				return;
			}
		}

		if (ignore && globalRegion.getFlag(Flag.FIRE) == FIRE.DENY) {
			event.setCancelled(true);
			return;
		}

		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH)
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

		if (event.getBlock().getType() != event.getChangedType()) {
			RegionListener.tagChangedRegions(event.getBlock().getLocation());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onLeavesDecay(final @NotNull LeavesDecayEvent event) {
		final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(event.getBlock().getWorld());
		if (globalRegion.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		boolean ignore = true;
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			final @Nullable Flag.Value<?> value = region.getFlag(Flag.LEAVES_DECAY);
			if (value != null) {
				ignore = false;
			}

			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE || value == LEAVES_DECAY.DENY) {
				event.setCancelled(true);
				return;
			}
		}

		if (ignore && globalRegion.getFlag(Flag.LEAVES_DECAY) == LEAVES_DECAY.DENY) {
			event.setCancelled(true);
			return;
		}

		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH)
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

		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockSpread(final @NotNull BlockSpreadEvent event) {
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

		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@SuppressWarnings("DuplicatedCode")
	@EventHandler(priority = EventPriority.HIGH)
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

		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@SuppressWarnings("DuplicatedCode")
	@EventHandler(priority = EventPriority.HIGH)
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

		RegionListener.tagChangedRegions(event.getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH)
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

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockMultiPlace(final @NotNull BlockMultiPlaceEvent event) {
		RegionListener.tagChangedRegions(event.getBlock().getLocation());

		new BukkitRunnable() {
			@Override
			public void run() {
				for (final @NotNull BlockState tempState : event.getReplacedBlockStates()) {
					RegionListener.tagChangedRegions(tempState.getLocation());
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(final @NotNull BlockPlaceEvent event) {
		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onCanBuild(final @NotNull BlockCanBuildEvent event) {
		if (event.isBuildable()) {
			if (event.getMaterial() == Material.TNT) {
				if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
					event.setBuildable(false);
					event.getBlock().setType(Material.TNT);
				} else {
					for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
						if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
							event.setBuildable(false);
							event.getBlock().setType(Material.TNT);
							break;
						}
					}
				}
			}

			RegionListener.tagChangedRegions(event.getBlock().getLocation());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(final @NotNull BlockBreakEvent event) {
		final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(Objects.notNull(event.getBlock().getWorld()));
		if (globalRegion.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setDropItems(false);
			RegionListener.tagChangedRegions(event.getBlock().getLocation());
			return;
		}

		boolean ignore = true;
		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			final @Nullable Flag.Value<?> value = region.getFlag(Flag.ITEM_DROPS);

			if (value != null) {
				ignore = false;
			}

			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE || value == ITEM_DROPS.DENY) {
				if (event.getBlock().getState() instanceof Container) {
					((Container) event.getBlock().getState()).getInventory().clear();
				}

				event.setDropItems(false);
				RegionListener.tagChangedRegions(event.getBlock().getLocation());
				return;
			}
		}

		if (ignore && globalRegion.getFlag(Flag.ITEM_DROPS) == ITEM_DROPS.DENY) {
			if (event.getBlock() instanceof InventoryHolder) {
				((InventoryHolder) event.getBlock()).getInventory().clear();
			}
			event.setDropItems(false);
		}

		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInteract(final @NotNull PlayerInteractEvent event) {
		if (event.getClickedBlock() != null) {
			if (event.getClickedBlock().getType() == Material.TNT && event.getItem() != null && event.getItem().getType() == Material.FLINT_AND_STEEL && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
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

			RegionListener.tagChangedRegions(event.getClickedBlock().getLocation());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
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

		RegionListener.tagChangedRegions(event.getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH)
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

		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH)
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

		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH)
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

		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH)
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

		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockRedstoneEvent(final @NotNull BlockRedstoneEvent event) {
		if (RegionManager.getGlobalRegion(event.getBlock().getWorld()).getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
			event.setNewCurrent(event.getOldCurrent());
		}

		for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getBlock().getLocation())) {
			if (region.getFlag(Flag.STOPLAG) == STOPLAG.ACTIVE) {
				event.setNewCurrent(event.getOldCurrent());
				return;
			}
		}

		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH)
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

		RegionListener.tagChangedRegions(event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryMoveItem(final @NotNull InventoryMoveItemEvent event) {
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

			RegionListener.tagChangedRegions(destination);
		}

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

			RegionListener.tagChangedRegions(source);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDamage(final @NotNull EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(Objects.notNull(event.getEntity().getWorld()));

			if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
				boolean ignore = true;

				for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getEntity().getLocation())) {
					final @Nullable Flag.Value<?> damageValue = region.getFlag(Flag.DAMAGE);
					final @Nullable Flag.Value<?> fallDamageValue = region.getFlag(Flag.FALL_DAMAGE);

					if (damageValue != null || fallDamageValue != null) {
						ignore = false;
					}

					if (damageValue == DAMAGE.DENY || fallDamageValue == FALL_DAMAGE.DENY) {
						event.setCancelled(true);
						return;
					}
				}

				if (ignore && (globalRegion.getFlag(Flag.DAMAGE) == DAMAGE.DENY || globalRegion.getFlag(Flag.FALL_DAMAGE) == FALL_DAMAGE.DENY)) {
					event.setCancelled(true);
				}
			} else {
				boolean ignore = true;

				for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(event.getEntity().getLocation())) {
					final @Nullable Flag.Value<?> damageValue = region.getFlag(Flag.DAMAGE);

					if (damageValue != null) {
						ignore = false;
					}

					if (damageValue == DAMAGE.DENY) {
						event.setCancelled(true);
						return;
					}
				}

				if (ignore && globalRegion.getFlag(Flag.DAMAGE) == DAMAGE.DENY) {
					event.setCancelled(true);
				}
			}
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
				for (final @NotNull DefinedRegion region : RegionManager.getApplicableRegions(location)) {
					region.setTag(Tag.CHANGED, CHANGED.TRUE);
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}
}