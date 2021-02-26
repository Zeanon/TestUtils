package de.zeanon.testutils.plugin.commands.testblock;

import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.CommandRequestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.InternalFileUtils;
import java.io.File;
import java.io.IOException;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class DeleteFolder {

	public void execute(final @NotNull String @NotNull [] args, final @NotNull Player p) {
		if (args.length <= 3) {
			if (args.length < 1) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Missing argument for "
							  + ChatColor.YELLOW + "<"
							  + ChatColor.GREEN + "filename"
							  + ChatColor.YELLOW + ">");
				DeleteFolder.usage(p);
			} else if (args[1].contains("./")) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "File '" + args[1] + "' resolution error: Path is not allowed.");
				DeleteFolder.usage(p);
			} else if (args.length == 3
					   && !CommandRequestUtils.checkDeleteFolderRequest(p.getUniqueId().toString(), args[1])
					   && !args[2].equalsIgnoreCase("confirm")
					   && !args[2].equalsIgnoreCase("deny")) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Too many arguments.");
				DeleteFolder.usage(p);
			} else {
				DeleteFolder.executeInternally(p, args);
			}
		} else {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Too many arguments.");
			DeleteFolder.usage(p);
		}
	}

	public @NotNull String usageMessage() {
		return ChatColor.GRAY + "/testutils"
			   + ChatColor.AQUA + " deletefolder "
			   + ChatColor.YELLOW + "<"
			   + ChatColor.GREEN + "foldername"
			   + ChatColor.YELLOW + ">";
	}

	public @NotNull String usageHoverMessage() {
		return ChatColor.RED + "e.g. "
			   + ChatColor.GRAY + "/testutils"
			   + ChatColor.AQUA + " deletefolder "
			   + ChatColor.GREEN + "example";
	}

	public @NotNull String usageCommand() {
		return "/testutils deletefolder ";
	}

	private void executeInternally(final @NotNull Player p, final @NotNull String @NotNull [] args) {
		final @NotNull File file = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString() + "/" + args[0]); //NOSONAR

		if (args.length == 3) {
			if (file.exists() && file.isDirectory()) {
				if (Objects.notNull(file.listFiles()).length > 0) {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.GREEN + args[1]
								  + ChatColor.RED + " still contains files.");
				}
				GlobalMessageUtils.sendBooleanMessage(GlobalMessageUtils.messageHead
													  + ChatColor.RED + "Do you really want to delete "
													  + ChatColor.GREEN + args[1]
													  + ChatColor.RED + "?",
													  "/tu " + args[1] + " confirm",
													  "/tu " + args[1] + " deny", p);
				CommandRequestUtils.addDeleteFolderRequest(p.getUniqueId().toString(), args[1]);
			} else {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.GREEN + args[1] + ChatColor.RED + " does not exist.");
			}
		} else if (args.length == 4 && CommandRequestUtils.checkDeleteFolderRequest(p.getUniqueId().toString(), args[1])) {
			if (args[2].equalsIgnoreCase("confirm")) {
				CommandRequestUtils.removeDeleteFolderRequest(p.getUniqueId().toString());
				if (file.exists() && file.isDirectory()) {
					try {
						FileUtils.deleteDirectory(file);
						final @Nullable String parentName = Objects.notNull(file.getAbsoluteFile().getParentFile().listFiles()).length == 0
															? InternalFileUtils.deleteEmptyParent(file, new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString()))
															: null;

						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.GREEN + args[1] +
									  ChatColor.RED + " was deleted successfully.");
						if (parentName != null) {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "Folder " + ChatColor.GREEN + parentName + ChatColor.RED + " was deleted successfully due to being empty.");
						}
					} catch (IOException e) {
						e.printStackTrace();
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.GREEN + args[1] + ChatColor.RED + " could not be deleted, for further information please see [console].");
					}
				} else {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.GREEN + args[1] + ChatColor.RED + " does not exist.");
				}
			} else if (args[2].equalsIgnoreCase("deny")) {
				CommandRequestUtils.removeDeleteFolderRequest(p.getUniqueId().toString());
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.GREEN + args[1] + ChatColor.RED + " was not deleted.");
			}
		}
	}

	private void usage(final @NotNull Player p) {
		GlobalMessageUtils.sendSuggestMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "Usage: ",
											  DeleteFolder.usageMessage(),
											  DeleteFolder.usageHoverMessage(),
											  DeleteFolder.usageCommand(), p);
	}
}
