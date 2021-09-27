package de.zeanon.testutils.regionsystem.commands;

import de.steamwar.commandframework.SWCommand;
import de.zeanon.testutils.plugin.utils.enums.RegionName;
import de.zeanon.testutils.regionsystem.RegionManager;
import de.zeanon.testutils.regionsystem.flags.Flag;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class RegionCommand extends SWCommand {


	public static final @NotNull String MESSAGE_HEAD = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "RegionSystem" + ChatColor.DARK_GRAY + "] ";
	private static final @NotNull String FLAGS_HELP_MESSAGE = RegionCommand.getFlagsHelpMessage();


	public RegionCommand() {
		super("region", "rg");
	}


	public static void sendMultipleRegions(final @NotNull List<DefinedRegion> regions, final @NotNull Player p) {
		p.sendMessage(RegionCommand.MESSAGE_HEAD
					  + ChatColor.RED + "You are standing in multiple regions, please define which one to use: "
					  + regions.stream().map(region -> ChatColor.DARK_RED + region.getName()).collect(Collectors.joining(ChatColor.RED + ", ")));
	}


	@Register(value = "info", description = "Get the info about the region you are currently standing in.")
	public void noArgsInfo(final @NotNull Player p) {
		Info.execute(null, p);
	}

	@Register(value = "info", description = "Get the info about the given region.")
	public void oneArgInfo(final @NotNull Player p, final @NotNull RegionName regionName) {
		Info.execute(regionName, p);
	}


	@Register(value = "removeentities", description = "Remove all entities in the region you are currently standing in.")
	public void noArgsRemoveEntities(final @NotNull Player p) {
		RemoveEntities.execute(null, p);
	}

	@Register(value = "removeentities", description = "Remove all entities in the given region.")
	public void oneArgRemoveEntities(final @NotNull Player p, final @NotNull RegionName regionName) {
		RemoveEntities.execute(regionName, p);
	}


	@Register(value = {"flag"}, help = true)
	public void flagHelp(final @NotNull Player p, final @NotNull String... args) {
		RegionCommand.flagHelpInternal(p, args);
	}

	@Register(value = "flag", description = "Get the applicable values for the given flag.")
	public void oneArgFlag(final @NotNull Player p, final @NotNull Flag flag) {
		de.zeanon.testutils.regionsystem.commands.Flag.execute(null, flag, null, p);
	}

	@Register(value = "flag", description = "Set a value for the given flag.")
	public void twoArgsFlag(final @NotNull Player p, final @NotNull Flag flag, final @NotNull Flag.Value<?> value) {
		de.zeanon.testutils.regionsystem.commands.Flag.execute(null, flag, value, p);
	}

	@Register(value = "flag", description = "Set a value for the given flag in the given region.")
	public void threeArgsFlag(final @NotNull Player p, final @NotNull RegionName regionName, final @NotNull Flag flag, final @NotNull Flag.Value<?> value) {
		de.zeanon.testutils.regionsystem.commands.Flag.execute(regionName, flag, value, p);
	}

	@Register(value = "flag", description = "Set a value for the given flag in the given region.")
	public void threeArgsFlag(final @NotNull Player p, final @NotNull Flag flag, final @NotNull Flag.Value<?> value, final @NotNull RegionName regionName) {
		de.zeanon.testutils.regionsystem.commands.Flag.execute(regionName, flag, value, p);
	}


	@Register(value = {"delflag"}, help = true)
	@Register(value = {"removeflag"}, help = true)
	public void removeFlagHelp(final @NotNull Player p, final @NotNull String... args) {
		RegionCommand.flagHelpInternal(p, args);
	}

	@Register(value = "delflag", description = "Delete the given flag in the region you are currently standing in.")
	@Register(value = "removeflag", description = "Delete the given flag in the region you are currently standing in.")
	public void oneArgRemoveFlag(final @NotNull Player p, final @NotNull Flag flag) {
		de.zeanon.testutils.regionsystem.commands.RemoveFlag.execute(null, flag, p);
	}

	@Register(value = "delflag", description = "Delete the given flag in the given region.")
	@Register(value = "removeflag", description = "Delete the given flag in the given region.")
	public void twoArgsRemoveFlag(final @NotNull Player p, final @NotNull RegionName regionName, final @NotNull Flag flag) {
		de.zeanon.testutils.regionsystem.commands.RemoveFlag.execute(regionName, flag, p);
	}


	@Register(value = "reload", description = "Reload all regions from the file-system.")
	public void noArgsReload(final @NotNull Player p) {
		Reload.execute(p);
	}

	@Register(value = "save", description = "Save all region-data manually")
	public void noArgsSave(final @NotNull Player p) {
		Save.execute(p);
	}


	private static @NotNull String getFlagsHelpMessage() {
		return RegionCommand.MESSAGE_HEAD
			   + ChatColor.RED
			   + "Applicable flags are: "
			   + Arrays.stream(Flag.getFlags())
					   .map(flag -> ChatColor.DARK_RED + flag.toString())
					   .collect(Collectors.joining(ChatColor.RED + ", "));
	}

	private static void flagHelpInternal(final @NotNull Player p, final @NotNull String... args) {
		if (args.length > 1) {
			@Nullable Flag flag = Flag.getFlag(args[0]);
			if (flag != null) {
				de.zeanon.testutils.regionsystem.commands.Flag.execute(null, flag, null, p);
				return;
			}

			flag = Flag.getFlag(args[1]);
			if (flag != null) {
				if (!RegionManager.hasDefinedRegion(args[0]) && !RegionManager.hasGlobalRegion(args[0])) {
					p.sendMessage(RegionCommand.MESSAGE_HEAD
								  + ChatColor.RED
								  + "'"
								  + ChatColor.DARK_RED
								  + args[0]
								  + ChatColor.RED
								  + "' is not a valid region.");
					return;
				}
				de.zeanon.testutils.regionsystem.commands.Flag.execute(null, flag, null, p);
				return;
			}
		}

		if (args.length == 1 && !RegionManager.hasDefinedRegion(args[0]) && !RegionManager.hasGlobalRegion(args[0])) {
			p.sendMessage(RegionCommand.MESSAGE_HEAD
						  + ChatColor.RED
						  + "'"
						  + ChatColor.DARK_RED
						  + args[0]
						  + ChatColor.RED
						  + "' is not a valid region.");
			return;
		}

		p.sendMessage(RegionCommand.FLAGS_HELP_MESSAGE);
	}
}