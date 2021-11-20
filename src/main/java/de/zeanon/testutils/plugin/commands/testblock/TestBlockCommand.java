package de.zeanon.testutils.plugin.commands.testblock;

import de.steamwar.commandframework.SWCommand;
import de.steamwar.commandframework.TypeMapper;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.CaseSensitive;
import de.zeanon.testutils.plugin.utils.enums.DeepSearch;
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


public class TestBlockCommand extends SWCommand {


	public static final @NotNull String MESSAGE_HEAD = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "TestBlocks" + ChatColor.DARK_GRAY + "] ";
	public static final @NotNull Path TESTBLOCK_FOLDER = TestUtils.getPluginFolder().resolve("TestBlocks");


	public TestBlockCommand() {
		super(new Prefix("testutils"), "testblock", "tb");
	}

	public static @Nullable Pair<String, InputStream> getBlock(final @NotNull Player p, final @Nullable MappedFile mappedFile) {
		if (mappedFile != null) {
			final @NotNull File tempFile = TestBlockCommand.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString()).resolve(mappedFile + ".schem").toFile();
			if (tempFile.exists() && tempFile.isFile()) {
				return new Pair<>(mappedFile.getName(), BaseFileUtils.createNewInputStreamFromFile(tempFile));
			} else if (BaseFileUtils.removeExtension(tempFile).exists() && BaseFileUtils.removeExtension(tempFile).isDirectory()) {
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "'" + ChatColor.DARK_RED + mappedFile + ChatColor.RED + "' is not a valid block but a directory.");
				return null;
			} else {
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "'" + ChatColor.DARK_RED + mappedFile + ChatColor.RED + "' is not a valid block.");
				return new Pair<>("default", TestBlockCommand.getDefaultBlock(p.getUniqueId().toString()));
			}
		} else {
			return new Pair<>("default", TestBlockCommand.getDefaultBlock(p.getUniqueId().toString()));
		}
	}

	public static @NotNull TypeMapper<MappedFile> getMappedFileTypeMapper() {
		return new TypeMapper<MappedFile>() {
			@Override
			public MappedFile map(final @NotNull String[] previous, final @NotNull String s) {
				if (TestAreaUtils.forbiddenFileName(s)) {
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
						final @NotNull Path filePath = TestBlockCommand.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString()).resolve(path).toRealPath();
						final @NotNull Path basePath = TestBlockCommand.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString()).toRealPath();
						if (filePath.startsWith(basePath)) {
							final @NotNull List<String> results = new LinkedList<>();
							for (final @NotNull File file : Objects.notNull(BaseFileUtils.listFilesOfTypeAndFolders(filePath.toFile(), "schem"))) {
								final @NotNull String fileName = FilenameUtils.separatorsToUnix(BaseFileUtils.removeExtension(basePath.relativize(file.toPath()).toString()));
								if (!fileName.equalsIgnoreCase("default")) {
									results.add(fileName);
								}
							}
							return results;
						} else {
							return null;
						}
					} catch (final IOException e) {
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


	@Register("listfolders")
	public void noArgListFolders(final @NotNull Player p) {
		SearchFolder.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, 1, null);
	}

	@Register("listfolders")
	public void oneArgListFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch) {
		SearchFolder.execute(p, null, deepSearch, CaseSensitive.DENY, 1, null);
	}

	@Register("listfolders")
	public void oneArgListFolders(final @NotNull Player p, final @NotNull MappedFile mappedFile) {
		SearchFolder.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, 1, null);
	}

	@Register("listfolders")
	public void oneArgListFolders(final @NotNull Player p, final int page) {
		SearchFolder.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, page, null);
	}

	@Register("listfolders")
	public void twoArgsListFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile) {
		SearchFolder.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, 1, null);
	}

	@Register("listfolders")
	public void twoArgsListFolders(final @NotNull Player p, final @NotNull MappedFile mappedFile, final int page) {
		SearchFolder.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, page, null);
	}

	@Register("listfolders")
	public void twoArgsListFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final int page) {
		SearchFolder.execute(p, null, deepSearch, CaseSensitive.DENY, page, null);
	}

	@Register("listfolders")
	public void threeArgsListFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final int page) {
		SearchFolder.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, page, null);
	}


	@Register("list")
	public void noArgList(final @NotNull Player p) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, 1, null);
	}

	@Register("list")
	public void oneArgList(final @NotNull Player p, final @NotNull DeepSearch deepSearch) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, null, deepSearch, CaseSensitive.DENY, 1, null);
	}

	@Register("list")
	public void oneArgList(final @NotNull Player p, final @NotNull MappedFile mappedFile) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, 1, null);
	}

	@Register("list")
	public void oneArgList(final @NotNull Player p, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, page, null);
	}

	@Register("list")
	public void twoArgsList(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, 1, null);
	}

	@Register("list")
	public void twoArgsList(final @NotNull Player p, final @NotNull MappedFile mappedFile, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, page, null);
	}

	@Register("list")
	public void twoArgsList(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, null, deepSearch, CaseSensitive.DENY, page, null);
	}

	@Register("list")
	public void threeArgsList(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, page, null);
	}


	@Register("listblocks")
	public void noArgListBlock(final @NotNull Player p) {
		de.zeanon.testutils.plugin.commands.testblock.SearchBlock.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, 1, null);
	}

	@Register("listblocks")
	public void oneArgListBlock(final @NotNull Player p, final @NotNull DeepSearch deepSearch) {
		de.zeanon.testutils.plugin.commands.testblock.SearchBlock.execute(p, null, deepSearch, CaseSensitive.DENY, 1, null);
	}

	@Register("listblocks")
	public void oneArgListBlock(final @NotNull Player p, final @NotNull MappedFile mappedFile) {
		de.zeanon.testutils.plugin.commands.testblock.SearchBlock.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, 1, null);
	}

	@Register("listblocks")
	public void oneArgListBlock(final @NotNull Player p, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.SearchBlock.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, page, null);
	}

	@Register("listblocks")
	public void twoArgsListBlock(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile) {
		de.zeanon.testutils.plugin.commands.testblock.SearchBlock.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, 1, null);
	}

	@Register("listblocks")
	public void twoArgsListBlock(final @NotNull Player p, final @NotNull MappedFile mappedFile, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.SearchBlock.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, page, null);
	}

	@Register("listblocks")
	public void twoArgsListBlock(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.SearchBlock.execute(p, null, deepSearch, CaseSensitive.DENY, page, null);
	}

	@Register("listblocks")
	public void threeArgsListBlock(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.SearchBlock.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, page, null);
	}


	@Register("search")
	public void oneArgSearch(final @NotNull Player p, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("search")
	public void twoArgSearch(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, null, deepSearch, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("search")
	public void twoArgSearch(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, null, DeepSearch.DENY, caseSensitive, 1, sequence.getName());
	}

	@Register("search")
	public void twoArgSearch(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("search")
	public void twoArgSearch(final @NotNull Player p, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, page, sequence.getName());
	}


	@Register("search")
	public void threeArgsSearch(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("search")
	public void threeArgsSearch(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, null, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("search")
	public void threeArgsSearch(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, null, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("search")
	public void threeArgsSearch(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, null, deepSearch, CaseSensitive.DENY, page, sequence.getName());
	}

	@Register("search")
	public void threeArgsSearch(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, mappedFile, DeepSearch.DENY, caseSensitive, 1, sequence.getName());
	}

	@Register("search")
	public void threeArgsSearch(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, null, DeepSearch.DENY, caseSensitive, page, sequence.getName());
	}

	@Register("search")
	public void threeArgsSearch(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, page, sequence.getName());
	}

	@Register("search")
	public void fourArgsSearch(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, page, sequence.getName());
	}

	@Register("search")
	public void fourArgsSearch(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, null, deepSearch, caseSensitive, page, sequence.getName());
	}

	@Register("search")
	public void fourArgsSearch(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, mappedFile, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("search")
	public void fourArgsSearch(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, null, deepSearch, caseSensitive, page, sequence.getName());
	}

	@Register("search")
	public void fourArgsSearch(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, mappedFile, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("search")
	public void fourArgsSearch(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, mappedFile, DeepSearch.DENY, caseSensitive, page, sequence.getName());
	}

	@Register("search")
	public void fiveArgsSearch(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, mappedFile, deepSearch, caseSensitive, page, sequence.getName());
	}

	@Register("search")
	public void fiveArgsSearch(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.Search.execute(p, mappedFile, deepSearch, caseSensitive, page, sequence.getName());
	}


	@Register("searchblock")
	public void oneArgSearchBlock(final @NotNull Player p, final @NotNull MappedFile sequence) {
		SearchBlock.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("searchblock")
	public void twoArgSearchBlock(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence) {
		SearchBlock.execute(p, null, deepSearch, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("searchblock")
	public void twoArgSearchBlock(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence) {
		SearchBlock.execute(p, null, DeepSearch.DENY, caseSensitive, 1, sequence.getName());
	}

	@Register("searchblock")
	public void twoArgSearchBlock(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		SearchBlock.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("searchblock")
	public void twoArgSearchBlock(final @NotNull Player p, final @NotNull MappedFile sequence, final int page) {
		SearchBlock.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, page, sequence.getName());
	}


	@Register("searchblock")
	public void threeArgsSearchBlock(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		SearchBlock.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("searchblock")
	public void threeArgsSearchBlock(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence) {
		SearchBlock.execute(p, null, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("searchblock")
	public void threeArgsSearchBlock(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence) {
		SearchBlock.execute(p, null, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("searchblock")
	public void threeArgsSearchBlock(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence, final int page) {
		SearchBlock.execute(p, null, deepSearch, CaseSensitive.DENY, page, sequence.getName());
	}

	@Register("searchblock")
	public void threeArgsSearchBlock(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		SearchBlock.execute(p, mappedFile, DeepSearch.DENY, caseSensitive, 1, sequence.getName());
	}

	@Register("searchblock")
	public void threeArgsSearchBlock(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence, final int page) {
		SearchBlock.execute(p, null, DeepSearch.DENY, caseSensitive, page, sequence.getName());
	}

	@Register("searchblock")
	public void threeArgsSearchBlock(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		SearchBlock.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, page, sequence.getName());
	}

	@Register("searchblock")
	public void fourArgsSearchBlock(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		SearchBlock.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, page, sequence.getName());
	}

	@Register("searchblock")
	public void fourArgsSearchBlock(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence, final int page) {
		SearchBlock.execute(p, null, deepSearch, caseSensitive, page, sequence.getName());
	}

	@Register("searchblock")
	public void fourArgsSearchBlock(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		SearchBlock.execute(p, mappedFile, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("searchblock")
	public void fourArgsSearchBlock(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence, final int page) {
		SearchBlock.execute(p, null, deepSearch, caseSensitive, page, sequence.getName());
	}

	@Register("searchblock")
	public void fourArgsSearchBlock(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		SearchBlock.execute(p, mappedFile, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("searchblock")
	public void fourArgsSearchBlock(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		SearchBlock.execute(p, mappedFile, DeepSearch.DENY, caseSensitive, page, sequence.getName());
	}

	@Register("searchblock")
	public void fiveArgsSearchBlock(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		SearchBlock.execute(p, mappedFile, deepSearch, caseSensitive, page, sequence.getName());
	}

	@Register("searchblock")
	public void fiveArgsSearchBlock(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		SearchBlock.execute(p, mappedFile, deepSearch, caseSensitive, page, sequence.getName());
	}


	@Register("seachfolder")
	public void oneArgSearchFolders(final @NotNull Player p, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("seachfolder")
	public void twoArgSearchFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, null, deepSearch, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("seachfolder")
	public void twoArgSearchFolders(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, null, DeepSearch.DENY, caseSensitive, 1, sequence.getName());
	}

	@Register("seachfolder")
	public void twoArgSearchFolders(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("seachfolder")
	public void twoArgSearchFolders(final @NotNull Player p, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, page, sequence.getName());
	}


	@Register("seachfolder")
	public void threeArgsSearchFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("seachfolder")
	public void threeArgsSearchFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, null, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("seachfolder")
	public void threeArgsSearchFolders(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, null, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("seachfolder")
	public void threeArgsSearchFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, null, deepSearch, CaseSensitive.DENY, page, sequence.getName());
	}

	@Register("seachfolder")
	public void threeArgsSearchFolders(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, mappedFile, DeepSearch.DENY, caseSensitive, 1, sequence.getName());
	}

	@Register("seachfolder")
	public void threeArgsSearchFolders(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, null, DeepSearch.DENY, caseSensitive, page, sequence.getName());
	}

	@Register("seachfolder")
	public void threeArgsSearchFolders(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, page, sequence.getName());
	}

	@Register("seachfolder")
	public void fourArgsSearchFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, page, sequence.getName());
	}

	@Register("seachfolder")
	public void fourArgsSearchFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, null, deepSearch, caseSensitive, page, sequence.getName());
	}

	@Register("seachfolder")
	public void fourArgsSearchFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, mappedFile, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("seachfolder")
	public void fourArgsSearchFolders(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, null, deepSearch, caseSensitive, page, sequence.getName());
	}

	@Register("seachfolder")
	public void fourArgsSearchFolders(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, mappedFile, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("seachfolder")
	public void fourArgsSearchFolders(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, mappedFile, DeepSearch.DENY, caseSensitive, page, sequence.getName());
	}

	@Register("seachfolder")
	public void fiveArgsSearchFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, mappedFile, deepSearch, caseSensitive, page, sequence.getName());
	}

	@Register("seachfolder")
	public void fiveArgsSearchFolders(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		de.zeanon.testutils.plugin.commands.testblock.SearchFolder.execute(p, mappedFile, deepSearch, caseSensitive, page, sequence.getName());
	}


	private static @NotNull InputStream getDefaultBlock(final @NotNull String uuid) {
		final @NotNull File tempFile = TestBlockCommand.TESTBLOCK_FOLDER.resolve(uuid).resolve("default.schem").toFile();
		if (tempFile.exists() && tempFile.isFile()) {
			return BaseFileUtils.createNewInputStreamFromFile(tempFile);
		} else {
			return BaseFileUtils.createNewInputStreamFromResource("resources/default.schem");
		}
	}


	@ClassMapper(value = MappedFile.class, local = true)
	private @NotNull TypeMapper<MappedFile> mapFile() {
		return TestBlockCommand.getMappedFileTypeMapper();
	}
}