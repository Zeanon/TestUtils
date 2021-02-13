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
import de.zeanon.testutils.plugin.utils.InternalFileUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import javafx.util.Pair;
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
			p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
						  ChatColor.RED + "You are in no suitable region.");
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

			forwardExtentCopy.setCopyingEntities(false);
			forwardExtentCopy.setCopyingBiomes(false);

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
		p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
					  ChatColor.RED + "You registered a new block with the name: " + ChatColor.DARK_RED + (name == null ? "default" : name));
	}

	public void deleteBlock(final @NotNull Player p, final @NotNull String name) {
		final @NotNull File file = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString() + "/" + name + ".schem"); //NOSONAR
		try {
			Files.delete(file.toPath());
			@Nullable String parentName = Objects.notNull(file.getAbsoluteFile().getParentFile().listFiles()).length == 0
										  ? InternalFileUtils.deleteEmptyParent(file, new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks"))
										  : null;

			p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
						  ChatColor.DARK_RED + name + ChatColor.RED + " was deleted successfully.");

			if (parentName != null) {
				p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
							  ChatColor.RED + "Folder "
							  + ChatColor.GREEN + parentName
							  + ChatColor.RED + " was deleted successfully due to being empty.");
			}
		} catch (IOException e) {
			p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
						  ChatColor.DARK_RED + name + ChatColor.RED + " could not be deleted, for further information please see [console].");
		}
	}

	private void pasteBlock(final @NotNull Player p, final @Nullable String name, final @Nullable ProtectedRegion tempRegion, final boolean here) {
		final Pair<File, String> testBlock = TestBlock.getBlock(p.getUniqueId().toString(), name);
		final ClipboardFormat format = testBlock.getKey() != null ? ClipboardFormats.findByFile(testBlock.getKey()) : ClipboardFormats.findByAlias("schem");
		try (ClipboardReader reader = Objects.notNull(format).getReader(testBlock.getKey() != null ? BaseFileUtils.createNewInputStreamFromFile(testBlock.getKey())
																								   : BaseFileUtils.createNewInputStreamFromResource("resources/default.schem"))) {
			Clipboard clipboard = reader.read();
			try (EditSession editSession = SessionFactory.createSession(p)) {
				if (tempRegion == null) {
					p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
								  ChatColor.RED + "You are not standing in an applicable region.");
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
				p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
							  ChatColor.RED + "Testblock '" + ChatColor.DARK_RED + testBlock.getValue() + ChatColor.RED + "' has been set " + (here ? "on your side." : "on the other side."));
			}
		} catch (IOException | WorldEditException e) {
			e.printStackTrace();
		}
	}

	private void undo(final @NotNull Player p) {
		EditSession tempSession = SessionFactory.getSession(p);
		if (tempSession == null) {
			p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
						  ChatColor.RED + "Nothing left to undo.");
		} else {
			tempSession.undo(tempSession);
			p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
						  ChatColor.RED + "You undid your last action.");
		}
	}

	private @NotNull Pair<File, String> getBlock(final @NotNull String uuid, final @Nullable String name) {
		if (name != null) {
			final @NotNull File tempFile = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + uuid, name + ".schem");
			if (tempFile.exists() && tempFile.isFile()) {
				return new Pair<>(tempFile, name);
			} else {
				return new Pair<>(TestBlock.getDefaultBlock(uuid), "default");
			}
		} else {
			return new Pair<>(TestBlock.getDefaultBlock(uuid), "default");
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