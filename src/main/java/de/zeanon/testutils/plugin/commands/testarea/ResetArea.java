package de.zeanon.testutils.plugin.commands.testarea;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.zeanon.storagemanagercore.internal.utility.basic.BaseFileUtils;
import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.GlobalMessageUtils;
import de.zeanon.testutils.plugin.utils.SessionFactory;
import de.zeanon.testutils.plugin.utils.TestAreaUtils;
import java.io.File;
import java.io.IOException;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@UtilityClass
public class ResetArea {

	public void execute(final @NotNull String[] args, final @NotNull Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (args.length == 1) {
					final @NotNull String worldName = p.getWorld().getName();
					final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
					final @Nullable ProtectedRegion oppositeRegion = TestAreaUtils.getOppositeRegion(p);

					if (tempRegion == null || oppositeRegion == null) {
						GlobalMessageUtils.sendNotApplicableRegion(p);
					} else {
						try (final @NotNull EditSession editSession = SessionFactory.createSession(p)) {
							final @NotNull File tempFile = new File(TestUtils
																			.getInstance()
																			.getDataFolder()
																			.getAbsolutePath() + "/TestAreas/" + worldName + "/" + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + "/" + tempRegion.getId().substring(tempRegion.getId().length() - 5) + ".schem");
							final @NotNull File oppositeFile = new File(TestUtils
																				.getInstance()
																				.getDataFolder()
																				.getAbsolutePath() + "/TestAreas/" + worldName + "/" + oppositeRegion.getId().substring(9, oppositeRegion.getId().length() - 6) + "/" + oppositeRegion.getId().substring(oppositeRegion.getId().length() - 5) + ".schem");
							if (tempFile.exists()
								&& oppositeFile.exists()) {
								ResetArea.pasteSide(tempRegion, editSession, tempFile);
								ResetArea.pasteSide(oppositeRegion, editSession, oppositeFile);

								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "You pasted the reset for '"
											  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
							} else {
								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "There is no reset for '"
											  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
							}
						} catch (WorldEditException | IOException e) {
							p.sendMessage(GlobalMessageUtils.messageHead
										  + ChatColor.RED + "There has been an error, pasting the reset for '"
										  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "'.");
							e.printStackTrace();
						}
					}
				} else if (args.length == 2) {
					if (args[1].equalsIgnoreCase("-here")) {
						final @NotNull String worldName = p.getWorld().getName();
						final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getRegion(p);
						if (tempRegion == null) {
							GlobalMessageUtils.sendNotApplicableRegion(p);
						} else {
							try (final @NotNull EditSession editSession = SessionFactory.createSession(p)) {
								final @NotNull File tempFile = new File(TestUtils
																				.getInstance()
																				.getDataFolder()
																				.getAbsolutePath() + "/TestAreas/" + worldName + "/" + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + "/" + tempRegion.getId().substring(tempRegion.getId().length() - 5) + ".schem");
								if (tempFile.exists()) {
									ResetArea.pasteSide(tempRegion, editSession, tempFile);

									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "You pasted the reset for '"
												  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "' on your side.");
								} else {
									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "There is no reset for '"
												  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "' on your side.");
								}
							} catch (WorldEditException | IOException e) {
								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "There has been an error, pasting the reset for '"
											  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "' on your side.");
								e.printStackTrace();
							}
						}
					} else if (args[1].equalsIgnoreCase("-other")) {
						final @NotNull String worldName = p.getWorld().getName();
						final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getOppositeRegion(p);
						if (tempRegion == null) {
							GlobalMessageUtils.sendNotApplicableRegion(p);
						} else {
							try (final @NotNull EditSession editSession = SessionFactory.createSession(p)) {
								final @NotNull File tempFile = new File(TestUtils
																				.getInstance()
																				.getDataFolder()
																				.getAbsolutePath() + "/TestAreas/" + worldName + "/" + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + "/" + tempRegion.getId().substring(tempRegion.getId().length() - 5) + ".schem");
								if (tempFile.exists()) {
									ResetArea.pasteSide(tempRegion, editSession, tempFile);

									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "You pasted the reset for '"
												  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "' on the other side.");
								} else {
									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "There is no reset for '"
												  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "' on the other side.");
								}
							} catch (WorldEditException | IOException e) {
								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "There has been an error, pasting the reset for '"
											  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "' on the other side.");
								e.printStackTrace();
							}
						}
					} else if (args[1].equalsIgnoreCase("-north") || args[1].equalsIgnoreCase("-n")) {
						final @NotNull String worldName = p.getWorld().getName();
						final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getNorthRegion(p);
						if (tempRegion == null) {
							GlobalMessageUtils.sendNotApplicableRegion(p);
						} else {
							try (final @NotNull EditSession editSession = SessionFactory.createSession(p)) {
								final @NotNull File tempFile = new File(TestUtils
																				.getInstance()
																				.getDataFolder()
																				.getAbsolutePath() + "/TestAreas/" + worldName + "/" + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + "/" + tempRegion.getId().substring(tempRegion.getId().length() - 5) + ".schem");
								if (tempFile.exists()) {
									ResetArea.pasteSide(tempRegion, editSession, tempFile);

									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "You pasted the reset for '"
												  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "' on the north side.");
								} else {
									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "There is no reset for '"
												  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "' on the north side.");
								}
							} catch (WorldEditException | IOException e) {
								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "There has been an error, pasting the reset for '"
											  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "' on the north side.");
								e.printStackTrace();
							}
						}
					} else if (args[1].equalsIgnoreCase("-south") || args[1].equalsIgnoreCase("-s")) {
						final @NotNull String worldName = p.getWorld().getName();
						final @Nullable ProtectedRegion tempRegion = TestAreaUtils.getSouthRegion(p);
						if (tempRegion == null) {
							GlobalMessageUtils.sendNotApplicableRegion(p);
						} else {
							try (final @NotNull EditSession editSession = SessionFactory.createSession(p)) {
								final @NotNull File tempFile = new File(TestUtils
																				.getInstance()
																				.getDataFolder()
																				.getAbsolutePath() + "/TestAreas/" + worldName + "/" + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + "/" + tempRegion.getId().substring(tempRegion.getId().length() - 5) + ".schem");
								if (tempFile.exists()) {
									ResetArea.pasteSide(tempRegion, editSession, tempFile);

									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "You pasted the reset for '"
												  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "' on the south side.");
								} else {
									p.sendMessage(GlobalMessageUtils.messageHead
												  + ChatColor.RED + "There is no reset for '"
												  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "' on the south side.");
								}
							} catch (WorldEditException | IOException e) {
								p.sendMessage(GlobalMessageUtils.messageHead
											  + ChatColor.RED + "There has been an error, pasting the reset for '"
											  + ChatColor.DARK_RED + tempRegion.getId().substring(9, tempRegion.getId().length() - 6) + ChatColor.RED + "' on the south side.");
								e.printStackTrace();
							}
						}
					} else {
						p.sendMessage(GlobalMessageUtils.messageHead
									  + ChatColor.RED + "Too many arguments.");
					}
				} else {
					p.sendMessage(GlobalMessageUtils.messageHead
								  + ChatColor.RED + "Too many arguments.");
				}
			}
		}.runTask(TestUtils.getInstance());
	}

	private void pasteSide(final @NotNull ProtectedRegion tempRegion, final @NotNull EditSession editSession, final @NotNull File file) throws WorldEditException, IOException {
		try (final @NotNull ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(BaseFileUtils.createNewInputStreamFromFile(file))) {
			final @NotNull Clipboard clipboard = reader.read();

			final @NotNull BlockVector3 pastePoint = tempRegion.getMinimumPoint();

			final @NotNull ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);

			Operation operation = clipboardHolder
					.createPaste(editSession)
					.to(pastePoint)
					.ignoreAirBlocks(false)
					.build();

			Operations.complete(operation);
		}
	}
}