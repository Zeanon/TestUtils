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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class Stoplag {

	private final List<ProtectedRegion> stoplagRegions = new GapList<>();


	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length == 1) {
			ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);

			if (tempRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "To activate stoplag globally, type '/stoplag global'.");
			} else {
				if (!Stoplag.stoplagRegions.contains(tempRegion)) {
					Stoplag.stoplagRegions.add(tempRegion);
					Stoplag.stoplagRegions.add(TestAreaUtils.getOppositeRegion(p));
				}
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Stoplag has been " + ChatColor.GREEN + "activated" + ChatColor.RED + " in your TestArea.");
			}
		} else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("-c")) {
				ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);

				if (tempRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "To activate stoplag globally, type '/stoplag global'.");
				} else {
					Stoplag.stoplagRegions.remove(tempRegion);
					Stoplag.stoplagRegions.remove(TestAreaUtils.getOppositeRegion(p));
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
					Stoplag.stoplagRegions.add(tempRegion);
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
					Stoplag.stoplagRegions.add(tempRegion);
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
						Stoplag.stoplagRegions.remove(tempRegion);
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
						Stoplag.stoplagRegions.remove(tempRegion);
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

	public @NotNull List<String> onTab(final @NotNull String buffer) {
		final @NotNull String message = buffer.replaceAll("\\s+", " ");
		final @NotNull String[] args = buffer.replaceAll("\\s+", " ").split(" ");
		final boolean argumentEnded = message.endsWith(" ");
		if ((args.length == 2 && !argumentEnded) || (args.length == 1 && argumentEnded)) {
			if (argumentEnded) {
				return Arrays.asList("-c", "here", "other", "global");
			} else {
				return Stoplag.getCompletions(args[1], "-c", "here", "other", "global");
			}
		} else if (args.length == 3 && !argumentEnded || args.length == 2) {
			if (argumentEnded) {
				if (!args[1].equalsIgnoreCase("-c")) {
					return Collections.singletonList("-c");
				} else {
					return Collections.emptyList();
				}
			} else {
				if (!args[1].equalsIgnoreCase("-c")) {
					return Stoplag.getCompletions(args[2], "-c");
				} else {
					return Collections.emptyList();
				}
			}
		} else {
			return Collections.emptyList();
		}
	}

	public boolean inStoplagRegion(final @NotNull Location location) {
		final @NotNull RegionManager tempManager = Objects.notNull(InitMode.getRegionContainer()
																		   .get(new BukkitWorld(location.getWorld())));
		for (ProtectedRegion temp : tempManager.getApplicableRegions(BlockVector3.at(location.getX(),
																					 location.getY(),
																					 location.getZ()))) {
			if (Stoplag.stoplagRegions.contains(temp)) {
				return true;
			}
		}
		return false;
	}

	private List<String> getCompletions(final @NotNull String arg, final @NotNull String... completions) {
		return Arrays.stream(completions)
					 .filter(completion -> completion.startsWith(arg.toLowerCase()))
					 .collect(Collectors.toList());
	}
}
