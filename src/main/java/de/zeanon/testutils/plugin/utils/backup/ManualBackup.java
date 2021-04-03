package de.zeanon.testutils.plugin.utils.backup;

import de.zeanon.testutils.plugin.utils.enums.BackupMode;
import de.zeanon.testutils.regionsystem.region.TestArea;
import java.io.File;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;


public class ManualBackup extends Backup {

	public ManualBackup() {
		super(BackupMode.MANUAL);
	}

	@SneakyThrows
	@Override
	public void run() {
		throw new NoSuchMethodException("This method is not supported for manual backups.");
	}

	@Override
	protected void cleanup(final @NotNull File backupFolder) {
		//NOTHING
	}

	@Override
	protected void systemOutStart() {
		//DO NOTHING
	}

	@Override
	protected void systemOutDone() {
		//DO NOTHING
	}

	@Override
	protected boolean doBackup(final @NotNull TestArea southRegion, final @NotNull TestArea northRegion) {
		return false;
	}
}