package de.zeanon.testutils.plugin.backup;


import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.commands.backup.BackupCommand;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import de.zeanon.testutils.plugin.utils.InternalFileUtils;
import de.zeanon.testutils.plugin.utils.enums.BackupMode;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class DailyBackup extends Backup {

	public DailyBackup() {
		super(BackupMode.DAILY);
	}

	@Override
	protected void cleanup(final @NotNull File backupFolder) {
		try {
			final @NotNull File dailyBackup = new File(backupFolder, this.sequence.getPath(null));
			if (dailyBackup.exists()) {
				final int size = ConfigUtils.getInt("Backups", "daily");
				final @NotNull File stopHere = BackupCommand.BACKUP_FOLDER.toFile();
				@Nullable final List<File> files = BaseFileUtils.listFolders(dailyBackup);
				if (files.size() > size) {
					files.sort(Comparator.comparingLong(File::lastModified));
					do {
						final @NotNull File file = files.remove(0);
						BaseFileUtils.deleteDirectory(file);
						InternalFileUtils.deleteEmptyParent(file, stopHere);
					} while (files.size() > size);
				}
			}
		} catch (final IOException e) {
			TestUtils.getChatLogger().log(Level.SEVERE, "Error while performing daily backup", e);
		}
	}

	@Override
	protected void systemOutStart() {
		TestUtils.getChatLogger().info(">> Creating Daily-Backup...");
	}

	@Override
	protected void systemOutDone() {
		TestUtils.getChatLogger().info(">> Created Daily-Backup.");
	}

	@Override
	protected boolean doBackup(final @NotNull DefinedRegion southRegion, final @NotNull DefinedRegion northRegion) {
		return true;
	}
}