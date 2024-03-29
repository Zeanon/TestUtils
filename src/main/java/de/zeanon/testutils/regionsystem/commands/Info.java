package de.zeanon.testutils.regionsystem.commands;


import de.zeanon.testutils.plugin.utils.enums.RegionName;
import de.zeanon.testutils.plugin.utils.enums.StringModifiers;
import de.zeanon.testutils.regionsystem.RegionManager;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import de.zeanon.testutils.regionsystem.region.Region;
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
				final @Nullable DefinedRegion region = RegionManager.getDefinedRegion(regionName.getName());
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
		p.sendMessage(StringModifiers.LINE_BREAK
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

		final @NotNull TextComponent flags = new TextComponent(RegionCommand.MESSAGE_HEAD
															   + ChatColor.RED + "---=== " + ChatColor.DARK_RED + "Flags" + ChatColor.RED + " ===---");
		if (!region.getFlags().isEmpty()) {
			flags.addExtra(StringModifiers.LINE_BREAK.toString());
		}

		region.getFlags().forEach((flag, value) -> {
			currentCount[0]++;
			if (!lineBreak[0]) {
				flags.addExtra(RegionCommand.MESSAGE_HEAD + ChatColor.BLACK + "[");
			}

			final @NotNull TextComponent currentFlag = new TextComponent(ChatColor.DARK_RED + flag.toString() + ChatColor.DARK_GRAY + " : " + value.getChatValue());
			currentFlag.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
													 new ComponentBuilder(new TextComponent(
															 TextComponent.fromLegacyText(
																	 ChatColor.RED + "Edit the value of '" + ChatColor.DARK_RED + flag + ChatColor.RED + "'")))
															 .create()));
			currentFlag.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rg flag " + region.getName() + " " + flag + " "));
			flags.addExtra(currentFlag);

			if (currentCount[0] == flagCount) {
				flags.addExtra(ChatColor.BLACK + "]");
			} else {
				if (lineBreak[0]) {
					flags.addExtra(ChatColor.BLACK + "]" + StringModifiers.LINE_BREAK);
				} else {
					flags.addExtra(ChatColor.BLACK + " " + ChatColor.BOLD + "|" + ChatColor.BLACK + " ");
				}
			}

			lineBreak[0] = !lineBreak[0];
		});

		p.spigot().sendMessage(flags);

		lineBreak[0] = false;
		currentCount[0] = 0;
		final int nbtCount = region.getTags().size();

		final @NotNull TextComponent tags = new TextComponent(RegionCommand.MESSAGE_HEAD
															  + ChatColor.RED + "---=== " + ChatColor.DARK_RED + "Tags" + ChatColor.RED + " ===---");
		if (!region.getTags().isEmpty()) {
			tags.addExtra(StringModifiers.LINE_BREAK.toString());
		}

		region.getTags().forEach((tag, value) -> {
			currentCount[0]++;
			if (!lineBreak[0]) {
				tags.addExtra(RegionCommand.MESSAGE_HEAD + ChatColor.BLACK + "[");
			}

			final @NotNull TextComponent currentTag = new TextComponent(ChatColor.DARK_RED + tag.toString() + ChatColor.DARK_GRAY + " : " + value.getChatValue());
			currentTag.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
													new ComponentBuilder(new TextComponent(
															TextComponent.fromLegacyText(
																	value.getDescription())))
															.create()));
			tags.addExtra(currentTag);

			if (currentCount[0] == nbtCount) {
				tags.addExtra(ChatColor.BLACK + "]");
			} else {
				if (lineBreak[0]) {
					tags.addExtra(ChatColor.BLACK + "]" + StringModifiers.LINE_BREAK);
				} else {
					tags.addExtra(ChatColor.BLACK + " " + ChatColor.BOLD + "|" + ChatColor.BLACK + " ");
				}
			}

			lineBreak[0] = !lineBreak[0];
		});

		p.spigot().sendMessage(tags);
	}
}