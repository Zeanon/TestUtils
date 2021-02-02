package de.zeanon.testutils;

import com.sun.corba.se.impl.activation.CommandHandler;
import de.zeanon.storagemanager.internal.utility.basic.Objects;
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
		Objects.notNull(this.getCommand("testutils")).setExecutor(new CommandHandler());
		Objects.notNull(this.getCommand("testutils")).setTabCompleter(new SchemManagerTabCompleter());
		InitMode.initPlugin();
	}

	@Override
	public void onDisable() {
		System.out.println("[" + this.getName() + "] >> unloaded.");
	}
}