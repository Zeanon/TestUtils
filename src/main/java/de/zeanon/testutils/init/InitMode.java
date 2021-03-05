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
import de.zeanon.testutils.plugin.handlers.*;
import de.zeanon.testutils.plugin.update.Update;
import de.zeanon.thunderfilemanager.ThunderFileManager;
import de.zeanon.thunderfilemanager.internal.files.config.ThunderConfig;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.World;
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
			LocalTabCompleter localTabCompleter = new LocalTabCompleter();
			Objects.notNull(TestUtils.getInstance().getCommand("testutils")).setExecutor(commandHandler);
			Objects.notNull(TestUtils.getInstance().getCommand("testutils")).setTabCompleter(localTabCompleter);
			Objects.notNull(TestUtils.getInstance().getCommand("testblock")).setExecutor(commandHandler);
			Objects.notNull(TestUtils.getInstance().getCommand("testblock")).setTabCompleter(localTabCompleter);
			Objects.notNull(TestUtils.getInstance().getCommand("tnt")).setExecutor(commandHandler);
			Objects.notNull(TestUtils.getInstance().getCommand("tnt")).setTabCompleter(localTabCompleter);
			Objects.notNull(TestUtils.getInstance().getCommand("/update")).setExecutor(commandHandler);
			Objects.notNull(TestUtils.getInstance().getCommand("/update")).setTabCompleter(localTabCompleter);
			TestUtils.getPluginManager().registerEvents(new EventListener(), TestUtils.getInstance());
			InitMode.regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
			InitMode.cleanUpResets();
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
			for (final @NotNull File tempFile : BaseFileUtils.listFilesOfType(new File(TestUtils.getInstance().getDataFolder().getAbsolutePath() + "/TestAreas"), "schem")) {
				boolean exists = false;
				@NotNull RegionManager tempManager;
				for (final @NotNull World world : Bukkit.getWorlds()) {
					tempManager = Objects.notNull(InitMode.regionContainer.get(new BukkitWorld(world)));
					if (tempManager.hasRegion(BaseFileUtils.removeExtension(tempFile.getName()))) {
						exists = true;
						break;
					}
				}
				if (!exists) {
					Files.delete(tempFile.toPath());
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