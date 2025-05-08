package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.session.ScheduledSession;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Transactional
public interface ScheduledSessionRepository extends SessionRepository<ScheduledSession>{

    @Query("Select distinct s from ScheduledSession s join s.sections secs join fetch s.timeslots st where secs.id = :sectionid and st.startTime > :today")
    List<ScheduledSession> findScheduledSessionBySectionIdAndStartTimeAfterToday(@Param("sectionid") UUID sectionid, @Param("today") Date today);

    @Query("SELECT ss FROM ScheduledSession ss JOIN SessionTimeSlot st ON st.session = ss.id WHERE st.id IN :res")
    List<ScheduledSession> getSessions(@Param("res") List<UUID> res);

    @Query("select distinct s from ScheduledSession s JOIN s.timeslots t JOIN s.sections ss JOIN ss.course c JOIN ss.semester sem where t.startTime <= :today AND sem.active=true order by t.startTime")
    List<ScheduledSession> getScheduledSessions(@Param("today") Date today);

    @Query("select distinct s from ScheduledSession s JOIN s.timeslots t JOIN s.sections ss JOIN ss.students u JOIN ss.course c JOIN ss.semester sem where sem.active =true and s.id = :sessionid ORDER BY u.firstName")
    ScheduledSession getAttendanceGrade(@Param("sessionid") UUID sessionid);

    @Query("SELECT DISTINCT s FROM ScheduledSession s JOIN s.sections se JOIN se.semester sm")
    List<ScheduledSession> findAllSessionJoinSectionActiveSemester();

    @Query("SELECT DISTINCT ss FROM ScheduledSession ss JOIN ss.timeslots t JOIN ss.sections sc JOIN sc.instructors i JOIN sc.semester sem " +
            "WHERE t.startTime <= :today AND i.username = :netId AND sem.active=true ORDER BY t.startTime DESC")
    List<ScheduledSession> findAllGradedSessionsByInstructorOrderByStartTime(@Param("netId") String netId, @Param("today") Date today);

    ScheduledSession findAllById(UUID sessionId);

    @Query("SELECT DISTINCT ss FROM ScheduledSession ss JOIN ss.sections s JOIN FETCH ss.type st WHERE s.id = :sec_id AND st.name = 'quiz'")
    List<ScheduledSession> findQuizSessionsBySectionId(@Param("sec_id") UUID sectionId);

    @Query("SELECT DISTINCT ss FROM ScheduledSession ss JOIN ss.sections s JOIN FETCH ss.type st WHERE s.id = :sec_id AND st.name != 'quiz'")
    List<ScheduledSession> findSessionExceptQuizzesBySectionId(@Param("sec_id") UUID sectionId);

    List<ScheduledSession> findAllBySections(Section section);

    @Query("select ss from ScheduledSession ss left join ss.timeslots st where size(ss.timeslots) < ss.repeats group by ss.id order by ss.lastModifiedOn")
    List<ScheduledSession> getUnscheduledSessionsForCalendar();

    @Query("select ss from ScheduledSession ss left join ss.timeslots st where st.actualEndTime >= :today")
    List<ScheduledSession> geScheduledSessionsForCalendar(@Param("today") Date today);

    @Query("select ss from ScheduledSession ss where ss.id =:sessionid")
    ScheduledSession getScheduledSessionById(@Param("sessionid") UUID sessionid);


}
