package de.zeanon.testutils.plugin.utils;

import de.zeanon.storagemanagercore.internal.base.exceptions.ObjectNullException;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.update.Update;
import lombok.experimental.UtilityClass;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class ConfigUtils {

	/**
	 * get an int from the config.
	 *
	 * @param key the Config key.
	 *
	 * @return value.
	 */
	public int getInt(final @NotNull String key) {
		try {
			return Objects.notNull(InitMode.getConfig()).getIntUseArray(key);
		} catch (ObjectNullException e) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Update.updateConfig();
				}
			}.runTaskAsynchronously(TestUtils.getInstance());
			return (int) ConfigUtils.getDefaultValue(key);
		}
	}

	/**
	 * get a boolean from the config.
	 *
	 * @param key the Config key.
	 *
	 * @return value.
	 */
	public boolean getBoolean(final @NotNull String key) {
		try {
			return Objects.notNull(InitMode.getConfig()).getBooleanUseArray(key);
		} catch (ObjectNullException e) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Update.updateConfig();
				}
			}.runTaskAsynchronously(TestUtils.getInstance());
			return (boolean) ConfigUtils.getDefaultValue(key);
		}
	}

	/**
	 * Get the default values of the different Config keys.
	 *
	 * @param key the Config key.
	 *
	 * @return the default value.
	 */
	private @NotNull Object getDefaultValue(final @NotNull String key) {
		switch (key) {
			case "Automatic Reload":
				return true;
			case "Max History":
				return 10;
			case "Plugin Version":
				return TestUtils.getInstance().getDescription().getVersion();
			default:
				return new Object();
		}
	}
}