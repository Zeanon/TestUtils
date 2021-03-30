package de.zeanon.testutils.plugin.commands.region;

import de.zeanon.testutils.commandframework.SWCommand;
import de.zeanon.testutils.plugin.utils.enums.Flag;
import de.zeanon.testutils.plugin.utils.enums.RegionName;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class Region extends SWCommand {

	public Region() {
		super("region", "rg");
	}


	@Register(help = true)
	public void noArgsHelp(final @NotNull Player p, final @NotNull String... args) {
		p.sendMessage("help"); //TODO
	}


	@Register("info")
	public void noArgsRegion(final @NotNull Player p) {
		Info.execute(null, p);
	}

	@Register("info")
	public void oneArgRegion(final @NotNull Player p, final @NotNull RegionName regionName) {
		Info.execute(regionName, p);
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
	public void threeArgsFlag(final @NotNull Player p, final @NotNull RegionName flag, final @NotNull Flag.Value<?> value) {
		//de.zeanon.testutils.plugin.commands.region.Flag.execute(regionName., flag, value, p);
	}
}