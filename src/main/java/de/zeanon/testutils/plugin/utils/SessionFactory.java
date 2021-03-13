package de.zeanon.testutils.plugin.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import de.zeanon.storagemanagercore.internal.utility.basic.SizedStack;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class SessionFactory {

	private static final Map<String, SizedStack<EditSession>> undoSessions;

	static {
		undoSessions = new HashMap<>();
	}

	public @NotNull EditSession createSession(final @NotNull Player p) {
		if (SessionFactory.undoSessions.containsKey(p.getUniqueId().toString())) {
			SizedStack<EditSession> tempStack = SessionFactory.undoSessions.get(p.getUniqueId().toString());
			if (tempStack.getMaxSize() != ConfigUtils.getInt("Max History")) {
				tempStack.resize(ConfigUtils.getInt("Max History"));
			}
		} else {
			SessionFactory.undoSessions.put(p.getUniqueId().toString(), new SizedStack<>(ConfigUtils.getInt("Max History")));
		}
		final @NotNull EditSession tempSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(p.getWorld()), -1);
		SessionFactory.undoSessions.get(p.getUniqueId().toString()).push(tempSession);
		return tempSession;
	}

	public @Nullable EditSession getSession(final @NotNull Player p) {
		if (SessionFactory.undoSessions.containsKey(p.getUniqueId().toString())) {
			SizedStack<EditSession> tempStack = SessionFactory.undoSessions.get(p.getUniqueId().toString());
			if (!tempStack.empty()) {
				return tempStack.pop();
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	public void removeSessions(final @NotNull Player p) {
		SessionFactory.undoSessions.remove(p.getUniqueId().toString());
	}
}