package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.session.Quiz;
import edu.utdallas.csmc.web.model.session.QuizAttendance;
import edu.utdallas.csmc.web.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface QuizAttendanceRepository extends SessionAttendanceRepository<QuizAttendance> {

    //Student - Quiz Sessions Grade View
    @Query("SELECT qa FROM QuizAttendance qa JOIN qa.quiz q JOIN qa.section JOIN qa.timeSlot JOIN qa.course c JOIN qa.user u WHERE u.username = :netid")
    List<QuizAttendance> getQuizAttendance(@Param("netid") String netid);

    @Query("select qa from QuizAttendance  qa JOIN qa.quiz qq JOIN qa.user u where qq.id = :quizId")
    List<QuizAttendance> getQuizAttendanceForGrades(@Param("quizId") UUID quizId);

    //Query for mentor swipe screen
    @Query("select qa from QuizAttendance qa where qa.user = :user and qa.timeIn >= :today and qa.timeOut is null")
    QuizAttendance findCurrentUser(@Param("user") User user, @Param("today") Date today);

    @Query("SELECT qa FROM QuizAttendance qa WHERE qa.quiz = :quizSession")
    List<QuizAttendance> findAllAttendanceById(@Param("quizSession") Quiz quizSession);
}
