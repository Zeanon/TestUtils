package de.zeanon.testutils.plugin.utils;

import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import de.zeanon.testutils.regionsystem.RegionManager;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.util.*;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class TestAreaUtils {

	final @NotNull Set<String> forbiddenNames = new HashSet<>(Arrays.asList("-here", "-other", "-north", "-n", "-south", "-s", "-manual", "-hourly", "-daily", "-startup"));

	public boolean forbiddenFileName(final @NotNull String name) {
		return TestAreaUtils.forbiddenNames.stream().anyMatch(name::equalsIgnoreCase);
	}

	public boolean illegalName(final @NotNull String name) {
		return name.contains("./") || name.contains(".\\") || name.contains("§") || TestAreaUtils.forbiddenFileName(name);
	}

	public @Nullable DefinedRegion getRegion(final @NotNull Player p) {
		final @NotNull List<DefinedRegion> regions = RegionManager.getApplicableRegions(p.getLocation());
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
		final @NotNull Optional<DefinedRegion> optionalRegion = RegionManager.getApplicableRegions(p.getLocation()).stream().findFirst();
		return optionalRegion.map(region -> RegionManager.getRegion(region
																			.getName()
																			.substring(0, region.getName().length() - 6)
																	+ (region.getName()
																			 .substring(region.getName().length() - 6)
																			 .equalsIgnoreCase("_north") ? "_south" : "_north")))
							 .orElse(null);
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
		final @NotNull Optional<DefinedRegion> temp = RegionManager.getApplicableRegions(p.getLocation()).stream().findFirst();
		if (temp.isPresent()) {
			if (temp.get().getName().substring(temp.get().getName().length() - 6).equalsIgnoreCase("_north")) {
				return temp.get();
			} else {
				return RegionManager.getRegion(temp.get().getName().substring(0, temp.get().getName().length() - 6) + "_north");
			}
		} else {
			return null;
		}
	}

	public @Nullable DefinedRegion getSouthRegion(final @NotNull Player p) {
		final @NotNull Optional<DefinedRegion> temp = RegionManager.getApplicableRegions(p.getLocation()).stream().findFirst();
		if (temp.isPresent()) {
			if (temp.get().getName().substring(temp.get().getName().length() - 6).equalsIgnoreCase("_south")) {
				return temp.get();
			} else {
				return RegionManager.getRegion(temp.get().getName().substring(0, temp.get().getName().length() - 6) + "_south");
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