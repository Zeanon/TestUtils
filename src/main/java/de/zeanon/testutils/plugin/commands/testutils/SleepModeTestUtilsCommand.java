package de.zeanon.testutils.plugin.commands.testutils;

import de.steamwar.commandframework.SWCommand;
import de.steamwar.commandframework.TypeMapper;
import de.zeanon.testutils.plugin.utils.enums.CommandConfirmation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class SleepModeTestUtilsCommand extends SWCommand {

	public SleepModeTestUtilsCommand() {
		super(new Prefix("testutils"), "testutils", "tu");
	}


	@Register(help = true)
	public void help(final @NotNull Player p, final @NotNull String... args) {
		//TODO
	}

	@Register(value = {"update"}, help = true, description = "Help me.")
	public void updateHelp(final @NotNull Player p, final @NotNull String... args) {
		Update.sendUpdateUsage(p);
	}

	@Register(value = "update", description = "Update the plugin.")
	public void noArgsUpdate(final @NotNull Player p) {
		Update.execute(null, p);
	}

	@Register(value = "update", description = "Confirm the update.")
	public void oneArgUpdate(final @NotNull Player p, final @NotNull CommandConfirmation confirmation) {
		Update.execute(confirmation, p);
	}


	@ClassMapper(value = CommandConfirmation.class, local = true)
	private @NotNull TypeMapper<CommandConfirmation> mapCommandConfirmation() {
		return TestUtilsCommand.getCommandConfirmationMapper();
	}
}