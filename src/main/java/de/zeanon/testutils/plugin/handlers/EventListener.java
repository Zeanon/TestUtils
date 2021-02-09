package de.zeanon.testutils.plugin.handlers;

import de.zeanon.testutils.plugin.update.Update;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class EventListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Update.updateAvailable(event.getPlayer());
	}
}