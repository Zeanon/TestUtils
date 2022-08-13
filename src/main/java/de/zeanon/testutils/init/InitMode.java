package de.zeanon.testutils.init;

import de.steamwar.commandframework.SWCommand;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.backup.BackupScheduler;
import de.zeanon.testutils.plugin.commands.backup.BackupCommand;
import de.zeanon.testutils.plugin.commands.countingwand.CountingwandCommand;
import de.zeanon.testutils.plugin.commands.gamemode.GamemodeCommand;
import de.zeanon.testutils.plugin.commands.inventoryclean.InventoryClean;
import de.zeanon.testutils.plugin.commands.stoplag.Stoplag;
import de.zeanon.testutils.plugin.commands.testblock.TestBlockCommand;
import de.zeanon.testutils.plugin.commands.testutils.SleepModeTestUtilsCommand;
import de.zeanon.testutils.plugin.commands.testutils.TestUtilsCommand;
import de.zeanon.testutils.plugin.commands.tnt.TNTCommand;
import de.zeanon.testutils.plugin.handlers.EventListener;
import de.zeanon.testutils.plugin.handlers.WakeupListener;
import de.zeanon.testutils.plugin.mapper.Mapper;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import de.zeanon.testutils.plugin.utils.ScoreBoard;
import de.zeanon.testutils.regionsystem.RegionListener;
import de.zeanon.testutils.regionsystem.RegionManager;
import de.zeanon.testutils.regionsystem.commands.RegionCommand;
import lombok.experimental.UtilityClass;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.Set;


@UtilityClass
public class InitMode {


	private final @NotNull Set<SWCommand> registeredCommands;

	static {
		registeredCommands = new HashSet<>();
	}

	public void initPlugin() {
		if (de.zeanon.testutils.TestUtils.getPluginManager().getPlugin("WorldGuard") != null
			&& de.zeanon.testutils.TestUtils.getPluginManager().isPluginEnabled("WorldGuard")) {

			InitMode.enableSleepModeWorldGuard();
			return;
		}

		if (((de.zeanon.testutils.TestUtils.getPluginManager().getPlugin("FastAsyncWorldEdit") == null
			  || !de.zeanon.testutils.TestUtils.getPluginManager().isPluginEnabled("FastAsyncWorldEdit"))
			 && (de.zeanon.testutils.TestUtils.getPluginManager().getPlugin("WorldEdit") == null
				 || !de.zeanon.testutils.TestUtils.getPluginManager().isPluginEnabled("WorldEdit")))) {
			InitMode.enableSleepModeWorldEdit();

			return;
		}

		try {
			TestUtils.getChatLogger().info(">> Loading Config...");
			ConfigUtils.loadConfigs();
			TestUtils.getChatLogger().info(">> Config file are loaded successfully.");
		} catch (final UncheckedIOException e) {
			TestUtils.getChatLogger().info(">> Could not load config file.");
			TestUtils.getChatLogger().info(">> Maybe try to delete the config file and reload the plugin.");
			TestUtils.getChatLogger().info(">> Unloading Plugin...");

			TestUtils.getPluginManager().disablePlugin(TestUtils.getInstance());
			return;
		}

		try {
			TestUtils.getChatLogger().info(">> Initializing default Configs...");
			ConfigUtils.initDefaultConfigs();
			TestUtils.getChatLogger().info(">> Default config is initialized successfully.");
		} catch (final UncheckedIOException e) {
			TestUtils.getChatLogger().info(">> Could not initialize default config.");
			TestUtils.getChatLogger().info(">> Maybe try to reload the plugin.");
			TestUtils.getChatLogger().info(">> Unloading Plugin...");

			TestUtils.getPluginManager().disablePlugin(TestUtils.getInstance());
			return;
		}

		try {
			TestUtils.getChatLogger().info(">> Initializing Config...");
			ConfigUtils.initConfigs();
			TestUtils.getChatLogger().info(">> Config file is initialized successfully.");
		} catch (final UncheckedIOException e) {
			TestUtils.getChatLogger().info(">> Could not update config file.");
			TestUtils.getChatLogger().info(">> Maybe try to delete the config file and reload the plugin.");
			TestUtils.getChatLogger().info(">> Unloading Plugin...");

			TestUtils.getPluginManager().disablePlugin(TestUtils.getInstance());
			return;
		}

		try {
			TestUtils.getChatLogger().info(">> Initializing Regions...");
			RegionManager.initialize();
			TestUtils.getChatLogger().info(">> Initialized Regions.");
		} catch (final UncheckedIOException | IOException e) {
			TestUtils.getChatLogger().info(">> Could not initialize Regions");
			TestUtils.getChatLogger().info(">> Unloading Plugin...");

			TestUtils.getPluginManager().disablePlugin(TestUtils.getInstance());
			return;
		}

		InitMode.registerEvents(new EventListener());
		InitMode.registerEvents(new RegionListener());


		ScoreBoard.initialize();


		BackupScheduler.initialize();


		InitMode.registerCommands();
	}

	public void registerCommands() {
		Mapper.initialize();

		InitMode.registeredCommands.add(new TNTCommand());
		InitMode.registeredCommands.add(new BackupCommand());
		InitMode.registeredCommands.add(new RegionCommand());
		InitMode.registeredCommands.add(new Stoplag());
		InitMode.registeredCommands.add(new TestBlockCommand());
		InitMode.registeredCommands.add(new TestUtilsCommand());
		InitMode.registeredCommands.add(new InventoryClean());
		InitMode.registeredCommands.add(new GamemodeCommand());
		InitMode.registeredCommands.add(new CountingwandCommand());
	}

	public void unregisterCommands() {
		for (final @NotNull SWCommand command : InitMode.registeredCommands) {
			command.unregister();
		}
	}

	public void registerEvents(final @NotNull Listener listener) {
		TestUtils.getPluginManager().registerEvents(listener, TestUtils.getInstance());
	}

	private void enableSleepModeWorldEdit() {
		InitMode.registeredCommands.add(new SleepModeTestUtilsCommand());
		InitMode.registerEvents(new WakeupListener());

		TestUtils.getChatLogger().info(">> Could not load plugin, it needs FastAsyncWorldEdit or WorldEdit to work.");
		TestUtils.getChatLogger().info(">> " + TestUtils.getInstance().getName() + " will automatically activate when one of the above gets enabled.");
		TestUtils.getChatLogger().info(">> Rudimentary function like updating and disabling will still work.");
	}

	private void enableSleepModeWorldGuard() {
		InitMode.registeredCommands.add(new SleepModeTestUtilsCommand());
		InitMode.registerEvents(new WakeupListener());

		TestUtils.getChatLogger().info(">> WorldGuard detected, this plugin replaces WorldGuard, please only use one.");
		TestUtils.getChatLogger().info(">> " + TestUtils.getInstance().getName() + " will automatically activate when WorldGuard gets disabled.");
		TestUtils.getChatLogger().info(">> Rudimentary function like updating and disabling will still work.");
	}
}