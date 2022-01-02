package de.zeanon.testutils;

import de.zeanon.testutils.init.InitMode;
import java.nio.file.Path;
import java.util.logging.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public final class TestUtils extends JavaPlugin {

	@Getter
	@Setter(AccessLevel.PRIVATE)
	@SuppressWarnings("CanBeFinal")
	private static TestUtils instance;
	@Getter
	@Setter(AccessLevel.PRIVATE)
	private static Logger chatLogger;
	@Getter
	@Setter(AccessLevel.PRIVATE)
	@SuppressWarnings("CanBeFinal")
	private static PluginManager pluginManager;
	@Getter
	@Setter(AccessLevel.PRIVATE)
	private static Path pluginFolder;


	@Override
	public void onEnable() {
		TestUtils.setInstance(this);
		TestUtils.setChatLogger(TestUtils.getInstance().getLogger());
		TestUtils.setPluginManager(Bukkit.getPluginManager());
		TestUtils.setPluginFolder(TestUtils.getInstance().getDataFolder().toPath());

		InitMode.initPlugin();
	}


	@Override
	public void onDisable() {
		InitMode.unregisterCommands();
		System.out.println("[" + this.getName() + "] >> unloaded.");
	}
}