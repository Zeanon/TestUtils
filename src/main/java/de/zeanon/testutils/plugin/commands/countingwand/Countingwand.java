package de.zeanon.testutils.plugin.commands.countingwand;

import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.ItemUtils;
import de.zeanon.testutils.regionsystem.region.Region;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Countingwand {

	public final @NotNull ItemStack WAND_ITEM;
	private final @NotNull Map<String, Pair<Region.Point, Region.Point>> selections;

	static {
		selections = new HashMap<>();
		WAND_ITEM = new ItemStack(Material.STICK);

		Countingwand.WAND_ITEM.setAmount(1);
		final @NotNull ItemMeta wandMeta = Objects.notNull(Countingwand.WAND_ITEM.getItemMeta());
		wandMeta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.ITALIC + "Zollstock");
		wandMeta.setLore(Arrays.asList(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Left click: select pos #1",
									   ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Right click: select pos #2"));
		Countingwand.WAND_ITEM.setItemMeta(wandMeta);
	}

	public void giveItem(final @NotNull Player p) {
		ItemUtils.giveItemToPlayer(p, Countingwand.WAND_ITEM);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isCountingwand(final @Nullable ItemStack item) {
		return Countingwand.WAND_ITEM.isSimilar(item);
	}

	public void checkSelection(final @NotNull Region.Point point, final boolean pos1, final @NotNull Player p) {
		@Nullable Pair<Region.Point, Region.Point> selection = Countingwand.selections.get(p.getUniqueId().toString());
		final boolean newPos;
		if (selection != null) {
			if (pos1) {
				newPos = !point.equals(selection.setKey(point));
			} else {
				newPos = !point.equals(selection.setValue(point));
			}
		} else {
			if (pos1) {
				selection = new Pair<>(point, null);
			} else {
				selection = new Pair<>(null, point);
			}
			Countingwand.selections.put(p.getUniqueId().toString(), selection);
			newPos = true;
		}

		if (newPos) {
			p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
						  + ChatColor.RED
						  + (pos1 ? "First position set to: " : "Second position set to: ")
						  + ChatColor.DARK_GRAY
						  + "["
						  + ChatColor.DARK_RED
						  + point.getX()
						  + ChatColor.DARK_GRAY
						  + ", "
						  + ChatColor.DARK_RED
						  + point.getY()
						  + ChatColor.DARK_GRAY
						  + ", "
						  + ChatColor.DARK_RED
						  + point.getZ()
						  + ChatColor.DARK_GRAY
						  + "] ("
						  + ChatColor.DARK_RED
						  + Countingwand.getAmount(selection.getKey(), selection.getValue())
						  + ChatColor.DARK_GRAY
						  + ")");
		}
	}

	public int getAmount(final @Nullable Region.Point point1, final @Nullable Region.Point point2) {
		if (point1 == null || point2 == null) {
			return 0;
		}

		return (Math.abs(point1.getX() - point2.getX()) + 1) * (Math.abs(point1.getY() - point2.getY()) + 1) * (Math.abs(point1.getZ() - point2.getZ()) + 1);
	}
}