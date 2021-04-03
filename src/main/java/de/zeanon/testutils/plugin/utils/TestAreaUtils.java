package de.zeanon.testutils.plugin.utils;

import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import de.zeanon.testutils.regionsystem.region.RegionManager;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class TestAreaUtils {

	public @Nullable DefinedRegion getRegion(final @NotNull Player p) {
		List<DefinedRegion> regions = de.zeanon.testutils.regionsystem.region.RegionManager.getApplicableRegions(p.getLocation());
		if (regions.isEmpty()) {
			return null;
		} else {
			return regions.get(0);
		}
	}

	public @Nullable DefinedRegion getRegion(final @NotNull Player p, final @Nullable RegionSide regionSide) {
		if (regionSide == null) {
			return TestAreaUtils.getRegion(p);
		} else {
			switch (regionSide) {
				case NORTH:
					return TestAreaUtils.getNorthRegion(p);
				case SOUTH:
					return TestAreaUtils.getSouthRegion(p);
				case HERE:
					return TestAreaUtils.getRegion(p);
				case OTHER:
					return TestAreaUtils.getOppositeRegion(p);
				default:
					return null;
			}
		}
	}

	public @Nullable DefinedRegion getOppositeRegion(final @NotNull Player p) {
		final Optional<DefinedRegion> temp = de.zeanon.testutils.regionsystem.region.RegionManager.getApplicableRegions(p.getLocation()).stream().findFirst();
		return temp.map(region -> de.zeanon.testutils.regionsystem.region.RegionManager.getRegion(region.getName().substring(0, region.getName().length() - 5) + (region.getName().substring(region.getName().length() - 5).equalsIgnoreCase("north") ? "south" : "north"))).orElse(null);
	}

	public @Nullable DefinedRegion getOppositeRegion(final @NotNull Player p, final @Nullable RegionSide regionSide) {
		if (regionSide == null) {
			return TestAreaUtils.getOppositeRegion(p);
		} else {
			switch (regionSide) {
				case NORTH:
					return TestAreaUtils.getSouthRegion(p);
				case SOUTH:
					return TestAreaUtils.getNorthRegion(p);
				case HERE:
					return TestAreaUtils.getOppositeRegion(p);
				case OTHER:
					return TestAreaUtils.getRegion(p);
				default:
					return null;
			}
		}
	}

	public @Nullable DefinedRegion getNorthRegion(final @NotNull Player p) {
		final Optional<DefinedRegion> temp = de.zeanon.testutils.regionsystem.region.RegionManager.getApplicableRegions(p.getLocation()).stream().findFirst();
		if (temp.isPresent()) {
			if (temp.get().getName().substring(temp.get().getName().length() - 5).equalsIgnoreCase("north")) {
				return temp.get();
			} else {
				return de.zeanon.testutils.regionsystem.region.RegionManager.getRegion(temp.get().getName().substring(0, temp.get().getName().length() - 5) + "north");
			}
		} else {
			return null;
		}
	}

	public @Nullable DefinedRegion getSouthRegion(final @NotNull Player p) {
		final Optional<DefinedRegion> temp = de.zeanon.testutils.regionsystem.region.RegionManager.getApplicableRegions(p.getLocation()).stream().findFirst();
		if (temp.isPresent()) {
			if (temp.get().getName().substring(temp.get().getName().length() - 5).equalsIgnoreCase("south")) {
				return temp.get();
			} else {
				return de.zeanon.testutils.regionsystem.region.RegionManager.getRegion(temp.get().getName().substring(0, temp.get().getName().length() - 5) + "south");
			}
		} else {
			return null;
		}
	}

	public @Nullable DefinedRegion getNorthRegion(final @NotNull String name) {
		return RegionManager.getRegion(name + "_north");
	}

	public @Nullable DefinedRegion getSouthRegion(final @NotNull String name) {
		return RegionManager.getRegion(name + "_south");
	}
}