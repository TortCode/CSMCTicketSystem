package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import edu.utdallas.csmc.web.model.course.Course;
import edu.utdallas.csmc.web.model.misc.Semester;
import java.util.Optional;

import java.util.List;
import java.util.UUID;

/**
 * This class extends the JPA repository for Section.
 */
public interface SectionRepository extends JpaRepository<Section, UUID> {

    @Query("SELECT s FROM Section s JOIN FETCH s.course c JOIN s.students u WHERE u.username = :netid")
    List<Section> findSectionAndCourseByStudentUsername(@Param("netid") String netid);

    @Query("SELECT DISTINCT s FROM Section s JOIN s.course c JOIN s.students ss JOIN FETCH s.instructors i JOIN FETCH s.semester m WHERE ss.username = :netid AND m.active = true AND c.supported = true")
    List<Section> getCourseDetails(@Param("netid") String netid);

    @Query("SELECT s FROM Section s JOIN s.semester sm WHERE sm.active = true ORDER BY s.course.department.abbreviation")
    List<Section> findAllSectionsActiveSemester();

    @Query("select distinct s from Section s JOIN FETCH s.semester x where x.active = true")
    List<Section> getSectionList();

    @Query("select s from Section s where s.id = :id")
    Section getSectionFromId(@Param("id") UUID id);

    @Query("select s from Section s where s.number = :number")
    Section getSectionFromNumber(@Param("number") String number);

    @Query("SELECT s FROM Section s JOIN s.instructors i JOIN FETCH s.semester x WHERE :user IN (i) and x.active = true")
    List<Section> findAllSectionsByInstructorUsername(@Param("user") User user);

    @Query("SELECT s FROM Section s JOIN s.instructors i JOIN s.semester sm WHERE sm.active = true and i.username = :netid ORDER BY s.course.department.abbreviation, s.course.number, s.number")
    List<Section> findSectionsByInstructorUsername(@Param("netid") String netid);

    @Query("SELECT s FROM Section s where s.id IN (:sectionIds)")
    List<Section> findSectionsByIdList(@Param("sectionIds") List<UUID> sectionIds);

    @Query("SELECT s FROM Section s WHERE s.number = :number AND s.course = :course AND s.semester = :semester")
    Optional<Section> findSectionByCourseAndNumber(@Param("course") Course course, @Param("semester") Semester semester, @Param("number") String sectionNumber);
}
