package de.zeanon.testutils.plugin.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.utils.enums.BackUpMode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


@AllArgsConstructor
public class BackUp implements Runnable {

	private final @NotNull BackUpMode sequence;

	@Override
	public void run() {
		try {
			final @NotNull String name = InitMode.getFormatter().format(System.currentTimeMillis());
			@NotNull RegionManager tempManager;
			@NotNull World tempWorld;
			for (final @NotNull File worldFolder : BaseFileUtils.listFolders(new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/TestAreas"))) {
				tempWorld = new BukkitWorld(Bukkit.getWorld(worldFolder.getName()));
				tempManager = Objects.notNull(InitMode.getRegionContainer().get(tempWorld));
				for (final @NotNull File regionFolder : BaseFileUtils.listFolders(worldFolder)) {
					final @NotNull File backupFolder = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps/" + worldFolder.getName() + "/" + regionFolder.getName());

					if (tempManager.hasRegion("testarea_" + regionFolder.getName() + "_north") && tempManager.hasRegion("testarea_" + regionFolder.getName() + "_south")) {
						if (this.sequence == BackUpMode.DAILY) {
							final @NotNull File dailyBackup = new File(backupFolder, this.sequence.getPath());
							if (dailyBackup.exists()) {
								@NotNull List<File> files = BaseFileUtils.listFolders(dailyBackup);
								while (files.size() > ConfigUtils.getInt("Backups", "daily") - 1) {
									final @NotNull Optional<File> toBeDeleted = files.stream().min(Comparator.comparingLong(File::lastModified));
									if (toBeDeleted.isPresent()) {
										FileUtils.deleteDirectory(toBeDeleted.get()); //NOSONAR
										InternalFileUtils.deleteEmptyParent(toBeDeleted.get(), new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps"));
										files = BaseFileUtils.listFolders(dailyBackup);
									}
								}
							}
						} else if (this.sequence == BackUpMode.HOURLY) {
							final @NotNull File hourlyBackup = new File(backupFolder, this.sequence.getPath());
							if (hourlyBackup.exists()) {
								@NotNull List<File> files = BaseFileUtils.listFolders(hourlyBackup);
								while (files.size() > ConfigUtils.getInt("Backups", "hourly") - 1) {
									final @NotNull Optional<File> toBeDeleted = files.stream().min(Comparator.comparingLong(File::lastModified));
									if (toBeDeleted.isPresent()) {
										FileUtils.deleteDirectory(toBeDeleted.get()); //NOSONAR
										InternalFileUtils.deleteEmptyParent(toBeDeleted.get(), new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps"));
										files = BaseFileUtils.listFolders(hourlyBackup);
									}
								}
							}
						} else if (this.sequence == BackUpMode.STARTUP) {
							final @NotNull File startupBackup = new File(backupFolder, this.sequence.getPath());
							if (startupBackup.exists()) {
								@NotNull List<File> files = BaseFileUtils.listFolders(startupBackup);
								while (files.size() > ConfigUtils.getInt("Backups", "startup") - 1) {
									final @NotNull Optional<File> toBeDeleted = files.stream().min(Comparator.comparingLong(File::lastModified));
									if (toBeDeleted.isPresent()) {
										FileUtils.deleteDirectory(toBeDeleted.get()); //NOSONAR
										InternalFileUtils.deleteEmptyParent(toBeDeleted.get(), new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps"));
										files = BaseFileUtils.listFolders(startupBackup);
									}
								}
							}
						}

						this.backupSide(tempWorld, Objects.notNull(tempManager.getRegion("testarea_" + regionFolder.getName() + "_north")), this.sequence.getPath(), name);
						this.backupSide(tempWorld, Objects.notNull(tempManager.getRegion("testarea_" + regionFolder.getName() + "_south")), this.sequence.getPath(), name);
					} else {
						FileUtils.deleteDirectory(regionFolder);
						InternalFileUtils.deleteEmptyParent(regionFolder, new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps"));

						if (backupFolder.exists()) {
							FileUtils.deleteDirectory(backupFolder);
							InternalFileUtils.deleteEmptyParent(backupFolder, new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps"));
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void backupSide(final @NotNull World tempWorld, final @NotNull ProtectedRegion tempRegion, final @NotNull String folder, final @NotNull String date) {
		new BukkitRunnable() {
			@Override
			public void run() {
				final @NotNull CuboidRegion region = new CuboidRegion(tempWorld, tempRegion.getMinimumPoint(), tempRegion.getMaximumPoint());
				final @NotNull BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

				final @NotNull BlockVector3 copyPoint = region.getMinimumPoint();

				try (final @NotNull EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(tempWorld, -1)) {
					final @NotNull ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
							editSession, region, clipboard, copyPoint
					);

					forwardExtentCopy.setCopyingEntities(false);
					forwardExtentCopy.setCopyingBiomes(false);

					Operations.complete(forwardExtentCopy);

					final @NotNull File tempFile = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps/" + tempWorld.getName() + "/" + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + "/" + folder + "/" + date + "/" + tempRegion.getId().substring(tempRegion.getId().length() - 5) + ".schem");

					BaseFileUtils.createFile(tempFile);

					try (final @NotNull ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(tempFile))) {
						writer.write(clipboard);
					}
				} catch (IOException | WorldEditException exception) {
					exception.printStackTrace();
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}

	public void pasteSide(final @NotNull ProtectedRegion tempRegion, final @NotNull EditSession editSession, final @NotNull File file) throws IOException, WorldEditException { //NOSONAR
		try (final @NotNull ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(BaseFileUtils.createNewInputStreamFromFile(file))) {
			final @NotNull Clipboard clipboard = reader.read();

			final @NotNull BlockVector3 pastePoint = tempRegion.getMinimumPoint();

			final @NotNull ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);

			Operation operation = clipboardHolder
					.createPaste(editSession)
					.to(pastePoint)
					.ignoreAirBlocks(false)
					.build();

			Operations.complete(operation);
		}
	}
}