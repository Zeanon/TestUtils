package de.zeanon.testutils.plugin.commands.region;

import de.zeanon.testutils.commandframework.SWCommand;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.enums.Flag;
import de.zeanon.testutils.plugin.utils.enums.RegionName;
import de.zeanon.testutils.plugin.utils.region.DefinedRegion;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class Region extends SWCommand {

	public Region() {
		super("region", "rg");
	}

	public static void sendMultipleRegions(final @NotNull List<DefinedRegion> regions, final @NotNull Player p) {
		p.sendMessage(GlobalMessageUtils.messageHead
					  + ChatColor.RED + "You are standing in multiple regions, please define which one to use: "
					  + ChatColor.DARK_RED + regions.toString());
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
		p.sendMessage(GlobalMessageUtils.messageHead
					  + ChatColor.RED + "Applicable flags are: "
					  + ChatColor.DARK_RED + "tnt"
					  + ChatColor.RED + ", "
					  + ChatColor.DARK_RED + "item_drops"
					  + ChatColor.RED + ", "
					  + ChatColor.DARK_RED + "fire"
					  + ChatColor.RED + ", "
					  + ChatColor.DARK_RED + "leaves_decay");
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
}