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
				de.zeanon.testutils.plugin.commands.region.Region.sendMultipleRegions(regions, p);
			}
		} else {
			if (RegionManager.isGlobalRegion(regionName.getName())) {
				Info.sendRegionInfo(RegionManager.getGlobalRegion(p.getWorld()), p);
			} else {
				final @Nullable DefinedRegion region = RegionManager.getRegion(regionName.getName());
				if (region != null) {
					Info.sendRegionInfo(region, p);
				} else {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "The given region does not exist.");
				}
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
					  + ChatColor.RED + "Type: "
					  + ChatColor.DARK_RED + region.getType());


		final @NotNull StringBuilder flags = new StringBuilder(GlobalMessageUtils.messageHead);
		flags.append(ChatColor.RED).append("Flags:").append(ChatColor.BLACK).append(" | ");
		region.getFlags().forEach((flag, value) ->
										  flags.append(ChatColor.DARK_RED)
											   .append(flag.toString())
											   .append(ChatColor.DARK_GRAY)
											   .append(" : ")
											   .append(value.getChatValue())
											   .append(ChatColor.BLACK)
											   .append(" | "));
		p.sendMessage(flags.toString());
	}
}