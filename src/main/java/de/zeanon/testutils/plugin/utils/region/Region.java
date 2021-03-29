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

	private final @NotNull JsonFile jsonFile;

	public Region(final @NotNull String name) {
		this.jsonFile = JsonFileManager.jsonFile(TestUtils.getInstance().getDataFolder(), "Regions/" + name)
									   .concurrentData(true)
									   .create();
	}

	public Region(final @NotNull File file) {
		this.jsonFile = JsonFileManager.jsonFile(file)
									   .concurrentData(true)
									   .create();
	}

	public Region(final @NotNull String name, final @NotNull Point firstPoint, final @NotNull Point secondPoint, final @NotNull World world) {
		this.jsonFile = JsonFileManager.jsonFile(TestUtils.getInstance().getDataFolder(), "Regions/" + name)
									   .fromResource("resources/region.json")
									   .concurrentData(true)
									   .create();
		final @NotNull Pair<Point, Point> points = this.getPoints(firstPoint, secondPoint);

		this.setPoint(points.getKey(), "highest");
		this.setPoint(points.getValue(), "lowest");
		//noinspection unchecked
		this.jsonFile.setAllUseArray(
				new Pair<>(new String[]{"world"}, world.getName()),
				new Pair<>(new String[]{"tnt"}, true),
				new Pair<>(new String[]{"stoplag"}, false),
				new Pair<>(new String[]{"fire"}, true),
				new Pair<>(new String[]{"itemdrops"}, true),
				new Pair<>(new String[]{"changed"}, true)
									);
	}

	public boolean inRegion(final @NotNull Location location) {
		final @NotNull Point minimumPoint = this.getMinimumPoint();
		final @NotNull Point maximumPoint = this.getMaximumPoint();
		return Objects.notNull(this.getWorld()).equals(location.getWorld())
			   && maximumPoint.getX() >= location.getBlockX()
			   && minimumPoint.getX() <= location.getBlockX()
			   && maximumPoint.getY() >= location.getBlockY()
			   && minimumPoint.getY() <= location.getBlockY()
			   && maximumPoint.getZ() >= location.getBlockZ()
			   && minimumPoint.getZ() <= location.getBlockZ();
	}

	public void setTnt(final boolean tnt) {
		this.jsonFile.setUseArray(new String[]{"tnt"}, tnt);
	}

	public void setStoplag(final boolean stoplag) {
		this.jsonFile.set("stoplag", stoplag);
	}

	public void setFire(final boolean fire) {
		this.jsonFile.set("fire", fire);
	}

	public void setItemDrops(final boolean itemDrops) {
		this.jsonFile.set("itemdrops", itemDrops);
	}

	public void setHasChanged(final boolean hasChanged) {
		this.jsonFile.set("changed", hasChanged);
	}

	public World getWorld() {
		return Bukkit.getWorld(Objects.notNull(this.jsonFile.getStringUseArray("world")));
	}

	public @NotNull Point getMinimumPoint() {
		return this.getPoint("lowest");
	}

	public @NotNull Point getMaximumPoint() {
		return this.getPoint("highest");
	}

	public @NotNull String getName() {
		return BaseFileUtils.removeExtension(this.jsonFile.getName());
	}

	public boolean tnt() {
		return this.jsonFile.getBooleanUseArray("tnt");
	}

	public boolean stoplag() {
		return this.jsonFile.getBooleanUseArray("stoplag");
	}

	public boolean fire() {
		return this.jsonFile.getBooleanUseArray("fire");
	}

	public boolean hasChanged() {
		return this.jsonFile.getBooleanUseArray("changed");
	}

	public boolean itemDrops() {
		return this.jsonFile.getBooleanUseArray("itemdrops");
	}

	protected void deleteRegion() {
		this.jsonFile.clearData();
		this.jsonFile.deleteFile();
	}

	private Point getPoint(final @NotNull String path) {
		return new Point(this.jsonFile.getIntUseArray(path, "x"), this.jsonFile.getIntUseArray(path, "y"), this.jsonFile.getIntUseArray(path, "z"));
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