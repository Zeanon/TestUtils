package de.zeanon.testutils.plugin.commands;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanagercore.external.browniescollections.GapList;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.utils.*;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Backup {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("save")) {
						if (args.length < 3) {
							final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
							final @Nullable ProtectedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);

							if (tempRegion == null || otherRegion == null) {
								GlobalMessageUtils.sendNotApplicableRegion(p);
							} else {
								final @NotNull String name;
								if (args.length == 1) {
									name = InitMode.getFormatter().format(System.currentTimeMillis());
								} else {
									name = args[1];
								}

								final @NotNull File manualBackup = new File(new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps/" + p.getWorld().getName() + "/" + tempRegion.getId().substring(9, tempRegion.getId().length() - 6)), "manual/" + p.getUniqueId());
								if (manualBackup.exists()) {
									try {
										@NotNull List<File> files;
										files = BaseFileUtils.listFolders(manualBackup);
										while (files.size() > ConfigUtils.getInt("Backups", "manual") - 1) {
											final @NotNull Optional<File> toBeDeleted = files.stream().min(Comparator.comparingLong(File::lastModified));
											if (toBeDeleted.isPresent()) {
												p.sendMessage(GlobalMessageUtils.messageHead
															  + ChatColor.RED + "You have more than " + ConfigUtils.getInt("Backups", "manual") + " backups, deleting '" + ChatColor.DARK_RED + toBeDeleted.get().getName() + ChatColor.RED + "' due to it being the oldest."); //NOSONAR
												FileUtils.deleteDirectory(toBeDeleted.get()); //NOSONAR
												InternalFileUtils.deleteEmptyParent(toBeDeleted.get(), new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps"));
											}
											files = BaseFileUtils.listFolders(manualBackup);
										}
									} catch (IOException exception) {
										exception.printStackTrace();
									}
								}

								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "Registering Backup for '"
											  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6)
											  + ChatColor.RED + "'...");

								InitMode.getManualBackUp().backupSide(new BukkitWorld(p.getWorld()), tempRegion, "manual/" + p.getUniqueId(), name);
								InitMode.getManualBackUp().backupSide(new BukkitWorld(p.getWorld()), otherRegion, "manual/" + p.getUniqueId(), name);

								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "You registered a new backup for '"
											  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6)
											  + ChatColor.RED + "' named '" + name + "'.");
							}
						} else {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "Too many arguments.");
						}
					} else if (args[0].equalsIgnoreCase("load")) {
						final @NotNull Pair<Pair<PasteSide, BackUpSequence>, String> modifiers = Backup.modifiers(args);

						Backup.executeLoad(modifiers.getKey().getKey(), modifiers.getKey().getValue(), modifiers.getValue(), p);
					} else if (args[0].equalsIgnoreCase("list")) {

					}
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}

	private void executeLoad(final @NotNull PasteSide pasteSide, final @NotNull BackUpSequence backUpSequence, final @Nullable String file, final @NotNull Player p) {
		final @Nullable World world = p.getWorld();
		final @Nullable ProtectedRegion tempRegion = pasteSide == PasteSide.ALL ? TestAreaUtils.getRegion(p) : TestAreaUtils.getRegion(p, pasteSide);
		final @Nullable ProtectedRegion otherRegion = pasteSide == PasteSide.ALL ? TestAreaUtils.getOppositeRegion(p) : TestAreaUtils.getOppositeRegion(p, pasteSide);

		if (tempRegion == null || otherRegion == null) {
			GlobalMessageUtils.sendNotApplicableRegion(p);
		} else {
			try {
				final @NotNull File regionFolder = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps/" + world.getName() + "/" + tempRegion.getId().substring(9, tempRegion.getId().length() - 6));
				if (regionFolder.exists()) {
					final @NotNull File backupFile;

					if (file == null) {
						final @NotNull Optional<File> possibleFirst = Backup.getOldest(regionFolder, p.getUniqueId().toString(), backUpSequence);

						if (possibleFirst.isPresent()) {
							backupFile = possibleFirst.get();
						} else {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "There is no backup for '"
										  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
							return;
						}
					} else {
						backupFile = backUpSequence == BackUpSequence.NONE || backUpSequence == BackUpSequence.MANUAL ? new File(regionFolder, "manual/" + p.getUniqueId().toString() + "/" + file)
																													  : new File(backUpSequence.toString() + "/" + file);
						if (!backupFile.exists()) {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "There is no backup named '" + ChatColor.DARK_RED + file + ChatColor.RED + "' for '"
										  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
							return;
						}
					}

					new BukkitRunnable() {
						@Override
						public void run() {
							try (final @NotNull EditSession editSession = SessionFactory.createSession(p)) {
								if (pasteSide != PasteSide.ALL) {
									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "Loading the " + (file == null ? "latest " + (backUpSequence != BackUpSequence.NONE ? backUpSequence.name().toLowerCase() : "") + " backup" : "backup '" + ChatColor.DARK_RED + file + ChatColor.RED + "'") + " for " + pasteSide.toString() + " side.");

									InitMode.getManualBackUp().pasteSide(tempRegion, editSession, new File(backupFile, tempRegion.getId().substring(tempRegion.getId().length() - 5) + ".schem"));

									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "You pasted the " + (file == null ? "latest " + (backUpSequence != BackUpSequence.NONE ? backUpSequence.name().toLowerCase() : "") + " backup" : "backup '" + ChatColor.DARK_RED + file + ChatColor.RED + "'") + " for " + pasteSide.toString() + " side.");
								} else {
									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "Loading the " + (file == null ? "latest " + (backUpSequence != BackUpSequence.NONE ? backUpSequence.name().toLowerCase() : "") + " backup" : "backup '" + ChatColor.DARK_RED + file + ChatColor.RED + "'") + " for your Testarea.");

									InitMode.getManualBackUp().pasteSide(tempRegion, editSession, new File(backupFile, tempRegion.getId().substring(tempRegion.getId().length() - 5) + ".schem"));
									InitMode.getManualBackUp().pasteSide(otherRegion, editSession, new File(backupFile, otherRegion.getId().substring(tempRegion.getId().length() - 5) + ".schem"));

									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "You pasted the " + (file == null ? "latest " + (backUpSequence != BackUpSequence.NONE ? backUpSequence.name().toLowerCase() : "") + " backup" : "backup '" + ChatColor.DARK_RED + file + ChatColor.RED + "'") + " for your Testarea.");
								}
							} catch (WorldEditException | IOException e) {
								e.printStackTrace();
							}
						}
					}.runTask(TestUtils.getInstance());
				} else {
					p.sendMessage(GlobalMessageUtils.messageHead //TODO Better messages
								  + ChatColor.RED + "There is no backup for '"
								  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
				}
			} catch (IOException e) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "There has been an error, pasting the backup for '"
							  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
				e.printStackTrace();
			}
		}
	}

	private Optional<File> getOldest(final @NotNull File regionFolder, final @NotNull String uuid) throws IOException {
		final @NotNull List<File> backups = new GapList<>();

		Backup.getOldestManual(regionFolder, uuid).ifPresent(backups::add);

		Backup.getOldestStartup(regionFolder).ifPresent(backups::add);

		Backup.getOldestHourly(regionFolder).ifPresent(backups::add);

		Backup.getOldestDaily(regionFolder).ifPresent(backups::add);

		return backups.stream().min((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
	}

	private @NotNull Optional<File> getOldest(final @NotNull File regionFolder, final @NotNull String uuid, final @NotNull BackUpSequence backUpSequence) throws IOException {
		if (backUpSequence == BackUpSequence.NONE) {
			return Backup.getOldest(regionFolder, uuid);
		} else {
			switch (backUpSequence) {
				case MANUAL:
					return Backup.getOldestManual(regionFolder, uuid);
				case STARTUP:
					return Backup.getOldestStartup(regionFolder);
				case HOURLY:
					return Backup.getOldestHourly(regionFolder);
				case DAILY:
					return Backup.getOldestDaily(regionFolder);
				default:
					return Backup.getOldest(regionFolder, uuid);
			}
		}
	}

	private Optional<File> getOldestManual(final @NotNull File regionFolder, final @NotNull String uuid) throws IOException {
		@NotNull Optional<File> possibleFirst = BaseFileUtils.listFolders(new File(regionFolder, "manual/" + uuid)).stream().min((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

		return possibleFirst;
	}

	private Optional<File> getOldestStartup(final @NotNull File regionFolder) throws IOException {
		@NotNull Optional<File> possibleFirst = BaseFileUtils.listFolders(new File(regionFolder, "automatic/startup")).stream().min((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

		return possibleFirst;
	}

	private Optional<File> getOldestHourly(final @NotNull File regionFolder) throws IOException {
		@NotNull Optional<File> possibleFirst = BaseFileUtils.listFolders(new File(regionFolder, "automatic/hourly")).stream().min((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

		return possibleFirst;
	}

	private Optional<File> getOldestDaily(final @NotNull File regionFolder) throws IOException {
		@NotNull Optional<File> possibleFirst = BaseFileUtils.listFolders(new File(regionFolder, "automatic/daily")).stream().min((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

		return possibleFirst;
	}

	private @NotNull Pair<Pair<PasteSide, BackUpSequence>, String> modifiers(final @NotNull String[] args) {
		final @NotNull Pair<Pair<PasteSide, BackUpSequence>, String> result = new Pair<>(new Pair<>(PasteSide.ALL, BackUpSequence.NONE), null);
		for (final @NotNull String arg : args) {
			if (PasteSide.parse(arg) != PasteSide.NONE) {
				result.getKey().setKey(PasteSide.parse(arg));
			} else if (BackUpSequence.parse(arg) != BackUpSequence.NONE) {
				result.getKey().setValue(BackUpSequence.parse(arg));
			} else if (!arg.equalsIgnoreCase("load")) {
				result.setValue(arg);
			}
		}

		return result;
	}
}