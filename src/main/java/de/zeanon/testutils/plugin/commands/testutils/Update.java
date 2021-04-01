package de.zeanon.testutils.plugin.commands.testutils;

import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.GlobalRequestUtils;
import de.zeanon.testutils.plugin.utils.enums.CommandConfirmation;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Update {

	public void execute(final @Nullable CommandConfirmation confirmation, final @NotNull Player p) {
		if (confirmation == null) {
			if (!de.zeanon.testutils.plugin.update.Update.checkForUpdate()) {
				p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + de.zeanon.testutils.TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] "
							  + ChatColor.RED + "You are already running the latest Version.");
			}
			GlobalMessageUtils.sendBooleanMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + de.zeanon.testutils.TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] "
												  + ChatColor.RED + "Do you really want to update?"
					, "/tu update -confirm"
					, "/tu update -deny"
					, p);
			GlobalRequestUtils.addUpdateRequest(p.getUniqueId());
		} else {
			if (GlobalRequestUtils.checkUpdateRequest(p.getUniqueId())) {
				GlobalRequestUtils.removeUpdateRequest(p.getUniqueId());
				if (confirmation.confirm()) {
					de.zeanon.testutils.plugin.update.Update.updatePlugin(p, de.zeanon.testutils.TestUtils.getInstance());
				} else {
					p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + de.zeanon.testutils.TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] "
								  + ChatColor.RED + "Plugin will not be updated.");
				}
			} else {
				p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + de.zeanon.testutils.TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "] "
							  + ChatColor.RED + "You don't have a pending update request.");
			}
		}
	}

	public static void sendUpdateUsage(final @NotNull Player p) {
		GlobalMessageUtils.sendSuggestMessage(ChatColor.RED + "Usage: ",
											  ChatColor.GRAY + "/testutils"
											  + ChatColor.AQUA + " update",
											  ChatColor.DARK_GREEN + ""
											  + ChatColor.UNDERLINE + ""
											  + ChatColor.ITALIC + ""
											  + ChatColor.BOLD + "!!UPDATE BABY!!",
											  "/tu update", p);
	}
}
