package de.zeanon.testutils.plugin.commands.backup;

import de.zeanon.storagemanagercore.external.browniescollections.GapList;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.BackupMode;
import de.zeanon.testutils.plugin.utils.enums.MappedFile;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Search {

	public void execute(final @NotNull MappedFile mappedFile, final @Nullable BackupMode backupMode, final @NotNull Player p) {
		final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p);
		final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);

		new BukkitRunnable() {
			@Override
			public void run() {
				if (TestAreaUtils.illegalName(mappedFile.getName())) {
					p.sendMessage(BackupCommand.MESSAGE_HEAD
								  + ChatColor.RED + "Backup '" + mappedFile.getName() + "' resolution error: Name is not allowed.");
					return;
				}

				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
					return;
				}

				try {
					final @NotNull File regionFolder = BackupCommand.BACKUP_FOLDER.resolve(tempRegion.getName().substring(0, tempRegion.getName().length() - 6)).toFile();
					if (!regionFolder.exists() || !regionFolder.isDirectory()) {
						p.sendMessage(BackupCommand.MESSAGE_HEAD
									  + ChatColor.RED + "There are no " + backupMode + " backups for '"
									  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
						return;
					}

					final @NotNull java.util.List<Pair<File, String>> files = new GapList<>();
					if (backupMode == null) {
						final @NotNull File manualBackups = new File(regionFolder, "manual/" + p.getUniqueId());
						if (manualBackups.exists() && manualBackups.isDirectory()) {
							final @NotNull java.util.List<File> tempFiles = BaseFileUtils.searchFolders(manualBackups, mappedFile.getName());
							if (!tempFiles.isEmpty()) {
								files.addAll(tempFiles.stream().map(f -> new Pair<>(f, "manual")).collect(Collectors.toList()));
							}
						}

						final @NotNull File hourlyBackups = new File(regionFolder, "automatic/hourly");
						if (hourlyBackups.exists() && hourlyBackups.isDirectory()) {
							final @NotNull java.util.List<File> tempFiles = BaseFileUtils.searchFolders(hourlyBackups, mappedFile.getName());
							if (!tempFiles.isEmpty()) {
								files.addAll(tempFiles.stream().map(f -> new Pair<>(f, "hourly")).collect(Collectors.toList()));
							}
						}

						final @NotNull File dailyBackups = new File(regionFolder, "automatic/daily");
						if (dailyBackups.exists() && dailyBackups.isDirectory()) {
							final @NotNull java.util.List<File> tempFiles = BaseFileUtils.searchFolders(dailyBackups, mappedFile.getName());
							if (!tempFiles.isEmpty()) {
								files.addAll(tempFiles.stream().map(f -> new Pair<>(f, "daily")).collect(Collectors.toList()));
							}
						}

						final @NotNull File startupBackups = new File(regionFolder, "automatic/startup");
						if (startupBackups.exists() && startupBackups.isDirectory()) {
							final @NotNull java.util.List<File> tempFiles = BaseFileUtils.searchFolders(startupBackups, mappedFile.getName());
							if (!tempFiles.isEmpty()) {
								files.addAll(tempFiles.stream().map(f -> new Pair<>(f, "startup")).collect(Collectors.toList()));
							}
						}
					} else {
						final @NotNull File backupFolder = new File(regionFolder, backupMode.getPath(p.getUniqueId().toString()));
						if (backupFolder.exists() && backupFolder.isDirectory()) {
							final @NotNull java.util.List<File> tempFiles = BaseFileUtils.searchFolders(backupFolder, mappedFile.getName());
							if (!tempFiles.isEmpty()) {
								files.addAll(tempFiles.stream().map(f -> new Pair<>(f, backupMode.toString())).collect(Collectors.toList()));
							}
						}
					}

					if (files.isEmpty()) {
						p.sendMessage(BackupCommand.MESSAGE_HEAD
									  + ChatColor.RED + "There is no " + (backupMode == BackupMode.NONE ? "" : backupMode + " ")
									  + "backup matching '" + ChatColor.DARK_RED + mappedFile + ChatColor.RED + "' for '"
									  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
					} else {
						p.sendMessage("\n"
									  + BackupCommand.MESSAGE_HEAD
									  + ChatColor.RED + "=== " + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + " === " + BackupCommand.MESSAGE_HEAD);
						for (final @NotNull Pair<File, String> file : files.stream().sorted(Comparator.comparingLong(f -> f.getKey().lastModified())).collect(Collectors.toList())) {
							BackupCommand.sendLoadBackupMessage(file.getKey().getName(),
																Objects.notNull(file.getValue()),
																p);
						}
					}
				} catch (IOException e) {
					p.sendMessage(BackupCommand.MESSAGE_HEAD
								  + ChatColor.RED + "There has been an error, searching the backups for '"
								  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}
}