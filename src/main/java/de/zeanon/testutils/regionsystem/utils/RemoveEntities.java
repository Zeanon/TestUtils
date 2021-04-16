package de.zeanon.testutils.regionsystem.utils;

import de.zeanon.testutils.regionsystem.region.Region;
import java.util.*;
import lombok.experimental.UtilityClass;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class RemoveEntities {

	public long removeEntities(final @NotNull Region... regions) {
		long count = 0;
		final @NotNull Map<World, Set<Region>> worldRegionMap = new HashMap<>();
		for (final @NotNull Region region : regions) {
			if (worldRegionMap.containsKey(region.getWorld())) {
				worldRegionMap.get(region.getWorld()).add(region);
			} else {
				worldRegionMap.put(region.getWorld(), new HashSet<>(Collections.singletonList(region)));
			}
		}
		for (final @NotNull Map.Entry<World, Set<Region>> mapEntry : worldRegionMap.entrySet()) {
			count += mapEntry.getKey().getEntities().stream()
							 .filter(e -> !(e instanceof Player))
							 .filter(e -> {
								 for (final @NotNull Region region : mapEntry.getValue()) {
									 if (region.inRegion(e.getLocation())) {
										 return true;
									 }
								 }
								 return false;
							 })
							 .peek(Entity::remove) //NOSONAR
							 .count();
		}
		return count;
	}
}