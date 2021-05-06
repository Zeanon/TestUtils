package de.zeanon.testutils.plugin.commands.countingwand;

import de.steamwar.commandframework.SWCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class CountingwandCommand extends SWCommand {

	public CountingwandCommand() {
		super("countingwand", "zollstock", "/zollstock", "/countingwand", "cwand", "/cwand");
	}

	@Register
	public void genericCommand(final @NotNull Player p) {
		Countingwand.giveItem(p);
	}
}