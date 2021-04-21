package de.zeanon.testutils.plugin.utils;

import de.zeanon.storagemanagercore.internal.base.cache.provider.CollectionsProvider;
import de.zeanon.storagemanagercore.internal.base.exceptions.FileParseException;
import de.zeanon.storagemanagercore.internal.base.exceptions.ObjectNullException;
import de.zeanon.storagemanagercore.internal.base.exceptions.RuntimeIOException;
import de.zeanon.storagemanagercore.internal.base.interfaces.DataMap;
import de.zeanon.storagemanagercore.internal.base.settings.Comment;
import de.zeanon.storagemanagercore.internal.base.settings.Reload;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.update.Update;
import de.zeanon.thunderfilemanager.ThunderFileManager;
import de.zeanon.thunderfilemanager.internal.files.config.ThunderConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class ConfigUtils {

	@Getter
	private ThunderConfig config;

	public void loadConfigs() {
		@Nullable Throwable cause = null;
		try {
			ConfigUtils.config = ThunderFileManager.thunderConfig(TestUtils.getPluginFolder(), "config")
												   .fromResource("resources/config.tf")
												   .reloadSetting(Reload.INTELLIGENT)
												   .commentSetting(Comment.PRESERVE)
												   .concurrentData(true)
												   .create();

			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> [Configs] >> 'config.tf' loaded.");
		} catch (final @NotNull RuntimeIOException | FileParseException e) {
			System.err.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> [Configs] >> 'config.tf' could not be loaded.");
			e.printStackTrace();
			cause = e;
		}

		if (cause != null) {
			throw new RuntimeIOException(cause);
		}
	}

	public void initConfigs() {
		if (!ConfigUtils.getConfig().hasKeyUseArray("Plugin Version")
			|| !Objects.notNull(ConfigUtils.getConfig().getStringUseArray("Plugin Version"))
					   .equals(de.zeanon.testutils.TestUtils.getInstance().getDescription().getVersion())) {
			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Updating Configs...");
			Update.checkConfigUpdate();
			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Config files are updated successfully.");
		}
	}

	/**
	 * get an int from the config.
	 *
	 * @param key the Config key.
	 *
	 * @return value.
	 */
	public int getInt(final @NotNull String... key) {
		try {
			if (ConfigUtils.getConfig().hasKeyUseArray(key)) {
				return ConfigUtils.getConfig().getIntUseArray(key);
			} else {
				throw new ObjectNullException();
			}
		} catch (Exception e) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Update.updateConfig();
				}
			}.runTaskAsynchronously(TestUtils.getInstance());
			return ConfigUtils.getDefaultValue(key);
		}
	}

	/**
	 * get a boolean from the config.
	 *
	 * @param key the Config key.
	 *
	 * @return value.
	 */
	public boolean getBoolean(final @NotNull String... key) {
		try {
			if (ConfigUtils.getConfig().hasKeyUseArray(key)) {
				return ConfigUtils.getConfig().getBooleanUseArray(key);
			} else {
				throw new ObjectNullException();
			}
		} catch (Exception e) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Update.updateConfig();
				}
			}.runTaskAsynchronously(TestUtils.getInstance());
			return ConfigUtils.getDefaultValue(key);
		}
	}

	/**
	 * Get the default values of the different Config keys.
	 *
	 * @param key the Config key.
	 *
	 * @return the default value.
	 */
	public @NotNull <V> V getDefaultValue(final @NotNull String... key) {
		try {
			return ConfigUtils.getDataFromResource("resources/config.tf", key);
		} catch (ResourceParser.ThunderParseException e) {
			e.printStackTrace();
		}
		//noinspection unchecked
		return (V) new Object();
	}

	public @NotNull <V> V getDataFromResource(final @NotNull String resource, final @NotNull String... key) throws ResourceParser.ThunderParseException {
		// noinspection ConstantConditions
		return ResourceParser.internalGet(ResourceParser.initialReadWithOutComments(BaseFileUtils.createNewInputStreamFromResource(resource), ConfigUtils.getConfig().collectionsProvider()), key);
	}


	@SuppressWarnings("DuplicatedCode")
	private static class ResourceParser {

		@SuppressWarnings("rawtypes")
		private static @Nullable <V> V internalGet(final @NotNull DataMap map, final @NotNull String... key) { //NOSONAR
			@Nullable Object tempValue = map;
			for (final @NotNull String tempKey : key) {
				if (tempValue instanceof DataMap) {
					tempValue = ((DataMap) tempValue).get(tempKey);
				} else {
					throw new ObjectNullException("File does not contain '" + Arrays.toString(key) + "' -> could not find '" + tempKey + "'");
				}
			}
			return Objects.toDef(tempValue);
		}

		@SuppressWarnings("rawtypes")
		private static @NotNull DataMap<String, Object> initialReadWithOutComments(final @NotNull InputStream inputStream,
																				   final @NotNull CollectionsProvider<? extends DataMap, ? extends List> collectionsProvider) throws ThunderParseException {
			try {
				final @NotNull ListIterator<String> lines;
				try (final @NotNull BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
					lines = reader.lines().collect(Collectors.toList()).listIterator();
				}

				//noinspection unchecked
				final @NotNull DataMap<String, Object> currentMap = collectionsProvider.newMap();

				@NotNull String tempLine;
				@Nullable String tempKey = null;
				while (lines.hasNext()) {
					tempLine = lines.next().trim();

					if (!tempLine.isEmpty() && !tempLine.startsWith("#")) {
						if (tempLine.contains("}")) {
							throw new ThunderParseException("Syntax Error at line '" + lines.previousIndex() + "' -> Block closed without being opened");
						} else if (tempLine.endsWith("{")) {
							if (!tempLine.equals("{")) {
								tempKey = tempLine.substring(0, tempLine.length() - 1).trim();
							} else if (tempKey == null) {
								throw new ThunderParseException("'" + tempLine + "' (line: " + lines.previousIndex() + ") -> Key must not be null");
							}
							//noinspection unchecked
							currentMap.add(tempKey, ResourceParser.internalReadWithOutComments(lines, collectionsProvider.newMap(), collectionsProvider));
						} else {
							tempKey = ResourceParser.readKey(lines, currentMap, tempLine, collectionsProvider);
						}
					}
				}
				currentMap.trimToSize();
				return currentMap;
			} catch (final @NotNull IndexOutOfBoundsException e) {
				throw new ThunderParseException("Could not parse content", e);
			} catch (final @NotNull IOException e) {
				throw new RuntimeIOException("Error while reading content from resource", e);
			}
		}

		@SuppressWarnings("rawtypes")
		private static @NotNull DataMap<String, Object> internalReadWithOutComments(final @NotNull ListIterator<String> lines,
																					final @NotNull DataMap<String, Object> currentMap,
																					final @NotNull CollectionsProvider<? extends DataMap, ? extends List> collectionsProvider) throws ThunderParseException {
			@NotNull String tempLine;
			@Nullable String tempKey = null;
			while (lines.hasNext()) {
				tempLine = lines.next().trim();

				if (!tempLine.isEmpty() && !tempLine.startsWith("#")) {
					if (tempLine.equals("}")) {
						currentMap.trimToSize();
						return currentMap;
					} else if (tempLine.endsWith("}")) {
						ResourceParser.readKey(lines, currentMap, tempLine.substring(0, tempLine.length() - 1), collectionsProvider);
						currentMap.trimToSize();
						return currentMap;
					} else if (tempLine.contains("}")) {
						throw new ThunderParseException("Syntax Error at line '" + lines.previousIndex() + "' -> Illegal Character placement: '}' only allowed as a single Character in line to close blocks");
					} else if (tempLine.endsWith("{")) {
						if (!tempLine.equals("{")) {
							tempKey = tempLine.substring(0, tempLine.length() - 1).trim();
						} else if (tempKey == null) {
							throw new ThunderParseException("'" + tempLine + "' (line: " + lines.previousIndex() + ") -> Key must not be null");
						}
						//noinspection unchecked
						currentMap.add(tempKey, ResourceParser.internalReadWithOutComments(lines, collectionsProvider.newMap(), collectionsProvider));
					} else if (tempLine.startsWith("{")) {
						if (tempKey == null) {
							throw new ThunderParseException("'" + tempLine + "' (line: " + lines.previousIndex() + ") -> Key must not be null");
						}
						//noinspection unchecked
						final @NotNull DataMap<String, Object> tempMap = collectionsProvider.newMap();
						ResourceParser.readKey(lines, tempMap, tempLine.substring(1).trim(), collectionsProvider);
						currentMap.add(tempKey, ResourceParser.internalReadWithOutComments(lines, tempMap, collectionsProvider));
					} else {
						tempKey = ResourceParser.readKey(lines, currentMap, tempLine, collectionsProvider);
					}
				}
			}
			throw new ThunderParseException("Block does not close");
		}
		// </Read without Comments>

		@SuppressWarnings("rawtypes")
		private static @Nullable String readKey(final @NotNull ListIterator<String> lines,
												final @NotNull DataMap<String, Object> tempMap,
												final @NotNull String tempLine,
												final @NotNull CollectionsProvider<? extends DataMap, ? extends List> collectionsProvider) throws ThunderParseException {
			if (tempLine.contains("=")) {
				final @NotNull String[] line = tempLine.split("=", 2);
				line[0] = line[0].trim();
				line[1] = line[1].trim();
				if (line[1].startsWith("[")) {
					if (line[1].endsWith("]")) {
						if (line[1].startsWith("[") && line[1].endsWith("]") && line[1].contains(":") && !line[1].replaceFirst(":", "").contains(":")) {
							final @NotNull String[] pair = line[1].substring(1, line[1].length() - 1).split(":");
							if (pair.length > 2) {
								throw new ThunderParseException("'" + tempLine + "' (line: " + lines.previousIndex() + ") ->  Illegal Object(Pairs may only have two values");
							} else if (pair.length < 2) {
								throw new ThunderParseException("'" + tempLine + "' (line: " + lines.previousIndex() + ") ->  Illegal Object(Pairs need two values");
							} else {
								tempMap.add(line[0], new Pair<>(pair[0].trim(), pair[1].trim()));
								return null;
							}
						} else {
							final @NotNull String[] listArray = line[1].substring(1, line[1].length() - 1).split(",");
							//noinspection unchecked
							final @NotNull List<String> list = collectionsProvider.newList();
							for (final @NotNull String value : listArray) {
								list.add(value.trim());
							}
							tempMap.add(line[0], list);
							return null;
						}
					} else {
						tempMap.add(line[0], ResourceParser.readList(lines, collectionsProvider));
						return null;
					}
				} else {
					if (line[1].equalsIgnoreCase("true") || line[1].equalsIgnoreCase("false")) {
						tempMap.add(line[0], line[1].equalsIgnoreCase("true"));
					} else {
						tempMap.add(line[0], line[1]);
					}
					return null;
				}
			} else {
				if (lines.next().contains("{")) {
					lines.previous();
					return tempLine;
				} else {
					throw new ThunderParseException("'" + tempLine + "' (line: " + lines.previousIndex() + ") -> Line does not contain value or subblock");
				}
			}
		}

		@SuppressWarnings("rawtypes")
		private static @NotNull List<String> readList(final @NotNull ListIterator<String> lines,
													  final @NotNull CollectionsProvider<? extends DataMap, ? extends List> collectionsProvider) throws ThunderParseException {
			@NotNull String tempLine;
			//noinspection unchecked
			final @NotNull List<String> tempList = collectionsProvider.newList();
			while (lines.hasNext()) {
				tempLine = lines.next().trim();
				if (tempLine.startsWith("-")) {
					if (tempLine.endsWith("]")) {
						tempList.add(tempLine.substring(1, tempLine.length() - 1).trim());
						return tempList;
					} else {
						tempList.add(tempLine.substring(1).trim());
					}
				} else if (tempLine.endsWith("]")) {
					return tempList;
				} else {
					throw new ThunderParseException("Syntax Error at '" + tempLine + "' (line: " + lines.previousIndex() + ") -> missing '-'");
				}
			}
			throw new ThunderParseException("Syntax Error at line '" + lines.previousIndex() + "' -> List not closed properly");
		}

		@SuppressWarnings("unused")
		private static class ThunderParseException extends Exception {

			private static final long serialVersionUID = -5477666037332663814L;

			public ThunderParseException() {
				super();
			}

			public ThunderParseException(final String message) {
				super(message);
			}

			public ThunderParseException(final Throwable cause) {
				super(cause);
			}

			public ThunderParseException(final String message, final Throwable cause) {
				super(message, cause);
			}
		}
	}
}