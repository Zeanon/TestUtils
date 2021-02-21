package de.zeanon.testutils.plugin.commands.testblock;

import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.CommandRequestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.InternalFileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class DeleteBlock {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (args.length <= 3) {
					if (args.length < 1) {
						p.sendMessage(ChatColor.RED + "Missing argument for "
									  + ChatColor.YELLOW + "<"
									  + ChatColor.GOLD + "filename"
									  + ChatColor.YELLOW + ">");
						DeleteBlock.usage(p);
					} else if (args[1].contains("./")) {
						p.sendMessage(ChatColor.RED + "File '" + args[1] + "'resolution error: Path is not allowed.");
						DeleteBlock.usage(p);
					} else if (args.length == 3 && !CommandRequestUtils.checkDeleteFolderRequest(p.getUniqueId().toString(), args[1])
							   && !args[2].equalsIgnoreCase("confirm")
							   && !args[2].equalsIgnoreCase("deny")) {
						p.sendMessage(ChatColor.RED + "Too many arguments.");
						DeleteBlock.usage(p);
					} else {
						DeleteBlock.executeInternally(p, args);
					}
				} else {
					p.sendMessage(ChatColor.RED + "Too many arguments.");
					DeleteBlock.usage(p);
				}
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
	}

	public @NotNull String usageMessage() {
		return ChatColor.GRAY + "/testutils"
			   + ChatColor.AQUA + " deleteblock "
			   + ChatColor.YELLOW + "<"
			   + ChatColor.GOLD + "filename"
			   + ChatColor.YELLOW + ">";
	}

	public @NotNull String usageHoverMessage() {
		return ChatColor.RED + "e.g. "
			   + ChatColor.GRAY + "/testutils"
			   + ChatColor.AQUA + " deleteblock "
			   + ChatColor.GOLD + "example";
	}

	public @NotNull String usageCommand() {
		return "/testutils deleteblock ";
	}

	private void executeInternally(final @NotNull Player p, final @NotNull String @NotNull [] args) {
		final @NotNull File file = new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks/" + p.getUniqueId().toString() + "/" + args[1] + ".schem"); //NOSONAR
		if (args.length == 2) {
			if (file.exists()) {
				GlobalMessageUtils.sendBooleanMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
													  ChatColor.RED + "Do you really want to delete "
													  + ChatColor.GOLD + args[1]
													  + ChatColor.RED + "?",
													  "/tu deleteblock " + args[1] + " confirm",
													  "/tu deleteblock " + args[1] + " deny", p);
				CommandRequestUtils.addDeleteRequest(p.getUniqueId().toString(), args[1]);
			} else {
				p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
							  ChatColor.GOLD + args[1] + ChatColor.RED + " does not exist.");
			}
		} else if (args.length == 3 && CommandRequestUtils.checkDeleteRequest(p.getUniqueId().toString(), args[1])) {
			if (args[2].equalsIgnoreCase("confirm")) {
				CommandRequestUtils.removeDeleteRequest(p.getUniqueId().toString());
				if (file.exists()) {
					try {
						Files.delete(file.toPath());
						final @Nullable String parentName = Objects.notNull(file.getAbsoluteFile().getParentFile().listFiles()).length == 0
															? InternalFileUtils.deleteEmptyParent(file, new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/Blocks"))
															: null;

						p.sendMessage(GlobalMessageUtils.messageHead +
									  ChatColor.DARK_RED + args[1] + ChatColor.RED + " was deleted successfully.");

						if (parentName != null) {
							p.sendMessage(GlobalMessageUtils.messageHead +
										  ChatColor.RED + "Folder "
										  + ChatColor.GREEN + parentName
										  + ChatColor.RED + " was deleted successfully due to being empty.");
						}
					} catch (IOException e) {
						p.sendMessage(GlobalMessageUtils.messageHead +
									  ChatColor.DARK_RED + args[1] + ChatColor.RED + " could not be deleted, for further information please see [console].");
					}
				} else {
					p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
								  ChatColor.GOLD + args[1] + ChatColor.RED + " does not exist.");
				}
			} else if (args[2].equalsIgnoreCase("deny")) {
				CommandRequestUtils.removeDeleteRequest(p.getUniqueId().toString());
				p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] " +
							  ChatColor.GOLD + args[1] + ChatColor.RED + " was not deleted.");
			}
		}
	}

	private void usage(final @NotNull Player p) {
		GlobalMessageUtils.sendSuggestMessage(ChatColor.RED + "Usage: ",
											  DeleteBlock.usageMessage(),
											  DeleteBlock.usageHoverMessage(),
											  DeleteBlock.usageCommand(), p);
	}
}