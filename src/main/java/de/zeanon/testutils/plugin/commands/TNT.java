package de.zeanon.testutils.plugin.commands;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.testutils.TestUtils;
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
						p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
									  ChatColor.RED + "You are not standing in an applicable region.");
					} else {
						tempRegion.setFlag(Flags.TNT, tempRegion.getFlag(Flags.TNT) == StateFlag.State.DENY ? StateFlag.State.ALLOW : StateFlag.State.DENY);
						p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
									  ChatColor.RED + "TNT is now " + (tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW ? "activated" : "deactivated") + " in '" + ChatColor.DARK_RED + tempRegion.getId() + ChatColor.RED + "'.");
					}
				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("info")) {
						final ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
						if (tempRegion == null) {
							p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
										  ChatColor.RED + "You are not standing in an applicable region.");
						} else {
							p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
										  ChatColor.RED + "TNT is " + (tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW ? "activated" : "deactivated") + " in '" + ChatColor.DARK_RED + tempRegion.getId() + ChatColor.RED + "'.");
						}
					} else if (args[0].equalsIgnoreCase("allow")) {
						final ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
						if (tempRegion == null) {
							p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
										  ChatColor.RED + "You are not standing in an applicable region.");
						} else {
							tempRegion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
							p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
										  ChatColor.RED + "TNT is now activated in '" + ChatColor.DARK_RED + tempRegion.getId() + ChatColor.RED + "'.");
						}
					} else if (args[0].equalsIgnoreCase("deny")) {
						final ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
						if (tempRegion == null) {
							p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
										  ChatColor.RED + "You are not standing in an applicable region.");
						} else {
							tempRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
							p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
										  ChatColor.RED + "TNT is now deactivated in '" + ChatColor.DARK_RED + tempRegion.getId() + ChatColor.RED + "'.");
						}
					} else if (args[0].equalsIgnoreCase("other")) {
						final ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
						if (tempRegion == null) {
							p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
										  ChatColor.RED + "You are not standing in an applicable region.");
						} else {
							tempRegion.setFlag(Flags.TNT, tempRegion.getFlag(Flags.TNT) == StateFlag.State.DENY ? StateFlag.State.ALLOW : StateFlag.State.DENY);
							p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
										  ChatColor.RED + "TNT is now " + (tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW ? "activated" : "deactivated") + " in '" + ChatColor.DARK_RED + tempRegion.getId() + ChatColor.RED + "'.");
						}
					}
				} else if (args.length == 2) {
					if (args[0].equalsIgnoreCase("other")) {
						if (args[1].equalsIgnoreCase("info")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
							if (tempRegion == null) {
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
											  ChatColor.RED + "You are not standing in an applicable region.");
							} else {
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
											  ChatColor.RED + "TNT is " + (tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW ? "activated" : "deactivated") + " in '" + ChatColor.DARK_RED + tempRegion.getId() + ChatColor.RED + "'.");
							}
						} else if (args[1].equalsIgnoreCase("allow")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
							if (tempRegion == null) {
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
											  ChatColor.RED + "You are not standing in an applicable region.");
							} else {
								tempRegion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
											  ChatColor.RED + "TNT is now activated in '" + ChatColor.DARK_RED + tempRegion.getId() + ChatColor.RED + "'.");
							}
						} else if (args[1].equalsIgnoreCase("deny")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
							if (tempRegion == null) {
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
											  ChatColor.RED + "You are not standing in an applicable region.");
							} else {
								tempRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
											  ChatColor.RED + "TNT is now deactivated in '" + ChatColor.DARK_RED + tempRegion.getId() + ChatColor.RED + "'.");
							}
						}
					} else if (args[1].equalsIgnoreCase("other")) {
						if (args[0].equalsIgnoreCase("info")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
							if (tempRegion == null) {
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
											  ChatColor.RED + "You are not standing in an applicable region.");
							} else {
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
											  ChatColor.RED + "TNT is " + (tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW ? "activated" : "deactivated") + " in '" + ChatColor.DARK_RED + tempRegion.getId() + ChatColor.RED + "'.");
							}
						} else if (args[0].equalsIgnoreCase("allow")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
							if (tempRegion == null) {
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
											  ChatColor.RED + "You are not standing in an applicable region.");
							} else {
								tempRegion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
											  ChatColor.RED + "TNT is now activated in '" + ChatColor.DARK_RED + tempRegion.getId() + ChatColor.RED + "'.");
							}
						} else if (args[0].equalsIgnoreCase("deny")) {
							final ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
							if (tempRegion == null) {
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
											  ChatColor.RED + "You are not standing in an applicable region.");
							} else {
								tempRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
											  ChatColor.RED + "TNT is now deactivated in '" + ChatColor.DARK_RED + tempRegion.getId() + ChatColor.RED + "'.");
							}
						}
					}
				} else {
					p.sendMessage(ChatColor.DARK_AQUA + "Invalid sub-command '" + ChatColor.GOLD + args[0] + "'.");
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}
}