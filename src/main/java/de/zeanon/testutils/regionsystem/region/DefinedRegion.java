package de.zeanon.testutils.regionsystem.region;

import de.zeanon.jsonfilemanager.JsonFileManager;
import de.zeanon.jsonfilemanager.internal.files.section.JsonFileSection;
import de.zeanon.storagemanagercore.internal.base.settings.Reload;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.regionsystem.RegionManager;
import de.zeanon.testutils.regionsystem.RegionType;
import java.io.File;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;


public class DefinedRegion extends Region {

	private final @NotNull Point maxPoint;
	private final @NotNull Point minPoint;
	private boolean hasChanged;


	public DefinedRegion(final @NotNull File file) {
		super(JsonFileManager.jsonFile(file)
							 .fromResource("resources/region.json")
							 .reloadSetting(Reload.MANUALLY)
							 .create());

		this.maxPoint = this.getPoint("max");

		this.minPoint = this.getPoint("min");

		this.saveData();

		this.hasChanged = false;
	}

	@SuppressWarnings("unused")
	public DefinedRegion(final @NotNull String name, final @NotNull Point firstPoint, final @NotNull Point secondPoint, final @NotNull World world) {
		super(JsonFileManager.jsonFile(RegionManager.DEFINED_REGIONS_FOLDER.resolve(name + ".json"))
							 .fromResource("resources/region.json")
							 .reloadSetting(Reload.MANUALLY)
							 .create(),
			  name,
			  world,
			  RegionType.DEFINED_REGION);

		final @NotNull Pair<Point, Point> points = this.sortPoints(firstPoint, secondPoint);

		this.maxPoint = points.getKey();
		this.setPoint(this.maxPoint, "max");
		this.minPoint = Objects.notNull(points.getValue());
		this.setPoint(this.minPoint, "min");

		this.saveData();

		this.hasChanged = false;
	}

	public DefinedRegion(final @NotNull String name, final @NotNull Point firstPoint, final @NotNull Point secondPoint, final @NotNull World world, final @NotNull RegionType regionType) {
		super(JsonFileManager.jsonFile(RegionManager.DEFINED_REGIONS_FOLDER.resolve(name + ".json"))
							 .fromResource("resources/region.json")
							 .reloadSetting(Reload.MANUALLY)
							 .create(),
			  name,
			  world,
			  regionType);

		final @NotNull Pair<Point, Point> points = this.sortPoints(firstPoint, secondPoint);

		this.maxPoint = points.getKey();
		this.setPoint(this.maxPoint, "max");
		this.minPoint = Objects.notNull(points.getValue());
		this.setPoint(this.minPoint, "min");

		this.saveData();

		this.hasChanged = false;
	}


	@Override
	public boolean inRegion(final @NotNull Location location) {
		return this.getWorld().equals(location.getWorld())
			   && this.maxPoint.getX() >= location.getBlockX()
			   && this.minPoint.getX() <= location.getBlockX()
			   && this.maxPoint.getY() >= location.getBlockY()
			   && this.minPoint.getY() <= location.getBlockY()
			   && this.maxPoint.getZ() >= location.getBlockZ()
			   && this.minPoint.getZ() <= location.getBlockZ();
	}

	public boolean hasChanged() {
		return this.hasChanged;
	}

	public void setHasChanged(final boolean hasChanged) {
		this.hasChanged = hasChanged;
	}

	public @NotNull Point getMinimumPoint() {
		return this.minPoint;
	}

	public @NotNull Point getMaximumPoint() {
		return this.maxPoint;
	}


	public void deleteRegion() {
		this.jsonFile.clearData();
		this.jsonFile.deleteFile();
	}


	private @NotNull Point getPoint(final @NotNull String path) {
		final @NotNull JsonFileSection section = this.jsonFile.getSectionUseArray("points", path);
		return new Point(section.getIntUseArray("x"),
						 section.getIntUseArray("y"),
						 section.getIntUseArray("z"));
	}

	private void setPoint(final @NotNull Point point, final @NotNull String path) {
		final @NotNull JsonFileSection section = this.jsonFile.getSectionUseArray("points", path);
		//noinspection unchecked
		section.setAllUseArrayWithoutCheck(
				new Pair<>(new String[]{"x"}, point.getX()),
				new Pair<>(new String[]{"y"}, point.getX()),
				new Pair<>(new String[]{"z"}, point.getX())
										  );
	}


	private @NotNull Pair<Point, Point> sortPoints(final @NotNull Point firstPoint, final @NotNull Point secondPoint) {
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