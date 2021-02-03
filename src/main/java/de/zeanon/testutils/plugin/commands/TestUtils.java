package de.zeanon.testutils.plugin.commands;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import de.zeanon.testutils.plugin.update.Update;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class TestUtils {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("register")) {
						if (args.length == 1) {
							TestAreaUtils.registerBlock(p, null);
						} else if (args.length == 2) {
							TestAreaUtils.registerBlock(p, args[1]);
						} else {
							p.sendMessage(ChatColor.RED + "Too many arguments.");
						}
					} else if (args[0].equalsIgnoreCase("tg")) {
						final @NotNull String name = args.length > 1 ? args[1] : p.getName();
						TestAreaUtils.generate(new BukkitWorld(p.getWorld()),
											   p.getLocation().getBlockX(),
											   p.getLocation().getBlockY(),
											   p.getLocation().getBlockZ(),
											   name);
						p.sendMessage(ChatColor.GOLD + "Created TG with name 'testarea_" + name + "'");
					} else if (args[0].equalsIgnoreCase("update")) {
						Update.updatePlugin(p);
					}
				} else {
					p.sendMessage(ChatColor.RED + "Missing argument.");
				}
			}
		}.runTaskAsynchronously(de.zeanon.testutils.TestUtils.getInstance());
	}
}