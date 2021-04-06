package de.zeanon.testutils.plugin.commands.testutils.testarea;

import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.enums.AreaName;
import de.zeanon.testutils.regionsystem.flags.Flag;
import de.zeanon.testutils.regionsystem.flags.flagvalues.*;
import de.zeanon.testutils.regionsystem.region.Region;
import de.zeanon.testutils.regionsystem.region.RegionManager;
import de.zeanon.testutils.regionsystem.region.TestArea;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class RegisterArea {

	public void execute(final @Nullable AreaName name, final @NotNull Player p) {
		if (name != null && (name.getName().contains("./") || name.getName().contains(".\\") || InitMode.forbiddenFileName(name.getName()))) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Area '" + name.getName() + "' resolution error: Name is not allowed.");
			return;
		}

		final @NotNull String regionName = name == null ? p.getName() : name.getName();
		RegisterArea.generate(p.getWorld(),
							  p.getLocation().getBlockX(),
							  p.getLocation().getBlockY(),
							  p.getLocation().getBlockZ(),
							  regionName);
		p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
					  + ChatColor.RED + "You created a testarea with the name '" + ChatColor.DARK_RED + regionName + ChatColor.RED + "'.");
		RegisterReset.execute(p);
	}

	private void generate(final @NotNull World world, final int x, final int y, final int z, final @NotNull String name) {
		TestArea regionSouth = new TestArea(name + "_south",
											new Region.Point(x - 58, y, z + 1),
											new Region.Point(x + 58, y + 65, z + 97),
											world);


		TestArea regionNorth = new TestArea(name + "_north",
											new Region.Point(x - 58, y, z),
											new Region.Point(x + 58, y + 65, z - 96),
											world);

		RegionManager.addRegion(regionSouth);
		RegionManager.addRegion(regionNorth);

		regionSouth.set(Flag.LEAVES_DECAY, LEAVES_DECAY.DENY);
		regionNorth.set(Flag.LEAVES_DECAY, LEAVES_DECAY.DENY);
		regionSouth.set(Flag.FALL_DAMAGE, FALL_DAMAGE.DENY);
		regionNorth.set(Flag.FALL_DAMAGE, FALL_DAMAGE.DENY);
		regionSouth.set(Flag.ITEM_DROPS, ITEM_DROPS.DENY);
		regionNorth.set(Flag.ITEM_DROPS, ITEM_DROPS.DENY);
		regionSouth.set(Flag.FIRE, FIRE.DENY);
		regionNorth.set(Flag.FIRE, FIRE.DENY);
		regionSouth.set(Flag.TNT, TNT.DENY);
		regionNorth.set(Flag.TNT, TNT.DENY);
	}
}