package de.zeanon.testutils.plugin.commands.backup;

import de.steamwar.commandframework.SWCommand;
import de.steamwar.commandframework.TypeMapper;
import de.zeanon.storagemanagercore.external.browniescollections.GapList;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.CommandRequestUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.BackupMode;
import de.zeanon.testutils.plugin.utils.enums.CommandConfirmation;
import de.zeanon.testutils.plugin.utils.enums.MappedFile;
import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class BackupCommand extends SWCommand {

	public static final @NotNull String MESSAGE_HEAD = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Backups" + ChatColor.DARK_GRAY + "] ";
	public static final Path BACKUP_FOLDER = TestUtils.getPluginFolder().resolve("Backups");

	public BackupCommand() {
		super("backup", true);
	}

	public static @NotNull Optional<File> getLatest(final @NotNull File regionFolder, final @NotNull String uuid, final @Nullable BackupMode backUpMode) throws IOException {
		if (backUpMode == null) {
			return BackupCommand.getLatest(regionFolder, uuid);
		} else {
			switch (backUpMode) {
				case MANUAL:
					return BackupCommand.getLatestManual(regionFolder, uuid);
				case STARTUP:
					return BackupCommand.getLatestStartup(regionFolder);
				case HOURLY:
					return BackupCommand.getLatestHourly(regionFolder);
				case DAILY:
					return BackupCommand.getLatestDaily(regionFolder);
				default:
					return BackupCommand.getLatest(regionFolder, uuid);
			}
		}
	}

	public static void sendLoadBackupMessage(final @NotNull String fileName,
											 final @NotNull String backupMode,
											 final @NotNull Player target) {
		final @NotNull String command = "/backup load " + fileName + " -" + backupMode;
		final @NotNull TextComponent localMessage = new TextComponent(
				TextComponent.fromLegacyText(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + backupMode + ChatColor.DARK_GRAY + "] "));
		final @NotNull TextComponent message = new TextComponent(
				TextComponent.fromLegacyText(ChatColor.RED + fileName));
		final @NotNull TextComponent separator = new TextComponent(
				TextComponent.fromLegacyText(ChatColor.BLACK + " " + ChatColor.BOLD + "|" + ChatColor.BLACK + " "));
		final @NotNull TextComponent northSide = new TextComponent(
				TextComponent.fromLegacyText(ChatColor.DARK_RED + "N"));
		final @NotNull TextComponent southSide = new TextComponent(
				TextComponent.fromLegacyText(ChatColor.DARK_RED + "S"));
		final @NotNull TextComponent yourSide = new TextComponent(
				TextComponent.fromLegacyText(ChatColor.DARK_RED + "H"));
		final @NotNull TextComponent otherSide = new TextComponent(
				TextComponent.fromLegacyText(ChatColor.DARK_RED + "O"));


		message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
											 command));
		northSide.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
											   command + " -n"));
		southSide.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
											   command + " -s"));
		yourSide.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
											  command + " -here"));
		otherSide.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
											   command + " -other"));
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											 new ComponentBuilder(ChatColor.RED + "Paste the backup '" + ChatColor.DARK_RED + fileName + ChatColor.RED + "' for this TestArea.").create()));
		northSide.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											   new ComponentBuilder(ChatColor.RED + "Paste it on the north side.").create()));
		southSide.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											   new ComponentBuilder(ChatColor.RED + "Paste it on the south side.").create()));
		yourSide.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											  new ComponentBuilder(ChatColor.RED + "Paste it on your side.").create()));
		otherSide.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											   new ComponentBuilder(ChatColor.RED + "Paste it on the other side.").create()));


		localMessage.addExtra(message);
		localMessage.addExtra("  ");
		localMessage.addExtra(northSide);
		localMessage.addExtra(separator);
		localMessage.addExtra(southSide);
		localMessage.addExtra(separator);
		localMessage.addExtra(yourSide);
		localMessage.addExtra(separator);
		localMessage.addExtra(otherSide);

		target.spigot().sendMessage(localMessage);
	}

	public static @Nullable File getFile(final @NotNull File regionFolder, final @NotNull String name, final @NotNull BackupMode backUpMode, final @NotNull Player p) throws IOException {
		switch (backUpMode) {
			case NONE:
				final @NotNull File manualBackup = new File(regionFolder, "manual/" + p.getUniqueId());
				if (manualBackup.exists() && manualBackup.isDirectory()) {
					for (final @NotNull File temp : BaseFileUtils.searchFolders(manualBackup, name)) {
						if (temp.getName().equals(name)) {
							return temp;
						}
					}
				}

				final @NotNull File hourlyBackup = new File(regionFolder, "automatic/hourly");
				if (hourlyBackup.exists() && hourlyBackup.isDirectory()) {
					for (final @NotNull File temp : BaseFileUtils.searchFolders(hourlyBackup, name)) {
						if (temp.getName().equals(name)) {
							return temp;
						}
					}
				}

				final @NotNull File dailyBackup = new File(regionFolder, "automatic/daily");
				if (dailyBackup.exists() && dailyBackup.isDirectory()) {
					for (final @NotNull File temp : BaseFileUtils.searchFolders(dailyBackup, name)) {
						if (temp.getName().equals(name)) {
							return temp;
						}
					}
				}

				final @NotNull File startupBackup = new File(regionFolder, "automatic/startup");
				if (startupBackup.exists() && startupBackup.isDirectory()) {
					for (final @NotNull File temp : BaseFileUtils.searchFolders(startupBackup, name)) {
						if (temp.getName().equals(name)) {
							return temp;
						}
					}
				}
				return null;
			case MANUAL:
				return new File(regionFolder, backUpMode.getPath(p.getUniqueId().toString()) + "/" + name);
			default:
				return new File(regionFolder, backUpMode.getPath(null) + "/" + name);
		}
	}

	@Register(help = true)
	public void help(final @NotNull Player p, final @NotNull String... args) {
		if (args.length == 0) {
			p.sendMessage(BackupCommand.MESSAGE_HEAD
						  + ChatColor.RED + "Missing argument.");
		} else {
			p.sendMessage(BackupCommand.MESSAGE_HEAD
						  + ChatColor.RED + "Unknown argument '" + ChatColor.DARK_RED + args[args.length - 1] + ChatColor.RED + "'.");
		}
	}

	@Register({"load"})
	public void noArgsLoad(final @NotNull Player p) {
		Load.execute(null, null, null, p);
	}

	@Register({"load"})
	public void oneArgLoad(final @NotNull Player p, final @NotNull RegionSide regionSide) {
		Load.execute(regionSide, null, null, p);
	}

	@Register({"load"})
	public void oneArgLoad(final @NotNull Player p, final @NotNull MappedFile mappedFile) {
		Load.execute(null, mappedFile, null, p);
	}

	@Register({"load"})
	public void oneArgLoad(final @NotNull Player p, final @NotNull BackupMode backupMode) {
		Load.execute(null, null, backupMode, p);
	}

	@Register({"load"})
	public void twoArgsLoad(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull MappedFile mappedFile) {
		Load.execute(regionSide, mappedFile, null, p);
	}

	@Register({"load"})
	public void twoArgsLoad(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull BackupMode backupMode) {
		Load.execute(regionSide, null, backupMode, p);
	}

	@Register({"load"})
	public void twoArgsLoad(final @NotNull Player p, final @NotNull BackupMode backupMode, final @NotNull MappedFile mappedFile) {
		Load.execute(null, mappedFile, backupMode, p);
	}

	@Register({"load"})
	public void twoArgsLoad(final @NotNull Player p, final @NotNull BackupMode backupMode, final @NotNull RegionSide regionSide) {
		Load.execute(regionSide, null, backupMode, p);
	}

	@Register({"load"})
	public void twoArgsLoad(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull BackupMode backupMode) {
		Load.execute(null, mappedFile, backupMode, p);
	}

	@Register({"load"})
	public void twoArgsLoad(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull RegionSide regionSide) {
		Load.execute(regionSide, mappedFile, null, p);
	}

	@Register({"load"})
	public void threeArgsLoad(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull MappedFile mappedFile, final @NotNull BackupMode backupMode) {
		Load.execute(regionSide, mappedFile, backupMode, p);
	}

	@Register({"load"})
	public void threeArgsLoad(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull BackupMode backupMode, final @NotNull RegionSide regionSide) {
		Load.execute(regionSide, mappedFile, backupMode, p);
	}

	@Register({"load"})
	public void threeArgsLoad(final @NotNull Player p, final @NotNull BackupMode backupMode, final @NotNull RegionSide regionSide, final @NotNull MappedFile mappedFile) {
		Load.execute(regionSide, mappedFile, backupMode, p);
	}

	@Register({"load"})
	public void threeArgsLoad(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull RegionSide regionSide, final @NotNull BackupMode backupMode) {
		Load.execute(regionSide, mappedFile, backupMode, p);
	}

	@Register({"load"})
	public void threeArgsLoad(final @NotNull Player p, final @NotNull BackupMode backupMode, final @NotNull MappedFile mappedFile, final @NotNull RegionSide regionSide) {
		Load.execute(regionSide, mappedFile, backupMode, p);
	}

	@Register({"load"})
	public void threeArgsLoad(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull BackupMode backupMode, final @NotNull MappedFile mappedFile) {
		Load.execute(regionSide, mappedFile, backupMode, p);
	}

	@Register({"save"})
	public void noArgsSave(final @NotNull Player p) {
		Save.execute(null, null, p);
	}

	@Register({"save"})
	public void oneArgSave(final @NotNull Player p, final @NotNull MappedFile mappedFile) {
		Save.execute(mappedFile, null, p);
	}

	@Register({"save"})
	public void twoArgsSave(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull CommandConfirmation commandConfirmation) {
		Save.execute(mappedFile, commandConfirmation, p);
	}


	@Register(value = {"del"}, help = true)
	public void delHelp(final @NotNull Player p, final @NotNull String... args) {
		Delete.usage(p);
	}

	@Register({"del"})
	public void oneArgDel(final @NotNull Player p, final @NotNull MappedFile mappedFile) {
		Delete.execute(mappedFile, null, p);
	}

	@Register({"del"})
	public void twoArgsDel(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull CommandConfirmation commandConfirmation) {
		Delete.execute(mappedFile, commandConfirmation, p);
	}


	@Register(value = {"delete"}, help = true)
	public void deleteHelp(final @NotNull Player p, final @NotNull String... args) {
		Delete.usage(p);
	}

	@Register({"delete"})
	public void oneArgDelete(final @NotNull Player p, final @NotNull MappedFile mappedFile) {
		Delete.execute(mappedFile, null, p);
	}

	@Register({"delete"})
	public void twoArgsDelete(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull CommandConfirmation commandConfirmation) {
		Delete.execute(mappedFile, commandConfirmation, p);
	}


	@Register({"list"})
	public void noArgsList(final @NotNull Player p) {
		List.execute(null, p);
	}

	@Register({"list"})
	public void oneArgList(final @NotNull Player p, final @NotNull BackupMode backupMode) {
		List.execute(backupMode, p);
	}


	@Register(value = {"search"}, help = true)
	public void searchHelp(final @NotNull Player p, final @NotNull String... args) {
		p.sendMessage(BackupCommand.MESSAGE_HEAD
					  + ChatColor.RED + "Missing sequence to search for.");
	}

	@Register({"search"})
	public void oneArgSearch(final @NotNull Player p, final @NotNull MappedFile mappedFile) {
		Search.execute(mappedFile, null, p);
	}

	@Register({"search"})
	public void twoArgsSearch(final @NotNull Player p, final @NotNull BackupMode backupMode, final @NotNull MappedFile mappedFile) {
		Search.execute(mappedFile, backupMode, p);
	}

	@Register({"search"})
	public void twoArgsSearch(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull BackupMode backupMode) {
		Search.execute(mappedFile, backupMode, p);
	}


	private static Optional<File> getLatest(final @NotNull File regionFolder, final @NotNull String uuid) throws IOException {
		final @NotNull java.util.List<File> backups = new GapList<>();

		BackupCommand.getLatestManual(regionFolder, uuid).ifPresent(backups::add);

		BackupCommand.getLatestStartup(regionFolder).ifPresent(backups::add);

		BackupCommand.getLatestHourly(regionFolder).ifPresent(backups::add);

		BackupCommand.getLatestDaily(regionFolder).ifPresent(backups::add);

		return backups.stream().min((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
	}

	private static Optional<File> getLatestManual(final @NotNull File regionFolder, final @NotNull String uuid) throws IOException {
		return BaseFileUtils.listFolders(new File(regionFolder, "manual/" + uuid)).stream().min((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
	}

	private static Optional<File> getLatestStartup(final @NotNull File regionFolder) throws IOException {
		return BaseFileUtils.listFolders(new File(regionFolder, "automatic/startup")).stream().min((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
	}

	private static Optional<File> getLatestHourly(final @NotNull File regionFolder) throws IOException {
		return BaseFileUtils.listFolders(new File(regionFolder, "automatic/hourly")).stream().min((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
	}

	private static Optional<File> getLatestDaily(final @NotNull File regionFolder) throws IOException {
		return BaseFileUtils.listFolders(new File(regionFolder, "automatic/daily")).stream().min((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
	}


	@ClassMapper(value = CommandConfirmation.class, local = true)
	private @NotNull TypeMapper<CommandConfirmation> mapCommandConfirmation() {
		return new TypeMapper<CommandConfirmation>() {
			@Override
			public CommandConfirmation map(final @NotNull String[] previousArguments, final @NotNull String s) {
				return CommandConfirmation.map(s);
			}

			@Override
			public java.util.List<String> tabCompletes(final @NotNull CommandSender commandSender, final @NotNull String[] previousArguments, final @NotNull String arg) {
				final @NotNull java.util.List<String> tabCompletions = Arrays.asList("-confirm", "-deny");
				if (commandSender instanceof Player && previousArguments.length > 0) {
					final @NotNull Player p = (Player) commandSender;
					if (previousArguments[0].equalsIgnoreCase("save")
						&& previousArguments.length > 1
						&& CommandRequestUtils.checkSaveBackupRequest(p.getUniqueId(), previousArguments[previousArguments.length - 1]) != null) {
						return tabCompletions;
					} else if ((previousArguments[0].equalsIgnoreCase("del") || previousArguments[0].equalsIgnoreCase("delete"))
							   && previousArguments.length > 1
							   && CommandRequestUtils.checkDeleteBackupRequest(p.getUniqueId(), previousArguments[previousArguments.length - 1]) != null) {
						return tabCompletions;
					} else {
						return null;
					}
				} else {
					return null;
				}
			}
		};
	}


	@ClassMapper(value = MappedFile.class, local = true)
	private @NotNull TypeMapper<MappedFile> mapFile() {
		return new TypeMapper<MappedFile>() {
			@Override
			public MappedFile map(final @NotNull String[] previous, final @NotNull String s) {
				if (TestAreaUtils.forbiddenFileName(s)) {
					return null;
				} else {
					return new MappedFile(s);
				}
			}

			@Override
			public java.util.List<String> tabCompletes(final @NotNull CommandSender commandSender, final @NotNull String[] previousArguments, final @NotNull String arg) {
				if (commandSender instanceof Player) {
					final @NotNull Player p = (Player) commandSender;
					final @Nullable DefinedRegion region = TestAreaUtils.getRegion(p);
					if (region != null && previousArguments.length > 0) {
						final int lastIndex = arg.lastIndexOf("/");
						final @NotNull String path = arg.substring(0, Math.max(lastIndex, 0));

						if (previousArguments[0].equalsIgnoreCase("load")) {
							if (Arrays.stream(previousArguments).anyMatch(s -> s.equalsIgnoreCase("-manual"))) {
								try {
									final @NotNull Path filePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("manual").resolve(p.getUniqueId().toString()).resolve(path).toRealPath();
									final @NotNull Path basePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("manual").resolve(p.getUniqueId().toString()).toRealPath();
									if (filePath.startsWith(basePath)) {
										final @NotNull java.util.List<String> results = new LinkedList<>();
										for (final @NotNull File file : BaseFileUtils.listFilesOfTypeAndFolders(filePath.toFile(), "schem")) {
											final @NotNull String fileName = FilenameUtils.separatorsToUnix(BaseFileUtils.removeExtension(basePath.relativize(file.toPath()).toString()));
											results.add(fileName);
										}
										return results;
									} else {
										return null;
									}
								} catch (IOException e) {
									return null;
								}
							} else if (Arrays.stream(previousArguments).anyMatch(s -> s.equalsIgnoreCase("-hourly"))) {
								try {
									final @NotNull Path filePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("automatic/hourly").resolve(path).toRealPath();
									final @NotNull Path basePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("automatic/hourly").toRealPath();
									if (filePath.startsWith(basePath)) {
										final @NotNull java.util.List<String> results = new LinkedList<>();
										for (final @NotNull File file : BaseFileUtils.listFilesOfTypeAndFolders(filePath.toFile(), "schem")) {
											final @NotNull String fileName = FilenameUtils.separatorsToUnix(BaseFileUtils.removeExtension(basePath.relativize(file.toPath()).toString()));
											results.add(fileName);
										}
										return results;
									} else {
										return null;
									}
								} catch (IOException e) {
									return null;
								}
							} else if (Arrays.stream(previousArguments).anyMatch(s -> s.equalsIgnoreCase("-daily"))) {
								try {
									final @NotNull Path filePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("automatic/daily").resolve(path).toRealPath();
									final @NotNull Path basePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("automatic/daily").toRealPath();
									if (filePath.startsWith(basePath)) {
										final @NotNull java.util.List<String> results = new LinkedList<>();
										for (final @NotNull File file : BaseFileUtils.listFilesOfTypeAndFolders(filePath.toFile(), "schem")) {
											final @NotNull String fileName = FilenameUtils.separatorsToUnix(BaseFileUtils.removeExtension(basePath.relativize(file.toPath()).toString()));
											results.add(fileName);
										}
										return results;
									} else {
										return null;
									}
								} catch (IOException e) {
									return null;
								}
							} else if (Arrays.stream(previousArguments).anyMatch(s -> s.equalsIgnoreCase("-startup"))) {
								try {
									final @NotNull Path filePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("automatic/startup").resolve(path).toRealPath();
									final @NotNull Path basePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("automatic/startup").toRealPath();
									if (filePath.startsWith(basePath)) {
										final @NotNull java.util.List<String> results = new LinkedList<>();
										for (final @NotNull File file : BaseFileUtils.listFilesOfTypeAndFolders(filePath.toFile(), "schem")) {
											final @NotNull String fileName = FilenameUtils.separatorsToUnix(BaseFileUtils.removeExtension(basePath.relativize(file.toPath()).toString()));
											results.add(fileName);
										}
										return results;
									} else {
										return null;
									}
								} catch (IOException e) {
									return null;
								}
							} else {
								final @NotNull java.util.List<String> results = new LinkedList<>();
								try {
									final @NotNull Path filePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("manual").resolve(p.getUniqueId().toString()).resolve(path).toRealPath();
									final @NotNull Path basePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("manual").resolve(p.getUniqueId().toString()).toRealPath();
									if (filePath.startsWith(basePath)) {
										for (final @NotNull File file : BaseFileUtils.listFilesOfTypeAndFolders(filePath.toFile(), "schem")) {
											final @NotNull String fileName = FilenameUtils.separatorsToUnix(BaseFileUtils.removeExtension(basePath.relativize(file.toPath()).toString()));
											results.add(fileName);
										}
									} else {
										return null;
									}
								} catch (IOException ignored) {
									//DO NOTHING
								}

								try {
									final @NotNull Path filePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("automatic/hourly").resolve(path).toRealPath();
									final @NotNull Path basePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("automatic/hourly").toRealPath();
									if (filePath.startsWith(basePath)) {
										for (final @NotNull File file : BaseFileUtils.listFilesOfTypeAndFolders(filePath.toFile(), "schem")) {
											final @NotNull String fileName = FilenameUtils.separatorsToUnix(BaseFileUtils.removeExtension(basePath.relativize(file.toPath()).toString()));
											results.add(fileName);
										}
									} else {
										return null;
									}
								} catch (IOException ignored) {
									//DO NOTHING
								}

								try {
									final @NotNull Path filePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("automatic/daily").resolve(path).toRealPath();
									final @NotNull Path basePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("automatic/daily").toRealPath();
									if (filePath.startsWith(basePath)) {
										for (final @NotNull File file : BaseFileUtils.listFilesOfTypeAndFolders(filePath.toFile(), "schem")) {
											final @NotNull String fileName = FilenameUtils.separatorsToUnix(BaseFileUtils.removeExtension(basePath.relativize(file.toPath()).toString()));
											results.add(fileName);
										}
									} else {
										return null;
									}
								} catch (IOException ignored) {
									//DO NOTHING
								}

								try {
									final @NotNull Path filePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("automatic/startup").resolve(path).toRealPath();
									final @NotNull Path basePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("automatic/startup").toRealPath();
									if (filePath.startsWith(basePath)) {
										for (final @NotNull File file : BaseFileUtils.listFilesOfTypeAndFolders(filePath.toFile(), "schem")) {
											final @NotNull String fileName = FilenameUtils.separatorsToUnix(BaseFileUtils.removeExtension(basePath.relativize(file.toPath()).toString()));
											results.add(fileName);
										}
									} else {
										return null;
									}
								} catch (IOException ignored) {
									//DO NOTHING
								}
								return results;
							}
						} else if (previousArguments[0].equalsIgnoreCase("save")) {
							try {
								final @NotNull Path filePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("manual").resolve(p.getUniqueId().toString()).resolve(path).toRealPath();
								final @NotNull Path basePath = BackupCommand.BACKUP_FOLDER.resolve(region.getName().substring(0, region.getName().length() - 6)).resolve("manual").resolve(p.getUniqueId().toString()).toRealPath();
								if (filePath.startsWith(basePath)) {
									final @NotNull java.util.List<String> results = new LinkedList<>();
									for (final @NotNull File file : BaseFileUtils.listFilesOfTypeAndFolders(filePath.toFile(), "schem")) {
										final @NotNull String fileName = FilenameUtils.separatorsToUnix(BaseFileUtils.removeExtension(basePath.relativize(file.toPath()).toString()));
										results.add(fileName);
									}
									return results;
								} else {
									return null;
								}
							} catch (IOException e) {
								return null;
							}
						} else {
							return null;
						}
					} else {
						return null;
					}
				} else {
					return null;
				}
			}
		};
	}
}