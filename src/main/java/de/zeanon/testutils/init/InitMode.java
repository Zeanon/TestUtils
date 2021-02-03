package de.zeanon.testutils.init;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.zeanon.testutils.TestUtils;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class InitMode {

	@Getter(onMethod_ = {@NotNull})
	private RegionContainer regionContainer;
	@Getter(onMethod_ = {@NotNull})
	private String worldEditPluginName;

	public void initPlugin() {
		InitMode.regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
		if (TestUtils.getPluginManager().getPlugin("FastAsyncWorldEdit") != null && TestUtils.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
			InitMode.worldEditPluginName = "FastAsyncWorldEdit";
		} else if (TestUtils.getPluginManager().getPlugin("WorldEdit") != null && TestUtils.getPluginManager().isPluginEnabled("WorldEdit")) {
			InitMode.worldEditPluginName = "WorldEdit";
		}
	}
}