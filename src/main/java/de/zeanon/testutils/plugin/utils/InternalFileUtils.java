package de.zeanon.testutils.plugin.utils;

import java.io.File;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class InternalFileUtils {

	public @NotNull String deleteEmptyParent(final @NotNull File file, final @Nullable File stopHere) {
		if (!file.getAbsoluteFile().getParentFile().equals(stopHere) && file.getAbsoluteFile().getParentFile().delete()) { //NOSONAR
			return InternalFileUtils.deleteEmptyParent(file.getAbsoluteFile().getParentFile(), stopHere);
		}
		return file.getName();
	}
}