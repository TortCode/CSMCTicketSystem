package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.session.ScheduledSessionAttendance;
import edu.utdallas.csmc.web.model.session.SessionTimeSlot;
import edu.utdallas.csmc.web.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduledSessionAttendanceRepository extends SessionAttendanceRepository<ScheduledSessionAttendance>{

    //Student - Scheduled Sessions Grade View
    @Query("select ssa from ScheduledSessionAttendance ssa JOIN ssa.user u where u.username =:netid ")
    List<ScheduledSessionAttendance> getScheduledSessions(@Param("netid") String netid);

    Optional<ScheduledSessionAttendance> findByUserAndTimeSlot(User user, SessionTimeSlot timeslot);

}
