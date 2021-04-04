package de.zeanon.testutils.plugin.commands.testutils;

import com.sk89q.worldedit.EditSession;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Undo {

	public void undo(final @NotNull Player p) {
		try (final @Nullable EditSession tempSession = SessionFactory.getUndoSession(p)) {
			if (tempSession == null) {
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "Nothing left to undo.");
			} else {
				tempSession.undo(tempSession);
				SessionFactory.registerRedoSession(p, tempSession);
				p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
							  + ChatColor.RED + "You undid your last action.");
			}
		}
	}
}