package de.zeanon.testutils.plugin.commands;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanagercore.external.browniescollections.GapList;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class Stoplag {

	private final List<String> stoplagRegions = new GapList<>();

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
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Stoplag has been " + ChatColor.GREEN + "activated" + ChatColor.RED + " in your TestArea.");
			}
		} else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("-c")) {
				ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
				ProtectedRegion oppositeRegion = TestAreaUtils.getOppositeRegion(p);

				if (tempRegion == null || oppositeRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "To activate stoplag globally, type '/stoplag global'.");
				} else {
					Stoplag.stoplagRegions.remove(tempRegion.getId());
					Stoplag.stoplagRegions.remove(oppositeRegion.getId());
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "Stoplag has been deactivated in your TestArea.");
				}
			} else if (args[1].equalsIgnoreCase("here")) {
				ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);

				if (tempRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "To activate stoplag globally, type '/stoplag global'.");
				} else {
					Stoplag.stoplagRegions.add(tempRegion.getId());
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "Stoplag has been " + ChatColor.GREEN + "activated" + ChatColor.RED + " on your side of your TestArea.");
				}
			} else if (args[1].equalsIgnoreCase("other")) {
				ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);

				if (tempRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "To activate stoplag globally, type '/stoplag global'.");
				} else {
					Stoplag.stoplagRegions.add(tempRegion.getId());
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "Stoplag has been " + ChatColor.GREEN + "activated" + ChatColor.RED + " on the other side of your TestArea.");
				}
			} else if (args[1].equalsIgnoreCase("global") || args[1].equalsIgnoreCase("confirm")) {
				p.performCommand("stoplag confirm");
			}
		} else if (args.length == 3) {
			if (args[2].equalsIgnoreCase("-c")) {
				if (args[1].equalsIgnoreCase("global")) {
					p.performCommand("stoplag -c");
				} else if (args[1].equalsIgnoreCase("here")) {
					ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);

					if (tempRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "To activate stoplag globally, type '/stoplag global'.");
					} else {
						Stoplag.stoplagRegions.remove(tempRegion.getId());
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "Stoplag has been deactivated on your side of your TestArea.");
					}
				} else if (args[1].equalsIgnoreCase("other")) {
					ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);

					if (tempRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "To activate stoplag globally, type '/stoplag global'.");
					} else {
						Stoplag.stoplagRegions.remove(tempRegion.getId());
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "Stoplag has been deactivated on the other side  of your TestArea.");
					}
				}
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
}
