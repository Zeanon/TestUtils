package de.zeanon.testutils.plugin.handlers;

import de.zeanon.testutils.plugin.utils.SessionFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;


public class EventListener implements Listener {

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		SessionFactory.removeSession(event.getPlayer().getUniqueId().toString());
	}
}