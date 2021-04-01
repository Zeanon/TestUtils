package de.zeanon.testutils.plugin.utils.region;

import de.zeanon.jsonfilemanager.JsonFileManager;
import de.zeanon.jsonfilemanager.internal.files.raw.JsonFile;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.enums.Flag;
import java.io.File;
import java.util.EnumMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings("unused")
public class GlobalRegion implements Region {

	private final @NotNull JsonFile jsonFile;

	private final @NotNull String name;
	private final @NotNull World world;
	@SuppressWarnings("rawtypes")
	private final @NotNull Map<Flag, Flag.Value> flags;

	public GlobalRegion(final @NotNull String world) {
		this.jsonFile = JsonFileManager.jsonFile(TestUtils.getInstance().getDataFolder(), "Regions/__" + world + "__")
									   .create();

		this.name = "__" + world + "__";

		this.world = Objects.notNull(Bukkit.getWorld(world));
		this.jsonFile.setUseArray(new String[]{"world"}, world);

		this.flags = new EnumMap<>(Flag.class);
		this.readFlags();
	}

	GlobalRegion(final @NotNull File file) {
		this.jsonFile = JsonFileManager.jsonFile(file)
									   .fromResource("resources/global.json")
									   .create();

		this.name = BaseFileUtils.removeExtension(this.jsonFile.getName());
		this.world = Objects.notNull(Bukkit.getWorld(Objects.notNull(this.jsonFile.getStringUseArray("world"))));

		this.flags = new EnumMap<>(Flag.class);
		this.readFlags();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void set(final @NotNull Flag flagType, final @NotNull Flag.Value value) {
		this.flags.put(flagType, value);
		this.saveData();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Flag.Value get(final @NotNull Flag flagType) {
		return this.flags.get(flagType);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public @NotNull Map<Flag, Flag.Value> getFlags() {
		return this.flags;
	}

	@Override
	public @NotNull World getWorld() {
		return this.world;
	}

	@Override
	public @NotNull String getType() {
		return "global";
	}

	@Override
	public @NotNull String getName() {
		return this.name;
	}


	@Override
	public void saveData() {
		new BukkitRunnable() {
			@Override
			public void run() {
				GlobalRegion.this.jsonFile.setUseArray(new String[]{"flags"}, GlobalRegion.this.flags);
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}

	private void readFlags() {
		final @NotNull Map<String, Object> tempFlag = Objects.notNull(this.jsonFile.getMap("flags"));
		for (final @NotNull Map.Entry<String, Object> flagValue : tempFlag.entrySet()) {
			final @NotNull Flag flag = Flag.valueOf(flagValue.getKey().toUpperCase());
			this.flags.put(flag, flag.getDefaultValue().getValue(flagValue.getValue().toString()));
		}
	}
}