package de.zeanon.testutils.plugin.utils;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class GlobalRequestUtils {

	private final @NotNull Set<String> disableRequests = new ConcurrentSkipListSet<>();
	private final @NotNull Set<String> updateRequests = new ConcurrentSkipListSet<>();
	private boolean consoleUpdate = false;
	private boolean consoleDisable = false;

	public void addDisableRequest(final @NotNull String uuid) {
		GlobalRequestUtils.disableRequests.add(uuid);
	}

	public void removeDisableRequest(final @NotNull String uuid) {
		GlobalRequestUtils.disableRequests.remove(uuid);
	}

	public boolean checkDisableRequest(final @NotNull String uuid) {
		return GlobalRequestUtils.disableRequests.contains(uuid);
	}

	public void addUpdateRequest(final @NotNull String uuid) {
		GlobalRequestUtils.updateRequests.add(uuid);
	}

	public void removeUpdateRequest(final @NotNull String uuid) {
		GlobalRequestUtils.updateRequests.remove(uuid);
	}

	public boolean checkUpdateRequest(final @NotNull String uuid) {
		return GlobalRequestUtils.updateRequests.contains(uuid);
	}

	public void addConsoleDisableRequest() {
		GlobalRequestUtils.consoleDisable = true;
	}

	public void removeConsoleDisableRequest() {
		GlobalRequestUtils.consoleDisable = false;
	}

	public boolean checkConsoleDisableRequest() {
		return GlobalRequestUtils.consoleDisable;
	}

	public void addConsoleUpdateRequest() {
		GlobalRequestUtils.consoleUpdate = true;
	}

	public void removeConsoleUpdateRequest() {
		GlobalRequestUtils.consoleUpdate = false;
	}

	public boolean checkConsoleUpdateRequest() {
		return GlobalRequestUtils.consoleUpdate;
	}
}