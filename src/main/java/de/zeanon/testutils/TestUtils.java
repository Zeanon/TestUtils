package de.zeanon.testutils;

import de.zeanon.testutils.init.InitMode;
import de.zeanon.testutils.plugin.handlers.CommandHandler;
import de.zeanon.testutils.plugin.handlers.TabCompleter;
import java.util.Objects;
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
		TabCompleter tabCompleter = new TabCompleter();
		Objects.requireNonNull(this.getCommand("testutils")).setExecutor(commandHandler);
		Objects.requireNonNull(this.getCommand("testutils")).setTabCompleter(tabCompleter);
		Objects.requireNonNull(this.getCommand("testblock")).setExecutor(commandHandler);
		Objects.requireNonNull(this.getCommand("testblock")).setTabCompleter(tabCompleter);
		Objects.requireNonNull(this.getCommand("tnt")).setExecutor(commandHandler);
		Objects.requireNonNull(this.getCommand("tnt")).setTabCompleter(tabCompleter);
		InitMode.initPlugin();
	}

	@Override
	public void onDisable() {
		System.out.println("[" + this.getName() + "] >> unloaded.");
	}
}