package de.zeanon.testutils.plugin.mapper;

import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.commandframework.SWCommandUtils;
import de.zeanon.testutils.commandframework.TypeMapper;
import de.zeanon.testutils.plugin.commands.backup.Save;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.*;
import de.zeanon.testutils.plugin.utils.region.DefinedRegion;
import de.zeanon.testutils.plugin.utils.region.RegionManager;
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
		SWCommandUtils.addMapper(BackupMode.class, Mapper.mapBackupMode());
		SWCommandUtils.addMapper(CommandConfirmation.class, Mapper.mapCommandConfirmation());
		SWCommandUtils.addMapper(Flag.Value.class.getTypeName(), Mapper.mapFlagValue());
		SWCommandUtils.addMapper(RegionName.class, Mapper.mapRegionName());
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
			public BackupFile map(final @NotNull String[] previous, final @NotNull String s) {
				if (Save.forbiddenFileName(s)) {
					return null;
				} else {
					return new BackupFile(s);
				}
			}

			@Override
			public List<String> tabCompletes(final @NotNull CommandSender commandSender, final @NotNull String[] previousArguments, final @NotNull String arg) {
				if (commandSender instanceof Player) {
					final @NotNull Player p = (Player) commandSender;
					final @Nullable DefinedRegion region = TestAreaUtils.getRegion(p);
					if (region != null && previousArguments.length > 0) {
						if (previousArguments[0].equalsIgnoreCase("load")) {
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
									final @NotNull File manualBackup = new File(TestUtils.getInstance().getDataFolder(), "Backups/" + p.getWorld().getName() + "/" + region.getName().substring(0, region.getName().length() - 6) + "/manual/" + p.getUniqueId());
									if (manualBackup.exists() && manualBackup.isDirectory()) {
										tabCompletions.addAll(
												BaseFileUtils.listFolders(manualBackup)
															 .stream()
															 .map(f -> BaseFileUtils.removeExtension(f.getName()))
															 .collect(Collectors.toList()));
									}

									final @NotNull File hourlyBackup = new File(TestUtils.getInstance().getDataFolder(), "Backups/" + p.getWorld().getName() + "/" + region.getName().substring(0, region.getName().length() - 6) + "/automatic/hourly");
									if (hourlyBackup.exists() && hourlyBackup.isDirectory()) {
										tabCompletions.addAll(
												BaseFileUtils.listFolders(hourlyBackup)
															 .stream()
															 .map(f -> BaseFileUtils.removeExtension(f.getName()))
															 .collect(Collectors.toList()));
									}

									final @NotNull File dailyBackup = new File(TestUtils.getInstance().getDataFolder(), "Backups/" + p.getWorld().getName() + "/" + region.getName().substring(0, region.getName().length() - 6) + "/automatic/daily");
									if (dailyBackup.exists() && dailyBackup.isDirectory()) {
										tabCompletions.addAll(
												BaseFileUtils.listFolders(dailyBackup)
															 .stream()
															 .map(f -> BaseFileUtils.removeExtension(f.getName()))
															 .collect(Collectors.toList()));
									}

									final @NotNull File startupBackup = new File(TestUtils.getInstance().getDataFolder(), "Backups/" + p.getWorld().getName() + "/" + region.getName().substring(0, region.getName().length() - 6) + "/automatic/startup");
									if (startupBackup.exists() && startupBackup.isDirectory()) {
										tabCompletions.addAll(
												BaseFileUtils.listFolders(startupBackup)
															 .stream()
															 .map(f -> BaseFileUtils.removeExtension(f.getName()))
															 .collect(Collectors.toList()));
									}
									return tabCompletions;
								} catch (IOException e) {
									return null;
								}
							}
						} else if (previousArguments[0].equalsIgnoreCase("save")) {
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

	private @NotNull TypeMapper<Flag.Value<?>> mapFlagValue() {
		return new TypeMapper<Flag.Value<?>>() {
			@Override
			public Flag.Value<?> map(final @NotNull String[] previousArguments, final @NotNull String s) {
				System.out.println(Arrays.toString(previousArguments));
				final @Nullable Flag flag = this.getFlag(previousArguments); //NOSONAR
				if (flag != null) {
					//noinspection rawtypes
					final @NotNull Enum[] values = (Enum[]) flag.getValue().getEnumConstants(); //NOSONAR
					//noinspection rawtypes
					for (final @NotNull Enum tempEnum : values) { //NOSONAR
						if (tempEnum.name().equalsIgnoreCase(s)) {
							//noinspection unchecked,rawtypes,rawtypes
							return new Flag.Value<>(tempEnum);
						}
					}
				}
				return null;
			}

			@Override
			public List<String> tabCompletes(CommandSender commandSender, String[] previousArguments, String s) {
				final @Nullable Flag flag = this.getFlag(previousArguments); //NOSONAR
				if (flag != null) {
					//noinspection rawtypes
					final @NotNull Enum[] values = (Enum[]) flag.getValue().getEnumConstants(); //NOSONAR
					return Arrays.stream(values).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
				} else {
					return null;
				}
			}

			private @Nullable Flag getFlag(final @NotNull String[] previous) {
				if (previous.length > 0) {
					try {
						return Flag.valueOf(previous[previous.length - 1].toUpperCase());
					} catch (IllegalArgumentException e) {
						return null;
					}
				} else {
					return null;
				}
			}
		};
	}

	private @NotNull TypeMapper<RegionName> mapRegionName() {
		return SWCommandUtils.createMapper(RegionName::new
				, s -> {
					final @NotNull List<String> tabCompletes = RegionManager.getRegions().stream().map(DefinedRegion::getName).collect(Collectors.toList());
					tabCompletes.add("__global__");
					return tabCompletes;
				});
	}
}