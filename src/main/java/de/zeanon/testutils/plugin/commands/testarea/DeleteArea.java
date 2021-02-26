package de.zeanon.testutils.plugin.commands.testarea;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


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
		if (regionManager.getRegion("testarea_" + name + "_north") != null
			&& regionManager.getRegion("testarea_" + name + "_south") != null) {
			regionManager.removeRegion("testarea_" + name + "_north", RemovalStrategy.UNSET_PARENT_IN_CHILDREN);
			regionManager.removeRegion("testarea_" + name + "_south", RemovalStrategy.UNSET_PARENT_IN_CHILDREN);
			return true;
		} else {
			return false;
		}
	}
}
