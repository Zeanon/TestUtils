package de.zeanon.testutils.plugin.commands.testblock;

import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.CommandRequestUtils;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.InternalFileUtils;
import de.zeanon.testutils.plugin.utils.enums.CommandConfirmation;
import de.zeanon.testutils.plugin.utils.enums.MappedFolder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class RenameFolder {

	public void execute(final @NotNull MappedFolder oldMappedFolder, final @NotNull MappedFolder newMappedFolder, final @Nullable CommandConfirmation confirmation, final @NotNull Player p) {
		try {
			final @NotNull Path filePath = Paths.get(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/TestBlocks/" + p.getUniqueId().toString());
			final @NotNull File directoryOld = filePath.resolve(oldMappedFolder.getName()).toFile(); //NOSONAR
			final @NotNull File directoryNew = filePath.resolve(newMappedFolder.getName()).toFile(); //NOSONAR
			if (oldMappedFolder.getName().contains("./")) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "File '" + oldMappedFolder.getName() + "' resolution error: Path is not allowed.");
			} else if (newMappedFolder.getName().contains("./")) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "File '" + newMappedFolder.getName() + "' resolution error: Path is not allowed.");
			} else if (!directoryOld.exists() || !directoryOld.isDirectory()) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.DARK_RED + oldMappedFolder + ChatColor.RED + " does not exist.");
			} else if (confirmation == null) {
				CommandRequestUtils.addRenameFolderRequest(p.getUniqueId(), oldMappedFolder.getName());
				if (directoryNew.exists() && directoryNew.isDirectory()) {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.DARK_RED + newMappedFolder + ChatColor.RED + " already exists, the folders will be merged.");

					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "These blocks already exist in " + ChatColor.DARK_RED + newMappedFolder + ChatColor.RED + ", they will be overwritten.");
					int id = 0;
					for (final @NotNull File oldFile : BaseFileUtils.listFilesOfType(directoryOld, true, "schem")) {
						for (final @NotNull File newFile : BaseFileUtils.searchFilesOfType(directoryNew, true, BaseFileUtils.removeExtension(oldFile.getName()), "schem")) {
							if (BaseFileUtils.removeExtension(newFile.toPath().relativize(directoryNew.toPath()).toString())
											 .equalsIgnoreCase(BaseFileUtils.removeExtension(oldFile.toPath().relativize(directoryOld.toPath()).toString()))) {

								final @NotNull String shortenedRelativePath = FilenameUtils.separatorsToUnix(
										filePath.resolve(newMappedFolder.getName())
												.toRealPath()
												.relativize(newFile.toPath().toRealPath())
												.toString());

								final @NotNull String name;
								name = newFile.getName();
								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.DARK_RED + name
											  + ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + shortenedRelativePath + ChatColor.DARK_GRAY + "]");
								id++;
							}
						}
					}

					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "These folders already exist in " + ChatColor.GREEN + newMappedFolder + ChatColor.RED + ", they will be merged.");
					int i = 0;
					for (final @NotNull File oldFolder : BaseFileUtils.listFolders(directoryOld, true)) {
						for (final @NotNull File newFolder : BaseFileUtils.searchFolders(directoryNew, true, oldFolder.getName())) {
							if (BaseFileUtils.removeExtension(newFolder.toPath().relativize(directoryNew.toPath()).toString())
											 .equalsIgnoreCase(BaseFileUtils.removeExtension(oldFolder.toPath().relativize(directoryOld.toPath()).toString()))) {

								final @NotNull String name = newFolder.getName();
								final @NotNull String shortenedRelativePath = FilenameUtils.separatorsToUnix(filePath.resolve(newMappedFolder.getName()).toRealPath().relativize(newFolder.toPath().toRealPath()).toString());
								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.DARK_RED + name
											  + ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + shortenedRelativePath + ChatColor.DARK_GRAY + "]");
								i++;
							}
						}
					}
					if (id > 0 && i > 0) {
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "There are already " + ChatColor.DARK_PURPLE + id + ChatColor.RED
									  + " blocks and " + ChatColor.DARK_PURPLE + i + ChatColor.RED
									  + " folders with the same name in " + ChatColor.GREEN + newMappedFolder + ChatColor.RED + ".");
					} else if (id > 0) {
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "There are already " + ChatColor.DARK_PURPLE + id + ChatColor.RED
									  + " blocks with the same name in " + ChatColor.GREEN + newMappedFolder + ChatColor.RED + ".");
					} else if (i > 0) {
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "There are already " + ChatColor.DARK_PURPLE + i + ChatColor.RED
									  + " folders with the same name in " + ChatColor.GREEN + newMappedFolder + ChatColor.RED + ".");
					}
				}
				GlobalMessageUtils.sendBooleanMessage(GlobalMessageUtils.messageHead
													  + ChatColor.RED + "Do you really want to rename "
													  + ChatColor.DARK_RED + oldMappedFolder.getName()
													  + ChatColor.RED + "?",
													  "/tu renamefolder " + oldMappedFolder + " " + newMappedFolder + " -confirm",
													  "/tu renamefolder " + oldMappedFolder + " " + newMappedFolder + " -deny", p);
			} else {
				if (CommandRequestUtils.checkRenameFolderRequest(p.getUniqueId(), oldMappedFolder.getName())) {
					CommandRequestUtils.removeRenameFolderRequest(p.getUniqueId());
					if (confirmation.confirm()) { //NOSONAR
						CommandRequestUtils.removeRenameFolderRequest(p.getUniqueId());
						if (directoryOld.exists() && directoryOld.isDirectory()) {
							if (RenameFolder.deepMerge(directoryOld, directoryNew)) {
								RenameFolder.deleteParents(directoryOld, oldMappedFolder.getName(), p);
							} else {
								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.DARK_RED + oldMappedFolder + ChatColor.RED + " could not be renamed, for further information please see [console].");
							}
						} else {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.DARK_RED + oldMappedFolder + ChatColor.RED + " does not exist.");
						}
					} else {
						CommandRequestUtils.removeRenameFolderRequest(p.getUniqueId());
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.DARK_RED + oldMappedFolder + ChatColor.RED + " was not renamed.");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "An Error occurred while getting the filepaths for the blocks and folders, for further information please see [console].");
		}
	}

	public @NotNull String usageMessage() {
		return ChatColor.GRAY + "/testutils"
			   + ChatColor.AQUA + " renamefolder "
			   + ChatColor.YELLOW + "<"
			   + ChatColor.GREEN + "foldername"
			   + ChatColor.YELLOW + "> <"
			   + ChatColor.GREEN + "newname"
			   + ChatColor.YELLOW + ">";
	}

	public @NotNull
	String usageHoverMessage() {
		return ChatColor.RED + "e.g. "
			   + ChatColor.GRAY + "/testutils"
			   + ChatColor.AQUA + " renamefolder "
			   + ChatColor.GREEN + "example newname";
	}

	public @NotNull
	String usageCommand() {
		return "/testutils renamefolder ";
	}

	public void usage(final @NotNull Player p) {
		GlobalMessageUtils.sendSuggestMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "Usage: ",
											  RenameFolder.usageMessage(),
											  RenameFolder.usageHoverMessage(),
											  RenameFolder.usageCommand(), p);
	}

	@SuppressWarnings("DuplicatedCode")
	private void executeInternally(final @NotNull Player p, final @NotNull String[] args) {
		try {
			final @NotNull Path filePath = Paths.get(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/TestBlocks/" + p.getUniqueId().toString());
			final @NotNull File directoryOld = filePath.resolve(args[1]).toFile(); //NOSONAR
			final @NotNull File directoryNew = filePath.resolve(args[2]).toFile(); //NOSONAR

			if (args.length == 4) {
				if (!directoryOld.exists() || !directoryOld.isDirectory()) {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.GREEN + args[1] + ChatColor.RED + " does not exist.");
					return;
				} else if (directoryNew.exists() && directoryNew.isDirectory()) {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.GREEN + args[2] + ChatColor.RED + " already exists, the folders will be merged.");

					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "These blocks already exist in " + ChatColor.GREEN + args[2] + ChatColor.RED + ", they will be overwritten.");
					int id = 0;
					for (final @NotNull File oldFile : BaseFileUtils.listFilesOfType(directoryOld, true, "schem")) {
						for (final @NotNull File newFile : BaseFileUtils.searchFilesOfType(directoryNew, true, BaseFileUtils.removeExtension(oldFile.getName()), "schem")) {
							if (BaseFileUtils.removeExtension(newFile.toPath().relativize(directoryNew.toPath()).toString())
											 .equalsIgnoreCase(BaseFileUtils.removeExtension(oldFile.toPath().relativize(directoryOld.toPath()).toString()))) {

								final @NotNull String shortenedRelativePath = FilenameUtils.separatorsToUnix(
										filePath.resolve(args[2])
												.toRealPath()
												.relativize(newFile.toPath().toRealPath())
												.toString());

								final @NotNull String name;
								name = newFile.getName();
								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + (id + 1) + ": "
											  + ChatColor.GOLD + name
											  + ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + shortenedRelativePath + ChatColor.DARK_GRAY + "]");
								id++;
							}
						}
					}

					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "These folders already exist in " + ChatColor.GREEN + args[2] + ChatColor.RED + ", they will be merged.");
					int i = 0;
					for (final @NotNull File oldFolder : BaseFileUtils.listFolders(directoryOld, true)) {
						for (final @NotNull File newFolder : BaseFileUtils.searchFolders(directoryNew, true, oldFolder.getName())) {
							if (BaseFileUtils.removeExtension(newFolder.toPath().relativize(directoryNew.toPath()).toString())
											 .equalsIgnoreCase(BaseFileUtils.removeExtension(oldFolder.toPath().relativize(directoryOld.toPath()).toString()))) {

								final @NotNull String name = newFolder.getName();
								final @NotNull String shortenedRelativePath = FilenameUtils.separatorsToUnix(filePath.resolve(args[2]).toRealPath().relativize(newFolder.toPath().toRealPath()).toString());
								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + (i + 1) + ": "
											  + ChatColor.GREEN + name
											  + ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + shortenedRelativePath + ChatColor.DARK_GRAY + "]");
								i++;
							}
						}
					}
					if (id > 0 && i > 0) {
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "There are already " + ChatColor.DARK_PURPLE + id + ChatColor.RED
									  + " blocks and " + ChatColor.DARK_PURPLE + i + ChatColor.RED
									  + " folders with the same name in " + ChatColor.GREEN + args[2] + ChatColor.RED + ".");
					} else if (id > 0) {
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "There are already " + ChatColor.DARK_PURPLE + id + ChatColor.RED
									  + " blocks with the same name in " + ChatColor.GREEN + args[2] + ChatColor.RED + ".");
					} else if (i > 0) {
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "There are already " + ChatColor.DARK_PURPLE + i + ChatColor.RED
									  + " folders with the same name in " + ChatColor.GREEN + args[2] + ChatColor.RED + ".");
					}
				}
				GlobalMessageUtils.sendBooleanMessage(GlobalMessageUtils.messageHead
													  + ChatColor.RED + "Do you really want to rename " + ChatColor.GREEN + args[1] + ChatColor.RED + "?",
													  "/tu " + args[1] + " " + args[2] + " -confirm",
													  "/tu " + args[1] + " " + args[2] + " -deny", p);
				CommandRequestUtils.addRenameFolderRequest(p.getUniqueId(), args[1]);
			} else if (args.length == 5 && CommandRequestUtils.checkRenameFolderRequest(p.getUniqueId(), args[1])) {
				if (args[3].equalsIgnoreCase("-confirm")) {
					CommandRequestUtils.removeRenameFolderRequest(p.getUniqueId());
					if (directoryOld.exists() && directoryOld.isDirectory()) {
						if (RenameFolder.deepMerge(directoryOld, directoryNew)) {
							RenameFolder.deleteParents(directoryOld, args[1], p);
						} else {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.GREEN + args[1] + ChatColor.RED + " could not be renamed, for further information please see [console].");
						}
					} else {
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.GREEN + args[1] + ChatColor.RED + " does not exist.");
					}
				} else if (args[3].equalsIgnoreCase("-deny")) {
					CommandRequestUtils.removeRenameFolderRequest(p.getUniqueId());
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.GREEN + args[1] + ChatColor.RED + " was not renamed");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "An Error occurred while getting the filepaths for the blocks and folders, for further information please see [console].");
		}
	}

	private void deleteParents(final @NotNull File directory, final @NotNull String arg, final @NotNull Player p) {
		try {
			FileUtils.deleteDirectory(directory);
			@Nullable String parentName = Objects.notNull(directory.getAbsoluteFile().getParentFile().listFiles()).length == 0
										  && ConfigUtils.getBoolean("Delete empty Folders") ? InternalFileUtils.deleteEmptyParent(directory, new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString())) : null;

			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.GREEN + arg + ChatColor.RED + " was renamed successfully.");
			if (parentName != null) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Folder " + ChatColor.GREEN + parentName + ChatColor.RED + " was deleted successfully due to being empty.");
			}
		} catch (IOException e) {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.GREEN + arg + ChatColor.RED + " could not be renamed, for further information please see [console].");
			e.printStackTrace();
			CommandRequestUtils.removeRenameFolderRequest(p.getUniqueId());
		}
	}

	private boolean deepMerge(final @NotNull File oldFile, final @NotNull File newFile) {
		if (Objects.notNull(oldFile.listFiles()).length != 0) {
			try {
				for (final @NotNull File tempFile : Objects.notNull(oldFile.listFiles())) {
					if (new File(newFile, tempFile.getName()).exists()) {
						if (tempFile.isDirectory()) {
							if (!RenameFolder.deepMerge(tempFile, new File(newFile, tempFile.getName()))) {
								return false;
							}
						} else {
							Files.delete(new File(newFile, tempFile.getName()).toPath());
							FileUtils.moveToDirectory(tempFile, newFile, true);
						}
					} else {
						FileUtils.moveToDirectory(tempFile, newFile, true);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}