package de.zeanon.testutils.plugin.update;

import de.zeanon.storagemanagercore.internal.base.exceptions.ObjectNullException;
import de.zeanon.storagemanagercore.internal.base.exceptions.RuntimeIOException;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings("unused")
@UtilityClass
public class Update {

	final String DOWNLOAD_URL = Update.RELEASE_URL + "/download/TestUtils.jar";
	private final String RELEASE_URL = "https://github.com/Zeanon/TestUtils/releases/latest";

	public void updatePlugin(final @NotNull JavaPlugin instance) {
		if (TestUtils.getPluginManager().getPlugin("PlugMan") != null
			&& TestUtils.getPluginManager()
						.isPluginEnabled(TestUtils.getPluginManager().getPlugin("PlugMan"))) {
			PlugManEnabledUpdate.updatePlugin(ConfigUtils.getBoolean("Automatic Reload"), instance);
		} else {
			DefaultUpdate.updatePlugin(ConfigUtils.getBoolean("Automatic Reload"), instance);
		}
	}

	public void updatePlugin(final @NotNull Player p, final @NotNull JavaPlugin instance) {
		if (TestUtils.getPluginManager().getPlugin("PlugMan") != null
			&& TestUtils.getPluginManager()
						.isPluginEnabled(TestUtils.getPluginManager().getPlugin("PlugMan"))) {
			PlugManEnabledUpdate.updatePlugin(p, ConfigUtils.getBoolean("Automatic Reload"), instance);
		} else {
			DefaultUpdate.updatePlugin(p, ConfigUtils.getBoolean("Automatic Reload"), instance);
		}
	}

	public void checkConfigUpdate() {
		try {
			if (!Objects.notNull(ConfigUtils.getConfig().getStringUseArray("Plugin Version"))
						.equals(TestUtils.getInstance().getDescription().getVersion())
				|| !ConfigUtils.getConfig().hasKeyUseArray("Max History")
				|| !ConfigUtils.getConfig().hasKeyUseArray("Max Back")
				|| !ConfigUtils.getConfig().hasKeyUseArray("Automatic Reload")
				|| !ConfigUtils.getConfig().hasKeyUseArray("Listmax")
				|| !ConfigUtils.getConfig().hasKeyUseArray("Space Lists")
				|| !ConfigUtils.getConfig().hasKeyUseArray("Backups", "manual")
				|| !ConfigUtils.getConfig().hasKeyUseArray("Backups", "startup")
				|| !ConfigUtils.getConfig().hasKeyUseArray("Backups", "hourly")
				|| !ConfigUtils.getConfig().hasKeyUseArray("Backups", "daily")) {

				Update.updateConfig();
			}
		} catch (final ObjectNullException e) {
			Update.updateConfig();
		}
	}

	public void updateConfig() {
		try {
			final Integer maxHistory = ConfigUtils.getConfig().hasKeyUseArray("Max History")
									   ? ConfigUtils.getConfig().getIntUseArray("Max History")
									   : Objects.toInt(ConfigUtils.getDefaultValue(Integer.class, "Max History"));

			final Integer maxBack = ConfigUtils.getConfig().hasKeyUseArray("Max Back")
									? ConfigUtils.getConfig().getIntUseArray("Max Back")
									: Objects.toInt(ConfigUtils.getDefaultValue(Integer.class, "Max Back"));

			final boolean autoReload = !ConfigUtils.getConfig().hasKeyUseArray("Automatic Reload")
									   || ConfigUtils.getConfig().getBooleanUseArray("Automatic Reload");

			final Integer listmax = ConfigUtils.getConfig().hasKeyUseArray("Listmax")
									? ConfigUtils.getConfig().getIntUseArray("Listmax")
									: Objects.toInt(ConfigUtils.getDefaultValue(Integer.class, "Listmax"));

			final boolean spaceLists = !ConfigUtils.getConfig().hasKeyUseArray("Space Lists")
									   || ConfigUtils.getConfig().getBooleanUseArray("Space Lists");

			final Integer maxManual = ConfigUtils.getConfig().hasKeyUseArray("Backups", "manual")
									  ? ConfigUtils.getConfig().getIntUseArray("Backups", "manual")
									  : Objects.toInt(ConfigUtils.getDefaultValue(Integer.class, "Backups", "manual"));

			final Integer maxStartup = ConfigUtils.getConfig().hasKeyUseArray("Backups", "startup")
									   ? ConfigUtils.getConfig().getIntUseArray("Backups", "startup")
									   : Objects.toInt(ConfigUtils.getDefaultValue(Integer.class, "Backups", "startup"));

			final Integer maxHourly = ConfigUtils.getConfig().hasKeyUseArray("Backups", "hourly")
									  ? ConfigUtils.getConfig().getIntUseArray("Backups", "hourly")
									  : Objects.toInt(ConfigUtils.getDefaultValue(Integer.class, "Backups", "hourly"));

			final Integer maxDaily = ConfigUtils.getConfig().hasKeyUseArray("Backups", "daily")
									 ? ConfigUtils.getConfig().getIntUseArray("Backups", "daily")
									 : Objects.toInt(ConfigUtils.getDefaultValue(Integer.class, "Backups", "daily"));

			ConfigUtils.getConfig().setDataFromResource("resources/config.tf");

			//noinspection unchecked
			ConfigUtils.getConfig().setAllUseArray(new Pair<>(new String[]{"Plugin Version"}, TestUtils.getInstance().getDescription().getVersion()),
												   new Pair<>(new String[]{"Max History"}, maxHistory),
												   new Pair<>(new String[]{"Max Back"}, maxBack),
												   new Pair<>(new String[]{"Automatic Reload"}, autoReload),
												   new Pair<>(new String[]{"Listmax"}, listmax),
												   new Pair<>(new String[]{"Space Lists"}, spaceLists),
												   new Pair<>(new String[]{"Backups", "manual"}, maxManual),
												   new Pair<>(new String[]{"Backups", "startup"}, maxStartup),
												   new Pair<>(new String[]{"Backups", "hourly"}, maxHourly),
												   new Pair<>(new String[]{"Backups", "daily"}, maxDaily));

			System.out.println("[" + TestUtils.getInstance().getName() + "] >> [Configs] >> 'config.tf' updated.");
		} catch (final RuntimeIOException e) {
			throw new RuntimeIOException("[" + TestUtils.getInstance().getName() + "] >> [Configs] >> 'config.tf' could not be updated.", e);
		}
	}

	public void updateAvailable(final @NotNull Player p) {
		if ((p.hasPermission("testutils.update")) && Update.checkForUpdate()) {
			GlobalMessageUtils.sendCommandMessage("",
												  ChatColor.RED + ""
												  + ChatColor.BOLD + "There is a new Update available for TestUtils, click here to update.",
												  ChatColor.DARK_GREEN + ""
												  + ChatColor.UNDERLINE + ""
												  + ChatColor.ITALIC + ""
												  + ChatColor.BOLD + "!!UPDATE BABY!!",
												  "/tu update",
												  p);
		}
	}

	public boolean checkForUpdate() {
		return !("v" + TestUtils.getInstance().getDescription().getVersion()).equalsIgnoreCase(Update.getGithubVersionTag());
	}

	private String getGithubVersionTag() {
		try {
			final HttpURLConnection urlConnect = (HttpURLConnection) new URL(Update.RELEASE_URL).openConnection();
			urlConnect.setInstanceFollowRedirects(false);
			urlConnect.getResponseCode();
			return urlConnect.getHeaderField("Location").replaceFirst(".*/", "");
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}