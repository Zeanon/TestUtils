package de.zeanon.testutils.plugin.utils.backup;

import de.zeanon.testutils.TestUtils;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class BackupScheduler {

	@Getter
	private final @NotNull Backup MANUAL_BACKUP = new ManualBackup();
	private final @NotNull ScheduledExecutorService internalScheduler = Executors.newSingleThreadScheduledExecutor();

	public void initialize() {
		new BukkitRunnable() {
			@Override
			public void run() {
				BackupScheduler.initBackups();
			}
		}.runTask(TestUtils.getInstance());
	}


	public void initBackups() {
		//Initialize hourly backups
		final @NotNull LocalDateTime hourlyStart = LocalDateTime.now()
																.withMinute(0)
																.withSecond(1)
																.plusHours(1);

		BackupScheduler.internalScheduler.scheduleAtFixedRate(new HourlyBackup(), LocalDateTime.now().until(hourlyStart, ChronoUnit.SECONDS), TimeUnit.HOURS.toSeconds(1), TimeUnit.SECONDS);


		//Initialize daily backups
		final @NotNull LocalDateTime dailyStart = hourlyStart.withHour(5)
															 .plusDays(1);

		BackupScheduler.internalScheduler.scheduleAtFixedRate(new DailyBackup(), LocalDateTime.now().until(dailyStart, ChronoUnit.SECONDS) % (TimeUnit.DAYS.toSeconds(1)), TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);

		//Backup once the Plugin starts
		BackupScheduler.internalScheduler.schedule(new StartupBackup(), 0, TimeUnit.SECONDS);
	}

	public void terminate() {
		for (final @NotNull Runnable task : BackupScheduler.internalScheduler.shutdownNow()) {
			//noinspection rawtypes
			((FutureTask) task).cancel(true);
		}
	}
}