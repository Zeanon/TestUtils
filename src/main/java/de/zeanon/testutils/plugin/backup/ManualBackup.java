package de.zeanon.testutils.plugin.backup;

import de.zeanon.testutils.plugin.utils.enums.BackupMode;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.io.File;
import org.jetbrains.annotations.NotNull;


public class ManualBackup extends Backup {

	public ManualBackup() {
		super(BackupMode.MANUAL);
	}

	@Override
	public void run() {
		throw new UnsupportedOperationException("This method is not supported for manual backups.");
	}

	@Override
	protected void cleanup(final @NotNull File backupFolder) {
		throw new UnsupportedOperationException("This method is not supported for manual backups.");
	}

	@Override
	protected void systemOutStart() {
		throw new UnsupportedOperationException("This method is not supported for manual backups.");
	}

	@Override
	protected void systemOutDone() {
		throw new UnsupportedOperationException("This method is not supported for manual backups.");
	}

	@Override
	protected boolean doBackup(final @NotNull DefinedRegion southRegion, final @NotNull DefinedRegion northRegion) {
		throw new UnsupportedOperationException("This method is not supported for manual backups.");
	}
}