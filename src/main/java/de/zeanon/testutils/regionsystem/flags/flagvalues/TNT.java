package de.zeanon.testutils.regionsystem.flags.flagvalues;

import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.testutils.plugin.utils.enums.TNTMode;
import de.zeanon.testutils.regionsystem.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Getter
@AllArgsConstructor
public enum TNT implements Flag.Value<TNT> {

	ALLOW(ChatColor.GREEN + "allow"),
	DENY(ChatColor.RED + "deny");


	private static @Nullable TNT[] values;
	private final @NotNull String chatValue;

	@Override
	public @NotNull TNT[] getValues() {
		if (TNT.values == null) {
			TNT.values = TNT.values(); //NOSONAR
		}
		//noinspection NullableProblems
		return Objects.notNull(TNT.values);
	}

	@Override
	public @NotNull TNT getValue() {
		return this;
	}

	public @NotNull TNT getValue(final @Nullable TNTMode tntMode) {
		if (tntMode == null) {
			return this == TNT.ALLOW ? TNT.DENY : TNT.ALLOW;
		} else {
			return tntMode.getValue();
		}
	}

	@Override
	public @NotNull TNT getValueOf(final @NotNull String name) {
		try {
			return TNT.valueOf(name);
		} catch (IllegalArgumentException e) {
			return TNT.ALLOW;
		}
	}
}