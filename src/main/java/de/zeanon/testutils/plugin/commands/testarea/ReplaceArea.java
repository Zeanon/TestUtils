package de.zeanon.testutils.plugin.commands.testarea;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import java.util.HashSet;
import java.util.Set;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class ReplaceArea {

	public void execute(final @NotNull String[] args, final @NotNull Player p, final boolean toTNT) {
		if (args.length == 1) {
			ReplaceArea.replaceArea(p, TestAreaUtils.getRegion(p), "your", toTNT);
		} else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("-other")) {
				ReplaceArea.replaceArea(p, TestAreaUtils.getOppositeRegion(p), "the other", toTNT);
			} else if (args[1].equalsIgnoreCase("-n") || args[1].equalsIgnoreCase("-north")) {
				ReplaceArea.replaceArea(p, TestAreaUtils.getNorthRegion(p), "the north", toTNT);
			} else if (args[1].equalsIgnoreCase("-s") || args[1].equalsIgnoreCase("-south")) {
				ReplaceArea.replaceArea(p, TestAreaUtils.getSouthRegion(p), "the south", toTNT);
			} else {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Too many arguments.");
			}
		} else {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Too many arguments.");
		}
	}

	private void replaceArea(final @NotNull Player p, final @Nullable ProtectedRegion tempRegion, final @NotNull String area, final boolean toTNT) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try (final @NotNull EditSession editSession = SessionFactory.createSession(p)) {
					if (tempRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						final @NotNull World tempWorld = new BukkitWorld(p.getWorld());
						final @NotNull CuboidRegion region = new CuboidRegion(tempWorld, tempRegion.getMinimumPoint(), tempRegion.getMaximumPoint());

						if (toTNT) {
							final @NotNull Set<BaseBlock> obsidian = new HashSet<>();
							obsidian.add(Objects.notNull(BlockTypes.OBSIDIAN).getDefaultState().toBaseBlock());
							editSession.replaceBlocks(region, obsidian, Objects.notNull(BlockTypes.TNT).getDefaultState().toBaseBlock());
						} else {
							final @NotNull Set<BaseBlock> tnt = new HashSet<>();
							tnt.add(Objects.notNull(BlockTypes.TNT).getDefaultState().toBaseBlock());
							editSession.replaceBlocks(region, tnt, Objects.notNull(BlockTypes.OBSIDIAN).getDefaultState().toBaseBlock());
						}

						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED
									  + "The "
									  + ChatColor.DARK_RED
									  + (toTNT ? "Obsidian" : "TNT")
									  + ChatColor.RED
									  + " on "
									  + area
									  + " side has been replaced.");
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
		}.runTask(TestUtils.getInstance());
	}
}
