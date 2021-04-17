package de.zeanon.testutils.regionsystem.commands;

import de.zeanon.testutils.plugin.utils.enums.RegionName;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import de.zeanon.testutils.regionsystem.region.GlobalRegion;
import de.zeanon.testutils.regionsystem.region.RegionManager;
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

	public void execute(final @Nullable RegionName regionName, final @NotNull de.zeanon.testutils.regionsystem.flags.Flag flag, final @Nullable de.zeanon.testutils.regionsystem.flags.Flag.Value<?> value, final @NotNull Player p) {
		if (value == null) {
			p.sendMessage(RegionCommand.MESSAGE_HEAD
						  + ChatColor.RED
						  + "Applicable values for '"
						  + ChatColor.DARK_RED
						  + flag.toString()
						  + ChatColor.RED
						  + "' are: "
						  + Arrays.stream(flag.getValues())
								  .map(v -> ChatColor.DARK_RED + v.getName())
								  .collect(Collectors.joining(ChatColor.RED + ", "))
						  + ".");
		} else {
			if (regionName == null) {
				final @NotNull List<DefinedRegion> regions = RegionManager.getApplicableRegions(p.getLocation()); //NOSONAR
				if (regions.isEmpty()) {
					final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(p.getWorld()); //NOSONAR
					globalRegion.set(flag, value);
					Flag.sendFlagSet(globalRegion.getName(), flag.toString(), value.getChatValue(), p);
				} else if (regions.size() == 1) {
					regions.get(0).set(flag, value);
					Flag.sendFlagSet(regions.get(0).getName(), flag.toString(), value.getChatValue(), p);
				} else {
					RegionCommand.sendMultipleRegions(regions, p);
				}
			} else {
				if (RegionManager.isGlobalRegion(regionName.getName())) {
					RegionManager.getGlobalRegion(p.getWorld()).set(flag, value);
					Flag.sendFlagSet(regionName.getName(), flag.toString(), value.getChatValue(), p);
				} else {
					final @Nullable DefinedRegion region = RegionManager.getRegion(regionName.getName()); //NOSONAR
					if (region != null) {
						region.set(flag, value);
						Flag.sendFlagSet(regionName.getName(), flag.toString(), value.getChatValue(), p);
					} else {
						p.sendMessage(RegionCommand.MESSAGE_HEAD
									  + ChatColor.RED + "The given region does not exist.");
					}
				}
			}
		}
	}

	private void sendFlagSet(final @NotNull String regionName, final @NotNull String flag, final @NotNull String value, final @NotNull Player p) {
		p.sendMessage(RegionCommand.MESSAGE_HEAD
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
		p.sendMessage(RegionCommand.MESSAGE_HEAD
					  + ChatColor.RED + "The flag '"
					  + ChatColor.DARK_RED + flag
					  + ChatColor.RED + "' is not valid.");
	}
}