package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {

    List<Course> findCourseBySupported(boolean supported);

    @Query("select c from Course c where c.id = :id")
    Course getCourse(@Param("id") UUID id);

    @Query("select c.id from Course c where c.number = :number")
    String getCourseID(@Param("number") String number);

    @Query("select c from Course c order by c.supported desc, c.number")
    List<Course> findCourses();

    @Query(nativeQuery = true, value = "SELECT SUM(course_count) \n" +
            "FROM (\n" +
            "    SELECT COUNT(s.course_id) AS course_count\n" +
            "    FROM section s \n" +
            "    INNER JOIN section_students ss ON s.id = ss.section_id \n" +
            "    INNER JOIN attendance a ON a.user_id = ss.user_id \n" +
            "    INNER JOIN course c ON s.course_id = c.id \n" +
            "    WHERE course_id IN (SELECT id FROM course) \n" +
            "      AND (a.time_in BETWEEN :startDate AND :endDate AND a.time_out BETWEEN :startDate AND :endDate ) \n" +
            "      OR (a.time_in BETWEEN :startDate AND :endDate AND a.time_out IS NULL) \n" +
            "    GROUP BY s.course_id\n" +
            ") AS subquery;")
    int getCourseVisitsForCourseForRequestedSemester(@Param("startDate")String startDate, @Param("endDate")String endDate);

    public interface CourseVisitDetails {
        String getCourseNumber();
        Integer getVisitCount();
    }

    @Query(nativeQuery = true, value = "SELECT c.number as courseNumber, COUNT(s.course_id) as visitCount \n" +
            "FROM section s \n" +
            "INNER JOIN section_students ss ON s.id = ss.section_id \n" +
            "INNER JOIN attendance a ON a.user_id = ss.user_id \n" +
            "INNER JOIN course c ON s.course_id = c.id \n" +
            "WHERE course_id IN (SELECT id FROM course) \n" +
            "  AND (a.time_in BETWEEN :startDate AND :endDate AND a.time_out BETWEEN :startDate AND :endDate ) \n" +
            "  OR (a.time_in BETWEEN :startDate AND :endDate AND a.time_out IS NULL) \n" +
            "GROUP BY s.course_id;")
    List<CourseVisitDetails> getCourseVisitsForDateRange(@Param("startDate")String startDate, @Param("endDate")String endDate);
}
