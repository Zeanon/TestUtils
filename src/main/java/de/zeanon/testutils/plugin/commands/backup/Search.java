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
import java.util.HashMap;
import java.util.List;
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
public class Search {

	public void executeSearch(final @NotNull Backup.ModifierBlock modifiers, final @NotNull Player p) {
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
										  + ChatColor.RED + "There is no " + modifiers.getBackUpMode() + " backup for '"
										  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
						} else {
							if (modifiers.getFileName() == null) {
								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "Missing sequence to search for.");
							} else {

								final @NotNull Map<String, List<File>> files = new HashMap<>();
								if (modifiers.getBackUpMode() == BackUpMode.NONE) {
									final @NotNull File manualBackups = new File(regionFolder, "manual/" + p.getUniqueId());
									if (manualBackups.exists()) {
										final @NotNull List<File> tempFiles = BaseFileUtils.searchFolders(manualBackups, modifiers.getFileName())
																						   .stream()
																						   .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))
																						   .collect(Collectors.toList());
										if (!tempFiles.isEmpty()) {
											files.put("manual", tempFiles);
										}
									}

									final @NotNull File hourlyBackups = new File(regionFolder, "automatic/hourly");
									if (hourlyBackups.exists()) {
										final @NotNull List<File> tempFiles = BaseFileUtils.searchFolders(hourlyBackups, modifiers.getFileName()).stream()
																						   .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))
																						   .collect(Collectors.toList());
										if (!tempFiles.isEmpty()) {
											files.put("hourly", tempFiles);
										}
									}

									final @NotNull File dailyBackups = new File(regionFolder, "automatic/daily");
									if (dailyBackups.exists()) {
										final @NotNull List<File> tempFiles = BaseFileUtils.searchFolders(dailyBackups, modifiers.getFileName()).stream()
																						   .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))
																						   .collect(Collectors.toList());
										if (!tempFiles.isEmpty()) {
											files.put("daily", tempFiles);
										}
									}

									final @NotNull File startupBackups = new File(regionFolder, "automatic/startup");
									if (startupBackups.exists()) {
										final @NotNull List<File> tempFiles = BaseFileUtils.searchFolders(startupBackups, modifiers.getFileName()).stream()
																						   .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))
																						   .collect(Collectors.toList());
										if (!tempFiles.isEmpty()) {
											files.put("startup", tempFiles);
										}
									}
								} else if (modifiers.getBackUpMode() == BackUpMode.MANUAL) {
									final @NotNull List<File> tempFiles = BaseFileUtils.searchFolders(new File(regionFolder, modifiers.getBackUpMode().getPath() + "/" + p.getUniqueId()), modifiers.getFileName()).stream()
																					   .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))
																					   .collect(Collectors.toList());
									if (!tempFiles.isEmpty()) {
										files.put(modifiers.getBackUpMode().toString(), tempFiles);
									}
								} else {
									final @NotNull List<File> tempFiles = BaseFileUtils.searchFolders(new File(regionFolder, modifiers.getBackUpMode().getPath()), modifiers.getFileName()).stream()
																					   .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))
																					   .collect(Collectors.toList());
									if (!tempFiles.isEmpty()) {
										files.put(modifiers.getBackUpMode().toString(), tempFiles);
									}
								}

								if (files.isEmpty()) {
									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "There is no " + (modifiers.getBackUpMode() == BackUpMode.NONE ? "" : modifiers.getBackUpMode() + " ") + "backup for '"
												  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
								} else {
									for (final @NotNull Map.Entry<String, java.util.List<File>> entry : files.entrySet()) {
										p.sendMessage("");
										p.sendMessage(ChatColor.AQUA + "=== " + entry.getKey() + " ===");

										for (final @NotNull File file : entry.getValue()) {
											Backup.sendLoadBackupMessage("", ChatColor.GOLD + file.getName(),
																		 ChatColor.RED + "Paste the backup '" + ChatColor.DARK_RED + file.getName() + ChatColor.RED + "' for this TestArea.",
																		 "/backup load " + file.getName() + " -" + entry.getKey(),
																		 p);
										}
									}
								}
							}
						}
					} catch (IOException e) {
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "There has been an error, searching the backups for '"
									  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
						e.printStackTrace();
					}
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}
}