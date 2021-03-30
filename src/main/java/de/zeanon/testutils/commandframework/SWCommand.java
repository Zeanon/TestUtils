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


import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.IntPredicate;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public abstract class SWCommand {

	private final Command command;
	private final List<SubCommand> commandSet = new ArrayList<>();
	private final List<SubCommand> commandHelpSet = new ArrayList<>();
	private final Map<String, TypeMapper<?>> localTypeMapper = new HashMap<>();

	protected SWCommand(String command) {
		this(command, new String[0]);
	}

	protected SWCommand(String command, String... aliases) {
		this.command = new Command(command, "", "/" + command, Arrays.asList(aliases)) {
			@Override
			public boolean execute(CommandSender sender, String alias, String[] args) {
				for (SubCommand subCommand : SWCommand.this.commandSet) {
					if (subCommand.invoke(sender, args)) {
						return false;
					}
				}
				for (SubCommand subCommand : SWCommand.this.commandHelpSet) {
					if (subCommand.invoke(sender, args)) {
						return false;
					}
				}
				return false;
			}

			@Override
			public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
				List<String> strings = new ArrayList<>();
				for (SubCommand subCommand : SWCommand.this.commandSet) {
					List<String> tabCompletes = subCommand.tabComplete(sender, args);
					if (tabCompletes != null) {
						strings.addAll(tabCompletes);
					}
				}
				strings = new ArrayList<>(strings);
				for (int i = strings.size() - 1; i >= 0; i--) {
					if (!strings.get(i).startsWith(args[args.length - 1])) {
						strings.remove(i);
					}
				}
				return strings;
			}
		};
		this.register();

		for (Method method : this.getClass().getDeclaredMethods()) {
			this.addMapper(Mapper.class, method, i -> i == 0, false, TypeMapper.class, (anno, typeMapper) -> {
				if (anno.local()) {
					this.localTypeMapper.put(anno.value(), typeMapper);
				} else {
					SWCommandUtils.addMapper(anno.value(), typeMapper);
				}
			});
			this.addMapper(ClassMapper.class, method, i -> i != 0, false, TypeMapper.class, (anno, typeMapper) -> {
				SWCommandUtils.addMapper(anno.value().getTypeName(), typeMapper);
			});
			this.add(Register.class, method, i -> i == 2, true, null, (anno, parameters) -> {
				if (!anno.help()) {
					return;
				}
				if (!parameters[parameters.length - 1].isVarArgs()) {
					Bukkit.getLogger().log(Level.WARNING, "The method '" + method.toString() + "' is lacking the varArgs parameters as last Argument");
					return;
				}
				if (parameters[parameters.length - 1].getType().getComponentType() != String.class) {
					Bukkit.getLogger().log(Level.WARNING, "The method '" + method.toString() + "' is lacking the varArgs parameters of type '" + String.class.getTypeName() + "' as last Argument");
					return;
				}
				this.commandHelpSet.add(new SubCommand(this, method, anno.value()));
			});
		}
		for (Method method : this.getClass().getDeclaredMethods()) {
			this.add(Register.class, method, i -> i > 0, true, null, (anno, parameters) -> {
				if (anno.help()) {
					return;
				}
				for (int i = 1; i < parameters.length; i++) {
					Parameter parameter = parameters[i];
					Class<?> clazz = parameter.getType();
					if (parameter.isVarArgs() && i == parameters.length - 1) {
						clazz = parameter.getType().getComponentType();
					}
					Mapper mapper = parameter.getAnnotation(Mapper.class);
					if (clazz.isEnum() && mapper == null && !SWCommandUtils.MAPPER_FUNCTIONS.containsKey(clazz.getTypeName())) {
						continue;
					}
					String name = clazz.getTypeName();
					if (mapper != null) {
						name = mapper.value();
					}
					if (!SWCommandUtils.MAPPER_FUNCTIONS.containsKey(name)) {
						Bukkit.getLogger().log(Level.WARNING, "The parameter '" + parameter.toString() + "' is using an unsupported Mapper of type '" + name + "'");
						return;
					}
				}
				this.commandSet.add(new SubCommand(this, method, anno.value()));
			});

			this.commandSet.sort(Comparator.comparingInt(o -> -o.subCommand.length));
			this.commandHelpSet.sort(Comparator.comparingInt(o -> -o.subCommand.length));
		}
	}

	protected void unregister() {
		SWCommandUtils.knownCommandMap.remove(this.command.getName());
		for (String alias : this.command.getAliases()) {
			SWCommandUtils.knownCommandMap.remove(alias);
		}
		this.command.unregister(SWCommandUtils.commandMap);
	}

	protected void register() {
		SWCommandUtils.commandMap.register("steamwar", this.command);
	}

	private <T extends Annotation> void add(Class<T> annotation, Method method, IntPredicate parameterTester, boolean firstParameter, Class<?> returnType, BiConsumer<T, Parameter[]> consumer) {
		T anno = SWCommandUtils.getAnnotation(method, annotation);
		if (anno == null) {
			return;
		}

		Parameter[] parameters = method.getParameters();
		if (!parameterTester.test(parameters.length)) {
			Bukkit.getLogger().log(Level.WARNING, "The method '" + method.toString() + "' is lacking parameters or has too many");
			return;
		}
		if (firstParameter && !CommandSender.class.isAssignableFrom(parameters[0].getType())) {
			Bukkit.getLogger().log(Level.WARNING, "The method '" + method.toString() + "' is lacking the first parameter of type '" + CommandSender.class.getTypeName() + "'");
			return;
		}
		if (returnType != null && method.getReturnType() != returnType) {
			Bukkit.getLogger().log(Level.WARNING, "The method '" + method.toString() + "' is lacking the desired return type '" + returnType.getTypeName() + "'");
			return;
		}
		consumer.accept(anno, parameters);
	}

	private <T extends Annotation> void addMapper(Class<T> annotation, Method method, IntPredicate parameterTester, boolean firstParameter, Class<?> returnType, BiConsumer<T, TypeMapper<?>> consumer) {
		this.add(annotation, method, parameterTester, firstParameter, returnType, (anno, parameters) -> {
			try {
				method.setAccessible(true);
				Object object = method.invoke(this);
				consumer.accept(anno, (TypeMapper<?>) object);
			} catch (Exception e) {
				throw new SecurityException(e.getMessage(), e);
			}
		});
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	protected @interface Register {

		String[] value() default {};

		boolean help() default false;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.PARAMETER, ElementType.METHOD})
	protected @interface Mapper {

		String value();

		boolean local() default false;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	protected @interface ClassMapper {

		Class<?> value();
	}
}