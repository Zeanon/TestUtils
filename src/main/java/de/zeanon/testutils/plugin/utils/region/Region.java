package de.zeanon.testutils.plugin.utils.region;

import com.sk89q.worldedit.math.BlockVector3;
import de.zeanon.jsonfilemanager.JsonFileManager;
import de.zeanon.jsonfilemanager.internal.files.raw.JsonFile;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.TestUtils;
import java.io.File;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;


public class Region {

	private final @NotNull BlockVector3 maximumPoint;
	private final @NotNull BlockVector3 minimumPoint;
	private final @NotNull World world;
	private final @NotNull JsonFile jsonFile;
	private final @NotNull String name;
	private boolean stoplag;
	private boolean fire;
	private boolean hasChanged;
	private boolean itemDrops;

	public Region(final @NotNull String name) {
		this.jsonFile = JsonFileManager.jsonFile(TestUtils.getInstance().getDataFolder(), "Regions/" + name)
									   .concurrentData(true)
									   .create();
		this.name = name;
		this.world = Objects.notNull(Bukkit.getWorld(Objects.notNull(this.jsonFile.getString("world"))));
		this.maximumPoint = this.getLocation(this.world, "highest");
		this.minimumPoint = this.getLocation(this.world, "lowest");

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
		this.jsonFile.setUseArray(new String[]{"tnt"}, tnt);
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

	public @NotNull BlockVector3 getMaximumPoint() {
		return this.maximumPoint;
	}

	public @NotNull BlockVector3 getMinimumPoint() {
		return this.minimumPoint;
	}

	public @NotNull String getName() {
		return this.name;
	}

	public boolean tnt() {
		return this.jsonFile.getBooleanUseArray("tnt");
	}

	public boolean stoplag() {
		return this.stoplag;
	}

	public boolean fire() {
		return this.fire;
	}

	public boolean hasChanged() {
		return this.hasChanged;
	}

	public boolean itemDrops() {
		return this.itemDrops;
	}

	protected void deleteRegion() {
		this.jsonFile.deleteFile();
	}

	private Location getPosition(final @NotNull String path) {
		return new Location(this.world, this.jsonFile.getInt(path + ".x"), this.jsonFile.getInt(path + ".y"), this.jsonFile.getInt(path + ".z"));
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

	@Getter
	@AllArgsConstructor
	public static class Point {

		final int x;
		final int y;
		final int z;
	}
}