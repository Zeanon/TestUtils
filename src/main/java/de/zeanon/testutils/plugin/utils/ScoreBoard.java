package de.zeanon.testutils.plugin.utils;


import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanagercore.external.browniescollections.GapList;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.commands.Stoplag;
import java.util.List;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class ScoreBoard {

	private final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

	private final List<String> scoreBoards = new GapList<>();


	public void execute(final @NotNull Player p) {
		if (!ScoreBoard.scoreBoards.contains(p.getUniqueId().toString())) {
			ScoreBoard.scoreBoards.add(p.getUniqueId().toString());
			new BukkitRunnable() {
				@Override
				public void run() {
					if (Bukkit.getOnlinePlayers().contains(p)) {
						ScoreBoard.setScoreBoard(p);
					} else {
						ScoreBoard.scoreBoards.remove(p.getUniqueId().toString());
						this.cancel();
					}
				}
			}.runTaskTimer(TestUtils.getInstance(), 0, 10);
		}
	}

	private void setScoreBoard(final @NotNull Player p) {
		final @NotNull Scoreboard scoreboard = Objects.notNull(ScoreBoard.scoreboardManager).getNewScoreboard();
		final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
		if (tempRegion != null) {
			final @NotNull Objective infoBoard = scoreboard.registerNewObjective("infoBoard", "dummy", ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[" + ChatColor.DARK_RED + "" + ChatColor.BOLD + TestUtils.getInstance().getName() + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "]");

			final @NotNull Score header = infoBoard.getScore(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "!================!");
			header.setScore(7);

			final @NotNull Score area = infoBoard.getScore(ChatColor.DARK_GRAY + " [" + ChatColor.DARK_RED + "TestArea" + ChatColor.DARK_GRAY + "]");
			area.setScore(6);

			final @NotNull Score areaName = infoBoard.getScore(ChatColor.DARK_GRAY + "   " + ChatColor.BOLD + ">> " + ChatColor.RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + " <<");
			areaName.setScore(5);

			final @NotNull Score tnt = infoBoard.getScore(ChatColor.DARK_GRAY + " [" + ChatColor.DARK_RED + "TNT" + ChatColor.DARK_GRAY + "]");
			tnt.setScore(4);

			final @NotNull Score tntInfo = infoBoard.getScore(ChatColor.DARK_GRAY + "   " + ChatColor.BOLD + ">> " + (tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW ? ChatColor.GREEN + "allow" : ChatColor.RED + "deny") + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + " <<");
			tntInfo.setScore(3);

			final @NotNull Score stoplag = infoBoard.getScore(ChatColor.DARK_GRAY + " [" + ChatColor.DARK_RED + "Stoplag" + ChatColor.DARK_GRAY + "]");
			stoplag.setScore(2);

			final @NotNull Score stoplagInfo = infoBoard.getScore(ChatColor.DARK_GRAY + "   " + ChatColor.BOLD + ">> " + (Stoplag.isStopLagRegion(tempRegion) ? ChatColor.GREEN + "activated" : ChatColor.RED + "deactivated") + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + " <<");
			stoplagInfo.setScore(1);

			final @NotNull Score footer = infoBoard.getScore(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "¡================¡");
			footer.setScore(0);

			infoBoard.setDisplaySlot(DisplaySlot.SIDEBAR);
		}

		p.setScoreboard(scoreboard);
	}
}