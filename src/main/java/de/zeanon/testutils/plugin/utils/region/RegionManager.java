package de.zeanon.testutils.plugin.utils.region;

import de.zeanon.storagemanagercore.external.browniescollections.GapList;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class RegionManager {

	@Getter
	private final List<Region> regions = new GapList<>();

	public void initialize() throws IOException {
		RegionManager.regions.clear();
		BaseFileUtils.createFolder(TestUtils.getInstance().getDataFolder(), "Regions");
		for (final @NotNull File file : BaseFileUtils.listFilesOfType(new File(TestUtils.getInstance().getDataFolder(), "Regions"), "json")) {
			RegionManager.regions.add(new Region(file));
		}
	}

	public @NotNull List<Region> getApplicableRegions(final @NotNull Location location) {
		return RegionManager.regions.stream().filter(r -> r.inRegion(location)).collect(Collectors.toList());
	}

	public @Nullable Region getRegion(final @NotNull String name) {
		Optional<Region> temp = RegionManager.regions.stream().filter(r -> r.getName().equals(name)).findFirst();
		return temp.orElse(null);
	}

	public @Nullable Region getRegion(final @NotNull String name, final @NotNull World world) {
		Optional<Region> temp = RegionManager.regions.stream().filter(r -> r.getName().equals(name) && r.getWorld().equals(world)).findFirst();
		return temp.orElse(null);
	}

	public void addRegion(final @NotNull Region region) {
		if (!RegionManager.regions.contains(region)) {
			RegionManager.regions.add(region);
		}
	}

	public boolean hasRegion(final @NotNull String name) {
		return RegionManager.getRegion(name) != null;
	}

	public boolean hasRegion(final @NotNull String name, final @NotNull World world) {
		return RegionManager.getRegion(name, world) != null;
	}

	public void removeRegion(final @NotNull Region region) {
		region.deleteRegion();
		RegionManager.regions.remove(region);
	}
}