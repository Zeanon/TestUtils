package de.zeanon.testutils.plugin.commands.testutils;

import de.steamwar.commandframework.SWCommand;
import de.steamwar.commandframework.TypeMapper;
import de.zeanon.testutils.plugin.commands.testutils.testarea.*;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.GlobalRequestUtils;
import de.zeanon.testutils.plugin.utils.enums.AreaName;
import de.zeanon.testutils.plugin.utils.enums.CommandConfirmation;
import de.zeanon.testutils.plugin.utils.enums.GlobalToggle;
import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import java.nio.file.Path;
import java.util.Arrays;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class TestUtilsCommand extends SWCommand {

	public static final @NotNull Path TESTAREA_FOLDER;

	static {
		TESTAREA_FOLDER = de.zeanon.testutils.TestUtils.getPluginFolder().resolve("TestAreas");
	}

	public TestUtilsCommand() {
		super(new Prefix("testutils"), "testutils", "tu");
	}

	public static TypeMapper<CommandConfirmation> getCommandConfirmationMapper() {
		return new TypeMapper<CommandConfirmation>() {
			@Override
			public CommandConfirmation map(final @NotNull String[] previousArguments, final @NotNull String s) {
				return CommandConfirmation.map(s);
			}

			@Override
			public java.util.List<String> tabCompletes(final @NotNull CommandSender commandSender, final @NotNull String[] previousArguments, final @NotNull String arg) {
				final @NotNull java.util.List<String> tabCompletions = Arrays.asList("-confirm", "-deny");
				if (commandSender instanceof Player && previousArguments.length > 0) {
					final @NotNull Player p = (Player) commandSender;
					if (previousArguments[0].equalsIgnoreCase("update")
						&& GlobalRequestUtils.checkUpdateRequest(p.getUniqueId())) {
						return tabCompletions;
					} else {
						return null;
					}
				} else {
					return null;
				}
			}
		};
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


	@Register(value = "undo", description = "Undo your last action.")
	public void noArgsUndo(final @NotNull Player p) {
		Undo.undo(p);
	}


	@Register(value = "redo", description = "Redo the last undone action.")
	public void noArgsRedo(final @NotNull Player p) {
		Redo.redo(p);
	}


	@Register(value = {"count"}, help = true)
	public void countHelp(final @NotNull Player p, final @NotNull String... args) {
		if (args.length == 0) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Missing argument for "
						  + ChatColor.YELLOW + "<"
						  + ChatColor.DARK_RED + "block"
						  + ChatColor.YELLOW + ">");
		}
	}

	@Register(value = "count", description = "Count the amount of the given blocks in the region you are currently standing in.")
	public void oneArgCount(final @NotNull Player p, final @NotNull Material material) {
		Count.execute(p, material, RegionSide.NONE);
	}

	@Register("count")
	public void twoArgsCount(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull Material material) {
		Count.execute(p, material, regionSide);
	}

	@Register("count")
	public void twoArgsCount(final @NotNull Player p, final @NotNull Material material, final @NotNull RegionSide regionSide) {
		Count.execute(p, material, regionSide);
	}


	@Register("registerreset")
	public void noArgsRegisterReset(final @NotNull Player p) {
		RegisterReset.execute(p);
	}


	@Register("resetarea")
	public void noArgsResetArea(final @NotNull Player p) {
		ResetArea.execute(p, RegionSide.NONE);
	}

	@Register("resetarea")
	public void oneArgResetArea(final @NotNull Player p, final @NotNull RegionSide regionSide) {
		ResetArea.execute(p, regionSide);
	}


	@Register("invarea")
	@Register("invertarea")
	public void noArgsInvertArea(final @NotNull Player p) {
		InvertArea.execute(p, RegionSide.NONE);
	}

	@Register("invarea")
	@Register("invertarea")
	public void oneArgInvertArea(final @NotNull Player p, final @NotNull RegionSide regionSide) {
		InvertArea.execute(p, regionSide);
	}


	@Register("reparea")
	@Register("replacearea")
	public void noArgsReplaceArea(final @NotNull Player p) {
		ReplaceArea.execute(p, RegionSide.NONE, Material.OBSIDIAN, Material.TNT);
	}

	@Register("reparea")
	@Register("replacearea")
	public void oneArgReplaceArea(final @NotNull Player p, final @NotNull RegionSide regionSide) {
		ReplaceArea.execute(p, regionSide, Material.OBSIDIAN, Material.TNT);
	}

	@Register("reparea")
	@Register("replacearea")
	public void twoArgsReplaceArea(final @NotNull Player p, final @NotNull Material source, final @NotNull Material destination) {
		ReplaceArea.execute(p, RegionSide.NONE, source, destination);
	}

	@Register("reparea")
	@Register("replacearea")
	public void threeArgsReplaceArea(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull Material source, final @NotNull Material destination) {
		ReplaceArea.execute(p, regionSide, source, destination);
	}

	@Register("reparea")
	@Register("replacearea")
	public void threeArgsReplaceArea(final @NotNull Player p, final @NotNull Material source, final @NotNull Material destination, final @NotNull RegionSide regionSide) {
		ReplaceArea.execute(p, regionSide, source, destination);
	}


	@Register("reptnt")
	@Register("replacetnt")
	public void noArgsReplaceTNT(final @NotNull Player p) {
		ReplaceArea.execute(p, RegionSide.NONE, Material.TNT, Material.OBSIDIAN);
	}

	@Register("reptnt")
	@Register("replacetnt")
	public void oneArgReplaceTNT(final @NotNull Player p, final @NotNull RegionSide regionSide) {
		ReplaceArea.execute(p, regionSide, Material.TNT, Material.OBSIDIAN);
	}


	@Register(value = "registerarea", help = true)
	public void registerAreaHelp(final @NotNull Player p, final @NotNull String... args) {
		//TODO
	}

	@Register("registerarea")
	public void noArgsRegisterArea(final @NotNull Player p) {
		RegisterArea.execute(p, null);
	}

	@Register("registerarea")
	public void oneArgRegisterArea(final @NotNull Player p, final @NotNull AreaName areaName) {
		RegisterArea.execute(p, areaName);
	}


	@Register(value = "delarea", help = true)
	@Register(value = "deletearea", help = true)
	public void deleteAreaHelp(final @NotNull Player p, final @NotNull String... args) {
		//TODO
	}

	@Register("delarea")
	@Register("deletearea")
	public void oneArgDeleteArea(final @NotNull Player p, final @NotNull AreaName areaName) {
		DeleteArea.execute(p, areaName);
	}


	@Register(value = "warp", help = true)
	public void warpHelp(final @NotNull Player p, final @NotNull String... args) {
		//TODO
	}

	@Register("warp")
	public void oneArgWarp(final @NotNull Player p, final @NotNull AreaName areaName) {
		Warp.execute(p, areaName);
	}


	@Register(value = "back", help = true)
	public void backHelp(final @NotNull Player p, final @NotNull String... args) {
		//TODO
	}

	@Register("back")
	public void oneArgBack(final @NotNull Player p) {
		Warp.back(p);
	}


	@Register("removeentities")
	public void noArgsRemoveEntities(final @NotNull Player p) {
		RemoveEntities.execute(p, RegionSide.NONE, false);
	}

	@Register("removeentities")
	public void oneArgRemoveEntities(final @NotNull Player p, final @NotNull RegionSide regionSide) {
		RemoveEntities.execute(p, regionSide, false);
	}

	@Register("removeentities")
	public void oneArgRemoveEntities(final @NotNull Player p, final @NotNull GlobalToggle globalToggle) {
		RemoveEntities.execute(p, RegionSide.NONE, true);
	}


	@ClassMapper(value = CommandConfirmation.class, local = true)
	private @NotNull TypeMapper<CommandConfirmation> mapCommandConfirmation() {
		return TestUtilsCommand.getCommandConfirmationMapper();
	}
}