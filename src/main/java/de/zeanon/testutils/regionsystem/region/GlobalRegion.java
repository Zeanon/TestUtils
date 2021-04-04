package de.zeanon.testutils.regionsystem.region;

import de.zeanon.jsonfilemanager.JsonFileManager;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings("unused")
public class GlobalRegion extends Region {

	public GlobalRegion(final @NotNull World world) {
		super(JsonFileManager.jsonFile(RegionManager.GLOBAL_REGIONS_FOLDER.resolve("__" + world.getName() + "__.json"))
							 .fromResource("resources/global.json")
							 .create(),
			  "__" + world.getName() + "__", world);
	}

	@Override
	public @NotNull String getType() {
		return "GlobalRegion";
	}
}