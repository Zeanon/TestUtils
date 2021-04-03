package de.zeanon.testutils.plugin.commands.region;

import de.zeanon.testutils.commandframework.SWCommand;
import de.zeanon.testutils.plugin.utils.enums.RegionName;
import de.zeanon.testutils.regionsystem.flags.Flag;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import de.zeanon.testutils.regionsystem.region.RegionManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class Region extends SWCommand {


	public static final @NotNull String MESSAGE_HEAD = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "RegionManager" + ChatColor.DARK_GRAY + "] ";
	private static final @NotNull String FLAGS_HELP_MESSAGE = Region.getFlagsHelpMessage();

	public Region() {
		super("region", true, "rg");
	}

	public static void sendMultipleRegions(final @NotNull List<DefinedRegion> regions, final @NotNull Player p) {
		p.sendMessage(Region.MESSAGE_HEAD
					  + ChatColor.RED + "You are standing in multiple regions, please define which one to use: "
					  + regions.stream().map(r -> ChatColor.DARK_RED + r.getName()).collect(Collectors.joining(ChatColor.RED + ", ")));
	}

	@Register(help = true)
	public void help(final @NotNull Player p, final @NotNull String... args) {
		//TODO
	}

	@Register("info")
	public void noArgsRegion(final @NotNull Player p) {
		Info.execute(null, p);
	}

	@Register("info")
	public void oneArgRegion(final @NotNull Player p, final @NotNull RegionName regionName) {
		Info.execute(regionName, p);
	}

	@Register(value = {"flag"}, help = true)
	public void flagHelp(final @NotNull Player p, final @NotNull String... args) {
		if (args.length > 1) {
			final Optional<Flag> flag = Flag.getFlags().stream().filter(f -> args[1].equalsIgnoreCase(f.name())).findFirst();
			if (flag.isPresent()) {
				if (!RegionManager.hasRegion(args[0])) {
					p.sendMessage(Region.MESSAGE_HEAD
								  + ChatColor.RED
								  + "'"
								  + ChatColor.DARK_RED
								  + args[0]
								  + ChatColor.RED
								  + "' is not a valid region.");
					return;
				}
				de.zeanon.testutils.plugin.commands.region.Flag.execute(null, flag.get(), null, p);
				return;
			}
		}

		if (args.length == 1) {
			final Optional<Flag> flag = Flag.getFlags().stream().filter(f -> args[0].equalsIgnoreCase(f.name())).findFirst();
			if (flag.isPresent()) {
				de.zeanon.testutils.plugin.commands.region.Flag.execute(null, flag.get(), null, p);
				return;
			}
		}

		p.sendMessage(Region.FLAGS_HELP_MESSAGE);
	}

	@Register("flag")
	public void oneArgFlag(final @NotNull Player p, final @NotNull Flag flag) {
		de.zeanon.testutils.plugin.commands.region.Flag.execute(null, flag, null, p);
	}

	@Register("flag")
	public void twoArgsFlag(final @NotNull Player p, final @NotNull Flag flag, final @NotNull Flag.Value<?> value) {
		de.zeanon.testutils.plugin.commands.region.Flag.execute(null, flag, value, p);
	}

	@Register("flag")
	public void twoArgsFlag(final @NotNull Player p, final @NotNull RegionName regionName, final @NotNull Flag flag) {
		de.zeanon.testutils.plugin.commands.region.Flag.execute(regionName, flag, null, p);
	}

	@Register("flag")
	public void threeArgsFlag(final @NotNull Player p, final @NotNull RegionName regionName, final @NotNull Flag flag, final @NotNull Flag.Value<?> value) {
		de.zeanon.testutils.plugin.commands.region.Flag.execute(regionName, flag, value, p);
	}

	@Register("flag")
	public void threeArgsFlag(final @NotNull Player p, final @NotNull Flag flag, final @NotNull Flag.Value<?> value, final @NotNull RegionName regionName) {
		de.zeanon.testutils.plugin.commands.region.Flag.execute(regionName, flag, value, p);
	}

	@Register("reload")
	public void noArgsReload(final @NotNull Player p) {
		Reload.execute(p);
	}

	@Register("save")
	public void test(final @NotNull Player p) {
		RegionManager.saveRegions();
	}

	private static @NotNull String getFlagsHelpMessage() {
		return Region.MESSAGE_HEAD
			   + ChatColor.RED
			   + "Applicable flags are: "
			   + Flag.getFlags()
					 .stream()
					 .map(f -> ChatColor.DARK_RED + f.toString())
					 .collect(Collectors.joining(ChatColor.RED + ", "));
	}
}