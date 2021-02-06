package de.zeanon.testutils.plugin.utils;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.testutils.init.InitMode;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class TestAreaUtils {

	public void generate(final @NotNull World world, final double x, final double y, final double z, final @NotNull String name) {
		ProtectedRegion regionSouth = new ProtectedCuboidRegion("testarea_" + name + "_south",
																BlockVector3.at(x - 58, y, z + 1),
																BlockVector3.at(x + 58, y + 66, z + 98));

		ProtectedRegion regionNorth = new ProtectedCuboidRegion("testarea_" + name + "_north",
																BlockVector3.at(x - 58, y, z - 1),
																BlockVector3.at(x + 58, y + 66, z - 98));

		Objects.notNull(InitMode.getRegionContainer().get(world)).addRegion(regionSouth);
		Objects.notNull(InitMode.getRegionContainer().get(world)).addRegion(regionNorth);

		regionSouth.setFlag(Flags.ITEM_DROP, StateFlag.State.DENY);
		regionNorth.setFlag(Flags.ITEM_DROP, StateFlag.State.DENY);
		regionSouth.setFlag(Flags.TNT, StateFlag.State.DENY);
		regionNorth.setFlag(Flags.TNT, StateFlag.State.DENY);
	}

	public @Nullable ProtectedRegion getRegion(final @NotNull Player p) {
		for (ProtectedRegion temp : Objects.notNull(InitMode.getRegionContainer()
															.get(new BukkitWorld(p.getWorld())))
										   .getApplicableRegions(BlockVector3.at(p.getLocation().getX(),
																				 p.getLocation().getY(),
																				 p.getLocation().getZ()))) {
			if (temp.getId().startsWith("testarea_")) {
				return temp;
			}
		}
		return null;
	}

	public @Nullable ProtectedRegion getOppositeRegion(final @NotNull Player p) {
		RegionManager tempManager = Objects.notNull(InitMode.getRegionContainer()
															.get(new BukkitWorld(p.getWorld())));
		for (ProtectedRegion temp : tempManager.getApplicableRegions(BlockVector3.at(p.getLocation().getX(),
																					 p.getLocation().getY(),
																					 p.getLocation().getZ()))) {
			if (temp.getId().startsWith("testarea_")) {
				return tempManager.getRegion(temp.getId().substring(0, temp.getId().length() - 6) + (temp.getId().substring(temp.getId().length() - 6).equalsIgnoreCase("_north") ? "_south" : "_north"));
			}
		}
		return null;
	}
}
