package de.zeanon.testutils.plugin.commands;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import java.util.HashSet;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class Stoplag {

	private final @NotNull Set<String> stoplagRegions = new HashSet<>();

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length == 1) {
			ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
			ProtectedRegion oppositeRegion = TestAreaUtils.getOppositeRegion(p);
			if (tempRegion == null || oppositeRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "To activate stoplag globally, type '/stoplag global'.");
			} else {
				if (!Stoplag.stoplagRegions.contains(tempRegion.getId())) {
					Stoplag.stoplagRegions.add(tempRegion.getId());
					Stoplag.stoplagRegions.add(oppositeRegion.getId());
				}
				for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
					if ((tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())
						 || oppositeRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ()))) {
						tempPlayer.sendMessage(GlobalMessageUtils.messageHead
											   + ChatColor.RED + "Stoplag has been " + ChatColor.GREEN + "activated" + ChatColor.RED + " in your TestArea.");
					}
				}
			}
		} else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("-c")) {
				ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
				ProtectedRegion oppositeRegion = TestAreaUtils.getOppositeRegion(p);

				if (tempRegion == null || oppositeRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "To deactivate stoplag globally, type '/stoplag global'.");
				} else {
					Stoplag.stoplagRegions.remove(tempRegion.getId());
					Stoplag.stoplagRegions.remove(oppositeRegion.getId());
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if ((tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())
							 || oppositeRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ()))) {
							tempPlayer.sendMessage(GlobalMessageUtils.messageHead
												   + ChatColor.RED + "Stoplag has been deactivated in your TestArea.");
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("-here")) {
				ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);

				if (tempRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "To activate stoplag globally, type '/stoplag global'.");
				} else {
					Stoplag.stoplagRegions.add(tempRegion.getId());
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
							tempPlayer.sendMessage(Stoplag.getNowActivated("your"));
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("-other")) {
				ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);

				if (tempRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "To activate stoplag globally, type '/stoplag global'.");
				} else {
					Stoplag.stoplagRegions.add(tempRegion.getId());
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
							tempPlayer.sendMessage(Stoplag.getNowActivated("other"));
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("-north") || args[1].equalsIgnoreCase("-n")) {
				ProtectedRegion tempRegion = TestAreaUtils.getNorthRegion(p);

				if (tempRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "To activate stoplag globally, type '/stoplag global'.");
				} else {
					Stoplag.stoplagRegions.add(tempRegion.getId());
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
							tempPlayer.sendMessage(Stoplag.getNowActivated("north"));
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("-south") || args[1].equalsIgnoreCase("-s")) {
				ProtectedRegion tempRegion = TestAreaUtils.getSouthRegion(p);

				if (tempRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "To activate stoplag globally, type '/stoplag global'.");
				} else {
					Stoplag.stoplagRegions.add(tempRegion.getId());
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
							tempPlayer.sendMessage(Stoplag.getNowActivated("south"));
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("-global") || args[1].equalsIgnoreCase("confirm")) {
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

	public boolean inStoplagRegion(final @NotNull Location location) {
		final @NotNull RegionManager tempManager = Objects.notNull(InitMode.getRegionContainer()
																		   .get(new BukkitWorld(location.getWorld())));
		for (ProtectedRegion temp : tempManager.getApplicableRegions(BlockVector3.at(location.getX(),
																					 location.getY(),
																					 location.getZ()))) {
			if (Stoplag.stoplagRegions.contains(temp.getId())) {
				return true;
			}
		}
		return false;
	}

	public boolean isStopLagRegion(final @NotNull ProtectedRegion region) {
		return Stoplag.stoplagRegions.contains(region.getId());
	}

	private void deactivate(final @NotNull String arg, final @NotNull Player p) {
		if (arg.equalsIgnoreCase("-global")) {
			p.performCommand("stoplag -c");
		} else if (arg.equalsIgnoreCase("-here")) {
			ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);

			if (tempRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "To deactivate stoplag globally, type '/stoplag global -c'.");
			} else {
				Stoplag.stoplagRegions.remove(tempRegion.getId());
				for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
					if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
						tempPlayer.sendMessage(Stoplag.getNowDeactivated("your"));
					}
				}
			}
		} else if (arg.equalsIgnoreCase("-other")) {
			ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);

			if (tempRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "To deactivate stoplag globally, type '/stoplag global -c'.");
			} else {
				Stoplag.stoplagRegions.remove(tempRegion.getId());
				for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
					if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
						tempPlayer.sendMessage(Stoplag.getNowDeactivated("other"));
					}
				}
			}
		} else if (arg.equalsIgnoreCase("-north") || arg.equalsIgnoreCase("-n")) {
			ProtectedRegion tempRegion = TestAreaUtils.getNorthRegion(p);

			if (tempRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "To deactivate stoplag globally, type '/stoplag global -c'.");
			} else {
				Stoplag.stoplagRegions.remove(tempRegion.getId());
				for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
					if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
						tempPlayer.sendMessage(Stoplag.getNowDeactivated("north"));
					}
				}
			}
		} else if (arg.equalsIgnoreCase("-south") || arg.equalsIgnoreCase("-s")) {
			ProtectedRegion tempRegion = TestAreaUtils.getSouthRegion(p);

			if (tempRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "To deactivate stoplag globally, type '/stoplag global -c'.");
			} else {
				Stoplag.stoplagRegions.remove(tempRegion.getId());
				for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
					if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
						tempPlayer.sendMessage(Stoplag.getNowDeactivated("south"));
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