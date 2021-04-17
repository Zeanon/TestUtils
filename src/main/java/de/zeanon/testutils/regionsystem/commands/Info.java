package de.zeanon.testutils.regionsystem.commands;


import de.zeanon.testutils.plugin.utils.enums.RegionName;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import de.zeanon.testutils.regionsystem.region.Region;
import de.zeanon.testutils.regionsystem.region.RegionManager;
import java.util.List;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class Info {

	public void execute(final @Nullable RegionName regionName, final @NotNull Player p) {
		if (regionName == null) {
			final @NotNull List<DefinedRegion> regions = RegionManager.getApplicableRegions(p.getLocation());
			if (regions.isEmpty()) {
				Info.sendRegionInfo(RegionManager.getGlobalRegion(p.getWorld()), p);
			} else if (regions.size() == 1) {
				Info.sendRegionInfo(regions.get(0), p);
			} else {
				RegionCommand.sendMultipleRegions(regions, p);
			}
		} else {
			if (RegionManager.isGlobalRegion(regionName.getName())) {
				Info.sendRegionInfo(RegionManager.getGlobalRegion(p.getWorld()), p);
			} else {
				final @Nullable DefinedRegion region = RegionManager.getRegion(regionName.getName());
				if (region != null) {
					Info.sendRegionInfo(region, p);
				} else {
					p.sendMessage(RegionCommand.MESSAGE_HEAD
								  + ChatColor.RED + "The given region does not exist.");
				}
			}
		}
	}

	private void sendRegionInfo(final @NotNull Region region, final @NotNull Player p) {
		p.sendMessage("\n"
					  + RegionCommand.MESSAGE_HEAD
					  + ChatColor.RED + "=== " + ChatColor.DARK_RED + region.getName() + ChatColor.RED + " ===");

		p.sendMessage(RegionCommand.MESSAGE_HEAD
					  + ChatColor.RED + "World: "
					  + ChatColor.DARK_RED + region.getWorld().getName());

		p.sendMessage(RegionCommand.MESSAGE_HEAD
					  + ChatColor.RED + "Type: "
					  + ChatColor.DARK_RED + region.getType());


		final boolean[] lineBreak = {false};
		final int[] currentCount = new int[]{0};
		final int flagCount = region.getFlags().size();
		final @NotNull TextComponent separator = new TextComponent(
				TextComponent.fromLegacyText(ChatColor.BLACK + " " + ChatColor.BOLD + "|" + ChatColor.BLACK + " "));

		final @NotNull TextComponent flags = new TextComponent(RegionCommand.MESSAGE_HEAD
															   + ChatColor.RED + "=== " + ChatColor.DARK_RED + "Flags" + ChatColor.RED + " === \n");
		region.getFlags().forEach((flag, value) -> {
			currentCount[0]++;
			if (!lineBreak[0]) {
				flags.addExtra(RegionCommand.MESSAGE_HEAD + ChatColor.BLACK + "[");
			}

			final @NotNull TextComponent currentFlag = new TextComponent(ChatColor.DARK_RED + flag.toString() + ChatColor.DARK_GRAY + " : " + value.getChatValue());
			currentFlag.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
													 new ComponentBuilder(new TextComponent(
															 TextComponent.fromLegacyText(
																	 ChatColor.RED + "Edit the value of '" + ChatColor.DARK_RED + flag.toString() + ChatColor.RED + "'")))
															 .create()));
			currentFlag.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rg flag " + region.getName() + " " + flag.toString() + " "));
			flags.addExtra(currentFlag);

			if (lineBreak[0]) {
				flags.addExtra(ChatColor.BLACK + "]\n");
			} else {
				if (currentCount[0] == flagCount) {
					flags.addExtra(ChatColor.BLACK + "]");
				} else {
					flags.addExtra(separator);
				}
			}

			lineBreak[0] = !lineBreak[0];
		});
		p.spigot().sendMessage(flags);
	}
}