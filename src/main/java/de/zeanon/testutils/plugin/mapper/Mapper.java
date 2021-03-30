package de.zeanon.testutils.plugin.mapper;

import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.commandframework.SWCommandUtils;
import de.zeanon.testutils.commandframework.TypeMapper;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.*;
import de.zeanon.testutils.plugin.utils.region.Region;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Mapper {

	public void initialize() {
		SWCommandUtils.addMapper(RegionSide.class, Mapper.mapRegionSide());
		SWCommandUtils.addMapper(TNTMode.class, Mapper.mapTNTMode());
		SWCommandUtils.addMapper(GlobalToggle.class, Mapper.mapGlobalToggle());
		SWCommandUtils.addMapper(StoplagToggle.class, Mapper.mapStoplagToggle());
		SWCommandUtils.addMapper(BackupFile.class, Mapper.mapBackupFile());
	}

	private @NotNull TypeMapper<RegionSide> mapRegionSide() {
		final @NotNull List<String> tabCompletes = Arrays.asList("-here", "-other", "-north", "-n", "-south", "-s");
		return SWCommandUtils.createMapper(s -> {
			switch (s.toLowerCase()) {
				case "-here":
					return RegionSide.HERE;
				case "-other":
					return RegionSide.OTHER;
				case "-north":
				case "-n":
					return RegionSide.NORTH;
				case "-south":
				case "-s":
					return RegionSide.SOUTH;
				default:
					return null;
			}
		}, s -> tabCompletes);
	}

	private @NotNull TypeMapper<TNTMode> mapTNTMode() {
		final @NotNull List<String> tabCompletes = Arrays.asList("-allow", "-deny");
		return SWCommandUtils.createMapper(s -> {
			switch (s.toLowerCase()) {
				case "-allow":
					return TNTMode.ALLOW;
				case "-deny":
					return TNTMode.DENY;
				default:
					return null;
			}
		}, s -> tabCompletes);
	}

	private @NotNull TypeMapper<GlobalToggle> mapGlobalToggle() {
		final @NotNull List<String> tabCompletes = Arrays.asList("-global", "-g");
		return SWCommandUtils.createMapper(s -> {
			switch (s.toLowerCase()) {
				case "-g":
				case "-global":
					return GlobalToggle.GLOBAL;
				default:
					return null;
			}
		}, s -> tabCompletes);
	}

	private @NotNull TypeMapper<StoplagToggle> mapStoplagToggle() {
		final @NotNull List<String> tabCompletes = Collections.singletonList("-c");
		return SWCommandUtils.createMapper(s -> {
			if ("-c".equalsIgnoreCase(s)) {
				return StoplagToggle.C;
			} else {
				return null;
			}
		}, s -> tabCompletes);
	}

	private @NotNull TypeMapper<BackupFile> mapBackupFile() {
		return new TypeMapper<BackupFile>() {
			@Override
			public BackupFile map(final @NotNull String s) {
				return new BackupFile(s);
			}

			@Override
			public List<String> tabCompletes(final @NotNull CommandSender commandSender, final @NotNull String[] previousArguments, final @NotNull String arg) {
				if (commandSender instanceof Player) {
					final @NotNull Player p = (Player) commandSender;
					final @Nullable Region region = TestAreaUtils.getRegion(p);
					if (region != null) {
						if (Arrays.stream(previousArguments).anyMatch(s -> s.equalsIgnoreCase("load"))) {
							if (Arrays.stream(previousArguments).anyMatch(s -> s.equalsIgnoreCase("-manual"))) {
								try {
									return BaseFileUtils.listFolders(new File(TestUtils.getInstance().getDataFolder(), "Backups/" + p.getWorld().getName() + "/" + region.getName().substring(0, region.getName().length() - 6) + "/manual/" + p.getUniqueId()))
														.stream()
														.map(f -> BaseFileUtils.removeExtension(f.getName()))
														.collect(Collectors.toList());
								} catch (IOException e) {
									return null;
								}
							} else if (Arrays.stream(previousArguments).anyMatch(s -> s.equalsIgnoreCase("-hourly"))) {
								try {
									return BaseFileUtils.listFolders(new File(TestUtils.getInstance().getDataFolder(), "Backups/" + p.getWorld().getName() + "/" + region.getName().substring(0, region.getName().length() - 6) + "/automatic/hourly"))
														.stream()
														.map(f -> BaseFileUtils.removeExtension(f.getName()))
														.collect(Collectors.toList());
								} catch (IOException e) {
									return null;
								}
							} else if (Arrays.stream(previousArguments).anyMatch(s -> s.equalsIgnoreCase("-daily"))) {
								try {
									return BaseFileUtils.listFolders(new File(TestUtils.getInstance().getDataFolder(), "Backups/" + p.getWorld().getName() + "/" + region.getName().substring(0, region.getName().length() - 6) + "/automatic/daily"))
														.stream()
														.map(f -> BaseFileUtils.removeExtension(f.getName()))
														.collect(Collectors.toList());
								} catch (IOException e) {
									return null;
								}
							} else if (Arrays.stream(previousArguments).anyMatch(s -> s.equalsIgnoreCase("-startup"))) {
								try {
									return BaseFileUtils.listFolders(new File(TestUtils.getInstance().getDataFolder(), "Backups/" + p.getWorld().getName() + "/" + region.getName().substring(0, region.getName().length() - 6) + "/automatic/startup"))
														.stream()
														.map(f -> BaseFileUtils.removeExtension(f.getName()))
														.collect(Collectors.toList());
								} catch (IOException e) {
									return null;
								}
							} else {
								try {
									final @NotNull List<String> tabCompletions = new LinkedList<>();
									tabCompletions.addAll(
											BaseFileUtils.listFolders(new File(TestUtils.getInstance().getDataFolder(), "Backups/" + p.getWorld().getName() + "/" + region.getName().substring(0, region.getName().length() - 6) + "/manual/" + p.getUniqueId()))
														 .stream()
														 .map(f -> BaseFileUtils.removeExtension(f.getName()))
														 .collect(Collectors.toList()));
									tabCompletions.addAll(
											BaseFileUtils.listFolders(new File(TestUtils.getInstance().getDataFolder(), "Backups/" + p.getWorld().getName() + "/" + region.getName().substring(0, region.getName().length() - 6) + "/automatic/hourly"))
														 .stream()
														 .map(f -> BaseFileUtils.removeExtension(f.getName()))
														 .collect(Collectors.toList()));
									tabCompletions.addAll(
											BaseFileUtils.listFolders(new File(TestUtils.getInstance().getDataFolder(), "Backups/" + p.getWorld().getName() + "/" + region.getName().substring(0, region.getName().length() - 6) + "/automatic/daily"))
														 .stream()
														 .map(f -> BaseFileUtils.removeExtension(f.getName()))
														 .collect(Collectors.toList()));
									tabCompletions.addAll(
											BaseFileUtils.listFolders(new File(TestUtils.getInstance().getDataFolder(), "Backups/" + p.getWorld().getName() + "/" + region.getName().substring(0, region.getName().length() - 6) + "/automatic/startup"))
														 .stream()
														 .map(f -> BaseFileUtils.removeExtension(f.getName()))
														 .collect(Collectors.toList()));
									return tabCompletions;
								} catch (IOException e) {
									return null;
								}
							}
						} else if (Arrays.stream(previousArguments).anyMatch(s -> s.equalsIgnoreCase("save"))) {
							try {
								return BaseFileUtils.listFolders(new File(TestUtils.getInstance().getDataFolder(), "Backups/" + p.getWorld().getName() + "/" + region.getName().substring(0, region.getName().length() - 6) + "/manual/" + p.getUniqueId()))
													.stream()
													.map(f -> BaseFileUtils.removeExtension(f.getName()))
													.collect(Collectors.toList());
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

	private @NotNull TypeMapper<BackupMode> mapBackupMode() {
		final @NotNull List<String> tabCompletes = Arrays.asList("-manual", "-hourly", "-daily", "-startup");
		return SWCommandUtils.createMapper(s -> {
			switch (s.toLowerCase()) {
				case "-manual":
					return BackupMode.MANUAL;
				case "-hourly":
					return BackupMode.HOURLY;
				case "-daily":
					return BackupMode.DAILY;
				case "-startup":
					return BackupMode.STARTUP;
				default:
					return null;
			}
		}, s -> tabCompletes);
	}

	private @NotNull TypeMapper<CommandConfirmation> mapCommandConfirmation() {
		return SWCommandUtils.createMapper(s -> {
			switch (s.toLowerCase()) {
				case "-confirm":
					return CommandConfirmation.CONFIRM;
				case "-deny":
					return CommandConfirmation.DENY;
				default:
					return null;
			}
		}, s -> Collections.emptyList());
	}
}