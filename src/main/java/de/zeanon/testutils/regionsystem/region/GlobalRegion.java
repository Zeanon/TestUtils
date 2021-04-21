package de.zeanon.testutils.regionsystem.region;

import de.zeanon.jsonfilemanager.JsonFileManager;
import de.zeanon.testutils.regionsystem.RegionManager;
import de.zeanon.testutils.regionsystem.RegionType;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings("unused")
public class GlobalRegion extends Region {

	public GlobalRegion(final @NotNull World world) {
		super(JsonFileManager.jsonFile(RegionManager.GLOBAL_REGIONS_FOLDER.resolve("__" + world.getName() + "__.json"))
							 .fromResource("resources/global.json")
							 .create(),
			  "__" + world.getName() + "__",
			  world,
			  RegionType.GLOBAL_REGION);
	}


	@Override
	public boolean inRegion(final @NotNull Location location) {
		return this.getWorld().equals(location.getWorld());
	}
}