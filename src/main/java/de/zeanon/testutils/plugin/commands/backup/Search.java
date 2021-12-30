package de.zeanon.testutils.plugin.commands.backup;

import de.zeanon.storagemanagercore.external.browniescollections.GapList;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.plugin.commands.testblock.TestBlockCommand;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.BackupMode;
import de.zeanon.testutils.plugin.utils.enums.CaseSensitive;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Search {

	public void execute(final @NotNull Player p, final @NotNull BackupMode backupMode, final @NotNull CaseSensitive caseSensitive, final int page, final @Nullable String sequence) {
		final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p);
		final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);

		if (TestAreaUtils.illegalName(sequence)) {
			p.sendMessage(BackupCommand.MESSAGE_HEAD
						  + ChatColor.RED + "Backup '" + sequence + "' resolution error: Name is not allowed.");
			return;
		}

		if (tempRegion == null || otherRegion == null) {
			GlobalMessageUtils.sendNotApplicableRegion(p);
			return;
		}

		try {
			int listmax = ConfigUtils.getInt("Listmax");
			final boolean spaceLists = ConfigUtils.getBoolean("Space Lists");

			final @NotNull File regionFolder = BackupCommand.BACKUP_FOLDER.resolve(tempRegion.getName().substring(0, tempRegion.getName().length() - 6)).toFile();
			if (!regionFolder.exists() || !regionFolder.isDirectory()) {
				if (spaceLists) {
					p.sendMessage("");
				}

				p.sendMessage(BackupCommand.MESSAGE_HEAD
							  + ChatColor.RED + "There are no " + backupMode + " backups for '"
							  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
				return;
			}

			final @NotNull java.util.List<Pair<File, String>> files = new GapList<>();
			if (backupMode == BackupMode.NONE) {
				final @NotNull File manualBackups = new File(regionFolder, "manual/" + p.getUniqueId());
				if (manualBackups.exists() && manualBackups.isDirectory()) {
					final @NotNull java.util.List<File> tempFiles = Objects.notNull(Objects.notNull(BaseFileUtils.searchFolders(manualBackups, sequence, caseSensitive.confirm())));
					if (!Objects.notNull(tempFiles).isEmpty()) {
						files.addAll(tempFiles.stream().map(f -> new Pair<>(f, "manual")).collect(Collectors.toList()));
					}
				}

				final @NotNull File hourlyBackups = new File(regionFolder, "automatic/hourly");
				if (hourlyBackups.exists() && hourlyBackups.isDirectory()) {
					final @NotNull java.util.List<File> tempFiles = Objects.notNull(BaseFileUtils.searchFolders(hourlyBackups, sequence, caseSensitive.confirm()));
					if (!Objects.notNull(tempFiles).isEmpty()) {
						files.addAll(tempFiles.stream().map(f -> new Pair<>(f, "hourly")).collect(Collectors.toList()));
					}
				}

				final @NotNull File dailyBackups = new File(regionFolder, "automatic/daily");
				if (dailyBackups.exists() && dailyBackups.isDirectory()) {
					final @NotNull java.util.List<File> tempFiles = Objects.notNull(BaseFileUtils.searchFolders(dailyBackups, sequence, caseSensitive.confirm()));
					if (!Objects.notNull(tempFiles).isEmpty()) {
						files.addAll(tempFiles.stream().map(f -> new Pair<>(f, "daily")).collect(Collectors.toList()));
					}
				}

				final @NotNull File startupBackups = new File(regionFolder, "automatic/startup");
				if (startupBackups.exists() && startupBackups.isDirectory()) {
					final @NotNull java.util.List<File> tempFiles = Objects.notNull(BaseFileUtils.searchFolders(startupBackups, sequence, caseSensitive.confirm()));
					if (!Objects.notNull(tempFiles).isEmpty()) {
						files.addAll(tempFiles.stream().map(f -> new Pair<>(f, "startup")).collect(Collectors.toList()));
					}
				}
			} else {
				final @NotNull File backupFolder = new File(regionFolder, backupMode.getPath(p.getUniqueId().toString()));
				if (backupFolder.exists() && backupFolder.isDirectory()) {
					final @NotNull java.util.List<File> tempFiles = Objects.notNull(BaseFileUtils.searchFolders(backupFolder, sequence, caseSensitive.confirm()));
					if (!Objects.notNull(tempFiles).isEmpty()) {
						files.addAll(tempFiles.stream().map(f -> new Pair<>(f, backupMode.toString())).collect(Collectors.toList()));
					}
				}
			}

			files.sort(Comparator.comparingLong(file -> Objects.notNull(file.getKey()).lastModified()));

			final double count = files.size();
			final int pageAmount = (int) (((count / listmax) % 1 != 0) ? (count / listmax) + 1 : (count / listmax));

			if (spaceLists) {
				p.sendMessage("");
			}

			if (count < 1) {
				GlobalMessageUtils.sendHoverMessage(BackupCommand.MESSAGE_HEAD
													+ ChatColor.RED + "=== ",
													ChatColor.DARK_RED + "No backups found",
													ChatColor.RED + " === "
													+ BackupCommand.MESSAGE_HEAD,
													ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6),
													p);
			} else {
				GlobalMessageUtils.sendHoverMessage(BackupCommand.MESSAGE_HEAD
													+ ChatColor.RED + "=== ",
													ChatColor.DARK_RED + "" + (int) count + " Backups | Page " + page + "/" + pageAmount,
													ChatColor.RED + " === "
													+ BackupCommand.MESSAGE_HEAD,
													ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6),
													p);
				if (count < listmax) {
					listmax = (int) count;
				}

				int id = (page - 1) * listmax;

				if (count < listmax * page) {
					listmax = (int) count - (listmax * (page - 1));
				}

				@NotNull Pair<File, String> file;
				for (int i = 0; i < listmax; i++) {
					file = files.get(id);
					BackupCommand.sendLoadBackupMessage(Objects.notNull(file.getKey()).getName(),
														Objects.notNull(file.getValue()),
														p);
					id++;
				}

				final int nextPage = page >= pageAmount ? 1 : page + 1;
				final int previousPage = (page <= 1 ? pageAmount : page - 1);
				if (pageAmount > 1) {
					GlobalMessageUtils.sendScrollMessage(TestBlockCommand.MESSAGE_HEAD,
														 "/backup " + (caseSensitive.confirm() ? " -c " : "") + (backupMode == BackupMode.NONE ? "" : " " + backupMode.getCommand() + " ") + (sequence == null ? "list " : "search " + sequence + " ") + nextPage,
														 "/backup  " + (caseSensitive.confirm() ? " -c " : "") + (backupMode == BackupMode.NONE ? "" : " " + backupMode.getCommand() + " ") + (sequence == null ? "list " : "search " + sequence + " ") + previousPage,
														 ChatColor.RED + "Page " + nextPage,
														 ChatColor.RED + "Page " + previousPage, p,
														 ChatColor.DARK_RED);
				} else {
					GlobalMessageUtils.sendScrollMessage(BackupCommand.MESSAGE_HEAD,
														 "",
														 "",
														 ChatColor.RED + "There is only one page of backups in this list",
														 ChatColor.RED + "There is only one page of backups in this list", p,
														 ChatColor.GRAY);
				}
			}
		} catch (final @NotNull IOException e) {
			p.sendMessage(BackupCommand.MESSAGE_HEAD + ChatColor.RED + "Could not access backups folder, for further information please see [console].");
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e.getCause());
		}
	}
}