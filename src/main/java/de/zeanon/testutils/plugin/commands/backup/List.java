package de.zeanon.testutils.plugin.commands.backup;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.BackUpMode;
import de.zeanon.testutils.plugin.utils.enums.PasteSide;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class List {

	public void executeList(final @NotNull Backup.ModifierBlock modifiers, final @NotNull Player p) {
		final @Nullable World world = p.getWorld();
		final @Nullable ProtectedRegion tempRegion = modifiers.getPasteSide() == PasteSide.NONE ? TestAreaUtils.getRegion(p) : TestAreaUtils.getRegion(p, modifiers.getPasteSide());
		final @Nullable ProtectedRegion otherRegion = modifiers.getPasteSide() == PasteSide.NONE ? TestAreaUtils.getOppositeRegion(p) : TestAreaUtils.getOppositeRegion(p, modifiers.getPasteSide());

		new BukkitRunnable() {
			@Override
			public void run() {
				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
				} else {
					try {
						final @NotNull File regionFolder = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps/" + world.getName() + "/" + tempRegion.getId().substring(9, tempRegion.getId().length() - 6));
						if (!regionFolder.exists()) {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "There is no " + (modifiers.getBackUpMode() == BackUpMode.NONE ? "" : modifiers.getBackUpMode() + " ") + "backup for '"
										  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
						} else {
							if (modifiers.getFileName() != null) {
								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "Argument '" + ChatColor.DARK_RED + modifiers.getFileName() + ChatColor.RED + "' is not applicable here.");
							} else {

								final @NotNull Map<String, java.util.List<File>> files = new HashMap<>();
								if (modifiers.getBackUpMode() == BackUpMode.NONE) {
									final @NotNull File manualBackups = new File(regionFolder, "manual/" + p.getUniqueId());
									if (manualBackups.exists()) {
										final @NotNull java.util.List<File> tempFiles = BaseFileUtils.listFolders(manualBackups)
																									 .stream()
																									 .sorted(Comparator.comparingLong(File::lastModified))
																									 .collect(Collectors.toList());
										if (!tempFiles.isEmpty()) {
											files.put("manual", tempFiles);
										}
									}

									final @NotNull File hourlyBackups = new File(regionFolder, "automatic/hourly");
									if (hourlyBackups.exists()) {
										final @NotNull java.util.List<File> tempFiles = BaseFileUtils.listFolders(hourlyBackups)
																									 .stream()
																									 .sorted(Comparator.comparingLong(File::lastModified))
																									 .collect(Collectors.toList());
										if (!tempFiles.isEmpty()) {
											files.put("hourly", tempFiles);
										}
									}

									final @NotNull File dailyBackups = new File(regionFolder, "automatic/daily");
									if (dailyBackups.exists()) {
										final @NotNull java.util.List<File> tempFiles = BaseFileUtils.listFolders(dailyBackups)
																									 .stream()
																									 .sorted(Comparator.comparingLong(File::lastModified))
																									 .collect(Collectors.toList());
										if (!tempFiles.isEmpty()) {
											files.put("daily", tempFiles);
										}
									}

									final @NotNull File startupBackups = new File(regionFolder, "automatic/startup");
									if (startupBackups.exists()) {
										final @NotNull java.util.List<File> tempFiles = BaseFileUtils.listFolders(startupBackups)
																									 .stream()
																									 .sorted(Comparator.comparingLong(File::lastModified))
																									 .collect(Collectors.toList());
										if (!tempFiles.isEmpty()) {
											files.put("startup", tempFiles);
										}
									}
								} else {
									final @NotNull File backupFolder = new File(regionFolder, modifiers.getBackUpMode().getPath(p.getUniqueId().toString()));
									if (backupFolder.exists()) {
										final @NotNull java.util.List<File> tempFiles = BaseFileUtils.listFolders(backupFolder)
																									 .stream()
																									 .sorted(Comparator.comparingLong(File::lastModified))
																									 .collect(Collectors.toList());
										if (!tempFiles.isEmpty()) {
											files.put(modifiers.getBackUpMode().toString(), tempFiles);
										}
									}
								}

								if (files.isEmpty()) {
									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "There is no " + (modifiers.getBackUpMode() == BackUpMode.NONE ? "" : modifiers.getBackUpMode() + " ") + "backup for '"
												  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
								} else {
									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "=== Backups for '" + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "' === " + GlobalMessageUtils.messageHead);
									for (final @NotNull Map.Entry<String, java.util.List<File>> entry : files.entrySet()) {
										for (final @NotNull File file : entry.getValue()) {
											Backup.sendLoadBackupMessage(file.getName(),
																		 entry.getKey(),
																		 p);
										}
									}
								}
							}
						}
					} catch (IOException e) {
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "There has been an error, listing the backups for '"
									  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
						e.printStackTrace();
					}
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}
}