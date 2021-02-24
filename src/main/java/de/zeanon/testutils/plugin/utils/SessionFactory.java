package de.zeanon.testutils.plugin.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class SessionFactory {

	private static final Map<String, SizedStack<EditSession>> undoSessions;
	private static final Map<String, SizedStack<EditSession>> redoSessions;

	static {
		undoSessions = new HashMap<>();
		redoSessions = new HashMap<>();
	}

	public @Nullable EditSession createSession(final @NotNull Player p) {
		SessionFactory.undoSessions.computeIfAbsent(p.getUniqueId().toString(), user -> new SizedStack<>(ConfigUtils.getInt("Max History")));
		EditSession tempSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(p.getWorld()), -1);
		SessionFactory.undoSessions.get(p.getUniqueId().toString()).push(tempSession);
		return tempSession;
	}

	public void registerUndoSession(final @NotNull String uuid, final @NotNull EditSession editSession) {
		SessionFactory.undoSessions.computeIfAbsent(uuid, user -> new SizedStack<>(ConfigUtils.getInt("Max History")));
		SessionFactory.undoSessions.get(uuid).push(editSession);
	}

	public @Nullable EditSession getUndoSession(final @NotNull String uuid) {
		if (SessionFactory.undoSessions.containsKey(uuid)) {
			SizedStack<EditSession> tempStack = SessionFactory.undoSessions.get(uuid);
			if (!tempStack.isEmpty()) {
				return tempStack.pop();
			}
		}
		return null;
	}

	public void registerRedoSession(final @NotNull String uuid, final @NotNull EditSession editSession) {
		SessionFactory.redoSessions.computeIfAbsent(uuid, user -> new SizedStack<>(ConfigUtils.getInt("Max History")));
		SessionFactory.redoSessions.get(uuid).push(editSession);
	}

	public @Nullable EditSession getRedoSession(final @NotNull String uuid) {
		if (SessionFactory.redoSessions.containsKey(uuid)) {
			SizedStack<EditSession> tempStack = SessionFactory.redoSessions.get(uuid);
			if (!tempStack.isEmpty()) {
				return tempStack.pop();
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	public void removeSessions(final @NotNull String uuid) {
		SessionFactory.undoSessions.remove(uuid);
		SessionFactory.redoSessions.remove(uuid);
	}
}