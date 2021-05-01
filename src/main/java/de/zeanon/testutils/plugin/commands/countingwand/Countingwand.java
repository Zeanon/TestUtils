package de.zeanon.testutils.plugin.commands.countingwand;

import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
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

	public final @NotNull ItemStack WAND_ITEM = new ItemStack(Material.BLAZE_ROD);
	private final @NotNull Map<String, Pair<Region.Point, Region.Point>> selections = new HashMap<>();

	static {
		Countingwand.WAND_ITEM.setAmount(1);
		final @NotNull ItemMeta wandMeta = Objects.notNull(Countingwand.WAND_ITEM.getItemMeta());
		wandMeta.setDisplayName("Counting Wand");
		wandMeta.setLore(Arrays.asList(ChatColor.DARK_RED + "" + ChatColor.ITALIC + "Left click: select pos #1",
									   ChatColor.DARK_RED + "" + ChatColor.ITALIC + "Right click: select pos #2"));
		Countingwand.WAND_ITEM.setItemMeta(wandMeta);
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
			selection = new Pair<>(point, point);
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
						  + Countingwand.getAmount(selection.getKey(), Objects.notNull(selection.getValue()))
						  + ChatColor.DARK_GRAY
						  + ")");
		}
	}

	public int getAmount(final @NotNull Region.Point point1, final @NotNull Region.Point point2) {
		return (Math.abs(point1.getX() - point2.getX()) + 1) * (Math.abs(point1.getY() - point2.getY()) + 1) * (Math.abs(point1.getZ() - point2.getZ()) + 1);
	}
}