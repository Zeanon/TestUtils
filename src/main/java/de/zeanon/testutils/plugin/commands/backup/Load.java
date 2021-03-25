package de.zeanon.testutils.plugin.commands.backup;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.backup.BackUpScheduler;
import de.zeanon.testutils.plugin.utils.enums.BackUpMode;
import de.zeanon.testutils.plugin.utils.enums.PasteSide;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Load {

	public void execute(final @NotNull Backup.ModifierBlock modifiers, final @NotNull Player p) {
		final @Nullable World world = p.getWorld();
		final @Nullable ProtectedRegion tempRegion = modifiers.getPasteSide() == PasteSide.NONE ? TestAreaUtils.getRegion(p) : TestAreaUtils.getRegion(p, modifiers.getPasteSide());
		final @Nullable ProtectedRegion otherRegion = modifiers.getPasteSide() == PasteSide.NONE ? TestAreaUtils.getOppositeRegion(p) : TestAreaUtils.getOppositeRegion(p, modifiers.getPasteSide());

		if (tempRegion == null || otherRegion == null) {
			GlobalMessageUtils.sendNotApplicableRegion(p);
		} else {
			try {
				final @NotNull File regionFolder = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/BackUps/" + world.getName() + "/" + tempRegion.getId().substring(9, tempRegion.getId().length() - 6));
				if (regionFolder.exists()) {
					final @Nullable File backupFile;

					if (modifiers.getFileName() == null) {
						final @NotNull Optional<File> possibleFirst = Backup.getLatest(regionFolder, p.getUniqueId().toString(), modifiers.getBackUpMode());

						if (possibleFirst.isPresent()) {
							backupFile = possibleFirst.get();
						} else {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "There is no backup for '"
										  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
							return;
						}
					} else {
						backupFile = Backup.getFile(regionFolder, modifiers.getFileName(), modifiers.getBackUpMode(), p);
						if (backupFile == null || !backupFile.exists()) {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "There is no backup named '" + ChatColor.DARK_RED + modifiers.getFileName() + ChatColor.RED + "' for '"
										  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
							return;
						}
					}

					try (final @NotNull EditSession editSession = SessionFactory.createSession(p)) {
						if (modifiers.getPasteSide() != PasteSide.NONE) {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "Loading the " + (modifiers.getFileName() == null ? "latest " + (modifiers.getBackUpMode() != BackUpMode.NONE ? modifiers.getBackUpMode() : "") + " backup" : "backup '" + ChatColor.DARK_RED + modifiers.getFileName() + ChatColor.RED + "'") + " for " + modifiers.getPasteSide().toString() + " side.");

							BackUpScheduler.getManualBackup().pasteSide(tempRegion, editSession, new File(backupFile, tempRegion.getId().substring(tempRegion.getId().length() - 5) + ".schem"));

							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "You pasted the " + (modifiers.getFileName() == null ? "latest " + (modifiers.getBackUpMode() != BackUpMode.NONE ? modifiers.getBackUpMode() : "") + " backup" : "backup '" + ChatColor.DARK_RED + modifiers.getFileName() + ChatColor.RED + "'") + " for " + modifiers.getPasteSide().toString() + " side.");
						} else {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "Loading the " + (modifiers.getFileName() == null ? "latest " + (modifiers.getBackUpMode() != BackUpMode.NONE ? modifiers.getBackUpMode() : "") + " backup" : "backup '" + ChatColor.DARK_RED + modifiers.getFileName() + ChatColor.RED + "'") + " for your Testarea.");

							BackUpScheduler.getManualBackup().pasteSide(tempRegion, editSession, new File(backupFile, tempRegion.getId().substring(tempRegion.getId().length() - 5) + ".schem"));
							BackUpScheduler.getManualBackup().pasteSide(otherRegion, editSession, new File(backupFile, otherRegion.getId().substring(tempRegion.getId().length() - 5) + ".schem"));

							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "You pasted the " + (modifiers.getFileName() == null ? "latest " + (modifiers.getBackUpMode() != BackUpMode.NONE ? modifiers.getBackUpMode() : "") + " backup" : "backup '" + ChatColor.DARK_RED + modifiers.getFileName() + ChatColor.RED + "'") + " for your Testarea.");
						}
					} catch (WorldEditException | IOException e) {
						e.printStackTrace();
					}
				} else {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "There is no backup for '"
								  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
				}
			} catch (IOException e) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "There has been an error, pasting the backup for '"
							  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
				e.printStackTrace();
			}
		}
	}
}