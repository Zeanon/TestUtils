package de.zeanon.testutils.plugin.backup;

import de.zeanon.testutils.TestUtils;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


@UtilityClass
public class BackupScheduler {

	@Getter
	private final @NotNull Backup MANUAL_BACKUP;

	static {
		MANUAL_BACKUP = new ManualBackup();
	}

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

		BackupScheduler.scheduleAtFixedRate(new HourlyBackup(), LocalDateTime.now().until(hourlyStart, ChronoUnit.SECONDS), TimeUnit.HOURS.toSeconds(1));


		//Initialize daily backups
		final @NotNull LocalDateTime dailyStart = hourlyStart.withHour(5)
															 .plusDays(1);

		BackupScheduler.scheduleAtFixedRate(new DailyBackup(), LocalDateTime.now().until(dailyStart, ChronoUnit.SECONDS) % (TimeUnit.DAYS.toSeconds(1)), TimeUnit.DAYS.toSeconds(1));

		//Backup once the Plugin starts
		(new StartupBackup()).runTaskLater(TestUtils.getInstance(), 100);
	}

	private void scheduleAtFixedRate(final @NotNull BukkitRunnable runnable, final long wait, final long period) {
		runnable.runTaskTimer(TestUtils.getInstance(), TimeUnit.SECONDS.toSeconds(wait) * 20, TimeUnit.SECONDS.toSeconds(period) * 20);
	}
}