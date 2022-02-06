package de.zeanon.testutils.plugin.commands.testutils.testarea;

import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.commands.backup.BackupCommand;
import de.zeanon.testutils.plugin.commands.testutils.TestUtilsCommand;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.AreaName;
import de.zeanon.testutils.regionsystem.RegionManager;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class DeleteArea {

	public void execute(final @NotNull Player p, final @NotNull AreaName name) {
		if (TestAreaUtils.illegalName(name.getName())) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Area '" + name.getName() + "' resolution error: Name is not allowed.");
			return;
		}

		if (DeleteArea.delete(name.getName())) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "You deleted the testarea '" + ChatColor.DARK_RED + name + ChatColor.RED + "'.");
		} else {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Testarea '" + ChatColor.DARK_RED + name + ChatColor.RED + "' does not exist and thus could not be deleted.");
		}
	}

	private boolean delete(final @NotNull String name) {
		final @Nullable DefinedRegion southRegion = RegionManager.getDefinedRegion(name + "_south");
		final @Nullable DefinedRegion northRegion = RegionManager.getDefinedRegion(name + "_north");
		if (southRegion != null && northRegion != null && RegionManager.removeDefinedRegion(southRegion) && RegionManager.removeDefinedRegion(northRegion)) {
			try {
				final @NotNull File resetFolder = TestUtilsCommand.TESTAREA_FOLDER.resolve(name).toFile();
				if (resetFolder.exists() && resetFolder.isDirectory()) {
					BaseFileUtils.deleteDirectory(resetFolder);
				}
			} catch (final @NotNull IOException e) {
				TestUtils.getChatLogger().log(Level.SEVERE, String.format("Error while deleting resets for %s", name), e);
			}

			try {
				final @NotNull File backUpFolder = BackupCommand.BACKUP_FOLDER.resolve(name).toFile();
				if (backUpFolder.exists() && backUpFolder.isDirectory()) {
					BaseFileUtils.deleteDirectory(backUpFolder);
				}
			} catch (final @NotNull IOException e) {
				TestUtils.getChatLogger().log(Level.SEVERE, String.format("Error while deleting backups for %s", name), e);
			}

			return true;
		} else {
			return false;
		}
	}
}