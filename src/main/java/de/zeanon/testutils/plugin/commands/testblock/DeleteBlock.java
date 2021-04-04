package de.zeanon.testutils.plugin.commands.testblock;

import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.init.InitMode;
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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class DeleteBlock {

	public void execute(final @NotNull MappedFile mappedFile, final @Nullable CommandConfirmation confirmation, final @NotNull Player p) {
		if (mappedFile.getName().contains("./") || mappedFile.getName().contains(".\\") || InitMode.forbiddenFileName(mappedFile.getName())) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Block '" + mappedFile.getName() + "' resolution error: Name is not allowed.");
			return;
		}

		final @NotNull Path filePath = TestBlock.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString());
		final @NotNull File file = filePath.resolve(mappedFile + ".schem").toFile();
		if (!file.exists() || !file.isDirectory()) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.DARK_RED + mappedFile + ChatColor.RED + " does not exist.");
			return;
		}

		if (confirmation == null) {
			CommandRequestUtils.addDeleteBlockRequest(p.getUniqueId(), mappedFile.getName());
			GlobalMessageUtils.sendBooleanMessage(GlobalMessageUtils.MESSAGE_HEAD
												  + ChatColor.RED + "Do you really want to delete "
												  + ChatColor.DARK_RED + mappedFile.getName()
												  + ChatColor.RED + "?",
												  "/tu deleteblock " + mappedFile.getName() + " -confirm",
												  "/tu deleteblock " + mappedFile.getName() + " -deny", p);
			return;
		}

		if (CommandRequestUtils.checkDeleteBlockRequest(p.getUniqueId(), mappedFile.getName())) {
			CommandRequestUtils.removeDeleteBlockRequest(p.getUniqueId());
			if (confirmation.confirm()) { //NOSONAR
				try {
					Files.delete(file.toPath());
					final @Nullable String parentName = Objects.notNull(file.getAbsoluteFile().getParentFile().listFiles()).length == 0
														? InternalFileUtils.deleteEmptyParent(file, filePath.toFile())
														: null;

					p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
								  + ChatColor.DARK_RED + mappedFile + ChatColor.RED + " was deleted successfully.");

					if (parentName != null) {
						p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
									  + ChatColor.RED + "Folder "
									  + ChatColor.DARK_RED + parentName
									  + ChatColor.RED + " was deleted successfully due to being empty.");
					}
				} catch (IOException e) {
					p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
								  + ChatColor.DARK_RED + mappedFile + ChatColor.RED + " could not be deleted, for further information please see [console].");
					e.printStackTrace();
				}
			} else {
				CommandRequestUtils.removeDeleteBlockRequest(p.getUniqueId());
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.DARK_RED + mappedFile + ChatColor.RED + " was not deleted.");
			}
		}
	}

	public @NotNull String usageMessage() {
		return ChatColor.GRAY + "/testutils"
			   + ChatColor.AQUA + " deleteblock "
			   + ChatColor.YELLOW + "<"
			   + ChatColor.DARK_RED + "filename"
			   + ChatColor.YELLOW + ">";
	}

	public @NotNull String usageHoverMessage() {
		return ChatColor.RED + "e.g. "
			   + ChatColor.GRAY + "/testutils"
			   + ChatColor.AQUA + " deleteblock "
			   + ChatColor.DARK_RED + "example";
	}

	public @NotNull String usageCommand() {
		return "/testutils deleteblock ";
	}

	public void usage(final @NotNull Player p) {
		GlobalMessageUtils.sendSuggestMessage(GlobalMessageUtils.MESSAGE_HEAD
											  + ChatColor.RED + "Usage: ",
											  DeleteBlock.usageMessage(),
											  DeleteBlock.usageHoverMessage(),
											  DeleteBlock.usageCommand(), p);
	}
}