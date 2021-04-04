package de.zeanon.testutils.regionsystem.region;

import com.sk89q.worldedit.math.BlockVector3;
import de.zeanon.jsonfilemanager.internal.files.raw.JsonFile;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.regionsystem.flags.Flag;
import java.util.EnumMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class Region {

	protected final @NotNull JsonFile jsonFile;

	protected final @NotNull String name;
	protected final @NotNull World world;
	protected final @NotNull Map<Flag, Flag.Value<?>> flags;

	protected Region(final @NotNull JsonFile jsonFile, final @NotNull String name, final @NotNull World world) {
		this.jsonFile = jsonFile;
		this.name = name;
		this.world = world;

		this.flags = new EnumMap<>(Flag.class);
		this.readFlags();
	}

	protected Region(final @NotNull JsonFile jsonFile) {
		this.jsonFile = jsonFile;
		this.name = BaseFileUtils.removeExtension(this.jsonFile.getName());
		this.world = Objects.notNull(Bukkit.getWorld(Objects.notNull(this.jsonFile.getStringUseArray("world"))));

		this.flags = new EnumMap<>(Flag.class);
		this.readFlags();
	}

	public void set(final @NotNull Flag flagType, final @NotNull Flag.Value<?> value) {
		if (this.flags.put(flagType, value) != value) {
			this.saveData();
		}
	}

	public Flag.Value<?> get(final @NotNull Flag flagType) {
		return this.flags.get(flagType);
	}

	public @NotNull Map<Flag, Flag.Value<?>> getFlags() {
		return this.flags;
	}

	public @NotNull World getWorld() {
		return this.world;
	}

	public @NotNull String getName() {
		return this.name;
	}

	public abstract @NotNull String getType();


	public void saveData() {
		this.jsonFile.save();
	}


	private void readFlags() {
		final @NotNull Map<String, String> tempFlagMap = Objects.notNull(Region.this.jsonFile.getMap("flags"));
		for (final @NotNull Flag flag : Flag.getFlags()) {
			final @Nullable String flagValue = tempFlagMap.get(flag.toString());
			this.flags.put(flag, flagValue == null ? flag.getDefaultValue() : flag.getFlagValueOf(flagValue.toUpperCase()));
		}
		this.jsonFile.setUseArrayWithoutCheck(new String[]{"flags"}, this.flags);
	}


	@Getter
	@AllArgsConstructor
	public static class Point {

		final int x;
		final int y;
		final int z;

		public @NotNull BlockVector3 toBlockVector3() {
			return BlockVector3.at(this.x, this.y, this.z);
		}
	}
}