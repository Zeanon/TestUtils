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
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.region.Region;
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

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length == 1) {
			RegisterBlock.registerBlock(p, null);
		} else if (args.length == 2) {
			RegisterBlock.registerBlock(p, args[1]);
		} else {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Too many arguments.");
		}
	}

	private void registerBlock(final @NotNull Player p, final @Nullable String name) {
		if (name != null && name.contains("./")) {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "File '" + ChatColor.DARK_RED + name + ChatColor.RED + "' resolution error: Path is not allowed.");
		} else if (name != null && (name.equalsIgnoreCase("-here")
									|| name.equalsIgnoreCase("-north")
									|| name.equalsIgnoreCase("-n")
									|| name.equalsIgnoreCase("-south")
									|| name.equalsIgnoreCase("-s"))) {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "'"
						  + ChatColor.DARK_RED + name + ChatColor.RED
						  + "' is not allowed due to '"
						  + ChatColor.DARK_RED + name + ChatColor.RED
						  + "' being a sub-command of /testblock.");
		} else {
			final @Nullable Region tempRegion = TestAreaUtils.getRegion(p);

			if (tempRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
			} else {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Registering new testblock as '"
							  + ChatColor.DARK_RED + (name == null ? "default" : name)
							  + ChatColor.RED + "'...");
				final @NotNull World tempWorld = new BukkitWorld(p.getWorld());
				final @NotNull CuboidRegion region = new CuboidRegion(tempWorld, tempRegion.getMinimumPoint().toBlockVector3(), tempRegion.getMaximumPoint().toBlockVector3());
				final @NotNull BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

				final @NotNull BlockVector3 copyPoint;

				if (tempRegion.getName().endsWith("_south")) {
					copyPoint = BlockVector3.at(region.getMaximumPoint().getBlockX(), region.getMinimumPoint().getBlockY(), region.getMaximumPoint().getBlockZ());
				} else {
					copyPoint = region.getMinimumPoint();
				}

				try (final @NotNull EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(tempWorld, -1)) {
					final @NotNull ForwardExtentCopy copy = new ForwardExtentCopy(
							editSession, region, clipboard, copyPoint
					);

					copy.setCopyingEntities(false);
					copy.setCopyingBiomes(false);

					if (tempRegion.getName().endsWith("_south")) {
						copy.setTransform(new AffineTransform().rotateY(180));
					}

					Operations.complete(copy);

					File tempFile = name != null ? new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/TestBlocks/" + p.getUniqueId().toString() + "/" + name + ".schem")
												 : new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/TestBlocks/" + p.getUniqueId().toString() + "/default.schem");

					BaseFileUtils.createFile(tempFile);

					try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(tempFile))) {
						writer.write(clipboard);
					}

					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "You registered a new testblock with the name '"
								  + ChatColor.DARK_RED + (name == null ? "default" : name) + ChatColor.RED + "'.");
				} catch (WorldEditException | IOException e) {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "There has been an error, registering a new testblock with the name '"
								  + ChatColor.DARK_RED + (name == null ? "default" : name) + ChatColor.RED + "'.");
					e.printStackTrace();
				}
			}
		}
	}
}
