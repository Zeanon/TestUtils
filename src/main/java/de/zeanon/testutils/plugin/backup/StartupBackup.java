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
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class StartupBackup extends Backup {

	public StartupBackup() {
		super(BackupMode.STARTUP);
	}

	@Override
	protected void cleanup(final @NotNull File backupFolder) {
		try {
			final @NotNull File startupBackup = new File(backupFolder, this.sequence.getPath(null));
			if (startupBackup.exists()) {
				final int size = ConfigUtils.getInt("Backups", "startup");
				final @NotNull File stopHere = BackupCommand.BACKUP_FOLDER.toFile();
				@Nullable final List<File> files = BaseFileUtils.listFolders(startupBackup);
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
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e.getCause());
		}
	}

	@Override
	protected void systemOutStart() {
		System.out.println("[" + TestUtils.getInstance().getName() + "] >> Creating Startup-Backup...");
	}

	@Override
	protected void systemOutDone() {
		System.out.println("[" + TestUtils.getInstance().getName() + "] >> Created Startup-Backup.");
	}

	@Override
	protected boolean doBackup(final @NotNull DefinedRegion southRegion, final @NotNull DefinedRegion northRegion) {
		return true;
	}
}