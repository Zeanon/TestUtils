package de.zeanon.testutils;

import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.utils.backup.BackupScheduler;
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
	@SuppressWarnings("CanBeFinal")
	private static PluginManager pluginManager;

	@Override
	public void onEnable() {
		TestUtils.setInstance(this);
		TestUtils.setPluginManager(Bukkit.getPluginManager());
		InitMode.initPlugin();
	}

	@Override
	public void onDisable() {
		BackupScheduler.terminate();
		System.out.println("[" + this.getName() + "] >> unloaded.");
	}
}