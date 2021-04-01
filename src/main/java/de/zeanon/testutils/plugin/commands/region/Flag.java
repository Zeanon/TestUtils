package de.zeanon.testutils.plugin.commands.region;

import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.enums.RegionName;
import de.zeanon.testutils.plugin.utils.region.DefinedRegion;
import de.zeanon.testutils.plugin.utils.region.GlobalRegion;
import de.zeanon.testutils.plugin.utils.region.RegionManager;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Flag {

	public void execute(final @Nullable RegionName regionName, final @NotNull de.zeanon.testutils.plugin.utils.enums.Flag flag, final @Nullable de.zeanon.testutils.plugin.utils.enums.Flag.Value<?> value, final @NotNull Player p) {
		if (value == null) {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Applicable values for '" + ChatColor.DARK_RED + flag.toString() + ChatColor.RED + "' are '" + ChatColor.DARK_RED + "[" + Arrays.stream(flag.getValue().getEnumConstants()).map(e -> ((Enum<?>) e).name().toLowerCase()).collect(Collectors.joining(", ")) + "]" + ChatColor.RED + "'.");
		} else {
			if (regionName == null) {
				final @NotNull List<DefinedRegion> regions = RegionManager.getApplicableRegions(p.getLocation());
				if (regions.isEmpty()) {
					final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(p.getWorld());
					globalRegion.set(flag, value);
					Flag.sendFlagSet(globalRegion.getName(), flag.toString(), value.getChatValue(), p);
				} else if (regions.size() == 1) {
					regions.get(0).set(flag, value);
					Flag.sendFlagSet(regions.get(0).getName(), flag.toString(), value.getChatValue(), p);
				} else {
					de.zeanon.testutils.plugin.commands.region.Region.sendMultipleRegions(regions, p);
				}
			} else {
				if (RegionManager.isGlobalRegion(regionName.getName())) {
					RegionManager.getGlobalRegion(p.getWorld()).set(flag, value);
					Flag.sendFlagSet(regionName.getName(), flag.toString(), value.getChatValue(), p);
				} else {
					final @Nullable DefinedRegion region = RegionManager.getRegion(regionName.getName());
					if (region != null) {
						region.set(flag, value);
						Flag.sendFlagSet(regionName.getName(), flag.toString(), value.getChatValue(), p);
					} else {
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "The given region does not exist.");
					}
				}
			}
		}
	}

	private void sendFlagSet(final @NotNull String regionName, final @NotNull String flag, final @NotNull String value, final @NotNull Player p) {
		p.sendMessage(GlobalMessageUtils.messageHead
					  + ChatColor.RED + "You have set '"
					  + ChatColor.DARK_RED + flag
					  + ChatColor.RED + "' in '"
					  + ChatColor.DARK_RED + regionName
					  + ChatColor.RED + "' to '"
					  + ChatColor.DARK_RED + value
					  + ChatColor.RED + "'.");
	}

	@SuppressWarnings("unused")
	private void sendFlagSetFailed(final @NotNull String flag, final @NotNull Player p) {
		p.sendMessage(GlobalMessageUtils.messageHead
					  + ChatColor.RED + "The flag '"
					  + ChatColor.DARK_RED + flag
					  + ChatColor.RED + "' is not valid.");
	}
}