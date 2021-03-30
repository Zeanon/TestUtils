package de.zeanon.testutils.plugin.commands.region;

import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.enums.RegionName;
import de.zeanon.testutils.plugin.utils.region.RegionManager;
import java.util.Arrays;
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
			if (RegionManager.getApplicableRegions(p.getLocation()).size() > 1) {

			}
		}
	}
}