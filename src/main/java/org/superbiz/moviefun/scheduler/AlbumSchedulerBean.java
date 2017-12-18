package org.superbiz.moviefun.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

@Repository
public class AlbumSchedulerBean {

    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger log = LoggerFactory.getLogger(AlbumSchedulerBean.class);

    @Transactional
    public int addScheduleStartEntry(long startTime){
        int returnedId = -1;
        log.debug("Checking for data existence for startTime {}", startTime);
        Query query = entityManager.createNativeQuery("SELECT * FROM album_scheduler WHERE start_time>= :startTime");
        query.setParameter("startTime", startTime-180000);
        List resultList = query.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            log.debug("No results found for 3 minutes prior to startTime {}. Proceeding with data insertion", startTime);
            AlbumScheduler albumScheduler = new AlbumScheduler();
            albumScheduler.setStartTime(startTime);
            entityManager.persist(albumScheduler);
            returnedId = albumScheduler.getId();
        }
        return returnedId;
    }

    @Transactional
    public void updateScheduleEndEntry(int id, long endTime){
        AlbumScheduler albumScheduler = entityManager.find(AlbumScheduler.class, id);
        albumScheduler.setEndTime(endTime);
        entityManager.persist(albumScheduler);
    }

}
