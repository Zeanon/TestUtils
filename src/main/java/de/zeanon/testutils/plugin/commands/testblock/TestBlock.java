package de.zeanon.testutils.plugin.commands.testblock;

import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.commandframework.SWCommand;
import de.zeanon.testutils.commandframework.TypeMapper;
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.MappedFile;
import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class TestBlock extends SWCommand {


	public TestBlock() {
		super("testblock", "tb");
	}

	public static @Nullable Pair<String, InputStream> getBlock(final @NotNull Player p, final @Nullable MappedFile mappedFile) {
		if (mappedFile != null) {
			final @NotNull File tempFile = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/TestBlocks/" + p.getUniqueId().toString(), mappedFile + ".schem");
			if (tempFile.exists() && tempFile.isFile()) {
				return new Pair<>(mappedFile.getName(), BaseFileUtils.createNewInputStreamFromFile(tempFile));
			} else if (BaseFileUtils.removeExtension(tempFile).exists() && BaseFileUtils.removeExtension(tempFile).isDirectory()) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "'" + ChatColor.DARK_RED + mappedFile + ChatColor.RED + "' is not a valid block but a directory.");
				return null;
			} else {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "'" + ChatColor.DARK_RED + mappedFile + ChatColor.RED + "' is not a valid block.");
				return new Pair<>("default", TestBlock.getDefaultBlock(p.getUniqueId().toString()));
			}
		} else {
			return new Pair<>("default", TestBlock.getDefaultBlock(p.getUniqueId().toString()));
		}
	}


	@Register
	public void noArgs(final @NotNull Player p) {
		PasteBlock.pasteBlock(p, null, TestAreaUtils.getOppositeRegion(p), "the other");
	}

	@Register
	public void oneArg(final @NotNull Player p, final @NotNull RegionSide regionSide) {
		PasteBlock.pasteBlock(p, null, TestAreaUtils.getRegion(p, regionSide), regionSide.getName());
	}

	@Register
	public void oneArg(final @NotNull Player p, final @NotNull MappedFile mappedFile) {
		PasteBlock.pasteBlock(p, mappedFile, TestAreaUtils.getOppositeRegion(p), "the other");
	}

	@Register
	public void twoArgs(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull MappedFile mappedFile) {
		PasteBlock.pasteBlock(p, mappedFile, TestAreaUtils.getRegion(p, regionSide), regionSide.getName());
	}

	@Register
	public void twoArgs(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull RegionSide regionSide) {
		PasteBlock.pasteBlock(p, mappedFile, TestAreaUtils.getRegion(p, regionSide), regionSide.getName());
	}


	private static @NotNull InputStream getDefaultBlock(final @NotNull String uuid) {
		final @NotNull File tempFile = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/TestBlocks/" + uuid, "default.schem");
		if (tempFile.exists() && tempFile.isFile()) {
			return BaseFileUtils.createNewInputStreamFromFile(tempFile);
		} else {
			return BaseFileUtils.createNewInputStreamFromResource("resources/default.schem");
		}
	}


	@ClassMapper(value = MappedFile.class, local = true)
	private @NotNull TypeMapper<MappedFile> mapFile() {
		return new TypeMapper<MappedFile>() {
			@Override
			public MappedFile map(final @NotNull String[] previous, final @NotNull String s) {
				if (InitMode.forbiddenFileName(s)) {
					return null;
				} else {
					return new MappedFile(s);
				}
			}

			@Override
			public java.util.List<String> tabCompletes(final @NotNull CommandSender commandSender, final @NotNull String[] previousArguments, final @NotNull String arg) {
				if (commandSender instanceof Player) {
					final @NotNull Player p = (Player) commandSender;
					final int lastIndex = arg.lastIndexOf("/");
					final @NotNull String path;
					if (lastIndex < 0) {
						path = "";
					} else {
						path = "/" + arg.substring(0, lastIndex);
					}
					try {
						final @NotNull Path filePath = TestUtils.getInstance().getDataFolder().toPath().resolve("TestBlocks/" + p.getUniqueId() + path).toRealPath();
						final @NotNull Path basePath = TestUtils.getInstance().getDataFolder().toPath().resolve("TestBlocks/" + p.getUniqueId()).toRealPath();
						if (filePath.startsWith(basePath)) {
							final @NotNull List<String> results = new LinkedList<>();
							for (final @NotNull File file : BaseFileUtils.listFilesOfTypeAndFolders(filePath.toFile(), "schem")) {
								final @NotNull String fileName = FilenameUtils.separatorsToUnix(BaseFileUtils.removeExtension(basePath.relativize(file.toPath()).toString()));
								if (!fileName.equalsIgnoreCase("default")) {
									results.add(fileName);
								}
							}
							return results;
						} else {
							return null;
						}
					} catch (IOException e) {
						return null;
					}
				} else {
					return null;
				}
			}
		};
	}
}