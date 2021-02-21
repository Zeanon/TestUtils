package de.zeanon.testutils.plugin.commands.testblock;

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
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class RegisterBlock {

	public void registerBlock(final @NotNull Player p, final @Nullable String name) {
		if (name != null && name.contains("./")) {
			p.sendMessage(GlobalMessageUtils.messageHead +
						  ChatColor.RED + "File '" + ChatColor.DARK_RED + name + ChatColor.RED + "'resolution error: Path is not allowed.");
		} else {
			final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);

			if (tempRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				return;
			}

			final @NotNull World tempWorld = new BukkitWorld(p.getWorld());
			final @NotNull CuboidRegion region = new CuboidRegion(tempWorld, tempRegion.getMinimumPoint(), tempRegion.getMaximumPoint());
			final @NotNull BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

			final @NotNull BlockVector3 copyPoint;

			if (tempRegion.getId().endsWith("_south")) {
				copyPoint = BlockVector3.at(region.getMaximumPoint().getBlockX(), region.getMinimumPoint().getBlockY(), region.getMaximumPoint().getBlockZ());
			} else {
				copyPoint = region.getMinimumPoint();
			}

			try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(tempWorld, -1)) {
				ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
						editSession, region, region.getMinimumPoint(), clipboard, copyPoint
				);

				forwardExtentCopy.setCopyingEntities(false);
				forwardExtentCopy.setCopyingBiomes(false);

				if (tempRegion.getId().endsWith("_south")) {
					forwardExtentCopy.setTransform(new AffineTransform().rotateY(180));
				}

				Operations.complete(forwardExtentCopy);

				File tempFile = name != null ? new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString() + "/" + name + ".schem") //NOSONAR
											 : new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString() + "/default.schem");

				BaseFileUtils.createFile(tempFile);

				try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(tempFile))) {
					writer.write(clipboard);
				}
			} catch (WorldEditException | IOException e) {
				e.printStackTrace();
			}
			p.sendMessage(GlobalMessageUtils.messageHead +
						  ChatColor.RED + "You registered a new block with the name: " + ChatColor.DARK_RED + (name == null ? "default" : name));
		}
	}
}