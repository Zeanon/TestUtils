package de.zeanon.testutils.plugin.commands.testblock;

import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.plugin.utils.CommandRequestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.InternalFileUtils;
import de.zeanon.testutils.plugin.utils.enums.CommandConfirmation;
import de.zeanon.testutils.plugin.utils.enums.MappedFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class RenameBlock {

	public void execute(final @NotNull MappedFile oldMappedFile, final @NotNull MappedFile newMappedFile, final @Nullable CommandConfirmation confirmation, final @NotNull Player p) {
		final @NotNull Path filePath = TestBlock.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString());
		final @NotNull File oldFile = filePath.resolve(oldMappedFile.getName() + ".schem").toFile(); //NOSONAR
		final @NotNull File newFile = filePath.resolve(newMappedFile.getName() + ".schem").toFile(); //NOSONAR
		if (!oldFile.exists() || !oldFile.isDirectory()) {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.DARK_RED + oldMappedFile + ChatColor.RED + " does not exist.");
			return;
		}

		if (confirmation == null) {
			CommandRequestUtils.addRenameBlockRequest(p.getUniqueId(), oldMappedFile.getName());
			if (newFile.exists()) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.DARK_RED + newMappedFile + ChatColor.RED + " already exists, the file will be overwritten.");
			}
			GlobalMessageUtils.sendBooleanMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "Do you really want to rename "
												  + ChatColor.DARK_RED + oldMappedFile.getName()
												  + ChatColor.RED + "?",
												  "/tu renameblock " + oldMappedFile.getName() + " " + newMappedFile + " -confirm",
												  "/tu renameblock " + oldMappedFile.getName() + " " + newMappedFile + " -deny", p);
		} else {
			if (CommandRequestUtils.checkRenameBlockRequest(p.getUniqueId(), oldMappedFile.getName())) {
				CommandRequestUtils.removeRenameBlockRequest(p.getUniqueId());
				if (confirmation.confirm()) { //NOSONAR
					CommandRequestUtils.removeRenameBlockRequest(p.getUniqueId());
					if (oldFile.exists()) {
						RenameBlock.moveFile(p, oldMappedFile.getName(), oldFile, newFile);
					} else {
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.DARK_RED + oldMappedFile + ChatColor.RED + " does not exist.");
					}
				} else {
					CommandRequestUtils.removeRenameBlockRequest(p.getUniqueId());
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.DARK_RED + oldMappedFile + ChatColor.RED + " was not renamed.");
				}
			}
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

	public void usage(final @NotNull Player p) {
		GlobalMessageUtils.sendSuggestMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "Usage: ",
											  RenameBlock.usageMessage(),
											  RenameBlock.usageHoverMessage(),
											  RenameBlock.usageCommand(), p);
	}


	private void moveFile(final @NotNull Player p, final String fileName, final @NotNull File oldFile, final @NotNull File newFile) {
		try {
			if (newFile.exists()) {
				Files.delete(newFile.toPath());
			}

			FileUtils.moveFile(oldFile, newFile);

			final @Nullable String parentName = Objects.notNull(oldFile.getAbsoluteFile().getParentFile().listFiles()).length == 0
												? InternalFileUtils.deleteEmptyParent(oldFile, TestBlock.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString()).toFile())
												: null;

			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.DARK_RED + fileName + ChatColor.RED + " was renamed successfully.");

			if (parentName != null) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Folder " + ChatColor.DARK_RED + parentName + ChatColor.RED + " was deleted successfully due to being empty.");
			}
		} catch (IOException e) {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.DARK_RED + fileName + ChatColor.RED + " could not be renamed, for further information please see [console].");
			e.printStackTrace();
		}
	}
}