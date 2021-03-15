package de.zeanon.testutils.plugin.commands;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class TNT {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (args.length == 0) {
					ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
					if (tempRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setFlag(Flags.TNT, tempRegion.getFlag(Flags.TNT) == StateFlag.State.DENY ? StateFlag.State.ALLOW : StateFlag.State.DENY);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
								tempPlayer.sendMessage(tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW
													   ? TNT.getNowActivated(tempRegion)
													   : TNT.getNowDeactivated(tempRegion));
							}
						}
					}
				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("allow")) {
						final ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
						if (tempRegion == null) {
							GlobalMessageUtils.sendNotApplicableRegion(p);
						} else {
							tempRegion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
							for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
								if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowActivated(tempRegion));
								}
							}
						}
					} else if (args[0].equalsIgnoreCase("deny")) {
						final ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
						if (tempRegion == null) {
							GlobalMessageUtils.sendNotApplicableRegion(p);
						} else {
							tempRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
							for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
								if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowDeactivated(tempRegion));
								}
							}
						}
					} else if (args[0].equalsIgnoreCase("-other")) {
						final ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
						if (tempRegion == null) {
							GlobalMessageUtils.sendNotApplicableRegion(p);
						} else {
							tempRegion.setFlag(Flags.TNT, tempRegion.getFlag(Flags.TNT) == StateFlag.State.DENY ? StateFlag.State.ALLOW : StateFlag.State.DENY);
							for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
								if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW
														   ? TNT.getNowActivated(tempRegion)
														   : TNT.getNowDeactivated(tempRegion));
								}
							}
						}
					} else if (args[0].equalsIgnoreCase("-north") || args[0].equalsIgnoreCase("-n")) {
						final ProtectedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
						if (tempRegion == null) {
							GlobalMessageUtils.sendNotApplicableRegion(p);
						} else {
							tempRegion.setFlag(Flags.TNT, tempRegion.getFlag(Flags.TNT) == StateFlag.State.DENY ? StateFlag.State.ALLOW : StateFlag.State.DENY);
							for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
								if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW
														   ? TNT.getNowActivated(tempRegion)
														   : TNT.getNowDeactivated(tempRegion));
								}
							}
						}
					} else if (args[0].equalsIgnoreCase("-south") || args[0].equalsIgnoreCase("-s")) {
						final ProtectedRegion tempRegion = TestAreaUtils.getSouthRegion(p);
						if (tempRegion == null) {
							GlobalMessageUtils.sendNotApplicableRegion(p);
						} else {
							tempRegion.setFlag(Flags.TNT, tempRegion.getFlag(Flags.TNT) == StateFlag.State.DENY ? StateFlag.State.ALLOW : StateFlag.State.DENY);
							for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
								if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW
														   ? TNT.getNowActivated(tempRegion)
														   : TNT.getNowDeactivated(tempRegion));
								}
							}
						}
					}
				} else if (args.length == 2) {
					if (args[0].equalsIgnoreCase("-other")) {
						if (args[1].equalsIgnoreCase("allow")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
							if (tempRegion == null) {
								GlobalMessageUtils.sendNotApplicableRegion(p);
							} else {
								tempRegion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
								for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
									if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
										tempPlayer.sendMessage(TNT.getNowActivated(tempRegion));
									}
								}
							}
						} else if (args[1].equalsIgnoreCase("deny")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
							if (tempRegion == null) {
								GlobalMessageUtils.sendNotApplicableRegion(p);
							} else {
								tempRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
								for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
									if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
										tempPlayer.sendMessage(TNT.getNowDeactivated(tempRegion));
									}
								}
							}
						}
					} else if (args[0].equalsIgnoreCase("-north") || args[0].equalsIgnoreCase("-n")) {
						if (args[1].equalsIgnoreCase("allow")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
							if (tempRegion == null) {
								GlobalMessageUtils.sendNotApplicableRegion(p);
							} else {
								tempRegion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
								for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
									if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
										tempPlayer.sendMessage(TNT.getNowActivated(tempRegion));
									}
								}
							}
						} else if (args[1].equalsIgnoreCase("deny")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
							if (tempRegion == null) {
								GlobalMessageUtils.sendNotApplicableRegion(p);
							} else {
								tempRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
								for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
									if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
										tempPlayer.sendMessage(TNT.getNowDeactivated(tempRegion));
									}
								}
							}
						}
					} else if (args[0].equalsIgnoreCase("-south") || args[0].equalsIgnoreCase("-s")) {
						if (args[1].equalsIgnoreCase("allow")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getSouthRegion(p);
							if (tempRegion == null) {
								GlobalMessageUtils.sendNotApplicableRegion(p);
							} else {
								tempRegion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
								for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
									if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
										tempPlayer.sendMessage(TNT.getNowActivated(tempRegion));
									}
								}
							}
						} else if (args[1].equalsIgnoreCase("deny")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getSouthRegion(p);
							if (tempRegion == null) {
								GlobalMessageUtils.sendNotApplicableRegion(p);
							} else {
								tempRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
								for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
									if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
										tempPlayer.sendMessage(TNT.getNowDeactivated(tempRegion));
									}
								}
							}
						}
					} else if (args[1].equalsIgnoreCase("-other")) {
						if (args[0].equalsIgnoreCase("allow")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
							if (tempRegion == null) {
								GlobalMessageUtils.sendNotApplicableRegion(p);
							} else {
								tempRegion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
								for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
									if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
										tempPlayer.sendMessage(TNT.getNowActivated(tempRegion));
									}
								}
							}
						} else if (args[0].equalsIgnoreCase("deny")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
							if (tempRegion == null) {
								GlobalMessageUtils.sendNotApplicableRegion(p);
							} else {
								tempRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
								for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
									if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
										tempPlayer.sendMessage(TNT.getNowDeactivated(tempRegion));
									}
								}
							}
						}
					} else if (args[1].equalsIgnoreCase("-north") || args[1].equalsIgnoreCase("-n")) {
						if (args[0].equalsIgnoreCase("allow")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
							if (tempRegion == null) {
								GlobalMessageUtils.sendNotApplicableRegion(p);
							} else {
								tempRegion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
								for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
									if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
										tempPlayer.sendMessage(TNT.getNowActivated(tempRegion));
									}
								}
							}
						} else if (args[0].equalsIgnoreCase("deny")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
							if (tempRegion == null) {
								GlobalMessageUtils.sendNotApplicableRegion(p);
							} else {
								tempRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
								for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
									if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
										tempPlayer.sendMessage(TNT.getNowDeactivated(tempRegion));
									}
								}
							}
						}
					} else if (args[1].equalsIgnoreCase("-south") || args[1].equalsIgnoreCase("-s")) {
						if (args[0].equalsIgnoreCase("allow")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getSouthRegion(p);
							if (tempRegion == null) {
								GlobalMessageUtils.sendNotApplicableRegion(p);
							} else {
								tempRegion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
								for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
									if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
										tempPlayer.sendMessage(TNT.getNowActivated(tempRegion));
									}
								}
							}
						} else if (args[0].equalsIgnoreCase("deny")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getSouthRegion(p);
							if (tempRegion == null) {
								GlobalMessageUtils.sendNotApplicableRegion(p);
							} else {
								tempRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
								for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
									if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
										tempPlayer.sendMessage(TNT.getNowDeactivated(tempRegion));
									}
								}
							}
						}
					}
				} else {
					p.sendMessage(ChatColor.DARK_AQUA + "Invalid sub-command '" + ChatColor.GOLD + args[0] + "'.");
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}

	private @NotNull String getNowActivated(final @NotNull ProtectedRegion tempRegion) {
		return GlobalMessageUtils.messageHead
			   + ChatColor.RED + "TNT is now " + ChatColor.GREEN + "activated" + ChatColor.RED + " in '" + ChatColor.DARK_RED + tempRegion.getId().substring(9) + ChatColor.RED + "'.";
	}

	private @NotNull String getNowDeactivated(final @NotNull ProtectedRegion tempRegion) {
		return GlobalMessageUtils.messageHead
			   + ChatColor.RED + "TNT is now deactivated in '" + ChatColor.DARK_RED + tempRegion.getId().substring(9) + ChatColor.RED + "'.";
	}
}