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
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.commands.testutils.TestUtilsCommand;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class RegisterReset {

	public void execute(final @NotNull Player p) {
		final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p);
		final @Nullable DefinedRegion oppositeRegion = TestAreaUtils.getOppositeRegion(p);

		if (tempRegion == null || oppositeRegion == null) {
			GlobalMessageUtils.sendNotApplicableRegion(p);
		} else {
			try {
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "Registering reset for '"
							  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6)
							  + ChatColor.RED + "'...");
				RegisterReset.registerSide(tempRegion);
				Bukkit.getScheduler().scheduleSyncDelayedTask(TestUtils.getInstance(), () -> {
				}, 5);
				RegisterReset.registerSide(oppositeRegion);

				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "You registered a new reset for '"
							  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
			} catch (final @NotNull WorldEditException | IOException e) {
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "There has been an error, registering a new reset for '"
							  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
				TestUtils.getChatLogger().log(Level.SEVERE, String.format("Error while registering reset for %s", tempRegion.getName().substring(0, tempRegion.getName().length() - 6)), e);
			}
		}
	}


	public void registerSide(final @NotNull DefinedRegion tempRegion) throws WorldEditException, IOException {
		final @NotNull BukkitWorld tempWorld = new BukkitWorld(tempRegion.getWorld());
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

			final @NotNull Path tempFile = TestUtilsCommand.TESTAREA_FOLDER.resolve(tempRegion.getName().substring(0, tempRegion.getName().length() - 6)).resolve(tempRegion.getName().substring(tempRegion.getName().length() - 5) + ".schem");

			BaseFileUtils.createFile(tempFile);

			try (final ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(BaseFileUtils.createNewOutputStreamFromFile(tempFile))) {
				writer.write(clipboard);
			}
		}
	}
}
