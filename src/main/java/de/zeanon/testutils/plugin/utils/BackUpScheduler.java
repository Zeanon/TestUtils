package de.zeanon.testutils.plugin.utils;

import de.zeanon.testutils.plugin.utils.enums.BackUpMode;
import java.time.ZonedDateTime;
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
		final @NotNull BackupCreator onStart = new BackupCreator(BackUpMode.STARTUP);
		onStart.run();


		//Initialize hourly backups
		final @NotNull ZonedDateTime hourlyStart = ZonedDateTime.now()
																.withMinute(0)
																.withSecond(0)
																.plusHours(1);

		final @NotNull BackupCreator hourlyBackup = new BackupCreator(BackUpMode.HOURLY);

		BackUpScheduler.hourly.scheduleAtFixedRate(hourlyBackup, ZonedDateTime.now().until(hourlyStart, ChronoUnit.SECONDS), TimeUnit.HOURS.toSeconds(1), TimeUnit.SECONDS);


		//Initialize daily backups
		final @NotNull ZonedDateTime dailyStart = ZonedDateTime.now()
															   .withHour(0)
															   .withMinute(0)
															   .withSecond(0)
															   .plusDays(1);

		final @NotNull BackupCreator dailyBackup = new BackupCreator(BackUpMode.DAILY);

		BackUpScheduler.daily.scheduleAtFixedRate(dailyBackup, ZonedDateTime.now().until(dailyStart, ChronoUnit.SECONDS), TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
	}

	public void terminate() {
		try {
			BackUpScheduler.hourly.shutdown();
			BackUpScheduler.hourly.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}

		try {
			BackUpScheduler.daily.shutdown();
			BackUpScheduler.daily.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}
}