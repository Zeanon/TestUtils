package de.zeanon.testutils.plugin.commands.backup;

import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.CommandRequestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.CommandConfirmation;
import de.zeanon.testutils.plugin.utils.enums.MappedFile;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Delete {

	public void execute(final @NotNull MappedFile mappedFile, final @Nullable CommandConfirmation confirmation, final @NotNull Player p) {
		if (TestAreaUtils.illegalName(mappedFile.getName())) {
			p.sendMessage(BackupCommand.MESSAGE_HEAD
						  + ChatColor.RED + "Backup '" + mappedFile.getName() + "' resolution error: Name is not allowed.");
			return;
		}

		if (confirmation == null) {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
			final @Nullable DefinedRegion otherRegion = TestAreaUtils.getSouthRegion(p);

			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				return;
			}

			final @NotNull File folder = BackupCommand.BACKUP_FOLDER.resolve(tempRegion.getName().substring(0, tempRegion.getName().length() - 6)).resolve("manual").resolve(p.getUniqueId().toString()).resolve(mappedFile.getName()).toFile();
			if (folder.exists() && folder.isDirectory()) {
				CommandRequestUtils.addDeleteBackupRequest(p.getUniqueId(), mappedFile.getName(), tempRegion.getName().substring(0, tempRegion.getName().length() - 6));
				GlobalMessageUtils.sendBooleanMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
													  ChatColor.RED + "Do you really want to delete "
													  + ChatColor.DARK_RED + mappedFile.getName()
													  + ChatColor.RED + "?",
													  "/backup delete " + mappedFile.getName() + " -confirm",
													  "/backup delete " + mappedFile.getName() + " -deny", p);
			} else {
				p.sendMessage(BackupCommand.MESSAGE_HEAD
							  + ChatColor.DARK_RED + mappedFile.getName() + ChatColor.RED + " does not exist.");
			}
		} else {
			final @Nullable String region = CommandRequestUtils.checkDeleteBackupRequest(p.getUniqueId(), mappedFile.getName());
			CommandRequestUtils.removeDeleteBackupRequest(p.getUniqueId());
			if (region != null) {
				if (confirmation.confirm()) { //NOSONAR
					final @Nullable DefinedRegion tempRegion = TestAreaUtils.getNorthRegion(region);
					final @Nullable DefinedRegion otherRegion = TestAreaUtils.getSouthRegion(region);
					if (tempRegion == null || otherRegion == null) {
						Delete.sendNotApplicableRegion(p);
					} else {
						final @NotNull File folder = BackupCommand.BACKUP_FOLDER.resolve(region).resolve("manual").resolve(p.getUniqueId().toString()).resolve(mappedFile.getName()).toFile();
						if (folder.exists() && folder.isDirectory()) {
							try {
								FileUtils.deleteDirectory(folder);
								p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
											  + ChatColor.DARK_RED + mappedFile + ChatColor.RED + " was deleted successfully.");
							} catch (final IOException e) {
								p.sendMessage(BackupCommand.MESSAGE_HEAD
											  + ChatColor.DARK_RED + mappedFile.getName() + ChatColor.RED + " could not be deleted, for further information please see " + ChatColor.DARK_RED + "[console]" + ChatColor.RED + ".");
								Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e.getCause());
							}
						} else {
							p.sendMessage(BackupCommand.MESSAGE_HEAD
										  + ChatColor.DARK_RED + mappedFile.getName() + ChatColor.RED + " does not exist.");
						}
					}
				} else {
					p.sendMessage(BackupCommand.MESSAGE_HEAD
								  + ChatColor.DARK_RED + mappedFile.getName() + ChatColor.RED + " was not deleted.");
				}
			}
		}
	}

	public @NotNull String usageMessage() {
		return ChatColor.GRAY + "/backup"
			   + ChatColor.AQUA + " delete "
			   + ChatColor.YELLOW + "<"
			   + ChatColor.DARK_RED + "filename"
			   + ChatColor.YELLOW + ">";
	}

	public @NotNull String usageHoverMessage() {
		return ChatColor.RED + "e.g. "
			   + ChatColor.GRAY + "/backup"
			   + ChatColor.AQUA + " delete "
			   + ChatColor.DARK_RED + "example";
	}

	public @NotNull String usageCommand() {
		return "/backup delete ";
	}

	public void usage(final @NotNull Player p) {
		GlobalMessageUtils.sendSuggestMessage(BackupCommand.MESSAGE_HEAD
											  + ChatColor.RED + "Usage: ",
											  Delete.usageMessage(),
											  Delete.usageHoverMessage(),
											  Delete.usageCommand(), p);
	}

	private void sendNotApplicableRegion(final @NotNull Player p) {
		p.sendMessage(BackupCommand.MESSAGE_HEAD
					  + ChatColor.RED + "The given region does not exist.");
	}
}