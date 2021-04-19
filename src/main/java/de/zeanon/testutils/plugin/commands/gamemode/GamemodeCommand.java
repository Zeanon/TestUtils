/*
 * This file is a part of the SteamWar software.
 *
 * Copyright (C) 2021  SteamWar.de-Serverteam
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.zeanon.testutils.plugin.commands.gamemode;

import de.steamwar.commandframework.SWCommand;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;


public class GamemodeCommand extends SWCommand {

	public GamemodeCommand() {
		super("gamemode", "gm", "g");
	}

	@Register(help = true)
	public void help(final Player p, final String... args) {
		p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
					  + ChatColor.RED + "Unknown Gamemode.");
		p.sendMessage(GlobalMessageUtils.MESSAGE_HEAD
					  + ChatColor.RED + "Applicable values are: "
					  + ChatColor.DARK_RED + "survival"
					  + ChatColor.RED + ", "
					  + ChatColor.DARK_RED + "adventure"
					  + ChatColor.RED + ", "
					  + ChatColor.DARK_RED + "creative"
					  + ChatColor.RED + ", "
					  + ChatColor.DARK_RED + "specator");
	}

	@Register
	public void genericCommand(final Player p) {
		if (p.getGameMode() == GameMode.CREATIVE) {
			p.setGameMode(GameMode.SPECTATOR);
		} else {
			p.setGameMode(GameMode.CREATIVE);
		}
	}

	@Register
	public void gamemodeCommand(final Player p, final GameMode gameMode) {
		p.setGameMode(gameMode);
	}
}