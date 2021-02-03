package de.zeanon.testutils.plugin.commands;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class TNT {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("allow")) {
				Objects.requireNonNull(TestAreaUtils.getRegion(p)).setFlag(Flags.TNT, StateFlag.State.ALLOW);
				p.sendMessage(ChatColor.RED + "TNT is now activated");
			} else if (args[0].equalsIgnoreCase("deny")) {
				Objects.requireNonNull(TestAreaUtils.getRegion(p)).setFlag(Flags.TNT, StateFlag.State.DENY);
				p.sendMessage(ChatColor.RED + "TNT is now deactivated");
			} else {
				p.sendMessage(ChatColor.RED + "Incorrect Syntax.");
			}
		} else {
			ProtectedRegion temp = TestAreaUtils.getRegion(p);
			Objects.requireNonNull(temp).setFlag(Flags.TNT, temp.getFlag(Flags.TNT) == StateFlag.State.ALLOW ? StateFlag.State.DENY : StateFlag.State.ALLOW);
			p.sendMessage(ChatColor.RED + "TNT is now " + (temp.getFlag(Flags.TNT) == StateFlag.State.ALLOW ? "activated" : "deactivated"));
		}
	}
}