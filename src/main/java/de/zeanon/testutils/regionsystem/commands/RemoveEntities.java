package de.zeanon.testutils.regionsystem.commands;

import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.enums.RegionName;
import de.zeanon.testutils.regionsystem.RegionManager;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import de.zeanon.testutils.regionsystem.region.Region;
import java.util.List;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class RemoveEntities {

	public void execute(final @Nullable RegionName regionName, final @NotNull Player p) {
		if (regionName == null) {
			final @NotNull List<DefinedRegion> regions = RegionManager.getApplicableRegions(p.getLocation());
			if (regions.isEmpty()) {
				RemoveEntities.sendRemovedEntities(RegionManager.getGlobalRegion(p.getWorld()), p);
			} else if (regions.size() == 1) {
				RemoveEntities.sendRemovedEntities(regions.get(0), p);
			} else {
				RegionCommand.sendMultipleRegions(regions, p);
			}
		} else {
			if (RegionManager.isGlobalRegion(regionName.getName())) {
				RemoveEntities.sendRemovedEntities(RegionManager.getGlobalRegion(p.getWorld()), p);
			} else {
				final @Nullable DefinedRegion region = RegionManager.getRegion(regionName.getName());
				if (region != null) {
					RemoveEntities.sendRemovedEntities(region, p);
				} else {
					p.sendMessage(RegionCommand.MESSAGE_HEAD
								  + ChatColor.RED + "The given region does not exist.");
				}
			}
		}
	}

	private void sendRemovedEntities(final @NotNull Region region, final @NotNull Player p) {
		p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
					  + ChatColor.RED + "You have removed '"
					  + ChatColor.DARK_RED + de.zeanon.testutils.regionsystem.utils.RemoveEntities.removeEntities(region)
					  + ChatColor.RED + "' entities in '"
					  + ChatColor.DARK_RED + region.getName()
					  + ChatColor.RED + "'.");
	}
}