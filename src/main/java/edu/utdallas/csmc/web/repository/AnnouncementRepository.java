package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.misc.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {

    @Query("FROM Announcement a JOIN a.roles r JOIN r.user u WHERE u.username = :netid AND a.active = true AND a.startDate <= CURRENT_DATE AND a.endDate >= CURRENT_DATE")
    List<Announcement> findAnnouncementByUserAndLeStartDateAndGeEndDate(@Param("netid") String netid);

}
