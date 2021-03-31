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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import org.bukkit.command.CommandSender;


class SubCommand {

	private final SWCommand swCommand;
	private final Method method;
	private final Function<CommandSender, ?> commandSenderFunction;
	String[] subCommand;
	TypeMapper<?>[] arguments;
	Class<?> varArgType = null;

	public SubCommand(SWCommand swCommand, Method method, String[] subCommand) {
		this(swCommand, method, subCommand, new HashMap<>());
	}

	public SubCommand(SWCommand swCommand, Method method, String[] subCommand, Map<String, TypeMapper<?>> localTypeMapper) {
		this.swCommand = swCommand;
		this.method = method;

		Parameter[] parameters = method.getParameters();
		this.commandSenderFunction = sender -> parameters[0].getType().cast(sender);
		this.subCommand = subCommand;

		this.arguments = new TypeMapper[parameters.length - 1];
		for (int i = 1; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			Class<?> clazz = parameter.getType();
			if (parameter.isVarArgs()) {
				clazz = clazz.getComponentType();
				this.varArgType = clazz;
			}

			SWCommand.Mapper mapper = parameter.getAnnotation(SWCommand.Mapper.class);
			if (clazz.isEnum() && mapper == null && !SWCommandUtils.MAPPER_FUNCTIONS.containsKey(clazz.getTypeName()) && !localTypeMapper.containsKey(clazz.getTypeName())) {
				Class<Enum<?>> enumClass = (Class<Enum<?>>) clazz;
				List<String> tabCompletes = new ArrayList<>();
				for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
					tabCompletes.add(enumConstant.name().toLowerCase());
				}
				this.arguments[i - 1] = SWCommandUtils.createMapper(s -> SWCommandUtils.ENUM_MAPPER.apply(enumClass, s), s -> tabCompletes);
				continue;
			}

			String name = clazz.getTypeName();
			if (mapper != null) {
				name = mapper.value();
			}
			if (localTypeMapper.containsKey(name)) {
				this.arguments[i - 1] = localTypeMapper.getOrDefault(name, SWCommandUtils.ERROR_FUNCTION);
			} else {
				this.arguments[i - 1] = SWCommandUtils.MAPPER_FUNCTIONS.getOrDefault(name, SWCommandUtils.ERROR_FUNCTION);
			}
		}
	}

	boolean invoke(CommandSender commandSender, String[] args) {
		if (args.length < this.arguments.length + this.subCommand.length - (this.varArgType != null ? 1 : 0)) {
			return false;
		}
		if (this.varArgType == null && args.length > this.arguments.length + this.subCommand.length) {
			return false;
		}
		try {
			Object[] objects = SWCommandUtils.generateArgumentArray(this.arguments, args, this.varArgType, this.subCommand);
			objects[0] = this.commandSenderFunction.apply(commandSender);
			this.method.setAccessible(true);
			this.method.invoke(this.swCommand, objects);
		} catch (IllegalAccessException | RuntimeException | InvocationTargetException e) {
			throw new SecurityException(e.getMessage(), e);
		} catch (CommandParseException e) {
			return false;
		}
		return true;
	}

	List<String> tabComplete(CommandSender commandSender, String[] args) {
		if (this.varArgType == null && args.length > this.arguments.length + this.subCommand.length) {
			return null;
		}
		int index = 0;
		List<String> argsList = new LinkedList<>(Arrays.asList(args));
		for (String value : this.subCommand) {
			String s = argsList.remove(0);
			index++;
			if (argsList.isEmpty()) {
				return Collections.singletonList(value);
			}
			if (!value.equalsIgnoreCase(s)) {
				return null;
			}
		}
		for (TypeMapper<?> argument : this.arguments) {
			String s = argsList.remove(0);
			if (argsList.isEmpty()) {
				return argument.tabCompletes(commandSender, Arrays.copyOf(args, args.length - 1), s);
			}
			try {
				if (argument.map(Arrays.copyOf(args, index), s) == null) {
					return null;
				}
			} catch (Exception e) {
				return null;
			}
			index++;
		}
		if (this.varArgType != null && !argsList.isEmpty()) {
			while (!argsList.isEmpty()) {
				String s = argsList.remove(0);
				if (argsList.isEmpty()) {
					return this.arguments[this.arguments.length - 1].tabCompletes(commandSender, Arrays.copyOf(args, args.length - 1), s);
				}
				try {
					if (this.arguments[this.arguments.length - 1].map(Arrays.copyOf(args, index), s) == null) {
						return null;
					}
				} catch (Exception e) {
					return null;
				}
				index++;
			}
		}
		return null;
	}
}