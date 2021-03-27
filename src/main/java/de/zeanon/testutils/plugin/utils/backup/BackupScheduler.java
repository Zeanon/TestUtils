package de.zeanon.testutils.plugin.utils.backup;

import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class BackupScheduler {

	private final @NotNull ScheduledExecutorService internalScheduler = Executors.newSingleThreadScheduledExecutor();
	@Getter
	private final @NotNull Backup MANUAL_BACKUP = new ManualBackup();


	public void backup() {
		//Initialize hourly backups
		final @NotNull LocalDateTime hourlyStart = LocalDateTime.now()
																.withMinute(0)
																.withSecond(1)
																.plusHours(1);


		BackupScheduler.scheduleAtFixedRate(BackupScheduler.internalScheduler, new HourlyBackup(), LocalDateTime.now().until(hourlyStart, ChronoUnit.SECONDS), TimeUnit.HOURS.toSeconds(1), TimeUnit.SECONDS);


		//Initialize daily backups
		final @NotNull LocalDateTime dailyStart = hourlyStart.withHour(0)
															 .plusDays(1);

		BackupScheduler.scheduleAtFixedRate(BackupScheduler.internalScheduler, new DailyBackup(), LocalDateTime.now().until(dailyStart, ChronoUnit.SECONDS), TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);


		//Backup once the Plugin starts
		if (ConfigUtils.getInt("Backups", "startup") > 0) {
			System.out.println("[" + TestUtils.getInstance().getName() + "] >> Creating Startup-Backup...");
			BackupScheduler.schedule(BackupScheduler.internalScheduler, new StartupBackup(), 10, TimeUnit.SECONDS);
			System.out.println("[" + TestUtils.getInstance().getName() + "] >> Created Startup-Backup.");
		}
	}

	public void terminate() {
		try {
			BackupScheduler.internalScheduler.shutdown();
			BackupScheduler.internalScheduler.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}

	@SuppressWarnings("SameParameterValue")
	private void scheduleAtFixedRate(final @NotNull ScheduledExecutorService executorService, final @NotNull Backup backup, final long initialDelay, final long period, final @NotNull TimeUnit unit) {
		executorService.scheduleAtFixedRate(backup, initialDelay, period, unit);
	}

	@SuppressWarnings("SameParameterValue")
	private void schedule(final @NotNull ScheduledExecutorService executorService, final @NotNull Backup backup, final long initialDelay, final @NotNull TimeUnit unit) {
		executorService.schedule(backup, initialDelay, unit);
	}
}