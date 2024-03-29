package de.zeanon.testutils.plugin.commands.tnt;

import de.steamwar.commandframework.SWCommand;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import de.zeanon.testutils.plugin.utils.enums.TNTMode;
import de.zeanon.testutils.regionsystem.flags.Flag;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class TNTCommand extends SWCommand {

	public TNTCommand() {
		super(new Prefix("testutils"), "tnt");
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
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p);
			final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);

			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				return;
			}

			tempRegion.setFlag(Flag.TNT, TNTMode.parse((de.zeanon.testutils.regionsystem.flags.flagvalues.TNT) Objects.notNull(tempRegion.getFlag(Flag.TNT)), tntMode));
			otherRegion.setFlag(Flag.TNT, TNTMode.parse((de.zeanon.testutils.regionsystem.flags.flagvalues.TNT) Objects.notNull(otherRegion.getFlag(Flag.TNT)), tntMode));
			for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
				if (tempRegion.inRegion(tempPlayer.getLocation())) {
					tempPlayer.sendMessage(tempRegion.getFlag(Flag.TNT) == de.zeanon.testutils.regionsystem.flags.flagvalues.TNT.ALLOW
										   ? TNTCommand.getNowActivated("your")
										   : TNTCommand.getNowDeactivated("your"));
					tempPlayer.sendMessage(otherRegion.getFlag(Flag.TNT) == de.zeanon.testutils.regionsystem.flags.flagvalues.TNT.ALLOW
										   ? TNTCommand.getNowActivated("the other")
										   : TNTCommand.getNowDeactivated("the other"));
				} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
					tempPlayer.sendMessage(tempRegion.getFlag(Flag.TNT) == de.zeanon.testutils.regionsystem.flags.flagvalues.TNT.ALLOW
										   ? TNTCommand.getNowActivated("the other")
										   : TNTCommand.getNowDeactivated("the other"));
					tempPlayer.sendMessage(otherRegion.getFlag(Flag.TNT) == de.zeanon.testutils.regionsystem.flags.flagvalues.TNT.ALLOW
										   ? TNTCommand.getNowActivated("your")
										   : TNTCommand.getNowDeactivated("your"));
					//TODO Obviously not that nice
				}
			}
		} else {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p, regionSide);
			final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p, regionSide);

			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				return;
			}

			tempRegion.setFlag(Flag.TNT, TNTMode.parse((de.zeanon.testutils.regionsystem.flags.flagvalues.TNT) Objects.notNull(tempRegion.getFlag(Flag.TNT)), tntMode));
			for (final @NotNull Player tempPlayer : p.getWorld().getPlayers()) {
				if (tempPlayer == p) {
					tempPlayer.sendMessage(tempRegion.getFlag(Flag.TNT) == de.zeanon.testutils.regionsystem.flags.flagvalues.TNT.ALLOW
										   ? TNTCommand.getNowActivated(regionSide.getName())
										   : TNTCommand.getNowDeactivated(regionSide.getName()));
				} else {
					if (tempRegion.inRegion(tempPlayer.getLocation())) {
						tempPlayer.sendMessage(tempRegion.getFlag(Flag.TNT) == de.zeanon.testutils.regionsystem.flags.flagvalues.TNT.ALLOW
											   ? TNTCommand.getNowActivated("your")
											   : TNTCommand.getNowDeactivated("your"));
					} else if (otherRegion.inRegion(tempPlayer.getLocation())) {
						tempPlayer.sendMessage(tempRegion.getFlag(Flag.TNT) == de.zeanon.testutils.regionsystem.flags.flagvalues.TNT.ALLOW
											   ? TNTCommand.getNowActivated("the other")
											   : TNTCommand.getNowDeactivated("the other"));
					}
				}
			}
		}
	}
}