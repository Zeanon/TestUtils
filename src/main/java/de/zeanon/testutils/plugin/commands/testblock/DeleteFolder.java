package de.zeanon.testutils.plugin.commands.testblock;

import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.plugin.utils.CommandRequestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.InternalFileUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.CommandConfirmation;
import de.zeanon.testutils.plugin.utils.enums.MappedFolder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class DeleteFolder {

	public void execute(final @NotNull MappedFolder mappedFolder, final @Nullable CommandConfirmation confirmation, final @NotNull Player p) {
		if (TestAreaUtils.illegalName(mappedFolder.getName())) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Folder '" + mappedFolder.getName() + "' resolution error: Name is not allowed.");
			return;
		}

		final @NotNull Path filePath = TestBlockCommand.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString());
		final @NotNull File file = filePath.resolve(mappedFolder.getName()).toFile();
		if (!file.exists() || !file.isDirectory()) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.DARK_RED + mappedFolder + ChatColor.RED + " does not exist.");
			return;
		}

		if (confirmation == null) {
			CommandRequestUtils.addDeleteFolderRequest(p.getUniqueId(), mappedFolder.getName());
			GlobalMessageUtils.sendBooleanMessage(GlobalMessageUtils.MESSAGE_HEAD
												  + ChatColor.RED + "Do you really want to delete "
												  + ChatColor.DARK_RED + mappedFolder.getName()
												  + ChatColor.RED + "?",
												  "/tu deletefolder " + mappedFolder.getName() + " -confirm",
												  "/tu deletefolder " + mappedFolder.getName() + " -deny", p);
			return;
		}

		if (CommandRequestUtils.checkDeleteFolderRequest(p.getUniqueId(), mappedFolder.getName())) {
			CommandRequestUtils.removeDeleteFolderRequest(p.getUniqueId());
			if (confirmation.confirm()) { //NOSONAR
				try {
					FileUtils.deleteDirectory(file);
					final @Nullable String parentName = Objects.notNull(file.getAbsoluteFile().getParentFile().listFiles()).length == 0
														? InternalFileUtils.deleteEmptyParent(file, filePath.toFile())
														: null;

					p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
								  + ChatColor.DARK_RED + mappedFolder + ChatColor.RED + " was deleted successfully.");

					if (parentName != null) {
						p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
									  + ChatColor.RED + "Folder "
									  + ChatColor.GREEN + parentName
									  + ChatColor.RED + " was deleted successfully due to being empty.");
					}
				} catch (final IOException e) {
					p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
								  + ChatColor.DARK_RED + mappedFolder + ChatColor.RED + " could not be deleted, for further information please see [console].");
					e.printStackTrace();
				}
			} else {
				CommandRequestUtils.removeDeleteFolderRequest(p.getUniqueId());
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.DARK_RED + mappedFolder + ChatColor.RED + " was not deleted.");
			}
		}
	}

	public @NotNull String usageMessage() {
		return ChatColor.GRAY + "/testutils"
			   + ChatColor.AQUA + " deletefolder "
			   + ChatColor.YELLOW + "<"
			   + ChatColor.DARK_RED + "foldername"
			   + ChatColor.YELLOW + ">";
	}

	public @NotNull String usageHoverMessage() {
		return ChatColor.RED + "e.g. "
			   + ChatColor.GRAY + "/testutils"
			   + ChatColor.AQUA + " deletefolder "
			   + ChatColor.DARK_RED + "example";
	}

	public @NotNull String usageCommand() {
		return "/testutils deletefolder ";
	}

	public void usage(final @NotNull Player p) {
		GlobalMessageUtils.sendSuggestMessage(GlobalMessageUtils.MESSAGE_HEAD
											  + ChatColor.RED + "Usage: ",
											  DeleteFolder.usageMessage(),
											  DeleteFolder.usageHoverMessage(),
											  DeleteFolder.usageCommand(), p);
	}
}