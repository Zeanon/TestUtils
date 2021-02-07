package de.zeanon.testutils.plugin.commands;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.BaseFileUtils;
import de.zeanon.testutils.plugin.utils.Objects;
import de.zeanon.testutils.plugin.utils.SessionFactory;
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
public class TestBlock {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length == 0) {
			TestBlock.executeInternally(p, null, TestAreaUtils.getRegion(p));
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("undo")) {
				TestBlock.undo(p);
			} else if (args[0].equalsIgnoreCase("here")) {
				TestBlock.executeInternally(p, null, TestAreaUtils.getOppositeRegion(p));
			} else {
				TestBlock.executeInternally(p, args[0], TestAreaUtils.getRegion(p));
			}
		} else {
			if (args[0].equalsIgnoreCase("here")) {
				TestBlock.executeInternally(p, args[1], TestAreaUtils.getOppositeRegion(p));
			}
		}
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

	private void executeInternally(final @NotNull Player p, final @Nullable String name, final @Nullable ProtectedRegion tempRegion) {
		final File testBlock = TestBlock.getBlock(p.getUniqueId().toString(), name);
		final ClipboardFormat format = testBlock != null ? ClipboardFormats.findByFile(testBlock) : ClipboardFormats.findByAlias("schem");
		try (ClipboardReader reader = Objects.notNull(format).getReader(testBlock != null ? BaseFileUtils.createNewInputStreamFromFile(testBlock)
																						  : BaseFileUtils.createNewInputStreamFromResource("resources/default.schem"))) {
			Clipboard clipboard = reader.read();
			try (EditSession editSession = SessionFactory.createSession(p)) {
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

				BlockVector3 finalPastePoint = pastePoint;
				Operation operation = clipboardHolder
						.createPaste(editSession)
						.to(finalPastePoint)
						.ignoreAirBlocks(true)
						.build();
				try {
					Operations.complete(operation);
					p.sendMessage(ChatColor.RED + "Testblock '" + (name != null && testBlock != null ? BaseFileUtils.removeExtension(testBlock.getName()) : "default") + "' was set.");
				} catch (WorldEditException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void undo(final @NotNull Player p) {
		EditSession tempSession = SessionFactory.getSession(p);
		if (tempSession == null) {
			p.sendMessage(ChatColor.RED + "Nothing left to undo.");
		} else {
			tempSession.undo(tempSession);
			p.sendMessage(ChatColor.RED + "You undid your last action.");
		}
	}

	private @Nullable File getBlock(final @NotNull String uuid, final @Nullable String name) {
		if (name != null) {
			final @NotNull File tempFile = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + uuid, name + ".schem");
			if (tempFile.exists() && tempFile.isFile()) {
				return tempFile;
			} else {
				return TestBlock.getDefaultBlock(uuid);
			}
		} else {
			return TestBlock.getDefaultBlock(uuid);
		}
	}

	private @Nullable File getDefaultBlock(final @NotNull String uuid) {
		final @NotNull File tempFile = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + uuid, "default.schem");
		if (tempFile.exists() && tempFile.isFile()) {
			return tempFile;
		} else {
			return null;
		}
	}
}