package de.zeanon.testutils.plugin.utils.region;

import de.zeanon.jsonfilemanager.JsonFileManager;
import de.zeanon.jsonfilemanager.internal.files.raw.JsonFile;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.TestUtils;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;


public class DefinedRegion implements Region {

	private final @NotNull JsonFile jsonFile;

	public DefinedRegion(final @NotNull String name) {
		this.jsonFile = JsonFileManager.jsonFile(TestUtils.getInstance().getDataFolder(), "Regions/" + name)
									   .create();
	}

	public DefinedRegion(final @NotNull File file) {
		this.jsonFile = JsonFileManager.jsonFile(file)
									   .create();
	}

	public DefinedRegion(final @NotNull String name, final @NotNull Point firstPoint, final @NotNull Point secondPoint, final @NotNull World world) {
		this.jsonFile = JsonFileManager.jsonFile(TestUtils.getInstance().getDataFolder(), "Regions/" + name)
									   .fromResource("resources/region.json")
									   .create();

		final @NotNull Pair<Point, Point> points = this.getPoints(firstPoint, secondPoint);

		this.setPoint(points.getKey(), "highest");
		this.setPoint(points.getValue(), "lowest");
		this.jsonFile.setUseArray(new String[]{"world"}, world.getName());
	}

	public boolean inRegion(final @NotNull Location location) {
		final @NotNull Point minimumPoint = this.getMinimumPoint();
		final @NotNull Point maximumPoint = this.getMaximumPoint();
		return this.getWorld().equals(location.getWorld())
			   && maximumPoint.getX() >= location.getBlockX()
			   && minimumPoint.getX() <= location.getBlockX()
			   && maximumPoint.getY() >= location.getBlockY()
			   && minimumPoint.getY() <= location.getBlockY()
			   && maximumPoint.getZ() >= location.getBlockZ()
			   && minimumPoint.getZ() <= location.getBlockZ();
	}

	@Override
	public void setTnt(final boolean tnt) {
		this.jsonFile.setUseArray(new String[]{"tnt"}, tnt);
	}

	@Override
	public void setStoplag(final boolean stoplag) {
		this.jsonFile.setUseArray(new String[]{"stoplag"}, stoplag);
	}

	@Override
	public void setFire(final boolean fire) {
		this.jsonFile.setUseArray(new String[]{"fire"}, fire);
	}

	@Override
	public void setItemDrops(final boolean itemDrops) {
		this.jsonFile.setUseArray(new String[]{"itemdrops"}, itemDrops);
	}

	@Override
	public void setLeavesDecay(final boolean leavesDecay) {
		this.jsonFile.setUseArray(new String[]{"leavesdecay"}, leavesDecay);
	}

	public void setHasChanged(final boolean hasChanged) {
		this.jsonFile.setUseArray(new String[]{"changed"}, hasChanged);
	}

	@Override
	public @NotNull World getWorld() {
		return Objects.notNull(Bukkit.getWorld(Objects.notNull(this.jsonFile.getStringUseArray("world"))));
	}

	@Override
	public @NotNull String getType() {
		return "defined";
	}

	public @NotNull Point getMinimumPoint() {
		return this.getPoint("lowest");
	}

	public @NotNull Point getMaximumPoint() {
		return this.getPoint("highest");
	}

	@Override
	public @NotNull String getName() {
		return BaseFileUtils.removeExtension(this.jsonFile.getName());
	}

	@Override
	public boolean tnt() {
		return this.jsonFile.getBooleanUseArray("tnt");
	}

	@Override
	public boolean stoplag() {
		return this.jsonFile.getBooleanUseArray("stoplag");
	}

	@Override
	public boolean fire() {
		return this.jsonFile.getBooleanUseArray("fire");
	}

	public boolean hasChanged() {
		return this.jsonFile.getBooleanUseArray("changed");
	}

	@Override
	public boolean itemDrops() {
		return this.jsonFile.getBooleanUseArray("itemdrops");
	}

	@Override
	public boolean leavesDecay() {
		return this.jsonFile.getBooleanUseArray("leavesdecay");
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
}