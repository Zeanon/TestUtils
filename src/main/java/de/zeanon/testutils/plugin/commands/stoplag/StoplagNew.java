package de.zeanon.testutils.plugin.commands.stoplag;

import de.zeanon.testutils.commandframework.SWCommand;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.GlobalToggle;
import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import de.zeanon.testutils.plugin.utils.enums.StoplagToggle;
import de.zeanon.testutils.plugin.utils.region.DefinedRegion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class StoplagNew extends SWCommand {

	public StoplagNew() {
		super("stoplag");
	}

	@Register
	public void noArgs(final @NotNull Player p) {
		this.execute(p, RegionSide.NONE, true, false);
	}

	@Register
	public void oneArg(final @NotNull Player p, final @NotNull RegionSide regionSide) {
		this.execute(p, regionSide, true, false);
	}

	@Register
	public void oneArg(final @NotNull Player p, final @NotNull StoplagToggle stoplagToggle) {
		this.execute(p, RegionSide.NONE, stoplagToggle != StoplagToggle.C, false);
	}

	@Register
	public void oneArg(final @NotNull Player p, final @NotNull GlobalToggle globalToggle) {
		this.execute(p, RegionSide.NONE, false, globalToggle == GlobalToggle.GLOBAL);
	}

	@Register
	public void twoArgs(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull StoplagToggle stoplagToggle) {
		this.execute(p, regionSide, stoplagToggle != StoplagToggle.C, false);
	}

	@Register
	public void twoArgs(final @NotNull Player p, final @NotNull StoplagToggle stoplagToggle, final @NotNull RegionSide regionSide) {
		this.execute(p, regionSide, stoplagToggle != StoplagToggle.C, false);
	}

	@Register
	public void twoArgs(final @NotNull Player p, final @NotNull GlobalToggle globalToggle, final @NotNull StoplagToggle stoplagToggle) {
		this.execute(p, RegionSide.NONE, stoplagToggle != StoplagToggle.C, globalToggle == GlobalToggle.GLOBAL);
	}

	@Register
	public void twoArgs(final @NotNull Player p, final @NotNull StoplagToggle stoplagToggle, final @NotNull GlobalToggle globalToggle) {
		this.execute(p, RegionSide.NONE, stoplagToggle != StoplagToggle.C, globalToggle == GlobalToggle.GLOBAL);
	}

	private static @NotNull String getNowActivated(final @NotNull String area) {
		return GlobalMessageUtils.messageHead
			   + org.bukkit.ChatColor.RED + "Stoplag has been " + net.md_5.bungee.api.ChatColor.GREEN + "activated" + net.md_5.bungee.api.ChatColor.RED + " on the " + area + " side of your TestArea.";
	}

	private static @NotNull String getNowDeactivated(final @NotNull String area) {
		return GlobalMessageUtils.messageHead
			   + org.bukkit.ChatColor.RED + "Stoplag has been deactivated on the " + area + " side of your TestArea.";
	}

	private void execute(final @NotNull Player p, final @NotNull RegionSide regionSide, final boolean activate, final boolean global) {
		if (regionSide == RegionSide.NONE) {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p);
			final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);
			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "To activate stoplag globally, type '/stoplag -global'.");
			} else {
				tempRegion.setStoplag(activate);
				otherRegion.setStoplag(activate);
				for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
					if ((tempRegion.inRegion(tempPlayer.getLocation())
						 || otherRegion.inRegion(tempPlayer.getLocation()))) {
						tempPlayer.sendMessage(GlobalMessageUtils.messageHead
											   + ChatColor.RED + "Stoplag has been " + ChatColor.GREEN + "activated" + ChatColor.RED + " in your TestArea.");
					}
				}
			}
		} else {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p, regionSide);
			final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p, regionSide);
			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "To activate stoplag globally, type '/stoplag -global'.");
			} else {
				tempRegion.setStoplag(activate);
				otherRegion.setStoplag(activate);
				for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
					if (tempPlayer == p) {
						tempPlayer.sendMessage(activate
											   ? StoplagNew.getNowActivated(regionSide.getName())
											   : StoplagNew.getNowDeactivated(regionSide.getName()));
					} else {
						if (tempRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(activate
												   ? StoplagNew.getNowActivated("your")
												   : StoplagNew.getNowDeactivated("your"));
						} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(activate
												   ? StoplagNew.getNowActivated("the other")
												   : StoplagNew.getNowDeactivated("the other"));
						}
					}
				}
			}
		}
	}
}