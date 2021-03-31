package de.zeanon.testutils.plugin.utils.region;

import de.zeanon.jsonfilemanager.JsonFileManager;
import de.zeanon.jsonfilemanager.internal.files.raw.JsonFile;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.TestUtils;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;


public class GlobalRegion implements Region {

	private final @NotNull JsonFile jsonFile;

	public GlobalRegion(final @NotNull String world) {
		this.jsonFile = JsonFileManager.jsonFile(TestUtils.getInstance().getDataFolder(), "Regions/__" + world + "__")
									   .fromResource("resources/global.json")
									   .create();
		this.jsonFile.set("world", world);
	}

	GlobalRegion(final @NotNull File file) {
		this.jsonFile = JsonFileManager.jsonFile(file)
									   .create();
	}

	@Override
	public void setTnt(final boolean tnt) {
		this.jsonFile.setUseArray(new String[]{"tnt"}, tnt);
	}

	@Override
	public void setStoplag(final boolean stoplag) {
		this.jsonFile.setUseArray(new String[]{"stoplag"}, stoplag);
	}

	@Override
	public void setFire(final boolean fire) {
		this.jsonFile.setUseArray(new String[]{"fire"}, fire);
	}

	@Override
	public void setItemDrops(final boolean itemDrops) {
		this.jsonFile.setUseArray(new String[]{"itemdrops"}, itemDrops);
	}

	@Override
	public void setLeavesDecay(final boolean leavesDecay) {
		this.jsonFile.setUseArray(new String[]{"leavesdecay"}, leavesDecay);
	}

	@Override
	public @NotNull World getWorld() {
		return Objects.notNull(Bukkit.getWorld(Objects.notNull(this.jsonFile.getStringUseArray("world"))));
	}

	@Override
	public @NotNull String getType() {
		return "global";
	}

	@Override
	public @NotNull String getName() {
		return BaseFileUtils.removeExtension(this.jsonFile.getName());
	}

	@Override
	public boolean tnt() {
		return this.jsonFile.getBooleanUseArray("tnt");
	}

	@Override
	public boolean stoplag() {
		return this.jsonFile.getBooleanUseArray("stoplag");
	}

	@Override
	public boolean fire() {
		return this.jsonFile.getBooleanUseArray("fire");
	}

	public boolean hasChanged() {
		return this.jsonFile.getBooleanUseArray("changed");
	}

	@Override
	public boolean itemDrops() {
		return this.jsonFile.getBooleanUseArray("itemdrops");
	}

	@Override
	public boolean leavesDecay() {
		return this.jsonFile.getBooleanUseArray("leavesdecay");
	}
}
