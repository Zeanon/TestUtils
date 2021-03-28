package de.zeanon.testutils.plugin.commands;

import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.region.Region;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class TNT {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length == 0) {
			final @Nullable Region tempRegion = TestAreaUtils.getRegion(p);
			final @Nullable Region otherRegion = TestAreaUtils.getOppositeRegion(p);

			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
			} else {
				tempRegion.setTnt(!tempRegion.tnt());
				for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
					if (tempRegion.inRegion(tempPlayer.getLocation())) {
						tempPlayer.sendMessage(tempRegion.tnt()
											   ? TNT.getNowActivated("your")
											   : TNT.getNowDeactivated("your"));
					} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
						tempPlayer.sendMessage(tempRegion.tnt()
											   ? TNT.getNowActivated("the other")
											   : TNT.getNowDeactivated("the other"));
					}
				}
			}
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("allow")) {
				final @Nullable Region tempRegion = TestAreaUtils.getRegion(p);
				final @Nullable Region otherRegion = TestAreaUtils.getOppositeRegion(p);
				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
				} else {
					tempRegion.setTnt(true);
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(TNT.getNowActivated("your"));
						} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(TNT.getNowActivated("the other"));
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("deny")) {
				final @Nullable Region tempRegion = TestAreaUtils.getRegion(p);
				final @Nullable Region otherRegion = TestAreaUtils.getOppositeRegion(p);

				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
				} else {
					tempRegion.setTnt(false);
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(TNT.getNowDeactivated("your"));
						} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(TNT.getNowDeactivated("the other"));
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("-other")) {
				final @Nullable Region tempRegion = TestAreaUtils.getOppositeRegion(p);
				final @Nullable Region otherRegion = TestAreaUtils.getRegion(p);

				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
				} else {
					tempRegion.setTnt(!tempRegion.tnt());
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(tempRegion.tnt()
												   ? TNT.getNowActivated("your")
												   : TNT.getNowDeactivated("your"));
						} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
							tempPlayer.sendMessage(tempRegion.tnt()
												   ? TNT.getNowActivated("the other")
												   : TNT.getNowDeactivated("the other"));
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("-north") || args[0].equalsIgnoreCase("-n")) {
				final @Nullable Region tempRegion = TestAreaUtils.getNorthRegion(p);
				final @Nullable Region otherRegion = TestAreaUtils.getSouthRegion(p);

				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
				} else {
					tempRegion.setTnt(!tempRegion.tnt());
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempPlayer == p) {
							tempPlayer.sendMessage(tempRegion.tnt()
												   ? TNT.getNowActivated("the north")
												   : TNT.getNowDeactivated("the north"));
						} else {
							if (tempRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(tempRegion.tnt()
													   ? TNT.getNowActivated("your")
													   : TNT.getNowDeactivated("your"));
							} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(tempRegion.tnt()
													   ? TNT.getNowActivated("the other")
													   : TNT.getNowDeactivated("the other"));
							}
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("-south") || args[0].equalsIgnoreCase("-s")) {
				final @Nullable Region tempRegion = TestAreaUtils.getSouthRegion(p);
				final @Nullable Region otherRegion = TestAreaUtils.getNorthRegion(p);

				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
				} else {
					tempRegion.setTnt(!tempRegion.tnt());
					for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
						if (tempPlayer == p) {
							tempPlayer.sendMessage(tempRegion.tnt()
												   ? TNT.getNowActivated("the south")
												   : TNT.getNowDeactivated("the south"));
						} else {
							if (tempRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(tempRegion.tnt()
													   ? TNT.getNowActivated("your")
													   : TNT.getNowDeactivated("your"));
							} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(tempRegion.tnt()
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
					final @Nullable Region tempRegion = TestAreaUtils.getOppositeRegion(p);
					final @Nullable Region otherRegion = TestAreaUtils.getRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setTnt(true);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(TNT.getNowActivated("your"));
							} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(TNT.getNowActivated("the other"));
							}
						}
					}
				} else if (args[1].equalsIgnoreCase("deny")) {
					final @Nullable Region tempRegion = TestAreaUtils.getOppositeRegion(p);
					final @Nullable Region otherRegion = TestAreaUtils.getRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setTnt(false);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(TNT.getNowDeactivated("your"));
							} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(TNT.getNowDeactivated("the other"));
							}
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("-north") || args[0].equalsIgnoreCase("-n")) {
				if (args[1].equalsIgnoreCase("allow")) {
					final @Nullable Region tempRegion = TestAreaUtils.getNorthRegion(p);
					final @Nullable Region otherRegion = TestAreaUtils.getSouthRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setTnt(true);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempPlayer == p) {
								tempPlayer.sendMessage(TNT.getNowActivated("the north"));
							} else {
								if (tempRegion.inRegion(tempPlayer.getLocation())) {
									tempPlayer.sendMessage(TNT.getNowActivated("your"));
								} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
									tempPlayer.sendMessage(TNT.getNowActivated("the other"));
								}
							}
						}
					}
				} else if (args[1].equalsIgnoreCase("deny")) {
					final @Nullable Region tempRegion = TestAreaUtils.getNorthRegion(p);
					final @Nullable Region otherRegion = TestAreaUtils.getSouthRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setTnt(false);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempPlayer == p) {
								tempPlayer.sendMessage(TNT.getNowDeactivated("the north"));
							} else {
								if (tempRegion.inRegion(tempPlayer.getLocation())) {
									tempPlayer.sendMessage(TNT.getNowDeactivated("your"));
								} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
									tempPlayer.sendMessage(TNT.getNowDeactivated("the other"));
								}
							}
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("-south") || args[0].equalsIgnoreCase("-s")) {
				if (args[1].equalsIgnoreCase("allow")) {
					final @Nullable Region tempRegion = TestAreaUtils.getSouthRegion(p);
					final @Nullable Region otherRegion = TestAreaUtils.getNorthRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setTnt(true);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempPlayer == p) {
								tempPlayer.sendMessage(TNT.getNowActivated("the south"));
							} else {
								if (tempRegion.inRegion(tempPlayer.getLocation())) {
									tempPlayer.sendMessage(TNT.getNowActivated("your"));
								} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
									tempPlayer.sendMessage(TNT.getNowActivated("the other"));
								}
							}
						}
					}
				} else if (args[1].equalsIgnoreCase("deny")) {
					final @Nullable Region tempRegion = TestAreaUtils.getSouthRegion(p);
					final @Nullable Region otherRegion = TestAreaUtils.getNorthRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setTnt(false);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempPlayer == p) {
								tempPlayer.sendMessage(TNT.getNowDeactivated("the south"));
							} else {
								if (tempRegion.inRegion(tempPlayer.getLocation())) {
									tempPlayer.sendMessage(TNT.getNowDeactivated("your"));
								} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
									tempPlayer.sendMessage(TNT.getNowDeactivated("the other"));
								}
							}
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("-other")) {
				if (args[0].equalsIgnoreCase("allow")) {
					final @Nullable Region tempRegion = TestAreaUtils.getOppositeRegion(p);
					final @Nullable Region otherRegion = TestAreaUtils.getRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setTnt(true);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(TNT.getNowActivated("your"));
							} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(TNT.getNowActivated("the other"));
							}
						}
					}
				} else if (args[0].equalsIgnoreCase("deny")) {
					final @Nullable Region tempRegion = TestAreaUtils.getOppositeRegion(p);
					final @Nullable Region otherRegion = TestAreaUtils.getRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setTnt(false);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(TNT.getNowDeactivated("your"));
							} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
								tempPlayer.sendMessage(TNT.getNowDeactivated("the other"));
							}
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("-north") || args[1].equalsIgnoreCase("-n")) {
				if (args[0].equalsIgnoreCase("allow")) {
					final @Nullable Region tempRegion = TestAreaUtils.getNorthRegion(p);
					final @Nullable Region otherRegion = TestAreaUtils.getSouthRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setTnt(true);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempPlayer == p) {
								tempPlayer.sendMessage(TNT.getNowActivated("the north"));
							} else {
								if (tempRegion.inRegion(tempPlayer.getLocation())) {
									tempPlayer.sendMessage(TNT.getNowActivated("your"));
								} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
									tempPlayer.sendMessage(TNT.getNowActivated("the other"));
								}
							}
						}
					}
				} else if (args[0].equalsIgnoreCase("deny")) {
					final @Nullable Region tempRegion = TestAreaUtils.getNorthRegion(p);
					final @Nullable Region otherRegion = TestAreaUtils.getSouthRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setTnt(false);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempPlayer == p) {
								tempPlayer.sendMessage(TNT.getNowDeactivated("the north"));
							} else {
								if (tempRegion.inRegion(tempPlayer.getLocation())) {
									tempPlayer.sendMessage(TNT.getNowDeactivated("your"));
								} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
									tempPlayer.sendMessage(TNT.getNowDeactivated("the other"));
								}
							}
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("-south") || args[1].equalsIgnoreCase("-s")) {
				if (args[0].equalsIgnoreCase("allow")) {
					final @Nullable Region tempRegion = TestAreaUtils.getSouthRegion(p);
					final @Nullable Region otherRegion = TestAreaUtils.getNorthRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setTnt(true);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempPlayer == p) {
								tempPlayer.sendMessage(TNT.getNowActivated("the south"));
							} else {
								if (tempRegion.inRegion(tempPlayer.getLocation())) {
									tempPlayer.sendMessage(TNT.getNowActivated("your"));
								} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
									tempPlayer.sendMessage(TNT.getNowActivated("the other"));
								}
							}
						}
					}
				} else if (args[0].equalsIgnoreCase("deny")) {
					final @Nullable Region tempRegion = TestAreaUtils.getSouthRegion(p);
					final @Nullable Region otherRegion = TestAreaUtils.getNorthRegion(p);

					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						tempRegion.setTnt(false);
						for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
							if (tempPlayer == p) {
								tempPlayer.sendMessage(TNT.getNowDeactivated("the south"));
							} else {
								if (tempRegion.inRegion(tempPlayer.getLocation())) {
									tempPlayer.sendMessage(TNT.getNowDeactivated("your"));
								} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
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