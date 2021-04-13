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
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import de.zeanon.testutils.plugin.utils.enums.MappedFile;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.io.IOException;
import java.io.InputStream;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class PasteBlock {

	public void pasteBlock(final @NotNull Player p, final @Nullable MappedFile mappedFile, final @Nullable DefinedRegion tempRegion, final @NotNull String area) {
		if (mappedFile != null && (mappedFile.getName().contains("./") || mappedFile.getName().contains(".\\") || InitMode.forbiddenFileName(mappedFile.getName()))) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Block '" + mappedFile.getName() + "' resolution error: Name is not allowed.");
			return;
		}

		if (tempRegion == null) {
			GlobalMessageUtils.sendNotApplicableRegion(p);
			return;
		}

		final @Nullable Pair<String, InputStream> testBlock = TestBlock.getBlock(p, mappedFile);
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

					Operation operation = clipboardHolder
							.createPaste(editSession)
							.to(pastePoint)
							.ignoreAirBlocks(true)
							.build();

					Operations.complete(operation);
					p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
								  + ChatColor.RED + "Testblock '" + ChatColor.DARK_RED + testBlock.getKey() + ChatColor.RED + "' has been set on " + area + " side.");
				}
			} catch (IOException | WorldEditException e) {
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "There has been an error pasting '" + ChatColor.DARK_RED + testBlock.getKey() + ChatColor.RED + "' on " + area + " side.");
				e.printStackTrace();
			}
		}
	}
}