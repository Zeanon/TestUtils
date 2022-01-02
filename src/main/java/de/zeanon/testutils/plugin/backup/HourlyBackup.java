package de.zeanon.testutils.plugin.backup;

import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.commands.backup.BackupCommand;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import de.zeanon.testutils.plugin.utils.InternalFileUtils;
import de.zeanon.testutils.plugin.utils.enums.BackupMode;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import de.zeanon.testutils.regionsystem.tags.Tag;
import de.zeanon.testutils.regionsystem.tags.tagvalues.CHANGED;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class HourlyBackup extends Backup {

	public HourlyBackup() {
		super(BackupMode.HOURLY);
	}

	@Override
	protected void cleanup(final @NotNull File backupFolder) {
		try {
			final @NotNull File hourlyBackup = new File(backupFolder, this.sequence.getPath(null));
			if (hourlyBackup.exists()) {
				final int size = ConfigUtils.getInt("Backups", "hourly");
				final @NotNull File stopHere = BackupCommand.BACKUP_FOLDER.toFile();
				@Nullable final List<File> files = BaseFileUtils.listFolders(hourlyBackup);
				if (files != null && files.size() > size) {
					files.sort(Comparator.comparingLong(File::lastModified));
					do {
						final @NotNull File file = files.remove(0);
						FileUtils.deleteDirectory(file);
						InternalFileUtils.deleteEmptyParent(file, stopHere);
					} while (files.size() > size);
				}
			}
		} catch (final IOException e) {
			TestUtils.getChatLogger().log(Level.SEVERE, "Error while performing hourly backup", e);
		}
	}

	@Override
	protected void systemOutStart() {
		TestUtils.getChatLogger().info(">> Creating Hourly-Backup...");
	}

	@Override
	protected void systemOutDone() {
		TestUtils.getChatLogger().info(">> Created Hourly-Backup.");
	}

	@Override
	protected boolean doBackup(final @NotNull DefinedRegion southRegion, final @NotNull DefinedRegion northRegion) {
		return southRegion.getTag(Tag.CHANGED) == CHANGED.TRUE || northRegion.getTag(Tag.CHANGED) == CHANGED.TRUE;
	}
}