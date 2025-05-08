package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.course.Course;
import edu.utdallas.csmc.web.model.session.WalkInAttendance;
import edu.utdallas.csmc.web.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
public interface WalkInAttendanceRepository extends AttendanceRepository<WalkInAttendance>{

    //Student - Walkin Sessions Grade View
    @Query("select distinct wia from WalkInAttendance wia JOIN wia.activity wiaa JOIN wia.course wiac JOIN wia.user wiau LEFT JOIN FETCH wia.mentors m where wiau.username = :netid")
    List<WalkInAttendance> getWalkInAttendance(@Param("netid") String netid);

    List<WalkInAttendance> getAllByCourse(Course course);

    //Query for mentor swipe screen
    @Query("select wia from WalkInAttendance wia where wia.user = :user and wia.timeIn >= :today and wia.timeOut is null ")
    WalkInAttendance findCurrentUser(@Param("user") User user, @Param("today") Date today);

    //Query for Mentor Display student details
    @Query("select wia from WalkInAttendance wia where wia.timeOut is null AND wia.timeIn >= :date order by wia.timeIn asc ")
    List<WalkInAttendance> getStudentDisplayDetails(@Param("date") Date date);

    //Query for attendance info by user
    List<WalkInAttendance> findByUserOrderByTimeInDesc(User user);   // Automatically derived
}
