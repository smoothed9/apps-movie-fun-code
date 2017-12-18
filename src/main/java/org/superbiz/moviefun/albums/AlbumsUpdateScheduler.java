package org.superbiz.moviefun.albums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.superbiz.moviefun.scheduler.AlbumSchedulerBean;

@Configuration
@EnableAsync
@EnableScheduling
public class AlbumsUpdateScheduler {

    private static final long SECONDS = 1000;
    private static final long MINUTES = 60 * SECONDS;

    private final AlbumsUpdater albumsUpdater;
    private final AlbumSchedulerBean albumSchedulerBean;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AlbumsUpdateScheduler(AlbumsUpdater albumsUpdater, AlbumSchedulerBean albumSchedulerBean) {
        this.albumsUpdater = albumsUpdater;
        this.albumSchedulerBean = albumSchedulerBean;
    }


    @Scheduled(initialDelay = 15 * SECONDS, fixedRate = 2 * MINUTES)
    public void run() {
        try {
            logger.debug("Starting albums update");
            int id = albumSchedulerBean.addScheduleStartEntry(System.currentTimeMillis());
            if(id != -1) {
                albumsUpdater.update();
                albumSchedulerBean.updateScheduleEndEntry(id, System.currentTimeMillis());
                logger.debug("Finished albums update");
            } else {
                logger.debug("Skipped update as there was another update in last 3 minutes");
            }

        } catch (Throwable e) {
            logger.error("Error while updating albums", e);
        }
    }
}
