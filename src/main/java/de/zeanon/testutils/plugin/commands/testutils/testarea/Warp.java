package de.zeanon.testutils.plugin.commands.testutils.testarea;

import de.zeanon.jsonfilemanager.JsonFileManager;
import de.zeanon.jsonfilemanager.internal.files.raw.JsonFile;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.commands.testutils.TestUtilsCommand;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.enums.AreaName;
import java.io.File;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class Warp {

	public void execute(final @NotNull AreaName name, final @NotNull Player p) {
		if (name.getName().contains("./") || name.getName().contains(".\\") || InitMode.forbiddenFileName(name.getName())) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Area '" + name.getName() + "' resolution error: Name is not allowed.");
			return;
		}

		final @NotNull File warpData = TestUtilsCommand.TESTAREA_FOLDER.resolve(name.getName()).resolve("warp.json").toFile();
		if (!warpData.exists()) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "The given region does not exist.");
			return;
		}

		final @NotNull JsonFile jsonFile = JsonFileManager.jsonFile(warpData)
														  .fromResource("resources/warp.json")
														  .create();
		final @NotNull Location teleport = new Location(Bukkit.getWorld(Objects.notNull(jsonFile.getStringUseArray("world"))),
														jsonFile.getDoubleUseArray("x"),
														jsonFile.getDoubleUseArray("y"),
														jsonFile.getDoubleUseArray("z"),
														180,
														0);

		p.teleport(teleport);
		p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
					  + ChatColor.RED + "You have been teleported to '" + ChatColor.DARK_RED + name.getName() + ChatColor.RED + "'.");
	}
}