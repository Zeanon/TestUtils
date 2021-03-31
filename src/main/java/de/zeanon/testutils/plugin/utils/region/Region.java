package de.zeanon.testutils.plugin.utils.region;

import com.sk89q.worldedit.math.BlockVector3;
import de.zeanon.testutils.plugin.utils.enums.Flag;
import de.zeanon.testutils.plugin.utils.enums.flagvalues.FIRE;
import de.zeanon.testutils.plugin.utils.enums.flagvalues.ITEM_DROPS;
import de.zeanon.testutils.plugin.utils.enums.flagvalues.LEAVES_DECAY;
import de.zeanon.testutils.plugin.utils.enums.flagvalues.TNT;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;


public interface Region {

	void setTnt(final boolean tnt);

	void setStoplag(final boolean stoplag);

	void setFire(final boolean fire);

	void setItemDrops(final boolean itemDrops);

	void setLeavesDecay(final boolean leavesDecay);

	default void set(final @NotNull Flag flag, final Flag.Value<?> value) throws IllegalFlagException {
		if (flag == Flag.TNT) {
			this.setTnt(value.getEnumValue() == TNT.ALLOW);
		} else if (flag == Flag.FIRE) {
			this.setFire(value.getEnumValue() == FIRE.ALLOW);
		} else if (flag == Flag.ITEM_DROPS) {
			this.setItemDrops(value.getEnumValue() == ITEM_DROPS.ALLOW);
		} else if (flag == Flag.LEAVES_DECAY) {
			this.setItemDrops(value.getEnumValue() == LEAVES_DECAY.ALLOW);
		} else {
			throw new IllegalFlagException();
		}
	}

	@NotNull World getWorld();

	@NotNull String getName();

	@NotNull String getType();

	boolean tnt();

	boolean stoplag();

	boolean fire();

	boolean itemDrops();

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	boolean leavesDecay();


	@Getter
	@AllArgsConstructor
	class Point {

		final int x;
		final int y;
		final int z;

		public @NotNull BlockVector3 toBlockVector3() {
			return BlockVector3.at(this.x, this.y, this.z);
		}
	}
}