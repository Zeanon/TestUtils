package de.zeanon.testutils.plugin.utils.enums;

import de.zeanon.testutils.plugin.utils.enums.flagvalues.TNT;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Getter
@AllArgsConstructor
public enum TNTMode {

	ALLOW(TNT.ALLOW),
	DENY(TNT.DENY);

	private final @NotNull TNT value;

	public static @NotNull TNT parse(final @NotNull TNT current, final @Nullable TNTMode tntMode) {
		return current.getValue(tntMode);
	}
}