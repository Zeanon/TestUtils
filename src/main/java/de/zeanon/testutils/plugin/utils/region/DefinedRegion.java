package de.zeanon.testutils.plugin.utils.region;

import de.zeanon.jsonfilemanager.JsonFileManager;
import de.zeanon.jsonfilemanager.internal.files.raw.JsonFile;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.enums.Flag;
import java.io.File;
import java.util.EnumMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


public class DefinedRegion implements Region {

	private final @NotNull JsonFile jsonFile;
	private final @NotNull String name;
	private final @NotNull World world;
	private final @NotNull Point highestPoint;
	private final @NotNull Point lowestPoint;
	@SuppressWarnings("rawtypes")
	private final @NotNull Map<Flag, Flag.Value> flags;
	private boolean hasChanged;

	public DefinedRegion(final @NotNull String name) {
		this.jsonFile = JsonFileManager.jsonFile(TestUtils.getInstance().getDataFolder(), "Regions/" + name)
									   .fromResource("resources/region.json")
									   .create();

		this.name = name;

		this.world = Objects.notNull(Bukkit.getWorld(Objects.notNull(this.jsonFile.getStringUseArray("world"))));
		this.highestPoint = this.getPoint("highest");
		this.lowestPoint = this.getPoint("lowest");

		this.flags = new EnumMap<>(Flag.class);
		this.readFlags();

		this.hasChanged = true;
	}

	public DefinedRegion(final @NotNull File file) {
		this.jsonFile = JsonFileManager.jsonFile(file)
									   .fromResource("resources/region.json")
									   .create();

		this.name = BaseFileUtils.removeExtension(this.jsonFile.getName());

		this.world = Objects.notNull(Bukkit.getWorld(Objects.notNull(this.jsonFile.getStringUseArray("world"))));
		this.highestPoint = this.getPoint("highest");
		this.lowestPoint = this.getPoint("lowest");

		this.flags = new EnumMap<>(Flag.class);
		this.readFlags();

		this.hasChanged = false;
	}

	public DefinedRegion(final @NotNull String name, final @NotNull Point firstPoint, final @NotNull Point secondPoint, final @NotNull World world) {
		this.jsonFile = JsonFileManager.jsonFile(TestUtils.getInstance().getDataFolder(), "Regions/" + name)
									   .fromResource("resources/region.json")
									   .create();

		final @NotNull Pair<Point, Point> points = this.getPoints(firstPoint, secondPoint);

		this.name = name;

		this.world = world;
		this.jsonFile.setUseArray(new String[]{"world"}, world.getName());
		this.highestPoint = points.getKey();
		this.setPoint(points.getKey(), "highest");
		this.lowestPoint = points.getValue();
		this.setPoint(points.getValue(), "lowest");

		this.flags = new EnumMap<>(Flag.class);
		for (final @NotNull Flag flag : Flag.getFlags()) {
			this.flags.put(flag, flag.getDefaultValue());
		}

		this.hasChanged = true;

		this.saveData();
	}

	public boolean inRegion(final @NotNull Location location) {
		return this.getWorld().equals(location.getWorld())
			   && this.highestPoint.getX() >= location.getBlockX()
			   && this.lowestPoint.getX() <= location.getBlockX()
			   && this.highestPoint.getY() >= location.getBlockY()
			   && this.lowestPoint.getY() <= location.getBlockY()
			   && this.highestPoint.getZ() >= location.getBlockZ()
			   && this.lowestPoint.getZ() <= location.getBlockZ();
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
		return "defined";
	}

	@Override
	public @NotNull String getName() {
		return this.name;
	}

	public boolean hasChanged() {
		return this.hasChanged;
	}

	@Override
	public void saveData() {
		new BukkitRunnable() {
			@Override
			public void run() {
				DefinedRegion.this.jsonFile.setUseArray(new String[]{"flags"}, DefinedRegion.this.flags);
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}

	public void setHasChanged(final boolean hasChanged) {
		this.hasChanged = hasChanged;
	}

	public @NotNull Point getMinimumPoint() {
		return this.lowestPoint;
	}

	public @NotNull Point getMaximumPoint() {
		return this.highestPoint;
	}

	protected void deleteRegion() {
		this.jsonFile.clearData();
		this.jsonFile.deleteFile();
	}

	private Point getPoint(final @NotNull String path) {
		return new Point(this.jsonFile.getIntUseArray(path, "x"),
						 this.jsonFile.getIntUseArray(path, "y"),
						 this.jsonFile.getIntUseArray(path, "z"));
	}

	private void setPoint(final @NotNull Point point, final @NotNull String path) {
		//noinspection unchecked
		this.jsonFile.setAllUseArray(
				new Pair<>(new String[]{path, "x"}, point.getX()),
				new Pair<>(new String[]{path, "y"}, point.getY()),
				new Pair<>(new String[]{path, "z"}, point.getZ())
									);
	}

	private Pair<Point, Point> getPoints(final @NotNull Point firstPoint, final @NotNull Point secondPoint) {
		return new Pair<>(new Point(
				Math.max(firstPoint.getX(), secondPoint.getX()),
				Math.max(firstPoint.getY(), secondPoint.getY()),
				Math.max(firstPoint.getZ(), secondPoint.getZ())),
						  new Point(
								  Math.min(firstPoint.getX(), secondPoint.getX()),
								  Math.min(firstPoint.getY(), secondPoint.getY()),
								  Math.min(firstPoint.getZ(), secondPoint.getZ())));
	}

	private void readFlags() {
		final @NotNull Map<String, Object> tempFlag = Objects.notNull(this.jsonFile.getMap("flags"));
		for (final @NotNull Map.Entry<String, Object> flagValue : tempFlag.entrySet()) {
			final @NotNull Flag flag = Flag.valueOf(flagValue.getKey().toUpperCase());
			this.flags.put(flag, flag.getDefaultValue().getValue(flagValue.getValue().toString()));
		}
	}
}