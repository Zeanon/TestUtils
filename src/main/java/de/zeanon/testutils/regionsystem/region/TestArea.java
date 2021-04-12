package de.zeanon.testutils.regionsystem.region;

import de.zeanon.testutils.regionsystem.RegionType;
import java.io.File;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;


public class TestArea extends DefinedRegion {

	public TestArea(final @NotNull File file) {
		super(file);
	}

	public TestArea(final @NotNull String name, final @NotNull Point firstPoint, final @NotNull Point secondPoint, final @NotNull World world) {
		super(name,
			  firstPoint,
			  secondPoint,
			  world,
			  RegionType.TESTAREA);
	}
}