package de.zeanon.testutils.plugin.commands.testutils.testarea;

import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.commands.backup.BackupCommand;
import de.zeanon.testutils.plugin.commands.testutils.TestUtilsCommand;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.enums.AreaName;
import de.zeanon.testutils.regionsystem.region.TestArea;
import java.io.File;
import java.io.IOException;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class DeleteArea {

	public void execute(final @NotNull AreaName name, final @NotNull Player p) {
		if (name.getName().contains("./") || name.getName().contains(".\\") || InitMode.forbiddenFileName(name.getName())) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Area '" + name.getName() + "' resolution error: Name is not allowed.");
			return;
		}

		if (DeleteArea.remove(name.getName())) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "You deleted the testarea '" + ChatColor.DARK_RED + name + ChatColor.RED + "'.");
		} else {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Testarea '" + ChatColor.DARK_RED + name + ChatColor.RED + "' does not exist and thus could not be deleted.");
		}
	}

	private boolean remove(final @NotNull String name) {
		final @Nullable TestArea southRegion = de.zeanon.testutils.regionsystem.region.RegionManager.getRegion(name + "_south");
		final @Nullable TestArea northRegion = de.zeanon.testutils.regionsystem.region.RegionManager.getRegion(name + "_north");
		if (southRegion != null && northRegion != null && de.zeanon.testutils.regionsystem.region.RegionManager.removeRegion(southRegion) && de.zeanon.testutils.regionsystem.region.RegionManager.removeRegion(northRegion)) {
			try {
				final @NotNull File resetFolder = TestUtilsCommand.TESTAREA_FOLDER.resolve(name.substring(0, name.length() - 6)).toFile();
				if (resetFolder.exists() && resetFolder.isDirectory()) {
					FileUtils.deleteDirectory(resetFolder);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				final @NotNull File backUpFolder = BackupCommand.BACKUP_FOLDER.resolve(name.substring(0, name.length() - 6)).toFile();
				if (backUpFolder.exists() && backUpFolder.isDirectory()) {
					FileUtils.deleteDirectory(backUpFolder);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			return true;
		} else {
			return false;
		}
	}
}