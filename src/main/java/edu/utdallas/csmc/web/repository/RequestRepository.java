package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.session.Request;
import edu.utdallas.csmc.web.model.user.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, UUID> {

    @Query("SELECT DISTINCT req FROM Request req WHERE req.startDate BETWEEN (SELECT startDate from Semester where active=true) AND (SELECT endDate from Semester where active=true)" +
            "and req.endDate BETWEEN (SELECT startDate from Semester where active=true) AND (SELECT endDate from Semester where active=true) and req.user = :user")
    List<Request> findRequestsByUser(@Param("user") User user);


//    List<Request> findByStatus(Request.);
//    @Query("SELECT a FROM Absence a JOIN FETCH a.assignment m JOIN FETCH m.scheduledShift s JOIN FETCH s.shift h ORDER BY h.startTime DESC,s.date DESC")
//    List<Request> getAbsenceDetails();

    @Query("select r.id from Request  r  where r.status = :status ")
    List<UUID> getRequestsByStatus(@Param("status") Request.RequestStatus status);

    @Query("select r from Request  r  where r.status = :status ")
    List<Request> getRequestsByStatus2(@Param("status") Request.RequestStatus status);
}
