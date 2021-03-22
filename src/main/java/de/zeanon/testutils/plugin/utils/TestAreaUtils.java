package de.zeanon.testutils.plugin.utils;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.utils.enums.PasteSide;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class TestAreaUtils {

	public @Nullable ProtectedRegion getRegion(final @NotNull Player p) {
		for (final @NotNull ProtectedRegion temp : Objects.notNull(InitMode.getRegionContainer()
																		   .get(new BukkitWorld(p.getWorld())))
														  .getApplicableRegions(BlockVector3.at(p.getLocation().getX(),
																								p.getLocation().getY(),
																								p.getLocation().getZ()))) {
			if (temp.getId().startsWith("testarea_") && (temp.getId().endsWith("_north") || temp.getId().endsWith("_south"))) {
				return temp;
			}
		}
		return null;
	}

	public @Nullable ProtectedRegion getRegion(final @NotNull Player p, final @NotNull PasteSide pasteSide) {
		switch (pasteSide) {
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

	public @Nullable ProtectedRegion getOppositeRegion(final @NotNull Player p) {
		final @NotNull RegionManager tempManager = Objects.notNull(InitMode.getRegionContainer()
																		   .get(new BukkitWorld(p.getWorld())));
		for (ProtectedRegion temp : tempManager.getApplicableRegions(BlockVector3.at(p.getLocation().getX(),
																					 p.getLocation().getY(),
																					 p.getLocation().getZ()))) {
			if (temp.getId().startsWith("testarea_") && (temp.getId().endsWith("_north") || temp.getId().endsWith("_south"))) {
				return tempManager.getRegion(temp.getId().substring(0, temp.getId().length() - 5) + (temp.getId().substring(temp.getId().length() - 5).equalsIgnoreCase("north") ? "south" : "north"));
			}
		}
		return null;
	}

	public @Nullable ProtectedRegion getOppositeRegion(final @NotNull Player p, final @NotNull PasteSide pasteSide) {
		switch (pasteSide) {
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

	public @Nullable ProtectedRegion getNorthRegion(final @NotNull Player p) {
		final @NotNull RegionManager tempManager = Objects.notNull(InitMode.getRegionContainer()
																		   .get(new BukkitWorld(p.getWorld())));
		for (ProtectedRegion temp : tempManager.getApplicableRegions(BlockVector3.at(p.getLocation().getX(),
																					 p.getLocation().getY(),
																					 p.getLocation().getZ()))) {
			if (temp.getId().startsWith("testarea_")) {
				if (temp.getId().endsWith("_north")) {
					return temp;
				} else if (temp.getId().endsWith("_south")) {
					return tempManager.getRegion(temp.getId().substring(0, temp.getId().length() - 5) + "north");
				}
			}
		}
		return null;
	}

	public @Nullable ProtectedRegion getSouthRegion(final @NotNull Player p) {
		final @NotNull RegionManager tempManager = Objects.notNull(InitMode.getRegionContainer()
																		   .get(new BukkitWorld(p.getWorld())));
		for (ProtectedRegion temp : tempManager.getApplicableRegions(BlockVector3.at(p.getLocation().getX(),
																					 p.getLocation().getY(),
																					 p.getLocation().getZ()))) {
			if (temp.getId().startsWith("testarea_")) {
				if (temp.getId().endsWith("_south")) {
					return temp;
				} else if (temp.getId().endsWith("_north")) {
					return tempManager.getRegion(temp.getId().substring(0, temp.getId().length() - 5) + "south");
				}
			}
		}
		return null;
	}
}
