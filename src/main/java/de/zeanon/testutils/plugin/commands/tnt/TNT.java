package de.zeanon.testutils.plugin.commands.tnt;

import de.zeanon.testutils.commandframework.SWCommand;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import de.zeanon.testutils.plugin.utils.enums.TNTMode;
import de.zeanon.testutils.regionsystem.flags.Flag;
import de.zeanon.testutils.regionsystem.region.TestArea;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class TNT extends SWCommand {

	public TNT() {
		super("tnt", true);
	}

	@Register
	public void noArgs(final @NotNull Player p) {
		this.execute(p, RegionSide.NONE, null);
	}

	@Register(help = true)
	public void noArgsHelp(final @NotNull Player p, final @NotNull String... args) {
		if (args.length == 0) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Missing argument.");
		} else {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Unknown argument '" + ChatColor.DARK_RED + args[args.length - 1] + ChatColor.RED + "'.");
		}
	}

	@Register
	public void oneArg(final @NotNull Player p, final @NotNull RegionSide regionSide) {
		this.execute(p, regionSide, null);
	}

	@Register
	public void oneArg(final @NotNull Player p, final @NotNull TNTMode tntMode) {
		this.execute(p, RegionSide.NONE, tntMode);
	}

	@Register
	public void twoArgs(final @NotNull Player p, final @NotNull TNTMode tntMode, final @NotNull RegionSide regionSide) {
		this.execute(p, regionSide, tntMode);
	}

	@Register
	public void twoArgs(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull TNTMode tntMode) {
		this.execute(p, regionSide, tntMode);
	}


	private static @NotNull String getNowActivated(final @NotNull String area) {
		return GlobalMessageUtils.MESSAGE_HEAD
			   + ChatColor.RED + "TNT has been " + ChatColor.GREEN + "activated" + ChatColor.RED + " on " + area + " side of your TestArea.";
	}

	private static @NotNull String getNowDeactivated(final @NotNull String area) {
		return GlobalMessageUtils.MESSAGE_HEAD
			   + ChatColor.RED + "TNT has been deactivated on " + area + " side of your TestArea.";
	}

	private void execute(final @NotNull Player p, final @NotNull RegionSide regionSide, final @Nullable TNTMode tntMode) {
		if (regionSide == RegionSide.NONE) {
			final @Nullable TestArea tempRegion = TestAreaUtils.getRegion(p);
			final @Nullable TestArea otherRegion = TestAreaUtils.getOppositeRegion(p);

			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				return;
			}

			tempRegion.set(Flag.TNT, TNTMode.parse((de.zeanon.testutils.regionsystem.flags.flagvalues.TNT) tempRegion.get(Flag.TNT), tntMode));
			otherRegion.set(Flag.TNT, TNTMode.parse((de.zeanon.testutils.regionsystem.flags.flagvalues.TNT) otherRegion.get(Flag.TNT), tntMode));
			for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
				if (tempRegion.inRegion(tempPlayer.getLocation())) {
					tempPlayer.sendMessage(tempRegion.get(Flag.TNT) == de.zeanon.testutils.regionsystem.flags.flagvalues.TNT.ALLOW
										   ? TNT.getNowActivated("your")
										   : TNT.getNowDeactivated("your"));
					tempPlayer.sendMessage(otherRegion.get(Flag.TNT) == de.zeanon.testutils.regionsystem.flags.flagvalues.TNT.ALLOW
										   ? TNT.getNowActivated("the other")
										   : TNT.getNowDeactivated("the other"));
				} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
					tempPlayer.sendMessage(tempRegion.get(Flag.TNT) == de.zeanon.testutils.regionsystem.flags.flagvalues.TNT.ALLOW
										   ? TNT.getNowActivated("the other")
										   : TNT.getNowDeactivated("the other"));
					tempPlayer.sendMessage(otherRegion.get(Flag.TNT) == de.zeanon.testutils.regionsystem.flags.flagvalues.TNT.ALLOW
										   ? TNT.getNowActivated("your")
										   : TNT.getNowDeactivated("your"));
					//TODO Obviously not that nice
				}
			}
		} else {
			final @Nullable TestArea tempRegion = TestAreaUtils.getRegion(p, regionSide);
			final @Nullable TestArea otherRegion = TestAreaUtils.getOppositeRegion(p, regionSide);

			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				return;
			}

			tempRegion.set(Flag.TNT, TNTMode.parse((de.zeanon.testutils.regionsystem.flags.flagvalues.TNT) tempRegion.get(Flag.TNT), tntMode));
			for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
				if (tempPlayer == p) {
					tempPlayer.sendMessage(tempRegion.get(Flag.TNT) == de.zeanon.testutils.regionsystem.flags.flagvalues.TNT.ALLOW
										   ? TNT.getNowActivated(regionSide.getName())
										   : TNT.getNowDeactivated(regionSide.getName()));
				} else {
					if (tempRegion.inRegion(tempPlayer.getLocation())) {
						tempPlayer.sendMessage(tempRegion.get(Flag.TNT) == de.zeanon.testutils.regionsystem.flags.flagvalues.TNT.ALLOW
											   ? TNT.getNowActivated("your")
											   : TNT.getNowDeactivated("your"));
					} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
						tempPlayer.sendMessage(tempRegion.get(Flag.TNT) == de.zeanon.testutils.regionsystem.flags.flagvalues.TNT.ALLOW
											   ? TNT.getNowActivated("the other")
											   : TNT.getNowDeactivated("the other"));
					}
				}
			}
		}
	}
}