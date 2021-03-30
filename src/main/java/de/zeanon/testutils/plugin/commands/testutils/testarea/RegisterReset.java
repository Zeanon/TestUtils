package de.zeanon.testutils.plugin.commands.testutils.testarea;

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
public class RegisterReset {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length == 1) {
			final @Nullable Region tempRegion = TestAreaUtils.getRegion(p);
			final @Nullable Region oppositeRegion = TestAreaUtils.getOppositeRegion(p);

			if (tempRegion == null || oppositeRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
			} else {
				try {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "Registering reset for '"
								  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6)
								  + ChatColor.RED + "'...");
					RegisterReset.registerSide(p, tempRegion);
					RegisterReset.registerSide(p, oppositeRegion);

					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "You registered a new reset for '"
								  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
				} catch (WorldEditException | IOException e) {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "There has been an error, registering a new reset for '"
								  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
					e.printStackTrace();
				}
			}
		} else {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Too many arguments.");
		}
	}

	private void registerSide(final @NotNull Player p, final @NotNull Region tempRegion) throws WorldEditException, IOException {
		final @NotNull World tempWorld = new BukkitWorld(p.getWorld());
		final @NotNull CuboidRegion region = new CuboidRegion(tempWorld, tempRegion.getMinimumPoint().toBlockVector3(), tempRegion.getMaximumPoint().toBlockVector3());
		final @NotNull BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

		final @NotNull BlockVector3 copyPoint = region.getMinimumPoint();

		try (final @NotNull EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(tempWorld, -1)) {
			final @NotNull ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
					editSession, region, clipboard, copyPoint
			);

			forwardExtentCopy.setCopyingEntities(false);
			forwardExtentCopy.setCopyingBiomes(false);

			Operations.complete(forwardExtentCopy);

			final @NotNull File tempFile = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/TestAreas/" + tempWorld.getName() + "/" + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + "/" + tempRegion.getName().substring(tempRegion.getName().length() - 5) + ".schem");

			BaseFileUtils.createFile(tempFile);

			try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(tempFile))) {
				writer.write(clipboard);
			}
		}
	}
}
