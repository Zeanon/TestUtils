package de.zeanon.testutils.plugin.utils.backup;

import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import de.zeanon.testutils.plugin.utils.InternalFileUtils;
import de.zeanon.testutils.plugin.utils.enums.BackUpMode;
import de.zeanon.testutils.plugin.utils.interfaces.Backup;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;


public class StartupBackup extends Backup {

	public StartupBackup() {
		super(BackUpMode.STARTUP);
	}

	@Override
	protected void cleanup(final @NotNull File backupFolder) throws IOException {
		final @NotNull File startupBackup = new File(backupFolder, this.sequence.getPath(null));
		if (startupBackup.exists()) {
			@NotNull List<File> files = BaseFileUtils.listFolders(startupBackup);
			while (files.size() > ConfigUtils.getInt("Backups", "startup")) {
				final @NotNull Optional<File> toBeDeleted = files.stream().min(Comparator.comparingLong(File::lastModified));
				if (toBeDeleted.isPresent()) {
					FileUtils.deleteDirectory(toBeDeleted.get()); //NOSONAR
					InternalFileUtils.deleteEmptyParent(toBeDeleted.get(), new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps"));
					files = BaseFileUtils.listFolders(startupBackup);
				}
			}
		}
	}
}