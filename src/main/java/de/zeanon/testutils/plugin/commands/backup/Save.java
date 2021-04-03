package de.zeanon.testutils.plugin.commands.backup;

import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.init.InitMode;
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
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Save {

	public void execute(final @Nullable MappedFile mappedFile, final @Nullable CommandConfirmation confirmation, final @NotNull Player p) {
		if (ConfigUtils.getInt("Backups", "manual") > 0) {
			if (mappedFile != null && (mappedFile.getName().contains("./") || InitMode.forbiddenFileName(mappedFile.getName()))) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "File '" + mappedFile.getName() + "' resolution error: Path is not allowed.");
			} else if (confirmation == null) {
				final @Nullable DefinedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
				final @Nullable DefinedRegion otherRegion = TestAreaUtils.getSouthRegion(p);

				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
				} else {
					final @NotNull String name = mappedFile == null
												 ? LocalDateTime.now().format(Backup.getFormatter())
												 : mappedFile.getName();

					final @NotNull File folder = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Backups/" + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + "/manual/" + p.getUniqueId() + "/" + name);
					if (folder.exists()) {
						CommandRequestUtils.addOverwriteBackupRequest(p.getUniqueId(), name, tempRegion.getName().substring(0, tempRegion.getName().length() - 6));
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "The Backup " + ChatColor.DARK_RED + name + ChatColor.RED + " already exists.");
						GlobalMessageUtils.sendBooleanMessage(ChatColor.RED + "Do you want to overwrite " + ChatColor.DARK_RED + name + ChatColor.RED + "?",
															  "/backup save " + name + " -confirm",
															  "/backup save " + name + " -deny", p);
					} else {
						Save.save(p.getWorld(), tempRegion, otherRegion, name, folder, p);
					}
				}
			} else {
				if (mappedFile == null) {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "You need to specify the file you want to overwrite.");
				} else {
					final @Nullable String region = CommandRequestUtils.checkOverwriteBackupRequest(p.getUniqueId(), mappedFile.getName());
					CommandRequestUtils.removeOverwriteBackupRequest(p.getUniqueId());
					if (region != null) {
						if (confirmation.confirm()) { //NOSONAR

							final @NotNull org.bukkit.World tempWorld = p.getWorld();
							final @NotNull File folder = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Backups/" + region + "/manual/" + p.getUniqueId() + "/" + mappedFile.getName());
							final @Nullable DefinedRegion tempRegion = TestAreaUtils.getNorthRegion(region);
							final @Nullable DefinedRegion otherRegion = TestAreaUtils.getSouthRegion(region);
							if (tempRegion == null || otherRegion == null) {
								GlobalMessageUtils.sendNotApplicableRegion(p);
							} else {
								Save.save(tempWorld, tempRegion, otherRegion, mappedFile.getName(), folder, p);
							}
						} else {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.DARK_RED + mappedFile.getName() + ChatColor.RED + " was not overwritten.");
						}
					}
				}
			}
		} else {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Manual backups are disabled.");
		}
	}

	private void save(final @NotNull org.bukkit.World tempWorld, final @NotNull DefinedRegion tempRegion, final @NotNull DefinedRegion otherRegion, final @NotNull String name, final @NotNull File folder, final @NotNull Player p) {
		p.sendMessage(GlobalMessageUtils.messageHead
					  + ChatColor.RED + "Registering Backup for '"
					  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6)
					  + ChatColor.RED + "'...");
		BackupScheduler.getMANUAL_BACKUP().backupSide(tempWorld, tempRegion, folder);

		BackupScheduler.getMANUAL_BACKUP().backupSide(tempWorld, otherRegion, folder);

		p.sendMessage(GlobalMessageUtils.messageHead
					  + ChatColor.RED + "You registered a new backup for '"
					  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6)
					  + ChatColor.RED + "' named '" + ChatColor.DARK_RED + name + ChatColor.RED + "'.");


		new BukkitRunnable() {
			@Override
			public void run() {
				final @NotNull File manualBackup = new File(TestUtils.getInstance().getDataFolder(), "Backups/" + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + "/manual/" + p.getUniqueId());
				if (manualBackup.exists() && manualBackup.isDirectory()) {
					try {
						@NotNull List<File> files;
						files = BaseFileUtils.listFolders(manualBackup);
						while (files.size() > ConfigUtils.getInt("Backups", "manual")) {
							final @NotNull Optional<File> toBeDeleted = files.stream().min(Comparator.comparingLong(File::lastModified));
							if (toBeDeleted.isPresent()) {
								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "You have more than " + ConfigUtils.getInt("Backups", "manual") + " backups, deleting '" + ChatColor.DARK_RED + toBeDeleted.get().getName() + ChatColor.RED + "' due to it being the oldest."); //NOSONAR
								FileUtils.deleteDirectory(toBeDeleted.get()); //NOSONAR
								InternalFileUtils.deleteEmptyParent(toBeDeleted.get(), new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Backups"));
							}
							files = BaseFileUtils.listFolders(manualBackup);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}
}