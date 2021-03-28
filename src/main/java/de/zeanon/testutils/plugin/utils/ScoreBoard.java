package de.zeanon.testutils.plugin.utils;


import de.zeanon.storagemanagercore.external.browniescollections.GapList;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.region.Region;
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

	private final ScoreboardManager scoreboardManager = Objects.notNull(Bukkit.getScoreboardManager());

	private final List<String> scoreBoards = new GapList<>();


	public void initialize(final @NotNull Player p) {
		if (!ScoreBoard.scoreBoards.contains(p.getUniqueId().toString())) {
			ScoreBoard.scoreBoards.add(p.getUniqueId().toString());

			final @Nullable Region tempRegion = TestAreaUtils.getRegion(p);
			final @Nullable Region otherRegion = TestAreaUtils.getOppositeRegion(p);
			if (tempRegion != null && otherRegion != null) {
				ScoreBoard.setScoreBoard(p, tempRegion, otherRegion);
			} else {
				p.setScoreboard(ScoreBoard.scoreboardManager.getNewScoreboard());
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					if (Bukkit.getOnlinePlayers().contains(p)) {
						final @Nullable Region tempRegion = TestAreaUtils.getRegion(p);
						final @Nullable Region otherRegion = TestAreaUtils.getOppositeRegion(p);
						final @NotNull Scoreboard scoreboard = p.getScoreboard();
						if (tempRegion != null && otherRegion != null) {
							ScoreBoard.updateScoreBoard(p, tempRegion, otherRegion, scoreboard);
						} else if (scoreboard.getObjective("testareainfo") != null) {
							p.setScoreboard(ScoreBoard.scoreboardManager.getNewScoreboard());
						}
					} else {
						p.setScoreboard(ScoreBoard.scoreboardManager.getNewScoreboard());
						ScoreBoard.scoreBoards.remove(p.getUniqueId().toString());
						this.cancel();
					}
				}
			}.runTaskTimer(TestUtils.getInstance(), 0, 5);
		}
	}

	private void setScoreBoard(final @NotNull Player p, final @NotNull Region tempRegion, final @NotNull Region otherRegion) {
		final @NotNull Scoreboard scoreboard = ScoreBoard.scoreboardManager.getNewScoreboard();

		final @NotNull Objective infoBoard = scoreboard.registerNewObjective("testareainfo",
																			 "dummy",
																			 ChatColor.DARK_GRAY
																			 + ""
																			 + ChatColor.BOLD
																			 + "["
																			 + ChatColor.DARK_RED
																			 + ""
																			 + ChatColor.BOLD
																			 + "TestArea"
																			 + ChatColor.DARK_GRAY
																			 + ""
																			 + ChatColor.BOLD
																			 + "]");

		infoBoard.setDisplaySlot(DisplaySlot.SIDEBAR);

		final @NotNull String areaname = tempRegion.getName().substring(0, tempRegion.getName().length() - 6);
		final boolean stoplagOther = otherRegion.stoplag();
		final @NotNull StringBuilder headerAndFooterLine = new StringBuilder();
		for (int i = 0; i < (Math.max(areaname.length() + 4, (stoplagOther ? 15 : 17))); i++) {
			headerAndFooterLine.append("=");
		}


		final @NotNull Team header = scoreboard.registerNewTeam("header");
		header.addEntry(ChatColor.AQUA + "" + ChatColor.AQUA);
		header.setPrefix(ChatColor.DARK_GRAY
						 + ""
						 + ChatColor.BOLD
						 + headerAndFooterLine);
		infoBoard.getScore(ChatColor.AQUA + "" + ChatColor.AQUA).setScore(11);

		final @NotNull Score area = infoBoard.getScore(ChatColor.DARK_GRAY
													   + " ["
													   + ChatColor.DARK_RED
													   + "Name"
													   + ChatColor.DARK_GRAY
													   + "]");
		area.setScore(10);

		final @NotNull Team areaName = scoreboard.registerNewTeam("areaname");
		areaName.addEntry(ChatColor.BLACK + "" + ChatColor.BLACK);
		areaName.setPrefix(ChatColor.DARK_GRAY
						   + "   "
						   + ChatColor.BOLD
						   + ">> "
						   + ChatColor.RED
						   + areaname);
		infoBoard.getScore(ChatColor.BLACK + "" + ChatColor.BLACK).setScore(9);

		final @NotNull Score side = infoBoard.getScore(ChatColor.DARK_GRAY
													   + " ["
													   + ChatColor.DARK_RED
													   + "Side"
													   + ChatColor.DARK_GRAY
													   + "]");
		side.setScore(8);

		final @NotNull Team sideName = scoreboard.registerNewTeam("sidename");
		sideName.addEntry(ChatColor.BLUE + "" + ChatColor.BLUE);
		sideName.setPrefix(ChatColor.DARK_GRAY
						   + "   "
						   + ChatColor.BOLD
						   + ">> "
						   + ChatColor.RED
						   + tempRegion.getName().substring(tempRegion.getName().length() - 5));
		infoBoard.getScore(ChatColor.BLUE + "" + ChatColor.BLUE).setScore(7);

		final @NotNull Score tnt = infoBoard.getScore(ChatColor.DARK_GRAY
													  + " ["
													  + ChatColor.DARK_RED
													  + "TNT"
													  + ChatColor.DARK_GRAY
													  + "]");
		tnt.setScore(6);

		final @NotNull Team tntInfoHere = scoreboard.registerNewTeam("tntinfohere");
		tntInfoHere.addEntry(ChatColor.BOLD + "" + ChatColor.BOLD);
		tntInfoHere.setPrefix(ChatColor.DARK_GRAY
							  + "   "
							  + ChatColor.BOLD
							  + ">>"
							  + ChatColor.DARK_RED
							  + " here: "
							  + (tempRegion.tnt()
								 ? ChatColor.GREEN + "allow"
								 : ChatColor.RED + "deny"));
		infoBoard.getScore(ChatColor.BOLD + "" + ChatColor.BOLD).setScore(5);

		final @NotNull Team tntInfoOther = scoreboard.registerNewTeam("tntinfoother");
		tntInfoOther.addEntry(ChatColor.DARK_AQUA + "" + ChatColor.DARK_AQUA);
		tntInfoOther.setPrefix(ChatColor.DARK_GRAY
							   + "   "
							   + ChatColor.BOLD
							   + ">>"
							   + ChatColor.DARK_RED
							   + " other: "
							   + (otherRegion.tnt()
								  ? ChatColor.GREEN + "allow"
								  : ChatColor.RED + "deny"));
		infoBoard.getScore(ChatColor.DARK_AQUA + "" + ChatColor.DARK_AQUA).setScore(4);

		final @NotNull Score stoplag = infoBoard.getScore(ChatColor.DARK_GRAY
														  + " ["
														  + ChatColor.DARK_RED
														  + "Stoplag"
														  + ChatColor.DARK_GRAY
														  + "]");
		stoplag.setScore(3);

		final @NotNull Team stoplagInfoHere = scoreboard.registerNewTeam("stoplaginfohere");
		stoplagInfoHere.addEntry(ChatColor.DARK_BLUE + "" + ChatColor.DARK_BLUE);
		stoplagInfoHere.setPrefix(ChatColor.DARK_GRAY
								  + "   "
								  + ChatColor.BOLD
								  + ">>"
								  + ChatColor.DARK_RED
								  + " here: "
								  + (tempRegion.stoplag()
									 ? ChatColor.GREEN + "active"
									 : ChatColor.RED + "inactive"));
		infoBoard.getScore(ChatColor.DARK_BLUE + "" + ChatColor.DARK_BLUE).setScore(2);

		final @NotNull Team stoplagInfoOther = scoreboard.registerNewTeam("stoplaginfoother");
		stoplagInfoOther.addEntry(ChatColor.DARK_GRAY + "" + ChatColor.DARK_GRAY);
		stoplagInfoOther.setPrefix(ChatColor.DARK_GRAY
								   + "   "
								   + ChatColor.BOLD
								   + ">>"
								   + ChatColor.DARK_RED
								   + " other: "
								   + (stoplagOther
									  ? ChatColor.GREEN + "active"
									  : ChatColor.RED + "inactive"));
		infoBoard.getScore(ChatColor.DARK_GRAY + "" + ChatColor.DARK_GRAY).setScore(1);

		final @NotNull Team footer = scoreboard.registerNewTeam("footer");
		footer.addEntry(ChatColor.DARK_GREEN + "" + ChatColor.DARK_GREEN);
		footer.setPrefix(ChatColor.DARK_GRAY
						 + ""
						 + ChatColor.BOLD
						 + headerAndFooterLine);
		infoBoard.getScore(ChatColor.DARK_GREEN + "" + ChatColor.DARK_GREEN).setScore(0);

		p.setScoreboard(scoreboard);
	}


	private void updateScoreBoard(final @NotNull Player p, final @NotNull Region tempRegion, final @NotNull Region otherRegion, final @NotNull Scoreboard scoreboard) {
		if (scoreboard.getObjective("testareainfo") != null) {
			final @NotNull String areaname = tempRegion.getName().substring(0, tempRegion.getName().length() - 6);
			final boolean stoplagOther = otherRegion.stoplag();
			final @NotNull StringBuilder headerAndFooterLine = new StringBuilder();
			for (int i = 0; i < (Math.max(areaname.length() + 4, (stoplagOther ? 15 : 17))); i++) {
				headerAndFooterLine.append("=");
			}

			Objects.notNull(scoreboard.getTeam("header"))
				   .setPrefix(ChatColor.DARK_GRAY
							  + ""
							  + ChatColor.BOLD
							  + headerAndFooterLine);

			Objects.notNull(scoreboard.getTeam("areaname"))
				   .setPrefix(ChatColor.DARK_GRAY + "   "
							  + ChatColor.BOLD
							  + ">> "
							  + ChatColor.RED
							  + areaname);

			Objects.notNull(scoreboard.getTeam("sidename"))
				   .setPrefix(ChatColor.DARK_GRAY
							  + "   "
							  + ChatColor.BOLD
							  + ">> "
							  + ChatColor.RED
							  + tempRegion.getName().substring(tempRegion.getName().length() - 5));

			Objects.notNull(scoreboard.getTeam("tntinfohere"))
				   .setPrefix(ChatColor.DARK_GRAY
							  + "   "
							  + ChatColor.BOLD
							  + ">>"
							  + ChatColor.DARK_RED
							  + " here: "
							  + (tempRegion.tnt()
								 ? ChatColor.GREEN + "allow"
								 : ChatColor.RED + "deny"));

			Objects.notNull(scoreboard.getTeam("tntinfoother"))
				   .setPrefix(ChatColor.DARK_GRAY
							  + "   "
							  + ChatColor.BOLD
							  + ">>"
							  + ChatColor.DARK_RED
							  + " other: "
							  + (otherRegion.tnt()
								 ? ChatColor.GREEN + "allow"
								 : ChatColor.RED + "deny"));

			Objects.notNull(scoreboard.getTeam("stoplaginfohere"))
				   .setPrefix(ChatColor.DARK_GRAY
							  + "   "
							  + ChatColor.BOLD
							  + ">>"
							  + ChatColor.DARK_RED
							  + " here: "
							  + (tempRegion.stoplag()
								 ? ChatColor.GREEN + "active"
								 : ChatColor.RED + "inactive"));

			Objects.notNull(scoreboard.getTeam("stoplaginfoother"))
				   .setPrefix(ChatColor.DARK_GRAY
							  + "   "
							  + ChatColor.BOLD
							  + ">>"
							  + ChatColor.DARK_RED
							  + " other: "
							  + (stoplagOther
								 ? ChatColor.GREEN + "active"
								 : ChatColor.RED + "inactive"));

			Objects.notNull(scoreboard.getTeam("footer"))
				   .setPrefix(ChatColor.DARK_GRAY
							  + ""
							  + ChatColor.BOLD
							  + headerAndFooterLine);
		} else {
			ScoreBoard.setScoreBoard(p, tempRegion, otherRegion);
		}
	}
}