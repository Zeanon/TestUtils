package de.zeanon.testutils.plugin.commands.backup;

import de.zeanon.storagemanagercore.external.browniescollections.GapList;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.commandframework.SWCommand;
import de.zeanon.testutils.commandframework.TypeMapper;
import de.zeanon.testutils.plugin.utils.CommandRequestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.enums.BackupFile;
import de.zeanon.testutils.plugin.utils.enums.BackupMode;
import de.zeanon.testutils.plugin.utils.enums.CommandConfirmation;
import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class Backup extends SWCommand {

	public Backup() {
		super("backup");
	}

	public static @NotNull Optional<File> getLatest(final @NotNull File regionFolder, final @NotNull String uuid, final @NotNull BackupMode backUpMode) throws IOException {
		if (backUpMode == BackupMode.NONE) {
			return Backup.getLatest(regionFolder, uuid);
		} else {
			switch (backUpMode) {
				case MANUAL:
					return Backup.getLatestManual(regionFolder, uuid);
				case STARTUP:
					return Backup.getLatestStartup(regionFolder);
				case HOURLY:
					return Backup.getLatestHourly(regionFolder);
				case DAILY:
					return Backup.getLatestDaily(regionFolder);
				default:
					return Backup.getLatest(regionFolder, uuid);
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
	public void backupHelp(final @NotNull Player p, final @NotNull String... args) {
		if (args.length == 0) {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Missing argument.");
		} else {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Unknown argument '" + ChatColor.DARK_RED + args[args.length - 1] + ChatColor.RED + "'.");
		}
	}

	@Register({"load"})
	public void noArgsLoad(final @NotNull Player p) {
		Load.execute(RegionSide.NONE, null, BackupMode.NONE, p);
	}

	@Register({"load"})
	public void oneArgLoad(final @NotNull Player p, final @NotNull RegionSide regionSide) {
		Load.execute(regionSide, null, BackupMode.NONE, p);
	}

	@Register({"load"})
	public void oneArgLoad(final @NotNull Player p, final @NotNull BackupFile backupFile) {
		Load.execute(RegionSide.NONE, backupFile.getName(), BackupMode.NONE, p);
	}

	@Register({"load"})
	public void oneArgLoad(final @NotNull Player p, final @NotNull BackupMode backupMode) {
		Load.execute(RegionSide.NONE, null, backupMode, p);
	}

	@Register({"load"})
	public void twoArgsLoad(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull BackupFile backupFile) {
		Load.execute(regionSide, backupFile.getName(), BackupMode.NONE, p);
	}

	@Register({"load"})
	public void twoArgsLoad(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull BackupMode backupMode) {
		Load.execute(regionSide, null, backupMode, p);
	}

	@Register({"load"})
	public void twoArgsLoad(final @NotNull Player p, final @NotNull BackupMode backupMode, final @NotNull BackupFile backupFile) {
		Load.execute(RegionSide.NONE, backupFile.getName(), backupMode, p);
	}

	@Register({"load"})
	public void twoArgsLoad(final @NotNull Player p, final @NotNull BackupMode backupMode, final @NotNull RegionSide regionSide) {
		Load.execute(regionSide, null, backupMode, p);
	}

	@Register({"load"})
	public void twoArgsLoad(final @NotNull Player p, final @NotNull BackupFile backupFile, final @NotNull BackupMode backupMode) {
		Load.execute(RegionSide.NONE, backupFile.getName(), backupMode, p);
	}

	@Register({"load"})
	public void twoArgsLoad(final @NotNull Player p, final @NotNull BackupFile backupFile, final @NotNull RegionSide regionSide) {
		Load.execute(regionSide, backupFile.getName(), BackupMode.NONE, p);
	}

	@Register({"load"})
	public void threeArgsLoad(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull BackupFile backupFile, final @NotNull BackupMode backupMode) {
		Load.execute(regionSide, backupFile.getName(), backupMode, p);
	}

	@Register({"load"})
	public void threeArgsLoad(final @NotNull Player p, final @NotNull BackupFile backupFile, final @NotNull BackupMode backupMode, final @NotNull RegionSide regionSide) {
		Load.execute(regionSide, backupFile.getName(), backupMode, p);
	}

	@Register({"load"})
	public void threeArgsLoad(final @NotNull Player p, final @NotNull BackupMode backupMode, final @NotNull RegionSide regionSide, final @NotNull BackupFile backupFile) {
		Load.execute(regionSide, backupFile.getName(), backupMode, p);
	}

	@Register({"load"})
	public void threeArgsLoad(final @NotNull Player p, final @NotNull BackupFile backupFile, final @NotNull RegionSide regionSide, final @NotNull BackupMode backupMode) {
		Load.execute(regionSide, backupFile.getName(), backupMode, p);
	}

	@Register({"load"})
	public void threeArgsLoad(final @NotNull Player p, final @NotNull BackupMode backupMode, final @NotNull BackupFile backupFile, final @NotNull RegionSide regionSide) {
		Load.execute(regionSide, backupFile.getName(), backupMode, p);
	}

	@Register({"load"})
	public void threeArgsLoad(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull BackupMode backupMode, final @NotNull BackupFile backupFile) {
		Load.execute(regionSide, backupFile.getName(), backupMode, p);
	}

	@Register({"save"})
	public void noArgsSave(final @NotNull Player p) {
		Save.execute(null, null, p);
	}

	@Register({"save"})
	public void oneArgSave(final @NotNull Player p, final @NotNull BackupFile backupFile) {
		Save.execute(backupFile, null, p);
	}

	@Register({"save"})
	public void twoArgsSave(final @NotNull Player p, final @NotNull BackupFile backupFile, final @NotNull CommandConfirmation commandConfirmation) {
		Save.execute(backupFile, commandConfirmation.confirm(), p);
	}


	@Register(value = {"del"}, help = true)
	public void noArgDelHelp(final @NotNull Player p, final @NotNull String... args) {
		Delete.usage(p);
	}

	@Register({"del"})
	public void noArgsDel(final @NotNull Player p) {
		Delete.execute(null, null, p);
	}

	@Register({"del"})
	public void oneArgDel(final @NotNull Player p, final @NotNull BackupFile backupFile) {
		Delete.execute(backupFile, null, p);
	}

	@Register({"del"})
	public void twoArgsDel(final @NotNull Player p, final @NotNull BackupFile backupFile, final @NotNull CommandConfirmation commandConfirmation) {
		Delete.execute(backupFile, commandConfirmation.confirm(), p);
	}


	@Register(value = {"delete"}, help = true)
	public void noArgDeleteHelp(final @NotNull Player p, final @NotNull String... args) {
		Delete.usage(p);
	}

	@Register({"delete"})
	public void noArgsDelete(final @NotNull Player p) {
		Delete.execute(null, null, p);
	}

	@Register({"delete"})
	public void oneArgDelete(final @NotNull Player p, final @NotNull BackupFile backupFile) {
		Delete.execute(backupFile, null, p);
	}

	@Register({"delete"})
	public void twoArgsDelete(final @NotNull Player p, final @NotNull BackupFile backupFile, final @NotNull CommandConfirmation commandConfirmation) {
		Delete.execute(backupFile, commandConfirmation.confirm(), p);
	}


	@Register({"list"})
	public void noArgsList(final @NotNull Player p) {
		List.execute(RegionSide.NONE, BackupMode.NONE, p);
	}

	@Register({"list"})
	public void oneArgList(final @NotNull Player p, final @NotNull RegionSide regionSide) {
		List.execute(regionSide, BackupMode.NONE, p);
	}

	@Register({"list"})
	public void oneArgList(final @NotNull Player p, final @NotNull BackupMode backupMode) {
		List.execute(RegionSide.NONE, backupMode, p);
	}

	@Register({"list"})
	public void twoArgsList(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull BackupMode backupMode) {
		List.execute(regionSide, backupMode, p);
	}

	@Register({"list"})
	public void twoArgsList(final @NotNull Player p, final @NotNull BackupMode backupMode, final @NotNull RegionSide regionSide) {
		List.execute(regionSide, backupMode, p);
	}

	@Register(value = {"search"}, help = true)
	public void noArgSearchHelp(final @NotNull Player p, final @NotNull String... args) {
		p.sendMessage(GlobalMessageUtils.messageHead
					  + ChatColor.RED + "Missing sequence to search for.");
	}

	@Register({"search"})
	public void oneArgSearch(final @NotNull Player p, final @NotNull BackupFile backupFile) {
		Search.execute(backupFile.getName(), BackupMode.NONE, p);
	}

	@Register({"search"})
	public void twoArgsSearch(final @NotNull Player p, final @NotNull BackupMode backupMode, final @NotNull BackupFile backupFile) {
		Search.execute(backupFile.getName(), backupMode, p);
	}

	@Register({"search"})
	public void twoArgsSearch(final @NotNull Player p, final @NotNull BackupFile backupFile, final @NotNull BackupMode backupMode) {
		Search.execute(backupFile.getName(), backupMode, p);
	}


	private static Optional<File> getLatest(final @NotNull File regionFolder, final @NotNull String uuid) throws IOException {
		final @NotNull java.util.List<File> backups = new GapList<>();

		Backup.getLatestManual(regionFolder, uuid).ifPresent(backups::add);

		Backup.getLatestStartup(regionFolder).ifPresent(backups::add);

		Backup.getLatestHourly(regionFolder).ifPresent(backups::add);

		Backup.getLatestDaily(regionFolder).ifPresent(backups::add);

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
				switch (s.toLowerCase()) {
					case "-confirm":
						return CommandConfirmation.CONFIRM;
					case "-deny":
						return CommandConfirmation.DENY;
					default:
						return null;
				}
			}

			@Override
			public java.util.List<String> tabCompletes(final @NotNull CommandSender commandSender, final @NotNull String[] previousArguments, final @NotNull String arg) {
				final @NotNull java.util.List<String> tabCompletions = Arrays.asList("-confirm", "-deny");
				if (commandSender instanceof Player && previousArguments.length > 0) {
					final @NotNull Player p = (Player) commandSender;
					if (previousArguments[0].equalsIgnoreCase("save")
						&& previousArguments.length > 1
						&& CommandRequestUtils.checkOverwriteBackupRequest(p.getUniqueId(), previousArguments[1]) != null) {
						return tabCompletions;
					} else if ((previousArguments[0].equalsIgnoreCase("del") || previousArguments[0].equalsIgnoreCase("delete"))
							   && previousArguments.length > 1
							   && CommandRequestUtils.checkDeleteBackupRequest(p.getUniqueId(), previousArguments[1]) != null) {
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
}