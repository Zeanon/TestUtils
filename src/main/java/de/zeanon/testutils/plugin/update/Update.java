package de.zeanon.testutils.plugin.update;

import de.zeanon.storagemanagercore.internal.base.exceptions.ObjectNullException;
import de.zeanon.storagemanagercore.internal.base.exceptions.RuntimeIOException;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.util.Pair;
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
			if (!Objects.notNull(InitMode.getConfig().getStringUseArray("Plugin Version"))
						.equals(TestUtils.getInstance().getDescription().getVersion())
				|| !InitMode.getConfig().hasKeyUseArray("Max History")
				|| !InitMode.getConfig().hasKeyUseArray("Automatic Reload")) {

				Update.updateConfig();
			}
		} catch (ObjectNullException e) {
			Update.updateConfig();
		}
	}

	public void updateConfig() {
		try {
			final int maxHistory = InitMode.getConfig().hasKeyUseArray("Max History")
								   ? InitMode.getConfig().getIntUseArray("Max History")
								   : 5;
			final boolean autoReload = !InitMode.getConfig().hasKeyUseArray("Automatic Reload")
									   || InitMode.getConfig().getBooleanUseArray("Automatic Reload");

			InitMode.getConfig().setDataFromResource("resources/config.tf");

			//noinspection unchecked
			InitMode.getConfig().setAllUseArray(new Pair<>(new String[]{"Plugin Version"}, TestUtils.getInstance().getDescription().getVersion()),
												new Pair<>(new String[]{"Max History"}, maxHistory),
												new Pair<>(new String[]{"Automatic Reload"}, autoReload));

			System.out.println("[" + TestUtils.getInstance().getName() + "] >> [Configs] >> 'config.tf' updated.");
		} catch (RuntimeIOException e) {
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
			HttpURLConnection urlConnect = (HttpURLConnection) new URL(Update.RELEASE_URL).openConnection();
			urlConnect.setInstanceFollowRedirects(false);
			urlConnect.getResponseCode();
			return urlConnect.getHeaderField("Location").replaceFirst(".*/", "");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}