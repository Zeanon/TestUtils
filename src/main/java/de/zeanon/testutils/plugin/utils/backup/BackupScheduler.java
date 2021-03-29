package de.zeanon.testutils.plugin.utils.backup;

import de.zeanon.testutils.TestUtils;
import de.zeanon.testutils.plugin.utils.ConfigUtils;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class BackupScheduler {

	@Getter
	private final @NotNull Backup MANUAL_BACKUP = new ManualBackup();


	public void backup() {
		//Initialize hourly backups
		final @NotNull LocalDateTime hourlyStart = LocalDateTime.now()
																.withMinute(0)
																.withSecond(1)
																.plusHours(1);


		BackupScheduler.scheduleAtFixedRate(new HourlyBackup(), LocalDateTime.now().until(hourlyStart, ChronoUnit.SECONDS), TimeUnit.HOURS.toSeconds(1), TimeUnit.SECONDS);


		//Initialize daily backups
		final @NotNull LocalDateTime dailyStart = hourlyStart.withHour(5)
															 .plusDays(1);

		BackupScheduler.scheduleAtFixedRate(new DailyBackup(), LocalDateTime.now().until(dailyStart, ChronoUnit.SECONDS), TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);


		//Backup once the Plugin starts
		if (ConfigUtils.getInt("Backups", "startup") > 0) {
			BackupScheduler.schedule(new StartupBackup(), 5, TimeUnit.SECONDS);
		}
	}

	@SuppressWarnings("SameParameterValue")
	private void scheduleAtFixedRate(final @NotNull Backup backup, final long initialDelay, final long period, final @NotNull TimeUnit unit) {
		backup.runTaskTimer(TestUtils.getInstance(), unit.toSeconds(initialDelay) * 20, unit.toSeconds(period) * 20);
	}

	@SuppressWarnings("SameParameterValue")
	private void schedule(final @NotNull Backup backup, final long initialDelay, final @NotNull TimeUnit unit) {
		backup.runTaskLater(TestUtils.getInstance(), unit.toSeconds(initialDelay) * 20);
	}
}