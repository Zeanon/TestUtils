package de.zeanon.testutils.regionsystem.region;

import com.sk89q.worldedit.math.BlockVector3;
import de.zeanon.jsonfilemanager.internal.files.raw.JsonFile;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.regionsystem.RegionType;
import de.zeanon.testutils.regionsystem.flags.Flag;
import de.zeanon.testutils.regionsystem.tags.Tag;
import java.util.EnumMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@SuppressWarnings("unused")
public abstract class Region {

	protected final @NotNull JsonFile jsonFile;
	protected final @NotNull String name;
	protected final @NotNull World world;
	protected final @NotNull RegionType regionType;
	protected final @NotNull Map<Flag, Flag.Value<?>> flags;
	protected final @NotNull Map<Tag, Tag.Value<?>> tags;


	protected Region(final @NotNull JsonFile jsonFile, final @NotNull String name, final @NotNull World world, final @NotNull RegionType regionType) {
		this.jsonFile = jsonFile;
		this.name = name;
		this.world = world;
		this.jsonFile.setUseArray(new String[]{"world"}, this.world.getName());

		this.regionType = regionType;
		this.jsonFile.setUseArray(new String[]{"regiontype"}, this.regionType.name());

		this.flags = new EnumMap<>(Flag.class);
		this.readFlags();

		this.tags = new EnumMap<>(Tag.class);
		this.readTags();
	}

	protected Region(final @NotNull JsonFile jsonFile) {
		this.jsonFile = jsonFile;
		this.name = BaseFileUtils.removeExtension(this.jsonFile.getName());
		this.world = Objects.notNull(Bukkit.getWorld(Objects.notNull(this.jsonFile.getStringUseArray("world"))));

		this.regionType = RegionType.valueOf(this.jsonFile.getStringUseArray("regiontype"));

		this.flags = new EnumMap<>(Flag.class);
		this.readFlags();

		this.tags = new EnumMap<>(Tag.class);
		this.readTags();
	}


	public void setFlag(final @NotNull Flag flagType, final @Nullable Flag.Value<?> value) {
		if (value == null) {
			if (this.flags.remove(flagType) != null) {
				this.saveData();
			}
		} else {
			if (this.flags.put(flagType, value) != value) {
				this.saveData();
			}
		}
	}

	public void removeFlag(final @NotNull Flag flagType) {
		if (this.flags.remove(flagType) != null) {
			this.saveData();
		}
	}

	public @Nullable Flag.Value<?> getFlag(final @NotNull Flag flagType) {
		return this.flags.get(flagType);
	}

	public @NotNull Flag.Value<?> getFlagOrDefault(final @NotNull Flag flagType) {
		final @Nullable Flag.Value<?> value = this.getFlag(flagType);
		if (value != null) {
			return value;
		} else {
			return flagType.getDefaultValue();
		}
	}

	public @NotNull Flag.Value<?> getFlagOrDefault(final @NotNull Flag flagType, final @NotNull Flag.Value<?> defaultValue) {
		final @Nullable Flag.Value<?> value = this.getFlag(flagType);
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}

	public @NotNull Map<Flag, Flag.Value<?>> getFlags() {
		return this.flags;
	}


	public void setTag(final @NotNull Tag tagType, final @Nullable Tag.Value<?> value) {
		if (value == null) {
			if (this.tags.remove(tagType) != null) {
				this.saveData();
			}
		} else {
			if (this.tags.put(tagType, value) != value) {
				this.saveData();
			}
		}
	}

	public void removeTag(final @NotNull Tag tagType) {
		if (this.tags.remove(tagType) != null) {
			this.saveData();
		}
	}

	public @Nullable Tag.Value<?> getTag(final @NotNull Tag tagType) {
		return this.tags.get(tagType);
	}

	public @NotNull Tag.Value<?> getTagOrDefault(final @NotNull Tag tagType) {
		final @Nullable Tag.Value<?> value = this.getTag(tagType);
		if (value != null) {
			return value;
		} else {
			return tagType.getDefaultValue();
		}
	}

