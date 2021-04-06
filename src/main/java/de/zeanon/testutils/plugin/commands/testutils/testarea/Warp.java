package de.zeanon.testutils.plugin.commands.testutils.testarea;

import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.enums.AreaName;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import de.zeanon.testutils.regionsystem.region.RegionManager;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Warp {

	public void execute(final @NotNull AreaName name, final @NotNull Player p) {
		if (name.getName().contains("./") || name.getName().contains(".\\") || InitMode.forbiddenFileName(name.getName())) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Area '" + name.getName() + "' resolution error: Name is not allowed.");
			return;
		}

		final @Nullable DefinedRegion definedRegion = RegionManager.getRegion(name + "_north");
		if (definedRegion == null) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "The given region does not exist.");
			return;
		}

		final @NotNull Location teleport = new Location(definedRegion.getWorld(),
														definedRegion.getMinimumPoint().getX() + 58.5,
														definedRegion.getMinimumPoint().getY(),
														definedRegion.getMaximumPoint().getZ() + 0.5,
														-180,
														0);

		p.teleport(teleport);
		p.getWorld().playSound(teleport, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
		p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
					  + ChatColor.RED + "You have been teleported to '" + ChatColor.DARK_RED + name.getName() + ChatColor.RED + "'.");
	}
}