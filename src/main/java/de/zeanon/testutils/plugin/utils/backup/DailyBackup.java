package de.zeanon.testutils.plugin.utils.backup;


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
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;


public class DailyBackup extends Backup {

	public DailyBackup() {
		super(BackupMode.DAILY);
	}

	@Override
	protected void cleanup(final @NotNull File backupFolder) {
		try {
			final @NotNull File dailyBackup = new File(backupFolder, DailyBackup.this.sequence.getPath(null));
			if (dailyBackup.exists()) {
				@NotNull List<File> files = BaseFileUtils.listFolders(dailyBackup);
				while (files.size() > ConfigUtils.getInt("Backups", "daily")) {
					final @NotNull Optional<File> toBeDeleted = files.stream().min(Comparator.comparingLong(File::lastModified));
					if (toBeDeleted.isPresent()) {
						FileUtils.deleteDirectory(toBeDeleted.get()); //NOSONAR
						InternalFileUtils.deleteEmptyParent(toBeDeleted.get(), BackupCommand.BACKUP_FOLDER.toFile());
						files = BaseFileUtils.listFolders(dailyBackup);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void systemOutStart() {
		System.out.println("[" + TestUtils.getInstance().getName() + "] >> Creating Daily-Backup...");
	}

	@Override
	protected void systemOutDone() {
		System.out.println("[" + TestUtils.getInstance().getName() + "] >> Created Daily-Backup.");
	}

	@Override
	protected boolean doBackup(final @NotNull DefinedRegion southRegion, final @NotNull DefinedRegion northRegion) {
		return true;
	}
}