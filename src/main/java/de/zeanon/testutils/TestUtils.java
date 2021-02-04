package de.zeanon.testutils;

import de.zeanon.storagemanager.internal.utility.basic.Objects;
import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.handlers.CommandHandler;
import de.zeanon.testutils.plugin.handlers.LocalTabCompleter;
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
		CommandHandler commandHandler = new CommandHandler();
		LocalTabCompleter localTabCompleter = new LocalTabCompleter();
		Objects.notNull(this.getCommand("testutils")).setExecutor(commandHandler);
		Objects.notNull(this.getCommand("testutils")).setTabCompleter(localTabCompleter);
		Objects.notNull(this.getCommand("testblock")).setExecutor(commandHandler);
		Objects.notNull(this.getCommand("testblock")).setTabCompleter(localTabCompleter);
		Objects.notNull(this.getCommand("tnt")).setExecutor(commandHandler);
		Objects.notNull(this.getCommand("tnt")).setTabCompleter(localTabCompleter);
		InitMode.initPlugin();
	}

	@Override
	public void onDisable() {
		System.out.println("[" + this.getName() + "] >> unloaded.");
	}
}