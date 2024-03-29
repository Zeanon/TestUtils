package de.zeanon.testutils.plugin.commands.testutils.testarea;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class InvertArea {

	public void execute(final @NotNull Player p, final @NotNull RegionSide regionSide) {
		if (regionSide == RegionSide.NONE) {
			InvertArea.invertArea(p, TestAreaUtils.getOppositeRegion(p), "the other");
		} else {
			InvertArea.invertArea(p, TestAreaUtils.getRegion(p, regionSide), regionSide.getName());
		}
	}

	private void invertArea(final @NotNull Player p, final @Nullable DefinedRegion tempRegion, final @NotNull String area) {
		if (tempRegion == null) {
			GlobalMessageUtils.sendNotApplicableRegion(p);
			return;
		}

		try (final @NotNull EditSession editSession = SessionFactory.createSession(p, new BukkitWorld(p.getWorld()))) {
			final @NotNull World tempWorld = new BukkitWorld(p.getWorld());
			final @NotNull CuboidRegion region = new CuboidRegion(tempWorld, tempRegion.getMinimumPoint().toBlockVector3(), tempRegion.getMaximumPoint().toBlockVector3());

			final @NotNull Set<BaseBlock> airBlocks = new HashSet<>();
			airBlocks.add(Objects.notNull(BlockTypes.AIR).getDefaultState().toBaseBlock());
			editSession.replaceBlocks(region, airBlocks, Objects.notNull(BlockTypes.BLUE_STAINED_GLASS).getDefaultState().toBaseBlock());

			editSession.replaceBlocks(region, (Set<BaseBlock>) null, Objects.notNull(BlockTypes.AIR).getDefaultState().toBaseBlock());

			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "The testarea on " + area + " side has been inverted.");
		} catch (final @NotNull WorldEditException e) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "There has been an error, inverting the testarea on " + area + " side.");
			TestUtils.getChatLogger().log(Level.SEVERE, String.format("Error while inverting %s", tempRegion.getName()), e);
		}
	}
}