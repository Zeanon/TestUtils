package de.zeanon.testutils.plugin.commands.testarea;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.region.Region;
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

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length == 2) {
			if (DeleteArea.remove(new BukkitWorld(p.getWorld()), args[1])) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "You deleted the testarea '" + ChatColor.DARK_RED + args[1] + ChatColor.RED + "'.");
			} else {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Testarea '" + ChatColor.DARK_RED + args[1] + ChatColor.RED + "' does not exist and thus could not be deleted.");
			}
		} else if (args.length == 1) {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Missing argument for "
						  + ChatColor.YELLOW + "<"
						  + ChatColor.GOLD + "name"
						  + ChatColor.YELLOW + ">");
		} else {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Too many arguments.");
		}
	}

	private boolean remove(final @NotNull World world, final @NotNull String name) {
		RegionManager regionManager = Objects.notNull(InitMode.getRegionContainer().get(world));
		final @Nullable Region southRegion = de.zeanon.testutils.plugin.utils.region.RegionManager.getRegion(name + "_south");
		final @Nullable Region northRegion = de.zeanon.testutils.plugin.utils.region.RegionManager.getRegion(name + "_north");
		if (southRegion != null && northRegion != null && regionManager.getRegion("testarea_" + name + "_outside") != null) {
			de.zeanon.testutils.plugin.utils.region.RegionManager.removeRegion(southRegion);
			de.zeanon.testutils.plugin.utils.region.RegionManager.removeRegion(northRegion);
			regionManager.removeRegion("testarea_" + name + "_outside", RemovalStrategy.UNSET_PARENT_IN_CHILDREN);

			try {
				final @NotNull File resetFolder = new File(TestUtils.getInstance().getDataFolder(), "/TestAreas/" + world.getName() + "/" + name.substring(0, name.length() - 6));
				if (resetFolder.exists() && resetFolder.isDirectory()) {
					FileUtils.deleteDirectory(resetFolder);
				}
				final @NotNull File backUpFolder = new File(TestUtils.getInstance().getDataFolder(), "/Backups/" + world.getName() + "/" + name.substring(0, name.length() - 6));
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