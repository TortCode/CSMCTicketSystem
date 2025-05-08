package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.misc.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {

    List<Subject> findByShowOnCalendar(boolean showOnCalendar);

    @Query("select s from Subject s order by s.name asc")
    List<Subject> getSubjectsInSortedOrder();
}
