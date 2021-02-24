package de.zeanon.testutils.plugin.commands.testblock;

import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import java.io.File;
import java.io.InputStream;
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
			PasteBlock.pasteBlock(p, null, TestAreaUtils.getOppositeRegion(p), false);
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("undo")) {
				PasteBlock.undo(p);
			} else if (args[0].equalsIgnoreCase("redo")) {
				PasteBlock.redo(p);
			} else if (args[0].equalsIgnoreCase("here")) {
				PasteBlock.pasteBlock(p, null, TestAreaUtils.getRegion(p), true);
			} else {
				PasteBlock.pasteBlock(p, args[0], TestAreaUtils.getOppositeRegion(p), false);
			}
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("here")) {
				PasteBlock.pasteBlock(p, args[1], TestAreaUtils.getRegion(p), true);
			} else if (args[1].equalsIgnoreCase("here")) {
				PasteBlock.pasteBlock(p, args[0], TestAreaUtils.getRegion(p), true);
			} else {
				p.sendMessage(ChatColor.DARK_AQUA + "Invalid sub-commands '" + ChatColor.GOLD + args[0] + ChatColor.DARK_AQUA + "' and '" + ChatColor.GOLD + args[1] + "'.");
			}
		} else {
			p.sendMessage(ChatColor.DARK_AQUA + "Too many arguments.");
		}
	}

	public @Nullable Pair<InputStream, String> getBlock(final @NotNull Player p, final @Nullable String name) {
		if (name != null) {
			final @NotNull File tempFile = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString(), name + ".schem");
			if (tempFile.exists() && tempFile.isFile()) {
				return new Pair<>(BaseFileUtils.createNewInputStreamFromFile(tempFile), name);
			} else if (BaseFileUtils.removeExtension(tempFile).exists() && BaseFileUtils.removeExtension(tempFile).isDirectory()) {
				p.sendMessage(GlobalMessageUtils.messageHead +
							  ChatColor.RED + "'" + ChatColor.DARK_RED + name + ChatColor.RED + "' is not a valid block but a directory.");
				return null;
			} else {
				p.sendMessage(GlobalMessageUtils.messageHead +
							  ChatColor.RED + "'" + ChatColor.DARK_RED + name + ChatColor.RED + "' is not a valid block.");
				return new Pair<>(TestBlock.getDefaultBlock(p.getUniqueId().toString()), "default");
			}
		} else {
			return new Pair<>(TestBlock.getDefaultBlock(p.getUniqueId().toString()), "default");
		}
	}

	private @NotNull InputStream getDefaultBlock(final @NotNull String uuid) {
		final @NotNull File tempFile = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + uuid, "default.schem");
		if (tempFile.exists() && tempFile.isFile()) {
			return BaseFileUtils.createNewInputStreamFromFile(tempFile);
		} else {
			return BaseFileUtils.createNewInputStreamFromResource("resources/default.schem");
		}
	}
}