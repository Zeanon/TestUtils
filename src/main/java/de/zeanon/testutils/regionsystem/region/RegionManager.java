package de.zeanon.testutils.regionsystem.region;

import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
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


		RegionManager.initializeTestAreas();
		RegionManager.initializeGlobalRegions();
	}

	public void initializeTestAreas() throws IOException {
		RegionManager.regions.clear();
		for (final @NotNull File file : BaseFileUtils.listFilesOfType(RegionManager.DEFINED_REGIONS_FOLDER.toFile(), "json")) {
			if (!file.getName().startsWith("__") && !file.getName().endsWith("__.json")) {
				RegionManager.regions.add(new TestArea(file));
			}
		}
	}

	public void initializeGlobalRegions() {
		RegionManager.globalRegions.clear();
		for (final @NotNull World world : Bukkit.getWorlds()) {
			RegionManager.globalRegions.put("__" + world.getName() + "__", new GlobalRegion(world));
		}
	}


	public @NotNull GlobalRegion getGlobalRegion(final @NotNull World world) {
		final @Nullable GlobalRegion globalRegion = RegionManager.globalRegions.get("__" + world.getName() + "__");
		if (globalRegion == null) {
			RegionManager.initializeGlobalRegions();
			return Objects.notNull(RegionManager.globalRegions.get("__" + world.getName() + "__"));
		} else {
			return globalRegion;
		}
	}

	public @NotNull List<DefinedRegion> getApplicableRegions(final @NotNull Location location) {
		return RegionManager.regions.stream().filter(r -> r.inRegion(location)).collect(Collectors.toList());
	}

	public @Nullable DefinedRegion getRegion(final @NotNull String name) {
		Optional<DefinedRegion> temp = RegionManager.regions.stream().filter(r -> r.getName().equals(name)).findFirst();
		return temp.orElse(null);
	}

	public void addRegion(final @NotNull DefinedRegion region) {
		RegionManager.regions.add(region);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean hasRegion(final @NotNull String name) {
		return RegionManager.getRegion(name) != null;
	}

	public boolean hasGlobalRegion(final @NotNull String name) {
		return RegionManager.getGlobalRegions().containsKey(name);
	}

	public boolean removeRegion(final @NotNull DefinedRegion region) {
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
		for (final @NotNull DefinedRegion region : RegionManager.getRegions()) {
			region.saveData();
		}

		for (final @NotNull Map.Entry<String, GlobalRegion> globalRegion : RegionManager.getGlobalRegions().entrySet()) {
			globalRegion.getValue().saveData();
		}
	}
}