package de.zeanon.testutils.plugin.commands.testblock;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.MappedFile;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Load {

	public void execute(final @NotNull Player p, final @Nullable MappedFile mappedFile, final @Nullable DefinedRegion tempRegion, final @NotNull String area) {
		if (mappedFile != null && TestAreaUtils.illegalName(mappedFile.getName())) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Block '" + mappedFile.getName() + "' resolution error: Name is not allowed.");
			return;
		}

		if (tempRegion == null) {
			GlobalMessageUtils.sendNotApplicableRegion(p);
			return;
		}

		final @Nullable Pair<String, InputStream> testBlock = TestBlockCommand.getBlock(p, mappedFile);
		if (testBlock != null) { //NOSONAR
			try (final @NotNull ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(testBlock.getValue())) {
				final @NotNull Clipboard clipboard = reader.read();
				try (final @NotNull EditSession editSession = SessionFactory.createSession(p, new BukkitWorld(p.getWorld()))) {

					final @NotNull BlockVector3 pastePoint;

					final @NotNull ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);

					if (tempRegion.getName().endsWith("_south")) {
						pastePoint = BlockVector3.at(tempRegion.getMaximumPoint().getX(), tempRegion.getMinimumPoint().getY(), tempRegion.getMaximumPoint().getZ());
						clipboardHolder.setTransform(new AffineTransform().rotateY(180));
					} else {
						pastePoint = tempRegion.getMinimumPoint().toBlockVector3();
					}

					final Operation operation = clipboardHolder
							.createPaste(editSession)
							.to(pastePoint)
							.ignoreAirBlocks(true)
							.build();

					Operations.complete(operation);
					p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
								  + ChatColor.RED + "Testblock '" + ChatColor.DARK_RED + testBlock.getKey() + ChatColor.RED + "' has been set on " + area + " side.");
				}
			} catch (final IOException | WorldEditException e) {
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "There has been an error pasting '" + ChatColor.DARK_RED + testBlock.getKey() + ChatColor.RED + "' on " + area + " side.");
				TestUtils.getChatLogger().log(Level.SEVERE, String.format("Error while pasting %s on %s", testBlock.getKey(), area), e);
			}
		}
	}

	public @NotNull String usageMessage() {
		return ChatColor.GRAY + "/tb"
			   + ChatColor.AQUA + " load "
			   + ChatColor.YELLOW + "<"
			   + ChatColor.DARK_RED + "filename"
			   + ChatColor.YELLOW + ">";
	}

	public @NotNull String usageHoverMessage() {
		return ChatColor.RED + "e.g. "
			   + ChatColor.GRAY + "/tb"
			   + ChatColor.AQUA + " load "
			   + ChatColor.DARK_RED + "example";
	}

	public @NotNull String usageCommand() {
		return "/tb load ";
	}

	public void usage(final @NotNull Player p) {
		GlobalMessageUtils.sendSuggestMessage(GlobalMessageUtils.MESSAGE_HEAD
											  + ChatColor.RED + "Usage: ",
											  Load.usageMessage(),
											  Load.usageHoverMessage(),
											  Load.usageCommand(), p);
	}
}