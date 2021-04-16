package de.zeanon.testutils.plugin.commands.testutils.testarea;

import de.zeanon.storagemanagercore.internal.utility.basic.SizedStack;
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.enums.AreaName;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import de.zeanon.testutils.regionsystem.region.RegionManager;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Warp {

	private final @NotNull Map<String, SizedStack<Location>> backLocations = new HashMap<>();

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

		Warp.registerBackLocation(p, p.getLocation());
		p.teleport(teleport);
		p.getWorld().playSound(teleport, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
		p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
					  + ChatColor.RED + "You have been teleported to '" + ChatColor.DARK_RED + name.getName() + ChatColor.RED + "'.");
	}

	public void back(final @NotNull Player p) {
		final @Nullable Location backLocation = Warp.getBackLocation(p);
		if (backLocation == null) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Nothing to teleport back to.");
			return;
		}

		p.teleport(backLocation);
		p.getWorld().playSound(backLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
		p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
					  + ChatColor.RED + "You have been teleported back.");
	}

	public @Nullable Location getBackLocation(final @NotNull Player p) {
		final @Nullable SizedStack<Location> tempStack = Warp.backLocations.get(p.getUniqueId().toString());
		if (tempStack != null && !tempStack.empty()) {
			return tempStack.pop();
		}
		return null;
	}

	private void registerBackLocation(final @NotNull Player p, final @NotNull Location location) {
		if (Warp.backLocations.containsKey(p.getUniqueId().toString())) {
			SizedStack<Location> tempStack = Warp.backLocations.get(p.getUniqueId().toString());
			if (tempStack.getMaxSize() != ConfigUtils.getInt("Max Back")) {
				tempStack.resize(ConfigUtils.getInt("Max Back"));
			}
		} else {
			Warp.backLocations.put(p.getUniqueId().toString(), new SizedStack<>(ConfigUtils.getInt("Max Back")));
		}
		Warp.backLocations.get(p.getUniqueId().toString()).push(location);
	}
}