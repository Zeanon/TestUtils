package de.zeanon.testutils.plugin.commands.testutils.testarea;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import de.zeanon.storagemanagercore.internal.base.exceptions.ObjectNullException;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.util.logging.Level;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class ReplaceArea {

	public void execute(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull Material source, final @NotNull Material destination) {
		ReplaceArea.replaceArea(p,
								TestAreaUtils.getRegion(p, regionSide),
								TestAreaUtils.getOppositeRegion(p, regionSide),
								regionSide == RegionSide.NONE ? "your" : regionSide.getName(),
								source,
								destination);

		final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p, regionSide);
		final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p, regionSide);

		if (tempRegion == null || otherRegion == null) {
			GlobalMessageUtils.sendNotApplicableRegion(p);
			return;
		}

		final @NotNull World bukkitWorld = new BukkitWorld(p.getWorld());
		try (final @NotNull EditSession editSession = SessionFactory.createSession(p, bukkitWorld)) {
			final @NotNull BlockType sourceBlock;
			try {
				sourceBlock = Objects.notNull(BlockTypes.get(source.name().toLowerCase()));
			} catch (final @NotNull ObjectNullException e) {
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED
							  + "There has been an error, replacing '"
							  + ChatColor.DARK_RED
							  + source
							  + ChatColor.RED
							  + " on "
							  + (regionSide == RegionSide.NONE ? "your" : regionSide.getName())
							  + " side due to '"
							  + ChatColor.DARK_RED
							  + source
							  + ChatColor.RED
							  + "' not being a valid block.");
				return;
			}

			final @NotNull CuboidRegion cuboidRegion = new CuboidRegion(bukkitWorld, tempRegion.getMinimumPoint().toBlockVector3(), tempRegion.getMaximumPoint().toBlockVector3());

			try {
				editSession.replaceBlocks(cuboidRegion, new BlockTypeMask(editSession, sourceBlock), Objects.notNull(BlockTypes.get(Objects.notNull(destination).name().toLowerCase())).getDefaultState().toBaseBlock());
			} catch (final @NotNull ObjectNullException e) {
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED
							  + "There has been an error, replacing to '"
							  + ChatColor.DARK_RED
							  + destination
							  + ChatColor.RED
							  + " on "
							  + (regionSide == RegionSide.NONE ? "your" : regionSide.getName())
							  + " side due to '"
							  + ChatColor.DARK_RED
							  + destination
							  + ChatColor.RED
							  + "' not being a valid block.");
				return;
			}

			if (regionSide == RegionSide.NONE) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(TestUtils.getInstance(), () -> {
				}, 5);
				final @NotNull CuboidRegion otherCuboidRegion = new CuboidRegion(bukkitWorld, otherRegion.getMinimumPoint().toBlockVector3(), otherRegion.getMaximumPoint().toBlockVector3());
				try {
					editSession.replaceBlocks(otherCuboidRegion, new BlockTypeMask(editSession, sourceBlock), Objects.notNull(BlockTypes.get(Objects.notNull(destination).name().toLowerCase())).getDefaultState().toBaseBlock());
				} catch (final @NotNull ObjectNullException e) {
					p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
								  + ChatColor.RED
								  + "There has been an error, replacing to '"
								  + ChatColor.DARK_RED
								  + destination
								  + ChatColor.RED
								  + " in your area due to '"
								  + ChatColor.DARK_RED
								  + destination
								  + ChatColor.RED
								  + "' not being a valid block.");
					return;
				}

				for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
					tempPlayer.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
										   + ChatColor.RED
										   + "All "
										   + ChatColor.DARK_RED
										   + source
										   + ChatColor.RED
										   + " in your TestArea have been replaced to '"
										   + ChatColor.DARK_RED
										   + destination
										   + ChatColor.RED
										   + "'.");
				}
			} else {
				for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
					if (tempPlayer == p) {
						tempPlayer.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
											   + ChatColor.RED
											   + "All "
											   + ChatColor.DARK_RED
											   + source
											   + ChatColor.RED
											   + "'s on "
											   + regionSide.getName()
											   + " side has been replaced to '"
											   + ChatColor.DARK_RED
											   + destination
											   + ChatColor.RED
											   + "'.");
					} else {
						if (tempRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
												   + ChatColor.RED
												   + "All "
												   + ChatColor.DARK_RED
												   + source
												   + ChatColor.RED
												   + "'s on your side has been replaced to '"
												   + ChatColor.DARK_RED
												   + destination
												   + ChatColor.RED
												   + "'.");
						} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
												   + ChatColor.RED
												   + "All "
												   + ChatColor.DARK_RED
												   + source
												   + ChatColor.RED
												   + "'s on the other side has been replaced to '"
												   + ChatColor.DARK_RED
												   + destination
												   + ChatColor.RED
												   + "'.");
						}
					}
				}
			}
		} catch (final @NotNull WorldEditException e) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED
						  + "There has been an error, replacing the "
						  + ChatColor.DARK_RED
						  + source
						  + ChatColor.RED
						  + "'s on "
						  + (regionSide == RegionSide.NONE ? "your" : regionSide.getName())
						  + " side to '"
						  + ChatColor.DARK_RED
						  + destination
						  + ChatColor.RED
						  + "'.");
			TestUtils.getChatLogger().log(Level.SEVERE, String.format("Error while replacing %s to %s", source, destination), e);
		}
	}

	private void replaceArea(final @NotNull Player p,
							 final @Nullable DefinedRegion tempRegion,
							 final @Nullable DefinedRegion otherRegion,
							 final @NotNull String area,
							 final @NotNull Material source,
							 final @NotNull Material destination) {
		if (tempRegion == null || otherRegion == null) {
			GlobalMessageUtils.sendNotApplicableRegion(p);
			return;
		}

		final @NotNull World bukkitWorld = new BukkitWorld(p.getWorld());
		try (final @NotNull EditSession editSession = SessionFactory.createSession(p, bukkitWorld)) {
			final @NotNull CuboidRegion region = new CuboidRegion(bukkitWorld, tempRegion.getMinimumPoint().toBlockVector3(), tempRegion.getMaximumPoint().toBlockVector3());

			final @NotNull BlockType sourceBlock;
			try {
				sourceBlock = Objects.notNull(BlockTypes.get(source.name().toLowerCase()));
			} catch (final @NotNull ObjectNullException e) {
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED
							  + "There has been an error, replacing '"
							  + ChatColor.DARK_RED
							  + source
							  + ChatColor.RED
							  + " on "
							  + area
							  + " side due to '"
							  + ChatColor.DARK_RED
							  + source
							  + ChatColor.RED
							  + "' not being a valid block.");
				return;
			}

			try {
				editSession.replaceBlocks(region, new BlockTypeMask(editSession, sourceBlock), Objects.notNull(BlockTypes.get(Objects.notNull(destination).name().toLowerCase())).getDefaultState().toBaseBlock());
			} catch (final @NotNull ObjectNullException e) {
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED
							  + "There has been an error, replacing to '"
							  + ChatColor.DARK_RED
							  + destination
							  + ChatColor.RED
							  + " on "
							  + area
							  + " side due to '"
							  + ChatColor.DARK_RED
							  + destination
							  + ChatColor.RED
							  + "' not being a valid block.");
				return;
			}

			for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
				if (tempPlayer == p) {
					tempPlayer.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
										   + ChatColor.RED
										   + "All "
										   + ChatColor.DARK_RED
										   + source
										   + ChatColor.RED
										   + "'s on "
										   + area
										   + " side has been replaced to '"
										   + ChatColor.DARK_RED
										   + destination
										   + ChatColor.RED
										   + "'.");
				} else {
					if (tempRegion.inRegion(tempPlayer.getLocation())) {
						tempPlayer.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
											   + ChatColor.RED
											   + "All "
											   + ChatColor.DARK_RED
											   + source
											   + ChatColor.RED
											   + "'s on "
											   + "your"
											   + " side has been replaced to '"
											   + ChatColor.DARK_RED
											   + destination
											   + ChatColor.RED
											   + "'.");
					} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
						tempPlayer.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
											   + ChatColor.RED
											   + "All "
											   + ChatColor.DARK_RED
											   + source
											   + ChatColor.RED
											   + "'s on "
											   + "the other"
											   + " side has been replaced to '"
											   + ChatColor.DARK_RED
											   + destination
											   + ChatColor.RED
											   + "'.");
					}
				}
			}
		} catch (final @NotNull WorldEditException e) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED
						  + "There has been an error, replacing the "
						  + ChatColor.DARK_RED
						  + source
						  + ChatColor.RED
						  + "'s on "
						  + area
						  + " side to '"
						  + ChatColor.DARK_RED
						  + destination
						  + ChatColor.RED
						  + "'.");
			TestUtils.getChatLogger().log(Level.SEVERE, String.format("Error while replacing %s to %s", source, destination), e);
		}
	}
}