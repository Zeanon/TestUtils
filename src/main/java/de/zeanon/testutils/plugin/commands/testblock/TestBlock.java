package de.zeanon.testutils.plugin.commands.testblock;

import de.steamwar.commandframework.SWCommand;
import de.steamwar.commandframework.TypeMapper;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.TestUtils;
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


	public static final @NotNull Path TESTBLOCK_FOLDER = TestUtils.getPluginFolder().resolve("TestBlocks");


	public TestBlock() {
		super("testblock", true, "tb");
	}

	public static @Nullable Pair<String, InputStream> getBlock(final @NotNull Player p, final @Nullable MappedFile mappedFile) {
		if (mappedFile != null) {
			final @NotNull File tempFile = TestBlock.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString()).resolve(mappedFile + ".schem").toFile();
			if (tempFile.exists() && tempFile.isFile()) {
				return new Pair<>(mappedFile.getName(), BaseFileUtils.createNewInputStreamFromFile(tempFile));
			} else if (BaseFileUtils.removeExtension(tempFile).exists() && BaseFileUtils.removeExtension(tempFile).isDirectory()) {
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "'" + ChatColor.DARK_RED + mappedFile + ChatColor.RED + "' is not a valid block but a directory.");
				return null;
			} else {
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "'" + ChatColor.DARK_RED + mappedFile + ChatColor.RED + "' is not a valid block.");
				return new Pair<>("default", TestBlock.getDefaultBlock(p.getUniqueId().toString()));
			}
		} else {
			return new Pair<>("default", TestBlock.getDefaultBlock(p.getUniqueId().toString()));
		}
	}

	public static @NotNull TypeMapper<MappedFile> getMappedFileTypeMapper() {
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
			public List<String> tabCompletes(final @NotNull CommandSender commandSender, final @NotNull String[] previousArguments, final @NotNull String arg) {
				if (commandSender instanceof Player) {
					final @NotNull Player p = (Player) commandSender;
					final int lastIndex = arg.lastIndexOf("/");
					final @NotNull String path = arg.substring(0, Math.max(lastIndex, 0));

					try {
						final @NotNull Path filePath = TestBlock.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString()).resolve(path).toRealPath();
						final @NotNull Path basePath = TestBlock.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString()).toRealPath();
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
		final @NotNull File tempFile = TestBlock.TESTBLOCK_FOLDER.resolve(uuid).resolve("default.schem").toFile();
		if (tempFile.exists() && tempFile.isFile()) {
			return BaseFileUtils.createNewInputStreamFromFile(tempFile);
		} else {
			return BaseFileUtils.createNewInputStreamFromResource("resources/default.schem");
		}
	}


	@ClassMapper(value = MappedFile.class, local = true)
	private @NotNull TypeMapper<MappedFile> mapFile() {
		return TestBlock.getMappedFileTypeMapper();
	}
}