package de.zeanon.testutils.plugin.utils;

import de.zeanon.testutils.plugin.utils.enums.BackUpMode;
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
		final @NotNull BackupCreator onStart = new BackupCreator(BackUpMode.STARTUP);
		onStart.run();


		//Initialize hourly backups
		final @NotNull LocalDateTime hourlyStart = LocalDateTime.now()
																.withMinute(0)
																.withSecond(0)
																.plusHours(1);

		final @NotNull BackupCreator hourlyBackup = new BackupCreator(BackUpMode.HOURLY);

		BackUpScheduler.hourly.scheduleAtFixedRate(hourlyBackup, LocalDateTime.now().until(hourlyStart, ChronoUnit.SECONDS), TimeUnit.HOURS.toSeconds(1), TimeUnit.SECONDS);


		//Initialize daily backups
		final @NotNull LocalDateTime dailyStart = hourlyStart.withHour(0)
															 .plusDays(1);

		final @NotNull BackupCreator dailyBackup = new BackupCreator(BackUpMode.DAILY);

		BackUpScheduler.daily.scheduleAtFixedRate(dailyBackup, LocalDateTime.now().until(dailyStart, ChronoUnit.SECONDS), TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
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
}