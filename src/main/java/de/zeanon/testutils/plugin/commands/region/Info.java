package de.zeanon.testutils.plugin.commands.region;


import de.zeanon.testutils.plugin.utils.enums.RegionName;
import de.zeanon.testutils.regionsystem.region.Region;
import de.zeanon.testutils.regionsystem.region.RegionManager;
import de.zeanon.testutils.regionsystem.region.TestArea;
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
			final @NotNull List<TestArea> regions = RegionManager.getApplicableRegions(p.getLocation());
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
				final @Nullable TestArea region = RegionManager.getRegion(regionName.getName());
				if (region != null) {
					Info.sendRegionInfo(region, p);
				} else {
					p.sendMessage(de.zeanon.testutils.plugin.commands.region.Region.MESSAGE_HEAD
								  + ChatColor.RED + "The given region does not exist.");
				}
			}
		}
	}

	private void sendRegionInfo(final @NotNull Region region, final @NotNull Player p) {
		p.sendMessage("\n"
					  + de.zeanon.testutils.plugin.commands.region.Region.MESSAGE_HEAD
					  + ChatColor.RED + "=== " + ChatColor.DARK_RED + region.getName() + ChatColor.RED + " === " + de.zeanon.testutils.plugin.commands.region.Region.MESSAGE_HEAD);

		p.sendMessage(de.zeanon.testutils.plugin.commands.region.Region.MESSAGE_HEAD
					  + ChatColor.RED + "World: "
					  + ChatColor.DARK_RED + region.getWorld().getName());

		p.sendMessage(de.zeanon.testutils.plugin.commands.region.Region.MESSAGE_HEAD
					  + ChatColor.RED + "Type: "
					  + ChatColor.DARK_RED + region.getType());


		final @NotNull StringBuilder flags = new StringBuilder();
		final boolean[] lineBreak = {false};
		region.getFlags().forEach((flag, value) -> {
			if (!lineBreak[0]) {
				flags.append(de.zeanon.testutils.plugin.commands.region.Region.MESSAGE_HEAD)
					 .append(ChatColor.RED)
					 .append("Flags: ")
					 .append(ChatColor.BLACK)
					 .append("[");
			}

			flags.append(ChatColor.DARK_RED)
				 .append(flag.toString())
				 .append(ChatColor.DARK_GRAY)
				 .append(" : ")
				 .append(value.getChatValue());

			if (lineBreak[0]) {
				flags.append(ChatColor.BLACK)
					 .append("]\n");
			} else {
				flags.append(ChatColor.BLACK)
					 .append(" | ");
			}

			lineBreak[0] = !lineBreak[0];
		});
		p.sendMessage(lineBreak[0] ? (flags.substring(0, flags.length() - 5) + ChatColor.BLACK + "]") : flags.toString());
	}
}