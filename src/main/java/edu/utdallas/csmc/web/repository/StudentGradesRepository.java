package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.course.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * The method has been moved to the respective ModelRepository Class.
 */
public interface StudentGradesRepository extends JpaRepository<Section, UUID> {

//    //Student - Scheduled Sessions Grade View
//    @Query("select ssa from ScheduledSessionAttendance ssa JOIN ssa.user u where u.username =:netid ")
//    List<ScheduledSessionAttendance> getScheduledSessions(@Param("netid") String netid);

//    @Query("select ss from ScheduledSession ss JOIN SessionTimeSlot st on st.session = ss.id where st.id IN :res")
//    List<ScheduledSession> getSessions(@Param("res") List<UUID> res);

//    //Student - Quiz Sessions Grade View
//    @Query("select qa from QuizAttendance qa JOIN qa.quiz q JOIN qa.section JOIN qa.timeSlot JOIN qa.course c JOIN qa.user u where u.username = :netid")
//    List<QuizAttendance> getQuizAttendance(@Param("netid") String netid);

//    //Student - Walkin Sessions Grade View
//    @Query("select distinct wia from WalkInAttendance wia JOIN wia.activity wiaa JOIN wia.course wiac JOIN wia.user wiau JOIN FETCH wia.mentors m where wiau.username = :netid")
//    List<WalkInAttendance> getWalkInAttendance(@Param("netid") String netid);

}
