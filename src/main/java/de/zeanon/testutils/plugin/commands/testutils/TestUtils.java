package de.zeanon.testutils.plugin.commands.testutils;

import de.zeanon.testutils.plugin.commands.testblock.*;
import de.zeanon.testutils.plugin.commands.testutils.testarea.*;
import de.zeanon.testutils.plugin.utils.region.DefinedRegion;
import de.zeanon.testutils.plugin.utils.region.RegionManager;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class TestUtils {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("undo")) {
				Undo.undo(p);
			} else if (args[0].equalsIgnoreCase("redo")) {
				Redo.redo(p);
			} else if (args[0].equalsIgnoreCase("count")) {
				Count.execute(args, p);
			} else if (args[0].equalsIgnoreCase("registerblock")) {
				RegisterBlock.execute(args, p);
			} else if (args[0].equalsIgnoreCase("registerreset")) {
				RegisterReset.execute(args, p);
			} else if (args[0].equalsIgnoreCase("resetarea")) {
				ResetArea.execute(args, p);
			} else if (args[0].equalsIgnoreCase("invertarea")) {
				InvertArea.execute(args, p);
			} else if (args[0].equalsIgnoreCase("replacearea")) {
				ReplaceArea.execute(args, p, true);
			} else if (args[0].equalsIgnoreCase("replacetnt")) {
				ReplaceArea.execute(args, p, false);
			} else if (args[0].equalsIgnoreCase("deleteblock") || args[0].equalsIgnoreCase("delblock")) {
				DeleteBlock.execute(args, p);
			} else if (args[0].equalsIgnoreCase("deletefolder") || args[0].equalsIgnoreCase("delfolder")) {
				DeleteFolder.execute(args, p);
			} else if (args[0].equalsIgnoreCase("renameblock")) {
				RenameBlock.execute(args, p);
			} else if (args[0].equalsIgnoreCase("renamefolder")) {
				RenameFolder.execute(args, p);
			} else if (args[0].equalsIgnoreCase("registerarea")) {
				RegisterArea.execute(args, p);
			} else if (args[0].equalsIgnoreCase("deletearea") || args[0].equalsIgnoreCase("delarea")) {
				DeleteArea.execute(args, p);
			} else if (args[0].equalsIgnoreCase("update")) {
				Update.execute(args, p);
			} else if (args[0].equalsIgnoreCase("convert")) {
				for (DefinedRegion region : RegionManager.getRegions()) {
					region.setFire(false);
				}
			} else {
				p.sendMessage(ChatColor.DARK_AQUA + "Invalid sub-command '" + ChatColor.GOLD + args[0] + "'.");
			}
		} else {
			p.sendMessage(ChatColor.DARK_AQUA + "Missing argument.");
		}
	}
}