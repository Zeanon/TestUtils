package de.zeanon.testutils.plugin.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class ItemUtils {

	public static void giveItemToPlayer(final @NotNull Player player, @Nullable ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == Material.AIR) {
			return;
		}

		for (int i = 0; i < player.getInventory().getSize(); i++) {
			final @Nullable ItemStack current = player.getInventory().getItem(i);
			if (current != null && current.isSimilar(itemStack)) {
				player.getInventory().setItem(i, null);
				itemStack = current;
				break;
			}
		}

		final @Nullable ItemStack current = player.getInventory().getItemInMainHand();
		player.getInventory().setItemInMainHand(itemStack);
		if (current.getType() != Material.AIR) {
			player.getInventory().addItem(current);
		}
	}
}