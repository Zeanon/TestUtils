package de.zeanon.testutils.plugin.commands.stoplag;

import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.region.DefinedRegion;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Stoplag {


	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length == 1) {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p);
			final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);
			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "To activate stoplag globally, type '/stoplag global'.");
			} else {
				tempRegion.setStoplag(true);
				otherRegion.setStoplag(true);
				for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
					if ((tempRegion.inRegion(tempPlayer.getLocation())
						 || otherRegion.inRegion(tempPlayer.getLocation()))) {
						tempPlayer.sendMessage(GlobalMessageUtils.messageHead
											   + ChatColor.RED + "Stoplag has been " + ChatColor.GREEN + "activated" + ChatColor.RED + " in your TestArea.");
					}
				}
			}
		} else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("-c")) {
				final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p);
				final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);

				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "To deactivate stoplag globally, type '/stoplag global'.");
				} else {
					tempRegion.setStoplag(false);
					otherRegion.setStoplag(false);
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if ((tempRegion.inRegion(tempPlayer.getLocation())
							 || otherRegion.inRegion(tempPlayer.getLocation()))) {
							tempPlayer.sendMessage(GlobalMessageUtils.messageHead
												   + ChatColor.RED + "Stoplag has been deactivated in your TestArea.");
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("-here")) {
				final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p);
				final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);

				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "To activate stoplag globally, type '/stoplag global'.");
				} else {
					tempRegion.setStoplag(true);
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(Stoplag.getNowActivated("your"));
						} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(Stoplag.getNowActivated("the other"));
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("-other")) {
				final @Nullable DefinedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
				final @Nullable DefinedRegion otherRegion = TestAreaUtils.getRegion(p);

				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "To activate stoplag globally, type '/stoplag global'.");
				} else {
					tempRegion.setStoplag(true);
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempPlayer == p) {
							tempPlayer.sendMessage(Stoplag.getNowActivated("the other"));
						} else {
							if (tempRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(Stoplag.getNowActivated("your"));
							} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(Stoplag.getNowActivated("the other"));
							}
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("-north") || args[1].equalsIgnoreCase("-n")) {
				final @Nullable DefinedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
				final @Nullable DefinedRegion otherRegion = TestAreaUtils.getSouthRegion(p);

				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "To activate stoplag globally, type '/stoplag global'.");
				} else {
					tempRegion.setStoplag(true);
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempPlayer == p) {
							tempPlayer.sendMessage(Stoplag.getNowActivated("the north"));
						} else {
							if (tempRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(Stoplag.getNowActivated("your"));
							} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(Stoplag.getNowActivated("the other"));
							}
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("-south") || args[1].equalsIgnoreCase("-s")) {
				final @Nullable DefinedRegion tempRegion = TestAreaUtils.getSouthRegion(p);
				final @Nullable DefinedRegion otherRegion = TestAreaUtils.getNorthRegion(p);

				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "To activate stoplag globally, type '/stoplag global'.");
				} else {
					tempRegion.setStoplag(true);
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempPlayer == p) {
							tempPlayer.sendMessage(Stoplag.getNowActivated("the south"));
						} else {
							if (tempRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(Stoplag.getNowActivated("your"));
							} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(Stoplag.getNowActivated("the other"));
							}
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("-global") || args[1].equalsIgnoreCase("-confirm")) {
				p.performCommand("stoplag confirm");
			} else {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + net.md_5.bungee.api.ChatColor.RED + "Too many arguments.");
			}
		} else if (args.length == 3) {
			if (args[2].equalsIgnoreCase("-c")) {
				Stoplag.deactivate(args[1], p);
			} else if (args[1].equalsIgnoreCase("-c")) {
				Stoplag.deactivate(args[2], p);
			} else {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + net.md_5.bungee.api.ChatColor.RED + "Too many arguments.");
			}
		} else {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + net.md_5.bungee.api.ChatColor.RED + "Too many arguments.");
		}
	}

	private void deactivate(final @NotNull String arg, final @NotNull Player p) {
		if (arg.equalsIgnoreCase("-global")) {
			p.performCommand("stoplag -c");
		} else if (arg.equalsIgnoreCase("-here")) {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p);
			final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);

			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "To deactivate stoplag globally, type '/stoplag global -c'.");
			} else {
				tempRegion.setStoplag(false);
				for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
					if (tempRegion.inRegion(tempPlayer.getLocation())) {
						tempPlayer.sendMessage(Stoplag.getNowDeactivated("your"));
					} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
						tempPlayer.sendMessage(Stoplag.getNowActivated("the other"));
					}
				}
			}
		} else if (arg.equalsIgnoreCase("-other")) {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
			final @Nullable DefinedRegion otherRegion = TestAreaUtils.getRegion(p);

			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "To deactivate stoplag globally, type '/stoplag global -c'.");
			} else {
				tempRegion.setStoplag(false);
				for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
					if (tempRegion.inRegion(tempPlayer.getLocation())) {
						tempPlayer.sendMessage(Stoplag.getNowDeactivated("your"));
					} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
						tempPlayer.sendMessage(Stoplag.getNowDeactivated("the other"));
					}
				}
			}
		} else if (arg.equalsIgnoreCase("-north") || arg.equalsIgnoreCase("-n")) {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
			final @Nullable DefinedRegion otherRegion = TestAreaUtils.getSouthRegion(p);

			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "To deactivate stoplag globally, type '/stoplag global -c'.");
			} else {
				tempRegion.setStoplag(false);
				for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
					if (tempPlayer == p) {
						tempPlayer.sendMessage(Stoplag.getNowDeactivated("the north"));
					} else {
						if (tempRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(Stoplag.getNowDeactivated("your"));
						} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(Stoplag.getNowDeactivated("the other"));
						}
					}
				}
			}
		} else if (arg.equalsIgnoreCase("-south") || arg.equalsIgnoreCase("-s")) {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getSouthRegion(p);
			final @Nullable DefinedRegion otherRegion = TestAreaUtils.getNorthRegion(p);

			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "To deactivate stoplag globally, type '/stoplag global -c'.");
			} else {
				tempRegion.setStoplag(false);
				for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
					if (tempPlayer == p) {
						tempPlayer.sendMessage(Stoplag.getNowDeactivated("the south"));
					} else {
						if (tempRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(Stoplag.getNowDeactivated("your"));
						} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(Stoplag.getNowDeactivated("the other"));
						}
					}
				}
			}
		}
	}

	private @NotNull String getNowActivated(final @NotNull String area) {
		return GlobalMessageUtils.messageHead
			   + org.bukkit.ChatColor.RED + "Stoplag has been " + net.md_5.bungee.api.ChatColor.GREEN + "activated" + net.md_5.bungee.api.ChatColor.RED + " on the " + area + " side of your TestArea.";
	}

	private @NotNull String getNowDeactivated(final @NotNull String area) {
		return GlobalMessageUtils.messageHead
			   + org.bukkit.ChatColor.RED + "Stoplag has been deactivated on the " + area + " side of your TestArea.";
	}
}