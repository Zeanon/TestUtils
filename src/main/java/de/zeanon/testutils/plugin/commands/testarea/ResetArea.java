package de.zeanon.testutils.plugin.commands.testarea;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import java.io.File;
import java.io.IOException;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class ResetArea {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (args.length == 1) {
					final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
					final @Nullable ProtectedRegion oppositeRegion = TestAreaUtils.getOppositeRegion(p);

					if (tempRegion == null || oppositeRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						try (final @NotNull EditSession editSession = SessionFactory.createSession(p)) {
							File tempFile = new File(TestUtils
															 .getInstance()
															 .getDataFolder()
															 .getAbsolutePath() + "/TestAreas/" + tempRegion.getId() + ".schem");
							File oppositeFile = new File(TestUtils
																 .getInstance()
																 .getDataFolder()
																 .getAbsolutePath() + "/TestAreas/" + oppositeRegion.getId() + ".schem");
							if (tempFile.exists()
								&& oppositeFile.exists()) {
								ResetArea.pasteSide(tempRegion, editSession, tempFile);
								ResetArea.pasteSide(oppositeRegion, editSession, oppositeFile);

								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "You pasted the reset for '"
											  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
							} else {
								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "There is no reset for '"
											  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
							}
						} catch (WorldEditException | IOException e) {
							e.printStackTrace();
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "There has been an error, pasting the reset for '"
										  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
						}
					}
				} else {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "Too many arguments.");
				}
			}
		}.runTask(TestUtils.getInstance());
	}

	private void pasteSide(final @NotNull ProtectedRegion tempRegion, final @NotNull EditSession editSession, final @NotNull File file) throws WorldEditException, IOException {
		try (final @NotNull ClipboardReader reader = Objects.notNull(ClipboardFormats.findByAlias("schem")).getReader(BaseFileUtils.createNewInputStreamFromFile(file))) {
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