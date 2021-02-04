package de.zeanon.testutils.plugin.commands;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanager.internal.utility.basic.Objects;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class TNT {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length == 0) {
			ProtectedRegion temp = TestAreaUtils.getRegion(p);
			Objects.notNull(temp).setFlag(Flags.TNT, temp.getFlag(Flags.TNT) == StateFlag.State.ALLOW ? StateFlag.State.DENY : StateFlag.State.ALLOW);
			p.sendMessage(ChatColor.RED + "TNT is now " + (temp.getFlag(Flags.TNT) == StateFlag.State.ALLOW ? "activated" : "deactivated") + " in " + temp.getId());
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("allow")) {
				final ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
				Objects.notNull(tempRegion).setFlag(Flags.TNT, StateFlag.State.ALLOW);
				p.sendMessage(ChatColor.RED + "TNT is now activated in " + tempRegion.getId());
			} else if (args[0].equalsIgnoreCase("deny")) {
				final ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
				Objects.notNull(tempRegion).setFlag(Flags.TNT, StateFlag.State.DENY);
				p.sendMessage(ChatColor.RED + "TNT is now deactivated in " + tempRegion.getId());
			} else if (args[0].equalsIgnoreCase("other")) {
				ProtectedRegion temp = TestAreaUtils.getOppositeRegion(p);
				Objects.notNull(temp).setFlag(Flags.TNT, temp.getFlag(Flags.TNT) == StateFlag.State.ALLOW ? StateFlag.State.DENY : StateFlag.State.ALLOW);
				p.sendMessage(ChatColor.RED + "TNT is now " + (temp.getFlag(Flags.TNT) == StateFlag.State.ALLOW ? "activated" : "deactivated") + " in " + temp.getId());
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("other")) {
			if (args[1].equalsIgnoreCase("allow")) {
				final ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
				Objects.notNull(tempRegion).setFlag(Flags.TNT, StateFlag.State.ALLOW);
				p.sendMessage(ChatColor.RED + "TNT is now activated in " + tempRegion.getId());
			} else if (args[1].equalsIgnoreCase("deny")) {
				final ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
				Objects.notNull(tempRegion).setFlag(Flags.TNT, StateFlag.State.DENY);
				p.sendMessage(ChatColor.RED + "TNT is now deactivated in " + tempRegion.getId());
			}
		} else {
			p.sendMessage(ChatColor.RED + "Incorrect Syntax.");
		}
	}
}