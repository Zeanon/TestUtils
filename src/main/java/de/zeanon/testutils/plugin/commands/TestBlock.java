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
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
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
			TestBlock.pasteBlock(p, null, TestAreaUtils.getOppositeRegion(p), false);
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("undo")) {
				TestBlock.undo(p);
			} else if (args[0].equalsIgnoreCase("here")) {
				TestBlock.pasteBlock(p, null, TestAreaUtils.getRegion(p), true);
			} else {
				TestBlock.pasteBlock(p, args[0], TestAreaUtils.getOppositeRegion(p), false);
			}
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("here")) {
				TestBlock.pasteBlock(p, args[1], TestAreaUtils.getRegion(p), true);
			} else if (args[1].equalsIgnoreCase("here")) {
				TestBlock.pasteBlock(p, args[0], TestAreaUtils.getRegion(p), true);
			} else {
				p.sendMessage(ChatColor.DARK_AQUA + "Invalid sub-commands '" + ChatColor.GOLD + args[0] + ChatColor.DARK_AQUA + "' and '" + ChatColor.GOLD + args[1] + "'.");
			}
		} else {
			p.sendMessage(ChatColor.DARK_AQUA + "Too many arguments.");
		}
	}

	public void registerBlock(final @NotNull Player p, final @Nullable String name) {
		final ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);

		if (tempRegion == null) {
			p.sendMessage(ChatColor.RED + "You are in no suitable region.");
			return;
		}

		World tempWorld = new BukkitWorld(p.getWorld());
		CuboidRegion region = new CuboidRegion(tempWorld, tempRegion.getMinimumPoint(), tempRegion.getMaximumPoint());
		BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

		final BlockVector3 copyPoint;

		if (tempRegion.getId().endsWith("_south")) {
			copyPoint = BlockVector3.at(region.getMaximumPoint().getBlockX(), region.getMinimumPoint().getBlockY(), region.getMaximumPoint().getBlockZ());
		} else {
			copyPoint = region.getMinimumPoint();
		}

		try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(tempWorld, -1)) {
			ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
					editSession, region, region.getMinimumPoint(), clipboard, copyPoint
			);

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
		p.sendMessage(ChatColor.RED + "You registered a new block with the name: " + ChatColor.DARK_RED + (name == null ? "default" : name));
	}

	private void pasteBlock(final @NotNull Player p, final @Nullable String name, final @Nullable ProtectedRegion tempRegion, final boolean here) {
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
				try {
					Operations.complete(operation);
					p.sendMessage(ChatColor.RED + "Testblock '" + ChatColor.DARK_RED + (name != null && testBlock != null ? name : "default") + ChatColor.RED + "' has been set " + (here ? "on your side." : "on the other side."));
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