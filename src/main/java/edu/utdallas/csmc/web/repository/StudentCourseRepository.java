package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.course.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * The method has been moved to the respective ModelRepository Class.
 */
public interface StudentCourseRepository extends JpaRepository<Section, UUID> {
//
//    @Query("select distinct s from Section s JOIN s.course c JOIN s.students ss JOIN FETCH s.instructors i JOIN fetch s.semester m where ss.username = :netid AND m.active = true AND c.supported = true")
//    List<Section> getCourseDetails(@Param("netid") String netid);
//
}
