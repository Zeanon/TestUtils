package de.zeanon.testutils.plugin.utils.region;

import com.sk89q.worldedit.math.BlockVector3;
import de.zeanon.testutils.plugin.utils.enums.Flag;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;


public interface Region {

	void set(final @NotNull Flag flagType, final @NotNull Flag.Value<?> value);

	@SuppressWarnings("rawtypes")
	Flag.Value get(final @NotNull Flag flagType);

	@SuppressWarnings("rawtypes")
	@NotNull Map<Flag, Flag.Value> getFlags();

	@NotNull World getWorld();

	@NotNull String getName();

	@NotNull String getType();

	void saveData();


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