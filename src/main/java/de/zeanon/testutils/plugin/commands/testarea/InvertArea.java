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
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import java.util.HashSet;
import java.util.Set;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class InvertArea {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length == 2) {
			if (args[1].equalsIgnoreCase("here")) {
				InvertArea.replaceArea(p, TestAreaUtils.getRegion(p), true);
			} else {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Too many arguments.");
			}
		} else if (args.length == 1) {
			InvertArea.replaceArea(p, TestAreaUtils.getOppositeRegion(p), false);
		} else {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Too many arguments.");
		}
	}

	private void replaceArea(final @NotNull Player p, final @Nullable ProtectedRegion tempRegion, final boolean here) {
		try (final @NotNull EditSession editSession = SessionFactory.createSession(p)) {
			if (tempRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
			} else {
				final @NotNull World tempWorld = new BukkitWorld(p.getWorld());
				final @NotNull CuboidRegion region = new CuboidRegion(tempWorld, tempRegion.getMinimumPoint(), tempRegion.getMaximumPoint());

				final @NotNull Set<BaseBlock> airBlocks = new HashSet<>();
				airBlocks.add(Objects.notNull(BlockTypes.AIR).getDefaultState().toBaseBlock());
				editSession.replaceBlocks(region, airBlocks, Objects.notNull(BlockTypes.RED_STAINED_GLASS).getDefaultState().toBaseBlock());

				editSession.replaceBlocks(region, (Set<BaseBlock>) null, Objects.notNull(BlockTypes.AIR).getDefaultState().toBaseBlock());

				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "The testarea on " + (here ? "your" : "the other") + " side has been inverted.");
			}
		} catch (WorldEditException e) {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "There has been an error, inverting the testarea on " + (here ? "your side." : "the other side."));
			e.printStackTrace();
		}
	}
}