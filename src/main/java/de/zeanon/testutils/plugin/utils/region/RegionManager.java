package de.zeanon.testutils.plugin.utils.region;

import de.zeanon.jsonfilemanager.JsonFileManager;
import de.zeanon.jsonfilemanager.internal.files.raw.JsonFile;
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
	private final List<DefinedRegion> regions = new GapList<>();
	private final @NotNull Map<String, GlobalRegion> globalRegions = new HashMap<>();

	public void initialize() throws IOException {
		RegionManager.regions.clear();
		BaseFileUtils.createFolder(TestUtils.getInstance().getDataFolder(), "Regions");
		for (final @NotNull File file : BaseFileUtils.listFilesOfType(new File(TestUtils.getInstance().getDataFolder(), "Regions"), "json")) {
			if (file.getName().startsWith("__global__")) {
				RegionManager.globalRegions.put(file.getName().substring(10, file.getName().length() - 5), new GlobalRegion(file));
			} else {
				RegionManager.regions.add(new DefinedRegion(file));
			}
		}

		for (final @NotNull World world : Bukkit.getWorlds()) {
			final @Nullable GlobalRegion globalRegion = RegionManager.globalRegions.get(world.getName());
			if (globalRegion == null) {
				RegionManager.globalRegions.put(world.getName(), new GlobalRegion(world.getName()));
			}
		}
	}

	public @NotNull GlobalRegion getGlobalRegion(final @NotNull World world) {
		final @Nullable GlobalRegion globalRegion = RegionManager.globalRegions.get(world.getName());
		if (globalRegion == null) {
			try {
				RegionManager.initialize();
			} catch (IOException e) {
				e.printStackTrace();
			}
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

	public @Nullable DefinedRegion getRegion(final @NotNull String name, final @NotNull World world) {
		Optional<DefinedRegion> temp = RegionManager.regions.stream().filter(r -> r.getName().equals(name) && r.getWorld().equals(world)).findFirst();
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

	public boolean hasRegion(final @NotNull String name, final @NotNull World world) {
		return RegionManager.getRegion(name, world) != null;
	}

	public void removeRegion(final @NotNull DefinedRegion region) {
		region.deleteRegion();
		RegionManager.regions.remove(region);
	}


	private static class GlobalRegion implements Region {

		private final @NotNull JsonFile jsonFile;

		private GlobalRegion(final @NotNull String world) {
			this.jsonFile = JsonFileManager.jsonFile(TestUtils.getInstance().getDataFolder(), "Regions/__global__" + world)
										   .concurrentData(true)
										   .fromResource("resources/global.json")
										   .create();
			this.jsonFile.set("world", world);
		}

		private GlobalRegion(final @NotNull File file) {
			this.jsonFile = JsonFileManager.jsonFile(file)
										   .concurrentData(true)
										   .create();
		}

		@Override
		public void setTnt(final boolean tnt) {
			this.jsonFile.setUseArray(new String[]{"tnt"}, tnt);
		}

		@Override
		public void setStoplag(final boolean stoplag) {
			this.jsonFile.set("stoplag", stoplag);
		}

		@Override
		public void setFire(final boolean fire) {
			this.jsonFile.set("fire", fire);
		}

		@Override
		public void setItemDrops(final boolean itemDrops) {
			this.jsonFile.set("itemdrops", itemDrops);
		}

		@Override
		public World getWorld() {
			return Bukkit.getWorld(Objects.notNull(this.jsonFile.getStringUseArray("world")));
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

		@Override
		public boolean itemDrops() {
			return this.jsonFile.getBooleanUseArray("itemdrops");
		}
	}
}