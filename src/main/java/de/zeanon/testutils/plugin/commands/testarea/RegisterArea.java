package de.zeanon.testutils.plugin.commands.testarea;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.region.Region;
import de.zeanon.testutils.plugin.utils.region.RegionManager;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class RegisterArea {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length < 3) {
			final @NotNull String name = args.length > 1 ? args[1] : p.getName();
			RegisterArea.generate(p.getWorld(),
								  p.getLocation().getBlockX(),
								  p.getLocation().getBlockY(),
								  p.getLocation().getBlockZ(),
								  name);
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "You created a testarea with the name '" + ChatColor.DARK_RED + name + ChatColor.RED + "'.");
			RegisterReset.execute(new String[]{"registerreset"}, p);
		} else {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Too many arguments.");
		}
	}

	private void generate(final @NotNull World world, final double x, final double y, final double z, final @NotNull String name) {
		Region regionSouth = new Region(name + "_south",
										new Location(world, x - 58, y, z + 1),
										new Location(world, x + 58, y + 65, z + 97));


		Region regionNorth = new Region(name + "_north",
										new Location(world, x - 58, y, z),
										new Location(world, x + 58, y + 65, z - 96));


		RegionManager.addRegion(regionSouth);
		RegionManager.addRegion(regionNorth);

		regionSouth.setItemDrops(false);
		regionNorth.setItemDrops(false);
		regionSouth.setTnt(false);
		regionNorth.setTnt(false);

		ProtectedRegion wgregion = new ProtectedCuboidRegion("testarea_" + name + "_outside",
															 BlockVector3.at(x - 58, y, z - 96),
															 BlockVector3.at(x + 58, y + 65, z + 97));

		Objects.notNull(InitMode.getRegionContainer().get(new BukkitWorld(world))).addRegion(wgregion);

		wgregion.setFlag(Flags.TNT, StateFlag.State.ALLOW);
	}
}