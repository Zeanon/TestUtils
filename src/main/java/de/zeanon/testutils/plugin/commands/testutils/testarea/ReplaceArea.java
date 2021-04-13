package de.zeanon.testutils.plugin.commands.testutils.testarea;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import de.zeanon.storagemanagercore.internal.base.exceptions.ObjectNullException;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.util.HashSet;
import java.util.Set;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class ReplaceArea {

	public void execute(final @Nullable RegionSide regionSide, final @NotNull Material source, final @NotNull Material destination, final @NotNull Player p) {
		ReplaceArea.replaceArea(p, TestAreaUtils.getRegion(p, regionSide), TestAreaUtils.getOppositeRegion(p, regionSide), regionSide == null ? "your" : regionSide.getName(), source, destination);
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

			final @NotNull Set<BaseBlock> sourceBlocks = new HashSet<>();
			try {
				sourceBlocks.add(Objects.notNull(BlockTypes.get(source.name().toLowerCase())).getDefaultState().toBaseBlock());
			} catch (ObjectNullException e) {
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
			}

			try {
				editSession.replaceBlocks(region, sourceBlocks, Objects.notNull(BlockTypes.get(Objects.notNull(destination).name().toLowerCase())).getDefaultState().toBaseBlock());
			} catch (ObjectNullException e) {
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
			}

			for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
				if (tempPlayer == p) {
					tempPlayer.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
										   + ChatColor.RED
										   + "The "
										   + ChatColor.DARK_RED
										   + source
										   + ChatColor.RED
										   + " on "
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
											   + "The "
											   + ChatColor.DARK_RED
											   + source
											   + ChatColor.RED
											   + " on "
											   + "your"
											   + " side has been replaced to '"
											   + ChatColor.DARK_RED
											   + destination
											   + ChatColor.RED
											   + "'.");
					} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
						tempPlayer.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
											   + ChatColor.RED
											   + "The "
											   + ChatColor.DARK_RED
											   + source
											   + ChatColor.RED
											   + " on "
											   + "the other"
											   + " side has been replaced to '"
											   + ChatColor.DARK_RED
											   + destination
											   + ChatColor.RED
											   + "'.");
					}
				}
			}
		} catch (WorldEditException e) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED
						  + "There has been an error, replacing the "
						  + ChatColor.DARK_RED
						  + "Obsidian"
						  + ChatColor.RED
						  + " on "
						  + area
						  + " side.");
			e.printStackTrace();
		}
	}
}