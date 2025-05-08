package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.session.Registration;
import edu.utdallas.csmc.web.model.session.SessionTimeSlot;
import edu.utdallas.csmc.web.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistrationRepository extends JpaRepository<Registration, UUID> {

    Optional<Registration> findByTimeSlot_IdAndUser_Username(UUID id, String username);

    boolean existsByUserAndTimeSlot(User currentUser, SessionTimeSlot slot);

    // Define the custom interface within the repository
    public interface RegistrationDetails {
        String getSessionId();
        Date getStartTime();
        Date getEndTime();
        String getTopic();
        String getFirstName();
    }

    @Query(value = "select distinct u.netid from registration r join user u join event e join session_timeslot st join session s \n" +
            "where r.user_id=u.id and r.timeslot_id=e.id and e.id=st.id and st.session_id = s.id \n" +
            "and DATE(e.start_time) = DATE_ADD(CURDATE(), INTERVAL 1 DAY)", nativeQuery = true)
    List<String> findTomorrowRegistrationsForReminderMails();

    @Query(value = "select distinct s.id as sessionId, e.start_time as startTime, e.end_time as endTime, s.topic as topic, u.first_name as firstName from registration r join user u join event e join session_timeslot st join session s \n" +
            "where r.user_id=u.id and r.timeslot_id=e.id and e.id=st.id and st.session_id = s.id \n" +
            "and DATE(e.start_time) = DATE_ADD(CURDATE(), INTERVAL 1 DAY) and u.netid = :netId", nativeQuery = true)
    RegistrationDetails getDetailsReminderMailsWithNetId(@Param("netId") String netId);

}
