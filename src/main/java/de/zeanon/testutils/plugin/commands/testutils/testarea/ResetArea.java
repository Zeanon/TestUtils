package de.zeanon.testutils.plugin.commands.testutils.testarea;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.plugin.commands.testutils.TestUtilsCommand;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.RegionSide;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.io.File;
import java.io.IOException;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class ResetArea {

	public void execute(final @Nullable RegionSide regionSide, final @NotNull Player p) {
		if (regionSide == null) {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p);
			final @Nullable DefinedRegion oppositeRegion = TestAreaUtils.getOppositeRegion(p);

			if (tempRegion == null || oppositeRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				return;
			}

			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Pasting the reset for '"
						  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'...");

			try (final @NotNull EditSession editSession = SessionFactory.createSession(p, new BukkitWorld(p.getWorld()))) {
				final @NotNull File regionFile = TestUtilsCommand.TESTAREA_FOLDER.resolve(tempRegion.getName().substring(0, tempRegion.getName().length() - 6)).resolve(tempRegion.getName().substring(tempRegion.getName().length() - 5) + ".schem").toFile();
				final @NotNull File oppositeFile = TestUtilsCommand.TESTAREA_FOLDER.resolve(tempRegion.getName().substring(0, tempRegion.getName().length() - 6)).resolve(oppositeRegion.getName().substring(oppositeRegion.getName().length() - 5) + ".schem").toFile();
				if (!regionFile.exists() || !oppositeFile.exists()) {
					p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
								  + ChatColor.RED + "There is no reset for '"
								  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
					return;
				}

				ResetArea.pasteSide(tempRegion, editSession, regionFile);
				ResetArea.pasteSide(oppositeRegion, editSession, oppositeFile);

				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "You pasted the reset for '"
							  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
			} catch (final @NotNull Exception e) {
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "There has been an error, pasting the reset for '"
							  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
				e.printStackTrace();
			}
		} else {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p, regionSide);
			if (tempRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
				return;
			}

			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED + "Pasting the reset for '"
						  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "' on " + regionSide.getName() + " side...");
			try (final @NotNull EditSession editSession = SessionFactory.createSession(p, new BukkitWorld(p.getWorld()))) {
				final @NotNull File resetFile = TestUtilsCommand.TESTAREA_FOLDER.resolve(tempRegion.getName().substring(0, tempRegion.getName().length() - 6)).resolve(tempRegion.getName().substring(tempRegion.getName().length() - 5) + ".schem").toFile();
				if (!resetFile.exists()) {
					p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
								  + ChatColor.RED + "There is no reset for '"
								  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
					return;
				}

				ResetArea.pasteSide(tempRegion, editSession, resetFile);

				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "You pasted the reset for '"
							  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "' on " + regionSide.getName() + " side.");
			} catch (final @NotNull Exception e) {
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "There has been an error, pasting the reset for '"
							  + ChatColor.DARK_RED + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + ChatColor.RED + "'.");
				e.printStackTrace();
			}
		}
	}

	private void pasteSide(final @NotNull DefinedRegion tempRegion, final @NotNull EditSession editSession, final @NotNull File file) throws IOException, WorldEditException { //NOSONAR
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
}