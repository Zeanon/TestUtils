package de.zeanon.testutils.plugin.utils.region;

import de.zeanon.storagemanagercore.external.browniescollections.GapList;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

	@Getter
	private final @NotNull List<DefinedRegion> regions = new GapList<>();
	@Getter
	private final @NotNull Map<String, GlobalRegion> globalRegions = new HashMap<>();

	public void initialize() throws IOException {
		BaseFileUtils.createFolder(TestUtils.getInstance().getDataFolder(), "Regions");

		RegionManager.initializeDefinedRegions();
		RegionManager.initializeGlobalRegions();
	}

	public void initializeDefinedRegions() throws IOException {
		RegionManager.regions.clear();
		for (final @NotNull File file : BaseFileUtils.listFilesOfType(new File(TestUtils.getInstance().getDataFolder(), "Regions"), "json")) {
			if (!file.getName().startsWith("__") || !file.getName().endsWith("__")) {
				RegionManager.regions.add(new DefinedRegion(file));
			}
		}
	}

	public void initializeGlobalRegions() {
		RegionManager.globalRegions.clear();
		for (final @NotNull World world : Bukkit.getWorlds()) {
			RegionManager.globalRegions.put(world.getName(), new GlobalRegion(world));
		}
	}

	public @NotNull GlobalRegion getGlobalRegion(final @NotNull World world) {
		final @Nullable GlobalRegion globalRegion = RegionManager.globalRegions.get(world.getName());
		if (globalRegion == null) {
			RegionManager.initializeGlobalRegions();
			return Objects.notNull(RegionManager.globalRegions.get(world.getName()));
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
		if (!RegionManager.regions.contains(region)) {
			RegionManager.regions.add(region);
		}
	}

	public boolean hasRegion(final @NotNull String name) {
		return RegionManager.getRegion(name) != null;
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