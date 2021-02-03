package de.zeanon.testutils.plugin.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanager.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.init.InitMode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
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

		Objects.requireNonNull(InitMode.getRegionContainer().get(world)).addRegion(regionSouth);
		Objects.requireNonNull(InitMode.getRegionContainer().get(world)).addRegion(regionNorth);

		regionSouth.setFlag(Flags.ITEM_DROP, StateFlag.State.DENY);
		regionNorth.setFlag(Flags.ITEM_DROP, StateFlag.State.DENY);
		regionSouth.setFlag(Flags.TNT, StateFlag.State.DENY);
		regionNorth.setFlag(Flags.TNT, StateFlag.State.DENY);
	}

	public void registerBlock(final @NotNull Player p, final @Nullable String name) {
		final ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
		if (tempRegion == null || tempRegion.getId().endsWith("_south")) {
			p.sendMessage(ChatColor.RED + "You are in no suitable region.");
			p.sendMessage(ChatColor.RED + "Please move to the north region of a tg.");
			return;
		}
		World tempWorld = new BukkitWorld(p.getWorld());
		CuboidRegion region = new CuboidRegion(tempWorld, tempRegion.getMinimumPoint(), tempRegion.getMaximumPoint());
		BlockArrayClipboard clipboard = new BlockArrayClipboard(region);


		try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(tempWorld, -1)) {
			ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
					editSession, region, region.getMinimumPoint(), clipboard, region.getMinimumPoint()
			);

			// configure here
			Operations.complete(forwardExtentCopy);

			if (name != null) {
				BaseFileUtils.createFile(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString() + "/" + name + ".schem");
			} else {
				BaseFileUtils.createFile(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString() + "/default.schem");
			}
			File tempFile = name != null ? new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString() + "/" + name + ".schem") //NOSONAR
										 : new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString() + "/default.schem");

			try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(tempFile))) {
				writer.write(clipboard);
			}
		} catch (WorldEditException | IOException e) {
			e.printStackTrace();
		}
		p.sendMessage(ChatColor.RED + "You registered a new block with the name: " + (name == null ? "default" : name));
	}

	public @Nullable ProtectedRegion getRegion(final @NotNull Player p) {
		for (ProtectedRegion temp : Objects.requireNonNull(InitMode.getRegionContainer()
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
}
