package de.zeanon.testutils.plugin.commands.backup;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.backup.BackupScheduler;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.BackupMode;
import de.zeanon.testutils.plugin.utils.enums.MappedFile;
import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.logging.Level;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Load {

	public void execute(final @Nullable RegionSide regionSide, final @Nullable MappedFile mappedFile, final @NotNull BackupMode backupMode, final @NotNull Player p) {
		if (mappedFile != null && TestAreaUtils.illegalName(mappedFile.getName())) {
			p.sendMessage(BackupCommand.MESSAGE_HEAD
						  + ChatColor.RED + "Backup '" + mappedFile.getName() + "' resolution error: Name is not allowed.");
			return;
		}

		final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p, regionSide);
		final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p, regionSide);

		if (tempRegion == null || otherRegion == null) {
			GlobalMessageUtils.sendNotApplicableRegion(p);
			return;
		}

		try {
			final @NotNull File regionFolder = BackupCommand.BACKUP_FOLDER.resolve(tempRegion.getName().substring(0, tempRegion.getName().length() - 6)).toFile();
			if (regionFolder.exists() && regionFolder.isDirectory()) {
				final @Nullable File backupFile;

				if (mappedFile == null) {
					final @NotNull Optional<File> possibleFirst = BackupCommand.getLatest(regionFolder, p.getUniqueId().toString(), backupMode);

					if (possibleFirst.isPresent()) {
						backupFile = possibleFirst.get();
					} else {
						p.sendMessage(BackupCommand.MESSAGE_HEAD
									  + ChatColor.RED + "There is no backup for '"
									  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
						return;
					}
				} else {
					backupFile = BackupCommand.getFile(regionFolder, mappedFile.getName(), backupMode, p);
					if (backupFile == null || !backupFile.exists() || !backupFile.isDirectory()) {
						p.sendMessage(BackupCommand.MESSAGE_HEAD
									  + ChatColor.RED + "There is no backup named '" + ChatColor.DARK_RED + mappedFile.getName() + ChatColor.RED + "' for '"
									  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
						return;
					}
				}

				try (final @NotNull EditSession editSession = SessionFactory.createSession(p, new BukkitWorld(p.getWorld()))) {
					if (regionSide != null) {
						p.sendMessage(BackupCommand.MESSAGE_HEAD + ChatColor.RED + "Loading the " + (mappedFile == null ? "latest " + (backupMode != BackupMode.NONE ? backupMode + " " : "") + "backup" : "backup '" + ChatColor.DARK_RED + mappedFile + ChatColor.RED + "'") + " for " + regionSide + " side.");

						BackupScheduler.getMANUAL_BACKUP().pasteSide(tempRegion, editSession, new File(backupFile, tempRegion.getName().substring(tempRegion.getName().length() - 5) + ".schem"));

						p.sendMessage(BackupCommand.MESSAGE_HEAD
									  + ChatColor.RED + "You pasted the " + (mappedFile == null ? "latest " + (backupMode != BackupMode.NONE ? backupMode + " " : "") + "backup" : "backup '" + ChatColor.DARK_RED + mappedFile + ChatColor.RED + "'") + " for " + regionSide + " side.");
					} else {
						p.sendMessage(BackupCommand.MESSAGE_HEAD
									  + ChatColor.RED + "Loading the " + (mappedFile == null ? "latest " + (backupMode != BackupMode.NONE ? backupMode + " " : "") + "backup" : "backup '" + ChatColor.DARK_RED + mappedFile + ChatColor.RED + "'") + " for your Testarea.");

						BackupScheduler.getMANUAL_BACKUP().pasteSide(tempRegion, editSession, new File(backupFile, tempRegion.getName().substring(tempRegion.getName().length() - 5) + ".schem"));
						Bukkit.getScheduler().scheduleSyncDelayedTask(TestUtils.getInstance(), () -> {
							try {
								BackupScheduler.getMANUAL_BACKUP().pasteSide(otherRegion, editSession, new File(backupFile, otherRegion.getName().substring(tempRegion.getName().length() - 5) + ".schem"));
							} catch (final @NotNull WorldEditException e) {
								throw new RuntimeWorldeditException(e.getMessage(), e.getCause());
							} catch (final @NotNull IOException e) {
								throw new UncheckedIOException(e.getMessage(), e);
							}

							p.sendMessage(BackupCommand.MESSAGE_HEAD
										  + ChatColor.RED + "You pasted the " + (mappedFile == null ? "latest " + (backupMode != BackupMode.NONE ? backupMode + " " : "") + "backup" : "backup '" + ChatColor.DARK_RED + mappedFile + ChatColor.RED + "'") + " for your Testarea.");
						}, 5000);
					}
				} catch (final WorldEditException | IOException | RuntimeWorldeditException | UncheckedIOException e) {
					Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e.getCause());
				}
			} else {
				p.sendMessage(BackupCommand.MESSAGE_HEAD
							  + ChatColor.RED + "There is no backup for '"
							  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
			}
		} catch (final IOException e) {
			p.sendMessage(BackupCommand.MESSAGE_HEAD
						  + ChatColor.RED + "There has been an error, pasting the backup for '"
						  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e.getCause());
		}
	}

	@SuppressWarnings("unused")
	private static class RuntimeWorldeditException extends RuntimeException {

		private static final long serialVersionUID = -5310969195057453692L;

		public RuntimeWorldeditException() {
			super();
		}

		public RuntimeWorldeditException(final @NotNull String message) {
			super(message);
		}

		public RuntimeWorldeditException(final @NotNull String message, final @NotNull Throwable cause) {
			super(message, cause);
		}

		public RuntimeWorldeditException(final @NotNull Throwable cause) {
			super(cause);
		}
	}
}