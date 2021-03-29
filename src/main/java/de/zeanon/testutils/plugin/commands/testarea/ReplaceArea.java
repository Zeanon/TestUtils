package de.zeanon.testutils.plugin.commands.testarea;

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
import de.zeanon.testutils.plugin.utils.region.Region;
import java.util.HashSet;
import java.util.Set;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class ReplaceArea {

	public void execute(final @NotNull String[] args, final @NotNull Player p, final boolean toTNT) {
		if (args.length == 1) {
			ReplaceArea.replaceArea(p, TestAreaUtils.getRegion(p), TestAreaUtils.getOppositeRegion(p), "your", toTNT, null, null);
		} else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("-other")) {
				ReplaceArea.replaceArea(p, TestAreaUtils.getOppositeRegion(p), TestAreaUtils.getRegion(p), "your", toTNT, null, null);
			} else if (args[1].equalsIgnoreCase("-n") || args[1].equalsIgnoreCase("-north")) {
				ReplaceArea.replaceArea(p, TestAreaUtils.getNorthRegion(p), TestAreaUtils.getSouthRegion(p), "the north", toTNT, null, null);
			} else if (args[1].equalsIgnoreCase("-s") || args[1].equalsIgnoreCase("-south")) {
				ReplaceArea.replaceArea(p, TestAreaUtils.getSouthRegion(p), TestAreaUtils.getNorthRegion(p), "the south", toTNT, null, null);
			} else {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Too many arguments.");
			}
		} else if (args.length == 3) {
			ReplaceArea.replaceArea(p, TestAreaUtils.getRegion(p), TestAreaUtils.getOppositeRegion(p), "your", toTNT, args[1], args[2]);
		} else if (args.length == 4) {
			if (args[3].equalsIgnoreCase("-other")) {
				ReplaceArea.replaceArea(p, TestAreaUtils.getOppositeRegion(p), TestAreaUtils.getRegion(p), "your", toTNT, args[1], args[2]);
			} else if (args[3].equalsIgnoreCase("-n") || args[3].equalsIgnoreCase("-north")) {
				ReplaceArea.replaceArea(p, TestAreaUtils.getNorthRegion(p), TestAreaUtils.getSouthRegion(p), "the north", toTNT, args[1], args[2]);
			} else if (args[3].equalsIgnoreCase("-s") || args[3].equalsIgnoreCase("-south")) {
				ReplaceArea.replaceArea(p, TestAreaUtils.getSouthRegion(p), TestAreaUtils.getNorthRegion(p), "the south", toTNT, args[1], args[2]);
			} else if (args[1].equalsIgnoreCase("-other")) {
				ReplaceArea.replaceArea(p, TestAreaUtils.getOppositeRegion(p), TestAreaUtils.getRegion(p), "your", toTNT, args[2], args[3]);
			} else if (args[1].equalsIgnoreCase("-n") || args[1].equalsIgnoreCase("-north")) {
				ReplaceArea.replaceArea(p, TestAreaUtils.getNorthRegion(p), TestAreaUtils.getSouthRegion(p), "the north", toTNT, args[2], args[3]);
			} else if (args[1].equalsIgnoreCase("-s") || args[1].equalsIgnoreCase("-south")) {
				ReplaceArea.replaceArea(p, TestAreaUtils.getSouthRegion(p), TestAreaUtils.getNorthRegion(p), "the south", toTNT, args[2], args[3]);
			}
		} else {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Too many arguments.");
		}
	}

	private void replaceArea(final @NotNull Player p,
							 final @Nullable Region tempRegion,
							 final @Nullable Region otherRegion,
							 final @NotNull String area,
							 final boolean toTNT,
							 final @Nullable String source,
							 final @Nullable String destination) {
		try (final @NotNull EditSession editSession = SessionFactory.createSession(p)) {
			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
			} else {
				final @NotNull World tempWorld = new BukkitWorld(p.getWorld());
				final @NotNull CuboidRegion region = new CuboidRegion(tempWorld, tempRegion.getMinimumPoint().toBlockVector3(), tempRegion.getMaximumPoint().toBlockVector3());

				if (source != null) {
					final @NotNull Set<BaseBlock> sourceBlocks = new HashSet<>();
					try {
						sourceBlocks.add(Objects.notNull(BlockTypes.get(source)).getDefaultState().toBaseBlock());
					} catch (ObjectNullException e) {
						p.sendMessage(GlobalMessageUtils.messageHead
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
						editSession.replaceBlocks(region, sourceBlocks, Objects.notNull(BlockTypes.get(Objects.notNull(destination))).getDefaultState().toBaseBlock());
					} catch (ObjectNullException e) {
						p.sendMessage(GlobalMessageUtils.messageHead
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
				} else {
					if (toTNT) {
						final @NotNull Set<BaseBlock> obsidian = new HashSet<>();
						obsidian.add(Objects.notNull(BlockTypes.OBSIDIAN).getDefaultState().toBaseBlock());
						editSession.replaceBlocks(region, obsidian, Objects.notNull(BlockTypes.TNT).getDefaultState().toBaseBlock());
					} else {
						final @NotNull Set<BaseBlock> tnt = new HashSet<>();
						tnt.add(Objects.notNull(BlockTypes.TNT).getDefaultState().toBaseBlock());
						editSession.replaceBlocks(region, tnt, Objects.notNull(BlockTypes.OBSIDIAN).getDefaultState().toBaseBlock());
					}
				}

				for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
					if (tempPlayer == p) {
						tempPlayer.sendMessage(GlobalMessageUtils.messageHead
											   + ChatColor.RED
											   + "The "
											   + ChatColor.DARK_RED
											   + (source == null ? (toTNT ? "Obsidian" : "TNT") : source)
											   + ChatColor.RED
											   + " on "
											   + area
											   + " side has been replaced to '"
											   + ChatColor.DARK_RED
											   + (destination == null ? (toTNT ? "TNT" : "Obsidian") : destination)
											   + ChatColor.RED
											   + "'.");
					} else {
						if (tempRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(GlobalMessageUtils.messageHead
												   + ChatColor.RED
												   + "The "
												   + ChatColor.DARK_RED
												   + (source == null ? (toTNT ? "Obsidian" : "TNT") : source)
												   + ChatColor.RED
												   + " on "
												   + "your"
												   + " side has been replaced to '"
												   + ChatColor.DARK_RED
												   + (destination == null ? (toTNT ? "TNT" : "Obsidian") : destination)
												   + ChatColor.RED
												   + "'.");
						} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(GlobalMessageUtils.messageHead
												   + ChatColor.RED
												   + "The "
												   + ChatColor.DARK_RED
												   + (source == null ? (toTNT ? "Obsidian" : "TNT") : source)
												   + ChatColor.RED
												   + " on "
												   + "the other"
												   + " side has been replaced to '"
												   + ChatColor.DARK_RED
												   + (destination == null ? (toTNT ? "TNT" : "Obsidian") : destination)
												   + ChatColor.RED
												   + "'.");
						}
					}
				}
			}
		} catch (WorldEditException e) {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED
						  + "There has been an error, replacing the "
						  + ChatColor.DARK_RED
						  + (toTNT ? "Obsidian" : "TNT")
						  + ChatColor.RED
						  + " on "
						  + area
						  + " side.");
			e.printStackTrace();
		}
	}
}