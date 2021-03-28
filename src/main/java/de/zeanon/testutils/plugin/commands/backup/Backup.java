package de.zeanon.testutils.plugin.commands.backup;

import de.zeanon.storagemanagercore.external.browniescollections.GapList;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.plugin.utils.enums.BackUpMode;
import de.zeanon.testutils.plugin.utils.enums.PasteSide;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Backup {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("save")) {
				Save.execute(args, p);
			} else if (args[0].equalsIgnoreCase("load")) {
				Load.execute(Backup.modifiers(args, "load"), p);
			} else if (args[0].equalsIgnoreCase("list")) {
				de.zeanon.testutils.plugin.commands.backup.List.execute(Backup.modifiers(args, "list"), p);
			} else if (args[0].equalsIgnoreCase("search")) {
				Search.execute(Backup.modifiers(args, "search"), p);
			} else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del")) {
				Delete.execute(args, p);
			}
		}
	}

	public @NotNull
	Optional<File> getLatest(final @NotNull File regionFolder, final @NotNull String uuid, final @NotNull BackUpMode backUpMode) throws IOException {
		if (backUpMode == BackUpMode.NONE) {
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

	public void sendLoadBackupMessage(final @NotNull String fileName,
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

	public @Nullable File getFile(final @NotNull File regionFolder, final @NotNull String name, final @NotNull BackUpMode backUpMode, final @NotNull Player p) throws IOException {
		switch (backUpMode) {
			case NONE:
				final @NotNull File manualBackup = new File(regionFolder, "manual/" + p.getUniqueId());
				if (manualBackup.exists()) {
					for (final @NotNull File temp : BaseFileUtils.searchFolders(manualBackup, name)) {
						if (temp.getName().equals(name)) {
							return temp;
						}
					}
				}

				final @NotNull File hourlyBackup = new File(regionFolder, "automatic/hourly");
				if (hourlyBackup.exists()) {
					for (final @NotNull File temp : BaseFileUtils.searchFolders(hourlyBackup, name)) {
						if (temp.getName().equals(name)) {
							return temp;
						}
					}
				}

				final @NotNull File dailyBackup = new File(regionFolder, "automatic/daily");
				if (dailyBackup.exists()) {
					for (final @NotNull File temp : BaseFileUtils.searchFolders(dailyBackup, name)) {
						if (temp.getName().equals(name)) {
							return temp;
						}
					}
				}

				final @NotNull File startupBackup = new File(regionFolder, "automatic/startup");
				if (startupBackup.exists()) {
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

	private Optional<File> getLatest(final @NotNull File regionFolder, final @NotNull String uuid) throws IOException {
		final @NotNull List<File> backups = new GapList<>();

		Backup.getLatestManual(regionFolder, uuid).ifPresent(backups::add);

		Backup.getLatestStartup(regionFolder).ifPresent(backups::add);

		Backup.getLatestHourly(regionFolder).ifPresent(backups::add);

		Backup.getLatestDaily(regionFolder).ifPresent(backups::add);

		return backups.stream().min((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
	}

	private Optional<File> getLatestManual(final @NotNull File regionFolder, final @NotNull String uuid) throws IOException {
		@NotNull Optional<File> possibleFirst = BaseFileUtils.listFolders(new File(regionFolder, "manual/" + uuid)).stream().min((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

		return possibleFirst;
	}

	private Optional<File> getLatestStartup(final @NotNull File regionFolder) throws IOException {
		@NotNull Optional<File> possibleFirst = BaseFileUtils.listFolders(new File(regionFolder, "automatic/startup")).stream().min((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

		return possibleFirst;
	}

	private Optional<File> getLatestHourly(final @NotNull File regionFolder) throws IOException {
		@NotNull Optional<File> possibleFirst = BaseFileUtils.listFolders(new File(regionFolder, "automatic/hourly")).stream().min((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

		return possibleFirst;
	}

	private Optional<File> getLatestDaily(final @NotNull File regionFolder) throws IOException {
		@NotNull Optional<File> possibleFirst = BaseFileUtils.listFolders(new File(regionFolder, "automatic/daily")).stream().min((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

		return possibleFirst;
	}

	private @NotNull
	Backup.ModifierBlock modifiers(final @NotNull String[] args, final @NotNull String command) {
		final @NotNull Backup.ModifierBlock result = new ModifierBlock(PasteSide.NONE, BackUpMode.NONE, null);
		for (final @NotNull String arg : args) {
			if (PasteSide.parse(arg) != PasteSide.NONE) {
				result.setPasteSide(PasteSide.parse(arg));
			} else if (BackUpMode.parse(arg) != BackUpMode.NONE) {
				result.setBackUpMode(BackUpMode.parse(arg));
			} else if (!arg.equalsIgnoreCase(command)) {
				result.setFileName(arg);
			}
		}

		return result;
	}

	public static class ModifierBlock {

		@Getter
		@Setter
		private @NotNull
		PasteSide pasteSide;
		@Getter
		@Setter
		private @NotNull
		BackUpMode backUpMode;
		@Getter
		@Setter
		private @Nullable
		String fileName;

		private ModifierBlock(final @NotNull PasteSide pasteSide, final @NotNull BackUpMode backUpMode, final @Nullable String fileName) {
			this.pasteSide = pasteSide;
			this.backUpMode = backUpMode;
			this.fileName = fileName;
		}
	}
}