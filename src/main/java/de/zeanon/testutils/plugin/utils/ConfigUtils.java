package de.zeanon.testutils.plugin.utils;

import de.zeanon.storagemanagercore.internal.base.exceptions.FileParseException;
import de.zeanon.storagemanagercore.internal.base.settings.Comment;
import de.zeanon.storagemanagercore.internal.base.settings.Reload;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.update.Update;
import de.zeanon.thunderfilemanager.ThunderFileManager;
import de.zeanon.thunderfilemanager.internal.base.exceptions.ThunderException;
import de.zeanon.thunderfilemanager.internal.files.config.ThunderConfig;
import de.zeanon.thunderfilemanager.internal.utility.parser.ThunderFileParser;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.logging.Level;
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
		} catch (final @NotNull UncheckedIOException | FileParseException e) {
			System.err.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> [Configs] >> 'config.tf' could not be loaded.");
			TestUtils.getChatLogger().log(Level.SEVERE, "Error while loading configs", e);
			cause = e;
		}

		if (cause != null) {
			throw new UncheckedIOException(new IOException(cause));
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
		try {
			return Objects.notNull(Objects.toDef(ThunderFileParser.readDataAsFileData(BaseFileUtils.createNewInputStreamFromResource("resources/config.tf"),
																					  ConfigUtils.getConfig().collectionsProvider(),
																					  ConfigUtils.getConfig().getCommentSetting(),
																					  ConfigUtils.getConfig().getBufferSize()).getUseArray(key), type), "Could not read from the default config.");
		} catch (final @NotNull ThunderException e) {
			TestUtils.getChatLogger().log(Level.SEVERE, "Error while getting default value for config", e);
		}
		return Objects.notNull(Objects.toDef(new Object(), type));
	}
}