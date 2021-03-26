package de.zeanon.testutils.plugin.commands;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class TNT {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length == 0) {
			final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
			final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);

			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
			} else {
				tempRegion.setFlag(Flags.TNT, tempRegion.getFlag(Flags.TNT) == StateFlag.State.DENY ? StateFlag.State.ALLOW : StateFlag.State.DENY);
				for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
					if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
						tempPlayer.sendMessage(tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW
											   ? TNT.getNowActivated("your")
											   : TNT.getNowDeactivated("your"));
					} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
						tempPlayer.sendMessage(tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW
											   ? TNT.getNowActivated("the other")
											   : TNT.getNowDeactivated("the other"));
					}
				}
			}
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("allow")) {
				final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
				final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);
				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
				} else {
					tempRegion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
							tempPlayer.sendMessage(TNT.getNowActivated("your"));
						} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
							tempPlayer.sendMessage(TNT.getNowActivated("the other"));
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("-deny")) {
				final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
				final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);

				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
				} else {
					tempRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
							tempPlayer.sendMessage(TNT.getNowDeactivated("your"));
						} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
							tempPlayer.sendMessage(TNT.getNowDeactivated("the other"));
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("-other")) {
				final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
				final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getRegion(p);

				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
				} else {
					tempRegion.setFlag(Flags.TNT, tempRegion.getFlag(Flags.TNT) == StateFlag.State.DENY ? StateFlag.State.ALLOW : StateFlag.State.DENY);
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
							tempPlayer.sendMessage(tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW
												   ? TNT.getNowActivated("your")
												   : TNT.getNowDeactivated("your"));
						} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
							tempPlayer.sendMessage(tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW
												   ? TNT.getNowActivated("the other")
												   : TNT.getNowDeactivated("the other"));
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("-north") || args[0].equalsIgnoreCase("-n")) {
				final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
				final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getSouthRegion(p);

				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
				} else {
					tempRegion.setFlag(Flags.TNT, tempRegion.getFlag(Flags.TNT) == StateFlag.State.DENY ? StateFlag.State.ALLOW : StateFlag.State.DENY);
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempPlayer == p) {
							tempPlayer.sendMessage(tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW
												   ? TNT.getNowActivated("the north")
												   : TNT.getNowDeactivated("the north"));
						} else {
							if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
								tempPlayer.sendMessage(tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW
													   ? TNT.getNowActivated("your")
													   : TNT.getNowDeactivated("your"));
							} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
								tempPlayer.sendMessage(tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW
													   ? TNT.getNowActivated("the other")
													   : TNT.getNowDeactivated("the other"));
							}
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("-south") || args[0].equalsIgnoreCase("-s")) {
				final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getSouthRegion(p);
				final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getNorthRegion(p);

				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
				} else {
					tempRegion.setFlag(Flags.TNT, tempRegion.getFlag(Flags.TNT) == StateFlag.State.DENY ? StateFlag.State.ALLOW : StateFlag.State.DENY);
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempPlayer == p) {
							tempPlayer.sendMessage(tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW
												   ? TNT.getNowActivated("the south")
												   : TNT.getNowDeactivated("the south"));
						} else {
							if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
								tempPlayer.sendMessage(tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW
													   ? TNT.getNowActivated("your")
													   : TNT.getNowDeactivated("your"));
							} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
								tempPlayer.sendMessage(tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW
													   ? TNT.getNowActivated("the other")
													   : TNT.getNowDeactivated("the other"));
							}
						}
					}
				}
			}
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("-other")) {
				if (args[1].equalsIgnoreCase("allow")) {
					final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
					final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
								tempPlayer.sendMessage(TNT.getNowActivated("your"));
							} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
								tempPlayer.sendMessage(TNT.getNowActivated("the other"));
							}
						}
					}
				} else if (args[1].equalsIgnoreCase("-deny")) {
					final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
					final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
								tempPlayer.sendMessage(TNT.getNowDeactivated("your"));
							} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
								tempPlayer.sendMessage(TNT.getNowDeactivated("the other"));
							}
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("-north") || args[0].equalsIgnoreCase("-n")) {
				if (args[1].equalsIgnoreCase("allow")) {
					final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
					final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getSouthRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempPlayer == p) {
								tempPlayer.sendMessage(TNT.getNowActivated("the north"));
							} else {
								if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowActivated("your"));
								} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowActivated("the other"));
								}
							}
						}
					}
				} else if (args[1].equalsIgnoreCase("-deny")) {
					final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
					final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getSouthRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempPlayer == p) {
								tempPlayer.sendMessage(TNT.getNowDeactivated("the north"));
							} else {
								if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowDeactivated("your"));
								} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowDeactivated("the other"));
								}
							}
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("-south") || args[0].equalsIgnoreCase("-s")) {
				if (args[1].equalsIgnoreCase("allow")) {
					final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getSouthRegion(p);
					final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getNorthRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempPlayer == p) {
								tempPlayer.sendMessage(TNT.getNowActivated("the south"));
							} else {
								if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowActivated("your"));
								} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowActivated("the other"));
								}
							}
						}
					}
				} else if (args[1].equalsIgnoreCase("-deny")) {
					final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getSouthRegion(p);
					final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getNorthRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempPlayer == p) {
								tempPlayer.sendMessage(TNT.getNowDeactivated("the south"));
							} else {
								if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowDeactivated("your"));
								} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowDeactivated("the other"));
								}
							}
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("-other")) {
				if (args[0].equalsIgnoreCase("allow")) {
					final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
					final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
								tempPlayer.sendMessage(TNT.getNowActivated("your"));
							} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
								tempPlayer.sendMessage(TNT.getNowActivated("the other"));
							}
						}
					}
				} else if (args[0].equalsIgnoreCase("-deny")) {
					final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
					final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
								tempPlayer.sendMessage(TNT.getNowDeactivated("your"));
							} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
								tempPlayer.sendMessage(TNT.getNowDeactivated("the other"));
							}
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("-north") || args[1].equalsIgnoreCase("-n")) {
				if (args[0].equalsIgnoreCase("allow")) {
					final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
					final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getSouthRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempPlayer == p) {
								tempPlayer.sendMessage(TNT.getNowActivated("the north"));
							} else {
								if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowActivated("your"));
								} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowActivated("the other"));
								}
							}
						}
					}
				} else if (args[0].equalsIgnoreCase("-deny")) {
					final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
					final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getSouthRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempPlayer == p) {
								tempPlayer.sendMessage(TNT.getNowDeactivated("the north"));
							} else {
								if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowDeactivated("your"));
								} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowDeactivated("the other"));
								}
							}
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("-south") || args[1].equalsIgnoreCase("-s")) {
				if (args[0].equalsIgnoreCase("allow")) {
					final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getSouthRegion(p);
					final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getNorthRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempPlayer == p) {
								tempPlayer.sendMessage(TNT.getNowActivated("the south"));
							} else {
								if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowActivated("your"));
								} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowActivated("the other"));
								}
							}
						}
					}
				} else if (args[0].equalsIgnoreCase("-deny")) {
					final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getSouthRegion(p);
					final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getNorthRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempPlayer == p) {
								tempPlayer.sendMessage(TNT.getNowDeactivated("the south"));
							} else {
								if (tempRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowDeactivated("your"));
								} else if (otherRegion.contains(tempPlayer.getLocation().getBlockX(), tempPlayer.getLocation().getBlockY(), tempPlayer.getLocation().getBlockZ())) {
									tempPlayer.sendMessage(TNT.getNowDeactivated("the other"));
								}
							}
						}
					}
				}
			}
		} else {
			p.sendMessage(ChatColor.DARK_AQUA + "Invalid sub-command '" + ChatColor.GOLD + args[0] + "'.");
		}
	}

	private @NotNull String getNowActivated(final @NotNull String area) {
		return GlobalMessageUtils.messageHead
			   + org.bukkit.ChatColor.RED + "TNT has been " + ChatColor.GREEN + "activated" + ChatColor.RED + " on " + area + " side of your TestArea.";
	}

	private @NotNull String getNowDeactivated(final @NotNull String area) {
		return GlobalMessageUtils.messageHead
			   + org.bukkit.ChatColor.RED + "TNT has been deactivated on " + area + " side of your TestArea.";
	}
}