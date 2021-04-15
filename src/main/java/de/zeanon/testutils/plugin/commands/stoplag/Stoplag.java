package de.zeanon.testutils.plugin.commands.stoplag;

import de.steamwar.commandframework.SWCommand;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.GlobalToggle;
import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import de.zeanon.testutils.plugin.utils.enums.RemoveEntities;
import de.zeanon.testutils.plugin.utils.enums.StoplagToggle;
import de.zeanon.testutils.regionsystem.flags.Flag;
import de.zeanon.testutils.regionsystem.flags.flagvalues.STOPLAG;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import de.zeanon.testutils.regionsystem.region.GlobalRegion;
import de.zeanon.testutils.regionsystem.region.Region;
import de.zeanon.testutils.regionsystem.region.RegionManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class Stoplag extends SWCommand {

	public Stoplag() {
		super("stoplag", true);
	}

	@Register
	public void noArgs(final @NotNull Player p) {
		this.execute(p, null, true, false, false);
	}

	@Register
	public void oneArg(final @NotNull Player p, final @NotNull RegionSide regionSide) {
		this.execute(p, regionSide, true, false, false);
	}

	@Register
	public void oneArg(final @NotNull Player p, final @NotNull StoplagToggle stoplagToggle) {
		this.execute(p, null, stoplagToggle != StoplagToggle.C, false, false);
	}

	@Register
	public void oneArg(final @NotNull Player p, final @NotNull GlobalToggle globalToggle) {
		this.execute(p, null, true, globalToggle == GlobalToggle.GLOBAL, false);
	}

	@Register
	public void oneArg(final @NotNull Player p, final @NotNull RemoveEntities removeEntities) {
		this.execute(p, null, true, false, removeEntities == RemoveEntities.REMOVEENTITIES);
	}

	@Register
	public void twoArgs(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull StoplagToggle stoplagToggle) {
		this.execute(p, regionSide, stoplagToggle != StoplagToggle.C, false, false);
	}

	@Register
	public void twoArgs(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull RemoveEntities removeEntities) {
		this.execute(p, regionSide, true, false, removeEntities == RemoveEntities.REMOVEENTITIES);
	}

	@Register
	public void twoArgs(final @NotNull Player p, final @NotNull StoplagToggle stoplagToggle, final @NotNull RegionSide regionSide) {
		this.execute(p, regionSide, stoplagToggle != StoplagToggle.C, false, false);
	}

	@Register
	public void twoArgs(final @NotNull Player p, final @NotNull RemoveEntities removeEntities, final @NotNull RegionSide regionSide) {
		this.execute(p, regionSide, true, false, removeEntities == RemoveEntities.REMOVEENTITIES);
	}

	@Register
	public void twoArgs(final @NotNull Player p, final @NotNull GlobalToggle globalToggle, final @NotNull StoplagToggle stoplagToggle) {
		this.execute(p, null, stoplagToggle != StoplagToggle.C, globalToggle == GlobalToggle.GLOBAL, false);
	}

	@Register
	public void twoArgs(final @NotNull Player p, final @NotNull GlobalToggle globalToggle, final @NotNull RemoveEntities removeEntities) {
		this.execute(p, null, true, globalToggle == GlobalToggle.GLOBAL, removeEntities == RemoveEntities.REMOVEENTITIES);
	}

	@Register
	public void twoArgs(final @NotNull Player p, final @NotNull StoplagToggle stoplagToggle, final @NotNull GlobalToggle globalToggle) {
		this.execute(p, null, stoplagToggle != StoplagToggle.C, globalToggle == GlobalToggle.GLOBAL, false);
	}

	@Register
	public void twoArgs(final @NotNull Player p, final @NotNull RemoveEntities removeEntities, final @NotNull GlobalToggle globalToggle) {
		this.execute(p, null, true, globalToggle == GlobalToggle.GLOBAL, removeEntities == RemoveEntities.REMOVEENTITIES);
	}


	private static @NotNull String getNowActivated(final @NotNull String area) {
		return GlobalMessageUtils.MESSAGE_HEAD
			   + org.bukkit.ChatColor.RED + "Stoplag has been " + ChatColor.GREEN + "activated" + net.md_5.bungee.api.ChatColor.RED + " on the " + area + " side of your TestArea.";
	}

	private static @NotNull String getNowDeactivated(final @NotNull String area) {
		return GlobalMessageUtils.MESSAGE_HEAD
			   + org.bukkit.ChatColor.RED + "Stoplag has been deactivated on the " + area + " side of your TestArea.";
	}

	private static void removeEntities(final @NotNull Region region) {
		for (final @NotNull Entity entity : region.getWorld().getEntities()) {
			if (region.inRegion(entity.getLocation())) {
				entity.remove();
			}
		}
	}

	private void execute(final @NotNull Player p, final @Nullable RegionSide regionSide, final boolean activate, final boolean global, final boolean removeEntities) {
		if (global) {
			final @NotNull GlobalRegion globalRegion = RegionManager.getGlobalRegion(p.getWorld());
			globalRegion.set(Flag.STOPLAG, activate ? STOPLAG.ACTIVE : STOPLAG.INACTIVE);
			if (removeEntities) {
				Stoplag.removeEntities(globalRegion);
			}
			for (final @NotNull Player player : p.getWorld().getPlayers()) {
				player.sendMessage(activate ? GlobalMessageUtils.MESSAGE_HEAD
											  + ChatColor.DARK_RED + p.getName()
											  + ChatColor.RED + " has activated Stoplag globally."
											: GlobalMessageUtils.MESSAGE_HEAD
											  + ChatColor.RED + "Stoplag has been deactivated.");
			}
			return;
		}

		if (regionSide == null) {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p);
			final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);
			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "To activate stoplag globally, type '/stoplag -global'.");
				return;
			}

			tempRegion.set(Flag.STOPLAG, activate ? STOPLAG.ACTIVE : STOPLAG.INACTIVE);
			otherRegion.set(Flag.STOPLAG, activate ? STOPLAG.ACTIVE : STOPLAG.INACTIVE);
			if (removeEntities) {
				Stoplag.removeEntities(tempRegion);
				Stoplag.removeEntities(otherRegion);
			}
			for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
				if ((tempRegion.inRegion(tempPlayer.getLocation())
					 || otherRegion.inRegion(tempPlayer.getLocation()))) {
					tempPlayer.sendMessage(activate ? GlobalMessageUtils.MESSAGE_HEAD
													  + ChatColor.RED + "Stoplag has been " + ChatColor.GREEN + "activated" + ChatColor.RED + " in your TestArea."
													: GlobalMessageUtils.MESSAGE_HEAD
													  + ChatColor.RED + "Stoplag has been deactivated in your TestArea.");
				}
			}
		} else {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p, regionSide);
			final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p, regionSide);
			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "To activate stoplag globally, type '/stoplag -global'.");
				return;
			}

			tempRegion.set(Flag.STOPLAG, activate ? STOPLAG.ACTIVE : STOPLAG.INACTIVE);
			if (removeEntities) {
				Stoplag.removeEntities(tempRegion);
			}
			for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
				if (tempPlayer == p) {
					tempPlayer.sendMessage(activate
										   ? Stoplag.getNowActivated(regionSide.getName())
										   : Stoplag.getNowDeactivated(regionSide.getName()));
				} else {
					if (tempRegion.inRegion(tempPlayer.getLocation())) {
						tempPlayer.sendMessage(activate
											   ? Stoplag.getNowActivated("your")
											   : Stoplag.getNowDeactivated("your"));
					} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
						tempPlayer.sendMessage(activate
											   ? Stoplag.getNowActivated("the other")
											   : Stoplag.getNowDeactivated("the other"));
					}
				}
			}
		}
	}
}