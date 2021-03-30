package de.zeanon.testutils.plugin.utils.region;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;


public interface Region {

	void setTnt(final boolean tnt);

	void setStoplag(final boolean stoplag);

	void setFire(final boolean fire);

	void setItemDrops(final boolean itemDrops);

	World getWorld();

	@NotNull String getName();

	boolean tnt();

	boolean stoplag();

	boolean fire();

	boolean itemDrops();
}