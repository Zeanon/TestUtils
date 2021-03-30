/*
 * This file is a part of the SteamWar software.
 *
 * Copyright (C) 2020  SteamWar.de-Serverteam
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

package de.zeanon.testutils.commandframework;

import java.util.List;
import org.bukkit.command.CommandSender;


public interface TypeMapper<T> {

	T map(String[] previousArguments, String s);

	List<String> tabCompletes(CommandSender commandSender, String[] previousArguments, String s);
}