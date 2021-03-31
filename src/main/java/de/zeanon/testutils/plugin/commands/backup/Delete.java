package de.zeanon.testutils.plugin.commands.backup;

import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.CommandRequestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import de.zeanon.testutils.plugin.utils.enums.CommandConfirmation;
import de.zeanon.testutils.plugin.utils.enums.MappedFile;
import de.zeanon.testutils.plugin.utils.region.DefinedRegion;
import java.io.File;
import java.io.IOException;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Delete {

	public void execute(final @NotNull MappedFile mappedFile, final @Nullable CommandConfirmation confirmation, final @NotNull Player p) {
		if (mappedFile.getName().contains("./")) {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "File '" + mappedFile.getName() + "' resolution error: Path is not allowed.");
		} else if (confirmation == null) {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
			final @Nullable DefinedRegion otherRegion = TestAreaUtils.getSouthRegion(p);

			if (tempRegion == null || otherRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
			} else {
				final @NotNull File folder = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Backups/" + p.getWorld().getName() + "/" + tempRegion.getName().substring(0, tempRegion.getName().length() - 6) + "/manual/" + p.getUniqueId() + "/" + mappedFile.getName());
				if (folder.exists() && folder.isDirectory()) {
					CommandRequestUtils.addDeleteBackupRequest(p.getUniqueId(), mappedFile.getName(), tempRegion.getName().substring(0, tempRegion.getName().length() - 6));
					GlobalMessageUtils.sendBooleanMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
														  ChatColor.RED + "Do you really want to delete "
														  + ChatColor.DARK_RED + mappedFile.getName()
														  + ChatColor.RED + "?",
														  "/backup delete " + mappedFile.getName() + " -confirm",
														  "/backup delete " + mappedFile.getName() + " -deny", p);
				} else {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.DARK_RED + mappedFile.getName() + ChatColor.RED + " does not exist.");
				}
			}
		} else {
			if (confirmation.confirm()) { //NOSONAR
				final @Nullable String region = CommandRequestUtils.checkDeleteBackupRequest(p.getUniqueId(), mappedFile.getName());
				if (region != null) {
					CommandRequestUtils.removeDeleteBackupRequest(p.getUniqueId());

					final @Nullable DefinedRegion tempRegion = TestAreaUtils.getNorthRegion(region);
					final @Nullable DefinedRegion otherRegion = TestAreaUtils.getSouthRegion(region);
					if (tempRegion == null || otherRegion == null) {
						Delete.sendNotApplicableRegion(p);
					} else {
						final @NotNull File folder = new File(TestUtils.getInstance().getDataFolder(), "Backups/" + p.getWorld().getName() + "/" + region + "/manual/" + p.getUniqueId() + "/" + mappedFile.getName());
						if (folder.exists() && folder.isDirectory()) {
							try {
								FileUtils.deleteDirectory(folder);
							} catch (IOException e) {
								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.DARK_RED + mappedFile.getName() + ChatColor.RED + " could not be deleted, for further information please see [console].");
								e.printStackTrace();
							}
						} else {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.DARK_RED + mappedFile.getName() + ChatColor.RED + " does not exist.");
						}
					}
				}
			} else {
				CommandRequestUtils.removeOverwriteBackupRequest(p.getUniqueId());
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.DARK_RED + mappedFile.getName() + ChatColor.RED + " was not deleted.");
			}
		}
	}

	public @NotNull String usageMessage() {
		return ChatColor.GRAY + "/backup"
			   + ChatColor.AQUA + " delete "
			   + ChatColor.YELLOW + "<"
			   + ChatColor.DARK_RED + "filename"
			   + ChatColor.YELLOW + ">";
	}

	public @NotNull String usageHoverMessage() {
		return ChatColor.RED + "e.g. "
			   + ChatColor.GRAY + "/backup"
			   + ChatColor.AQUA + " delete "
			   + ChatColor.DARK_RED + "example";
	}

	public @NotNull String usageCommand() {
		return "/backup delete ";
	}

	public void usage(final @NotNull Player p) {
		GlobalMessageUtils.sendSuggestMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "Usage: ",
											  Delete.usageMessage(),
											  Delete.usageHoverMessage(),
											  Delete.usageCommand(), p);
	}

	private void sendNotApplicableRegion(final @NotNull Player p) {
		p.sendMessage(GlobalMessageUtils.messageHead
					  + ChatColor.RED + "The given region does not exist.");
	}
}