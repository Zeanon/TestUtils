package de.zeanon.testutils.plugin.commands.backup;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.CommandRequestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
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

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length <= 3) {
			if (args.length < 2) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Missing argument for "
							  + ChatColor.YELLOW + "<"
							  + ChatColor.DARK_RED + "filename"
							  + ChatColor.YELLOW + ">");
				Delete.usage(p);
			} else if (args[1].contains("./")) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "File '" + args[1] + "' resolution error: Path is not allowed.");
				Delete.usage(p);
			} else if (args.length == 3 && !CommandRequestUtils.checkDeleteFolderRequest(p.getUniqueId(), args[1])
					   && !args[2].equalsIgnoreCase("-confirm")
					   && !args[2].equalsIgnoreCase("-deny")) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Too many arguments.");
				Delete.usage(p);
			} else {
				Delete.executeInternally(p, args);
			}
		} else {
			p.sendMessage(GlobalMessageUtils.messageHead
						  + ChatColor.RED + "Too many arguments.");
			Delete.usage(p);
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

	private void executeInternally(final @NotNull Player p, final @NotNull String[] args) {
		if (args.length == 2) {
			final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
			if (tempRegion == null) {
				GlobalMessageUtils.sendNotApplicableRegion(p);
			} else {
				final @NotNull File file = new File(TestUtils.getInstance().getDataFolder(), "BackUps/" + p.getWorld().getName() + "/" + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + "/manual/" + p.getUniqueId() + "/" + args[1]);
				if (file.exists()) {
					GlobalMessageUtils.sendBooleanMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
														  ChatColor.RED + "Do you really want to delete "
														  + ChatColor.DARK_RED + args[1]
														  + ChatColor.RED + "?",
														  "/backup delete " + args[1] + " -confirm",
														  "/backup delete " + args[1] + " -deny", p);
					CommandRequestUtils.addDeleteBackupRequest(p.getUniqueId(), args[1], tempRegion.getId().substring(9, tempRegion.getId().length() - 6));
				} else {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.DARK_RED + args[1] + ChatColor.RED + " does not exist.");
				}
			}
		} else {
			final @Nullable String region = CommandRequestUtils.checkDeleteBackupRequest(p.getUniqueId(), args[1]);
			if (args.length == 3 && region != null) {
				if (args[2].equalsIgnoreCase("-confirm")) {
					CommandRequestUtils.removeDeleteBackupRequest(p.getUniqueId());
					final @NotNull File file = new File(TestUtils.getInstance().getDataFolder(), "BackUps/" + p.getWorld().getName() + "/" + region + "/manual/" + p.getUniqueId() + "/" + args[1]);
					if (file.exists()) {
						try {
							FileUtils.deleteDirectory(file);
						} catch (IOException e) {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.DARK_RED + args[1] + ChatColor.RED + " could not be deleted, for further information please see [console].");
							e.printStackTrace();
						}
					} else {
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.DARK_RED + args[1] + ChatColor.RED + " does not exist.");
					}
				} else if (args[2].equalsIgnoreCase("-deny")) {
					CommandRequestUtils.removeDeleteBackupRequest(p.getUniqueId());
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.DARK_RED + args[1] + ChatColor.RED + " was not deleted.");
				}
			}
		}
	}

	private void usage(final @NotNull Player p) {
		GlobalMessageUtils.sendSuggestMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "Usage: ",
											  Delete.usageMessage(),
											  Delete.usageHoverMessage(),
											  Delete.usageCommand(), p);
	}
}