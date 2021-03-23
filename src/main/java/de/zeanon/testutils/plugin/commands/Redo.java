package de.zeanon.testutils.plugin.commands;

import com.sk89q.worldedit.EditSession;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Redo {

	public void redo(final @NotNull Player p) {
		try (final @Nullable EditSession tempSession = SessionFactory.getRedoSession(p)) {
			if (tempSession == null) {
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "Nothing left to redo.");
			} else {
				tempSession.redo(tempSession);
				SessionFactory.registerUndoSession(p, tempSession);
				p.sendMessage(GlobalMessageUtils.messageHead
							  + ChatColor.RED + "You redid your last action.");
			}
		}
	}
}
