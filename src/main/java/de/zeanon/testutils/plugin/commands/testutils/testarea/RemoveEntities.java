package de.zeanon.testutils.plugin.commands.testutils.testarea;

import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import de.zeanon.testutils.regionsystem.RegionManager;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import de.zeanon.testutils.regionsystem.region.GlobalRegion;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class RemoveEntities {

	public void execute(final @NotNull Player p, final @NotNull RegionSide regionSide, final boolean global) {
		if (global) {
			final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(p.getWorld());
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "You have removed "
						  + ChatColor.DARK_RED + de.zeanon.testutils.regionsystem.utils.RemoveEntities.removeEntities(globalRegion)
						  + ChatColor.RED + " entities globally.");
			return;
		}

		if (regionSide == RegionSide.NONE) {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p);
			final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);
			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "To remove entities globally, type '/tu removeentities -global'.");
				return;
			}

			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "You have removed "
						  + ChatColor.DARK_RED + de.zeanon.testutils.regionsystem.utils.RemoveEntities.removeEntities(tempRegion, otherRegion)
						  + ChatColor.RED + " entities in your TestArea.");
		} else {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p, regionSide);
			final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p, regionSide);
			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "To remove entities globally, type '/tu removeentities -global'.");
				return;
			}
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "You have removed "
						  + ChatColor.DARK_RED + de.zeanon.testutils.regionsystem.utils.RemoveEntities.removeEntities(tempRegion)
						  + ChatColor.RED + " entities on the " + regionSide.getName() + " of your TestArea.");
			de.zeanon.testutils.regionsystem.utils.RemoveEntities.removeEntities(tempRegion);
		}
	}
}