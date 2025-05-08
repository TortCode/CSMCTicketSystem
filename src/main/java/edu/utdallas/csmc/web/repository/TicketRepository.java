package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.misc.Semester;
import edu.utdallas.csmc.web.model.misc.Ticket;
import edu.utdallas.csmc.web.model.misc.TicketStatus;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.model.misc.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID>{

    List<Ticket> findByStatus(TicketStatus status); // Automatically derived

    List<Ticket> findByStatusName(String status);   // Automatically derived

    List<Ticket> findByStatusNameOrderByCreatedAt(String status);   // Automatically derived

    @Query("SELECT t FROM Ticket t WHERE t.student.id = :studentId ORDER BY t.createdAt DESC")
    List<Ticket> findRecentFromStudent(UUID studentId);

    @Query("SELECT t FROM Ticket t JOIN t.claims c WHERE c.user = :mentor")
    List<Ticket> findByMentor(@Param("mentor") User mentor);

    @Query("SELECT t FROM Ticket t JOIN t.claims c JOIN t.status s WHERE c.user.id = :mentorId AND s.name = :status")
    List<Ticket> findByMentorIdAndStatus(@Param("mentorId") UUID mentorId, @Param("status") String status);

    @Query("SELECT t FROM Ticket t WHERE t.status.name = :status ORDER BY t.createdAt")
    List<Ticket> findTicketsByStatusSorted(@Param("status") String status);

    @Query("SELECT t FROM Ticket t WHERE t.status.name != 'FINISHED' AND t.student.id = :studentId")
    Ticket findUnfinishedTicketByStudent(UUID studentId);

//    void deleteById(ID id);                 // Included in JpaRepository
    @Query("SELECT DISTINCT t FROM Ticket t JOIN t.claims c WHERE t.semester.id = :semesterId AND c.user.id = :mentorId")
    List<Ticket> findBySemesterAndMentor(@Param("semesterId") UUID semesterId, @Param("mentorId") UUID mentorId);

    @Query("SELECT t FROM Ticket t JOIN t.claims c " +
            "WHERE c.user.id = :mentorId AND t.semester = :semester")
    List<Ticket> findBySemesterAndMentor(@Param("semester") Semester semester,
                                         @Param("mentorId") UUID mentorId);

    @Query("SELECT t FROM Ticket t JOIN t.claims c " +
            "WHERE c.user.id = :mentorId " +
            "AND c.timeIn >= :startOfDay " +
            "AND c.timeOut <= :endOfDay")
    List<Ticket> findByMentorAndDate(@Param("mentorId") UUID mentorId,
                                     @Param("startOfDay") Date startOfDay,
                                     @Param("endOfDay") Date endOfDay);

    @Query("SELECT t.id FROM Ticket t WHERE t.status.name = :status")
    List<UUID> findIDsByStatus(@Param("status") String status);

    @Query("SELECT t.id FROM Ticket t WHERE t.status.name = 'UNCLAIMED'")
    List<UUID> findActiveIDs();
}
