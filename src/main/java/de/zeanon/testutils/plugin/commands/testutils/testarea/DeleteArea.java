package de.zeanon.testutils.plugin.commands.testutils.testarea;

import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.enums.AreaName;
import de.zeanon.testutils.plugin.utils.region.DefinedRegion;
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
		if (DeleteArea.remove(name.getName())) {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "You deleted the testarea '" + ChatColor.DARK_RED + name + ChatColor.RED + "'.");
		} else {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Testarea '" + ChatColor.DARK_RED + name + ChatColor.RED + "' does not exist and thus could not be deleted.");
		}
	}

	private boolean remove(final @NotNull String name) {
		final @Nullable DefinedRegion southRegion = de.zeanon.testutils.plugin.utils.region.RegionManager.getRegion(name + "_south");
		final @Nullable DefinedRegion northRegion = de.zeanon.testutils.plugin.utils.region.RegionManager.getRegion(name + "_north");
		if (southRegion != null && northRegion != null && de.zeanon.testutils.plugin.utils.region.RegionManager.removeRegion(southRegion) && de.zeanon.testutils.plugin.utils.region.RegionManager.removeRegion(northRegion)) {
			try {
				final @NotNull File resetFolder = new File(TestUtils.getInstance().getDataFolder(), "/TestAreas/" + name.substring(0, name.length() - 6));
				if (resetFolder.exists() && resetFolder.isDirectory()) {
					FileUtils.deleteDirectory(resetFolder);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				final @NotNull File backUpFolder = new File(TestUtils.getInstance().getDataFolder(), "/Backups/" + name.substring(0, name.length() - 6));
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