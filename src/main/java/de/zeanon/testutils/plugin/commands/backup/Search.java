package de.zeanon.testutils.plugin.commands.backup;

import de.zeanon.storagemanagercore.external.browniescollections.GapList;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.BackupMode;
import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import de.zeanon.testutils.plugin.utils.region.Region;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Search {

	public void execute(final @NotNull RegionSide regionSide, final @Nullable String fileName, final @NotNull BackupMode backupMode, final @NotNull Player p) {
		final @Nullable World world = p.getWorld();
		final @Nullable Region tempRegion = regionSide == RegionSide.NONE ? TestAreaUtils.getRegion(p) : TestAreaUtils.getRegion(p, regionSide);
		final @Nullable Region otherRegion = regionSide == RegionSide.NONE ? TestAreaUtils.getOppositeRegion(p) : TestAreaUtils.getOppositeRegion(p, regionSide);

		new BukkitRunnable() {
			@Override
			public void run() {
				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
				} else {
					try {
						final @NotNull File regionFolder = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Backups/" + world.getName() + "/" + tempRegion.getName().substring(0, tempRegion.getName().length() - 6));
						if (!regionFolder.exists()) {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "There are no " + backupMode + " backups for '"
										  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
						} else {
							if (fileName == null) {
								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "Missing sequence to search for.");
							} else {
								final @NotNull java.util.List<Pair<File, String>> files = new GapList<>();
								if (backupMode == BackupMode.NONE) {
									final @NotNull File manualBackups = new File(regionFolder, "manual/" + p.getUniqueId());
									if (manualBackups.exists()) {
										final @NotNull java.util.List<File> tempFiles = BaseFileUtils.searchFolders(manualBackups, fileName);
										if (!tempFiles.isEmpty()) {
											files.addAll(tempFiles.stream().map(f -> new Pair<>(f, "manual")).collect(Collectors.toList()));
										}
									}

									final @NotNull File hourlyBackups = new File(regionFolder, "automatic/hourly");
									if (hourlyBackups.exists()) {
										final @NotNull java.util.List<File> tempFiles = BaseFileUtils.searchFolders(hourlyBackups, fileName);
										if (!tempFiles.isEmpty()) {
											files.addAll(tempFiles.stream().map(f -> new Pair<>(f, "hourly")).collect(Collectors.toList()));
										}
									}

									final @NotNull File dailyBackups = new File(regionFolder, "automatic/daily");
									if (dailyBackups.exists()) {
										final @NotNull java.util.List<File> tempFiles = BaseFileUtils.searchFolders(dailyBackups, fileName);
										if (!tempFiles.isEmpty()) {
											files.addAll(tempFiles.stream().map(f -> new Pair<>(f, "daily")).collect(Collectors.toList()));
										}
									}

									final @NotNull File startupBackups = new File(regionFolder, "automatic/startup");
									if (startupBackups.exists()) {
										final @NotNull java.util.List<File> tempFiles = BaseFileUtils.searchFolders(startupBackups, fileName);
										if (!tempFiles.isEmpty()) {
											files.addAll(tempFiles.stream().map(f -> new Pair<>(f, "startup")).collect(Collectors.toList()));
										}
									}
								} else {
									final @NotNull File backupFolder = new File(regionFolder, backupMode.getPath(p.getUniqueId().toString()));
									if (backupFolder.exists()) {
										final @NotNull java.util.List<File> tempFiles = BaseFileUtils.searchFolders(backupFolder, fileName);
										if (!tempFiles.isEmpty()) {
											files.addAll(tempFiles.stream().map(f -> new Pair<>(f, backupMode.toString())).collect(Collectors.toList()));
										}
									}
								}

								if (files.isEmpty()) {
									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "There is no " + (backupMode == BackupMode.NONE ? "" : backupMode + " ")
												  + "backup matching '" + ChatColor.DARK_RED + fileName + ChatColor.RED + "' for '"
												  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
								} else {
									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "=== Backups for '" + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "' === " + GlobalMessageUtils.messageHead);
									for (final @NotNull Pair<File, String> file : files.stream().sorted(Comparator.comparingLong(f -> f.getKey().lastModified())).collect(Collectors.toList())) {
										Backup.sendLoadBackupMessage(file.getKey().getName(),
																	 file.getValue(),
																	 p);
									}
								}
							}
						}
					} catch (IOException e) {
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "There has been an error, searching the backups for '"
									  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
						e.printStackTrace();
					}
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}
}