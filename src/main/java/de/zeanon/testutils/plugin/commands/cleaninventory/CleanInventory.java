package de.zeanon.testutils.plugin.commands.cleaninventory;

import de.steamwar.commandframework.SWCommand;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import java.util.HashSet;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;


public class CleanInventory extends SWCommand {

	public CleanInventory() {
		super("cleaninventory");
	}


	@Register(help = true)
	public void help(final @NotNull Player p, final @NotNull String... args) {
		//TODO
	}


	@Register
	public void noArgs(final @NotNull Player p) {
		CleanInventory.cleanInventory(p);
	}


	private static void cleanInventory(final @NotNull Player p) {
		final @NotNull Inventory inventory = p.getInventory();
		final @NotNull Set<Material> containedItems = new HashSet<>();
		inventory.forEach(itemStack -> {
			if (itemStack == null) {
				return;
			}

			if (containedItems.contains(itemStack.getType())) {
				itemStack.setAmount(0);
				return;
			}

			if (itemStack.getAmount() > 1) {
				itemStack.setAmount(1);
			}

			containedItems.add(itemStack.getType());
		});

		p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
					  + ChatColor.RED + "Your inventory has been decluttered.");
	}
}