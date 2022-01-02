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
import de.zeanon.testutils.plugin.utils.enums.MappedFile;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Register {

	public void execute(final @Nullable MappedFile mappedFile, final @NotNull Player p) {
		if (mappedFile != null && TestAreaUtils.illegalName(mappedFile.getName())) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Block '" + mappedFile.getName() + "' resolution error: Name is not allowed.");
			return;
		}

		final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p);

		if (tempRegion == null) {
			GlobalMessageUtils.sendNotApplicableRegion(p);
			return;
		}

		p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
					  + ChatColor.RED + "Registering new testblock as '"
					  + ChatColor.DARK_RED + (mappedFile == null ? "default" : mappedFile)
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

			final @NotNull Path tempFile = mappedFile != null ? TestBlockCommand.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString()).resolve(mappedFile + ".schem")
															  : TestBlockCommand.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString()).resolve("default.schem");

			BaseFileUtils.createFile(tempFile);

			try (final ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(BaseFileUtils.createNewOutputStreamFromFile(tempFile))) {
				writer.write(clipboard);
			}

			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "You registered a new testblock with the name '"
						  + ChatColor.DARK_RED + (mappedFile == null ? "default" : mappedFile) + ChatColor.RED + "'.");
		} catch (final WorldEditException | IOException e) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "There has been an error, registering a new testblock with the name '"
						  + ChatColor.DARK_RED + (mappedFile == null ? "default" : mappedFile) + ChatColor.RED + "'.");
			TestUtils.getChatLogger().log(Level.SEVERE, "Error while registering " + (mappedFile == null ? "default" : mappedFile) + " as a testblock", e);
		}
	}

	public @NotNull String usageMessage() {
		return ChatColor.GRAY + "/tb"
			   + ChatColor.AQUA + " register "
			   + ChatColor.YELLOW + "<"
			   + ChatColor.DARK_RED + "filename"
			   + ChatColor.YELLOW + ">";
	}

	public @NotNull String usageHoverMessage() {
		return ChatColor.RED + "e.g. "
			   + ChatColor.GRAY + "/tb"
			   + ChatColor.AQUA + " register "
			   + ChatColor.DARK_RED + "example";
	}

	public @NotNull String usageCommand() {
		return "/tb register ";
	}

	public void usage(final @NotNull Player p) {
		GlobalMessageUtils.sendSuggestMessage(GlobalMessageUtils.MESSAGE_HEAD
											  + ChatColor.RED + "Usage: ",
											  Register.usageMessage(),
											  Register.usageHoverMessage(),
											  Register.usageCommand(), p);
	}
}