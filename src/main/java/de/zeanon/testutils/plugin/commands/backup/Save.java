package de.zeanon.testutils.plugin.commands.backup;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.utils.*;
import de.zeanon.testutils.plugin.utils.backup.BackUpScheduler;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
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
public class Save {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (ConfigUtils.getInt("Backups", "manual") > 0) {
			if (args.length > 1 && args[1].contains("./")) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "File '" + args[1] + "' resolution error: Path is not allowed.");
			} else if (args.length > 3 || (args.length > 2
										   && CommandRequestUtils.checkOverwriteBackupRequest(p.getUniqueId(), args[1]) == null
										   && !args[2].equalsIgnoreCase("-confirm")
										   && !args[2].equalsIgnoreCase("-deny"))) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Too many arguments.");
			} else {
				Save.executeInternally(args, p);
			}
		} else {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Manual backups are disabled.");
		}
	}

	private void executeInternally(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length < 3) {
			final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
			final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getSouthRegion(p);

			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
			} else {
				final @NotNull String name;
				if (args.length == 1) {
					name = LocalDateTime.now().format(InitMode.getFormatter());
				} else {
					name = args[1];
				}

				final @NotNull World tempWorld = new BukkitWorld(p.getWorld());
				final @NotNull File folder = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps/" + tempWorld.getName() + "/" + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + "/manual/" + p.getUniqueId() + "/" + name);
				if (folder.exists()) {
					CommandRequestUtils.addOverwriteBackupRequest(p.getUniqueId(), name, tempRegion.getId().substring(9, tempRegion.getId().length() - 6));
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "The Backup " + ChatColor.DARK_RED + name + ChatColor.RED + " already exists.");
					GlobalMessageUtils.sendBooleanMessage(ChatColor.RED + "Do you want to overwrite " + ChatColor.DARK_RED + name + ChatColor.RED + "?",
														  "/backup save " + name + " confirm",
														  "/backup save " + name + " deny", p);
				} else {
					Save.save(tempWorld, tempRegion, otherRegion, name, folder, p);
				}
			}
		} else {
			final @Nullable String region = CommandRequestUtils.checkOverwriteBackupRequest(p.getUniqueId(), args[1]);
			if (region != null) {
				if (args[2].equalsIgnoreCase("-confirm")) {
					CommandRequestUtils.removeOverwriteBackupRequest(p.getUniqueId());

					final @NotNull World tempWorld = new BukkitWorld(p.getWorld());
					final @NotNull File folder = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps/" + tempWorld.getName() + "/" + region + "/manual/" + p.getUniqueId() + "/" + args[1]);
					final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getNorthRegion(tempWorld, region);
					final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getSouthRegion(tempWorld, region);
					if (tempRegion == null || otherRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						Save.save(tempWorld, tempRegion, otherRegion, args[1], folder, p);
					}
				} else if (args[2].equalsIgnoreCase("-deny")) {
					CommandRequestUtils.removeOverwriteBackupRequest(p.getUniqueId());
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.DARK_RED + args[1] + ChatColor.RED + " was not overwritten.");
				}
			}
		}
	}

	private void save(final @NotNull World tempWorld, final @NotNull ProtectedRegion tempRegion, final @NotNull ProtectedRegion otherRegion, final @NotNull String name, final @NotNull File folder, final @NotNull Player p) {
		p.sendMessage(GlobalMessageUtils.messageHead
					  + ChatColor.RED + "Registering Backup for '"
					  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6)
					  + ChatColor.RED + "'...");
		BackUpScheduler.getManualBackup().backupSide(tempWorld, tempRegion, folder);

		BackUpScheduler.getManualBackup().backupSide(tempWorld, otherRegion, folder);

		p.sendMessage(GlobalMessageUtils.messageHead
					  + ChatColor.RED + "You registered a new backup for '"
					  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6)
					  + ChatColor.RED + "' named '" + ChatColor.DARK_RED + name + ChatColor.RED + "'.");


		new BukkitRunnable() {
			@Override
			public void run() {
				final @NotNull File manualBackup = new File(TestUtils.getInstance().getDataFolder(), "BackUps/" + p.getWorld().getName() + "/" + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + "/manual/" + p.getUniqueId());
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