	public @NotNull Tag.Value<?> getTagOrDefault(final @NotNull Tag tagType, final @NotNull Tag.Value<?> defaultValue) {
		final @Nullable Tag.Value<?> value = this.getTag(tagType);
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}

	public @NotNull Map<Tag, Tag.Value<?>> getTags() {
		return this.tags;
	}

	public @NotNull World getWorld() {
		return this.world;
	}

	public abstract boolean inRegion(final @NotNull Location location);

	public @NotNull String getName() {
		return this.name;
	}

	public @NotNull RegionType getType() {
		return this.regionType;
	}

	public void saveData() {
		this.jsonFile.save();
	}

	public void readData() {
		this.jsonFile.reload();
	}


	protected void readFlags() {
		final @Nullable Map<String, Object> tempFlagMap = this.jsonFile.getDirectMapReference("flags");

		if (tempFlagMap == null) {
			return;
		}

		for (final @NotNull Map.Entry<String, Object> entry : tempFlagMap.entrySet()) {
			try {
				final @NotNull Flag flag = Flag.valueOf(entry.getKey().toUpperCase());
				final @Nullable String flagValue = Objects.toString(tempFlagMap.get(flag.toString()));
				this.flags.put(flag, flagValue == null ? flag.getDefaultValue() : flag.getFlagValueOf(flagValue));
			} catch (final @NotNull IllegalArgumentException e) {
				//NOTHING
			}
		}

		this.jsonFile.setUseArrayWithoutCheck(new String[]{"flags"}, this.flags);
	}

	protected void readTags() {
		final @Nullable Map<String, Object> tempTagMap = this.jsonFile.getDirectMapReference("tags");

		if (tempTagMap == null) {
			return;
		}

		for (final @NotNull Map.Entry<String, Object> entry : tempTagMap.entrySet()) {
			try {
				final @NotNull Tag tag = Tag.valueOf(entry.getKey().toUpperCase());
				final @Nullable String tagValue = Objects.toString(tempTagMap.get(tag.toString()));
				this.tags.put(tag, tagValue == null ? tag.getDefaultValue() : tag.getTagValueOf(tagValue.toUpperCase()));
			} catch (final @NotNull IllegalArgumentException e) {
				//NOTHING
			}
		}

		this.jsonFile.setUseArrayWithoutCheck(new String[]{"tags"}, this.tags);
	}


	@Getter
	@EqualsAndHashCode
	@AllArgsConstructor
	public static class Point {

		final int x;
		final int y;
		final int z;

		@SuppressWarnings("unused")
		public static @Nullable Point fromString(final @NotNull String string) {
			if (!string.startsWith("[x=") || !string.endsWith("]") || !string.contains("|y=") || !string.contains("|z=")) {
				return null;
			}

			final @NotNull String[] parts = string.substring(1, string.length() - 2).split("\\|");

			return new Point(Integer.parseInt(parts[0].substring(2)), Integer.parseInt(parts[1].substring(2)), Integer.parseInt(parts[2].substring(2)));
		}

		public static @NotNull Point fromLocation(final @NotNull Location location) {
			return new Point(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		}

		public static @NotNull Point fromBlockVector3(final @NotNull BlockVector3 blockVector3) {
			return new Point(blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getZ());
		}

		public Point add(final int x, final int y, final int z) {
			return new Point(this.x + x, this.y + y, this.z + z);
		}

		public Point subtract(final int x, final int y, final int z) {
			return new Point(this.x - x, this.y - y, this.z - z);
		}

		public @NotNull BlockVector3 toBlockVector3() {
			return BlockVector3.at(this.x, this.y, this.z);
		}

		@Override
		public String toString() {
			return "[x=" + this.x + "|y=" + this.y + "|z=" + this.z + "]";
		}
	}
}