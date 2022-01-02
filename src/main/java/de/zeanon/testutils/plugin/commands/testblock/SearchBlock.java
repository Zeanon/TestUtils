package de.zeanon.testutils.plugin.commands.testblock;

import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.CaseSensitive;
import de.zeanon.testutils.plugin.utils.enums.DeepSearch;
import de.zeanon.testutils.plugin.utils.enums.MappedFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class SearchBlock {

	public void execute(final @NotNull Player p, final @Nullable MappedFile mappedFile, final @NotNull DeepSearch deepSearch, final @NotNull CaseSensitive caseSensitive, final int page, final @Nullable String sequence) {
		if (mappedFile != null && TestAreaUtils.illegalName(mappedFile.getName())) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Folder '" + mappedFile.getName() + "' resolution error: Name is not allowed.");
			return;
		}

		int listmax = ConfigUtils.getInt("Listmax");
		final boolean spaceLists = ConfigUtils.getBoolean("Space Lists");

		try {
			final @NotNull Path folderPath = mappedFile == null ? TestBlockCommand.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString())
																: TestBlockCommand.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString()).resolve(mappedFile.getName());
			if (!folderPath.toFile().isDirectory()) {
				if (mappedFile == null) {
					throw new IOException("Schematic folder does not exist");
				}
				p.sendMessage(TestBlockCommand.MESSAGE_HEAD + ChatColor.DARK_GREEN + mappedFile.getName() + ChatColor.RED + " is no folder.");
				return;
			}

			final @Nullable java.util.List<File> list = BaseFileUtils.searchFilesOfType(folderPath.toFile(), deepSearch.confirm(), sequence, caseSensitive.confirm(), "schem");

			final double count = list == null ? 0 : list.size();
			final int pageAmount = (int) (((count / listmax) % 1 != 0) ? (count / listmax) + 1 : (count / listmax));
			final String baseFolder = mappedFile == null ? "default" : "default/" + mappedFile.getName();

			if (spaceLists) {
				p.sendMessage("");
			}

			if (count < 1) {
				GlobalMessageUtils.sendHoverMessage(TestBlockCommand.MESSAGE_HEAD
													+ ChatColor.RED + "=== ",
													ChatColor.DARK_RED + "No blocks found",
													ChatColor.RED + " === "
													+ TestBlockCommand.MESSAGE_HEAD,
													ChatColor.GRAY + baseFolder, p);
			} else {
				GlobalMessageUtils.sendHoverMessage(TestBlockCommand.MESSAGE_HEAD
													+ ChatColor.RED + "=== ",
													ChatColor.DARK_RED + "" + (int) count + " Blocks | Page " + page + "/" + pageAmount,
													ChatColor.RED + " === "
													+ TestBlockCommand.MESSAGE_HEAD,
													ChatColor.GRAY + baseFolder, p);
				if (count < listmax) {
					listmax = (int) count;
				}

				int id = (page - 1) * listmax;

				if (count < listmax * page) {
					listmax = (int) count - (listmax * (page - 1));
				}

				for (int i = 0; i < listmax; i++) {
					if (SearchBlock.sendListLineFailed(p, folderPath, folderPath, Objects.notNull(list).get(id), id, deepSearch.confirm())) {
						return;
					}
					id++;
				}

				final int nextPage = page >= pageAmount ? 1 : page + 1;
				final int previousPage = (page <= 1 ? pageAmount : page - 1);
				if (pageAmount > 1) {
					GlobalMessageUtils.sendScrollMessage(TestBlockCommand.MESSAGE_HEAD,
														 "/tb list " + nextPage,
														 "/tb list " + previousPage,
														 ChatColor.DARK_PURPLE + "Page " + nextPage,
														 ChatColor.DARK_PURPLE + "Page " + previousPage, p,
														 ChatColor.DARK_RED);
				} else {
					GlobalMessageUtils.sendScrollMessage(TestBlockCommand.MESSAGE_HEAD,
														 "",
														 "",
														 ChatColor.DARK_PURPLE + "There is only one page of blocks in this list",
														 ChatColor.DARK_PURPLE + "There is only one page of blocks in this list", p,
														 ChatColor.GRAY);
				}
			}
		} catch (final @NotNull IOException e) {
			p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "Could not access blocks folder, for further information please see [console].");
			TestUtils.getChatLogger().log(Level.SEVERE, "Error while accessing the blocks folder", e);
		}
	}

	public @NotNull String searchUsageMessage() {
		return ChatColor.GRAY + "/tb"
			   + ChatColor.AQUA + " searchblock "
			   + ChatColor.YELLOW + "<"
			   + ChatColor.DARK_RED + "filename"
			   + ChatColor.YELLOW + ">";
	}

	public @NotNull String searchUsageHoverMessage() {
		return ChatColor.RED + "e.g. "
			   + ChatColor.GRAY + "/tb"
			   + ChatColor.AQUA + " searchblock "
			   + ChatColor.DARK_RED + "example";
	}

	public @NotNull String searchUsageCommand() {
		return "/tb searchblock ";
	}

	public void searchUsage(final @NotNull Player p) {
		GlobalMessageUtils.sendSuggestMessage(GlobalMessageUtils.MESSAGE_HEAD
											  + ChatColor.RED + "Usage: ",
											  SearchBlock.searchUsageMessage(),
											  SearchBlock.searchUsageHoverMessage(),
											  SearchBlock.searchUsageCommand(), p);
	}

	public @NotNull String listUsageMessage() {
		return ChatColor.GRAY + "/tb"
			   + ChatColor.AQUA + " listblock "
			   + ChatColor.YELLOW + "<"
			   + ChatColor.DARK_RED + "filename"
			   + ChatColor.YELLOW + ">";
	}

	public @NotNull String listUsageHoverMessage() {
		return ChatColor.RED + "e.g. "
			   + ChatColor.GRAY + "/tb"
			   + ChatColor.AQUA + " listblock "
			   + ChatColor.DARK_RED + "example";
	}

	public @NotNull String listUsageCommand() {
		return "/tb listblock ";
	}

	public void listUsage(final @NotNull Player p) {
		GlobalMessageUtils.sendSuggestMessage(GlobalMessageUtils.MESSAGE_HEAD
											  + ChatColor.RED + "Usage: ",
											  SearchBlock.listUsageMessage(),
											  SearchBlock.listUsageHoverMessage(),
											  SearchBlock.listUsageCommand(), p);
	}

	private boolean sendListLineFailed(final @NotNull Player p, final @NotNull Path schemFolderPath, final @NotNull Path listPath, final @NotNull File file, final int id, final boolean deepSearch) {
		try {
			final @NotNull String name = BaseFileUtils.removeExtension(file.getName());
			final @Nullable String path = BaseFileUtils.removeExtension(FilenameUtils.separatorsToUnix(schemFolderPath.toRealPath().relativize(file.toPath().toRealPath()).toString()));
			final @Nullable String shortenedRelativePath = deepSearch
														   ? BaseFileUtils.removeExtension(FilenameUtils.separatorsToUnix(listPath.relativize(file.toPath().toRealPath()).toString()))
														   : null;

			if (deepSearch) {
				GlobalMessageUtils.sendCommandMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + (id + 1) + ChatColor.DARK_GRAY + "]: ",
													  ChatColor.DARK_RED + name + ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + shortenedRelativePath + ChatColor.DARK_GRAY + "]",
													  ChatColor.RED + "paste " + ChatColor.DARK_RED + path + ChatColor.RED + " on the opposite side.",
													  "/tb " + path, p);
			} else {
				GlobalMessageUtils.sendCommandMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + (id + 1) + ChatColor.DARK_GRAY + "]: ",
													  ChatColor.DARK_RED + name,
													  ChatColor.RED + "paste " + ChatColor.DARK_RED + path + ChatColor.RED + " on the opposite side.",
													  "/tb " + path, p);
			}
			return false;
		} catch (final @NotNull IOException e) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD +
						  ChatColor.RED + "An Error occurred while getting the filepaths for the schematics, for further information please see [console].");
			TestUtils.getChatLogger().log(Level.SEVERE, "Error while getting the filepaths for the schematics", e);
			return true;
		}
	}
}