package de.zeanon.testutils.plugin.utils.backup;

import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import de.zeanon.testutils.plugin.utils.InternalFileUtils;
import de.zeanon.testutils.plugin.utils.enums.BackUpMode;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;


public class HourlyBackup extends Backup {

	public HourlyBackup() {
		super(BackUpMode.HOURLY);
	}

	@Override
	protected void cleanup(final @NotNull File backupFolder) {
		try {
			final @NotNull File hourlyBackup = new File(backupFolder, this.sequence.getPath(null));
			if (hourlyBackup.exists()) {
				@NotNull List<File> files = BaseFileUtils.listFolders(hourlyBackup);
				while (files.size() > ConfigUtils.getInt("Backups", "hourly")) {
					final @NotNull Optional<File> toBeDeleted = files.stream().min(Comparator.comparingLong(File::lastModified));
					if (toBeDeleted.isPresent()) {
						FileUtils.deleteDirectory(toBeDeleted.get()); //NOSONAR
						InternalFileUtils.deleteEmptyParent(toBeDeleted.get(), new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps"));
						files = BaseFileUtils.listFolders(hourlyBackup);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}