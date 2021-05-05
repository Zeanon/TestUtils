package de.zeanon.testutils.plugin.utils;


import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.regionsystem.flags.Flag;
import de.zeanon.testutils.regionsystem.flags.flagvalues.STOPLAG;
import de.zeanon.testutils.regionsystem.flags.flagvalues.TNT;
import de.zeanon.testutils.regionsystem.region.DefinedRegion;
import java.util.HashSet;
import java.util.Set;
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

	private final @NotNull ScoreboardManager scoreboardManager = Objects.notNull(Bukkit.getScoreboardManager());
	private final @NotNull Set<Player> registeredPlayers = new HashSet<>();

	public void initialize() {
		ScoreBoard.registerPlayers();

		ScoreBoard.initTask();
	}

	public void registerPlayers() {
		for (final @NotNull Player p : Bukkit.getOnlinePlayers()) {
			ScoreBoard.register(p);
		}
	}

	public void initTask() {
		new BukkitRunnable() {
			@Override
			public void run() {
				ScoreBoard.registeredPlayers.forEach(p -> {
					final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p);
					final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);
					final @NotNull Scoreboard scoreboard = p.getScoreboard();
					if (tempRegion != null && otherRegion != null) {
						ScoreBoard.updateScoreBoard(p, tempRegion, otherRegion, scoreboard);
					} else if (scoreboard.getObjective("testareainfo") != null) {
						p.setScoreboard(ScoreBoard.scoreboardManager.getNewScoreboard());
					}
				});
			}
		}.runTaskTimer(TestUtils.getInstance(), 0L, 5L);
	}

	public void unregister(final @NotNull Player p) {
		p.setScoreboard(ScoreBoard.scoreboardManager.getNewScoreboard());
		ScoreBoard.registeredPlayers.remove(p);
	}

	public void register(final @NotNull Player p) {
		if (!ScoreBoard.registeredPlayers.contains(p)) {
			final @Nullable DefinedRegion tempRegion = TestAreaUtils.getRegion(p);
			final @Nullable DefinedRegion otherRegion = TestAreaUtils.getOppositeRegion(p);

			if (tempRegion != null && otherRegion != null) {
				ScoreBoard.setScoreBoard(p, tempRegion, otherRegion);
			} else {
				p.setScoreboard(ScoreBoard.scoreboardManager.getNewScoreboard());
			}

			ScoreBoard.registeredPlayers.add(p);
		}
	}

	private void setScoreBoard(final @NotNull Player p, final @NotNull DefinedRegion tempRegion, final @NotNull DefinedRegion otherRegion) {
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
		final boolean stoplagOther = otherRegion.getFlagOrDefault(Flag.STOPLAG, STOPLAG.INACTIVE) == STOPLAG.ACTIVE;
		final @NotNull StringBuilder headerAndFooterLine = new StringBuilder();
		for (int i = 0; i < (Math.max(areaname.length() + 4, (stoplagOther ? 14 : 16))); i++) {
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
							  + Objects.notNull(tempRegion.getFlagOrDefault(Flag.TNT, TNT.ALLOW)).getChatValue());
		infoBoard.getScore(ChatColor.BOLD + "" + ChatColor.BOLD).setScore(5);

		final @NotNull Team tntInfoOther = scoreboard.registerNewTeam("tntinfoother");
		tntInfoOther.addEntry(ChatColor.DARK_AQUA + "" + ChatColor.DARK_AQUA);
		tntInfoOther.setPrefix(ChatColor.DARK_GRAY
							   + "   "
							   + ChatColor.BOLD
							   + ">>"
							   + ChatColor.DARK_RED
							   + " other: "
							   + Objects.notNull(otherRegion.getFlagOrDefault(Flag.TNT, TNT.ALLOW)).getChatValue());
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
								  + Objects.notNull(tempRegion.getFlagOrDefault(Flag.STOPLAG, STOPLAG.INACTIVE)).getChatValue());
		infoBoard.getScore(ChatColor.DARK_BLUE + "" + ChatColor.DARK_BLUE).setScore(2);

		final @NotNull Team stoplagInfoOther = scoreboard.registerNewTeam("stoplaginfoother");
		stoplagInfoOther.addEntry(ChatColor.DARK_GRAY + "" + ChatColor.DARK_GRAY);
		stoplagInfoOther.setPrefix(ChatColor.DARK_GRAY
								   + "   "
								   + ChatColor.BOLD
								   + ">>"
								   + ChatColor.DARK_RED
								   + " other: "
								   + Objects.notNull(otherRegion.getFlagOrDefault(Flag.STOPLAG, STOPLAG.INACTIVE)).getChatValue());
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


	private void updateScoreBoard(final @NotNull Player p, final @NotNull DefinedRegion tempRegion, final @NotNull DefinedRegion otherRegion, final @NotNull Scoreboard scoreboard) {
		if (scoreboard.getObjective("testareainfo") != null) {
			final @NotNull String areaname = tempRegion.getName().substring(0, tempRegion.getName().length() - 6);
			final boolean stoplagOther = otherRegion.getFlagOrDefault(Flag.STOPLAG, STOPLAG.INACTIVE) == STOPLAG.ACTIVE;
			final @NotNull StringBuilder headerAndFooterLine = new StringBuilder();
			for (int i = 0; i < (Math.max(areaname.length() + 4, (stoplagOther ? 14 : 16))); i++) {
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
							  + Objects.notNull(tempRegion.getFlagOrDefault(Flag.TNT, TNT.ALLOW)).getChatValue());

			Objects.notNull(scoreboard.getTeam("tntinfoother"))
				   .setPrefix(ChatColor.DARK_GRAY
							  + "   "
							  + ChatColor.BOLD
							  + ">>"
							  + ChatColor.DARK_RED
							  + " other: "
							  + Objects.notNull(otherRegion.getFlagOrDefault(Flag.TNT, TNT.ALLOW)).getChatValue());

			Objects.notNull(scoreboard.getTeam("stoplaginfohere"))
				   .setPrefix(ChatColor.DARK_GRAY
							  + "   "
							  + ChatColor.BOLD
							  + ">>"
							  + ChatColor.DARK_RED
							  + " here: "
							  + Objects.notNull(tempRegion.getFlagOrDefault(Flag.STOPLAG, STOPLAG.INACTIVE)).getChatValue());

			Objects.notNull(scoreboard.getTeam("stoplaginfoother"))
				   .setPrefix(ChatColor.DARK_GRAY
							  + "   "
							  + ChatColor.BOLD
							  + ">>"
							  + ChatColor.DARK_RED
							  + " other: "
							  + Objects.notNull(otherRegion.getFlagOrDefault(Flag.STOPLAG, STOPLAG.INACTIVE)).getChatValue());

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