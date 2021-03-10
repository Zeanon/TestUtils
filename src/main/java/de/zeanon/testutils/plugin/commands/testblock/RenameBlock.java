package de.zeanon.testutils.plugin.commands.testblock;

import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.CommandRequestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.InternalFileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class RenameBlock {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length <= 4) {
			if (args.length < 2) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Missing argument for "
							  + ChatColor.YELLOW + "<"
							  + ChatColor.GOLD + "filename"
							  + ChatColor.YELLOW + ">");
				RenameBlock.usage(p);
			} else if (args[1].contains("./") || args.length >= 4 && args[2].contains("./")) {
				String name = args[1].contains("./") ? args[1] : args[2];
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "File '" + name + "' resolution error: Path is not allowed.");
				RenameBlock.usage(p);
			} else if (args.length == 4 && !CommandRequestUtils.checkRenameRequest(p.getUniqueId().toString(), args[1])
					   && !args[2].equalsIgnoreCase("confirm")
					   && !args[2].equalsIgnoreCase("deny")) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Too many arguments.");
				RenameBlock.usage(p);
			} else {
				RenameBlock.executeInternally(p, args);
			}
		} else {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Too many arguments.");
			RenameBlock.usage(p);
		}
	}

	public @NotNull String usageMessage() {
		return ChatColor.GRAY + "/testutils"
			   + ChatColor.AQUA + " renameblock "
			   + ChatColor.YELLOW + "<"
			   + ChatColor.GOLD + "filename"
			   + ChatColor.YELLOW + "> <"
			   + ChatColor.GOLD + "newname"
			   + ChatColor.YELLOW + ">";
	}

	public @NotNull String usageHoverMessage() {
		return ChatColor.RED + "e.g. "
			   + ChatColor.GRAY + "/testutils"
			   + ChatColor.AQUA + " renameblock "
			   + ChatColor.GOLD + "example newname";
	}

	public @NotNull String usageCommand() {
		return "/testutils renameblock ";
	}

	private void executeInternally(final @NotNull Player p, final @NotNull String @NotNull [] args) {
		final @NotNull File oldFile = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString() + "/" + args[1] + ".schem");
		final @NotNull File newFile = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString() + "/" + args[2] + ".schem");

		if (args.length == 3) {
			if (oldFile.exists()) {
				if (newFile.exists()) {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.GOLD + args[2] + ChatColor.RED + " already exists, the file will be overwritten.");
				}

				GlobalMessageUtils.sendBooleanMessage(GlobalMessageUtils.messageHead
													  + ChatColor.RED + "Do you really want to rename " + ChatColor.GOLD + args[1] + ChatColor.RED + "?",
													  "/tu renameblock " + args[1] + " " + args[2] + " confirm",
													  "/tu renameblock " + args[1] + " " + args[2] + " deny", p);
				CommandRequestUtils.addRenameRequest(p.getUniqueId().toString(), args[1]);
			} else {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.GOLD + args[1] + ChatColor.RED + " does not exist.");
			}
		} else if (args.length == 4 && CommandRequestUtils.checkRenameRequest(p.getUniqueId().toString(), args[1])) {
			if (args[3].equalsIgnoreCase("confirm")) {
				CommandRequestUtils.removeRenameRequest(p.getUniqueId().toString());
				if (oldFile.exists()) {
					RenameBlock.moveFile(p, args[1], oldFile, newFile);
				} else {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.GOLD + args[1] + ChatColor.RED + " does not exist.");
				}
			} else if (args[3].equalsIgnoreCase("deny")) {
				CommandRequestUtils.removeRenameRequest(p.getUniqueId().toString());
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.GOLD + args[1] + ChatColor.RED + " was not renamed.");
			}
		}
	}

	private void moveFile(final @NotNull Player p, final String fileName, final @NotNull File oldFile, final @NotNull File newFile) {
		try {
			if (newFile.exists()) {
				Files.delete(newFile.toPath());
			}

			FileUtils.moveFile(oldFile, newFile);

			final @Nullable String parentName = Objects.notNull(oldFile.getAbsoluteFile().getParentFile().listFiles()).length == 0
												? InternalFileUtils.deleteEmptyParent(oldFile, new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString()))
												: null;

			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.GOLD + fileName + ChatColor.RED + " was renamed successfully.");

			if (parentName != null) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Folder " + ChatColor.GREEN + parentName + ChatColor.RED + " was deleted successfully due to being empty.");
			}
		} catch (IOException e) {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.GOLD + fileName + ChatColor.RED + " could not be renamed, for further information please see [console].");
			e.printStackTrace();
		}
	}

	private void usage(final @NotNull Player p) {
		GlobalMessageUtils.sendSuggestMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "Usage: ",
											  RenameBlock.usageMessage(),
											  RenameBlock.usageHoverMessage(),
											  RenameBlock.usageCommand(), p);
	}
}