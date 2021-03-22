package de.zeanon.testutils.plugin.commands.backup;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.BackUpMode;
import de.zeanon.testutils.plugin.utils.enums.PasteSide;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class List {

	public void executeList(final @NotNull Backup.ModifierBlock modifiers, final @NotNull Player p) {
		final @Nullable World world = p.getWorld();
		final @Nullable ProtectedRegion tempRegion = modifiers.getPasteSide() == PasteSide.NONE ? TestAreaUtils.getRegion(p) : TestAreaUtils.getRegion(p, modifiers.getPasteSide());
		final @Nullable ProtectedRegion otherRegion = modifiers.getPasteSide() == PasteSide.NONE ? TestAreaUtils.getOppositeRegion(p) : TestAreaUtils.getOppositeRegion(p, modifiers.getPasteSide());

		if (tempRegion == null || otherRegion == null) {
			GlobalMessageUtils.sendNotApplicableRegion(p);
		} else {
			try {
				final @NotNull File regionFolder = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps/" + world.getName() + "/" + tempRegion.getId().substring(9, tempRegion.getId().length() - 6));
				if (!regionFolder.exists()) {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "There is no " + modifiers.getBackUpMode() + " backup for '"
								  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
				} else {
					if (modifiers.getFileName() != null) {
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "Argument '" + ChatColor.DARK_RED + modifiers.getFileName() + ChatColor.RED + "' is not applicable here.");
					} else {

						final @NotNull Map<String, java.util.List<File>> files = new HashMap<>();
						if (modifiers.getBackUpMode() == BackUpMode.NONE) {
							files.put("manual", BaseFileUtils.listFolders(new File(regionFolder, "manual/" + p.getUniqueId()))
															 .stream()
															 .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))
															 .collect(Collectors.toList()));
							files.put("hourly", BaseFileUtils.listFolders(new File(regionFolder, "automatic/hourly")).stream()
															 .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))
															 .collect(Collectors.toList()));
							files.put("daily", BaseFileUtils.listFolders(new File(regionFolder, "automatic/startup")).stream()
															.sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))
															.collect(Collectors.toList()));
							files.put("startup", BaseFileUtils.listFolders(new File(regionFolder, "automatic/startup")).stream()
															  .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))
															  .collect(Collectors.toList()));
						} else if (modifiers.getBackUpMode() == BackUpMode.MANUAL) {
							files.put(modifiers.getBackUpMode().toString(), BaseFileUtils.listFolders(new File(regionFolder, modifiers.getBackUpMode().getPath() + "/" + p.getUniqueId())).stream()
																						 .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))
																						 .collect(Collectors.toList()));
						} else {
							files.put(modifiers.getBackUpMode().toString(), BaseFileUtils.listFolders(new File(regionFolder, modifiers.getBackUpMode().getPath())).stream()
																						 .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))
																						 .collect(Collectors.toList()));
						}

						for (final @NotNull Map.Entry<String, java.util.List<File>> entry : files.entrySet()) {
							p.sendMessage(ChatColor.AQUA + "=== " + entry.getKey() + " ===");

							for (final @NotNull File file : entry.getValue()) {
								Backup.sendLoadBackupMessage("", ChatColor.GOLD + file.getName(),
															 ChatColor.RED + "Paste the backup '" + ChatColor.DARK_RED + file.getName() + ChatColor.RED + "' for this TestArea.",
															 "/backup load " + file.getName() + " -" + entry.getKey(),
															 p);
							}

							p.sendMessage("");
						}
					}
				}
			} catch (IOException e) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "There has been an error, listing the backup for '"
							  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
				e.printStackTrace();
			}
		}
	}
}