package de.zeanon.testutils.plugin.commands.testarea;

import com.sk89q.worldedit.EditSession;
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
public class Count {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length == 1) {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Missing argument for "
						  + ChatColor.YELLOW + "<"
						  + ChatColor.DARK_RED + "block"
						  + ChatColor.YELLOW + ">");
		} else if (args.length == 2) {
			Count.count(p, TestAreaUtils.getRegion(p), "your", args[1]);
		} else if (args.length == 3) {
			if (args[2].equalsIgnoreCase("-other")) {
				Count.count(p, TestAreaUtils.getOppositeRegion(p), "the other", args[1]);
			} else if (args[2].equalsIgnoreCase("-n") || args[2].equalsIgnoreCase("-north")) {
				Count.count(p, TestAreaUtils.getNorthRegion(p), "the north", args[1]);
			} else if (args[2].equalsIgnoreCase("-s") || args[2].equalsIgnoreCase("-south")) {
				Count.count(p, TestAreaUtils.getSouthRegion(p), "the south", args[1]);
			} else if (args[1].equalsIgnoreCase("-other")) {
				Count.count(p, TestAreaUtils.getOppositeRegion(p), "the other", args[2]);
			} else if (args[1].equalsIgnoreCase("-n") || args[1].equalsIgnoreCase("-north")) {
				Count.count(p, TestAreaUtils.getNorthRegion(p), "the north", args[2]);
			} else if (args[1].equalsIgnoreCase("-s") || args[1].equalsIgnoreCase("-south")) {
				Count.count(p, TestAreaUtils.getSouthRegion(p), "the south", args[2]);
			} else {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Too many arguments.");
			}
		} else {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Too many arguments.");
		}
	}

	private void count(final @NotNull Player p, final @Nullable Region tempRegion, final @NotNull String area, final @NotNull String block) {
		try (final @NotNull EditSession editSession = SessionFactory.createSession(p)) {
			if (tempRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
			} else {
				final @NotNull World tempWorld = new BukkitWorld(p.getWorld());
				final @NotNull CuboidRegion region = new CuboidRegion(tempWorld, tempRegion.getMinimumPoint().toBlockVector3(), tempRegion.getMaximumPoint().toBlockVector3());

				final @NotNull Set<BaseBlock> blocks = new HashSet<>();
				blocks.add(Objects.notNull(BlockTypes.get(block)).getDefaultState().toBaseBlock());
				final @NotNull int amount = editSession.countBlocks(region, blocks);


				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED
							  + "The amount of '"
							  + ChatColor.DARK_RED
							  + block
							  + ChatColor.RED
							  + "' is "
							  + ChatColor.DARK_RED
							  + amount
							  + ChatColor.RED
							  + " on "
							  + area
							  + " side.");
			}
		} catch (ObjectNullException e) {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED
						  + "There has been an error, counting the amount of '"
						  + ChatColor.DARK_RED
						  + block
						  + ChatColor.RED
						  + " on "
						  + area
						  + " side due to '"
						  + ChatColor.DARK_RED
						  + block
						  + ChatColor.RED
						  + "' not being a valid block.");
		}
	}
}