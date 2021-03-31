package de.zeanon.testutils.plugin.utils.backup;

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
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import de.zeanon.testutils.plugin.utils.InternalFileUtils;
import de.zeanon.testutils.plugin.utils.enums.BackupMode;
import de.zeanon.testutils.plugin.utils.region.DefinedRegion;
import de.zeanon.testutils.plugin.utils.region.RegionManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@AllArgsConstructor
public abstract class Backup extends BukkitRunnable {

	@Getter
	private static final @NotNull DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH-mm-ss'#'dd-MM-yyyy");
	protected final @NotNull BackupMode sequence;

	@Override
	public void run() {
		if (ConfigUtils.getInt("Backups", this.sequence.toString()) > 0) {
			this.systemOutStart();
			try {
				final @NotNull String name = LocalDateTime.now().format(Backup.getFormatter());
				@NotNull org.bukkit.World tempWorld;
				for (final @NotNull File regionFolder : BaseFileUtils.listFolders(TestUtils.getInstance().getDataFolder().toPath().resolve("TestAreas").toRealPath().toFile())) {
					final @NotNull File backupFolder = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Backups/" + regionFolder.getName());

					final @Nullable DefinedRegion southRegion = RegionManager.getRegion(regionFolder.getName() + "_south");
					final @Nullable DefinedRegion northRegion = RegionManager.getRegion(regionFolder.getName() + "_north");
					if (southRegion != null && northRegion != null) {
						if (this.doBackup(southRegion, northRegion)) {
							tempWorld = southRegion.getWorld();
							final @NotNull File folder = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Backups/" + regionFolder.getName() + "/" + this.sequence.getPath(null) + "/" + name);
							this.backupSide(tempWorld, southRegion, folder);
							southRegion.setHasChanged(false);
							this.backupSide(tempWorld, northRegion, folder);
							northRegion.setHasChanged(false);

							Backup.this.cleanup(backupFolder);
						}
					} else {
						FileUtils.deleteDirectory(regionFolder);
						InternalFileUtils.deleteEmptyParent(regionFolder, new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/TestAreas"));

						if (backupFolder.exists()) {
							FileUtils.deleteDirectory(backupFolder);
							InternalFileUtils.deleteEmptyParent(backupFolder, new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Backups"));
						}
					}
				}
				this.systemOutDone();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void backupSide(final @NotNull World tempWorld, final @NotNull DefinedRegion tempRegion, final @NotNull File folder) {
		final @NotNull BukkitWorld bukkitWorld = new BukkitWorld(tempWorld);
		final @NotNull CuboidRegion region = new CuboidRegion(bukkitWorld, tempRegion.getMinimumPoint().toBlockVector3(), tempRegion.getMaximumPoint().toBlockVector3());
		final @NotNull BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

		final @NotNull BlockVector3 copyPoint = region.getMinimumPoint();

		try (final @NotNull EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(bukkitWorld, -1)) {
			final @NotNull ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
					editSession, region, clipboard, copyPoint
			);

			forwardExtentCopy.setCopyingEntities(false);
			forwardExtentCopy.setCopyingBiomes(false);

			Operations.complete(forwardExtentCopy);

			final @NotNull File tempFile = new File(folder, tempRegion.getName().substring(tempRegion.getName().length() - 5) + ".schem");

			BaseFileUtils.createFile(tempFile);

			try (final @NotNull ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(tempFile))) {
				writer.write(clipboard);
			}
		} catch (IOException | WorldEditException exception) {
			exception.printStackTrace();
		}
	}

	public void pasteSide(final @NotNull DefinedRegion tempRegion, final @NotNull EditSession editSession, final @NotNull File file) throws IOException, WorldEditException { //NOSONAR
		try (final @NotNull ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(BaseFileUtils.createNewInputStreamFromFile(file))) {
			final @NotNull Clipboard clipboard = reader.read();

			final @NotNull BlockVector3 pastePoint = tempRegion.getMinimumPoint().toBlockVector3();

			final @NotNull ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);

			Operation operation = clipboardHolder
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
}