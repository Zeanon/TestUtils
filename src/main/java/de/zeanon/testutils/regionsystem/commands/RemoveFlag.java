package de.zeanon.testutils.regionsystem.commands;

import de.zeanon.testutils.plugin.utils.enums.RegionName;
import de.zeanon.testutils.regionsystem.RegionManager;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import de.zeanon.testutils.regionsystem.region.GlobalRegion;
import java.util.List;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class RemoveFlag {

	public void execute(final @Nullable RegionName regionName, final @NotNull de.zeanon.testutils.regionsystem.flags.Flag flag, final @NotNull Player p) {
		if (regionName == null) {
			final @NotNull List<DefinedRegion> regions = RegionManager.getApplicableRegions(p.getLocation()); //NOSONAR
			if (regions.isEmpty()) {
				final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(p.getWorld()); //NOSONAR
				globalRegion.removeFlag(flag);
				RemoveFlag.sendFlagRemoved(globalRegion.getName(), flag.toString(), p);
			} else if (regions.size() == 1) {
				regions.get(0).removeFlag(flag);
				RemoveFlag.sendFlagRemoved(regions.get(0).getName(), flag.toString(), p);
			} else {
				RegionCommand.sendMultipleRegions(regions, p);
			}
		} else {
			if (RegionManager.isGlobalRegion(regionName.getName())) {
				RegionManager.getGlobalRegion(p.getWorld()).removeFlag(flag);
				RemoveFlag.sendFlagRemoved(regionName.getName(), flag.toString(), p);
			} else {
				final @Nullable DefinedRegion region = RegionManager.getDefinedRegion(regionName.getName()); //NOSONAR
				if (region != null) {
					region.removeFlag(flag);
					RemoveFlag.sendFlagRemoved(regionName.getName(), flag.toString(), p);
				} else {
					p.sendMessage(RegionCommand.MESSAGE_HEAD
								  + ChatColor.RED + "The given region does not exist.");
				}
			}
		}
	}

	private void sendFlagRemoved(final @NotNull String regionName, final @NotNull String flag, final @NotNull Player p) {
		p.sendMessage(RegionCommand.MESSAGE_HEAD
					  + ChatColor.RED + "You have deleted the flag '"
					  + ChatColor.DARK_RED + flag
					  + ChatColor.RED + "' in '"
					  + ChatColor.DARK_RED + regionName
					  + ChatColor.RED + "'.");
	}

	@SuppressWarnings("unused")
	private void sendFlagRemoveFailed(final @NotNull String flag, final @NotNull Player p) {
		p.sendMessage(RegionCommand.MESSAGE_HEAD
					  + ChatColor.RED + "The flag '"
					  + ChatColor.DARK_RED + flag
					  + ChatColor.RED + "' is not valid.");
	}
}