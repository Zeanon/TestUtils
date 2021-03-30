package de.zeanon.testutils.plugin.commands.backup;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.backup.BackupScheduler;
import de.zeanon.testutils.plugin.utils.enums.BackupMode;
import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import de.zeanon.testutils.plugin.utils.region.Region;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Load {

	public void execute(final @NotNull RegionSide regionSide, final @Nullable String fileName, final @NotNull BackupMode backupMode, final @NotNull Player p) {
		final @Nullable World world = p.getWorld();
		final @Nullable Region tempRegion = regionSide == RegionSide.NONE ? TestAreaUtils.getRegion(p) : TestAreaUtils.getRegion(p, regionSide);
		final @Nullable Region otherRegion = regionSide == RegionSide.NONE ? TestAreaUtils.getOppositeRegion(p) : TestAreaUtils.getOppositeRegion(p, regionSide);

		if (tempRegion == null || otherRegion == null) {
			GlobalMessageUtils.sendNotApplicableRegion(p);
		} else {
			try {
				final @NotNull File regionFolder = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Backups/" + world.getName() + "/" + tempRegion.getName().substring(0, tempRegion.getName().length() - 6));
				if (regionFolder.exists()) {
					final @Nullable File backupFile;

					if (fileName == null) {
						final @NotNull Optional<File> possibleFirst = Backup.getLatest(regionFolder, p.getUniqueId().toString(), backupMode);

						if (possibleFirst.isPresent()) {
							backupFile = possibleFirst.get();
						} else {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "There is no backup for '"
										  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
							return;
						}
					} else {
						backupFile = Backup.getFile(regionFolder, fileName, backupMode, p);
						if (backupFile == null || !backupFile.exists()) {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "There is no backup named '" + ChatColor.DARK_RED + fileName + ChatColor.RED + "' for '"
										  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
							return;
						}
					}

					try (final @NotNull EditSession editSession = SessionFactory.createSession(p)) {
						if (regionSide != RegionSide.NONE) {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "Loading the " + (fileName == null ? "latest " + (backupMode != BackupMode.NONE ? backupMode : "") + " backup" : "backup '" + ChatColor.DARK_RED + fileName + ChatColor.RED + "'") + " for " + regionSide.toString() + " side.");

							BackupScheduler.getMANUAL_BACKUP().pasteSide(tempRegion, editSession, new File(backupFile, tempRegion.getName().substring(tempRegion.getName().length() - 5) + ".schem"));

							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "You pasted the " + (fileName == null ? "latest " + (backupMode != BackupMode.NONE ? backupMode : "") + " backup" : "backup '" + ChatColor.DARK_RED + fileName + ChatColor.RED + "'") + " for " + regionSide.toString() + " side.");
						} else {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "Loading the " + (fileName == null ? "latest " + (backupMode != BackupMode.NONE ? backupMode : "") + " backup" : "backup '" + ChatColor.DARK_RED + fileName + ChatColor.RED + "'") + " for your Testarea.");

							BackupScheduler.getMANUAL_BACKUP().pasteSide(tempRegion, editSession, new File(backupFile, tempRegion.getName().substring(tempRegion.getName().length() - 5) + ".schem"));
							BackupScheduler.getMANUAL_BACKUP().pasteSide(otherRegion, editSession, new File(backupFile, otherRegion.getName().substring(tempRegion.getName().length() - 5) + ".schem"));

							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "You pasted the " + (fileName == null ? "latest " + (backupMode != BackupMode.NONE ? backupMode : "") + " backup" : "backup '" + ChatColor.DARK_RED + fileName + ChatColor.RED + "'") + " for your Testarea.");
						}
					} catch (WorldEditException | IOException e) {
						e.printStackTrace();
					}
				} else {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "There is no backup for '"
								  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
				}
			} catch (IOException e) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "There has been an error, pasting the backup for '"
							  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
				e.printStackTrace();
			}
		}
	}
}