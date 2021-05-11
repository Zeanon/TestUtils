package de.zeanon.testutils.plugin.commands.testutils.testarea;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import de.zeanon.storagemanagercore.internal.base.exceptions.ObjectNullException;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
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
public class Count {

	public void execute(final @NotNull Material material, final @Nullable RegionSide regionSide, final @NotNull Player p) {
		final @Nullable DefinedRegion region = TestAreaUtils.getRegion(p, regionSide);
		if (regionSide == null) {
			if (region == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
			} else {
				Count.count(p, region, "your", material);
			}
		} else {
			if (region == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
			} else {
				Count.count(p, region, regionSide.getName(), material);
			}
		}
	}

	private void count(final @NotNull Player p, final @NotNull DefinedRegion tempRegion, final @NotNull String area, final @NotNull Material material) {
		try (final @NotNull EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(tempRegion.getWorld()), -1)) {
			final @NotNull World tempWorld = new BukkitWorld(p.getWorld());
			final @NotNull CuboidRegion region = new CuboidRegion(tempWorld, tempRegion.getMinimumPoint().toBlockVector3(), tempRegion.getMaximumPoint().toBlockVector3());

			final @NotNull Set<BaseBlock> blocks = new HashSet<>();
			blocks.add(Objects.notNull(BlockTypes.get(material.name().toLowerCase())).getDefaultState().toBaseBlock());
			final int amount = editSession.countBlocks(region, blocks);


			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED
						  + "The amount of '"
						  + ChatColor.DARK_RED
						  + material.name().toLowerCase()
						  + ChatColor.RED
						  + "' is '"
						  + ChatColor.DARK_RED
						  + amount
						  + ChatColor.RED
						  + "' on "
						  + area
						  + " side.");
		} catch (final @NotNull ObjectNullException e) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED
						  + "There has been an error, counting the amount of '"
						  + ChatColor.DARK_RED
						  + material.name().toLowerCase()
						  + ChatColor.RED
						  + "' on "
						  + area
						  + " side due to '"
						  + ChatColor.DARK_RED
						  + material.name().toLowerCase()
						  + ChatColor.RED
						  + "' not being a valid block.");
		}
	}
}