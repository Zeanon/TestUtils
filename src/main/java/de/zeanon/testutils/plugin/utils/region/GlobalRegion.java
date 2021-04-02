package de.zeanon.testutils.plugin.utils.region;

import de.zeanon.jsonfilemanager.JsonFileManager;
import de.zeanon.testutils.TestUtils;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings("unused")
public class GlobalRegion extends Region {

	public GlobalRegion(final @NotNull World world) {
		super(JsonFileManager.jsonFile(TestUtils.getInstance().getDataFolder(), "Regions/__" + world.getName() + "__")
							 .fromResource("resources/global.json")
							 .create(),
			  "__" + world + "__", world);
	}

	@Override
	public @NotNull String getType() {
		return "global";
	}
}