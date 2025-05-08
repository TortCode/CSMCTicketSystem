package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.schedule.Absence;
import edu.utdallas.csmc.web.model.schedule.ShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
public interface AbsenceRepository extends JpaRepository<Absence, UUID> {

    Optional<Absence> findBySubstitute(ShiftAssignment currentAssignment);

    //Add condition: where date>=today
    @Query("SELECT a FROM Absence a JOIN FETCH a.assignment m JOIN FETCH m.scheduledShift s JOIN FETCH s.shift h ORDER BY h.startTime DESC,s.date DESC")
    List<Absence> getAbsenceDetails();

    @Query("select ab from Absence ab " +
            "JOIN FETCH ab.assignment ass " +
            //"JOIN FETCH ab.substitute sub " +
            "JOIN FETCH ass.scheduledShift sc " +
            "JOIN FETCH sc.shift sh " +
            "JOIN FETCH ass.mentor me " +
            //"JOIN FETCH sub.mentor mes " +
            "LEFT JOIN FETCH ab.substitute sub " +
            "LEFT JOIN FETCH sub.mentor mes " +
            "WHERE me.username <> :netid " +
            "AND sc.date >= CURRENT_DATE " +
            "ORDER BY sh.startTime, sc.date DESC "
    )
    List<Absence> getAbsencesByMentorUserNameAndToday(@Param("netid") String netid);


    @Query("select ab from Absence ab " +
            "JOIN FETCH ab.assignment ass " +
            "JOIN FETCH ass.scheduledShift sc " +
            "JOIN FETCH sc.shift sh " +
            "JOIN FETCH ass.mentor me " +
            "LEFT JOIN FETCH ab.substitute sub " +
            "LEFT JOIN FETCH sub.mentor mes " +
            "WHERE me.username = :netid " +
            "AND sc.date > '2024-08-16' " +
            "ORDER BY sc.date DESC, sh.startTime "
    )
    List<Absence> getAbsencesByMentorUserName(@Param("netid") String netid);

}
