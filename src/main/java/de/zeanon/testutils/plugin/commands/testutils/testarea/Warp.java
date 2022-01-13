package de.zeanon.testutils.plugin.commands.testutils.testarea;

import de.zeanon.storagemanagercore.internal.utility.basic.SizedStack;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.AreaName;
import de.zeanon.testutils.regionsystem.RegionManager;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Warp {

	private final @NotNull Map<String, SizedStack<Location>> backLocations;

	static {
		backLocations = new HashMap<>();
	}

	public void execute(final @NotNull AreaName name, final @NotNull Player p) {
		if (TestAreaUtils.illegalName(name.getName())) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Area '" + name.getName() + "' resolution error: Name is not allowed.");
			return;
		}

		final @Nullable DefinedRegion definedRegion = RegionManager.getDefinedRegion(name + "_north");
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
		Warp.teleportPlayer(p, teleport, GlobalMessageUtils.MESSAGE_HEAD
										 + ChatColor.RED + "You have been teleported to '" + ChatColor.DARK_RED + name.getName() + ChatColor.RED + "'.");
	}

	public void back(final @NotNull Player p) {
		final @Nullable Location backLocation = Warp.getBackLocation(p);
		if (backLocation == null) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Nothing to teleport back to.");
			return;
		}

		Warp.teleportPlayer(p, backLocation, GlobalMessageUtils.MESSAGE_HEAD
											 + ChatColor.RED + "You have been teleported back.");
	}

	public @Nullable Location getBackLocation(final @NotNull Player p) {
		final @Nullable SizedStack<Location> tempStack = Warp.backLocations.get(p.getUniqueId().toString());
		if (tempStack != null && !tempStack.empty()) {
			return tempStack.pop();
		}
		return null;
	}

	public void teleportPlayer(final @NotNull Player p, final @NotNull Location location, final @NotNull String message) {
		p.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
		p.getWorld().playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
		p.sendMessage(message);
	}


	private void registerBackLocation(final @NotNull Player p, final @NotNull Location location) {
		if (Warp.backLocations.containsKey(p.getUniqueId().toString())) {
			final SizedStack<Location> tempStack = Warp.backLocations.get(p.getUniqueId().toString());
			if (tempStack.getMaxSize() != ConfigUtils.getInt("Max Back")) {
				tempStack.resize(ConfigUtils.getInt("Max Back"));
			}
		} else {
			Warp.backLocations.put(p.getUniqueId().toString(), new SizedStack<>(ConfigUtils.getInt("Max Back")));
		}
		Warp.backLocations.get(p.getUniqueId().toString()).push(location);
	}
}