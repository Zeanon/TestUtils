package de.zeanon.testutils.plugin.utils.region;

import de.zeanon.jsonfilemanager.JsonFileManager;
import de.zeanon.jsonfilemanager.internal.files.raw.JsonFile;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.TestUtils;
import java.io.File;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;


public class Region {

	@Getter
	private final @NotNull Location maximumPoint;
	@Getter
	private final @NotNull Location minimumPoint;
	private final @NotNull JsonFile jsonFile;
	@Getter
	private final @NotNull String name;
	@Getter
	@Accessors(fluent = true)
	private boolean tnt;
	@Getter
	@Accessors(fluent = true)
	private boolean stoplag;
	@Getter
	@Accessors(fluent = true)
	private boolean fire;
	@Getter
	@Accessors(fluent = true)
	private boolean hasChanged;
	@Getter
	@Accessors(fluent = true)
	private boolean itemDrops;

	public Region(final @NotNull String name) {
		this.jsonFile = JsonFileManager.jsonFile(TestUtils.getInstance().getDataFolder(), "Regions/" + name)
									   .concurrentData(true)
									   .create();
		this.name = name;
		final @NotNull World world = Objects.notNull(Bukkit.getWorld(Objects.notNull(this.jsonFile.getString("world"))));
		this.maximumPoint = this.getLocation(world, "highest");
		this.minimumPoint = this.getLocation(world, "lowest");

		this.tnt = this.jsonFile.getBoolean("tnt");
		this.stoplag = this.jsonFile.getBoolean("stoplag");
		this.fire = this.jsonFile.getBoolean("fire");
		this.hasChanged = this.jsonFile.getBoolean("changed");
	}

	public Region(final @NotNull File file) {
		this.jsonFile = JsonFileManager.jsonFile(file)
									   .concurrentData(true)
									   .create();
		this.name = BaseFileUtils.removeExtension(file.getName());
		final @NotNull World world = Objects.notNull(Bukkit.getWorld(Objects.notNull(this.jsonFile.getString("world"))));
		this.maximumPoint = this.getLocation(world, "highest");
		this.minimumPoint = this.getLocation(world, "lowest");

		this.tnt = this.jsonFile.getBoolean("tnt");
		this.stoplag = this.jsonFile.getBoolean("stoplag");
		this.fire = this.jsonFile.getBoolean("fire");
		this.hasChanged = this.jsonFile.getBoolean("changed");
		this.itemDrops = this.jsonFile.getBoolean("itemdrops");
	}

	public Region(final @NotNull String name, final @NotNull Location firstPoint, final @NotNull Location secondPoint) {
		this.jsonFile = JsonFileManager.jsonFile(TestUtils.getInstance().getDataFolder(), "Regions/" + name)
									   .fromResource("resources/region.json")
									   .concurrentData(true)
									   .create();
		this.name = name;
		final @NotNull Pair<Location, Location> points = this.getPoints(firstPoint, secondPoint);
		this.maximumPoint = points.getKey();
		this.minimumPoint = points.getValue();

		this.tnt = true;
		this.stoplag = false;
		this.fire = true;
		this.hasChanged = false;
		this.itemDrops = true;

		this.save();
	}

	public boolean inRegion(final @NotNull Location location) {
		return Objects.notNull(this.maximumPoint.getWorld()).equals(location.getWorld())
			   && this.maximumPoint.getBlockX() >= location.getBlockX()
			   && this.minimumPoint.getBlockX() <= location.getBlockX()
			   && this.maximumPoint.getBlockY() >= location.getBlockY()
			   && this.minimumPoint.getBlockY() <= location.getBlockY()
			   && this.maximumPoint.getBlockZ() >= location.getBlockZ()
			   && this.minimumPoint.getBlockZ() <= location.getBlockZ();
	}

	public void setTnt(final boolean tnt) {
		this.tnt = tnt;
		this.jsonFile.set("tnt", this.tnt);
	}

	public void setStoplag(final boolean stoplag) {
		this.stoplag = stoplag;
		this.jsonFile.set("stoplag", this.stoplag);
	}

	public void setFire(final boolean fire) {
		this.fire = fire;
		this.jsonFile.set("fire", this.fire);
	}

	public void setItemDrops(final boolean itemDrops) {
		this.itemDrops = itemDrops;
		this.jsonFile.set("itemdrops", this.itemDrops);
	}

	public World getWorld() {
		return this.maximumPoint.getWorld();
	}

	public void setHasChanged(final boolean hasChanged) {
		this.hasChanged = hasChanged;
		this.jsonFile.set("changed", this.hasChanged);
	}

	protected void deleteRegion() {
		this.jsonFile.deleteFile();
	}

	private Location getLocation(final @NotNull World world, final @NotNull String path) {
		return new Location(world, this.jsonFile.getInt(path + ".x"), this.jsonFile.getInt(path + ".y"), this.jsonFile.getInt(path + ".z"));
	}

	private void setLocation(final @NotNull Location location, final @NotNull String path) {
		//noinspection unchecked
		this.jsonFile.setAll(
				new Pair<>("world", Objects.notNull(location.getWorld()).getName()),
				new Pair<>(path + ".x", location.getBlockX()),
				new Pair<>(path + ".y", location.getBlockY()),
				new Pair<>(path + ".z", location.getBlockZ())
							);
	}

	private void save() {
		this.setLocation(this.maximumPoint, "highest");
		this.setLocation(this.minimumPoint, "lowest");
		//noinspection unchecked
		this.jsonFile.setAll(
				new Pair<>("tnt", this.tnt),
				new Pair<>("stoplag", this.stoplag),
				new Pair<>("fire", this.fire),
				new Pair<>("changed", this.hasChanged),
				new Pair<>("itemdrops", this.itemDrops));
	}

	private Pair<Location, Location> getPoints(final @NotNull Location firstPoint, final @NotNull Location secondPoint) {
		return new Pair<>(new Location(firstPoint.getWorld(),
									   Math.max(firstPoint.getBlockX(), secondPoint.getBlockX()),
									   Math.max(firstPoint.getBlockY(), secondPoint.getBlockY()),
									   Math.max(firstPoint.getBlockZ(), secondPoint.getBlockZ())),
						  new Location(secondPoint.getWorld(),
									   Math.min(secondPoint.getBlockX(), firstPoint.getBlockX()),
									   Math.min(secondPoint.getBlockY(), firstPoint.getBlockY()),
									   Math.min(secondPoint.getBlockZ(), firstPoint.getBlockZ())));
	}
}