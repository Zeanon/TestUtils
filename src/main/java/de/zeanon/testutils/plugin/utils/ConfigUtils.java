package de.zeanon.testutils.plugin.utils;

import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.update.Update;
import java.util.Arrays;
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
	public int getInt(final @NotNull String... key) {
		if (InitMode.getConfig().hasKeyUseArray(key)) {
			return InitMode.getConfig().getIntUseArray(key);
		} else {
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
	public boolean getBoolean(final @NotNull String... key) {
		if (InitMode.getConfig().hasKeyUseArray(key)) {
			return InitMode.getConfig().getBooleanUseArray(key);
		} else {
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
	private @NotNull Object getDefaultValue(final @NotNull String... key) {
		if (Arrays.equals(new String[]{"Automatic Reload"}, key)) {
			return true;
		} else if (Arrays.equals(new String[]{"Max History"}, key)) {
			return 10;
		} else if (Arrays.equals(new String[]{"Plugin Version"}, key)) {
			return TestUtils.getInstance().getDescription().getVersion();
		} else if (Arrays.equals(new String[]{"Backups", "manual", "enable"}, key)) {
			return true;
		} else if (Arrays.equals(new String[]{"Backups", "manual", "amount"}, key)) {
			return 10;
		} else if (Arrays.equals(new String[]{"Backups", "startup", "enable"}, key)) {
			return true;
		} else if (Arrays.equals(new String[]{"Backups", "startup", "amount"}, key)) {
			return 10;
		} else if (Arrays.equals(new String[]{"Backups", "hourly", "enable"}, key)) {
			return true;
		} else if (Arrays.equals(new String[]{"Backups", "hourly", "amount"}, key)) {
			return 24;
		} else if (Arrays.equals(new String[]{"Backups", "daily", "enable"}, key)) {
			return true;
		} else if (Arrays.equals(new String[]{"Backups", "daily", "amount"}, key)) {
			return 7;
		}
		return new Object();
	}
}