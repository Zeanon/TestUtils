package de.zeanon.testutils.plugin.commands;

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
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class TestBlock {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length == 0) {
			TestBlock.executeInternally(p, null);
		} else {
			if (args[0].equalsIgnoreCase("undo")) {
				TestBlock.undo(p);
			} else {
				TestBlock.executeInternally(p, args[0]);
			}
		}
	}

	private void executeInternally(final @NotNull Player p, final @Nullable String name) {
		File tempFile = TestBlock.getBlock(p.getUniqueId().toString(), name);
		if (tempFile == null) {
			p.sendMessage(ChatColor.RED + "You do not have registered a block as '" + name + "'.");
			return;
		}
		ClipboardFormat format = ClipboardFormats.findByFile(tempFile);
		try (ClipboardReader reader = Objects.requireNonNull(format).getReader(new FileInputStream(tempFile))) {
			Clipboard clipboard = reader.read();
			try (EditSession editSession = SessionFactory.createSession(p)) {
				ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
				if (tempRegion == null) {
					p.sendMessage(ChatColor.RED + "You are not standing in an applicable region.");
					return;
				}
				BlockVector3 pastePoint = BlockVector3.at(tempRegion.getMinimumPoint().getBlockX(), tempRegion.getMinimumPoint().getBlockY(), tempRegion.getMinimumPoint().getBlockZ() - 99);

				ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);

				if (tempRegion.getId().endsWith("_north")) {
					pastePoint = BlockVector3.at(tempRegion.getMaximumPoint().getBlockX(), tempRegion.getMinimumPoint().getBlockY(), tempRegion.getMaximumPoint().getBlockZ() + 99);
					clipboardHolder.setTransform(new AffineTransform().rotateY(180));
				}

				Operation operation = clipboardHolder
						.createPaste(editSession)
						.to(pastePoint)
						.ignoreAirBlocks(true)
						.build();
				try {
					Operations.complete(operation);
					p.sendMessage(ChatColor.RED + "Testblock '" + (name == null ? "default" : name) + "' was set.");
				} catch (WorldEditException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean undo(final @NotNull Player p) {
		EditSession tempSession = SessionFactory.getSession(p);
		if (tempSession == null) {
			p.sendMessage(ChatColor.RED + "Nothing left to undo.");
		} else {
			tempSession.undo(tempSession);
			p.sendMessage(ChatColor.RED + "You undid your last action.");
		}
		return true;
	}

	private @Nullable File getBlock(final @NotNull String uuid, final @Nullable String name) {

		final File tempFile = name == null ? new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + uuid, "default.schem")
										   : new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + uuid, name + ".schem");
		if (tempFile.exists() && tempFile.isFile()) {
			return tempFile;
		} else {
			return null;
		}
	}
}