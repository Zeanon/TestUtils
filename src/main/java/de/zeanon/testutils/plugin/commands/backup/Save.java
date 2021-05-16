package de.zeanon.testutils.plugin.commands.backup;

import de.zeanon.storagemanagercore.internal.base.exceptions.RuntimeIOException;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.*;
import de.zeanon.testutils.plugin.utils.backup.Backup;
import de.zeanon.testutils.plugin.utils.backup.BackupScheduler;
import de.zeanon.testutils.plugin.utils.enums.CommandConfirmation;
import de.zeanon.testutils.plugin.utils.enums.MappedFile;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Save {

	public void execute(final @Nullable MappedFile mappedFile, final @Nullable CommandConfirmation confirmation, final @NotNull Player p) {
		if (ConfigUtils.getInt("Backups", "manual") == 0) {
			p.sendMessage(BackupCommand.MESSAGE_HEAD
						  + ChatColor.RED + "Manual backups are disabled.");
			return;
		}

		if (mappedFile != null && TestAreaUtils.illegalName(mappedFile.getName())) {
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

			final @NotNull String name = mappedFile == null
										 ? LocalDateTime.now().format(Backup.getFormatter())
										 : mappedFile.getName();

			try {
				final @NotNull File backupFolder = BackupCommand.BACKUP_FOLDER.resolve(tempRegion.getName().substring(0, tempRegion.getName().length() - 6)).resolve("manual").resolve(p.getUniqueId().toString()).toAbsolutePath().toFile();
				final @NotNull File folder = new File(backupFolder, name);
				final @Nullable List<File> files = BaseFileUtils.listFolders(backupFolder);

				if (folder.exists()) {
					CommandRequestUtils.addSaveBackupRequest(p.getUniqueId(), name, tempRegion.getName().substring(0, tempRegion.getName().length() - 6), null);
					p.sendMessage(BackupCommand.MESSAGE_HEAD
								  + ChatColor.RED + "The Backup " + ChatColor.DARK_RED + name + ChatColor.RED + " already exists.");
					GlobalMessageUtils.sendBooleanMessage(ChatColor.RED + "Do you want to overwrite " + ChatColor.DARK_RED + name + ChatColor.RED + "?",
														  "/backup save " + name + " -confirm",
														  "/backup save " + name + " -deny", p);
				} else if (files != null && files.size() >= ConfigUtils.getInt("Backups", "manual")) {
					final @NotNull Optional<File> toBeDeleted = files.stream().min(Comparator.comparingLong(File::lastModified));
					if (toBeDeleted.isPresent()) {
						CommandRequestUtils.addSaveBackupRequest(p.getUniqueId(), name, tempRegion.getName().substring(0, tempRegion.getName().length() - 6), toBeDeleted.get().getName());
						p.sendMessage(BackupCommand.MESSAGE_HEAD
									  + ChatColor.RED + "You have more than " + ConfigUtils.getInt("Backups", "manual") + " backups.");
						GlobalMessageUtils.sendBooleanMessage(ChatColor.RED + "'" + ChatColor.DARK_RED + toBeDeleted.get().getName() + ChatColor.RED + "' will be deleted due to being the oldest.",
															  "/backup save " + name + " -confirm",
															  "/backup save " + name + " -deny", p);
					} else {
						p.sendMessage(BackupCommand.MESSAGE_HEAD
									  + ChatColor.RED + "There has been an error saving the Backup, please see [console] for further information.");
						System.err.println("Could not determine oldest file to delete.");
					}
				} else {
					Save.save(p.getWorld(), tempRegion, otherRegion, name, folder, p);
				}
			} catch (final @NotNull IOException e) {
				throw new RuntimeIOException(e);
			}
		} else {
			if (mappedFile == null) {
				p.sendMessage(BackupCommand.MESSAGE_HEAD
							  + ChatColor.RED + "You need to specify the file you want to overwrite.");
				return;
			}

			final @Nullable Pair<String, String> checkedRequest = CommandRequestUtils.checkSaveBackupRequest(p.getUniqueId(), mappedFile.getName());
			CommandRequestUtils.removeSaveBackupRequest(p.getUniqueId());
			if (checkedRequest != null && checkedRequest.getKey() != null) {
				if (confirmation.confirm()) { //NOSONAR
					final @NotNull org.bukkit.World tempWorld = p.getWorld();
					if (checkedRequest.getValue() != null) {
						try {
							final @NotNull File toBeDeleted = BackupCommand.BACKUP_FOLDER.resolve(checkedRequest.getKey()).resolve("manual").resolve(p.getUniqueId().toString()).resolve(checkedRequest.getValue()).toFile();
							FileUtils.deleteDirectory(toBeDeleted); //NOSONAR
							InternalFileUtils.deleteEmptyParent(toBeDeleted, BackupCommand.BACKUP_FOLDER.toFile());
							p.sendMessage(BackupCommand.MESSAGE_HEAD
										  + ChatColor.RED + "'" + ChatColor.DARK_RED + checkedRequest.getValue() + ChatColor.RED + "' was deleted.");
						} catch (final @NotNull IOException e) {
							throw new RuntimeIOException(e);
						}
					}

					final @NotNull File folder = BackupCommand.BACKUP_FOLDER.resolve(checkedRequest.getKey()).resolve("manual").resolve(p.getUniqueId().toString()).resolve(mappedFile.getName()).toFile();
					final @Nullable DefinedRegion tempRegion = TestAreaUtils.getNorthRegion(checkedRequest.getKey());
					final @Nullable DefinedRegion otherRegion = TestAreaUtils.getSouthRegion(checkedRequest.getKey());
					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						Save.save(tempWorld, tempRegion, otherRegion, mappedFile.getName(), folder, p);
					}
				} else {
					p.sendMessage(BackupCommand.MESSAGE_HEAD
								  + ChatColor.DARK_RED + mappedFile.getName() + ChatColor.RED + " was not overwritten.");
				}
			}
		}
	}

	private void save(final @NotNull World tempWorld, final @NotNull DefinedRegion tempRegion, final @NotNull DefinedRegion otherRegion, final @NotNull String name, final @NotNull File folder, final @NotNull Player p) {
		p.sendMessage(BackupCommand.MESSAGE_HEAD
					  + ChatColor.RED + "Registering Backup for '"
					  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6)
					  + ChatColor.RED + "'...");
		BackupScheduler.getMANUAL_BACKUP().backupSide(tempWorld, tempRegion, folder);

		BackupScheduler.getMANUAL_BACKUP().backupSide(tempWorld, otherRegion, folder);

		p.sendMessage(BackupCommand.MESSAGE_HEAD
					  + ChatColor.RED + "You registered a new backup for '"
					  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6)
					  + ChatColor.RED + "' named '" + ChatColor.DARK_RED + name + ChatColor.RED + "'.");


		new BukkitRunnable() {
			@Override
			public void run() {
				final @NotNull File manualBackup = BackupCommand.BACKUP_FOLDER.resolve(tempRegion.getName().substring(0, tempRegion.getName().length() - 6)).resolve("manual").resolve(p.getUniqueId().toString()).toFile();
				if (manualBackup.exists() && manualBackup.isDirectory()) {
					try {
						@Nullable List<File> files = BaseFileUtils.listFolders(manualBackup);
						while (files != null && files.size() > ConfigUtils.getInt("Backups", "manual")) {
							final @NotNull Optional<File> toBeDeleted = files.stream().min(Comparator.comparingLong(File::lastModified));
							if (toBeDeleted.isPresent()) {
								p.sendMessage(BackupCommand.MESSAGE_HEAD
											  + ChatColor.RED + "You have more than " + ConfigUtils.getInt("Backups", "manual") + " backups, deleting '" + ChatColor.DARK_RED + toBeDeleted.get().getName() + ChatColor.RED + "' due to it being the oldest."); //NOSONAR
								FileUtils.deleteDirectory(toBeDeleted.get()); //NOSONAR
								InternalFileUtils.deleteEmptyParent(toBeDeleted.get(), BackupCommand.BACKUP_FOLDER.toFile());
							}
							files = BaseFileUtils.listFolders(manualBackup);
						}
					} catch (final IOException e) {
						throw new RuntimeIOException(e);
					}
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}
}