package de.zeanon.testutils.plugin.commands.backup;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.InternalFileUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Delete {

	public void execute(@NotNull String @NotNull [] args, @NotNull Player p) {
		if (ConfigUtils.getInt("Backups", "manual") > 0) {
			if (args.length < 3) {
				final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
				final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);

				if (tempRegion == null || otherRegion == null) {
					GlobalMessageUtils.sendNotApplicableRegion(p);
				} else {
					if (args.length == 1) {
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "Missing argument for "
									  + ChatColor.YELLOW + "<"
									  + ChatColor.GREEN + "filename"
									  + ChatColor.YELLOW + ">");
					} else {
						final @NotNull String name = args[1];

						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "Registering Backup for '"
									  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6)
									  + ChatColor.RED + "'...");



						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "You registered a new backup for '"
									  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6)
									  + ChatColor.RED + "' named '" + ChatColor.DARK_RED + name + ChatColor.RED + "'.");


						new BukkitRunnable() {
							@Override
							public void run() {
								final @NotNull File manualBackup = new File(new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps/" + p.getWorld().getName() + "/" + tempRegion.getId().substring(9, tempRegion.getId().length() - 6)), "manual/" + p.getUniqueId());
								if (manualBackup.exists()) {
									try {
										@NotNull List<File> files;
										files = BaseFileUtils.listFolders(manualBackup);
										while (files.size() > ConfigUtils.getInt("Backups", "manual")) {
											final @NotNull Optional<File> toBeDeleted = files.stream().min(Comparator.comparingLong(File::lastModified));
											if (toBeDeleted.isPresent()) {
												p.sendMessage(GlobalMessageUtils.messageHead
															  + ChatColor.RED + "You have more than " + ConfigUtils.getInt("Backups", "manual") + " backups, deleting '" + ChatColor.DARK_RED + toBeDeleted.get().getName() + ChatColor.RED + "' due to it being the oldest."); //NOSONAR
												FileUtils.deleteDirectory(toBeDeleted.get()); //NOSONAR
												InternalFileUtils.deleteEmptyParent(toBeDeleted.get(), new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps"));
											}
											files = BaseFileUtils.listFolders(manualBackup);
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}.runTaskAsynchronously(TestUtils.getInstance());
					}
				}
			} else {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Too many arguments.");
			}
		} else {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Manual backups are disabled.");
		}
	}
}