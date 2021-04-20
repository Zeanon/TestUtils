package de.zeanon.testutils.plugin.utils;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class GlobalRequestUtils {

	private final @NotNull Set<String> updateRequests = new ConcurrentSkipListSet<>();

	public void addUpdateRequest(final @NotNull UUID uuid) {
		GlobalRequestUtils.updateRequests.add(uuid.toString());
	}

	public void removeUpdateRequest(final @NotNull UUID uuid) {
		GlobalRequestUtils.updateRequests.remove(uuid.toString());
	}

	public boolean checkUpdateRequest(final @NotNull UUID uuid) {
		return GlobalRequestUtils.updateRequests.contains(uuid.toString());
	}
}