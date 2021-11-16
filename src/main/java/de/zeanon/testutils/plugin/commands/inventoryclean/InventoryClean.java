package de.zeanon.testutils.plugin.commands.inventoryclean;

import de.steamwar.commandframework.SWCommand;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import java.util.HashSet;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


public class InventoryClean extends SWCommand {

	public InventoryClean() {
		super(new Prefix("testutils"), "inventoryclean", "invclean", "declutter");
	}


	@Register(help = true)
	public void help(final @NotNull Player p, final @NotNull String... args) {
		//TODO
	}


	@Register
	public void noArgs(final @NotNull Player p) {
		InventoryClean.inventoryClean(p);
	}


	private static void inventoryClean(final @NotNull Player p) {
		final @NotNull Inventory inventory = p.getInventory();
		final @NotNull Set<ItemStack> containedItems = new HashSet<>();
		inventory.forEach(itemStack -> {
			if (itemStack == null) {
				return;
			}

			if (containedItems.stream().anyMatch(itemStack::isSimilar)) {
				itemStack.setAmount(0);
				return;
			}

			if (itemStack.getAmount() > 1) {
				itemStack.setAmount(1);
			}

			containedItems.add(itemStack);
		});

		p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
					  + ChatColor.RED + "Your inventory has been decluttered.");
	}
}