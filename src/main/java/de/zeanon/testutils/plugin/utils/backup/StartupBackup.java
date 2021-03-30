package de.zeanon.testutils.plugin.utils.backup;

import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import de.zeanon.testutils.plugin.utils.InternalFileUtils;
import de.zeanon.testutils.plugin.utils.enums.BackupMode;
import de.zeanon.testutils.plugin.utils.region.DefinedRegion;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;


public class StartupBackup extends Backup {

	public StartupBackup() {
		super(BackupMode.STARTUP);
	}

	@Override
	protected void cleanup(final @NotNull File backupFolder) {
		try {
			final @NotNull File startupBackup = new File(backupFolder, StartupBackup.this.sequence.getPath(null));
			if (startupBackup.exists()) {
				@NotNull List<File> files = BaseFileUtils.listFolders(startupBackup);
				while (files.size() > ConfigUtils.getInt("Backups", "startup")) {
					final @NotNull Optional<File> toBeDeleted = files.stream().min(Comparator.comparingLong(File::lastModified));
					if (toBeDeleted.isPresent()) {
						FileUtils.deleteDirectory(toBeDeleted.get()); //NOSONAR
						InternalFileUtils.deleteEmptyParent(toBeDeleted.get(), new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Backups"));
						files = BaseFileUtils.listFolders(startupBackup);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
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
		return southRegion.hasChanged() || northRegion.hasChanged();
	}
}