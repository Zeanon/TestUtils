package de.zeanon.testutils.plugin.commands.testblock;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import java.io.IOException;
import java.io.InputStream;
import javafx.util.Pair;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class PasteBlock {

	public void pasteBlock(final @NotNull Player p, final @Nullable String name, final @Nullable ProtectedRegion tempRegion, final boolean here) {
		final @Nullable Pair<InputStream, String> testBlock = TestBlock.getBlock(p, name);
		if (testBlock != null) { //NOSONAR
			final @Nullable ClipboardFormat format = ClipboardFormats.findByAlias("schem");
			try (final @NotNull ClipboardReader reader = Objects.notNull(format).getReader(testBlock.getKey())) {
				@NotNull Clipboard clipboard = reader.read();
				try (EditSession editSession = SessionFactory.createSession(p)) {
					if (tempRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
						return;
					}

					final BlockVector3 pastePoint;

					ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);

					if (tempRegion.getId().endsWith("_south")) {
						pastePoint = BlockVector3.at(tempRegion.getMaximumPoint().getBlockX(), tempRegion.getMinimumPoint().getBlockY(), tempRegion.getMaximumPoint().getBlockZ());
						clipboardHolder.setTransform(new AffineTransform().rotateY(180));
					} else {
						pastePoint = tempRegion.getMinimumPoint();
					}

					Operation operation = clipboardHolder
							.createPaste(editSession)
							.to(pastePoint)
							.ignoreAirBlocks(true)
							.build();

					Operations.complete(operation);
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "Testblock '" + ChatColor.DARK_RED + testBlock.getValue() + ChatColor.RED + "' has been set " + (here ? "on your side." : "on the other side."));
				}
			} catch (IOException | WorldEditException e) {
				e.printStackTrace();
			}
		}
	}

	public void undo(final @NotNull Player p) {
		EditSession tempSession = SessionFactory.getSession(p);
		if (tempSession == null) {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Nothing left to undo.");
		} else {
			tempSession.undo(tempSession);
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "You undid your last action.");
		}
	}
}