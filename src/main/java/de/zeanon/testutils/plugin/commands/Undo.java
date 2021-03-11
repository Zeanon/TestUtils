package de.zeanon.testutils.plugin.commands;

import com.sk89q.worldedit.EditSession;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class Undo {


	public void undo(final @NotNull Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				EditSession tempSession = SessionFactory.getSession(p);
				if (tempSession == null) {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "Nothing left to undo.");
				} else {
					tempSession.undo(tempSession);
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "You undid your last action.");
				}
			}
		}.runTask(de.zeanon.testutils.TestUtils.getInstance());
	}
}