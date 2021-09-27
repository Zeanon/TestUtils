package de.zeanon.testutils.regionsystem;

import de.zeanon.storagemanagercore.internal.base.exceptions.ObjectNullException;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import de.zeanon.testutils.regionsystem.region.GlobalRegion;
import de.zeanon.testutils.regionsystem.region.Region;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class RegionManager {


	public static final @NotNull Path DEFINED_REGIONS_FOLDER = TestUtils.getPluginFolder().resolve("Regions").resolve("Defined");
	public static final @NotNull Path GLOBAL_REGIONS_FOLDER = TestUtils.getPluginFolder().resolve("Regions").resolve("Global");
	@Getter
	private final @NotNull Set<DefinedRegion> regions = new HashSet<>();
	@Getter
	private final @NotNull Map<String, GlobalRegion> globalRegions = new HashMap<>();


	public void initialize() throws IOException {
		BaseFileUtils.createFolder(RegionManager.DEFINED_REGIONS_FOLDER);
		BaseFileUtils.createFolder(RegionManager.GLOBAL_REGIONS_FOLDER);

		RegionManager.initializeDefinedRegions();
		RegionManager.initializeGlobalRegions();
	}

	public void initializeDefinedRegions() throws IOException {
		RegionManager.regions.clear();
		BaseFileUtils.listFilesOfType(RegionManager.DEFINED_REGIONS_FOLDER.toFile(), "json").forEach(file -> {
			try {
				RegionManager.regions.add(new DefinedRegion(file));
			} catch (final @NotNull ObjectNullException e) {
				System.out.println("[" + TestUtils.getInstance().getName() + "] >> TestAreas >> " + file.getName() + " could not be initialized properly, please check the region file.");
				e.printStackTrace();
			}
		});
	}

	public void initializeGlobalRegions() {
		RegionManager.globalRegions.clear();
		Bukkit.getWorlds().forEach(world -> {
			try {
				RegionManager.globalRegions.put("__" + world.getName() + "__", new GlobalRegion(world));
			} catch (final @NotNull ObjectNullException e) {
				System.out.println("[" + TestUtils.getInstance().getName() + "] >> GlobalRegions >> " + world.getName() + " could not be initialized properly, please check the region file.");
				e.printStackTrace();
			}
		});
	}


	public @NotNull GlobalRegion getGlobalRegion(final @NotNull World world) {
		return RegionManager.globalRegions.entrySet()
										  .stream()
										  .filter(entry -> entry.getKey().equals("__" + world.getName() + "__"))
										  .map(Map.Entry::getValue)
										  .findFirst()
										  .orElseGet(() -> new GlobalRegion(world));
	}

	public @NotNull List<DefinedRegion> getApplicableRegions(final @NotNull Location location) {
		return RegionManager.regions.stream().filter(region -> region.inRegion(location)).collect(Collectors.toList());
	}

	public void executeOnApplicableRegion(final @NotNull Location location, final @NotNull Consumer<DefinedRegion> action) {
		RegionManager.regions.stream().filter(region -> region.inRegion(location)).forEach(action);
	}

	public @Nullable DefinedRegion getDefinedRegion(final @NotNull String name) {
		return RegionManager.regions.stream().filter(region -> region.getName().equals(name)).findFirst().orElse(null);
	}

	public void addDefinedRegion(final @NotNull DefinedRegion region) {
		RegionManager.regions.add(region);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean hasDefinedRegion(final @NotNull String name) {
		return RegionManager.regions.stream().anyMatch(region -> region.getName().equals(name));
	}

	public boolean hasGlobalRegion(final @NotNull String name) {
		return RegionManager.getGlobalRegions().containsKey(name);
	}

	public boolean removeDefinedRegion(final @NotNull DefinedRegion region) {
		if (RegionManager.regions.remove(region)) {
			region.deleteRegion();
			return true;
		} else {
			return false;
		}
	}

	public boolean isGlobalRegion(final @NotNull String name) {
		return name.startsWith("__") && name.endsWith("__");
	}

	public void saveRegions() {
		RegionManager.getRegions().forEach(Region::saveData);

		RegionManager.getGlobalRegions().forEach((name, region) -> region.saveData());
	}

	public void reloadRegions() throws IOException {
		RegionManager.initialize();
	}

	public boolean intersectsCuboid(final @NotNull Region.Point aMin, final @NotNull Region.Point aMax, final @NotNull Region.Point bMin, final @NotNull Region.Point bMax) {
		if (RegionManager.noIntersect(aMin.getX(), aMax.getX(), bMin.getX(), bMax.getX())) {
			return false;
		}

		if (RegionManager.noIntersect(aMin.getY(), aMax.getY(), bMin.getY(), bMax.getY())) {
			return false;
		}

		if (RegionManager.noIntersect(aMin.getZ(), aMax.getZ(), bMin.getZ(), bMax.getZ())) {
			return false;
		}

		return true;
	}

	public boolean noIntersect(final int aMin, final int aMax, final int bMin, final int bMax) {
		return aMin > bMax || aMax < bMin;
	}
}