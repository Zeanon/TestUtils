package de.zeanon.testutils.plugin.utils;

import de.zeanon.storagemanagercore.internal.utility.basic.Objects;
import de.zeanon.storagemanagercore.internal.utility.basic.Pair;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class CommandRequestUtils {

	private final @NotNull Map<String, String> deleteBlockRequests = new ConcurrentHashMap<>();
	private final @NotNull Map<String, String> deleteFolderRequests = new ConcurrentHashMap<>();
	private final @NotNull Map<String, Pair<String, String>> deleteBackupRequests = new ConcurrentHashMap<>();
	private final @NotNull Map<String, Pair<String, String>> overwriteBackupRequests = new ConcurrentHashMap<>();
	private final @NotNull Map<String, String> renameRequests = new ConcurrentHashMap<>();
	private final @NotNull Map<String, String> renameFolderRequests = new ConcurrentHashMap<>();


	public void addDeleteBlockRequest(final @NotNull UUID uuid, final String name) {
		CommandRequestUtils.deleteBlockRequests.put(uuid.toString(), name);
	}

	public void removeDeleteBlockRequest(final @NotNull UUID uuid) {
		CommandRequestUtils.deleteBlockRequests.remove(uuid.toString());
	}

	public boolean checkDeleteBlockRequest(final @NotNull UUID uuid, final String name) {
		return CommandRequestUtils.deleteBlockRequests.containsKey(uuid.toString())
			   && CommandRequestUtils.deleteBlockRequests.get(uuid.toString()).equalsIgnoreCase(name);
	}


	public void addDeleteBackupRequest(final @NotNull UUID uuid, final @NotNull String name, final @NotNull String region) {
		CommandRequestUtils.deleteBackupRequests.put(uuid.toString(), new Pair<>(name, region));
	}

	public void removeDeleteBackupRequest(final @NotNull UUID uuid) {
		CommandRequestUtils.deleteBackupRequests.remove(uuid.toString());
	}

	public @Nullable String checkDeleteBackupRequest(final @NotNull UUID uuid, final @NotNull String name) {
		return (CommandRequestUtils.deleteBackupRequests.containsKey(uuid.toString())
				&& Objects.notNull(CommandRequestUtils.deleteBackupRequests.get(uuid.toString()).getKey()).equalsIgnoreCase(name))
			   ? CommandRequestUtils.deleteBackupRequests.get(uuid.toString()).getValue()
			   : null;
	}


	public void addOverwriteBackupRequest(final @NotNull UUID uuid, final @NotNull String name, final @NotNull String region) {
		CommandRequestUtils.overwriteBackupRequests.put(uuid.toString(), new Pair<>(name, region));
	}

	public void removeOverwriteBackupRequest(final @NotNull UUID uuid) {
		CommandRequestUtils.overwriteBackupRequests.remove(uuid.toString());
	}

	public @Nullable String checkOverwriteBackupRequest(final @NotNull UUID uuid, final @NotNull String name) {
		return (CommandRequestUtils.overwriteBackupRequests.containsKey(uuid.toString())
				&& Objects.notNull(CommandRequestUtils.overwriteBackupRequests.get(uuid.toString()).getKey()).equalsIgnoreCase(name))
			   ? CommandRequestUtils.overwriteBackupRequests.get(uuid.toString()).getValue()
			   : null;
	}


	public void addDeleteFolderRequest(final @NotNull UUID uuid, final String name) {
		CommandRequestUtils.deleteFolderRequests.put(uuid.toString(), name);
	}

	public void removeDeleteFolderRequest(final @NotNull UUID uuid) {
		CommandRequestUtils.deleteFolderRequests.remove(uuid.toString());
	}

	public boolean checkDeleteFolderRequest(final @NotNull UUID uuid, final String name) {
		return CommandRequestUtils.deleteFolderRequests.containsKey(uuid.toString())
			   && CommandRequestUtils.deleteFolderRequests.get(uuid.toString()).equalsIgnoreCase(name);
	}


	public void addRenameBlockRequest(final @NotNull UUID uuid, final String name) {
		CommandRequestUtils.renameRequests.put(uuid.toString(), name);
	}

	public void removeRenameBlockRequest(final @NotNull UUID uuid) {
		CommandRequestUtils.renameRequests.remove(uuid.toString());
	}

	public boolean checkRenameBlockRequest(final @NotNull UUID uuid, final String name) {
		return CommandRequestUtils.renameRequests.containsKey(uuid.toString())
			   && CommandRequestUtils.renameRequests.get(uuid.toString()).equalsIgnoreCase(name);
	}


	public void addRenameFolderRequest(final @NotNull UUID uuid, final String name) {
		CommandRequestUtils.renameFolderRequests.put(uuid.toString(), name);
	}

	public void removeRenameFolderRequest(final @NotNull UUID uuid) {
		CommandRequestUtils.renameFolderRequests.remove(uuid.toString());
	}

	public boolean checkRenameFolderRequest(final @NotNull UUID uuid, final String name) {
		return CommandRequestUtils.renameFolderRequests.containsKey(uuid.toString())
			   && CommandRequestUtils.renameFolderRequests.get(uuid.toString()).equalsIgnoreCase(name);
	}
}
