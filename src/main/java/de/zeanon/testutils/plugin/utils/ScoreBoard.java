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


	public void initialize(final @NotNull Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!ScoreBoard.scoreBoards.contains(p.getUniqueId().toString())) {
					ScoreBoard.scoreBoards.add(p.getUniqueId().toString());

					final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
					if (tempRegion != null) {
						ScoreBoard.setScoreBoard(p, tempRegion);
					} else {
						p.setScoreboard(Objects.notNull(ScoreBoard.scoreboardManager).getNewScoreboard());
					}

					new BukkitRunnable() {
						@Override
						public void run() {
							if (Bukkit.getOnlinePlayers().contains(p)) {
								final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
								if (tempRegion != null) {
									ScoreBoard.updateScoreBoard(p, tempRegion);
								} else {
									p.setScoreboard(Objects.notNull(ScoreBoard.scoreboardManager).getNewScoreboard());
								}
							} else {
								ScoreBoard.scoreBoards.remove(p.getUniqueId().toString());
								this.cancel();
							}
						}
					}.runTaskTimer(TestUtils.getInstance(), 0, 10);
				}
			}
		}.runTask(TestUtils.getInstance());
	}

	private void setScoreBoard(final @NotNull Player p, final @NotNull ProtectedRegion tempRegion) {
		final @NotNull Scoreboard scoreboard = Objects.notNull(ScoreBoard.scoreboardManager).getNewScoreboard();

		final @NotNull Objective infoBoard = scoreboard.registerNewObjective("infoBoard",
																			 "dummy",
																			 ChatColor.DARK_GRAY
																			 + ""
																			 + ChatColor.BOLD
																			 + "["
																			 + ChatColor.DARK_RED
																			 + ""
																			 + ChatColor.BOLD
																			 + TestUtils.getInstance().getName()
																			 + ChatColor.DARK_GRAY
																			 + ""
																			 + ChatColor.BOLD
																			 + "]");

		infoBoard.setDisplaySlot(DisplaySlot.SIDEBAR);

		final @NotNull String areaname = tempRegion.getId().substring(9, tempRegion.getId().length() - 6);
		final @NotNull StringBuilder headerAndFooterLine = new StringBuilder();
		for (int i = 0; i < (Math.max(areaname.length(), 17)); i++) {
			headerAndFooterLine.append("=");
		}


		final @NotNull Score header = infoBoard.getScore(ChatColor.DARK_GRAY
														 + ""
														 + ChatColor.BOLD
														 + headerAndFooterLine
														 + ChatColor.AQUA);
		header.setScore(9);

		final @NotNull Score area = infoBoard.getScore(ChatColor.DARK_GRAY
													   + " ["
													   + ChatColor.DARK_RED
													   + "TestArea"
													   + ChatColor.DARK_GRAY
													   + "]");
		area.setScore(8);

		final @NotNull Team areaName = scoreboard.registerNewTeam("areaname");
		areaName.addEntry(ChatColor.AQUA + "" + ChatColor.AQUA);
		areaName.setPrefix(ChatColor.DARK_GRAY
						   + "   "
						   + ChatColor.BOLD
						   + ">> "
						   + ChatColor.RED
						   + areaname
						   + ChatColor.DARK_GRAY
						   + ""
						   + ChatColor.BOLD
						   + " <<");
		infoBoard.getScore(ChatColor.AQUA + "" + ChatColor.AQUA).setScore(7);

		final @NotNull Score side = infoBoard.getScore(ChatColor.DARK_GRAY
													   + " ["
													   + ChatColor.DARK_RED
													   + "Side"
													   + ChatColor.DARK_GRAY
													   + "]");
		side.setScore(6);

		final @NotNull Team sideName = scoreboard.registerNewTeam("sidename");
		sideName.addEntry(ChatColor.BLACK + "" + ChatColor.BLACK);
		sideName.setPrefix(ChatColor.DARK_GRAY
						   + "   "
						   + ChatColor.BOLD
						   + ">> "
						   + ChatColor.RED
						   + tempRegion.getId().substring(tempRegion.getId().length() - 5)
						   + ChatColor.DARK_GRAY
						   + ""
						   + ChatColor.BOLD
						   + " <<");
		infoBoard.getScore(ChatColor.BLACK + "" + ChatColor.BLACK).setScore(5);

		final @NotNull Score tnt = infoBoard.getScore(ChatColor.DARK_GRAY
													  + " ["
													  + ChatColor.DARK_RED
													  + "TNT"
													  + ChatColor.DARK_GRAY
													  + "]");
		tnt.setScore(4);

		final @NotNull Team tntInfo = scoreboard.registerNewTeam("tntinfo");
		tntInfo.addEntry(ChatColor.BLUE + "" + ChatColor.BLUE);
		tntInfo.setPrefix(ChatColor.DARK_GRAY
						  + "   "
						  + ChatColor.BOLD
						  + ">> "
						  + (tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW
							 ? ChatColor.GREEN + "allow"
							 : ChatColor.RED + "deny")
						  + ChatColor.DARK_GRAY
						  + ""
						  + ChatColor.BOLD
						  + " <<");
		infoBoard.getScore(ChatColor.BLUE + "" + ChatColor.BLUE).setScore(3);

		final @NotNull Score stoplag = infoBoard.getScore(ChatColor.DARK_GRAY
														  + " ["
														  + ChatColor.DARK_RED
														  + "Stoplag"
														  + ChatColor.DARK_GRAY
														  + "]");
		stoplag.setScore(2);

		final @NotNull Team stoplagInfo = scoreboard.registerNewTeam("stoplaginfo");
		stoplagInfo.addEntry(ChatColor.BOLD + "" + ChatColor.BOLD);
		stoplagInfo.setPrefix(ChatColor.DARK_GRAY
							  + "   "
							  + ChatColor.BOLD
							  + ">> "
							  + (Stoplag.isStopLagRegion(tempRegion)
								 ? ChatColor.GREEN + "activated"
								 : ChatColor.RED + "deactivated")
							  + ChatColor.DARK_GRAY
							  + ""
							  + ChatColor.BOLD
							  + " <<");
		infoBoard.getScore(ChatColor.BOLD + "" + ChatColor.BOLD).setScore(1);

		final @NotNull Score footer = infoBoard.getScore(ChatColor.DARK_GRAY
														 + ""
														 + ChatColor.BOLD
														 + headerAndFooterLine
														 + ChatColor.BLACK);
		footer.setScore(0);

		p.setScoreboard(scoreboard);
	}

	private void updateScoreBoard(final @NotNull Player p, final @NotNull ProtectedRegion tempRegion) {
		final @NotNull Scoreboard scoreboard = p.getScoreboard();

		if (scoreboard.getObjective("infoBoard") != null) {
			Objects.notNull(scoreboard.getTeam("areaname"))
				   .setPrefix(ChatColor.DARK_GRAY + "   "
							  + ChatColor.BOLD
							  + ">> "
							  + ChatColor.RED
							  + tempRegion.getId().substring(9, tempRegion.getId().length() - 6)
							  + ChatColor.DARK_GRAY
							  + ""
							  + ChatColor.BOLD
							  + " <<");
			Objects.notNull(scoreboard.getTeam("sidename"))
				   .setPrefix(ChatColor.DARK_GRAY
							  + "   "
							  + ChatColor.BOLD
							  + ">> "
							  + ChatColor.RED
							  + tempRegion.getId().substring(tempRegion.getId().length() - 5)
							  + ChatColor.DARK_GRAY
							  + ""
							  + ChatColor.BOLD
							  + " <<");
			Objects.notNull(scoreboard.getTeam("tntinfo"))
				   .setPrefix(ChatColor.DARK_GRAY
							  + "   "
							  + ChatColor.BOLD
							  + ">> "
							  + (tempRegion.getFlag(Flags.TNT) == StateFlag.State.ALLOW
								 ? ChatColor.GREEN + "allow"
								 : ChatColor.RED + "deny")
							  + ChatColor.DARK_GRAY
							  + ""
							  + ChatColor.BOLD
							  + " <<");
			Objects.notNull(scoreboard.getTeam("stoplaginfo"))
				   .setPrefix(ChatColor.DARK_GRAY
							  + "   "
							  + ChatColor.BOLD
							  + ">> "
							  + (Stoplag.isStopLagRegion(tempRegion)
								 ? ChatColor.GREEN + "activated"
								 : ChatColor.RED + "deactivated")
							  + ChatColor.DARK_GRAY
							  + ""
							  + ChatColor.BOLD
							  + " <<");
		} else {
			ScoreBoard.setScoreBoard(p, tempRegion);
		}
	}
}