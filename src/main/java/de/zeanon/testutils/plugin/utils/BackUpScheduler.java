package de.zeanon.testutils.plugin.utils;

import de.zeanon.testutils.plugin.utils.backup.DailyBackup;
import de.zeanon.testutils.plugin.utils.backup.HourlyBackup;
import de.zeanon.testutils.plugin.utils.backup.StartupBackup;
import de.zeanon.testutils.plugin.utils.interfaces.Backup;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class BackUpScheduler {

	private final @NotNull ScheduledExecutorService daily = Executors.newSingleThreadScheduledExecutor();
	private final @NotNull ScheduledExecutorService hourly = Executors.newSingleThreadScheduledExecutor();


	public void backup() {
		//Backup once the Plugin starts
		final @NotNull Backup onStart = new StartupBackup();
		onStart.run();


		//Initialize hourly backups
		final @NotNull LocalDateTime hourlyStart = LocalDateTime.now()
																.withMinute(0)
																.withSecond(1)
																.plusHours(1);

		final @NotNull Backup hourlyBackup = new HourlyBackup();

		BackUpScheduler.schedule(BackUpScheduler.hourly, hourlyBackup, LocalDateTime.now().until(hourlyStart, ChronoUnit.SECONDS), TimeUnit.HOURS.toSeconds(1), TimeUnit.SECONDS);


		//Initialize daily backups
		final @NotNull LocalDateTime dailyStart = hourlyStart.withHour(0)
															 .plusDays(1);

		final @NotNull Backup dailyBackup = new DailyBackup();

		BackUpScheduler.schedule(BackUpScheduler.daily, dailyBackup, LocalDateTime.now().until(dailyStart, ChronoUnit.SECONDS), TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
	}

	public void terminate() {
		try {
			BackUpScheduler.hourly.shutdown();
			BackUpScheduler.hourly.awaitTermination(100, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}

		try {
			BackUpScheduler.daily.shutdown();
			BackUpScheduler.daily.awaitTermination(100, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}

	private void schedule(final @NotNull ScheduledExecutorService executorService, final @NotNull Backup backup, final long initialDelay, final long period, final @NotNull TimeUnit unit) {
		executorService.scheduleAtFixedRate(backup, initialDelay, period, unit);
	}
}