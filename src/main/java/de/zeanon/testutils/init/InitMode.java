package de.zeanon.testutils.init;

import de.steamwar.commandframework.SWCommand;
import de.zeanon.storagemanagercore.internal.base.exceptions.RuntimeIOException;
import de.zeanon.testutils.TestUtils;
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
import de.zeanon.testutils.plugin.utils.backup.BackupScheduler;
import de.zeanon.testutils.regionsystem.RegionListener;
import de.zeanon.testutils.regionsystem.RegionManager;
import de.zeanon.testutils.regionsystem.commands.RegionCommand;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class InitMode {


	final @NotNull Set<SWCommand> registeredCommands = new HashSet<>();

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
			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Loading Config...");
			ConfigUtils.loadConfigs();
			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Config file are loaded successfully.");
		} catch (final RuntimeIOException e) {
			System.err.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Could not load config file.");
			System.err.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Maybe try to delete the config file and reload the plugin.");
			System.err.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Unloading Plugin...");

			TestUtils.getPluginManager().disablePlugin(de.zeanon.testutils.TestUtils.getInstance());
			return;
		}

		try {
			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Initializing Config...");
			ConfigUtils.initConfigs();
			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Config file is initialized successfully.");
		} catch (final RuntimeIOException e) {
			System.err.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Could not update config file.");
			System.err.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Maybe try to delete the config file and reload the plugin.");
			System.err.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Unloading Plugin...");

			TestUtils.getPluginManager().disablePlugin(de.zeanon.testutils.TestUtils.getInstance());
			return;
		}

		try {
			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Initializing Regions...");
			RegionManager.initialize();
			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Initialized Regions.");
		} catch (final IOException e) {
			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Could not initialize Regions");
			System.err.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Unloading Plugin...");

			TestUtils.getPluginManager().disablePlugin(de.zeanon.testutils.TestUtils.getInstance());
			return;
		}


		InitMode.registerCommands();

		TestUtils.getPluginManager().registerEvents(new EventListener(), de.zeanon.testutils.TestUtils.getInstance());
		TestUtils.getPluginManager().registerEvents(new RegionListener(), de.zeanon.testutils.TestUtils.getInstance());


		ScoreBoard.initialize();


		BackupScheduler.initialize();
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

	private void enableSleepModeWorldEdit() {
		InitMode.registeredCommands.add(new SleepModeTestUtilsCommand());
		TestUtils.getPluginManager().registerEvents(new WakeupListener(), de.zeanon.testutils.TestUtils.getInstance());

		System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Could not load plugin, it needs FastAsyncWorldEdit or WorldEdit to work.");
		System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> " + de.zeanon.testutils.TestUtils.getInstance().getName() + " will automatically activate when one of the above gets enabled.");
		System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Rudimentary function like updating and disabling will still work.");
	}

	private void enableSleepModeWorldGuard() {
		InitMode.registeredCommands.add(new SleepModeTestUtilsCommand());
		TestUtils.getPluginManager().registerEvents(new WakeupListener(), de.zeanon.testutils.TestUtils.getInstance());

		System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> WorldGuard detected, this plugin replaces WorldGuard, please only use one.");
		System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> " + de.zeanon.testutils.TestUtils.getInstance().getName() + " will automatically activate when WorldGuard gets disabled.");
		System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Rudimentary function like updating and disabling will still work.");
	}
}