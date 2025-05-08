package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.session.Quiz;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Transactional
public interface QuizRepository extends SessionRepository<Quiz> {

    @Query("SELECT q FROM Quiz q JOIN q.timeSlot qt JOIN q.sections secs WHERE secs.id = :sectionid AND qt.startTime > :today")
    List<Quiz> getQuizDetails(@Param("sectionid") UUID sectionid, @Param("today") Date today);

    @Query("Select q from Quiz q join fetch q.sections secs")
    List<Quiz> getAllQuizzes();

    @Query("select q from Quiz q JOIN q.timeSlot qt where qt.startTime between :startOfDay and :endOfDay ")
    List<Quiz> findQuizzesForEntireDayByDate(@Param("startOfDay") Date startOfDay, @Param("endOfDay") Date endOfDay);

    //Query for mentor_calendar view to display details in quizzes table
    @Query("select q from Quiz q JOIN q.timeSlot qt JOIN FETCH q.sections where qt.startTime >= :today or qt.endTime is null order by qt.startTime asc ")
    List<Quiz> getQuizInformation(@Param("today") Date today);

    @Query("Select distinct q from Quiz q JOIN q.timeSlot qt JOIN q.sections secs JOIN secs.course c JOIN secs.semester sem where qt.startTime <= :today order by qt.startTime")
    List<Quiz> getQuizzes(@Param("today") Date today);

    @Query("Select distinct q from Quiz q JOIN q.timeSlot qt JOIN q.sections secs JOIN secs.course c JOIN secs.semester sem where sem.active=true and qt.startTime <= :today order by qt.startTime")
    List<Quiz> getQuizzesForCalendar(@Param("today") Date today);

    //To-Do: sem.active = true need to be added
    @Query("select distinct q from Quiz q JOIN q.timeSlot t JOIN q.sections ss JOIN ss.students u JOIN ss.course c JOIN ss.semester sem where q.id = :sessionid")
    Quiz getAttendanceGrade(@Param("sessionid") UUID sessionid);

    //Query for mentor_calendar view to display details when Display button is clicked in Quizzes Table
    @Query("select q from Quiz q JOIN q.timeSlot qt where q.id = :quizid")
    Quiz getQuizDetails(@Param("quizid") UUID quizid);

    //Query for swipe entry form to display quizzes
    @Query("select q from Quiz q join q.timeSlot qt where qt.startTime <= :day and qt.endTime >= :endday")
    List<Quiz> getQuizForEntry(@Param("day") Date day, @Param("endday") Date endday);

    //Query for mentor_display view for quizzes
    @Query("select q from Quiz q JOIN q.timeSlot qt where qt.startTime <= :now and qt.endTime > :now")
    List<Quiz> getQuizDisplayDetails(@Param("now") Date now);

    @Query("SELECT DISTINCT q FROM Quiz q JOIN q.sections s JOIN s.instructors i JOIN q.timeSlot t WHERE i.username = :netId AND t.startTime <= :today ORDER BY t.startTime DESC")
    List<Quiz> findAllGradedQuizByInstructorOrderByStartTime(@Param("netId") String netId, @Param("today") Date today);

    Quiz findAllById(UUID quizId);
}
