package de.zeanon.testutils.plugin.update;

import de.zeanon.storagemanagercore.internal.base.exceptions.ObjectNullException;
import de.zeanon.storagemanagercore.internal.base.interfaces.DataMap;
import de.zeanon.storagemanagercore.internal.base.settings.Comment;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.thunderfilemanager.internal.base.cache.filedata.ThunderFileData;
import de.zeanon.thunderfilemanager.internal.base.exceptions.ThunderException;
import de.zeanon.thunderfilemanager.internal.utility.parser.ThunderFileParser;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;


@SuppressWarnings("unused")
@UtilityClass
public class Update {

	final String DOWNLOAD_URL = Update.RELEASE_URL + "/download/TestUtils.jar";
	private final String RELEASE_URL = "https://github.com/Zeanon/TestUtils/releases/latest";

	public void updatePlugin(final @NotNull JavaPlugin instance) {
		final @NotNull Updater updater;
		if (TestUtils.getPluginManager().getPlugin("PlugMan") != null
			&& TestUtils.getPluginManager()
						.isPluginEnabled(TestUtils.getPluginManager().getPlugin("PlugMan"))) {
			updater = new PlugManEnabledUpdater();
		} else {
			updater = new PlugManDisabledUpdater();
		}

		updater.updatePlugin(ConfigUtils.getBoolean("Automatic Reload"), instance);
	}

	public void updatePlugin(final @NotNull Player p, final @NotNull JavaPlugin instance) {
		final @NotNull Updater updater;
		if (TestUtils.getPluginManager().getPlugin("PlugMan") != null
			&& TestUtils.getPluginManager()
						.isPluginEnabled(TestUtils.getPluginManager().getPlugin("PlugMan"))) {
			updater = new PlugManEnabledUpdater();
		} else {
			updater = new PlugManDisabledUpdater();
		}

		updater.updatePlugin(p, ConfigUtils.getBoolean("Automatic Reload"), instance);
	}

	public void checkConfigUpdate() {
		try {
			if (!Objects.notNull(ConfigUtils.getConfig().getStringUseArray("Plugin Version"))
						.equals(TestUtils.getInstance().getDescription().getVersion())) {
				Update.updateConfig();
				return;
			}

			for (final @NotNull String[] entry : Objects.notNull(Objects.notNull(ConfigUtils.getDefaultFileData()).getKeysUseArray())) {
				if (!ConfigUtils.getConfig().hasKeyUseArray(entry)) {
					Update.updateConfig();
					return;
				}
			}
		} catch (final @NotNull ObjectNullException e) {
			Update.updateConfig();
		}
	}

	public void updateConfig() {
		try {
			//noinspection rawtypes
			final @NotNull ThunderFileData<DataMap, ?, List> data = ThunderFileParser.readDataAsFileData(ConfigUtils.getConfig().file(), //NOSONAR
																										 ConfigUtils.getConfig().collectionsProvider(),
																										 Comment.SKIP,
																										 ConfigUtils.getConfig().getBufferSize()); //NOSONAR

			data.insertUseArray(new String[]{"Plugin Version"}, TestUtils.getInstance().getDescription().getVersion());

			ConfigUtils.getConfig().setDataFromResource("resources/config.tf");

			for (final @NotNull String[] key : Objects.notNull(data.getKeysUseArray())) {
				ConfigUtils.getConfig().setUseArray(key, data.getUseArray(key));
			}

			TestUtils.getChatLogger().info(">> [Configs] >> 'config.tf' updated.");
		} catch (final UncheckedIOException | ThunderException e) {
			TestUtils.getChatLogger().log(Level.SEVERE, ">> [Configs] >> 'config.tf' could not be updated.", e);
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
			TestUtils.getChatLogger().log(Level.SEVERE, "Error while getting newest version tag from Github", e);
			return null;
		}
	}
}