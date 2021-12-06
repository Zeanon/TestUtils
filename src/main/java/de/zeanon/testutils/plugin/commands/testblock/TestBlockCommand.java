package de.zeanon.testutils.plugin.commands.testblock;

import de.steamwar.commandframework.SWCommand;
import de.steamwar.commandframework.TypeMapper;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.CommandRequestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@SuppressWarnings("unused")
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


	@Register(value = "load", help = true)
	public void loadHelp(final @NotNull Player p, final @NotNull String... args) {
		Load.usage(p);
	}

	@Register("load")
	public void noArgsLoad(final @NotNull Player p) {
		Load.execute(p, null, TestAreaUtils.getOppositeRegion(p), "the other");
	}

	@Register("load")
	public void oneArgLoad(final @NotNull Player p, final @NotNull RegionSide regionSide) {
		Load.execute(p, null, TestAreaUtils.getRegion(p, regionSide), regionSide.getName());
	}

	@Register("load")
	public void oneArgLoad(final @NotNull Player p, final @NotNull MappedFile mappedFile) {
		Load.execute(p, mappedFile, TestAreaUtils.getOppositeRegion(p), "the other");
	}

	@Register("load")
	public void twoArgsLoad(final @NotNull Player p, final @NotNull RegionSide regionSide, final @NotNull MappedFile mappedFile) {
		Load.execute(p, mappedFile, TestAreaUtils.getRegion(p, regionSide), regionSide.getName());
	}

	@Register("load")
	public void twoArgsLoad(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull RegionSide regionSide) {
		Load.execute(p, mappedFile, TestAreaUtils.getRegion(p, regionSide), regionSide.getName());
	}


	@Register(value = "list", help = true)
	public void listHelp(final @NotNull Player p, final @NotNull String... args) {
		Search.listUsage(p);
	}

	@Register("list")
	public void noArgList(final @NotNull Player p) {
		Search.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, 1, null);
	}

	@Register("list")
	public void oneArgList(final @NotNull Player p, final @NotNull DeepSearch deepSearch) {
		Search.execute(p, null, deepSearch, CaseSensitive.DENY, 1, null);
	}

	@Register("list")
	public void oneArgList(final @NotNull Player p, final @NotNull MappedFile mappedFile) {
		Search.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, 1, null);
	}

	@Register("list")
	public void oneArgList(final @NotNull Player p, final int page) {
		Search.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, page, null);
	}

	@Register("list")
	public void twoArgsList(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile) {
		Search.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, 1, null);
	}

	@Register("list")
	public void twoArgsList(final @NotNull Player p, final @NotNull MappedFile mappedFile, final int page) {
		Search.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, page, null);
	}

	@Register("list")
	public void twoArgsList(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final int page) {
		Search.execute(p, null, deepSearch, CaseSensitive.DENY, page, null);
	}

	@Register("list")
	public void threeArgsList(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final int page) {
		Search.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, page, null);
	}


	@Register(value = "listblocks", help = true)
	public void listBlocksHelp(final @NotNull Player p, final @NotNull String... args) {
		SearchBlock.listUsage(p);
	}

	@Register("listblocks")
	public void noArgListBlocks(final @NotNull Player p) {
		SearchBlock.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, 1, null);
	}

	@Register("listblocks")
	public void oneArgListBlocks(final @NotNull Player p, final @NotNull DeepSearch deepSearch) {
		SearchBlock.execute(p, null, deepSearch, CaseSensitive.DENY, 1, null);
	}

	@Register("listblocks")
	public void oneArgListBlocks(final @NotNull Player p, final @NotNull MappedFile mappedFile) {
		SearchBlock.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, 1, null);
	}

	@Register("listblocks")
	public void oneArgListBlocks(final @NotNull Player p, final int page) {
		SearchBlock.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, page, null);
	}

	@Register("listblocks")
	public void twoArgsListBlocks(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile) {
		SearchBlock.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, 1, null);
	}

	@Register("listblocks")
	public void twoArgsListBlocks(final @NotNull Player p, final @NotNull MappedFile mappedFile, final int page) {
		SearchBlock.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, page, null);
	}

	@Register("listblocks")
	public void twoArgsListBlocks(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final int page) {
		SearchBlock.execute(p, null, deepSearch, CaseSensitive.DENY, page, null);
	}

	@Register("listblocks")
	public void threeArgsListBlocks(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final int page) {
		SearchBlock.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, page, null);
	}


	@Register(value = "listfolders", help = true)
	public void listFoldersHelp(final @NotNull Player p, final @NotNull String... args) {
		SearchFolder.listUsage(p);
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


	@Register(value = "search", help = true)
	public void searchHelp(final @NotNull Player p, final @NotNull String... args) {
		Search.searchUsage(p);
	}

	@Register("search")
	public void oneArgSearch(final @NotNull Player p, final @NotNull MappedFile sequence) {
		Search.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("search")
	public void twoArgSearch(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence) {
		Search.execute(p, null, deepSearch, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("search")
	public void twoArgSearch(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence) {
		Search.execute(p, null, DeepSearch.DENY, caseSensitive, 1, sequence.getName());
	}

	@Register("search")
	public void twoArgSearch(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		Search.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("search")
	public void twoArgSearch(final @NotNull Player p, final @NotNull MappedFile sequence, final int page) {
		Search.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, page, sequence.getName());
	}

	@Register("search")
	public void threeArgsSearch(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		Search.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("search")
	public void threeArgsSearch(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence) {
		Search.execute(p, null, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("search")
	public void threeArgsSearch(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence) {
		Search.execute(p, null, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("search")
	public void threeArgsSearch(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence, final int page) {
		Search.execute(p, null, deepSearch, CaseSensitive.DENY, page, sequence.getName());
	}

	@Register("search")
	public void threeArgsSearch(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		Search.execute(p, mappedFile, DeepSearch.DENY, caseSensitive, 1, sequence.getName());
	}

	@Register("search")
	public void threeArgsSearch(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence, final int page) {
		Search.execute(p, null, DeepSearch.DENY, caseSensitive, page, sequence.getName());
	}

	@Register("search")
	public void threeArgsSearch(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		Search.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, page, sequence.getName());
	}

	@Register("search")
	public void fourArgsSearch(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		Search.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, page, sequence.getName());
	}

	@Register("search")
	public void fourArgsSearch(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence, final int page) {
		Search.execute(p, null, deepSearch, caseSensitive, page, sequence.getName());
	}

	@Register("search")
	public void fourArgsSearch(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		Search.execute(p, mappedFile, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("search")
	public void fourArgsSearch(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence, final int page) {
		Search.execute(p, null, deepSearch, caseSensitive, page, sequence.getName());
	}

	@Register("search")
	public void fourArgsSearch(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		Search.execute(p, mappedFile, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("search")
	public void fourArgsSearch(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		Search.execute(p, mappedFile, DeepSearch.DENY, caseSensitive, page, sequence.getName());
	}

	@Register("search")
	public void fiveArgsSearch(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		Search.execute(p, mappedFile, deepSearch, caseSensitive, page, sequence.getName());
	}

	@Register("search")
	public void fiveArgsSearch(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		Search.execute(p, mappedFile, deepSearch, caseSensitive, page, sequence.getName());
	}


	@Register(value = "searchblock", help = true)
	public void searchBlockHelp(final @NotNull Player p, final @NotNull String... args) {
		SearchBlock.searchUsage(p);
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


	@Register(value = "searchfolder", help = true)
	public void searchFolderHelp(final @NotNull Player p, final @NotNull String... args) {
		SearchFolder.searchUsage(p);
	}

	@Register("searchfolder")
	public void oneArgSearchFolders(final @NotNull Player p, final @NotNull MappedFile sequence) {
		SearchFolder.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("searchfolder")
	public void twoArgSearchFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence) {
		SearchFolder.execute(p, null, deepSearch, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("searchfolder")
	public void twoArgSearchFolders(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence) {
		SearchFolder.execute(p, null, DeepSearch.DENY, caseSensitive, 1, sequence.getName());
	}

	@Register("searchfolder")
	public void twoArgSearchFolders(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		SearchFolder.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("searchfolder")
	public void twoArgSearchFolders(final @NotNull Player p, final @NotNull MappedFile sequence, final int page) {
		SearchFolder.execute(p, null, DeepSearch.DENY, CaseSensitive.DENY, page, sequence.getName());
	}

	@Register("searchfolder")
	public void threeArgsSearchFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		SearchFolder.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, 1, sequence.getName());
	}

	@Register("searchfolder")
	public void threeArgsSearchFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence) {
		SearchFolder.execute(p, null, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("searchfolder")
	public void threeArgsSearchFolders(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence) {
		SearchFolder.execute(p, null, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("searchfolder")
	public void threeArgsSearchFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence, final int page) {
		SearchFolder.execute(p, null, deepSearch, CaseSensitive.DENY, page, sequence.getName());
	}

	@Register("searchfolder")
	public void threeArgsSearchFolders(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		SearchFolder.execute(p, mappedFile, DeepSearch.DENY, caseSensitive, 1, sequence.getName());
	}

	@Register("searchfolder")
	public void threeArgsSearchFolders(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence, final int page) {
		SearchFolder.execute(p, null, DeepSearch.DENY, caseSensitive, page, sequence.getName());
	}

	@Register("searchfolder")
	public void threeArgsSearchFolders(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		SearchFolder.execute(p, mappedFile, DeepSearch.DENY, CaseSensitive.DENY, page, sequence.getName());
	}

	@Register("searchfolder")
	public void fourArgsSearchFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		SearchFolder.execute(p, mappedFile, deepSearch, CaseSensitive.DENY, page, sequence.getName());
	}

	@Register("searchfolder")
	public void fourArgsSearchFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile sequence, final int page) {
		SearchFolder.execute(p, null, deepSearch, caseSensitive, page, sequence.getName());
	}

	@Register("searchfolder")
	public void fourArgsSearchFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		SearchFolder.execute(p, mappedFile, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("searchfolder")
	public void fourArgsSearchFolders(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile sequence, final int page) {
		SearchFolder.execute(p, null, deepSearch, caseSensitive, page, sequence.getName());
	}

	@Register("searchfolder")
	public void fourArgsSearchFolders(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence) {
		SearchFolder.execute(p, mappedFile, deepSearch, caseSensitive, 1, sequence.getName());
	}

	@Register("searchfolder")
	public void fourArgsSearchFolders(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		SearchFolder.execute(p, mappedFile, DeepSearch.DENY, caseSensitive, page, sequence.getName());
	}

	@Register("searchfolder")
	public void fiveArgsSearchFolders(final @NotNull Player p, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		SearchFolder.execute(p, mappedFile, deepSearch, caseSensitive, page, sequence.getName());
	}

	@Register("searchfolder")
	public void fiveArgsSearchFolders(final @NotNull Player p, final @NotNull CaseSensitive caseSensitive, final @NotNull DeepSearch deepSearch, final @NotNull MappedFile mappedFile, final @NotNull MappedFile sequence, final int page) {
		SearchFolder.execute(p, mappedFile, deepSearch, caseSensitive, page, sequence.getName());
	}


	@Register(value = "register", help = true)
	public void registerHelp(final @NotNull Player p, final @NotNull String... args) {
		de.zeanon.testutils.plugin.commands.testblock.Register.usage(p);
	}

	@Register("register")
	public void noArgsRegisterBlock(final @NotNull Player p) {
		de.zeanon.testutils.plugin.commands.testblock.Register.execute(null, p);
	}

	@Register("register")
	public void oneArgRegisterBlock(final @NotNull Player p, final @NotNull MappedFile mappedFile) {
		de.zeanon.testutils.plugin.commands.testblock.Register.execute(mappedFile, p);
	}


	@Register(value = {"del"}, help = true)
	@Register(value = {"delete"}, help = true)
	public void deleteBlockHelp(final @NotNull Player p, final @NotNull String... args) {
		DeleteBlock.usage(p);
	}

	@Register("del")
	@Register("delete")
	public void oneArgDeleteBlock(final @NotNull Player p, final @NotNull MappedFile mappedFile) {
		DeleteBlock.execute(mappedFile, null, p);
	}

	@Register("del")
	@Register("delete")
	public void twoArgsDeleteBlock(final @NotNull Player p, final @NotNull MappedFile mappedFile, final @NotNull CommandConfirmation confirmation) {
		DeleteBlock.execute(mappedFile, confirmation, p);
	}

	@Register(value = {"delfolder"}, help = true)
	@Register(value = {"deletefolder"}, help = true)
	public void deleteFolderHelp(final @NotNull Player p, final @NotNull String... args) {
		DeleteFolder.usage(p);
	}

	@Register("delfolder")
	@Register("deletefolder")
	public void oneArgDeleteFolder(final @NotNull Player p, final @NotNull MappedFolder mappedFolder) {
		DeleteFolder.execute(mappedFolder, null, p);
	}

	@Register("delfolder")
	@Register("deletefolder")
	public void twoArgsDeleteFolder(final @NotNull Player p, final @NotNull MappedFolder mappedFolder, final @NotNull CommandConfirmation confirmation) {
		DeleteFolder.execute(mappedFolder, confirmation, p);
	}


	@Register(value = {"rename"}, help = true)
	public void renameBlockHelp(final @NotNull Player p, final @NotNull String... args) {
		RenameBlock.usage(p);
	}

	@Register("rename")
	public void twoArgsRenameBlock(final @NotNull Player p, final @NotNull MappedFile oldMappedFile, final @NotNull MappedFile newMappedFile) {
		RenameBlock.execute(oldMappedFile, newMappedFile, null, p);
	}

	@Register("rename")
	public void threeArgsRenameBlock(final @NotNull Player p, final @NotNull MappedFile oldMappedFile, final @NotNull MappedFile newMappedFile, final @NotNull CommandConfirmation confirmation) {
		RenameBlock.execute(oldMappedFile, newMappedFile, confirmation, p);
	}


	@Register(value = {"renamefolder"}, help = true)
	public void renameFolderHelp(final @NotNull Player p, final @NotNull String... args) {
		RenameBlock.usage(p);
	}

	@Register("renamefolder")
	public void twoArgsRenameFolder(final @NotNull Player p, final @NotNull MappedFolder oldMappedFolder, final @NotNull MappedFolder newMappedFolder) {
		RenameFolder.execute(oldMappedFolder, newMappedFolder, null, p);
	}

	@Register("renamefolder")
	public void threeArgsRenameFolder(final @NotNull Player p, final @NotNull MappedFolder oldMappedFolder, final @NotNull MappedFolder newMappedFolder, final @NotNull CommandConfirmation confirmation) {
		RenameFolder.execute(oldMappedFolder, newMappedFolder, confirmation, p);
	}


	private static @NotNull InputStream getDefaultBlock(final @NotNull String uuid) {
		final @NotNull File tempFile = TestBlockCommand.TESTBLOCK_FOLDER.resolve(uuid).resolve("default.schem").toFile();
		if (tempFile.exists() && tempFile.isFile()) {
			return BaseFileUtils.createNewInputStreamFromFile(tempFile);
		} else {
			return BaseFileUtils.createNewInputStreamFromResource("resources/default.schem");
		}
	}


	@ClassMapper(value = CommandConfirmation.class, local = true)
	private @NotNull TypeMapper<CommandConfirmation> mapCommandConfirmation() {
		return new TypeMapper<CommandConfirmation>() {
			@Override
			public CommandConfirmation map(final @NotNull String[] previousArguments, final @NotNull String s) {
				return CommandConfirmation.map(s);
			}

			@Override
			public java.util.List<String> tabCompletes(final @NotNull CommandSender commandSender, final @NotNull String[] previousArguments, final @NotNull String arg) {
				final @NotNull java.util.List<String> tabCompletions = Arrays.asList("-confirm", "-deny");
				if (commandSender instanceof Player && previousArguments.length > 0) {
					final @NotNull Player p = (Player) commandSender;
					if ((previousArguments[0].equalsIgnoreCase("del") || previousArguments[0].equalsIgnoreCase("delete"))
						&& previousArguments.length > 1
						&& CommandRequestUtils.checkDeleteBlockRequest(p.getUniqueId(), previousArguments[previousArguments.length - 1])) {
						return tabCompletions;
					} else if ((previousArguments[0].equalsIgnoreCase("delfolder") || previousArguments[0].equalsIgnoreCase("deletefolder"))
							   && previousArguments.length > 1
							   && CommandRequestUtils.checkDeleteBlockRequest(p.getUniqueId(), previousArguments[previousArguments.length - 1])) {
						return tabCompletions;
					} else if (previousArguments[0].equalsIgnoreCase("rename")
							   && previousArguments.length > 1
							   && CommandRequestUtils.checkRenameBlockRequest(p.getUniqueId(), previousArguments[previousArguments.length - 1])) {
						return tabCompletions;
					} else if (previousArguments[0].equalsIgnoreCase("renamefolder")
							   && previousArguments.length > 1
							   && CommandRequestUtils.checkRenameFolderRequest(p.getUniqueId(), previousArguments[previousArguments.length - 1])) {
						return tabCompletions;
					} else {
						return null;
					}
				} else {
					return null;
				}
			}
		};
	}

	@ClassMapper(value = MappedFolder.class, local = true)
	private @NotNull TypeMapper<MappedFolder> mapFolder() {
		return new TypeMapper<MappedFolder>() {
			@Override
			public MappedFolder map(final @NotNull String[] previous, final @NotNull String s) {
				if (TestAreaUtils.forbiddenFileName(s)) {
					return null;
				} else {
					return new MappedFolder(s);
				}
			}

			@Override
			public java.util.List<String> tabCompletes(final @NotNull CommandSender commandSender, final @NotNull String[] previousArguments, final @NotNull String arg) {
				if (commandSender instanceof Player) {
					final @NotNull Player p = (Player) commandSender;
					final int lastIndex = arg.lastIndexOf("/");
					final @NotNull String path = arg.substring(0, Math.max(lastIndex, 0));

					try {
						final @NotNull Path filePath = TestBlockCommand.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString()).resolve(path).toRealPath();
						final @NotNull Path basePath = TestBlockCommand.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString()).toRealPath();
						if (filePath.startsWith(basePath)) {
							final @NotNull List<String> results = new LinkedList<>();
							for (final @NotNull File file : Objects.notNull(BaseFileUtils.listFilesOfTypeAndFolders(filePath.toFile()))) {
								final @NotNull String fileName = FilenameUtils.separatorsToUnix(BaseFileUtils.removeExtension(basePath.relativize(file.toPath()).toString()));
								results.add(fileName);
							}
							return results;
						} else {
							return null;
						}
					} catch (final @NotNull IOException e) {
						return null;
					}
				} else {
					return null;
				}
			}
		};
	}

	@ClassMapper(value = MappedFile.class, local = true)
	private @NotNull TypeMapper<MappedFile> mapFile() {
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
}