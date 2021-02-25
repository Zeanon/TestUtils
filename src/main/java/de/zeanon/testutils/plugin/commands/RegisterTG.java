package de.zeanon.testutils.plugin.commands;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.init.InitMode;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class RegisterTG {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		final @NotNull String name = args.length > 1 ? args[1] : p.getName();
		RegisterTG.generate(new BukkitWorld(p.getWorld()),
							p.getLocation().getBlockX(),
							p.getLocation().getBlockY(),
							p.getLocation().getBlockZ(),
							name);
		p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + de.zeanon.testutils.TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
					  ChatColor.RED + "You created a testarea with the name '" + ChatColor.DARK_RED + "testarea_" + name + "" + ChatColor.RED + "'");
	}

	private void generate(final @NotNull World world, final double x, final double y, final double z, final @NotNull String name) {
		ProtectedRegion regionSouth = new ProtectedCuboidRegion("testarea_" + name + "_south",
																BlockVector3.at(x - 58, y, z + 1),
																BlockVector3.at(x + 58, y + 65, z + 97));

		ProtectedRegion regionNorth = new ProtectedCuboidRegion("testarea_" + name + "_north",
																BlockVector3.at(x - 58, y, z),
																BlockVector3.at(x + 58, y + 65, z - 96));

		Objects.notNull(InitMode.getRegionContainer().get(world)).addRegion(regionSouth);
		Objects.notNull(InitMode.getRegionContainer().get(world)).addRegion(regionNorth);

		regionSouth.setFlag(Flags.ITEM_DROP, StateFlag.State.DENY);
		regionNorth.setFlag(Flags.ITEM_DROP, StateFlag.State.DENY);
		regionSouth.setFlag(Flags.TNT, StateFlag.State.DENY);
		regionNorth.setFlag(Flags.TNT, StateFlag.State.DENY);
	}
}
