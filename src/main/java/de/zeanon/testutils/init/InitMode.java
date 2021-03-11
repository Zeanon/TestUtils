package de.zeanon.testutils.init;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.zeanon.storagemanagercore.internal.base.exceptions.FileParseException;
import de.zeanon.storagemanagercore.internal.base.exceptions.RuntimeIOException;
import de.zeanon.storagemanagercore.internal.base.settings.Comment;
import de.zeanon.storagemanagercore.internal.base.settings.Reload;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.handlers.CommandHandler;
import de.zeanon.testutils.plugin.handlers.EventListener;
import de.zeanon.testutils.plugin.handlers.SleepModeCommandHandler;
import de.zeanon.testutils.plugin.handlers.WakeupListener;
import de.zeanon.testutils.plugin.handlers.tabcompleter.SleepModeTabCompleter;
import de.zeanon.testutils.plugin.handlers.tabcompleter.TestUtilsTabCompleter;
import de.zeanon.testutils.plugin.handlers.tabcompleter.tablistener.PaperStoplagTabListener;
import de.zeanon.testutils.plugin.handlers.tabcompleter.tablistener.SpigotStoplagTabListener;
import de.zeanon.testutils.plugin.update.Update;
import de.zeanon.testutils.plugin.utils.InternalFileUtils;
import de.zeanon.testutils.plugin.utils.ScoreBoard;
import de.zeanon.thunderfilemanager.ThunderFileManager;
import de.zeanon.thunderfilemanager.internal.files.config.ThunderConfig;
import java.io.File;
import java.io.IOException;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class InitMode {

	@Getter
	private ThunderConfig config;
	@Getter
	private RegionContainer regionContainer;

	public void initPlugin() {
		try {
			System.out.println("[" + TestUtils.getInstance().getName() + "] >> Loading Config...");
			InitMode.loadConfigs();
			System.out.println("[" + TestUtils.getInstance().getName() + "] >> Config file are loaded successfully.");
		} catch (RuntimeIOException e) {
			System.err.println("[" + TestUtils.getInstance().getName() + "] >> Could not load config file.");
			System.err.println("[" + TestUtils.getInstance().getName() + "] >> Maybe try to delete the config file and reload the plugin.");
			System.err.println("[" + TestUtils.getInstance().getName() + "] >> Unloading Plugin...");

			TestUtils.getPluginManager().disablePlugin(TestUtils.getInstance());
			return;
		}

		try {
			System.out.println("[" + TestUtils.getInstance().getName() + "] >> Initializing Config...");
			InitMode.initConfigs();
			System.out.println("[" + TestUtils.getInstance().getName() + "] >> Config file is initialized successfully.");
		} catch (RuntimeIOException e) {
			System.err.println("[" + TestUtils.getInstance().getName() + "] >> Could not update config file.");
			System.err.println("[" + TestUtils.getInstance().getName() + "] >> Maybe try to delete the config file and reload the plugin.");
			System.err.println("[" + TestUtils.getInstance().getName() + "] >> Unloading Plugin...");

			TestUtils.getPluginManager().disablePlugin(TestUtils.getInstance());
			return;
		}

		if (((TestUtils.getPluginManager().getPlugin("FastAsyncWorldEdit") != null
			  && TestUtils.getPluginManager().isPluginEnabled("FastAsyncWorldEdit"))
			 || (TestUtils.getPluginManager().getPlugin("WorldEdit") != null
				 && TestUtils.getPluginManager().isPluginEnabled("WorldEdit")))
			&& TestUtils.getPluginManager().getPlugin("WorldGuard") != null
			&& TestUtils.getPluginManager().isPluginEnabled("WorldGuard")) {

			CommandHandler commandHandler = new CommandHandler();
			TestUtilsTabCompleter testUtilsTabCompleter = new TestUtilsTabCompleter();

			Objects.notNull(TestUtils.getInstance().getCommand("testutils")).setExecutor(commandHandler);
			Objects.notNull(TestUtils.getInstance().getCommand("testutils")).setTabCompleter(testUtilsTabCompleter);
			Objects.notNull(TestUtils.getInstance().getCommand("testblock")).setExecutor(commandHandler);
			Objects.notNull(TestUtils.getInstance().getCommand("testblock")).setTabCompleter(testUtilsTabCompleter);
			Objects.notNull(TestUtils.getInstance().getCommand("tnt")).setExecutor(commandHandler);
			Objects.notNull(TestUtils.getInstance().getCommand("tnt")).setTabCompleter(testUtilsTabCompleter);

			TestUtils.getPluginManager().registerEvents(new EventListener(), TestUtils.getInstance());
			if (Bukkit.getVersion().contains("git-Paper")) {
				TestUtils.getPluginManager().registerEvents(new PaperStoplagTabListener(), TestUtils.getInstance());
			} else {
				TestUtils.getPluginManager().registerEvents(new SpigotStoplagTabListener(), TestUtils.getInstance());
			}

			InitMode.regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
			InitMode.cleanUpResets();

			for (final @NotNull Player p : Bukkit.getOnlinePlayers()) {
				ScoreBoard.initialize(p);
			}
		} else {
			InitMode.enableSleepMode();
		}
	}

	private void loadConfigs() {
		@Nullable Throwable cause = null;
		try {
			InitMode.config = ThunderFileManager.thunderConfig(TestUtils.getInstance().getDataFolder(), "config")
												.fromResource("resources/config.tf")
												.reloadSetting(Reload.INTELLIGENT)
												.commentSetting(Comment.PRESERVE)
												.concurrentData(true)
												.create();

			System.out.println("[" + TestUtils.getInstance().getName() + "] >> [Configs] >> 'config.tf' loaded.");
		} catch (final @NotNull RuntimeIOException | FileParseException e) {
			System.err.println("[" + TestUtils.getInstance().getName() + "] >> [Configs] >> 'config.tf' could not be loaded.");
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
					   .equals(TestUtils.getInstance().getDescription().getVersion())) {
			System.out.println("[" + TestUtils.getInstance().getName() + "] >> Updating Configs...");
			Update.checkConfigUpdate();
			System.out.println("[" + TestUtils.getInstance().getName() + "] >> Config files are updated successfully.");
		}
	}

	private void cleanUpResets() {
		try {
			@NotNull RegionManager tempManager;
			for (final @NotNull File worldFolder : BaseFileUtils.listFolders(new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/TestAreas"))) {
				tempManager = Objects.notNull(InitMode.getRegionContainer().get(new BukkitWorld(Bukkit.getWorld(worldFolder.getName()))));
				for (final @NotNull File regionFolder : BaseFileUtils.listFolders(worldFolder)) {
					if (!tempManager.hasRegion("testarea_" + regionFolder.getName() + "_north") || !tempManager.hasRegion("testarea_" + regionFolder.getName() + "_south")) {
						FileUtils.deleteDirectory(regionFolder);
						InternalFileUtils.deleteEmptyParent(regionFolder, new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/TestAreas"));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void enableSleepMode() {
		TestUtils.getPluginManager().registerEvents(new WakeupListener(), TestUtils.getInstance());
		Objects.notNull(TestUtils.getInstance().getCommand("testutils")).setExecutor(new SleepModeCommandHandler());
		Objects.notNull(TestUtils.getInstance().getCommand("testutils")).setTabCompleter(new SleepModeTabCompleter());
		System.out.println("[" + TestUtils.getInstance().getName() + "] >> Could not load plugin, it needs FastAsyncWorldEdit or WorldEdit to work.");
		System.out.println("[" + TestUtils.getInstance().getName() + "] >> " + TestUtils.getInstance().getName() + " will automatically activate when one of the above gets enabled.");
		System.out.println("[" + TestUtils.getInstance().getName() + "] >> Rudimentary function like updating and disabling will still work.");
	}
}