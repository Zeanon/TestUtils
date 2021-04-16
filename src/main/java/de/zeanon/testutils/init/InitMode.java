package de.zeanon.testutils.init;

import de.steamwar.commandframework.SWCommand;
import de.zeanon.storagemanagercore.internal.base.exceptions.FileParseException;
import de.zeanon.storagemanagercore.internal.base.exceptions.RuntimeIOException;
import de.zeanon.storagemanagercore.internal.base.settings.Comment;
import de.zeanon.storagemanagercore.internal.base.settings.Reload;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.commands.backup.BackupCommand;
import de.zeanon.testutils.plugin.commands.inventoryclean.InventoryClean;
import de.zeanon.testutils.plugin.commands.stoplag.Stoplag;
import de.zeanon.testutils.plugin.commands.testblock.TestBlock;
import de.zeanon.testutils.plugin.commands.testutils.SleepModeTestUtils;
import de.zeanon.testutils.plugin.commands.testutils.TestUtilsCommand;
import de.zeanon.testutils.plugin.commands.tnt.TNT;
import de.zeanon.testutils.plugin.handlers.EventListener;
import de.zeanon.testutils.plugin.handlers.WakeupListener;
import de.zeanon.testutils.plugin.mapper.Mapper;
import de.zeanon.testutils.plugin.update.Update;
import de.zeanon.testutils.plugin.utils.ScoreBoard;
import de.zeanon.testutils.plugin.utils.backup.BackupScheduler;
import de.zeanon.testutils.regionsystem.RegionListener;
import de.zeanon.testutils.regionsystem.commands.RegionCommand;
import de.zeanon.testutils.regionsystem.region.RegionManager;
import de.zeanon.thunderfilemanager.ThunderFileManager;
import de.zeanon.thunderfilemanager.internal.files.config.ThunderConfig;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class InitMode {


	final @NotNull Set<SWCommand> registeredCommands = new HashSet<>();
	final @NotNull Set<String> forbiddenNames = new HashSet<>(Arrays.asList("-here", "-other", "-north", "-n", "-south", "-s", "-manual", "-hourly", "-daily", "-startup"));
	@Getter
	private ThunderConfig config;

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
			InitMode.loadConfigs();
			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Config file are loaded successfully.");
		} catch (RuntimeIOException e) {
			System.err.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Could not load config file.");
			System.err.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Maybe try to delete the config file and reload the plugin.");
			System.err.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Unloading Plugin...");

			de.zeanon.testutils.TestUtils.getPluginManager().disablePlugin(de.zeanon.testutils.TestUtils.getInstance());
			return;
		}

		try {
			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Initializing Config...");
			InitMode.initConfigs();
			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Config file is initialized successfully.");
		} catch (RuntimeIOException e) {
			System.err.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Could not update config file.");
			System.err.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Maybe try to delete the config file and reload the plugin.");
			System.err.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Unloading Plugin...");

			de.zeanon.testutils.TestUtils.getPluginManager().disablePlugin(de.zeanon.testutils.TestUtils.getInstance());
			return;
		}

		try {
			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Initializing Regions...");
			RegionManager.initialize();
			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Initialized Regions.");
		} catch (IOException e) {
			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Could not initialize Regions");
			System.err.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Unloading Plugin...");

			de.zeanon.testutils.TestUtils.getPluginManager().disablePlugin(de.zeanon.testutils.TestUtils.getInstance());
			return;
		}


		for (final @NotNull Player p : Bukkit.getOnlinePlayers()) {
			ScoreBoard.initialize(p);
		}


		InitMode.registerCommands();

		TestUtils.getPluginManager().registerEvents(new EventListener(), de.zeanon.testutils.TestUtils.getInstance());
		TestUtils.getPluginManager().registerEvents(new RegionListener(), de.zeanon.testutils.TestUtils.getInstance());


		new BukkitRunnable() {
			@Override
			public void run() {
				BackupScheduler.backup();
			}
		}.runTask(TestUtils.getInstance());
	}

	public boolean forbiddenFileName(final @NotNull String name) {
		return InitMode.forbiddenNames.contains(name.toLowerCase());
	}

	public void registerCommands() {
		Mapper.initialize();

		InitMode.registeredCommands.add(new TNT());
		InitMode.registeredCommands.add(new BackupCommand());
		InitMode.registeredCommands.add(new RegionCommand());
		InitMode.registeredCommands.add(new Stoplag());
		InitMode.registeredCommands.add(new TestBlock());
		InitMode.registeredCommands.add(new TestUtilsCommand());
		InitMode.registeredCommands.add(new InventoryClean());
	}

	public void unregisterCommands() {
		for (final @NotNull SWCommand command : InitMode.registeredCommands) {
			command.unregister();
		}
	}

	private void loadConfigs() {
		@Nullable Throwable cause = null;
		try {
			InitMode.config = ThunderFileManager.thunderConfig(TestUtils.getPluginFolder(), "config")
												.fromResource("resources/config.tf")
												.reloadSetting(Reload.INTELLIGENT)
												.commentSetting(Comment.PRESERVE)
												.concurrentData(true)
												.create();

			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> [Configs] >> 'config.tf' loaded.");
		} catch (final @NotNull RuntimeIOException | FileParseException e) {
			System.err.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> [Configs] >> 'config.tf' could not be loaded.");
			e.printStackTrace();
			cause = e;
		}

		if (cause != null) {
			throw new RuntimeIOException(cause);
		}
	}

	private void initConfigs() {
		if (!InitMode.getConfig().hasKeyUseArray("Plugin Version")
			|| !Objects.notNull(InitMode.getConfig().getStringUseArray("Plugin Version"))
					   .equals(de.zeanon.testutils.TestUtils.getInstance().getDescription().getVersion())) {
			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Updating Configs...");
			Update.checkConfigUpdate();
			System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Config files are updated successfully.");
		}
	}

	private void enableSleepModeWorldEdit() {
		new SleepModeTestUtils();
		de.zeanon.testutils.TestUtils.getPluginManager().registerEvents(new WakeupListener(), de.zeanon.testutils.TestUtils.getInstance());

		System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Could not load plugin, it needs FastAsyncWorldEdit or WorldEdit to work.");
		System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> " + de.zeanon.testutils.TestUtils.getInstance().getName() + " will automatically activate when one of the above gets enabled.");
		System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Rudimentary function like updating and disabling will still work.");
	}

	private void enableSleepModeWorldGuard() {
		new SleepModeTestUtils();
		de.zeanon.testutils.TestUtils.getPluginManager().registerEvents(new WakeupListener(), de.zeanon.testutils.TestUtils.getInstance());

		System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> WorldGuard detected, this plugin replaces WorldGuard, please only use one.");
		System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> " + de.zeanon.testutils.TestUtils.getInstance().getName() + " will automatically activate when WorldGuard gets disabled.");
		System.out.println("[" + de.zeanon.testutils.TestUtils.getInstance().getName() + "] >> Rudimentary function like updating and disabling will still work.");
	}
}