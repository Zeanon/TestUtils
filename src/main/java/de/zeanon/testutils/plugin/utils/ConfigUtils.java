package de.zeanon.testutils.plugin.utils;

import de.zeanon.storagemanagercore.internal.base.exceptions.FileParseException;
import de.zeanon.storagemanagercore.internal.base.interfaces.DataMap;
import de.zeanon.storagemanagercore.internal.base.settings.Comment;
import de.zeanon.storagemanagercore.internal.base.settings.Reload;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.update.Update;
import de.zeanon.thunderfilemanager.ThunderFileManager;
import de.zeanon.thunderfilemanager.internal.base.cache.filedata.ThunderFileData;
import de.zeanon.thunderfilemanager.internal.base.exceptions.ThunderException;
import de.zeanon.thunderfilemanager.internal.files.config.ThunderConfig;
import de.zeanon.thunderfilemanager.internal.utility.parser.ThunderFileParser;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.logging.Level;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class ConfigUtils {

	@SuppressWarnings("rawtypes")
	@Getter
	private ThunderFileData<DataMap, ?, List> defaultFileData; //NOSONAR
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
			System.out.println(ConfigUtils.config.fileData().size());

			TestUtils.getChatLogger().info(">> [Configs] >> 'config.tf' loaded.");
		} catch (final @NotNull UncheckedIOException | FileParseException e) {
			TestUtils.getChatLogger().info(">> [Configs] >> 'config.tf' could not be loaded.");
			TestUtils.getChatLogger().log(Level.SEVERE, "Error while loading configs", e);
			cause = e;
		}

		if (cause != null) {
			throw new UncheckedIOException(new IOException(cause));
		}
	}

	public void initDefaultConfigs() {
		try {
			ConfigUtils.defaultFileData = ThunderFileParser.readDataAsFileData(BaseFileUtils.createNewInputStreamFromResource("resources/config.tf"),
																			   ConfigUtils.getConfig().collectionsProvider(),
																			   ConfigUtils.getConfig().getCommentSetting(),
																			   ConfigUtils.getConfig().getBufferSize());
			TestUtils.getChatLogger().info(">> [Configs] >> default for 'config.tf' loaded.");
		} catch (final ThunderException e) {
			TestUtils.getChatLogger().info(">> [Configs] >> default for 'config.tf' could not be loaded.");
			TestUtils.getChatLogger().log(Level.SEVERE, "Error while loading default configs", e);
		}
	}

	public void initConfigs() {
		if (!ConfigUtils.getConfig().hasKeyUseArray("Plugin Version")
			|| !Objects.notNull(ConfigUtils.getConfig().getStringUseArray("Plugin Version"))
					   .equals(de.zeanon.testutils.TestUtils.getInstance().getDescription().getVersion())) {
			TestUtils.getChatLogger().info(">> Updating Configs...");
			Update.checkConfigUpdate();
			TestUtils.getChatLogger().info(">> Config files are updated successfully.");
		}
	}

	public boolean getBoolean(final @NotNull String... key) {
		return Objects.toBoolean(ConfigUtils.get(Boolean.class, key));
	}

	public int getInt(final @NotNull String... key) {
		return Objects.toInt(ConfigUtils.get(Integer.class, key));
	}

	public @NotNull <T> T get(final @NotNull Class<T> type, final @NotNull String... key) {
		final @Nullable T result = Objects.toDef(ConfigUtils.getConfig().getUseArray(key), type);
		if (result != null) {
			return result;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				Update.updateConfig();
			}
		}.runTaskAsynchronously(TestUtils.getInstance());
		return ConfigUtils.getDefaultValue(type, key);
	}

	/**
	 * Get the default values of the different Config keys.
	 *
	 * @param key the Config key.
	 *
	 * @return the default value.
	 */
	public @NotNull <T> T getDefaultValue(final @NotNull Class<T> type, final @NotNull String... key) {
		return Objects.notNull(Objects.toDef(Objects.notNull(ConfigUtils.getDefaultFileData()).getUseArray(key), type),
							   "Could not read from the default config.");
	}
}