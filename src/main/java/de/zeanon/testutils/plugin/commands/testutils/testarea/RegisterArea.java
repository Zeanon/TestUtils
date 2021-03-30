package de.zeanon.testutils.plugin.commands.testutils.testarea;

import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.region.DefinedRegion;
import de.zeanon.testutils.plugin.utils.region.RegionManager;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
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

	private void generate(final @NotNull World world, final int x, final int y, final int z, final @NotNull String name) {
		DefinedRegion regionSouth = new DefinedRegion(name + "_south",
													  new DefinedRegion.Point(x - 58, y, z + 1),
													  new DefinedRegion.Point(x + 58, y + 65, z + 97),
													  world);


		DefinedRegion regionNorth = new DefinedRegion(name + "_north",
													  new DefinedRegion.Point(x - 58, y, z),
													  new DefinedRegion.Point(x + 58, y + 65, z - 96),
													  world);


		RegionManager.addRegion(regionSouth);
		RegionManager.addRegion(regionNorth);

		regionSouth.setItemDrops(false);
		regionNorth.setItemDrops(false);
		regionSouth.setFire(false);
		regionNorth.setFire(false);
		regionSouth.setTnt(false);
		regionNorth.setTnt(false);
	}
}