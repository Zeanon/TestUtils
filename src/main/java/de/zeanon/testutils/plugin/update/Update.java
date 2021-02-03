package de.zeanon.testutils.plugin.update;

import de.zeanon.testutils.TestUtils;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class Update {

	final String DOWNLOAD_URL = Update.RELEASE_URL + "/download/TestUtils.jar";
	private final String RELEASE_URL = "https://github.com/Zeanon/TestUtils/releases/latest";

	public void updatePlugin() {
		if (TestUtils.getPluginManager().getPlugin("PlugMan") != null
			&& TestUtils.getPluginManager()
						.isPluginEnabled(TestUtils.getPluginManager().getPlugin("PlugMan"))) {
			PlugManEnabledUpdate.updatePlugin(true);
		} else {
			DefaultUpdate.updatePlugin(false, TestUtils.getInstance());
		}
	}

	public void updatePlugin(final @NotNull Player p) {
		if (TestUtils.getPluginManager().getPlugin("PlugMan") != null
			&& TestUtils.getPluginManager()
						.isPluginEnabled(TestUtils.getPluginManager().getPlugin("PlugMan"))) {
			PlugManEnabledUpdate.updatePlugin(p, true);
		} else {
			DefaultUpdate.updatePlugin(p, false, TestUtils.getInstance());
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