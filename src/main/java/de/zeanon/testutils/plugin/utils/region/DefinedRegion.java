package de.zeanon.testutils.plugin.utils.region;

import de.zeanon.jsonfilemanager.JsonFileManager;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.TestUtils;
import java.io.File;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;


public class DefinedRegion extends Region {

	private final @NotNull Point highestPoint;
	private final @NotNull Point lowestPoint;
	private boolean hasChanged;


	public DefinedRegion(final @NotNull File file) {
		super(JsonFileManager.jsonFile(file)
							 .fromResource("resources/region.json")
							 .create());
		this.highestPoint = this.getPoint("highest");
		this.lowestPoint = this.getPoint("lowest");

		this.hasChanged = false;
	}

	public DefinedRegion(final @NotNull String name, final @NotNull Point firstPoint, final @NotNull Point secondPoint, final @NotNull World world) {
		super(JsonFileManager.jsonFile(TestUtils.getInstance().getDataFolder(), "Regions/" + name)
							 .fromResource("resources/region.json")
							 .create(),
			  name,
			  world);

		final @NotNull Pair<Point, Point> points = this.getPoints(firstPoint, secondPoint);

		this.jsonFile.setUseArray(new String[]{"world"}, world.getName());
		this.highestPoint = points.getKey();
		this.setPoint(points.getKey(), "highest");
		this.lowestPoint = points.getValue();
		this.setPoint(points.getValue(), "lowest");

		this.hasChanged = true;
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

	@Override
	public @NotNull String getType() {
		return "defined";
	}

	public boolean hasChanged() {
		return this.hasChanged;
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
}