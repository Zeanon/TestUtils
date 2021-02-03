package de.zeanon.testutils.plugin.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class SessionFactory {

	private static final int MAX_HISTORY = 10;

	private static final Map<String, SizedStack<EditSession>> existingSessions;

	static {
		existingSessions = new HashMap<>();
	}

	public EditSession createSession(final @NotNull Player p) {
		if (!SessionFactory.existingSessions.containsKey(p.getUniqueId().toString())) {
			SessionFactory.existingSessions.put(p.getUniqueId().toString(), new SizedStack<>(SessionFactory.MAX_HISTORY));
		}
		EditSession tempSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(p.getWorld()), -1);
		SessionFactory.existingSessions.get(p.getUniqueId().toString()).push(tempSession);
		return tempSession;
	}

	public EditSession getSession(final @NotNull Player p) {
		if (SessionFactory.existingSessions.containsKey(p.getUniqueId().toString())) {
			SizedStack<EditSession> tempStack = SessionFactory.existingSessions.get(p.getUniqueId().toString());
			if (!tempStack.isEmpty()) {
				return tempStack.pop();
			}
		}
		return null;
	}

	public void removeSession(final @NotNull String uuid) {
		SessionFactory.existingSessions.remove(uuid);
	}
}