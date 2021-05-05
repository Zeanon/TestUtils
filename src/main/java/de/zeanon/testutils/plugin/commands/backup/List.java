package de.zeanon.testutils.plugin.commands.backup;

import de.zeanon.storagemanagercore.external.browniescollections.GapList;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.BackupMode;
import de.zeanon.testutils.plugin.utils.enums.StringModifiers;
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
public class List {

	public void execute(final @Nullable BackupMode backupMode, final @NotNull Player p) {
		final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p);
		final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);

		new BukkitRunnable() {
			@Override
			public void run() {
				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
				} else {
					try {
						final @NotNull File regionFolder = BackupCommand.BACKUP_FOLDER.resolve(tempRegion.getName().substring(0, tempRegion.getName().length() - 6)).toFile();
						if (!regionFolder.exists()) {
							p.sendMessage(BackupCommand.MESSAGE_HEAD
										  + ChatColor.RED + "There are no " + (backupMode == null ? "" : backupMode + " ") + "backups for '"
										  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
						} else {
							final @NotNull java.util.List<Pair<File, String>> files = new GapList<>();
							if (backupMode == null) {
								final @NotNull File manualBackups = new File(regionFolder, "manual/" + p.getUniqueId());
								if (manualBackups.exists()) {
									final @NotNull java.util.List<File> tempFiles = BaseFileUtils.listFolders(manualBackups);
									if (!tempFiles.isEmpty()) {
										files.addAll(tempFiles.stream().map(f -> new Pair<>(f, "manual")).collect(Collectors.toList()));
									}
								}

								final @NotNull File hourlyBackups = new File(regionFolder, "automatic/hourly");
								if (hourlyBackups.exists()) {
									final @NotNull java.util.List<File> tempFiles = BaseFileUtils.listFolders(hourlyBackups);
									if (!tempFiles.isEmpty()) {
										files.addAll(tempFiles.stream().map(f -> new Pair<>(f, "hourly")).collect(Collectors.toList()));
									}
								}

								final @NotNull File dailyBackups = new File(regionFolder, "automatic/daily");
								if (dailyBackups.exists()) {
									final @NotNull java.util.List<File> tempFiles = BaseFileUtils.listFolders(dailyBackups);
									if (!tempFiles.isEmpty()) {
										files.addAll(tempFiles.stream().map(f -> new Pair<>(f, "daily")).collect(Collectors.toList()));
									}
								}

								final @NotNull File startupBackups = new File(regionFolder, "automatic/startup");
								if (startupBackups.exists()) {
									final @NotNull java.util.List<File> tempFiles = BaseFileUtils.listFolders(startupBackups);
									if (!tempFiles.isEmpty()) {
										files.addAll(tempFiles.stream().map(f -> new Pair<>(f, "startup")).collect(Collectors.toList()));
									}
								}
							} else {
								final @NotNull File backupFolder = new File(regionFolder, backupMode.getPath(p.getUniqueId().toString()));
								if (backupFolder.exists()) {
									final @NotNull java.util.List<File> tempFiles = BaseFileUtils.listFolders(backupFolder);
									if (!tempFiles.isEmpty()) {
										files.addAll(tempFiles.stream().map(f -> new Pair<>(f, backupMode.toString())).collect(Collectors.toList()));
									}
								}
							}

							if (files.isEmpty()) {
								p.sendMessage(BackupCommand.MESSAGE_HEAD
											  + ChatColor.RED + "There are no " + (backupMode == null ? "" : backupMode + " ") + "backups for '"
											  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
							} else {
								p.sendMessage(StringModifiers.LINE_BREAK
											  + BackupCommand.MESSAGE_HEAD
											  + ChatColor.RED + "=== " + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + " === " + BackupCommand.MESSAGE_HEAD);
								for (final @NotNull Pair<File, String> file : files.stream().sorted(Comparator.comparingLong(file -> Objects.notNull(file.getKey()).lastModified())).collect(Collectors.toList())) {
									BackupCommand.sendLoadBackupMessage(Objects.notNull(file.getKey()).getName(),
																		Objects.notNull(file.getValue()),
																		p);
								}
							}
						}
					} catch (IOException e) {
						p.sendMessage(BackupCommand.MESSAGE_HEAD
									  + ChatColor.RED + "There has been an error, listing the backups for '"
									  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
						e.printStackTrace();
					}
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}
}