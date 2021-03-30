package de.zeanon.testutils.plugin.commands.backup;

import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.utils.CommandRequestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.BackupFile;
import de.zeanon.testutils.plugin.utils.region.DefinedRegion;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Delete {

	public void execute(final @Nullable BackupFile backupFile, final @Nullable Boolean confirm, final @NotNull Player p) {
		if (backupFile != null && backupFile.getName().contains("./")) {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "File '" + backupFile.getName() + "' resolution error: Path is not allowed.");
		} else if (confirm == null) {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
			final @Nullable DefinedRegion otherRegion = TestAreaUtils.getSouthRegion(p);

			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
			} else {
				final @NotNull String name = backupFile == null
											 ? LocalDateTime.now().format(InitMode.getFormatter())
											 : backupFile.getName();

				final @NotNull File folder = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Backups/" + p.getWorld().getName() + "/" + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + "/manual/" + p.getUniqueId() + "/" + name);
				if (folder.exists()) {
					CommandRequestUtils.addDeleteBackupRequest(p.getUniqueId(), name, tempRegion.getName().substring(0, tempRegion.getName().length() - 6));
					GlobalMessageUtils.sendBooleanMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
														  ChatColor.RED + "Do you really want to delete "
														  + ChatColor.DARK_RED + name
														  + ChatColor.RED + "?",
														  "/backup delete " + name + " -confirm",
														  "/backup delete " + name + " -deny", p);
				} else {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.DARK_RED + name + ChatColor.RED + " does not exist.");
				}
			}
		} else {
			if (backupFile == null) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "You need to specify the file you want to delete.");
			} else {
				if (confirm) { //NOSONAR
					final @Nullable String region = CommandRequestUtils.checkDeleteBackupRequest(p.getUniqueId(), backupFile.getName());
					if (region != null) {
						CommandRequestUtils.removeDeleteBackupRequest(p.getUniqueId());

						final @NotNull World tempWorld = p.getWorld();
						final @Nullable DefinedRegion tempRegion = TestAreaUtils.getNorthRegion(tempWorld, region);
						final @Nullable DefinedRegion otherRegion = TestAreaUtils.getSouthRegion(tempWorld, region);
						if (tempRegion == null || otherRegion == null) {
							Delete.sendNotApplicableRegion(p);
						} else {
							final @NotNull File file = new File(TestUtils.getInstance().getDataFolder(), "Backups/" + p.getWorld().getName() + "/" + region + "/manual/" + p.getUniqueId() + "/" + backupFile.getName());
							if (file.exists()) {
								try {
									FileUtils.deleteDirectory(file);
								} catch (IOException e) {
									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.DARK_RED + backupFile.getName() + ChatColor.RED + " could not be deleted, for further information please see [console].");
									e.printStackTrace();
								}
							} else {
								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.DARK_RED + backupFile.getName() + ChatColor.RED + " does not exist.");
							}
						}
					}
				} else {
					CommandRequestUtils.removeOverwriteBackupRequest(p.getUniqueId());
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.DARK_RED + backupFile.getName() + ChatColor.RED + " was not deleted.");
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
		GlobalMessageUtils.sendSuggestMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "Usage: ",
											  Delete.usageMessage(),
											  Delete.usageHoverMessage(),
											  Delete.usageCommand(), p);
	}

	private void sendNotApplicableRegion(final @NotNull Player p) {
		p.sendMessage(GlobalMessageUtils.messageHead
					  + ChatColor.RED + "The given region does not exist.");
	}
}