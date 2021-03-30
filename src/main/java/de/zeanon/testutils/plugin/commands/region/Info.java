package de.zeanon.testutils.plugin.commands.region;


import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.enums.RegionName;
import de.zeanon.testutils.plugin.utils.region.DefinedRegion;
import de.zeanon.testutils.plugin.utils.region.Region;
import de.zeanon.testutils.plugin.utils.region.RegionManager;
import java.util.List;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Info {

	public void execute(final @Nullable RegionName regionName, final @NotNull Player p) {
		if (regionName == null) {
			final @NotNull List<DefinedRegion> regions = RegionManager.getApplicableRegions(p.getLocation());
			if (regions.isEmpty()) {
				Info.sendRegionInfo(RegionManager.getGlobalRegion(p.getWorld()), p);
			} else if (regions.size() == 1) {
				Info.sendRegionInfo(regions.get(0), p);
			} else {
				Info.sendMultipleRegions(regions, p);
			}
		}
	}

	private void sendRegionInfo(final @NotNull Region region, final @NotNull Player p) {
		p.sendMessage(GlobalMessageUtils.messageHead
					  + ChatColor.RED + "=== Infos for '" + ChatColor.DARK_RED + region.getName() + ChatColor.RED + "' === " + GlobalMessageUtils.messageHead);

		p.sendMessage(GlobalMessageUtils.messageHead
					  + ChatColor.RED + "World: "
					  + ChatColor.DARK_RED + region.getWorld().getName());

		p.sendMessage(GlobalMessageUtils.messageHead
					  + ChatColor.RED + "Flags: "
					  + ChatColor.DARK_RED + "TNT"
					  + ChatColor.RED + " : "
					  + (region.tnt() ? ChatColor.GREEN + "allow" : ChatColor.RED + "deny")
					  + ChatColor.BLACK + " | "
					  + ChatColor.DARK_RED + "Stoplag"
					  + ChatColor.RED + " : "
					  + (region.stoplag() ? ChatColor.GREEN + "active" : ChatColor.RED + "inactive")
					  + ChatColor.BLACK + " | "
					  + ChatColor.DARK_RED + "Fire"
					  + ChatColor.RED + " : "
					  + (region.fire() ? ChatColor.GREEN + "allow" : ChatColor.RED + "deny")
					  + ChatColor.BLACK + " | "
					  + ChatColor.DARK_RED + "Itemdrops"
					  + ChatColor.RED + " : "
					  + (region.itemDrops() ? ChatColor.GREEN + "allow" : ChatColor.RED + "deny"));
	}

	private void sendMultipleRegions(final @NotNull List<DefinedRegion> regions, final @NotNull Player p) {
		p.sendMessage(GlobalMessageUtils.messageHead
					  + ChatColor.RED + "You are standing in multiple regions: "
					  + ChatColor.DARK_RED + regions.toString());
	}
}