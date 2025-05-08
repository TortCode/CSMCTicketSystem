package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.schedule.ShiftAssignment;
import edu.utdallas.csmc.web.model.session.SessionTimeSlot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Transactional
public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, UUID> {

        @Modifying
        @Transactional
        @Query("delete from ShiftAssignment s where s.id = :id")
        void deleteSingleQuery(@Param("id") UUID id);

        @Query("select sa from ShiftAssignment sa " + "join fetch sa.scheduledShift ss " + "join fetch ss.shift s "
                        + "join fetch sa.mentor me " + "where me.username = :netid " + "and ss.date >= CURRENT_DATE "
                        + "order by ss.date DESC, s.startTime ")
        List<ShiftAssignment> getShiftAssignmentByMentorUserName(@Param("netid") String netid);

     @Query(nativeQuery = true, value = "select  count(st.session_id) from session_timeslot st join timeslot t join shift_assignment sa join user u\n" +
            "where st.id = t.id and t.id = sa.timeslot_id and sa.user_id = u.id and u.netid = :netid and st.session_id in (select st.session_id from session_timeslot st join session s join session_sections ss join section sc join semester sm\n" +
            "where st.session_id = s.id and s.id = ss.session_id and ss.section_id = sc.id and sc.semester_id = sm.id \n" +
            "and sm.active = 1); ")
        int getSessionsCountByMentorUserName(@Param("netid") String netid);

    @Query("select sa from ShiftAssignment sa " + "join fetch sa.scheduledShift ss " + "join fetch ss.shift s "
                        + "join fetch sa.mentor me " + "where me.username = :netid and ss.date = :date "
                        + "and s.startTime = :time " + "order by s.startTime, ss.date DESC ")
        List<ShiftAssignment> getShiftAssignmentByDateAndTime(@Param("netid") String netid, @Param("date") Date date,
                        @Param("time") Date time);

        @Query("select sa from ShiftAssignment sa join fetch sa.scheduledShift ss join fetch ss.shift s where ss.date = :date and s.startTime = :startTime")
        List<ShiftAssignment> getShiftAssignmentByDateAndInterval(@Param("date") Date date,
                        @Param("startTime") Time start);

    @Query("select sa from ShiftAssignment sa join fetch sa.scheduledShift ss join fetch ss.shift s where ss.date >= :date and s.startTime >= :startTime")
    List<ShiftAssignment> getShiftAssignmentByDateAndIntervalModified(@Param("date") Date date,
                                                              @Param("startTime") Time start);

	@Modifying
        @Query("update ShiftAssignment sa SET sa.session = NULL where sa.session = :timeSlotId")
        void clearAllAssignments(@Param("timeSlotId") SessionTimeSlot timeSlot);

	@Modifying
	@Query("update ShiftAssignment sa SET sa.session = :timeSlot where sa.id = :id")
	void updateTimeSlot(@Param("id") UUID id, @Param("timeSlot") SessionTimeSlot timeSlot);
}
