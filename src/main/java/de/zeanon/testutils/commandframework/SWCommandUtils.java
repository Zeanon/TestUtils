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

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;


public class SWCommandUtils {

	static final Map<String, TypeMapper<?>> MAPPER_FUNCTIONS = new HashMap<>();
	static final TypeMapper<?> ERROR_FUNCTION = SWCommandUtils.createMapper(s -> {
		throw new SecurityException();
	}, s -> Collections.emptyList());
	static final BiFunction<Class<Enum<?>>, String, Enum<?>> ENUM_MAPPER = (enumClass, s) -> {
		Enum<?>[] enums = enumClass.getEnumConstants();
		for (Enum<?> e : enums) {
			if (e.name().equalsIgnoreCase(s)) {
				return e;
			}
		}
		return null;
	};
	static final CommandMap commandMap;
	static final Map<String, Command> knownCommandMap;

	static {
		SWCommandUtils.addMapper(boolean.class, Boolean.class, SWCommandUtils.createMapper(Boolean::parseBoolean, s -> Arrays.asList("true", "false")));
		SWCommandUtils.addMapper(float.class, Float.class, SWCommandUtils.createMapper(Float::parseFloat, SWCommandUtils.numberCompleter(Float::parseFloat)));
		SWCommandUtils.addMapper(double.class, Double.class, SWCommandUtils.createMapper(Double::parseDouble, SWCommandUtils.numberCompleter(Double::parseDouble)));
		SWCommandUtils.addMapper(int.class, Integer.class, SWCommandUtils.createMapper(Integer::parseInt, SWCommandUtils.numberCompleter(Integer::parseInt)));
		SWCommandUtils.MAPPER_FUNCTIONS.put(String.class.getTypeName(), SWCommandUtils.createMapper(s -> s, Collections::singletonList));
		SWCommandUtils.MAPPER_FUNCTIONS.put(Player.class.getTypeName(), SWCommandUtils.createMapper(Bukkit::getPlayer, s -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList())));
		SWCommandUtils.MAPPER_FUNCTIONS.put(GameMode.class.getTypeName(), SWCommandUtils.createMapper(s -> {
			s = s.toLowerCase();
			if (s.equals("s") || s.equals("survival") || s.equals("0")) {
				return GameMode.SURVIVAL;
			}
			if (s.equals("c") || s.equals("creative") || s.equals("1")) {
				return GameMode.CREATIVE;
			}
			if (s.equals("sp") || s.equals("spectator") || s.equals("3")) {
				return GameMode.SPECTATOR;
			}
			if (s.equals("a") || s.equals("adventure") || s.equals("2")) {
				return GameMode.ADVENTURE;
			}
			throw new SecurityException();
		}, s -> Arrays.asList("s", "survival", "0", "c", "creative", "1", "sp", "specator", "3", "a", "adventure", "2")));
	}

	static {
		try {
			final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
		} catch (NoSuchFieldException | IllegalAccessException exception) {
			Bukkit.shutdown();
			throw new SecurityException("Oh shit. Commands cannot be registered.", exception);
		}
		try {
			final Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
			knownCommandsField.setAccessible(true);
			knownCommandMap = (Map<String, Command>) knownCommandsField.get(SWCommandUtils.commandMap);
		} catch (NoSuchFieldException | IllegalAccessException exception) {
			Bukkit.shutdown();
			throw new SecurityException("Oh shit. Commands cannot be registered.", exception);
		}
	}

	private SWCommandUtils() {
		throw new IllegalStateException("Utility Class");
	}

	public static <T> void addMapper(Class<T> clazz, TypeMapper<T> mapper) {
		SWCommandUtils.addMapper(clazz.getTypeName(), mapper);
	}

	public static void addMapper(String name, TypeMapper<?> mapper) {
		if (SWCommandUtils.MAPPER_FUNCTIONS.containsKey(name)) {
			return;
		}
		SWCommandUtils.MAPPER_FUNCTIONS.put(name, mapper);
	}

	public static <T> TypeMapper<T> createMapper(Function<String, T> mapper, Function<String, List<String>> tabCompleter) {
		return SWCommandUtils.createMapper(mapper, (commandSender, s) -> tabCompleter.apply(s));
	}

	public static <T> TypeMapper<T> createMapper(Function<String, T> mapper, BiFunction<CommandSender, String, List<String>> tabCompleter) {
		return new TypeMapper<T>() {
			@Override
			public T map(String[] previous, String s) {
				return mapper.apply(s);
			}

			@Override
			public List<String> tabCompletes(CommandSender commandSender, String[] previous, String s) {
				return tabCompleter.apply(commandSender, s);
			}
		};
	}

	static Object[] generateArgumentArray(TypeMapper<?>[] parameters, String[] args, Class<?> varArgType, String[] subCommand) throws CommandParseException {
		Object[] arguments = new Object[parameters.length + 1];
		int index = 0;
		while (index < subCommand.length) {
			if (!args[index].equalsIgnoreCase(subCommand[index])) {
				throw new CommandParseException();
			}
			index++;
		}

		if (varArgType != null && index > args.length - 1) {
			Object varArgument = Array.newInstance(varArgType, 0);
			arguments[arguments.length - 1] = varArgument;
		} else {
			for (int i = 0; i < parameters.length - (varArgType != null ? 1 : 0); i++) {
				arguments[i + 1] = parameters[i].map(Arrays.copyOf(args, index), args[index]);
				index++;
				if (arguments[i + 1] == null) {
					throw new CommandParseException();
				}
			}

			if (varArgType != null) {
				int length = args.length - parameters.length - subCommand.length + 1;
				Object varArgument = Array.newInstance(varArgType, length);
				arguments[arguments.length - 1] = varArgument;

				for (int i = 0; i < length; i++) {
					Object value = parameters[parameters.length - 1].map(Arrays.copyOf(args, index), args[index]);
					if (value == null) {
						throw new CommandParseException();
					}
					Array.set(varArgument, i, value);
					index++;
				}
			}
		}
		return arguments;
	}

	static <T extends Annotation> T getAnnotation(Method method, Class<T> annotation) {
		if (method.getAnnotations().length != 1) {
			return null;
		}
		return method.getAnnotation(annotation);
	}

	private static void addMapper(Class<?> clazz, Class<?> alternativeClazz, TypeMapper<?> mapper) {
		SWCommandUtils.MAPPER_FUNCTIONS.put(clazz.getTypeName(), mapper);
		SWCommandUtils.MAPPER_FUNCTIONS.put(alternativeClazz.getTypeName(), mapper);
	}

	private static Function<String, List<String>> numberCompleter(Function<String, ?> mapper) {
		return s -> {
			try {
				mapper.apply(s);
				return Collections.singletonList(s);
			} catch (Exception e) {
				return Collections.emptyList();
			}
		};
	}
}