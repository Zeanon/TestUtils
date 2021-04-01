package de.zeanon.testutils.plugin.mapper;

import de.zeanon.testutils.commandframework.SWCommandUtils;
import de.zeanon.testutils.commandframework.TypeMapper;
import de.zeanon.testutils.plugin.utils.enums.*;
import de.zeanon.testutils.plugin.utils.region.Region;
import de.zeanon.testutils.plugin.utils.region.RegionManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Mapper {

	public void initialize() {
		SWCommandUtils.addMapper(RegionSide.class, Mapper.mapRegionSide());
		SWCommandUtils.addMapper(TNTMode.class, Mapper.mapTNTMode());
		SWCommandUtils.addMapper(GlobalToggle.class, Mapper.mapGlobalToggle());
		SWCommandUtils.addMapper(StoplagToggle.class, Mapper.mapStoplagToggle());
		SWCommandUtils.addMapper(BackupMode.class, Mapper.mapBackupMode());
		SWCommandUtils.addMapper(CommandConfirmation.class, Mapper.mapCommandConfirmation());
		SWCommandUtils.addMapper(RegionName.class, Mapper.mapRegionName());
		SWCommandUtils.addMapper(Flag.Value.class.getTypeName(), Mapper.mapFlagValue());
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

	private @NotNull TypeMapper<RegionName> mapRegionName() {
		return SWCommandUtils.createMapper(s -> Flag.getFlags().stream().anyMatch(f -> s.equalsIgnoreCase(f.name())) ? null : new RegionName(s)
				, s -> RegionManager.getRegions().stream().map(Region::getName).collect(Collectors.toList()));
	}

	private @NotNull <T extends Enum<T> & Flag.Value<T>> TypeMapper<Flag.Value<T>> mapFlagValue() {
		return new TypeMapper<Flag.Value<T>>() {
			@Override
			public Flag.Value<T> map(final @NotNull String[] previousArguments, final @NotNull String s) {
				final @Nullable Flag flag = this.getFlag(previousArguments); //NOSONAR
				if (flag != null) {
					//noinspection rawtypes
					final @NotNull Enum[] values = (Enum[]) flag.getValue().getEnumConstants(); //NOSONAR
					//noinspection rawtypes
					for (final @NotNull Enum tempEnum : values) { //NOSONAR
						if ((tempEnum.name()).equalsIgnoreCase(s)) {
							// noinspection unchecked
							return (T) tempEnum;
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
					return Arrays.stream(values).map(Enum::toString).map(String::toLowerCase).collect(Collectors.toList());
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
}