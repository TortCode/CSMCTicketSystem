package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.session.ScheduledSession;
import edu.utdallas.csmc.web.model.session.SessionTimeSlot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface SessionTimeSlotRepository extends TimeSlotRepository<SessionTimeSlot>{

    @Query("select st from SessionTimeSlot st where st.startTime >= :today or st.endTime is null order by st.startTime asc ")
    List<SessionTimeSlot> getSessionInformation(@Param("today") Date today);

    @Query("select st from SessionTimeSlot st where st.startTime between :startOfDay and :endOfDay order by st.startTime asc ")
    List<SessionTimeSlot> findSessionTimeSlotsForEntireDayByDate(@Param("startOfDay") Date startOfDay, @Param("endOfDay") Date endOfDay);

    @Query("select st from SessionTimeSlot st JOIN st.session ss where ss.id = :id order by st.startTime")
    List<SessionTimeSlot> findSessionTimeSlotBySessionId(@Param("id") UUID id);

    //Query for Mentor Display view to get Session Details
    //TODO
    @Query("select st from SessionTimeSlot st where st.actualEndTime is null and st.startTime between :firstDate and :lastDate order by st.startTime asc ")
    List<SessionTimeSlot> getSessionDisplayDetails(@Param("firstDate") Date firstDate, @Param("lastDate") Date lastDate);

    @Query("SELECT ss FROM SessionTimeSlot ss WHERE ss.session = :session")
    List<SessionTimeSlot> findAllBySession(@Param("session") ScheduledSession session);
}
