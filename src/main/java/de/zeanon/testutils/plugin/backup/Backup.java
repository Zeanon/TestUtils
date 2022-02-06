package de.zeanon.testutils.plugin.backup;

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
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.commands.backup.BackupCommand;
import de.zeanon.testutils.plugin.commands.testutils.TestUtilsCommand;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import de.zeanon.testutils.plugin.utils.InternalFileUtils;
import de.zeanon.testutils.plugin.utils.enums.BackupMode;
import de.zeanon.testutils.regionsystem.RegionManager;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import de.zeanon.testutils.regionsystem.tags.Tag;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.logging.Level;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@AllArgsConstructor
public abstract class Backup extends BukkitRunnable {

	@Getter
	private static final @NotNull DateTimeFormatter formatter;

	static {
		formatter = DateTimeFormatter.ofPattern("HH-mm-ss'#'dd-MM-yyyy");
	}

	protected final @NotNull BackupMode sequence;

	@Override
	public void run() {
		if (ConfigUtils.getInt("Backups", Backup.this.sequence.toString()) > 0) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Backup.this.execute();
				}
			}.runTask(TestUtils.getInstance());
		}
	}

	public void execute() {
		Backup.this.systemOutStart();
		try {
			final @NotNull String name = LocalDateTime.now().format(Backup.getFormatter());
			final @NotNull File[] folder = new File[1];
			final @NotNull File[] backupFolder = new File[1];
			final @NotNull World[] tempWorld = new World[1];
			final @NotNull RegionStorage[] regionStorage = new RegionStorage[1];

			final @NotNull Iterator<RegionStorage> regions = BaseFileUtils.listFolders(TestUtilsCommand.TESTAREA_FOLDER.toRealPath().toFile())
																		  .stream()
																		  .map(regionFolder -> new RegionStorage(RegionManager.getDefinedRegion(regionFolder.getName() + "_south"), RegionManager.getDefinedRegion(regionFolder.getName() + "_north"), regionFolder))
																		  .filter(internalRegionStorage -> {
																			  if (internalRegionStorage.southRegion != null && internalRegionStorage.northRegion != null) {
																				  return true;
																			  } else {
																				  new BukkitRunnable() {
																					  @Override
																					  public void run() {
																						  try {
																							  backupFolder[0] = BackupCommand.BACKUP_FOLDER.resolve(internalRegionStorage.regionFolder.getName()).toFile();
																							  if (internalRegionStorage.regionFolder.exists() && internalRegionStorage.regionFolder.isDirectory()) {
																								  BaseFileUtils.deleteDirectory(internalRegionStorage.regionFolder);
																								  InternalFileUtils.deleteEmptyParent(internalRegionStorage.regionFolder, TestUtilsCommand.TESTAREA_FOLDER.toFile());
																							  }

																							  if (backupFolder[0].exists() && backupFolder[0].isDirectory()) {
																								  BaseFileUtils.deleteDirectory(backupFolder[0]);
																								  InternalFileUtils.deleteEmptyParent(backupFolder[0], BackupCommand.BACKUP_FOLDER.toFile());
																							  }
																						  } catch (final @NotNull IOException e) {
																							  throw new UncheckedIOException(e);
																						  }
																					  }
																				  }.runTaskAsynchronously(TestUtils.getInstance());
																				  return false;
																			  }
																		  })
																		  .filter(internalRegionStorage -> Backup.this.doBackup(internalRegionStorage.southRegion, internalRegionStorage.northRegion))
																		  .iterator();
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!regions.hasNext()) {
						this.cancel();
						return;
					}

					regionStorage[0] = regions.next();
					backupFolder[0] = BackupCommand.BACKUP_FOLDER.resolve(regionStorage[0].regionFolder.getName()).toFile();
					tempWorld[0] = Objects.notNull(regionStorage[0].southRegion).getWorld();
					folder[0] = BackupCommand.BACKUP_FOLDER.resolve(regionStorage[0].regionFolder.getName()).resolve(Backup.this.sequence.getPath(null)).resolve(name).toFile();

					Backup.this.backupSide(tempWorld[0], regionStorage[0].southRegion, folder[0]);
					regionStorage[0].southRegion.removeTag(Tag.CHANGED);
					Backup.this.backupSide(tempWorld[0], Objects.notNull(regionStorage[0].northRegion), folder[0]);
					regionStorage[0].northRegion.removeTag(Tag.CHANGED);

					new BukkitRunnable() {
						@Override
						public void run() {
							Backup.this.cleanup(backupFolder[0]);
						}
					}.runTaskAsynchronously(TestUtils.getInstance());
				}
			}.runTaskTimer(TestUtils.getInstance(), 0, 20);

			Backup.this.systemOutDone();
		} catch (final @NotNull IOException | UncheckedIOException e) {
			TestUtils.getChatLogger().log(Level.SEVERE, "Error while backing up", e);
		}
	}

	public void backupSide(final @NotNull World tempWorld, final @NotNull DefinedRegion tempRegion, final @NotNull File folder) {
		final @NotNull BukkitWorld bukkitWorld = new BukkitWorld(tempWorld);
		final @NotNull CuboidRegion region = new CuboidRegion(bukkitWorld, tempRegion.getMinimumPoint().toBlockVector3(), tempRegion.getMaximumPoint().toBlockVector3());
		final @NotNull BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

		try (final @NotNull EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(bukkitWorld, -1)) {
			final @NotNull ForwardExtentCopy copy = new ForwardExtentCopy(
					editSession, region, clipboard, region.getMinimumPoint()
			);

			copy.setCopyingEntities(false);
			copy.setCopyingBiomes(false);

			Operations.complete(copy);

			final @NotNull File tempFile = new File(folder, tempRegion.getName().substring(tempRegion.getName().length() - 5) + ".schem");

			BaseFileUtils.createFile(tempFile);

			try (final @NotNull ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(tempFile))) {
				writer.write(clipboard);
			}
		} catch (final @NotNull IOException | WorldEditException exception) {
			exception.printStackTrace();
		}
	}

	public void pasteSide(final @NotNull DefinedRegion tempRegion, final @NotNull EditSession editSession, final @NotNull File file) throws IOException, WorldEditException { //NOSONAR
		try (final @NotNull ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(BaseFileUtils.createNewInputStreamFromFile(file))) {
			final @NotNull Clipboard clipboard = reader.read();

			final @NotNull BlockVector3 pastePoint = tempRegion.getMinimumPoint().toBlockVector3();

			final @NotNull ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);

			final Operation operation = clipboardHolder
					.createPaste(editSession)
					.to(pastePoint)
					.ignoreAirBlocks(false)
					.build();

			Operations.complete(operation);
		}
	}


	protected abstract void cleanup(final @NotNull File backupFolder);

	protected abstract void systemOutStart();

	protected abstract void systemOutDone();

	protected abstract boolean doBackup(final @NotNull DefinedRegion southRegion, final @NotNull DefinedRegion northRegion);


	@AllArgsConstructor
	private static class RegionStorage {

		final @Nullable DefinedRegion southRegion;
		final @Nullable DefinedRegion northRegion;
		final @NotNull File regionFolder;
	}
}