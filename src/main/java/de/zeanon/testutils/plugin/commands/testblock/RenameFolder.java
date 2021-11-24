package de.zeanon.testutils.plugin.commands.testblock;

import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.plugin.utils.*;
import de.zeanon.testutils.plugin.utils.enums.CommandConfirmation;
import de.zeanon.testutils.plugin.utils.enums.MappedFolder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        if (TestAreaUtils.illegalName(oldMappedFolder.getName())) {
            p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
                          + ChatColor.RED + "Folder '" + oldMappedFolder.getName() + "' resolution error: Name is not allowed.");
            return;
        }

        if (TestAreaUtils.illegalName(newMappedFolder.getName())) {
            p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
                          + ChatColor.RED + "Folder '" + newMappedFolder.getName() + "' resolution error: Name is not allowed.");
            return;
        }

        try {
            final @NotNull Path filePath = TestBlockCommand.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString());
            final @NotNull File directoryOld = filePath.resolve(oldMappedFolder.getName()).toFile(); //NOSONAR
            final @NotNull File directoryNew = filePath.resolve(newMappedFolder.getName()).toFile(); //NOSONAR

            if (!directoryOld.exists() || !directoryOld.isDirectory()) {
                p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
                              + ChatColor.DARK_RED + oldMappedFolder + ChatColor.RED + " does not exist.");
                return;
            }

            if (confirmation == null) {
                CommandRequestUtils.addRenameFolderRequest(p.getUniqueId(), oldMappedFolder.getName());
                if (directoryNew.exists() && directoryNew.isDirectory()) {
                    p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
                                  + ChatColor.DARK_RED + newMappedFolder + ChatColor.RED + " already exists, the folders will be merged.");

                    p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
                                  + ChatColor.RED + "These blocks already exist in " + ChatColor.DARK_RED + newMappedFolder + ChatColor.RED + ", they will be overwritten.");
                    int id = 0;
                    for (final @NotNull File oldFile : Objects.notNull(Objects.notNull(BaseFileUtils.listFilesOfType(directoryOld, true, "schem")))) {
                        for (final @NotNull File newFile : Objects.notNull(BaseFileUtils.searchFilesOfType(directoryNew, true, BaseFileUtils.removeExtension(oldFile.getName()), "schem"))) {
                            if (BaseFileUtils.removeExtension(newFile.toPath().relativize(directoryNew.toPath()).toString())
                                             .equalsIgnoreCase(BaseFileUtils.removeExtension(oldFile.toPath().relativize(directoryOld.toPath()).toString()))) {

                                final @NotNull String shortenedRelativePath = FilenameUtils.separatorsToUnix(
                                        filePath.resolve(newMappedFolder.getName())
                                                .toRealPath()
                                                .relativize(newFile.toPath().toRealPath())
                                                .toString());

                                final @NotNull String name;
                                name = newFile.getName();
                                p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
                                              + ChatColor.DARK_RED + name
                                              + ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + shortenedRelativePath + ChatColor.DARK_GRAY + "]");
                                id++;
                            }
                        }
                    }

                    p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
                                  + ChatColor.RED + "These folders already exist in " + ChatColor.GREEN + newMappedFolder + ChatColor.RED + ", they will be merged.");
                    int i = 0;
                    for (final @NotNull File oldFolder : Objects.notNull(BaseFileUtils.listFolders(directoryOld, true))) {
                        for (final @NotNull File newFolder : Objects.notNull(BaseFileUtils.searchFolders(directoryNew, true, oldFolder.getName()))) {
                            if (BaseFileUtils.removeExtension(newFolder.toPath().relativize(directoryNew.toPath()).toString())
                                             .equalsIgnoreCase(BaseFileUtils.removeExtension(oldFolder.toPath().relativize(directoryOld.toPath()).toString()))) {

                                final @NotNull String name = newFolder.getName();
                                final @NotNull String shortenedRelativePath = FilenameUtils.separatorsToUnix(filePath.resolve(newMappedFolder.getName()).toRealPath().relativize(newFolder.toPath().toRealPath()).toString());
                                p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
                                              + ChatColor.DARK_RED + name
                                              + ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + shortenedRelativePath + ChatColor.DARK_GRAY + "]");
                                i++;
                            }
                        }
                    }
                    if (id > 0 && i > 0) {
                        p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
                                      + ChatColor.RED + "There are already " + ChatColor.DARK_PURPLE + id + ChatColor.RED
                                      + " blocks and " + ChatColor.DARK_PURPLE + i + ChatColor.RED
                                      + " folders with the same name in " + ChatColor.GREEN + newMappedFolder + ChatColor.RED + ".");
                    } else if (id > 0) {
                        p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
                                      + ChatColor.RED + "There are already " + ChatColor.DARK_PURPLE + id + ChatColor.RED
                                      + " blocks with the same name in " + ChatColor.GREEN + newMappedFolder + ChatColor.RED + ".");
                    } else if (i > 0) {
                        p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
                                      + ChatColor.RED + "There are already " + ChatColor.DARK_PURPLE + i + ChatColor.RED
                                      + " folders with the same name in " + ChatColor.GREEN + newMappedFolder + ChatColor.RED + ".");
                    }
                }
                GlobalMessageUtils.sendBooleanMessage(GlobalMessageUtils.MESSAGE_HEAD
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
                                p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
                                              + ChatColor.DARK_RED + oldMappedFolder + ChatColor.RED + " could not be renamed, for further information please see [console].");
                            }
                        } else {
                            p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
                                          + ChatColor.DARK_RED + oldMappedFolder + ChatColor.RED + " does not exist.");
                        }
                    } else {
                        CommandRequestUtils.removeRenameFolderRequest(p.getUniqueId());
                        p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
                                      + ChatColor.DARK_RED + oldMappedFolder + ChatColor.RED + " was not renamed.");
                    }
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
            p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
                          + ChatColor.RED + "An Error occurred while getting the filepaths for the blocks and folders, for further information please see [console].");
        }
    }

    public @NotNull String usageMessage() {
        return ChatColor.GRAY + "/tb"
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
               + ChatColor.GRAY + "/tb"
               + ChatColor.AQUA + " renamefolder "
               + ChatColor.GREEN + "example newname";
    }

    public @NotNull
    String usageCommand() {
        return "/tb renamefolder ";
    }

    public void usage(final @NotNull Player p) {
        GlobalMessageUtils.sendSuggestMessage(GlobalMessageUtils.MESSAGE_HEAD
                                              + ChatColor.RED + "Usage: ",
                                              RenameFolder.usageMessage(),
                                              RenameFolder.usageHoverMessage(),
                                              RenameFolder.usageCommand(), p);
    }

    private void deleteParents(final @NotNull File directory, final @NotNull String arg, final @NotNull Player p) {
        try {
            FileUtils.deleteDirectory(directory);
            final @Nullable String parentName = Objects.notNull(directory.getAbsoluteFile().getParentFile().listFiles()).length == 0
                                                && ConfigUtils.getBoolean("Delete empty Folders")
                                                ? InternalFileUtils.deleteEmptyParent(directory, TestBlockCommand.TESTBLOCK_FOLDER.resolve(p.getUniqueId().toString()).toFile())
                                                : null;

            p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
                          + ChatColor.GREEN + arg + ChatColor.RED + " was renamed successfully.");
            if (parentName != null) {
                p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
                              + ChatColor.RED + "Folder " + ChatColor.GREEN + parentName + ChatColor.RED + " was deleted successfully due to being empty.");
            }
        } catch (final IOException e) {
            p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
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
            } catch (final IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}